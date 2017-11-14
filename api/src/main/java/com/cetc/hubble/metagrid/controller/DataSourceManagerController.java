package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DataSourceManagerService;
import com.cetc.hubble.metagrid.service.HDFSService;
import com.cetc.hubble.metagrid.service.StructuredDataService;
import com.cetc.hubble.metagrid.vo.*;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import metadata.etl.models.EtlJobName;
import metagrid.common.Constant;
import metagrid.common.utils.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sunfeng on 2016/10/11.
 */
@RestController
@RequestMapping(value = "/v2/api/dataSourceManager/")
@Api(value = "/api", description = "数据源管理API列表")
public class DataSourceManagerController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DataSourceManagerService dataSourceManagerService;
    @Autowired
    private StructuredDataService structuredDataService;
    @Value("${metagrid.encrypt.key}")
    private String encryptKey;

    @Autowired
    private HDFSService hdfsService;

    /**
     * 得到所有的数据源信息
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "数据源信息查询")
    public List<Map<String, Object>> getAllDataSourceInfo(@RequestParam(required = false) @ApiParam(value = "是否查询资源所属人信息") boolean withOwner) {
        try {
            return dataSourceManagerService.getAllDataSourceInfo(withOwner);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("查询数据源信息失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }


    /**
     * 删除数据源
     */
    @RequestMapping(value = "/{dataSourceId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据源")
    public void deleteDataSource(@PathVariable("dataSourceId") @ApiParam(value = "输入要删除的数据源id", required = true) int dataSourceId) {
        try {
            //删除数据源之前判断该数据源下表是否关联了资源集合
            if (dataSourceManagerService.checkTableInDomain(dataSourceId)){
                throw new AppException("该数据源中有表关联到了资源集合，不能删除", ErrorCode.CUSTOM_EXCEPTION);
            }
            Integer result = dataSourceManagerService.deleteDataSource(dataSourceId);
            //删除数据源之后删除该源下的hdfs文件属性
            if(result>0){
                hdfsService.deleteHdfsFileAttrBySourceId(dataSourceId);
            }


        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("删除数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增Oracle数据源
     */
    @RequestMapping(value = "/oracle/", method = RequestMethod.POST)
    @ApiOperation(value = "新增Oracle数据源")
    public Map saveOracleDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) OracleDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.ORACLE_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.ORACLE_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.ORACLE_DB_JDBC_URL, inputs.getOracleUrl());
            jobPropertyMap.put(Constant.ORACLE_DB_USERNAME, inputs.getOracleUsername());
            jobPropertyMap.put(Constant.ORACLE_DB_PASSWORD, inputs.getOraclePassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增Oracle数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }


    /**
     * 修改Oracle数据源
     */
    @RequestMapping(value = "/oracle/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改Oracle数据源")
    public void updateOracleDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) OracleDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.ORACLE_DB_JDBC_URL, inputs.getOracleUrl());
            jobPropertyMap.put(Constant.ORACLE_DB_USERNAME, inputs.getOracleUsername());
            jobPropertyMap.put(Constant.ORACLE_DB_PASSWORD, inputs.getOraclePassword());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改Oracle数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接Oracle数据源
     */
    @RequestMapping(value = "/oracle/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接Oracle数据源")
    public void connectOracleDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) OracleDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.ORACLE_DB_JDBC_URL, inputs.getOracleUrl());
            prop.setProperty(Constant.ORACLE_DB_USERNAME, inputs.getOracleUsername());
            String decryptedValue = AES.Decrypt(inputs.getOraclePassword(), encryptKey);
            prop.setProperty(Constant.ORACLE_DB_PASSWORD, decryptedValue);
            dataSourceManagerService.ping(prop, EtlJobName.ORACLE_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Oracle数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接Oracle数据源
     */
    @RequestMapping(value = "/oracle/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接Oracle数据源")
    public void connectOracleDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Oracle数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增Hive数据源
     */
    @RequestMapping(value = "/hive", method = RequestMethod.POST)
    @ApiOperation(value = "新增Hive数据源")
    public Map saveHiveDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HiveDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.HIVE_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.HIVE_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HIVE_METASTORE_JDBC_URL, inputs.getHiveUrl());
            jobPropertyMap.put(Constant.HIVE_METASTORE_USERNAME, inputs.getHiveUsername());
            jobPropertyMap.put(Constant.HIVE_METASTORE_PASSWORD, inputs.getHivePassword());
            jobPropertyMap.put(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
//            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException)
                throw (AppException) e;
            throw new AppException("新增Hive数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 修改Hive数据源
     */
    @RequestMapping(value = "/hive/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改Hive数据源")
    public void updateHiveDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HiveDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HIVE_METASTORE_JDBC_URL, inputs.getHiveUrl());
            jobPropertyMap.put(Constant.HIVE_METASTORE_USERNAME, inputs.getHiveUsername());
            jobPropertyMap.put(Constant.HIVE_METASTORE_PASSWORD, inputs.getHivePassword());
            jobPropertyMap.put(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
//            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改Hive数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }


    /**
     * 测试连接Hive数据源
     */
    @RequestMapping(value = "/hive/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接Hive数据源")
    public void connectHiveDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HiveDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.HIVE_METASTORE_JDBC_URL, inputs.getHiveUrl());
            prop.setProperty(Constant.HIVE_METASTORE_USERNAME, inputs.getHiveUsername());
            String decryptedValue = AES.Decrypt(inputs.getHivePassword(), encryptKey);
            prop.setProperty(Constant.HIVE_METASTORE_PASSWORD, decryptedValue);
            prop.setProperty(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            prop.setProperty(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
            dataSourceManagerService.ping(prop, EtlJobName.HIVE_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Hive数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接Hive数据源
     */
    @RequestMapping(value = "/hive/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接Hive数据源")
    public void connectHiveDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Hive数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }


    /**
     * 新增ElasticSearch数据源
     */
    @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST)
    @ApiOperation(value = "新增ElasticSearch数据源")
    public Map saveElasticSearchDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) ElasticSearchDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.ELASTICSEARCH_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.ELASTICSEARCH_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.ELASTICSEARCH_APP_HOST_KEY, inputs.getElasticsearchHost());
            jobPropertyMap.put(Constant.ELASTICSEARCH_APP_PORT_KEY, inputs.getElasticsearchPort());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增ElasticSearch数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 修改ElasticSearch数据源
     */
    @RequestMapping(value = "/elasticsearch/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改ElasticSearch数据源")
    public void updateElasticSearchDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) ElasticSearchDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.ELASTICSEARCH_APP_HOST_KEY, inputs.getElasticsearchHost());
            jobPropertyMap.put(Constant.ELASTICSEARCH_APP_PORT_KEY, inputs.getElasticsearchPort());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改ElasticSearch数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接ElasticSearch数据源
     */
    @RequestMapping(value = "/elasticsearch/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接ElasticSearch数据源")
    public void connectElasticSearchDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) ElasticSearchDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.ELASTICSEARCH_APP_HOST_KEY, inputs.getElasticsearchHost());
            prop.setProperty(Constant.ELASTICSEARCH_APP_PORT_KEY, inputs.getElasticsearchPort());
            dataSourceManagerService.ping(prop, EtlJobName.ELASTICSEARCH_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接ElasticSearch数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    @RequestMapping(value = "/elasticsearch/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接ElasticSearch数据源")
    public void connectElasticSearchDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接ElasticSearch数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增Trafodion数据源
     */
    @RequestMapping(value = "/trafodion", method = RequestMethod.POST)
    @ApiOperation(value = "新增Trafodion数据源")
    public Map saveTrafodionDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) TrafodionDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.TRAFODION_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.TRAFODION_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增Trafodion数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 修改Trafodion数据源
     */
    @RequestMapping(value = "/trafodion/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改Trafodion数据源")
    public void updateTrafodionDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) TrafodionDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改Trafodion数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接Trafodion数据源
     */
    @RequestMapping(value = "/trafodion/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接Trafodion数据源")
    public void connectTrafodionDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) TrafodionDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
            prop.setProperty(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
            String decryptedValue = AES.Decrypt(inputs.getTrafodionPassword(), encryptKey);
            prop.setProperty(Constant.TRAFODION_DB_PASSWORD, decryptedValue);
            dataSourceManagerService.ping(prop, EtlJobName.TRAFODION_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Trafodion数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    @RequestMapping(value = "/trafodion/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接Trafodion数据源")
    public void connectTrafodionDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接Trafodion数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增PGXZ数据源
     */
    @RequestMapping(value = "/pgxz", method = RequestMethod.POST)
    @ApiOperation(value = "新增PGXZ数据源")
    public Map savePGXZDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) PGXZDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.PGXZ_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.PGXZ_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.PGXZ_DB_JDBC_URL, inputs.getPgxzUrl());
            jobPropertyMap.put(Constant.PGXZ_DB_USERNAME, inputs.getPgxzUsername());
            jobPropertyMap.put(Constant.PGXZ_DB_PASSWORD, inputs.getPgxzPassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增PGXZ数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 修改PGXZ数据源
     */
    @RequestMapping(value = "/pgxz/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改PGXZ数据源")
    public void updatePGXZDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) PGXZDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.PGXZ_DB_JDBC_URL, inputs.getPgxzUrl());
            jobPropertyMap.put(Constant.PGXZ_DB_USERNAME, inputs.getPgxzUsername());
            jobPropertyMap.put(Constant.PGXZ_DB_PASSWORD, inputs.getPgxzPassword());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改PGXZ数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接PGXZ数据源
     */
    @RequestMapping(value = "/pgxz/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接PGXZ数据源")
    public void connectPGXZDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) PGXZDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.PGXZ_DB_JDBC_URL, inputs.getPgxzUrl());
            prop.setProperty(Constant.PGXZ_DB_USERNAME, inputs.getPgxzUsername());
            String decryptedValue = AES.Decrypt(inputs.getPgxzPassword(), encryptKey);
            prop.setProperty(Constant.PGXZ_DB_PASSWORD, decryptedValue);
            dataSourceManagerService.ping(prop, EtlJobName.PGXZ_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接PGXZ数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    @RequestMapping(value = "/pgxz/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接PGXZ数据源")
    public void connectPGXZDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(), false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接PGXZ数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增HBase数据源
     */
    @RequestMapping(value = "/hbase", method = RequestMethod.POST)
    @ApiOperation(value = "新增HBase数据源")
    public Map saveHBaseDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HBaseDataSourceParam inputs) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.HBASE_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.HBASE_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HBASE_ZOOKEEPER_QUORUM, inputs.getZookeeperUrl());
            jobPropertyMap.put(Constant.HBASE_ZK_CLIENT_PORT, inputs.getZkClientPort());
            //jobPropertyMap.put(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            jobPropertyMap.put(Constant.HBASE_ROOTDIR, inputs.getHbaseUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
