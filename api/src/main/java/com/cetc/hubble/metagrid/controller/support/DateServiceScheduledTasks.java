package com.cetc.hubble.metagrid.controller.support;

import com.cetc.hubble.metagrid.dao.DataDomainDAO;
import com.cetc.hubble.metagrid.service.DataDomainService;
import com.cetc.hubble.metagrid.vo.DataDomainExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DateServiceScheduledTasks {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataDomainService dataDomainService;
    @Autowired
    private DataDomainDAO dataDomainDAO;
    @Value("${metagrid.domainStatusCheck.auto}")
    private Boolean autoCheck;


    @Scheduled(cron = "${metagrid.domainStatusCheck.schedule}")
	public void domainStatusCheck(){
        if(autoCheck){
            List<DataDomainExt> domains =  dataDomainDAO.queryDueDomains();
            logger.info("数据资源集合待检测数量:size:{}",domains.size());

            for (DataDomainExt domain :domains){
                dataDomainService.checkDomainStatus(domain);
            }
        }
	}



}