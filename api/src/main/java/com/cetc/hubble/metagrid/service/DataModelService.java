package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataModelDAO;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import metagrid.common.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dahey on 2016/12/12.
 */
@Service
@Transactional
public class DataModelService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataModelDAO dataModelDAO;
    @Autowired
    private SqlService sqlService;

    @Value("${metagrid.datamodel.storagesource}")
    private Integer sourceId;

    public FrontModel get (Integer dataModelId) {
        DataModel dataModel = dataModelDAO.queryDataModelById(dataModelId);
        List<DataModelTable> tableList = dataModelDAO.queryDataModelTableByModelId(dataModelId);
        List<DataModelField> fieldList = dataModelDAO.queryDataModelFieldByModelId(dataModelId);
        List<DataModelRelation> relationList = dataModelDAO.queryDataModelRelationByModelId(dataModelId);

        //TODO 将数据库查出来的数据组装成FrontModel
        FrontModel frontModel = new FrontModel();

        return frontModel;
    }

    public int save (FrontModel frontModel,String name,String owner,String tag) throws Exception {
        ArrayList<DataModelTable> tableList = Lists.newArrayList();
        ArrayList<DataModelField> fieldList = Lists.newArrayList();
        ArrayList<DataModelRelation> relationList = Lists.newArrayList();

        DataModel dataModel = new DataModel(name,new Date(),owner,tag);
        final int modelId = dataModelDAO.insertDataModel(dataModel);
        ArrayList<FrontModelDataNode> nodesList = frontModel.getNodes();
        for (FrontModelDataNode node:nodesList) {
            ObjectNode position = Json.newObject();
            position.put("left",node.getLeft());
            position.put("top",node.getTop());
            position.put("w",node.getW());
            position.put("H",node.getH());
            FrontModelData data = node.getData();
            TreeNode tableData = data.getTableData();
            DataModelTable dataModelTable = new DataModelTable(modelId,tableData.getDatasetId(),tableData.getAlias(), Json.stringify(position));
            tableList.add(dataModelTable);//保存dataModelTable

            ArrayList<FrontModelField> fields = data.getFields();
            for (FrontModelField field:fields) {
                boolean checked = field.isChecked();
                if (checked){
                    DataModelField dataModelField = new DataModelField(modelId,field.getFieldID(),field.getName(),field.getComment(),switchFieldType(field.getType().toLowerCase()));
                    fieldList.add(dataModelField);//保存dataModelField
                }
            }

        }

        for (FrontModelEdge edge:  frontModel.getEdges()) {
            Long sourceId = Long.parseLong(edge.getSource().split("port_")[1]);
            Long targetId = Long.parseLong(edge.getTarget().split("port_")[1]);
            Integer relationTypeId  = (Integer)edge.getData().get("id");
            DataModelRelation dataModelRelation = new DataModelRelation(modelId,sourceId,targetId,relationTypeId);
            relationList.add(dataModelRelation);
        }

        //统一入库
        for (DataModelTable table:tableList) {
            dataModelDAO.insertDataModelTable(table);
        }

        ArrayList<String> tableSchema = Lists.newArrayList();

        for (DataModelField field:fieldList) {
            tableSchema.add(field.getFieldName()+" "+field.getFieldType());
            dataModelDAO.insertDataModelField(field);
            System.out.println("=====================");
            System.out.println(tableSchema);
        }
        for (DataModelRelation relation:relationList) {
            dataModelDAO.insertDataModelRelation(relation);
        }

        createTableHive(tableSchema,name);

        return modelId;
    }


    public void delete (Integer dataModelId) {
        dataModelDAO.deleteDataModel(dataModelId);
    }

    public void update (Integer dataModelId,FrontModel model) {

    }

    public void createTableHive (ArrayList<String> tableSchema,String name) throws Exception {
        StringBuilder sql = new StringBuilder();
//        create table if not exists hive_vw_share_dsj_mh_cgxx (hbh string,hbrq string,ddg string,sfsj string,zjhm string,zwm string,ywm string,zwh string,cw string,xb string)
        sql.append("create table if not exists ").append(name).append(" ( ").append(String.join(",",tableSchema)).append(" ) ");

        logger.info(sql.toString());

        sqlService.executeUpdateHive(sourceId,sql.toString());
    }

    private String switchFieldType(String type) {
        if (type.contains("number") ||type.contains("bigint")){
            return "bigint";
        }else if(type.contains("int")){
            return "int";
        }else if(type.contains("double")){
            return "double";
        }else if(type.contains("float")){
            return "float";
        }else if(type.contains("date")){
            return "date";
        }else {
            return "string";
        }
    }
}