//            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增HBase数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 修改HBase数据源
     */
    @RequestMapping(value = "/hbase/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改HBase数据源")
    public void updateHBaseDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HBaseDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId) {
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HBASE_ZOOKEEPER_QUORUM, inputs.getZookeeperUrl());
            jobPropertyMap.put(Constant.HBASE_ZK_CLIENT_PORT, inputs.getZkClientPort());
            //jobPropertyMap.put(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            jobPropertyMap.put(Constant.HBASE_ROOTDIR, inputs.getHbaseUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
//            jobPropertyMap.put(Constant.TRAFODION_DB_USERNAME, inputs.getTrafodionUsername());
//            jobPropertyMap.put(Constant.TRAFODION_DB_PASSWORD, inputs.getTrafodionPassword());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改HBase数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接HBase数据源
     */
    @RequestMapping(value = "/hbase/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接HBase数据源")
    public void connectHBaseDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HBaseDataSourceParam inputs) {
        try {
            Properties prop = new Properties();
            prop.setProperty(Constant.HBASE_ZOOKEEPER_QUORUM, inputs.getZookeeperUrl());
            prop.setProperty(Constant.HBASE_ZK_CLIENT_PORT, inputs.getZkClientPort());
            //prop.setProperty(Constant.HIVE_METASTORE_HIVESERVER2_URL, inputs.getHiveServer2Url());
//            prop.setProperty(Constant.HBASE_ROOTDIR, inputs.getHbaseUrl());
//            prop.setProperty(Constant.TRAFODION_DB_JDBC_URL, inputs.getTrafodionUrl());
            dataSourceManagerService.ping(prop, EtlJobName.HBASE_DATASET_METADATA_ETL);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接HBase数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    @RequestMapping(value = "/hbase/ping", method = RequestMethod.GET)
    @ApiOperation(value = "测试连接HBase数据源")
    public void connectHBaseDataSource2(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId) {
        try {
            dataSourceManagerService.ping(dataSourceId);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接HBase数据源操作失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 新增hdfs数据源
     * @param inputs
     * @return
     */
    @RequestMapping(value = "/hdfs", method = RequestMethod.POST)
    @ApiOperation(value = "新增hdfs数据源")
    public Map saveHdfsDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HdfsDataSourceParam inputs){
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobName", EtlJobName.HDFS_DATASET_METADATA_ETL.name());
            jobParams.put("whEtlType", Constant.HDFS_DATA_SOURCE);
            String cronExpr = buildCronExpr(inputs.getDataSourceName());
            jobParams.put("cronExpr", cronExpr);
            jobParams.put("nextRun", getNextRun(cronExpr));
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("createTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));
           // jobParams.put("namenodeport",inputs.getHdfsdataSerport());

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HDFS_URL, inputs.getHdfsUrl());
            jobPropertyMap.put(Constant.HDFS_NAMENODEPORT, inputs.getHdfsdataSerport());
//            jobPropertyMap.put(Constant.HDFS_PASSWORD, inputs.getHdfsPassword());
            int sourceId = dataSourceManagerService.saveDataSource(jobParams, jobPropertyMap);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("sourceId", sourceId);
            return res;
        }catch (DuplicateKeyException dke){
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("新增Hdfs数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }

    }

    /**
     * 修改hdfs数据源
     */
    @RequestMapping(value = "/hdfs/{dataSourceId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改hdfs数据源")
    public void updateHdfsDataSource(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HdfsDataSourceParam inputs, @PathVariable("dataSourceId") int dataSourceId){
        try {
            Map<String, Object> jobParams = new HashMap<String, Object>();
            jobParams.put("whEtlJobId", dataSourceId);
            jobParams.put("dataSourceName", inputs.getDataSourceName());
            jobParams.put("lastUpdateTime", new SimpleDateFormat(Constant.TIME_FORMAT).format(new Timestamp(System.currentTimeMillis())));
 //           jobParams.put("NameNodePort",inputs.getHdfsdataSerport());

            Map<String, Object> jobPropertyMap = new HashMap<String, Object>();
            jobPropertyMap.put(Constant.HDFS_URL, inputs.getHdfsUrl());
            jobPropertyMap.put(Constant.HDFS_NAMENODEPORT, inputs.getHdfsdataSerport());
//            jobPropertyMap.put(Constant.HDFS_PASSWORD, inputs.getHdfsdataSerport());
            dataSourceManagerService.updateDataSource(dataSourceId, jobParams, jobPropertyMap);
        } catch (DuplicateKeyException dke) {
            throw new AppException("数据源名称已存在", ErrorCode.CUSTOM_EXCEPTION);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("修改hdfs数据源失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接Hdfs数据源
     */
    @RequestMapping(value = "/hdfs/ping", method = RequestMethod.GET)
    @ApiOperation(value = "通过数据源ID测试连接hdfs数据源")
    public void connectHdfsDataSource(@RequestParam @ApiParam(value = "数据源ID", required = true) int dataSourceId){
        try {
            //dataSourceManagerService.pingHdfs(dataSourceId);

            hdfsService.listStatus(dataSourceId,"/",false,1,-1);
            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("hdfs数据源连接失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    /**
     * 测试连接hdfs数据源
     */
    @RequestMapping(value = "/hdfs/ping", method = RequestMethod.POST)
    @ApiOperation(value = "测试连接hdfs数据源")
    public void connectHdfsDataSource2(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HdfsDataSourceParam inputs) {
        try {
            dataSourceManagerService.pingHdfs2(inputs.getHdfsUrl(),inputs.getHdfsdataSerport());

//            int dataSourceId = Integer.parseInt(inputs.getId());
//            dataSourceManagerService.updateJobActive(dataSourceId);
        } catch (Exception e) {
            e.printStackTrace();
            if (inputs.getId() != null && !inputs.getId().isEmpty()) {
                int dataSourceId = Integer.parseInt(inputs.getId());
                dataSourceManagerService.updateJobNotActive(dataSourceId, e.getMessage(),false);
            }

            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("测试连接hdfs数据源操作失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    private String buildCronExpr(String sourceName) {
        return String.format("* %d %d * * ?", Math.abs(sourceName.hashCode() % 60), Math.abs(sourceName.hashCode() % 2));
    }


    /***
     * 根据cron表达式生成下次触发的时间
     * @param cronExpr
     * @return timeMillis/1000
     */
    private long getNextRun(String cronExpr) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpr);
        Date next = cronSequenceGenerator.next(new Date());
        return next.getTime() / 1000;
    }

    @RequestMapping(value = "/owner", method = RequestMethod.POST)
    @ApiOperation(value = "数据源所属人信息查询")
    @ApiResponses({
            @ApiResponse(code = 500, message = "数据源所属人信息查询")
    })
    public DataSourceOwner getDataSourceOwner(
            @RequestBody @ApiParam(value = "数据源所属人查询参数", required = true) DataResource dataResource) {

        try {
            return structuredDataService.getDataSourceOwnerByResouce(dataResource);
        } catch (AppException ae) {
            //捕捉到业务异常直接返回null的DataSourceOwner
            return new DataSourceOwner();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get data source owner. {}", e);
//            if (e instanceof AppException) throw (AppException) e;
            throw new AppException("数据源所属人信息获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{dataSourceId}/owner", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据源所属人信息")
    @ApiResponses({
            @ApiResponse(code = 500, message = "新增数据源所属人信息")
    })
    public void addDataSourceOwner(
            @RequestBody @ApiParam(value = "数据源所属人信息参数", required = true) DataSourceOwner dataSourceOwner) {

        try {
            structuredDataService.addDataSourceOwner(dataSourceOwner);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to add data source owner. {}", e);
            throw new AppException("新增数据源所属人信息失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{dataSourceId}/owner", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据源所属人信息")
    @ApiResponses({
            @ApiResponse(code = 500, message = "修改数据源所属人信息")
    })
    public void updateDataSourceOwner(
            @RequestBody @ApiParam(value = "数据源所属人信息参数", required = true) DataSourceOwner dataSourceOwner) {

        try {
            structuredDataService.updateDataSourceOwner(dataSourceOwner);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to update data source owner. {}", e);
            throw new AppException("修改数据源所属人信息失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
