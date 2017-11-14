package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DataModel;
import com.cetc.hubble.metagrid.vo.DataModelField;
import com.cetc.hubble.metagrid.vo.DataModelRelation;
import com.cetc.hubble.metagrid.vo.DataModelTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tao on 16-10-27.
 */
@Repository
@Transactional
public class DataModelDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(DataModelDAO.class);
    private static String INSERT_DATA_MODEL = " INSERT INTO datamodel(`name`, alias, createtime, owner,tag) VALUES (:name, :alias, :createtime, :owner,:tag)";
    private static String INSERT_DATA_MODEL_TABLE = "INSERT INTO datamodel_table(model_id, table_id, table_alias, canvas_position) VALUES (:modelId, :tableId, :tableAlias, :canvasPosition)";
    private static String INSERT_DATA_MODEL_FIELD = "INSERT INTO datamodel_field(model_id, field_id, field_alias) VALUES (:modelId, :fieldId, :fieldAlias)";
    private static String INSERT_DATA_MODEL_RELATION = " INSERT INTO datamodel_relation(model_id, source_id, target_id, relation_type_id) VALUES (:modelId, :sourceId, :targetId, :relationTypeId)";
    private static String QUERY_DATA_MODEL_BY_ID = "SELECT id, `name`, alias, createtime, owner from datamodel where id = ";
    private static String QUERY_DATA_MODEL_TABLE_BY_MODELID = "SELECT id,model_id, table_id, table_alias, canvas_position from datamodel_table where model_id = ";
    private static String QUERY_DATA_MODEL_FIELD_BY_MODELID = "select id, model_id, field_id, field_alias from datamodel_field where model_id = ";
    private static String QUERY_DATA_MODEL_RELATION_BY_MODELID = "select id, model_id, source_id, target_id, relation_type_id from datamodel_relation where model_id = ";
    private static String DELETE_DATA_MODEL_BY_ID = "delete from datamodel where id = ";
    private static String DELETE_DATA_MODEL_JSON_BY_ID = "delete from datamodel_json where id = ";
    private static String DELETE_DATA_MODEL_TABLE_BY_MODELID = "delete from datamodel_table where model_id = ";
    private static String DELETE_DATA_MODEL_FIELD_BY_MODELID = "delete from datamodel_field where model_id = ";
    private static String DELETE_DATA_MODEL_RELATION_BY_MODELID = "delete from datamodel_relation where model_id = ";


    public int insertDataModel(DataModel dataModel) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_DATA_MODEL,new BeanPropertySqlParameterSource(dataModel),keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void insertDataModelTable (DataModelTable table) {
        getNamedParameterJdbcTemplate().update(INSERT_DATA_MODEL_TABLE,new BeanPropertySqlParameterSource(table));
    }

    public void insertDataModelField (DataModelField field) {
        getNamedParameterJdbcTemplate().update(INSERT_DATA_MODEL_FIELD,new BeanPropertySqlParameterSource(field));
    }

    public void insertDataModelRelation (DataModelRelation relation) {
        getNamedParameterJdbcTemplate().update(INSERT_DATA_MODEL_RELATION,new BeanPropertySqlParameterSource(relation));
    }

    public DataModel queryDataModelById (Integer dataModelId) {
        List<DataModel> rows = getNamedParameterJdbcTemplate().query(QUERY_DATA_MODEL_BY_ID+dataModelId,new BeanPropertyRowMapper<DataModel>(DataModel.class));

        if(rows.size() != 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("dataModelId id:" + dataModelId + "不存在！", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows.get(0);
    }

    public List<DataModelTable> queryDataModelTableByModelId (Integer dataModelId) {
       return getNamedParameterJdbcTemplate().query(QUERY_DATA_MODEL_TABLE_BY_MODELID+dataModelId,new BeanPropertyRowMapper<DataModelTable>(DataModelTable.class));
    }

    public List<DataModelField> queryDataModelFieldByModelId (Integer dataModelId) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATA_MODEL_FIELD_BY_MODELID+dataModelId,new BeanPropertyRowMapper<DataModelField>(DataModelField.class));
    }

    public List<DataModelRelation> queryDataModelRelationByModelId (Integer dataModelId) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATA_MODEL_RELATION_BY_MODELID+dataModelId,new BeanPropertyRowMapper<DataModelRelation>(DataModelRelation.class));
    }

    public void deleteDataModel (Integer dataModelId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
        namedParameterJdbcTemplate.update(DELETE_DATA_MODEL_TABLE_BY_MODELID+dataModelId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_MODEL_FIELD_BY_MODELID+dataModelId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_MODEL_RELATION_BY_MODELID+dataModelId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_MODEL_BY_ID+dataModelId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_MODEL_JSON_BY_ID + dataModelId, (SqlParameterSource) null);
    }
}
