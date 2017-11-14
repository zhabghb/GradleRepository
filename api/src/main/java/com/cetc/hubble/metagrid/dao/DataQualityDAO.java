package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DataQuality;
import com.cetc.hubble.metagrid.vo.DataQualityRule;
import com.cetc.hubble.metagrid.vo.DataQualityTopn;
import com.cetc.hubble.metagrid.vo.DqAnalyseParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Quality DAO.
 *
 * Created by dahey on 17-2-13.
 */
@Repository
public class DataQualityDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(DataQualityDAO.class);

    private final static String GET_DATAQULITY_BY_FIELD = "SELECT field_id as fieldId,total_count as totalCount,valid_count as validCount,invalid_count as invalidCount,nulls,empties,unique_values as uniqueValues,sample from dataquality WHERE field_id = ";

    private final static String GET_DATAQULITY_SAMPLE_BY_FIELD = "SELECT sample from dataquality WHERE field_id = ";

    private final static String GET_DATAQULITY_TOPN_BY_FIELD = "SELECT field_id as fieldId,field_value as fieldValue,count from dataquality_topn WHERE field_id = ";

    private final static String GET_DATAQULITY_RULES = "SELECT id,rule_key as ruleKey,rule_name as ruleName,description from dataquality_rule ";

    private final static String GET_DATAQULITY_RULE_BY_FIELDID = "SELECT id,rule_key as ruleKey,rule_name as ruleName,description from dataquality_rule dr LEFT JOIN dataquality_field_rule dfr ON dfr.rule_id = dr.id WHERE dfr.field_id = ";
    private final static String GET_DATAQULITY_RULE_BY_FIELDNAME = "SELECT DISTINCT id,rule_key as ruleKey,rule_name as ruleName,description from dataquality_rule dr LEFT JOIN dataquality_field_rule dfr ON dfr.rule_id = dr.id WHERE dfr.field_name = ";
    private final static String INSERT_DATAQULITY_FIELD_RULE= "INSERT INTO dataquality_field_rule(`field_id`,`field_name`,`dataset_id`,`rule_id`,`dag_id`,`last_check_time`,`next_check_time`) VALUES(:fieldId,:fieldName,:datasetId,:ruleId,:dagId,:lastCheckTime,:nextCheckTime)";
    private final static String UPDATE_DATAQULITY_FIELD_RULE= "UPDATE dataquality_field_rule set `field_name` = :fieldName,`dataset_id` = :datasetId,`rule_id` = :ruleId ,`dag_id` = :dagId,`last_check_time` = :lastCheckTime ,`next_check_time` = :nextCheckTime WHERE `field_id` = :fieldId";
    private final static String UPDATE_DATAQULITY_CHECKTIME= "UPDATE dataquality_field_rule set `last_check_time` = :lastCheckTime ,`next_check_time` = :nextCheckTime WHERE `dataset_id` = :datasetId";
    private final static String UPDATE_DATAQULITY_FIELD_RULE_STATUS= "UPDATE dataquality_field_rule set `status` = ? WHERE `dataset_id` = ?";
    private final static String UPDATE_DATAQULITY_FIELD_RULE_STATUS_FINISHED= "UPDATE dataquality_field_rule set `status` = ?,`last_finish_time` = ?  WHERE `dataset_id` = ?";
    private final static String DELETE_DATAQULITY_FIELD_RULE= "DELETE FROM dataquality_field_rule  WHERE UPPER(field_name) = ? AND field_id not in ${fieldIds}";
    private final static String QUERY_ALL_DATAQULITY_FIELD_RULES= "SELECT DISTINCT dfr.dataset_id AS datasetId,dd.`name` as tableName,dd.parent_name as parent,dd.wh_etl_job_id as jobId,dd.dataset_type as dataType FROM dataquality_field_rule dfr LEFT JOIN dict_dataset dd ON dfr.dataset_id = dd.id LEFT JOIN dataquality_rule dr ON dfr.rule_id = dr.id WHERE dfr.is_active = 1 AND dfr.`status` != 1";
    private final static String QUERY_DATAQULITY_FIELD_RULE_BY_FIELDID= "SELECT dfr.field_id as fieldId,dfr.field_name as fieldName,dfr.dataset_id AS datasetId,dd.`name` as tableName,dd.parent_name as parent,dfr.rule_id as ruleId,dr.rule_key as ruleKey,dd.wh_etl_job_id as jobId,dd.dataset_type as dataType,1 as checked FROM dataquality_field_rule dfr LEFT JOIN dict_dataset dd ON dfr.dataset_id = dd.id LEFT JOIN dataquality_rule dr ON dfr.rule_id = dr.id WHERE dfr.field_id = ";
    private final static String QUERY_DATAQULITY_STATUS_BY_FIELDID= "SELECT `status`  FROM dataquality_field_rule WHERE field_id = ";
    private final static String QUERY_DATAQULITY_FIELDS_BY_DATASETID= "SELECT dfr.field_id,dfr.field_name,dr.rule_key  FROM dataquality_field_rule dfr LEFT JOIN dataquality_rule dr ON dfr.rule_id = dr.id WHERE dataset_id = ";


    /**
     * 根据fieldID查询数据质量特征信息
     * @param fieldId
     * @return
     */
    public DataQuality getDataQualityFeatures(Long fieldId) throws IOException {

        List<DataQuality> rows = getNamedParameterJdbcTemplate().query(GET_DATAQULITY_BY_FIELD+fieldId,new BeanPropertyRowMapper<DataQuality>(DataQuality.class));

        if(rows.size() != 1){
            logger.error("=======rows size not legal:{},未找到该记录! fieldId id:{}=======",new Object[]{rows.size(),fieldId});
            throw new AppException("暂无数据，请手动检测!", ErrorCode.NO_CONTENT);
        }

//        Map<String, Object> sample = getJdbcTemplate().queryForMap(GET_DATAQULITY_SAMPLE_BY_FIELD+fieldId);
//
//        ObjectMapper mapper = new ObjectMapper();
//        String sampleStr = (String)sample.get("sample");
//        System.out.println("==========================1");
//        System.out.println(sampleStr);
//        String replacedSample= sampleStr.replace("\\\"", "\"");
//        System.out.println("==========================2");
//        System.out.println(replacedSample);
//        ObjectNode sampleList = mapper.readValue(replacedSample, new TypeReference<ObjectNode>(){});

        return rows.get(0);
    }

    /***
     * 根据fieldID和topn获取数据质量TopN信息
     * @param fieldId
     * @param topn
     * @return
     */
    public List<DataQualityTopn> queryDataQualityTopn(Long fieldId,int topn) {
        List<DataQualityTopn> rows = getNamedParameterJdbcTemplate().query(GET_DATAQULITY_TOPN_BY_FIELD+fieldId+" ORDER BY  count DESC  LIMIT "+topn,new BeanPropertyRowMapper<DataQualityTopn>(DataQualityTopn.class));
        return rows;
    }

    public List<DataQualityRule> queryDataQualityRule (String fieldName) {
        List<DataQualityRule> rows = getNamedParameterJdbcTemplate().query(GET_DATAQULITY_RULE_BY_FIELDNAME+"'"+fieldName+"'",new BeanPropertyRowMapper<DataQualityRule>(DataQualityRule.class));
        if (rows.size() == 0){
            rows = getNamedParameterJdbcTemplate().query(GET_DATAQULITY_RULES,new BeanPropertyRowMapper<DataQualityRule>(DataQualityRule.class));
        }
        return rows;
    }
    public List<DataQualityRule> queryDataQualityRules () {
        return getNamedParameterJdbcTemplate().query(GET_DATAQULITY_RULES,new BeanPropertyRowMapper<DataQualityRule>(DataQualityRule.class));
    }

    public void saveDataQualityFieldRule (Map<String, Object> params) {
        getNamedParameterJdbcTemplate().update(INSERT_DATAQULITY_FIELD_RULE,params);
    }
    public void updateDataQualityFieldRule (Map<String, Object> params) {
        getNamedParameterJdbcTemplate().update(UPDATE_DATAQULITY_FIELD_RULE,params);
    }
    public void updateDataQualityCheckTime (Map<String, Object> params) {
        getNamedParameterJdbcTemplate().update(UPDATE_DATAQULITY_CHECKTIME,params);
    }
    public void updateDataQualityFieldRuleStatus (Integer status,Long datasetId,boolean finished) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (finished){
            jdbcTemplate.update(UPDATE_DATAQULITY_FIELD_RULE_STATUS_FINISHED,status,new Date(),datasetId);
        }else {
            jdbcTemplate.update(UPDATE_DATAQULITY_FIELD_RULE_STATUS,status,datasetId);
        }

    }

    public void deleteOldFieldRule (ArrayList<String> fieldIds, String fieldName) {
        getJdbcTemplate().update(DELETE_DATAQULITY_FIELD_RULE.replace("${fieldIds}","("+String.join(",", fieldIds)+")"),fieldName);
    }

    public List<DqAnalyseParam> queryDueTasks () {
        return getNamedParameterJdbcTemplate().query(QUERY_ALL_DATAQULITY_FIELD_RULES,new BeanPropertyRowMapper<DqAnalyseParam>(DqAnalyseParam.class));

    }
    public DqAnalyseParam queryDueTaskByFieldId (Long fieldId) {
        List<DqAnalyseParam> rows = getNamedParameterJdbcTemplate().query(QUERY_DATAQULITY_FIELD_RULE_BY_FIELDID + fieldId, new BeanPropertyRowMapper<DqAnalyseParam>(DqAnalyseParam.class));
        if(rows.size() != 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("未找到该记录!fieldId id:" + fieldId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows.get(0);
    }

    public Integer queryDataQualityStatusByFieldId (Long fieldId) {
        Map<String, Object> map = getJdbcTemplate().queryForMap(QUERY_DATAQULITY_STATUS_BY_FIELDID + fieldId);
        return Integer.parseInt(String.valueOf(map.get("status")));
    }

    public List<Map<String, Object>> queryCheckFieldsByDatasetId (Long datasetId) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(QUERY_DATAQULITY_FIELDS_BY_DATASETID + datasetId);
        return rows;
    }
}
