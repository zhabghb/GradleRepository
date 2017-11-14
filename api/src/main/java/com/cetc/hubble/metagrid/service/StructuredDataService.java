package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.StructuredDataDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.*;
import com.google.common.base.Strings;
import metadata.etl.EtlJob;
import metadata.etl.models.EtlJobFactory;
import metadata.etl.models.EtlJobName;
import metagrid.common.Constant;
import metagrid.common.vo.PreviewParam;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by tao on 16-10-31.
 */
@Service
@Transactional
public class StructuredDataService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StructuredDataDAO structuredDataDAO;
    @Autowired
    private SqlService sqlService;
    @Autowired
    private DataSourceManagerDAO sourceManagerDAO;

    /**
     * Update a data set's alias by specifying its ID.
     *
     * @param dataSetID
     * @param alias
     * @return true if update successful, vise versa.
     */
    public boolean setDataSetAlias(Long dataSetID, String alias) {

        return structuredDataDAO.updateDataSetAlias(dataSetID.longValue(), alias);
    }

    /**
     * Update a field's label (comment) by specifying the field ID
     * and data set ID.
     *
     * @param dataSetID
     * @param fieldID
     * @param comment
     * @return true if update successful, vise versa.
     */
    public boolean setFieldComment(Long dataSetID, Integer fieldID, String comment) {
        //更新注释之前判断该表是否已经被删除
        if(structuredDataDAO.getTableCountById(dataSetID)<=0){
            throw new AppException("该数据集已被删除", ErrorCode.CONFLICT);
        }
        boolean res = structuredDataDAO.updateFieldLabel(dataSetID.longValue(), fieldID.intValue(), comment);
        if (!res) {
            throw new AppException("该字段已被修改或删除", ErrorCode.CONFLICT);
        }
        return res;
    }

    /**
     * First, delete all data set to tag mappings.
     * Then insert all new mappings into the target table.
     *
     * @param dataSetID
     * @param tags
     */
    public void setDataSetTags(Long dataSetID, List<Tag> tags) throws AppException {

        if (structuredDataDAO.detectDataSetExistence(dataSetID.longValue())) {
            throw new AppException("数据集已被删除", ErrorCode.CONFLICT);
        }
        // the first step
        structuredDataDAO.deleteDatasetTags(dataSetID.longValue());

        if (tags.size() > 0){
            // then do the insertion
            structuredDataDAO.insertDatasetTags(dataSetID.longValue(), tags);
        }
    }

    /**
     * Get a data set's tag list by its ID.
     *
     * @param dataSetID
     * @return list of tags
     */
    public List<Tag> getDataSetTags(Long dataSetID) {

        return structuredDataDAO.getDataSetTags(dataSetID.longValue());
    }

    /**
     * Get data set meta data by data set ID.
     *
     * @param dataSetID
     * @return StructuredDataSetMetaData instance
     */
    public StructuredDataSetMetaData getStructuredTableMeta(Long dataSetID) {
        StructuredDataSetMetaData dataSetMeta = structuredDataDAO.getDataSetMeta(dataSetID);
        HashMap<String, Object> propertyMap = sourceManagerDAO.getPropertyMapByJobId(Integer.parseInt(dataSetMeta.getUrn().split(":///")[0]));
        dataSetMeta.setDataSource(propertyMap);
        return dataSetMeta;
    }

    /**
     * Get data set's column info by data set ID.
     *
     * @param dataSetID
     * @return StructuredDataSetColumnInfo instance
     */
    public StructuredDataSetColumnInfo getColumnInfo(Long dataSetID) {

        return structuredDataDAO.getColumnInfo(dataSetID);
    }

    /**
     * Dependent on SqlService's query method.
     *
     * @param param
     * @param dataSetID
     * @return
     * @throws Exception
     */
    public StructuredDataSetSample getDataSample(StructuredDataViewParam param, Long dataSetID)
            throws Exception {

        String urn = param.getUrn();
        String source = param.getSourceType();
        int size;
        if (null == param.getSize()) {
            size = 20;
        } else {
            size = param.getSize().intValue();
        }

        //处理字段显示问题
        String fields = param.getFields();
        if(Strings.isNullOrEmpty(fields)){
            fields = "*";

//            StructuredDataSetColumnInfo columnInfo = structuredDataDAO.getColumnInfo(dataSetID);
//            List<StructuredDataSetColumn> columns = columnInfo.getColumns();
//            if(columns.size() > 30){
//                StringJoiner joiner = new StringJoiner(",");
//                for(int i = 0;i<30;i++){
//                    joiner.add(columns.get(i).getName());
//                }
//                fields = joiner.toString();
//            }
        }


        // build SQL statement then carry on query
        String sql = sqlBuild(source, urn, size,fields);
        QueryParam queryParam = new QueryParam();
        // get sourceId from urn String
        int sourceId =
                Integer.valueOf(urn.substring(0, urn.indexOf(":///"))).intValue();
        System.out.println("sourceId: " + sourceId);
        queryParam.setSourceId(sourceId);
        queryParam.setSourceType(source);
        queryParam.setSql(sql);
        queryParam.setPage(1);
        queryParam.setLimit(size);
        QueryResult queryResult = sqlService.query(queryParam);
        return new StructuredDataSetSample(queryResult.getColNames(), queryResult.getResults());
    }

    /**
     * For hbase data preview. Inherently using HBase Scanner.
     *
     * @param param preview parameter.
     * @return
     */
    public QueryResult preview(PreviewParam param) throws Exception {

        DataSource dataSource = sourceManagerDAO.getEtlJobById(param.getSourceId());
        if (dataSource == null) {
            throw new AppException("数据源已被删除", ErrorCode.BAD_REQUEST);
        }

        EtlJobName etlJobName = EtlJobName.valueOf(dataSource.getEtlJobName());
        Properties prop = new Properties();
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            prop.setProperty(p.getPropertyName(), p.getPropertyValue());
        }
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, param.getSourceId(), null, prop);
        QueryResult res = etlJob.preview(param);
        return res;
    }

    public Map<String, Object> getDictionaryEntries(DictionaryParam param) {

        return structuredDataDAO.getDictionaryEntries(param);
    }

    /**
     * Build SQL String for specified data source type.
     *
     * @param source
     * @param urn
     * @param size
     * @param fileds
     * @return SQL String
     */
    private String sqlBuild(String source, String urn, int size, String fileds) {

        StringBuilder builder = new StringBuilder();
        // 2:///GA_TESTER1/ABC get name and namespace through urn
        String[] splits = urn.split("/");
        String dataSetName = splits[splits.length - 1];
        String namespace = splits[splits.length - 2];
        if(!fileds.equals("*")){
            if (source.equals("ORACLE")) {
                fileds = "\"" + String.join("\",\"",fileds.split(","))+ "\"";
            }
        }

        builder.append("select ").append(fileds).append(" from ");

        if (source.equals("ORACLE")) {
            builder.append("\"" + namespace + "\"").append(".").append("\"" + dataSetName + "\"");
            builder.append(" where rownum < ").append(size);
            return builder.toString();
        } else if (source.equals("PGXZ")) {
            builder.append("\"" + namespace + "\"").append(".").append("\"" + dataSetName + "\"");
        } else if (source.equals("TRAFODION")) {
            builder.append(namespace).append(".").append(dataSetName);
        } else if (source.equals("HIVE")) {
            builder.append(namespace).append(".").
                    append(dataSetName);
        } else if (source.equals("ELASTICSEARCH")) {
            builder.append(urn.substring(urn.indexOf(":///") + 4));
        }
        builder.append(" limit ").append(size);
        System.out.println(builder.toString());
        return builder.toString();
    }


    /***
     * 通过dataResource查询DatasetOwner
     * @param dataResource
     * @return DatasetOwner
     */
    public DatasetOwner getDatasetOwnerByResouce (DataResource dataResource) {
        String urn = buildDatasetUrnByResouce(dataResource);
        return structuredDataDAO.getDatasetOwnerByUrn(urn);
    }

    /***
     * 通过dataResource查询DataSourceOwner
     * @param dataResource
     * @return DataSourceOwner
     */
    public DataSourceOwner getDataSourceOwnerByResouce (DataResource dataResource) {
        Integer etlJobId = matchEtlJobIdByResouce(dataResource);
        DataSourceOwner dataSourceOwner = structuredDataDAO.getDataSourceOwnerByJobId(etlJobId);
        String urn = String.format("%s:///%s/%s", etlJobId, dataResource.getDbName(), dataResource.getTableName());
        String tableAlias = structuredDataDAO.queryTableAliasByUrn(urn);
        dataSourceOwner.setName(tableAlias);
        return dataSourceOwner;
    }


    /***
     * 通过DataResource生成数据集urn
     * @param dataResource
     * @return
     */
    public String buildDatasetUrnByResouce (DataResource dataResource) {
        String datasetUrn = null;
        String dbType = dataResource.getDbType();
        List<Map<String, Object>> sourcesByType = sourceManagerDAO.querySourcesByType(dbType);
        for (Map<String, Object> idRow : sourcesByType) {
            String urn = null;
            Integer etlJobId = (Integer) idRow.get("wh_etl_job_id");
            List<Map<String, Object>> properties= sourceManagerDAO.queryPropertyByJobid(etlJobId);
            DataResource dataResourceDB = new DataResource();
            dataResourceDB.setTableName(dataResource.getTableName());
            for (Map<String, Object> property: properties) {
                switchDataSourceProperty(String.valueOf(property.get("property_name")), String.valueOf(property.get("property_value")), dataResource,dataResourceDB);
            }

            logger.info("正在匹配符合条件的数据源,param:{},db:{}",new Object[]{dataResource,dataResourceDB});

            if (dataResourceDB.equals(dataResource)){
                urn = String.format("%s:///%s/%s", etlJobId, dataResource.getDbName(), dataResource.getTableName());
                datasetUrn = urn;
                logger.info("已匹配到符合条件的数据源! Dataset urn :{}",urn);
                break;
            }
        }

        if (datasetUrn == null){
            logger.error("未匹配到符合条件的数据源! Data Resource :{}",dataResource);
            throw new AppException("未匹配符合条件的数据源!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return datasetUrn;
    }
    /***
     * 通过DataResource匹配数据源ID
     * @param dataResource
     * @return etlJobId
     */
    public Integer matchEtlJobIdByResouce (DataResource dataResource) {
        Integer sourceId = null;
        String dbType = dataResource.getDbType();
        List<Map<String, Object>> sourcesByType = sourceManagerDAO.querySourcesByType(dbType);
        for (Map<String, Object> idRow : sourcesByType) {
            Integer etlJobId = (Integer) idRow.get("wh_etl_job_id");
            List<Map<String, Object>> properties= sourceManagerDAO.queryPropertyByJobid(etlJobId);
            DataResource dataResourceDB = new DataResource();
            dataResourceDB.setTableName(dataResource.getTableName());
            for (Map<String, Object> property: properties) {
                switchDataSourceProperty(String.valueOf(property.get("property_name")), String.valueOf(property.get("property_value")), dataResource,dataResourceDB);
            }

            logger.info("正在匹配符合条件的数据源,param:{},db:{}",new Object[]{dataResource,dataResourceDB});

            if (dataResourceDB.equals(dataResource)){
                logger.info("已匹配到符合条件的数据源! etl job id :{}",etlJobId);
                sourceId = etlJobId;
                break;
            }
        }

        if (sourceId == null){
            logger.error("未匹配到符合条件的数据源! Data Resource :{}",dataResource);
            throw new AppException("未匹配符合条件的数据源!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return sourceId;
    }

    /***
     * 根据数据库查询出来的数据源配置封装到DataResource，便于比较。
     * @param propertyName  属性名
     * @param propertyValue 属性值
     * @param dataResourceParam  参数中的dataResource，用于给dataResourceDB赋值，便于比较。
     * @param dataResourceDB 最终需要封装成的dataResource
     */
    private void switchDataSourceProperty(String propertyName, String propertyValue,DataResource dataResourceParam,DataResource dataResourceDB) {
        switch (propertyName) {
            case Constant.ORACLE_DB_JDBC_URL:
                String[] strings = propertyValue.split("//")[1].split(":");
                String[] values = strings[1].split("/");
                dataResourceDB.setIp(strings[0]);
                dataResourceDB.setDbType("ORACLE");
                dataResourceDB.setPort(values[0]);
                dataResourceDB.setAdditional(values[1].toUpperCase());
                break;
            case Constant.ORACLE_DB_USERNAME:
                dataResourceDB.setDbName(propertyValue.toUpperCase());
                break;
//            case Constant.HIVE_METASTORE_JDBC_URL:
//                String[] stringsHive = propertyValue.split("//")[1].split(":");
//                String[] valuesHive = stringsHive[1].split("/");
//                dataResourceDB.setIp(stringsHive[0]);
//                dataResourceDB.setDbType("HIVE");
//                dataResourceDB.setPort(valuesHive[0]);
//                dataResourceDB.setAdditional(valuesHive[1]);
//                dataResourceDB.setDbName(dataResourceParam.getDbName());
//                break;

            case Constant.HIVE_METASTORE_HIVESERVER2_URL:
                String[] stringsHive = propertyValue.split("//")[1].split(":");
                dataResourceDB.setIp(stringsHive[0]);
                dataResourceDB.setDbType("HIVE");
                dataResourceDB.setPort(stringsHive[1].split("/")[0]);
                dataResourceDB.setDbName(dataResourceParam.getDbName());
                break;
            case Constant.HBASE_ZOOKEEPER_QUORUM:
                dataResourceDB.setIp(propertyValue);
                dataResourceDB.setDbType("HBASE");
                dataResourceDB.setDbName(dataResourceParam.getDbName());
                break;
            case Constant.HBASE_ZK_CLIENT_PORT:
                dataResourceDB.setPort(propertyValue);
                break;
        }
    }

    /***
     * 新增DatasetOwner
     * @param datasetOwner
     */
    public void addDatasetOwner (DatasetOwner datasetOwner) {
        structuredDataDAO.insertDatasetOwner(datasetOwner);
    }

    /***
     * 新增DataSourceOwner
     * @param dataSourceOwner
     */
    public void addDataSourceOwner (DataSourceOwner dataSourceOwner) {
        structuredDataDAO.insertDataSourceOwner(dataSourceOwner);
    }

    /***
     * 修改DataSourceOwner
     * @param dataSourceOwner
     */
    public void updateDataSourceOwner (DataSourceOwner dataSourceOwner) {
        structuredDataDAO.updateDataSourceOwner2(dataSourceOwner);
    }

    public DatasetAttr saveDatasetAttr(DatasetAttr attr) {
        Map<String, Object> stdParams = new HashMap<String, Object>();
        stdParams.put("datasetId", attr.getDatasetId());
        stdParams.put("attrName", attr.getAttrName());
        stdParams.put("attrValue", attr.getAttrValue());
        if(!checkExistForAdding(stdParams)) {
            int id = structuredDataDAO.saveDataSetAttr(stdParams);
            attr.setId(id);
        } else {
            throw new AppException("属性名" + attr.getAttrName() +" 已经存在.", ErrorCode.CONFLICT);
        }
        return  attr;
    }

    public void updateDatasetAttr(DatasetAttr attr) {
        Map<String, Object> stdParams = new HashMap<String, Object>();
        stdParams.put("datasetId", attr.getDatasetId());
        stdParams.put("attrName", attr.getAttrName());
        stdParams.put("attrValue", attr.getAttrValue());
        stdParams.put("id", attr.getId());

        if(!checkExistForUpdating(stdParams)) {
            structuredDataDAO.updateDataSetAttr(stdParams);
        } else {
            throw new AppException("属性名" + attr.getAttrName() +" 已经存在.", ErrorCode.CONFLICT);
        }
    }

    public void deleteDatasetAttr(Integer attrId) {
        Map<String, Object> stdParams = new HashMap<String, Object>();
        stdParams.put("id", attrId);
        structuredDataDAO.deleteDatasetAttr(stdParams);
    }

    public List<DatasetAttr> getDatasetAttrListByDataSetId(Long datasetId) {
        Map<String, Object> stdParams = new HashMap<String, Object>();
        stdParams.put("datasetId", datasetId);
        return structuredDataDAO.getDatasetAttrListByDataSetId(stdParams);
    }

    public Long getDatasetAttrCountByDataSetId(Long datasetId) {
        return structuredDataDAO.getDatasetAttrCountByDataSetId(datasetId);
    }

    public boolean checkExistForAdding(Map<String, Object> stdParams) {
        List<DatasetAttr> list = structuredDataDAO.getDatasetAttrListByDataSetIdAndAttrName(stdParams);
        if(list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean checkExistForUpdating(Map<String, Object> stdParams) {
        List<DatasetAttr> list = structuredDataDAO.getDatasetAttrListByParams(stdParams);
        if(list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

}
