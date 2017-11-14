package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataQualityRuleDao;
import com.cetc.hubble.metagrid.vo.DataQualityRule_V2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cyq on 2017/9/27.
 */
@Service
public class DataQualityRuleService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataQualityRuleDao dataQualityRuleDao;

    /**
     * 根据规则id获取规则详情
     * @param ruleId
     * @return
     */
    public List<DataQualityRule_V2> getRuleInfo(Integer ruleId){
        return dataQualityRuleDao.getRuleInfo(ruleId);
    }

    /**
     * 获取规则列表
     * @return
     */
    public List<DataQualityRule_V2> getRuleList(){
        return dataQualityRuleDao.getRuleList();
    }

    public Integer deleteRule(Integer ruleId){
        return dataQualityRuleDao.deleteRule(ruleId);
    }

    public Integer updateRule(Integer ruleId,DataQualityRule_V2 dataQualityRuleV2){
        return dataQualityRuleDao.updateRule(ruleId,dataQualityRuleV2);
    }

    public  Integer insertRule(DataQualityRule_V2 dataQualityRuleV2){
        return dataQualityRuleDao.insertRule(dataQualityRuleV2);
    }

}
