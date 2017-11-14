package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DataSource;
import com.cetc.hubble.metagrid.vo.DataSourceProperty;
import com.fasterxml.jackson.databind.JsonNode;
import metadata.etl.EtlJob;
import metadata.etl.models.EtlJobFactory;
import metadata.etl.models.EtlJobName;
import metagrid.common.Constant;
import metagrid.common.utils.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by sunfeng on 10/13/2016.
 */
@Service
public class DataSourceManagerService {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;

    /**
     * 新增数据源
     *
     * @param jobParams
     * @param jobPropertyMap
     * @return sourceId
     */
    public synchronized int saveDataSource(Map<String, Object> jobParams, Map<String, Object> jobPropertyMap) {

        // 查重
        if (dataSourceManagerDAO.isDataSourceDuplicate(jobParams, jobPropertyMap)) {
            throw new AppException("此数据源已存在,请勿重复录入!", ErrorCode.BAD_REQUEST);
        }
        return dataSourceManagerDAO.saveDataSource(jobParams, jobPropertyMap);
    }

    /**
     * 修改数据源
     *
     * @param jobParams
     * @param jobPropertyMap
     */
    public void updateDataSource(int dataSourceID, Map<String, Object> jobParams, Map<String, Object> jobPropertyMap) {

        boolean running = dataSourceManagerDAO.queryWhetherJobRunning(dataSourceID);
        if (running) {
            throw new AppException("该数据源正在同步，暂时不能修改！", ErrorCode.BAD_REQUEST);
        }
        // 查重
        if (dataSourceManagerDAO.isDataSourceDuplicate(jobParams, jobPropertyMap)) {
            throw new AppException("此数据源已存在,请勿重复录入!", ErrorCode.BAD_REQUEST);
        }
        dataSourceManagerDAO.updateDataSource(jobParams, jobPropertyMap);
    }

    /**
     * 删除数据源
     *
     * @param dataSourceId
     */
    public Integer deleteDataSource(int dataSourceId) {

        boolean running = dataSourceManagerDAO.queryWhetherJobRunning(dataSourceId);
        if (running) {
            throw new AppException("该数据源正在同步，暂时不能删除！", ErrorCode.BAD_REQUEST);
        }
        return dataSourceManagerDAO.deleteDataSource(dataSourceId);
    }

    /**
     * 返回所有的数据源分类及数据源信息
     *
     * @return
     */
    public List<Map<String, Object>> getAllDataSourceInfo(boolean withOwner) {
        return dataSourceManagerDAO.getAllDataSourceInfo(withOwner);
    }

    /**
     * 根据主键获得对象
     *
     * @param id
     * @return
     */
    public DataSource getEtlJobById(long id) {
        return dataSourceManagerDAO.getEtlJobById(id);
    }

    /**
     * 通过数据源ID去测试数据源是否连通
     *
     * @param id
     * @throws Exception
     */
    public void ping(int id) throws Exception {
        DataSource dataSource = dataSourceManagerDAO.getEtlJobById(id);
        EtlJobName etlJobName = EtlJobName.valueOf(dataSource.getEtlJobName());
        Properties prop = new Properties();
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            prop.setProperty(p.getPropertyName(), p.getPropertyValue());
        }
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, id, null, prop);
        etlJob.ping();
    }

    /**
     * 通过数据源属性去测试数据源是否连通
     *
     * @param prop
     * @param etlJobName
     * @throws Exception
     */
    public void ping(Properties prop, EtlJobName etlJobName) throws Exception {
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, null, null, prop);
        etlJob.ping();
    }


    public void pingHdfs(int dataSourceId){
        HashMap<String, Object> map= dataSourceManagerDAO.getPropertyMapByJobId(dataSourceId);
        String url=map.get("hdfsIP").toString();
        int port=Integer.parseInt(map.get("hdfsPort").toString());
        try{
            Socket socket=new Socket(url, port);
        }catch (Exception e){
            e.printStackTrace();
            throw new AppException("hdfs数据源连接失败", ErrorCode.CUSTOM_EXCEPTION);
        }


    }
    public void pingHdfs2(String url,String Serport){

        String path = "/";

        try{

            String hdfsurl =  url +"/webhdfs/v1" + path + "?op=" + Constant.WEBHDFS_OPERATION_LISTSTATUS;
            String result = HttpUtil.doGet(hdfsurl);
            if (result == null) {
                throw new AppException("无法连接到该HDFS!", ErrorCode.NOT_FOUND);
            }

            JsonNode jsonNodeFileStatuses = Json.parse(result).get("FileStatuses");
            if (jsonNodeFileStatuses == null) {
                throw new AppException("无法获取文件状态!", ErrorCode.BAD_REQUEST);
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new AppException("hdfs数据源连接失败", ErrorCode.CUSTOM_EXCEPTION);
        }
    }

    public void updateJobActive(int dataSourceId) {
        dataSourceManagerDAO.updateJobActive(dataSourceId);
    }

    public void updateJobNotActive(int dataSourceId, String comments,boolean updateComments) {
        dataSourceManagerDAO.updateJobNotActive(dataSourceId, comments,updateComments);
    }

    /**
     * 判断数据源下是否有表关联了主题
     * @param dataSourceId
     * @return
     */
    public Boolean checkTableInDomain(int dataSourceId){
        long count = dataSourceManagerDAO.getTableDomainCount((long)dataSourceId);
        if (count>0){
            return true;
        }
        return false;
    }


}
