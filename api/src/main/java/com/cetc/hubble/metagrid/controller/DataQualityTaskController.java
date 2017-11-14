package com.cetc.hubble.metagrid.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cyq on 2017/9/26.
 * 第二版数据质量任务api
 */
@RestController
@RequestMapping(value = "/v2/api/dataQuality/task")
@Api(value = "/api", description = "数据质量任务API列表")
public class DataQualityTaskController {

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量任务列表")
    public void getTaskList(){

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据质量任务详情")
    public void getTaskInfo(){

    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "创建数据质量任务")
    public void createTask(){

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据质量任务")
    public void updateTask(){

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据质量任务")
    public void deleteTask(){

    }


}
