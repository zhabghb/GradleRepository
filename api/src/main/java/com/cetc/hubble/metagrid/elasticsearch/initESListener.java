package com.cetc.hubble.metagrid.elasticsearch;

import org.elasticsearch.node.NodeValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by dahey on 2016/4/14
 */
@Component
public class initESListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${metagrid.elasticsearch.enabled}")
    private Boolean enabled;
    @Value("${metagrid.elasticsearch.url}")
    private String url;
    @Value("${metagrid.elasticsearch.transportTcpPort}")
    private String transportTcpPort;
    @Value("${metagrid.elasticsearch.dataPath}")
    private String dataPath;
//    @Value("${metagrid.elasticsearch.logPath}")
//    private String logPath;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            try {
                if (enabled){
                    logger.info("Starting embedded elasticsearch server...");
                    String httpPort = url.split(":")[2];
                    new EmbedSearchServer(dataPath,httpPort,transportTcpPort).start();
                    logger.info("Embedded elasticsearch server start successfully!");
                }else {
                    logger.info("Embedded elasticsearch server not enabled!");
                }
            } catch (NodeValidationException e) {
                e.printStackTrace();
                logger.error("Embedded elasticsearch server start failed!");
            }
        }
    }

}
