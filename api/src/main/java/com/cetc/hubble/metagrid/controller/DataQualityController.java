package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DataQualityService;
import com.cetc.hubble.metagrid.vo.DataQuality;
import com.cetc.hubble.metagrid.vo.DataQualityRule;
import com.cetc.hubble.metagrid.vo.DataQualityTopn;
import com.cetc.hubble.metagrid.vo.DqAnalyseParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/2/13.
 */
@RestController
@RequestMapping(value = "/v2/api/dataQuality/")
@Api(value = "/api", description = "数据质量API列表")
public class DataQualityController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataQualityService dataQualityService;

/*    @RequestMapping(value = "/{fieldId}/features", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量特征信息")
    public DataQuality dataQualityFeatures(@PathVariable @ApiParam(value = "字段ID", required = true) Long fieldId){
        try {
            return dataQualityService.getDataQualityFeatures(fieldId);
        } catch (Exception e) {
            logger.error("获取数据质量特征信息出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据质量特征信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }*/
    @RequestMapping(value = "/{fieldId}/features", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量特征信息")
    public DataQuality dataQualityFeatures(@PathVariable @ApiParam(value = "字段ID", required = true) Long fieldId){
        try {
            return dataQualityService.getDataQualityFeatures(fieldId);
        } catch (Exception e) {
            logger.error("获取数据质量特征信息出错：", e);
            if (e instanceof AppException) throw (AppException) e;
            e.printStackTrace();
            throw new AppException("获取数据质量特征信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{fieldId}/topn", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量TopN信息")
    public List<DataQualityTopn> dataQualityTopn(@PathVariable @ApiParam(value = "字段ID", required = true) Long fieldId,@RequestParam @ApiParam(value = "输入参数，TopN",defaultValue = "10",required = true) int topn){
        try {
            return dataQualityService.getDataQualityTopn(fieldId,topn);
        } catch (Exception e) {
            logger.error("获取数据质量TopN信息出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据质量TopN信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{fieldName}/rules", method = RequestMethod.GET)
    @ApiOperation(value = "获取列表")
    public List<DataQualityRule> dataQualityRules(@PathVariable @ApiParam(value = "字段名称", required = true) String fieldName){
        try {
            return dataQualityService.getDataQualityRule(fieldName);
        } catch (Exception e) {
            logger.error("获取规则出错：", e);
            e.printStackTrace();
            throw new AppException("获取规则错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    @ApiOperation(value = "获取规则列表")
    public List<DataQualityRule> dataQualityRules(){
        try {
            return dataQualityService.getDataQualityRules();
        } catch (Exception e) {
            logger.error("获取规则列表出错：", e);
            e.printStackTrace();
            throw new AppException("获取规则列表错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{identifier}/dqanalyse", method = RequestMethod.POST)
    @ApiOperation(value = "保存数据质量分析任务")
    public void dataQualityAnalyse(@PathVariable @ApiParam(value = "字段名称", required = true) String identifier,@RequestBody @ApiParam(value = "输入参数，JSON格式",required = true) List<DqAnalyseParam> inputs){
        logger.info("准备保存数据质量分析任务数量:{}",inputs.size());
        try {
            dataQualityService.deleteOldRecords(inputs,identifier);
            for (DqAnalyseParam input:inputs) {
                dataQualityService.saveQualityAnalyseJobs(input,identifier);
            }
        } catch (Exception e) {
            logger.error("保存数据质量分析任务出错：", e);
            e.printStackTrace();
            throw new AppException("保存数据质量分析任务错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{fieldId}/dqanalyse", method = RequestMethod.GET)
    @ApiOperation(value = "手动检测字段数据质量")
    public void dataQualityManualAnalyse(@PathVariable @ApiParam(value = "字段ID", required = true) Long fieldId){
        logger.info("手动检测字段数据质量 fieldId:{}",fieldId);
        try {
            DqAnalyseParam input = dataQualityService.getDueTaskByFieldId(fieldId);
            dataQualityService.startQualityAnalyse(input);
        } catch (Exception e) {
            logger.error("手动检测字段数据质量出错：", e);
            e.printStackTrace();
            throw new AppException("手动检测字段数据质量错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{fieldId}/status", method = RequestMethod.GET)
    @ApiOperation(value = "查询字段数据质量状态")
    public Map dataQualityStatus(@PathVariable @ApiParam(value = "字段ID", required = true) Long fieldId){
        logger.info("查询字段数据质量状态,fieldId:{}",fieldId);
        try {
            return dataQualityService.getDataQualityStatus(fieldId);
        } catch (Exception e) {
            logger.error("查询字段数据质量状态出错：", e);
            e.printStackTrace();
            throw new AppException("查询字段数据质量状态错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
