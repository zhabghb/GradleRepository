package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DataSource;
import com.cetc.hubble.metagrid.vo.DataSourceOwner;
import com.cetc.hubble.metagrid.vo.DataSourceProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import metagrid.common.Constant;
import metagrid.common.utils.AES;
import metagrid.common.utils.StringNaturalOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Repository
@Transactional
public class DataSourceManagerDAO extends AbstractMySQLOpenSourceDAO {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${metagrid.encrypt.key}")
    private String encryptKey;

    private static final String GET_DATA_SOURCE_TYPE = "select wh_etl_type from wh_etl_job where wh_etl_job_id = ?";
    private static final String GET_ID_LIST_BY_SOURCETYPE = "select wh_etl_job_id from wh_etl_job where wh_etl_type = ?";
    private static final String GET_PROPERTY_LIST_BY_ID = "select property_name, property_value from wh_etl_job_property where wh_etl_job_id = ?";
    private static final String INSERT_JOB_PROPERTY =
            "INSERT INTO wh_etl_job_property(wh_etl_job_name, property_name, property_value, is_encrypted, wh_etl_job_id) "
                    + "VALUES(:whEtlJobName, :propertyName, :propertyValue, :isEncrypted, :whEtlJobId)";

    private static final String INSERT_ETL_JOB =
            "INSERT INTO wh_etl_job (wh_etl_job_name, wh_etl_type, cron_expr, next_run,is_active, data_source_name, create_time, last_update_time) "
                    + "VALUES (:whEtlJobName, :whEtlType, :cronExpr, :nextRun, NULL,:dataSourceName, str_to_date(:createTime,'%Y-%m-%d %H:%i:%s'), str_to_date(:createTime,'%Y-%m-%d %H:%i:%s'))";

    private static final String UPDATE_ETL_JOB =
            "UPDATE wh_etl_job SET is_active = NULL,data_source_name = :dataSourceName,last_update_time = str_to_date(:lastUpdateTime,'%Y-%m-%d %H:%i:%s') WHERE wh_etl_job_id = :whEtlJobId";

    private static final String UPDATE_JOB_PROPERTY =
            "UPDATE wh_etl_job_property SET property_value = :propertyValue WHERE wh_etl_job_id = :whEtlJobId and property_name = :propertyName";

    private static final String DELETE_JOB_ALL =
            "DELETE wej,wejp,dfd,dd FROM wh_etl_job wej LEFT JOIN wh_etl_job_property wejp ON wej.wh_etl_job_id = wejp.wh_etl_job_id LEFT JOIN dict_field_detail dfd ON wej.wh_etl_job_id = dfd.wh_etl_job_id LEFT JOIN dict_dataset dd ON wej.wh_etl_job_id = dd.wh_etl_job_id  WHERE wej.wh_etl_job_id = ?";

//    private static String DETECT_TAG_EXIST = "SELECT id FROM dataset_tag WHERE tag_id = ?";

//    private static String DELETE_TAG = "DELETE FROM dict_tag WHERE id = ?";

    private static final String DELETE_DATASET_TAG =
            "DELETE FROM dataset_tag WHERE dataset_id in (SELECT DISTINCT id from dict_dataset WHERE wh_etl_job_id = ?)";

    private static final  String DOMAIN_DATASET_RELATION_COUNT="select count(*) as relationCount FROM domain_entity WHERE entity_id in (SELECT DISTINCT id from dict_dataset WHERE wh_etl_job_id = ?)";

    private static final String DELETE_JOB_EXECUTION =
            "DELETE FROM wh_etl_job_execution WHERE wh_etl_job_id = ?";

    private static final String DELETE_STAGE_LOG =
            "DELETE FROM stage_log WHERE wh_etl_job_id = ?";

    private static final String GET_JOB_AND_JOB_PROPERTIES =
            "SELECT wej.wh_etl_job_id,wej.is_active,wej.wh_etl_type,DATE_FORMAT(wej.create_time,'%Y/%m/%d %H:%i:%s') create_time,DATE_FORMAT(wej.last_update_time,'%Y/%m/%d %H:%i:%s') last_update_time,wej.data_source_name,wejp.property_name,wejp.property_value "
                    + "FROM wh_etl_job wej LEFT JOIN wh_etl_job_property wejp ON wej.wh_etl_job_id = wejp.wh_etl_job_id  ORDER BY wej.create_time DESC";

    private static final String GET_JOB_AND_PROPERTIES_WITH_OWNER =
            "SELECT wej.wh_etl_job_id,wej.is_active,wej.wh_etl_type,DATE_FORMAT(wej.create_time,'%Y/%m/%d %H:%i:%s') create_time,DATE_FORMAT(wej.last_update_time,'%Y/%m/%d %H:%i:%s') last_update_time,wej.data_source_name,dso.`owner`,dso.owner_platform,dso.owner_department,dso.owner_tel,wejp.property_name,wejp.property_value FROM wh_etl_job wej LEFT JOIN data_source_owner dso on wej.wh_etl_job_id = dso.wh_etl_job_id  LEFT JOIN wh_etl_job_property wejp ON wej.wh_etl_job_id = wejp.wh_etl_job_id ORDER BY wej.create_time DESC";

