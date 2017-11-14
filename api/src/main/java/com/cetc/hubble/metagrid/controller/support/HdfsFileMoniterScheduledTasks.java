package com.cetc.hubble.metagrid.controller.support;

import com.cetc.hubble.metagrid.service.HDFSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by root on 9/6/17.
 */
@Component
public class HdfsFileMoniterScheduledTasks {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HDFSService HdfsService;

    @Value("${data.file.download.path}")
    private String downloadpath;




    @Scheduled(fixedDelay = 60000)
    public void HdfsFileTimeCheck()
    {
        logger.info("Hdfs 文件监控服务启动");

        HdfsService.HdfsTimeMoniter(downloadpath);

        logger.info("Hdfs 文件监控服务结束");
    }



}
