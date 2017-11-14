package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.TreeDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.TagParam;
import com.cetc.hubble.metagrid.vo.TagResult;
import com.cetc.hubble.metagrid.vo.TreeNodes;
import com.cetc.hubble.metagrid.vo.TreeParam;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tao on 16-10-25.
 */
@Service
public class TreeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TreeDAO treeDAO;
    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;
    @Autowired
    private DataSourceManagerService dataSourceManagerService;
    @Autowired
    private SimpMessagingTemplate template;

    @Value("${metagrid.backend.host}")
    private String backendHost;


    /**
     * Count all data set number and data source number, then return.
     *
     * @return List of two entities, one of them is a map of grand statistics,
     * the other is list of node's information.
     */
    public Map getDataSourceStatistics() {

        List<Map<String, Object>> dataSourceStatistics = treeDAO.getDataSourceStatistics();
        // count data sources & data sets
        int dataSourceCount = 0;
        long dataSetCount = 0;
        for (Map<String, Object> dataSourceStat : dataSourceStatistics) {
            dataSourceCount++;
            long datasetNum = ((Long) dataSourceStat.get("total")).longValue();
            dataSetCount += datasetNum;
        }
        System.out.println("Data source count: " + dataSourceCount);
        System.out.println("Data set count: " + dataSetCount);

        Map total = Maps.newHashMap();
        total.put("sources", dataSourceCount);
        total.put("tables", dataSetCount);

        Map res = Maps.newHashMap();
        res.put("total", total);
        res.put("nodes", dataSourceStatistics);
        return res;
    }

    /**
     * Get the next level nodes and distinguish between namespaces
     * and data sets.
     * Set the default page size: 20 rows.
     *
     * @param param
     * @return TreeNodes view object
     */
    public TreeNodes getSubNodes(TreeParam param) {

        String urn;
        // distinguish between second-class node (namespace, schema or database)
        // and third-class node by decide wet the TreeNodeName is null/empty or not.
        boolean isNamespace = Strings.isNullOrEmpty(param.getTreeNodename());
        if (isNamespace) {
            urn = String.format("%d:///", param.getSourceId().intValue());
        } else {
            urn = String.format("%d:///%s/", param.getSourceId().intValue(),
                    param.getTreeNodename());
        }
        // assign limit's default value --- 20
        int limit = param.getLimit();
        if (0 == limit) {
            limit = 20;
        }
        TreeNodes treeNodes = treeDAO.getSubNodesByURN(urn, param.getPage(),
                limit, isNamespace);
        return treeNodes;
    }

    /**
     * Get all tag nodes (TagResult).
     *
     * @return map result containing statistic according to tags.
     */
    public Map getTags() {

        List<TagResult> tagResults = treeDAO.getTagNodes();
        // count tags & data sets
        int tagsCount = 0;
        int dataSetCount = 0;
        for (TagResult tagResult : tagResults) {
            tagsCount++;
            // if total field is null then increase zero
            dataSetCount +=
                    (null == tagResult.getTotal()) ? 0 : tagResult.getTotal().longValue();
        }
//        System.out.println("Tags count: " + tagsCount);
//        System.out.println("Data sets count: " + dataSetCount);

        Map total = Maps.newHashMap();
        total.put("tags", tagsCount);
        total.put("tables", dataSetCount);

        Map res = Maps.newHashMap();
        res.put("total", total);
        res.put("tags", tagResults);
        return res;
    }

    /**
     * Get paged data set nodes for a specific tag by its ID.
     * Set the default page size: 20 rows.
     *
     * @param param
     * @return TreeNodes instance.
     */
    public TreeNodes getDatasetsByTag(TagParam param) {

        // assign limit's default value --- 20
        int limit = param.getLimit();
        if (0 == limit) {
            limit = 20;
        }
        TreeNodes treeNodes = treeDAO.getDataSetNodesByTagID(param.getTagId(),
                param.getPage(), limit);
        return treeNodes;
    }

    /**
     * 手动同步元数据，发送请求到backend-service
     * @param sourceId
     * @param username
     */
    public void syncMetadata(Integer sourceId,String username) {
        try {
            dataSourceManagerService.ping(sourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerDAO.updateJobNotActive(sourceId,e.getMessage(),true);
//            sendComments(sourceId,e.getMessage());
            throw new AppException("无法连接到该数据源,暂时不能同步!", ErrorCode.CUSTOM_EXCEPTION);
        }
        try {
            final String backendURL = backendHost + "/etl/" + sourceId + "/run?username=" +username;
            logger.info("send redirect to backend :{}" , backendURL);
            sendRedirect(backendURL);
            logger.info("redirect successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw  (AppException)e;
            dataSourceManagerDAO.updateJobComments(sourceId,e.getMessage());
//            sendComments(sourceId,e.getMessage());
            throw new AppException("服务异常,暂时不能同步!", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    public void sendMessage(Integer jobId,boolean success) {
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("jobId",jobId);
        data.put("success",success);
        data.put("total",treeDAO.getDatasetCountBySourceId(jobId));
        Map<String, Object> sourceInfo = dataSourceManagerDAO.getSourceInfoById(jobId);
        data.put("sourceName",sourceInfo.get("sourceName"));
        data.put("lastRunError",sourceInfo.get("comments")!=null);
        data.put("comments",sourceInfo.get("comments"));
        template.convertAndSend("/ws/topic/jobInfo", data);
    }
    public void sendComments(Integer jobId,String comments) {
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("jobId",jobId);
        data.put("commentsOnly",true);
        data.put("lastRunError",true);
        data.put("comments",comments);
        template.convertAndSend("/ws/topic/jobInfo", data);
    }

    public void ackRunning (Integer jobId, int running) {
        HashMap<String, Object> data = Maps.newHashMap();
        data.put("jobId",jobId);
        data.put("running",running);
        template.convertAndSend("/ws/topic/jobRunning", data);
    }


    public void sendRedirect(String url) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler =  getResponseHandler();
            String res = httpclient.execute(httpget, responseHandler);
            logger.info("调用:{} 返回结果:{}",new Object[]{url,res});
            if (res.contains("error_message")){
                throw new AppException("服务异常,请稍候再试!",ErrorCode.CUSTOM_EXCEPTION);
            }
        }
    }


    /****
     * 查询源sourceId最近一次同步的状态
     * @param sourceId
     * @return  {syncStatus：[状态]}  状态分三种,分别为 {"RUNNING":正在运行；"SUCCESS":同步成功；"ERROR":同步失败}
     */
    public Map getSourceStatus (int sourceId) {
        Map<String, Object> statusMap = dataSourceManagerDAO.querySourceStatusById(sourceId);
        boolean running = (Integer)statusMap.get("running")== 0?false:true;
        boolean hasComments = statusMap.get("comments")!=null;
        String status = running?"RUNNING":(hasComments?"ERROR":"SUCCESS");
        statusMap.put("syncStatus",status);
        statusMap.put("total",treeDAO.getDatasetCountBySourceId(sourceId));
        return statusMap;
    }


    public static ResponseHandler<String> getResponseHandler(){
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 ){// && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return responseHandler;
    }
}
