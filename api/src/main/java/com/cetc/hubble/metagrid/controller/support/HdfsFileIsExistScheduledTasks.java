package com.cetc.hubble.metagrid.controller.support;

import com.cetc.hubble.metagrid.service.HDFSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 检测数据库中还存在属性的hdfs文件是否在hdfs文件系统中还存在
 */
@Component
public class HdfsFileIsExistScheduledTasks {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HDFSService hdfsService;


    @Scheduled(cron = "${metagrid.hdfsFile.checkIsExist.schedule}")
    public void checkFileIsExist() throws Exception{
        logger.info("检测hdfs文件是否还存在监控服务已启动");

        hdfsService.checkFileIsExist();

        logger.info("检测hdfs文件是否还存在监控服务结束");
    }


}