    private static final String GET_ETL_JOB_BY_ID =
            "SELECT wej.wh_etl_job_id,wej.wh_etl_job_name,wej.wh_etl_type,wej.cron_expr,wej.is_active,DATE_FORMAT(wej.create_time,'%Y-%m-%d %H:%i:%s') create_time,DATE_FORMAT(wej.last_update_time,'%Y-%m-%d %H:%i:%s') last_update_time,wej.data_source_name,wejp.property_name,wejp.property_value,wejp.is_encrypted "
                    + "FROM wh_etl_job wej INNER JOIN wh_etl_job_property wejp ON wej.wh_etl_job_id = wejp.wh_etl_job_id WHERE wej.wh_etl_job_id = ?";

    private static final String GET_ETL_JOB_NAME_BY_ID =
            "SELECT data_source_name as sourceName FROM wh_etl_job  WHERE wh_etl_job_id = ?";

    private static final String GET_ETL_JOB_NAME_AND_COMMENTS_BY_ID =
            "SELECT data_source_name as sourceName,comments FROM wh_etl_job  WHERE wh_etl_job_id = ?";

//    private static final String INSERT_A_TAG = "INSERT INTO dict_tag (id,tag_name) VALUES (?,?)";

//    private static final String UPDATE_A_TAG = "UPDATE dict_tag SET tag_name = ? WHERE id = ?";

    private static final String WHETHER_JOB_RUNNING = "select running from wh_etl_job where wh_etl_job_id = ?";

    private static final String QUERY_JOB_STATUS = "select wh_etl_job_id as sourceId,running,comments,data_source_name as sourceName from wh_etl_job where wh_etl_job_id = ?";

    private static final String WHETHER_JOB_ACTIVE = "select is_active from wh_etl_job where wh_etl_job_id = ?";

    private static final String UPDATE_JOB_NOT_ACTIVE = "UPDATE wh_etl_job SET is_active = 'N' WHERE wh_etl_job_id = ?";

    private static final String UPDATE_JOB_NOT_ACTIVE_WITH_COMMENTS = "UPDATE wh_etl_job SET is_active = 'N',comments = ? WHERE wh_etl_job_id = ?";

    private static final String UPDATE_JOB_COMMENTS = "UPDATE wh_etl_job SET comments = ? WHERE wh_etl_job_id = ?";

    private static final String UPDATE_JOB_ACTIVE = "UPDATE wh_etl_job SET is_active = 'Y' WHERE wh_etl_job_id = ?";

    private static final String QUERY_PROPERTY_BY_JOBID = "SELECT property_name,property_value,is_encrypted FROM wh_etl_job_property WHERE wh_etl_job_id = ?";

    private static final String GET_WH_PROPERTIES = "SELECT * FROM wh_property";

    private static final String QUERY_NAMENODE_IP_PORT = "SELECT property_value FROM wh_etl_job_property WHERE wh_etl_job_id = ? and property_name = ? ";

    public Properties getWhProperties() {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(GET_WH_PROPERTIES);
        Properties ret = new Properties();
        for (Map<String, Object> row : rows) {
            if ((row.get("is_encrypted")).equals("N")) {
                ret.put(row.get("property_name"), row.get("property_value"));
            } else {
                ret.put(row.get("property_name"), AES.Decrypt((String) row.get("property_value"), encryptKey));
            }
        }
        return ret;
    }

