package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import metagrid.common.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by dahey on 2017/4/14.
 */
@Service
public class ElasticSearchService {

    @Value("${metagrid.elasticsearch.url}")
    private String elasticsearchUrl;

    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;


    public void createIndexForDB()  {
        Properties props =dataSourceManagerDAO.getWhProperties();
        props.setProperty(Constant.WH_ELASTICSEARCH_URL_KEY,elasticsearchUrl);//设置username
    }

}
