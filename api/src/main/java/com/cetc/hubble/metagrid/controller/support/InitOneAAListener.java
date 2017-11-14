package com.cetc.hubble.metagrid.controller.support;

import com.chinacloud.oneaa.common.entity.ServiceResource;
import com.chinacloud.oneaa.common.service.InitConfigService;
import com.chinacloud.oneaa.common.service.InitConfigServiceImpl;
import com.chinacloud.oneaa.common.service.YamlPrivService;
import com.chinacloud.oneaa.common.service.YamlPrivServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dahey on 2016/11/9.
 */
@Component
public class InitOneAAListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.datasource.driverClassName}")
    public String driverClassName;
    @Value("${sdk.oneaa.jdbc.url}")
    public String url;
    @Value("${sdk.oneaa.jdbc.username}")
    public String username;
    @Value("${sdk.oneaa.jdbc.password}")
    public String password;
    @Value("${sdk.oneaa.endpoint}")
    public String endpoint;
    @Value("${sdk.oneaa.clientid}")
    public String clientid;
    @Value("${sdk.oneaa.secret}")
    public String secret;
    @Value("${sdk.oneaa.policyFile.path}")
    public String policyFile;
    @Value("${sdk.oneaa.cached.duration}")
    public Long duration;

    @Autowired
    private ResourceLoader resourceLoader;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            InitConfigService init = new InitConfigServiceImpl();
            init.initDatabase(driverClassName, url,
                    username, password);
            init.initOneAA(endpoint, clientid, secret);
            init.initTokenCachedTime(duration);
            YamlPrivService yamlPrivService = new YamlPrivServiceImpl();
            yamlPrivService.initPolicy(initPolicy(policyFile));
        }
    }


    private ServiceResource initPolicy(String policyFile) {
        ServiceResource serviceResource = null;

        try {
            Resource e1 = resourceLoader.getResource(policyFile);
            InputStreamReader read1 = new InputStreamReader(e1.getInputStream(), "utf-8");
            BufferedReader br = new BufferedReader(read1);
            Yaml yaml = new Yaml();
            serviceResource = yaml.loadAs(br, ServiceResource.class);
            logger.debug("从路径:[" + this.policyFile + "]找到文件policy.yml,加载成功。");
            System.out.println("从路径:[" + this.policyFile + "]找到文件policy.yml,加载成功。");
        } catch (IOException var7) {
            logger.debug("从路径:[" + this.policyFile + "]中没找到文件policy.yml，尝试从其他加载方式。");
        }

        return serviceResource;

    }


}
