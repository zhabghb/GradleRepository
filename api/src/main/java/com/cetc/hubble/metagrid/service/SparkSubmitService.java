package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.ShellUtil;
import com.cetc.hubble.metagrid.dao.DataQualityDAO;
import com.cetc.hubble.metagrid.vo.DqAnalyseParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dahey on 2017/3/31.
 */
public class SparkSubmitService implements Runnable{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String sparkSubmitStr;
    private DataQualityDAO dataQualityDAO;
    private DqAnalyseParam input;

    public SparkSubmitService (String sparkSubmitStr, DqAnalyseParam input,DataQualityDAO dataQualityDAO) {
        this.sparkSubmitStr = sparkSubmitStr;
        this.dataQualityDAO = dataQualityDAO;
        this.input = input;
    }

    @Override
    public void run () {
        logger.info("===shell command process start===");
        dataQualityDAO.updateDataQualityFieldRuleStatus(1,input.getDatasetId(),false);
        int exitValue = ShellUtil.executeCmd(sparkSubmitStr);
        logger.info("===shell command process finished, status:{}===",exitValue);
        dataQualityDAO.updateDataQualityFieldRuleStatus(exitValue == 0?2:3,input.getDatasetId(),true);
    }
}
