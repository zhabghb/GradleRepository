package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataQualityDAO;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.StructuredDataDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import metagrid.common.Constant;
import metagrid.common.utils.AES;
import metagrid.common.utils.Json;
import metagrid.common.utils.StringUtil;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static metagrid.common.Constant.DQ_OPTIONS_DAG_APPNAME;

/**
 * Created by dahey on 17-2-13.
 */
@Service
public class DataQualityService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataQualityDAO dataQualityDAO;
    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;
    @Autowired
    private StructuredDataDAO structuredDataDAO;
    @Autowired
    private DagOperatorProperties operatorProperties;

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Value("${metagrid.encrypt.key}")
    private String encryptKey;

    @Value("${metagrid.artflow.host}")
    private String artflowHost;
    @Value("${metagrid.artflow.schedule}")
    private String schedule;
    @Value("${metagrid.datacheck.schedule}")
    private String datacheckSchedule;
    @Value("${metagrid.datacheck.cmd}")
    private String cmd;
    @Value("${airflow.api.create}")
    private String createApi;
    @Value("${airflow.api.run}")
    private String runApi;

    /***
     * 根据fieldID获取数据质量特征信息
     * @param fieldId 字段ID
     * @return DataQuality
     */
    public DataQuality getDataQualityFeatures (Long fieldId) throws IOException {
        DataQuality dataQuality = dataQualityDAO.getDataQualityFeatures(fieldId);
        List<Map<String, Object>> columns = structuredDataDAO.queryFieldNameAndLabelByFieldId(fieldId);
        //columns 排序
        String  sample = dataQuality.getSample();
        JsonNode jsonNode = Json.parse(sample);
        List<Map<String, Object>> newColumns = new ArrayList<>();
        List<String> schemaList = new ArrayList<String>();
        JsonNode  schema = jsonNode.get("schema");
        if(schema.isArray()){
            for(JsonNode objNode : schema){
                schemaList.add(objNode.asText());
            }
            for(String sma:schemaList){
                for(Map map:columns){
                    if (sma.equals(map.get("fieldName"))){
                        newColumns.add(map);
                        break;
                    }
                }
            }
        }
        dataQuality.setColumns(newColumns);
        return dataQuality;
    }

    /***
     * 根据fieldID和topn获取数据质量TopN信息
     * @param fieldId 字段ID
     * @param topn  TopN
     * @return List<DataQualityTopn>
     */
    public List<DataQualityTopn> getDataQualityTopn (Long fieldId, int topn) {
        return dataQualityDAO.queryDataQualityTopn(fieldId, topn);
    }


    public void startQualityAnalyse (DqAnalyseParam input)  {
        startSparkSubmit(input);
        updateDataQualityCheckTime(datacheckSchedule,input.getDatasetId());
    }

    public void saveQualityAnalyseJobs (DqAnalyseParam input,String identifer)  {
        Map<String, Object> field = structuredDataDAO.queryFieldByDatasetAndFieldName(input.getDatasetId(), identifer);
        Long fieldId = Long.parseLong(String.valueOf(field.get("field_id")));
        String fieldName = String.valueOf(field.get("field_name"));
        saveDataQualityFieldRule(datacheckSchedule,fieldId,null,fieldName,null,input);
    }


    public void startSparkSubmit (final DqAnalyseParam input)  {
        String runStr = buildSparkRun(input);

        final String sparkSubmitStr = buildSparkSubmit(runStr);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new SparkSubmitService(sparkSubmitStr,input,dataQualityDAO));
        executorService.shutdown();
        logger.info("===submit task finished===");

    }

    public String startQualityAnalyseThroughDAG (DqAnalyseParam input,String identifer) throws Exception {

        Map<String, Object> field = structuredDataDAO.queryFieldByDatasetAndFieldName(input.getDatasetId(), identifer);
        Long fieldId = Long.parseLong(String.valueOf(field.get("field_id")));
        String fieldName = String.valueOf(field.get("field_name"));
        String params = buildParams(input);
        ArtFlowDAGResult dag = createTask(artflowHost + createApi, params);
        String dagId= dag.getId();
//        String dagId= UUID.randomUUID().toString();
        saveDataQualityFieldRule(schedule+" *",fieldId,dagId,identifer,new Date(),input);
        return dagId;
    }

    private void saveDataQualityFieldRule (String cronSchedule,Long fieldId,String dagId,String fieldName,Date lastCheckTime,DqAnalyseParam input) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fieldId",fieldId);
        params.put("fieldName",fieldName);
        params.put("ruleId",input.getRuleId());
        params.put("datasetId",input.getDatasetId());
        params.put("dagId",dagId);
        params.put("lastCheckTime",lastCheckTime);
        params.put("nextCheckTime",getNextRun(cronSchedule));
        if (input.isChecked()){
            dataQualityDAO.updateDataQualityFieldRule(params);
        }else {
            dataQualityDAO.saveDataQualityFieldRule(params);
        }
    }

    private void updateDataQualityCheckTime (String cronSchedule,Long datasetId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("datasetId",datasetId);
        params.put("lastCheckTime",new Date());
        params.put("nextCheckTime",getNextRun(cronSchedule));
        dataQualityDAO.updateDataQualityCheckTime(params);
    }

    public ArtFlowDAGResult createTask (String url, String params) throws Exception {
        DAGResult dagResult = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            ResponseHandler<String> responseHandler = TreeService.getResponseHandler();
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            httpPost.setEntity(stringEntity);
            logger.info("a post request to :{} and the params are:{}", new Object[]{url, params});
            String res = httpclient.execute(httpPost, responseHandler);
            logger.info("调用:{} 返回:{},结果:{}", new Object[]{url, res, StringUtil.decodeUnicode(res)});
            if (res.contains("error")) {
                throw new AppException("服务异常,请稍候再试!", ErrorCode.CUSTOM_EXCEPTION);
            }

            ObjectMapper mapper = new ObjectMapper();
            dagResult = mapper.readValue(res,DAGResult.class);

        }

        return dagResult.getDag();
    }

    private String buildParams (DqAnalyseParam input) {
        String runStr = buildSparkRun(input);
        logger.info("DAG run params :{}", runStr);
        DagOperatorProperties operatorProps = null;
        try {
            operatorProps = (DagOperatorProperties) operatorProperties.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        operatorProps.setRunOption(runStr);

        ArtFlowDAG dag = new ArtFlowDAG();
        dag.setId("");
        dag.setSchedule(schedule);
        dag.setIs_active("Y");
        dag.setName(generateDAGName(input.getDatasetId()));
        dag.setDesc(input.toString());

        DagNodes dagNodes = new DagNodes();
        DagTaskProperties dagTaskProperties = new DagTaskProperties();
        dagTaskProperties.setId(UUID.randomUUID().toString());
        dagTaskProperties.setType("spark");
        dagNodes.setTask_properties(dagTaskProperties);
        dagNodes.setOperator_properties(operatorProps);
        dag.setNodes(Lists.newArrayList(dagNodes));

        ObjectNode data = Json.newObject();
        JsonNode dagNode = Json.toJson(dag);
        data.set("dag", dagNode);
        String postData = Json.stringify(data);
        logger.info("DAG post data :{}", postData);

        return postData;
    }


    private String generateDAGName (Long datasetId) {
        return DQ_OPTIONS_DAG_APPNAME + "-" + datasetId;
    }
    private String buildSparkSubmit (String runStr) {
        return cmd+runStr;
    }

    private String buildSparkRun (DqAnalyseParam input) {
        String confStr = buildConfByJobId(input.getJobId(), input.getDataType(), input.getTableName(), input.getParent());
        ObjectNode fieldAndKey = Json.newObject();
        List<Map<String, Object>> fields = dataQualityDAO.queryCheckFieldsByDatasetId(input.getDatasetId());
        for (Map<String, Object> field :fields){
            fieldAndKey.put(String.valueOf(field.get("field_name")),String.valueOf(field.get("rule_key")).toLowerCase()+"_"+field.get("field_id"));
        }
        String fieldAndKeyStr = Json.stringify(Json.toJson(fieldAndKey));

        String rsConnStr = buildRsConnStr();

        StringBuilder sb = new StringBuilder(" ");

        sb.append(DQ_OPTIONS_DAG_APPNAME).append(" '").append(confStr).append("' '").append(fieldAndKeyStr).append("' '").append(rsConnStr).append("' ");

        return sb.toString();
    }

    private String buildRsConnStr () {
        ObjectNode rsConnNode = Json.newObject();

        rsConnNode.put(Constant.DQ_OPTIONS_KEY_CHECKRESULT_TABLENAME, Constant.DQ_OPTIONS_CHECKRESULT_TABLENAME);
        rsConnNode.put(Constant.DQ_OPTIONS_KEY_TOPN_TABLENAME, Constant.DQ_OPTIONS_TOPN_TABLENAME);
        rsConnNode.put(Constant.DQ_OPTIONS_KEY_CHECKRESULT_DRIVER, Constant.DQ_MYSQL_DRIVER);
        rsConnNode.put(Constant.DQ_OPTIONS_KEY_CHECKRESULT_URL, url);
        rsConnNode.put(Constant.DQ_OPTIONS_KEY_CHECKRESULT_USER, username);
        rsConnNode.put(Constant.DQ_OPTIONS_KEY_CHECKRESULT_PASSWORD, password);

        return Json.stringify(rsConnNode);
    }

    private String buildConfByJobId (Integer jobId, String dataType, String tableName, String parent) {
        List<Map<String, Object>> resultMaps = dataSourceManagerDAO.queryPropertyByJobid(jobId);
        ObjectNode dataSourceNode = Json.newObject();

        if (Constant.ELASTICSEARCH_DATA_SOURCE.equals(dataType)) {
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_TYPE, Constant.DATA_CHECK_TYPE_ELASTICSEARCH);
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_ES_PUSHDOWN, "true");
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_ES_TYPE, parent + "/" + tableName);
        } else if (Constant.HIVE_DATA_SOURCE.equals(dataType)){
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_TYPE, Constant.DATA_CHECK_TYPE_HIVE);
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DBTABLE, parent + "." + tableName);
        } else {
            dataSourceNode.put(Constant.DQ_OPTIONS_KEY_TYPE,Constant.DATA_CHECK_TYPE_JDBC);
            if (Constant.ORACLE_DATA_SOURCE.equals(dataType)) {
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DRIVER, Constant.DQ_ORACLE_DRIVER);
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DBTABLE, parent + "." + tableName);
            } else if (Constant.PGXZ_DATA_SOURCE.equals(dataType)){
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DRIVER, Constant.DQ_PGXZ_DRIVER);
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DBTABLE, "\"" + tableName+"\"");
            }else {
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DRIVER, Constant.DQ_HIVE_DRIVER);
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_DBTABLE, parent + "." + tableName);
            }
        }

        for (Map<String, Object> resultObj : resultMaps) {
            switchDataSourceProperty(String.valueOf(resultObj.get("property_name")), String.valueOf(resultObj.get("property_value")), "Y".equals(String.valueOf(resultObj.get("is_encrypted"))), dataSourceNode);
        }
        return Json.stringify(dataSourceNode);

    }


    private void switchDataSourceProperty (String propertyName, String propertyValue, boolean isEncrypted, ObjectNode dataSourceNode) {
        if (isEncrypted) {
            propertyValue = AES.Decrypt(propertyValue, encryptKey);
        }
        switch (propertyName) {
            case Constant.ELASTICSEARCH_APP_HOST_KEY:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_ES_NODES, propertyValue);
                break;
            case Constant.ELASTICSEARCH_APP_PORT_KEY:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_ES_PORT, propertyValue);
                break;
            case Constant.ORACLE_DB_JDBC_URL:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_URL, propertyValue);
                break;
            case Constant.ORACLE_DB_USERNAME:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_USER, propertyValue);
                break;
            case Constant.ORACLE_DB_PASSWORD:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_PASSWORD, propertyValue);
                break;
            case Constant.PGXZ_DB_JDBC_URL:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_URL, propertyValue);
                break;
            case Constant.PGXZ_DB_USERNAME:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_USER, propertyValue);
                break;
            case Constant.PGXZ_DB_PASSWORD:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_PASSWORD, propertyValue);
                break;
            case Constant.HIVE_METASTORE_HIVESERVER2_URL:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_URL, propertyValue);
                break;
            case Constant.TRAFODION_DB_JDBC_URL:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_URL, propertyValue);
                break;
            case Constant.TRAFODION_DB_USERNAME:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_USER, propertyValue);
                break;
            case Constant.TRAFODION_DB_PASSWORD:
                dataSourceNode.put(Constant.DQ_OPTIONS_KEY_JDBC_PASSWORD, propertyValue);
                break;

        }
    }

    public List<DataQualityRule> getDataQualityRule (String fieldName) {
        return dataQualityDAO.queryDataQualityRule(fieldName);
    }
    public DqAnalyseParam getDueTaskByFieldId (Long fieldId) {
        return dataQualityDAO.queryDueTaskByFieldId(fieldId);
    }
    public List<DataQualityRule> getDataQualityRules () {
        return dataQualityDAO.queryDataQualityRules();
    }

    /***
     * 根据cron表达式生成下次触发的时间
     * @param cronExpr
     * @return Date
     */
    private Date getNextRun(String cronExpr) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpr);
        Date next = cronSequenceGenerator.next(new Date());
        return next;
    }

    public void deleteOldRecords (List<DqAnalyseParam> inputs,String identifier) {
        ArrayList<String> fieldIds = Lists.newArrayList();
        for (DqAnalyseParam input:inputs) {
            Map<String, Object> field = structuredDataDAO.queryFieldByDatasetAndFieldName(input.getDatasetId(), identifier);
            fieldIds.add(String.valueOf(field.get("field_id")));
        }
        dataQualityDAO.deleteOldFieldRule(fieldIds,identifier);
    }

    public Map getDataQualityStatus (Long fieldId) {
        HashMap<String, Object> map = Maps.newHashMap();
        Integer status = dataQualityDAO.queryDataQualityStatusByFieldId(fieldId);
        map.put("status",DqCheckState.getStatus(status));
        map.put("fieldId",fieldId);
        return map;
    }
    public Map getMockDataQualityStatus (Long fieldId) {
        String[] status = {"RUNNING","SUCCESS","ERROR","WAITING"};
        java.util.Random r=new java.util.Random();
        HashMap<String, Object> map = Maps.newHashMap();
        int i = r.nextInt(3);
        map.put("status",status[i]);
        map.put("fieldId",fieldId);
        return map;
    }
}