    public List<Map<String, Object>> queryPropertyByJobid(Integer whEtljobId) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(QUERY_PROPERTY_BY_JOBID, whEtljobId);
        return resultList;
    }

    public Map<String, Object> queryProperty_value(Integer whEtljobId,String property_name)
    {

        Map<String, Object> result =  getJdbcTemplate().queryForMap(QUERY_NAMENODE_IP_PORT,whEtljobId,property_name);
        return result;
    }

    public HashMap<String, Object> getPropertyMapByJobId(Integer whEtljobId) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(QUERY_PROPERTY_BY_JOBID, whEtljobId);

        HashMap<String, Object> resultMap = Maps.newHashMap();

        for (Map<String, Object> rowMap : resultList) {
            switchDataSourcePropertyCH((String) rowMap.get("property_name"), (String) rowMap.get("property_value"), resultMap);
        }

        return resultMap;
    }


    public int saveDataSource(Map<String, Object> jobParams, Map<String, Object> jobPropertyMap) {

        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource jobParamsSource = new MapSqlParameterSource(jobParams);
        getNamedParameterJdbcTemplate().update(INSERT_ETL_JOB, jobParamsSource, keyHolder);
        List<Map<String, Object>> jobPropertyParamsList = new ArrayList<Map<String, Object>>();
        for (Iterator<Map.Entry<String, Object>> iterator = jobPropertyMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            Map<String, Object> jobPropertyParams = new HashMap<String, Object>();
            jobPropertyParams.put("whEtlJobName", jobParams.get("whEtlJobName"));
            jobPropertyParams.put("propertyName", entry.getKey());
            jobPropertyParams.put("propertyValue", entry.getValue());
            jobPropertyParams.put("isEncrypted", entry.getKey().contains("password") ? "Y" : "N");
            jobPropertyParams.put("whEtlJobId", keyHolder.getKey().intValue());
//            jobPropertyParams.put("NameNodeport",jobParams.get("namenodeport"));
            jobPropertyParamsList.add(jobPropertyParams);
        }
        getNamedParameterJdbcTemplate().batchUpdate(INSERT_JOB_PROPERTY, jobPropertyParamsList.toArray(new Map[jobPropertyParamsList.size()]));
//        tagDAO.addTag(keyHolder.getKey().intValue(), (String) jobParams.get("dataSourceName"));
//        getJdbcTemplate().update(INSERT_A_TAG, keyHolder.getKey().intValue(), jobParams.get("dataSourceName"));
        return keyHolder.getKey().intValue();
    }

    public void updateDataSource(Map<String, Object> jobParams, Map<String, Object> jobPropertyMap) {
        getNamedParameterJdbcTemplate().update(UPDATE_ETL_JOB, jobParams);
        List<Map<String, Object>> jobPropertyParamsList = new ArrayList<Map<String, Object>>();
        for (Iterator<Map.Entry<String, Object>> iterator = jobPropertyMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            Map<String, Object> jobPropertyParams = new HashMap<String, Object>();
            jobPropertyParams.put("whEtlJobId", jobParams.get("whEtlJobId"));
            jobPropertyParams.put("propertyName", entry.getKey());
            jobPropertyParams.put("propertyValue", entry.getValue());
     //       jobPropertyParams.put("Namenode_Port",jobParams.get("NameNodePort"));
            jobPropertyParamsList.add(jobPropertyParams);
        }
        getNamedParameterJdbcTemplate().batchUpdate(UPDATE_JOB_PROPERTY, jobPropertyParamsList.toArray(new Map[jobPropertyParamsList.size()]));
//        Tag tag = new Tag((Integer) jobParams.get("whEtlJobId"), (String) jobParams.get("dataSourceName"));
//        tagDAO.updateTag(tag);
//        getJdbcTemplate().update(UPDATE_A_TAG, jobParams.get("dataSourceName"), jobParams.get("whEtlJobId"));
    }

    public Integer deleteDataSource(long dataSourceId) {
        getJdbcTemplate().update(DELETE_JOB_EXECUTION, dataSourceId);
        getJdbcTemplate().update(DELETE_STAGE_LOG, dataSourceId);
        getJdbcTemplate().update(DELETE_DATASET_TAG, dataSourceId);
//        tagDAO.deleteTag(dataSourceId);

//        List<Map<String, Object>> list = getJdbcTemplate().queryForList(DETECT_TAG_EXIST, dataSourceId);
//        if (list.size() == 0) {
//            getJdbcTemplate().update(DELETE_TAG, dataSourceId);
//        }

        int update = getJdbcTemplate().update(DELETE_JOB_ALL, dataSourceId);
        if (update == 0) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }
        return update;
    }

    public DataSource getEtlJobById(long id) {

        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(GET_ETL_JOB_BY_ID, id);
        if (resultList != null && !resultList.isEmpty()) {
            DataSource dataSource = null;
            for (Map<String, Object> resultObj : resultList) {
                if (dataSource == null) {
                    dataSource = new DataSource();
                    dataSource.setId((int) resultObj.get("wh_etl_job_id"));
                    dataSource.setEtlJobName(String.valueOf(resultObj.get("wh_etl_job_name")));
                    dataSource.setEtlType(String.valueOf(resultObj.get("wh_etl_type")));
                    dataSource.setCronExpr(String.valueOf(resultObj.get("cron_expr")));
                    dataSource.setActive(String.valueOf(resultObj.get("is_active")).equals("Y") ? true : false);
                    dataSource.setDataSourceName(String.valueOf(resultObj.get("data_source_name")));
                    dataSource.setCreateTime(String.valueOf(resultObj.get("create_time")));
                    dataSource.setLastUpdateTime(String.valueOf(resultObj.get("last_update_time")));
                }
                DataSourceProperty dataSourceProperty = new DataSourceProperty();
                dataSourceProperty.setEncrypted(String.valueOf(resultObj.get("is_encrypted")).equals("Y") ? true : false);
                if (dataSourceProperty.isEncrypted()) {
                    String decryptedValue = AES.Decrypt(String.valueOf(resultObj.get("property_value")), encryptKey);
                    dataSourceProperty.setPropertyValue(decryptedValue);
                } else {
                    dataSourceProperty.setPropertyValue(String.valueOf(resultObj.get("property_value")));
                }
                dataSourceProperty.setPropertyName(String.valueOf(resultObj.get("property_name")));
                dataSource.getEtlJobProperties().add(dataSourceProperty);
            }
            return dataSource;
        }
        return null;
    }

    public List<Map<String, Object>> getAllDataSourceInfo(boolean withOwner) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        Map<String, Object> oracleType = new HashMap<String, Object>();
        oracleType.put("categoryName", Constant.ORACLE_DATA_SOURCE);
        oracleType.put("tableTotal", 0);
        List<Map<String, Object>> oracleDataSources = new ArrayList<Map<String, Object>>();
        oracleType.put("dataSources", oracleDataSources);

        Map<String, Object> pgxzType = new HashMap<String, Object>();
        pgxzType.put("categoryName", Constant.PGXZ_DATA_SOURCE);
        pgxzType.put("tableTotal", 0);
        List<Map<String, Object>> pgxzDataSources = new ArrayList<Map<String, Object>>();
        pgxzType.put("dataSources", pgxzDataSources);

        Map<String, Object> hiveType = new HashMap<String, Object>();
        hiveType.put("categoryName", Constant.HIVE_DATA_SOURCE);
        hiveType.put("tableTotal", 0);
        List<Map<String, Object>> hiveDataSources = new ArrayList<Map<String, Object>>();
        hiveType.put("dataSources", hiveDataSources);

        Map<String, Object> elasticSearchType = new HashMap<String, Object>();
        elasticSearchType.put("categoryName", Constant.ELASTICSEARCH_DATA_SOURCE);
        elasticSearchType.put("tableTotal", 0);
        List<Map<String, Object>> elasticSearchDataSources = new ArrayList<Map<String, Object>>();
        elasticSearchType.put("dataSources", elasticSearchDataSources);

        Map<String, Object> trafodionType = new HashMap<String, Object>();
        trafodionType.put("categoryName", Constant.TRAFODION_DATA_SOURCE);
        trafodionType.put("tableTotal", 0);
        List<Map<String, Object>> trafodionDataSources = new ArrayList<Map<String, Object>>();
        trafodionType.put("dataSources", trafodionDataSources);

        Map<String, Object> hbaseType = new HashMap<String, Object>();
        hbaseType.put("categoryName", Constant.HBASE_DATA_SOURCE);
        hbaseType.put("tableTotal", 0);
        List<Map<String, Object>> hbaseDataSources = new ArrayList<Map<String, Object>>();
        hbaseType.put("dataSources", hbaseDataSources);

        Map<String, Object> hdfsType = new HashMap<String, Object>();
        hdfsType.put("categoryName", Constant.HDFS_DATA_SOURCE);
        hdfsType.put("tableTotal", 0);
        List<Map<String, Object>> hdfsDataSources = new ArrayList<Map<String, Object>>();
        hdfsType.put("dataSources", hdfsDataSources);

        resultList.add(oracleType);
        resultList.add(pgxzType);
        resultList.add(hiveType);
        resultList.add(elasticSearchType);
        resultList.add(trafodionType);
        resultList.add(hbaseType);
        resultList.add(hdfsType);

        int pingSuccessOra = 0;
        int pingSuccessPgxz = 0;
        int pingSuccessHbase = 0;
        int pingSuccessHive = 0;
        int pingSuccessES = 0;
        int pingSuccessTra = 0;
        int pingSuccessHdfs = 0;

        int pingErrorOra = 0;
        int pingErrorPgxz = 0;
        int pingErrorHbase = 0;
        int pingErrorHive = 0;
        int pingErrorES = 0;
        int pingErrorTra = 0;
        int pingErrorHdfs = 0;
        List<Map<String, Object>> queryList = getJdbcTemplate().queryForList(withOwner?GET_JOB_AND_PROPERTIES_WITH_OWNER:GET_JOB_AND_JOB_PROPERTIES);
        if (queryList != null && !queryList.isEmpty()) {
            Map<Integer, Map<String, Object>> mapperMap = new HashMap<Integer, Map<String, Object>>();
            for (Map<String, Object> rowMap : queryList) {
                Integer dataSourceId = Integer.parseInt( String.valueOf(rowMap.get("wh_etl_job_id")));
                String dataSourceType = (String) rowMap.get("wh_etl_type");
                if (mapperMap.containsKey(dataSourceId)) {
                    switchDataSourceProperty((String) rowMap.get("property_name"), (String) rowMap.get("property_value"), mapperMap.get(dataSourceId));
                } else {
                    Map<String, Object> dataSourceMap = new HashMap<String, Object>();
                    String dataSourceName = String.valueOf(rowMap.get("data_source_name"));
                    dataSourceMap.put("id", dataSourceId);
                    dataSourceMap.put("dataSourceName", dataSourceName);
                    dataSourceMap.put("dataSourceTableTotal", 0);
                    dataSourceMap.put("createTime", rowMap.get("create_time"));
                    dataSourceMap.put("lastUpdateTime", rowMap.get("last_update_time"));
                    dataSourceMap.put("status", rowMap.get("is_active") == null ? null : (rowMap.get("is_active").equals("Y") ? "success" : "error"));

                    if (withOwner){
                        DataSourceOwner dataSourceOwner = new DataSourceOwner(dataSourceId, dataSourceName,(String)rowMap.get("owner"),(String)rowMap.get("owner_tel"),(String)rowMap.get("owner_platform"),(String)rowMap.get("owner_department"));
                        dataSourceMap.put("ownerInfo",dataSourceOwner);
                    }

                    switchDataSourceProperty((String) rowMap.get("property_name"), (String) rowMap.get("property_value"), dataSourceMap);
                    mapperMap.put(dataSourceId, dataSourceMap);
                    switch (dataSourceType) {
                        case Constant.ORACLE_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessOra++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorOra++;
                            }
                            oracleDataSources.add(dataSourceMap);
                            break;
                        case Constant.PGXZ_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessPgxz++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorPgxz++;
                            }
                            pgxzDataSources.add(dataSourceMap);
                            break;
                        case Constant.HIVE_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessHive++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorHive++;
                            }
                            hiveDataSources.add(dataSourceMap);
                            break;
                        case Constant.ELASTICSEARCH_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessES++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorES++;
                            }
                            elasticSearchDataSources.add(dataSourceMap);
                            break;
                        case Constant.TRAFODION_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessTra++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorTra++;
                            }
                            trafodionDataSources.add(dataSourceMap);
                            break;
                        case Constant.HBASE_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessHbase++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorHbase++;
                            }
                            hbaseDataSources.add(dataSourceMap);
                            break;
                        case Constant.HDFS_DATA_SOURCE:
                            if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("success"))) {
                                pingSuccessHdfs++;
                            } else if ((dataSourceMap.get("status") != null) && (dataSourceMap.get("status").equals("error"))) {
                                pingErrorHdfs++;
                            }
