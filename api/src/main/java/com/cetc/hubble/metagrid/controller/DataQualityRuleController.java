package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.service.DataQualityRuleService;
import com.cetc.hubble.metagrid.vo.DataQualityRule_V2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cyq on 2017/9/26.
 * 第二版数据质量规则api
 */
@RestController
@RequestMapping(value = "/v2/api/dataQuality/rule")
@Api(value = "/api", description = "数据质量规则API列表")
public class DataQualityRuleController {


    @Autowired
    private DataQualityRuleService dataQualityRuleService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量规则列表")
    public List<DataQualityRule_V2> getRuleList(){
        return dataQualityRuleService.getRuleList();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量规则详情")
    public List<DataQualityRule_V2> getRuleInfo(@PathVariable @ApiParam(value = "规则ID", required = true) Integer id){
        return dataQualityRuleService.getRuleInfo(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "创建数据质量规则")
    public void createRule(){

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据质量规则")
    public Integer updateRule(@PathVariable @ApiParam(value = "规则ID", required = true) Integer id){
       return dataQualityRuleService.updateRule(id,null);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据质量规则")
    public Integer deleteRule(@PathVariable @ApiParam(value = "规则ID", required = true) Integer id){
        return dataQualityRuleService.deleteRule(id);
    }

    @RequestMapping(value = "/jar/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传数据质量规则jar包")
    public void uploadJar(){

    }

    @RequestMapping(value = "/jar/configuration/upload", method = RequestMethod.POST)
    @ApiOperation(value = "上传数据质量规则jar包配置文件")
    public void uploadJarConfig(){

    }

}
