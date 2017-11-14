/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package metadata.etl.dataset.elasticsearch;

import metadata.etl.EtlJob;
import metagrid.common.Constant;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ElasticSearchMetadataEtl extends EtlJob {

    public ElasticSearchMetadataEtl(Integer whEtljobId, Long whExecId, Properties prop) {
        super(null, whEtljobId, whExecId, prop);
    }

    public void ping() throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String hosts = prop.getProperty(Constant.ELASTICSEARCH_APP_HOST_KEY);
            boolean success = false;
            for (String host : hosts.split(",")) {
                String pingUrl = String.format("http://%s:%s%s", host, prop.getProperty(Constant.ELASTICSEARCH_APP_PORT_KEY), prop.getProperty(Constant.ELASTICSEARCH_PING_URL_KEY));
                logger.info("connecting to {}", pingUrl);
                HttpGet httpget = new HttpGet(pingUrl);
                String res = httpclient.execute(httpget, new ResponseHandler<String>() {
                    @Override
                    public String handleResponse(
                            final HttpResponse response) throws ClientProtocolException, IOException {
                        int status = response.getStatusLine().getStatusCode();
                        if (status == 200) {
                            return "OK";
                        } else {
                            return null;
                        }
                    }
                });
                success = res != null;
                if (success)
                    break;
            }
            if (!success)
                throw new Exception("connected to elasticsearch server failed:" + hosts);

        }
    }

    public QueryResult query(QueryParam param) throws Exception {
        String hosts = prop.getProperty(Constant.ELASTICSEARCH_APP_HOST_KEY);
        Exception exception = null;
        for (String host : hosts.split(",")) {
            try {
                return query(param, host);
            } catch (Exception e) {
                exception = e;
            }
        }
        throw exception;
    }

    public QueryResult query(QueryParam param, String host) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String queryUrl = String.format("http://%s:%s/%s", host, prop.getProperty(Constant.ELASTICSEARCH_APP_PORT_KEY), prop.getProperty(Constant.ELASTICSEARCH_QUERY_URL_KEY));
            HttpPost httppost = new HttpPost(queryUrl);

            StringEntity fileEntity = new StringEntity(param.getSql(),"utf-8");

            httppost.setEntity(fileEntity);
            String response = httpclient.execute(httppost, new ResponseHandler<String>() {
                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
//                    int status = response.getStatusLine().getStatusCode();
//                    if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
//                    } else {
//                        throw new ClientProtocolException("Unexpected response status: " + status);
//                    }
                }
            });
            if (response == null)
                throw new Exception("connected to elasticsearch serve failed:" + host);
            QueryResult result = new QueryResult();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map res = mapper.readValue(response, Map.class);
            if (res.containsKey("error")) {
                throw new Exception(res.get("error").toString());
            }
            Map hits = (Map) res.get("hits");
            result.setDuration((Integer) res.get("took"));
            long total = (Integer) hits.get("total");
            result.setTotal(total);
            if (total > 0) {
                List<String> colNames = new ArrayList<>();
                List<List<Object>> resData = new ArrayList<>();
                List<Map<String, Object>> data = (List<Map<String, Object>>) hits.get("hits");
                if (data != null && data.size() > 0) {
                    for (Map<String, Object> item : data) {
                        Map<String, Object> source = (Map<String, Object>) item.get("_source");
                        for (Map.Entry<String, Object> entry : source.entrySet()) {
                            if (!colNames.contains(entry.getKey())) {
                                colNames.add(entry.getKey());
                            }
                        }
                    }
                    for (Map<String, Object> item : data) {
                        Object[] objs = new Object[colNames.size()];
                        Arrays.fill(objs, "");

                        List<Object> line = Arrays.asList(objs);
                        Map<String, Object> source = (Map<String, Object>) item.get("_source");
                        for (Map.Entry<String, Object> entry : source.entrySet()) {
                            line.set(colNames.indexOf(entry.getKey()), entry.getValue());
                        }
                        resData.add(line);
                    }
                } else {
                    Map<String, Map<String, Object>> aggregations = (Map<String, Map<String, Object>>) ((Map) res.get("aggregations"));
                    if (aggregations != null) {
                        List<Object> line = new ArrayList<>();
                        for (Map.Entry<String, Map<String, Object>> entry : aggregations.entrySet()) {
                            if (!colNames.contains(entry.getKey())) {
                                colNames.add(entry.getKey());
                                line.add(entry.getValue().get("value"));
                            }
                        }
                        resData.add(line);
                    }
                }
                int colLength = colNames.size();
                for (List<Object> item : resData) {
                    int itemLength = item.size();
                    if (itemLength < colLength) {
                        for (int i = 0; i < (colLength - itemLength); i++) {
                            item.add("");
                        }
                    }
                }
                result.setColNames(colNames);
                result.setResults(resData);
            } else {
                result.setColNames(new ArrayList<>());
                result.setResults(new ArrayList<>());
            }
            return result;
        }
    }

    @Override
    public void extract()
            throws Exception {
        logger.info("In ElasticSearch metadata ETL, launch extract jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchExtract.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void transform()
            throws Exception {
    }

    @Override
    public void load()
            throws Exception {
        logger.info("In ElasticSearch metadata ETL, launch load jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchLoad.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void index () throws Exception {
        logger.info("In ElasticSearch metadata ETL, launch index jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In ElasticSearch metadata ETL load index scripts finished");
    }
}