//                            dataSourceMap.put("hdfsdataSerport",rowMap.get("namenode_port"));
                            hdfsDataSources.add(dataSourceMap);
                            break;
                    }
                }
            }
        }
        oracleType.put("pingSuccessNum", pingSuccessOra);
        oracleType.put("pingErrorNum", pingErrorOra);
        pgxzType.put("pingSuccessNum", pingSuccessPgxz);
        pgxzType.put("pingErrorNum", pingErrorPgxz);
        elasticSearchType.put("pingSuccessNum", pingSuccessES);
        elasticSearchType.put("pingErrorNum", pingErrorES);
        hiveType.put("pingSuccessNum", pingSuccessHive);
        hdfsType.put("pingSuccessNum",pingSuccessHdfs);
        hiveType.put("pingErrorNum", pingErrorHive);
        trafodionType.put("pingSuccessNum", pingSuccessTra);
        trafodionType.put("pingErrorNum", pingErrorTra);
        hbaseType.put("pingSuccessNum", pingSuccessHbase);
        hbaseType.put("pingErrorNum", pingErrorHbase);
        hdfsType.put("pingErrorNum",pingErrorHdfs);
        // 把每个数据源的个数做统计一并返回
        for (Map<String, Object> typeMap : resultList) {
            typeMap.put("totalDataSourcesNum", ((List) typeMap.get("dataSources")).size());
        }
        return resultList;
    }

    /**
     * 根据数据源对应的属性名称返回正确的key
     *
     * @param propertyName
     * @param propertyValue
     * @param dataSourceMap
     */
    private void switchDataSourceProperty(String propertyName, String propertyValue, Map<String, Object> dataSourceMap) {
        switch (propertyName) {
            case Constant.ORACLE_DB_JDBC_URL:
                dataSourceMap.put("oracleUrl", propertyValue);
                String[] strings = propertyValue.split("//")[1].split(":");
                String[] values = strings[1].split("/");
                dataSourceMap.put("oracleIP", strings[0]);
                dataSourceMap.put("oraclePort", values[0]);
                dataSourceMap.put("oracleName", values[1]);
                break;
            case Constant.ORACLE_DB_USERNAME:
                dataSourceMap.put("oracleUsername", propertyValue);
                break;
            case Constant.ORACLE_DB_PASSWORD:
                dataSourceMap.put("oraclePassword", propertyValue);
                break;
            case Constant.PGXZ_DB_JDBC_URL:
                dataSourceMap.put("pgxzUrl", propertyValue);
                String[] strings2 = propertyValue.split("//")[1].split(":");
                String[] values2 = strings2[1].split("/");
                dataSourceMap.put("pgxzIP", strings2[0]);
                dataSourceMap.put("pgxzPort", values2[0]);
                dataSourceMap.put("pgxzName", values2[1]);
                break;
            case Constant.PGXZ_DB_USERNAME:
                dataSourceMap.put("pgxzUsername", propertyValue);
                break;
            case Constant.PGXZ_DB_PASSWORD:
                dataSourceMap.put("pgxzPassword", propertyValue);
                break;
            case Constant.HIVE_METASTORE_HIVESERVER2_URL:
                dataSourceMap.put("hiveServer2Url", propertyValue);
                String[] stringsHive2 = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("hiveServer2IP", stringsHive2[0]);
                dataSourceMap.put("hiveServer2Port", stringsHive2[1].split("/")[0]);
                break;
            case Constant.HIVE_METASTORE_JDBC_URL:
                dataSourceMap.put("hiveUrl", propertyValue);
                String[] stringsHive = propertyValue.split("//")[1].split(":");
                String[] valuesHive = stringsHive[1].split("/");
                dataSourceMap.put("hiveIP", stringsHive[0]);
                dataSourceMap.put("hivePort", valuesHive[0]);
                dataSourceMap.put("hiveName", valuesHive[1]);
                break;
            case Constant.HIVE_METASTORE_USERNAME:
                dataSourceMap.put("hiveUsername", propertyValue);
                break;
            case Constant.HIVE_METASTORE_PASSWORD:
                dataSourceMap.put("hivePassword", propertyValue);
                break;
            case Constant.ELASTICSEARCH_APP_HOST_KEY:
                dataSourceMap.put("elasticsearchHost", propertyValue);
                break;
            case Constant.ELASTICSEARCH_APP_PORT_KEY:
                dataSourceMap.put("elasticsearchPort", propertyValue);
                break;
            case Constant.TRAFODION_DB_JDBC_URL:
                dataSourceMap.put("trafodionUrl", propertyValue);
                String[] stringsTro = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("trafodionIP", stringsTro[0]);
                dataSourceMap.put("trafodionPort", stringsTro[1].split("/")[0]);
                break;
            case Constant.TRAFODION_DB_USERNAME:
                dataSourceMap.put("trafodionUsername", propertyValue);
                break;
            case Constant.TRAFODION_DB_PASSWORD:
                dataSourceMap.put("trafodionPassword", propertyValue);
                break;
//            case Constant.HBASE_ROOTDIR:
//                dataSourceMap.put("hbaseUrl",propertyValue);
//                break;
            case Constant.HBASE_ZOOKEEPER_QUORUM:
                dataSourceMap.put("zookeeperUrl", propertyValue);
                break;
            case Constant.HBASE_ZK_CLIENT_PORT:
                dataSourceMap.put("zkClientPort", propertyValue);
                break;
            case Constant.HDFS_URL:
                dataSourceMap.put("hdfsUrl", propertyValue);
                String[] ip = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("hdfsIP", ip[0]);
                dataSourceMap.put("hdfsPort", ip[1].split("/")[0]);
                break;

            case Constant.HDFS_NAMENODEPORT:
                dataSourceMap.put("hdfsdataSerport",propertyValue);
                break;

        }
    }
    /**
     * 根据数据源对应的属性名称返回正确的key
     *
     * @param propertyName
     * @param propertyValue
     * @param dataSourceMap
     */
    private void switchDataSourcePropertyCH(String propertyName, String propertyValue, Map<String, Object> dataSourceMap) {
        switch (propertyName) {
            case Constant.ORACLE_DB_JDBC_URL:
                dataSourceMap.put("JDBC_URL", propertyValue);
                String[] strings = propertyValue.split("//")[1].split(":");
                String[] values = strings[1].split("/");
                dataSourceMap.put("主机/IP", strings[0]);
                dataSourceMap.put("端口", values[0]);
                dataSourceMap.put("服务名/SID", values[1]);
                break;
            case Constant.ORACLE_DB_USERNAME:
                dataSourceMap.put("用户名", propertyValue);
                break;
            case Constant.ORACLE_DB_PASSWORD:
                dataSourceMap.put("用户密码", propertyValue);
                break;
            case Constant.HIVE_METASTORE_HIVESERVER2_URL:
                dataSourceMap.put("HiveServer2 JDBC_URL", propertyValue);
                String[] stringsHive2 = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("HiveServer2主机/IP", stringsHive2[0]);
                dataSourceMap.put("HiveServer2端口", stringsHive2[1].split("/")[0]);
                break;
            case Constant.HIVE_METASTORE_JDBC_URL:
                dataSourceMap.put("HIVE_METASTORE JDBC_URL", propertyValue);
                String[] stringsHive = propertyValue.split("//")[1].split(":");
                String[] valuesHive = stringsHive[1].split("/");
                dataSourceMap.put("HIVE_METASTORE主机/IP", stringsHive[0]);
                dataSourceMap.put("HIVE_METASTORE端口", valuesHive[0]);
                dataSourceMap.put("HIVE_METASTORE数据库", valuesHive[1]);
                break;
            case Constant.HIVE_METASTORE_USERNAME:
                dataSourceMap.put("HIVE_METASTORE用户名", propertyValue);
                break;
            case Constant.HIVE_METASTORE_PASSWORD:
                dataSourceMap.put("HIVE_METASTORE密码", propertyValue);
                break;
            case Constant.ELASTICSEARCH_APP_HOST_KEY:
                dataSourceMap.put("主机/IP", propertyValue);
                break;
            case Constant.ELASTICSEARCH_APP_PORT_KEY:
                dataSourceMap.put("端口", propertyValue);
                break;
            case Constant.TRAFODION_DB_JDBC_URL:
                dataSourceMap.put("JDBC_URL", propertyValue);
                String[] stringsTro = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("主机/IP", stringsTro[0]);
                dataSourceMap.put("端口", stringsTro[1].split("/")[0]);
                break;
            case Constant.TRAFODION_DB_USERNAME:
                dataSourceMap.put("用户名", propertyValue);
                break;
            case Constant.TRAFODION_DB_PASSWORD:
                dataSourceMap.put("用户密码", propertyValue);
                break;
            case Constant.HBASE_ZOOKEEPER_QUORUM:
                dataSourceMap.put("ZOOKEEPER集群", propertyValue);
                break;
            case Constant.HBASE_ZK_CLIENT_PORT:
                dataSourceMap.put("ZK客户端端口", propertyValue);
                break;
            case Constant.HDFS_URL:
                dataSourceMap.put("hdfsUrl", propertyValue);
                String[] ip = propertyValue.split("//")[1].split(":");
                dataSourceMap.put("hdfsIP", ip[0]);
                dataSourceMap.put("hdfsPort", ip[1].split("/")[0]);
                break;
        }
    }


    public String getSourceNameById(Integer dataSourceId) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(GET_ETL_JOB_NAME_BY_ID, dataSourceId);
        if (resultList.size() != 1) {
            logger.error("=======rows size not legal:{}=======", resultList.size());
            throw new AppException("数据源id:" + dataSourceId + "不存在！", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return (String) resultList.get(0).get("sourceName");
    }

    public Map<String, Object> getSourceInfoById(Integer dataSourceId) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(GET_ETL_JOB_NAME_AND_COMMENTS_BY_ID, dataSourceId);
        if (resultList.size() != 1) {
            logger.error("=======rows size not legal:{}=======", resultList.size());
            throw new AppException("数据源id:" + dataSourceId + "不存在！", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return resultList.get(0);
    }

    public boolean queryWhetherJobRunning(int dataSourceId) {
        Map<String, Object> map = getJdbcTemplate().queryForMap(WHETHER_JOB_RUNNING, dataSourceId);
        Integer running = (Integer) map.get("running");
        return running == 0 ? false : true;
    }

    public boolean queryWhetherJobActive(int dataSourceId) {
        Map<String, Object> map = getJdbcTemplate().queryForMap(WHETHER_JOB_ACTIVE, dataSourceId);
        String isActive = (String) map.get("is_active");
        return isActive.equals("N") ? false : true;
    }

    public void updateJobActive(int dataSourceId) {
        getJdbcTemplate().update(UPDATE_JOB_ACTIVE, dataSourceId);
    }

    public void updateJobNotActive(int dataSourceId, String comments,boolean updateComments) {
        if (updateComments){
            getJdbcTemplate().update(UPDATE_JOB_NOT_ACTIVE_WITH_COMMENTS, comments, dataSourceId);
        }else{
            getJdbcTemplate().update(UPDATE_JOB_NOT_ACTIVE, dataSourceId);
        }
    }

    public void updateJobComments(int dataSourceId, String comments) {
        getJdbcTemplate().update(UPDATE_JOB_COMMENTS, comments, dataSourceId);
    }

    public Map<String, Object> querySourceStatusById(int sourceId) {
        return getJdbcTemplate().queryForMap(QUERY_JOB_STATUS, sourceId);
    }
    public List<Map<String, Object>> querySourcesByType(String sourceType) {
        return  getJdbcTemplate().queryForList(GET_ID_LIST_BY_SOURCETYPE, sourceType);
    }

    /**
     * Detection of duplication data source.
     *
     * @param jobParams      job parameters
     * @param jobPropertyMap job properties
     */
    public boolean isDataSourceDuplicate(Map<String, Object> jobParams, Map<String, Object> jobPropertyMap) {

        boolean res = false;
        // for datasource update
        Integer updateID = (Integer) jobParams.get("whEtlJobId");
        // there will be no job ID if it is a new data source
        boolean isUpdate = updateID != null;
        String sourceType = (String) jobParams.get("whEtlType");
        if (isUpdate) {
            sourceType = getJdbcTemplate().queryForObject(GET_DATA_SOURCE_TYPE, String.class, updateID.intValue());
        }
        List<Map<String, Object>> idRows = getJdbcTemplate().queryForList(GET_ID_LIST_BY_SOURCETYPE, sourceType);
        // when there is no such data source type
        if (0 == idRows.size()) return res;
        // get input properties natural sorted
        StringNaturalOrderComparator comparator = new StringNaturalOrderComparator();
        StringBuffer sb = new StringBuffer();
        String inputSource = buildPropStrFromMap(sb, jobPropertyMap, comparator);
        logger.debug("Input data source properties' concat : {}", inputSource);
        for (Map<String, Object> idRow : idRows) {
            Integer id = (Integer) idRow.get("wh_etl_job_id");
            if (isUpdate && id.intValue() == updateID.intValue()) continue;
            List<Map<String, Object>> storedPropertyRows =
                    getJdbcTemplate().queryForList(GET_PROPERTY_LIST_BY_ID, id.intValue());
            // when no properties exist
            if (0 == storedPropertyRows.size()) continue;
            // if length differs from the input property list, no need to compare
            // continue loop
            if (jobPropertyMap.size() != storedPropertyRows.size()) {
                continue;
            } else {
                Map<String, Object> storedPropMap = Maps.newHashMap();
                for (Map<String, Object> storedPropertyRow : storedPropertyRows) {
                    String key = (String) storedPropertyRow.get("property_name");
                    Object value = storedPropertyRow.get("property_value");
                    storedPropMap.put(key, value);
                }
                String storedSource = buildPropStrFromMap(sb, storedPropMap, comparator);

                if(sourceType.equals("HDFS"))
                {
                    String storeNamePort = getNameNodePort(storedSource);
                    String storeWebPort = getWebport(storedSource);
                    String storeIp = getIp(storedSource);

                    String inputNamePort = getNameNodePort(inputSource);
                    String inputWebPort = getWebport(inputSource);
                    String inputIp = getIp(inputSource);

                    if(inputIp.equals(storeIp))
                    {
                        if(inputNamePort.equals(storeNamePort) || inputWebPort.equals(storeWebPort))
                        {
                            logger.debug("The same data source properties concat : {}", storedSource);
                            res = true;
                            return res;
                        }
                    }
                }
                else
                {
                    if (storedSource.compareTo(inputSource) == 0) {
                        logger.debug("The same data source properties concat : {}", storedSource);
                        res = true;
                        return res;
                    }
                }

            }
        }
        return res;
    }


    public static String getNameNodePort(String str)
    {
        int index = str.indexOf('h');

        String nameNodePort = str.substring(0, index);

        return nameNodePort;

    }

    public static String getIp(String str)
    {

        int indexStart = str.indexOf('/');
        int indexEnd = str.lastIndexOf(':');

        String ip = str.substring(indexStart+2, indexEnd);

        return ip;
    }

    public static String getWebport(String str)
    {
        int index = str.lastIndexOf(':');
        String webPort = str.substring(index+1, str.length()-1);

        return webPort;
    }


    /**
     * Build a String out of a collection using string buffer
     * then clear the StringBuffer.
     *
     * @param sb
     * @param collection
     * @return
     */
    private String buildStrFromCollection(StringBuffer sb, Collection collection) {

        for (Object s : collection) {
            sb.append(((String) s).trim());
        }
        String res = sb.toString();
        // empty string buffer
        sb.delete(0, sb.length());
        return res;
    }

    /**
     * Build a String out of a property map using string buffer,
     * change several properties's case except for passwords and
     * hive meta store user name (mysql user name), then clear
     * the StringBuffer.
     *
     * @param sb string buffer to reuse
     * @param jobPropMap job's property map
     * @param c comparator to sort the list
     * @return
     */
    private String buildPropStrFromMap(StringBuffer sb, Map<String, Object> jobPropMap,
                                       Comparator c) {

        List properties = Lists.newArrayList();
        for (Iterator<Map.Entry<String, Object>> iterator = jobPropMap.entrySet().iterator();
             iterator.hasNext(); ) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (key.contains("password")||key.contains(Constant.HIVE_METASTORE_HIVESERVER2_URL)) continue; // bypass password
            // several passwords as well as hive metastore username are case sensitive
            if (!key.equals(Constant.HIVE_METASTORE_USERNAME)) {
                value = value.toLowerCase();
            }
            // build property list for sorting
            // trim in case heading or trailing spaces exist
            properties.add(value.trim());
        }
        Collections.sort(properties, c);
        // fill up string builder with contents
        for (Object s : properties) sb.append((String) s);
        String res = sb.toString();
        // empty string buffer
        sb.delete(0, sb.length());
        return res;
    }

    public Long getTableDomainCount(long dataSourceId) {
        Map<String, Object> res = getJdbcTemplate().queryForMap(DOMAIN_DATASET_RELATION_COUNT, dataSourceId);
        return (Long) res.get("relationCount");
    }
}
