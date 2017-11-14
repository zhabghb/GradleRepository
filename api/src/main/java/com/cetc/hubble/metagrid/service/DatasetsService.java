package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.DatasetsDAO;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import metagrid.common.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by yuson on 10/13/2016.
 */
@Service
public class DatasetsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String  queryTemplate = "{\"bool\":{\"should\":[{\"match\":{\"name\":\"${keyword}\"}},{\"match\":{\"alias\":\"${keyword}\"}},{\"has_child\":{\"type\":\"field\",\"query\":{\"match\":{\"field_name\":\"${keyword}\"}}}},{\"has_child\":{\"type\":\"field\",\"query\":{\"match\":{\"field_label\":\"${keyword}\"}}}}]}}";
    private final String  endpoint = "/metagrid/dataset/_search";

    @Value("${metagrid.elasticsearch.url}")
    private String elasticsearchUrl;
    @Autowired
    private DatasetsDAO datasetsDAO;
    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;

    public List<DatasetColumn> getDatasetColumnsByID(int datasetId) {
        return datasetsDAO.getDatasetColumnsByID(datasetId);
    }

    public List<TreeNode> searchByKeyword(SearchParam param) throws Exception{

        List<TreeNode> treenodes = new ArrayList<TreeNode>();

        List<Map<String, Object>> rows = datasetsDAO.getPagedDatasetByKeyword(param.getKeyword()==null?"":param.getKeyword(), param.getTypes(), param.getPage()==0?1:param.getPage(), param.getLimit()==0?20:param.getLimit());

        for (Map row : rows) {
            if(Strings.isNullOrEmpty((String)row.get("parent_name"))){
                continue;
            }

            TreeNode tn = new TreeNode(
                    (String)row.get("urn"),
                    (String)row.get("name"),
                    (String)row.get("parent_name"),
                    (Long)row.get("id"),
                    (String)row.get("alias"),
                    false,
                    (String)row.get("source"),
                    false);
            tn.setSourceName(dataSourceManagerDAO.getSourceNameById((Integer) row.get("wh_etl_job_id")));
            treenodes.add(tn);
        }

        return treenodes ;
    }
    public List<TreeNode> searchByTagName(SearchTagParam param) throws Exception{

        List<TreeNode> treenodes = new ArrayList<TreeNode>();

        List<Map<String, Object>> rows = datasetsDAO.getPagedDatasetByTagName(param.getTagName(), param.getPage()==0?1:param.getPage(), param.getLimit()==0?20:param.getLimit());

        for (Map row : rows) {
            if(Strings.isNullOrEmpty((String)row.get("parent_name"))){
                continue;
            }

            TreeNode tn = new TreeNode(
                    (String)row.get("urn"),
                    (String)row.get("name"),
                    (String)row.get("parent_name"),
                    (Long)row.get("dataset_id"),
                    (String)row.get("alias"),
                    false,
                    (String)row.get("source"),
                    false);
            tn.setSourceName(dataSourceManagerDAO.getSourceNameById((Integer) row.get("wh_etl_job_id")));
            treenodes.add(tn);
        }

        return treenodes ;
    }


    public String buildElasticSearchParam(String keyword,int page, int size) throws IOException {
        String s = queryTemplate.replace("${keyword}", keyword);
        ObjectMapper mapper = new ObjectMapper();
        Map query = mapper.readValue(s, Map.class);
        ElasticSearchParam elasticSearchParam = new ElasticSearchParam((page - 1) * size, size,query );
        return Json.stringify(Json.toJson(elasticSearchParam));
    }

    public List<TreeNode> useElasticSearch(SearchParam param) throws Exception {
        String postParam = buildElasticSearchParam(param.getKeyword() == null ? "" : param.getKeyword(), param.getPage() == 0 ? 1 : param.getPage(), param.getLimit() == 0 ? 20 : param.getLimit());
        List<TreeNode> treenodes = new ArrayList<TreeNode>();
        String searchUrl = elasticsearchUrl+endpoint;
        logger.info("====a post request to :{},and the param are:{}====",new Object[]{searchUrl,postParam});
        String response = HttpUtil.doPost(postParam, searchUrl);
        logger.info("====search successfully!the response are:{}====",response);
        ObjectMapper mapper = new ObjectMapper();
        Map res = mapper.readValue(response, Map.class);
        if (res.containsKey("error")) {
            throw new Exception(res.get("error").toString());
        }
        Map hits = (Map) res.get("hits");
        long total = (Integer) hits.get("total");
        if (total > 0) {
            List<Map<String, Object>> data = (List<Map<String, Object>>) hits.get("hits");
            if (data != null && data.size() > 0) {
                for (Map<String, Object> item : data) {
                    Map<String, Object> source = (Map<String, Object>) item.get("_source");
                    TreeNode tn = new TreeNode(
                            (String)source.get("urn"),
                            (String)source.get("name"),
                            (String)source.get("parent_name"),
                            Long.parseLong(String.valueOf(item.get("_id"))),
                            (String)source.get("alias"),
                            false,
                            (String)source.get("source"),
                            false);
                    tn.setSourceName((String)source.get("source_name"));
                    treenodes.add(tn);
                }

            }

        }
            return treenodes;
    }
}

