package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.DataQualityRuleParam;
import com.cetc.hubble.metagrid.vo.DataQualityRule_V2;
import com.cetc.hubble.metagrid.vo.HdfsFileAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by cyq on 2017/9/27.
 */
@Repository
public class DataQualityRuleDao extends AbstractMySQLOpenSourceDAO{

    private static Logger logger = LoggerFactory.getLogger(DataQualityRuleDao.class);

    private final static String CREATE_RULE = "INSERT INTO dataquality_v2_rule(name,description,jar_path) VALUES(?,?,?)";
    private final static String UPDATE_RULE = "UPDATE dataquality_v2_rule SET name =:name and description =:description and jar_path=:jarPath WHERE id=";
    private final static String DELETE_RULE = "delete from dataquality_v2_rule WHERE id= ?";
    private final static String GET_RULE_LIST = "SELECT * FROM dataquality_v2_rule";
    private final static String GET_RULE_INFO = "SELECT * FROM dataquality_v2_rule WHERE id=";
    private final static String GET_PARAMS_BY_RULE_ID = "SELECT * FROM dataquality_v2_rule_param WHERE rule_id=";
    private final static String DELETE_PARAMS_BY_RULE_ID = "delete FROM dataquality_v2_rule_param WHERE rule_id=";


    public  Integer insertRule(DataQualityRule_V2 dataQualityRuleV2){

        return getJdbcTemplate().update(CREATE_RULE,
                dataQualityRuleV2.getName(),dataQualityRuleV2.getDescription(),dataQualityRuleV2.getJarPath());

    }

    public Integer updateRule(Integer ruleId,DataQualityRule_V2 dataQualityRuleV2){
        Integer result = getNamedParameterJdbcTemplate().update(UPDATE_RULE + ruleId, new BeanPropertySqlParameterSource(dataQualityRuleV2));
        return result;

    }

    public Integer deleteRule(Integer ruleId){
        getJdbcTemplate().update(DELETE_PARAMS_BY_RULE_ID, ruleId);
        return getJdbcTemplate().update(DELETE_RULE, ruleId);
    }


    public List<DataQualityRule_V2> getRuleInfo(Integer ruleId){
        List<DataQualityRule_V2> dataQualityRuleV2s = getNamedParameterJdbcTemplate().query(GET_RULE_INFO+ruleId, new BeanPropertyRowMapper<DataQualityRule_V2>(DataQualityRule_V2.class));
        for(DataQualityRule_V2 dataQualityRuleV2 :dataQualityRuleV2s){
            List<DataQualityRuleParam> dataQualityRuleParams = getNamedParameterJdbcTemplate().query(GET_PARAMS_BY_RULE_ID+dataQualityRuleV2.getId(), new BeanPropertyRowMapper<DataQualityRuleParam>(DataQualityRuleParam.class));
            dataQualityRuleV2.setRuleParam(dataQualityRuleParams);
        }
        return dataQualityRuleV2s;
    }

    public List<DataQualityRule_V2> getRuleList(){
        List<DataQualityRule_V2> dataQualityRuleV2s = getNamedParameterJdbcTemplate().query(GET_RULE_LIST, new BeanPropertyRowMapper<DataQualityRule_V2>(DataQualityRule_V2.class));
        for(DataQualityRule_V2 dataQualityRuleV2 :dataQualityRuleV2s){
            List<DataQualityRuleParam> dataQualityRuleParams = getNamedParameterJdbcTemplate().query(GET_PARAMS_BY_RULE_ID+dataQualityRuleV2.getId(), new BeanPropertyRowMapper<DataQualityRuleParam>(DataQualityRuleParam.class));
            dataQualityRuleV2.setRuleParam(dataQualityRuleParams);
        }
        return dataQualityRuleV2s;
    }










}
