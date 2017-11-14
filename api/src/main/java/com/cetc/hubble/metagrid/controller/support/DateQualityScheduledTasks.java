package com.cetc.hubble.metagrid.controller.support;

import com.cetc.hubble.metagrid.dao.DataQualityDAO;
import com.cetc.hubble.metagrid.service.DataQualityService;
import com.cetc.hubble.metagrid.vo.DqAnalyseParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DateQualityScheduledTasks {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataQualityService dataQualityService;
    @Autowired
    private DataQualityDAO dataQualityDAO;


    @Scheduled(cron = "${metagrid.datacheck.schedule}")
	public void dataCheck(){
        List<DqAnalyseParam> inputs =  dataQualityDAO.queryDueTasks();

        logger.info("数据质量待检测任务数量:size:{}",inputs.size());

        for (DqAnalyseParam input :inputs){
            logger.info("正在创建数据质量检测任务:{}",input);
            dataQualityService.startQualityAnalyse(input);
        }
	}


}