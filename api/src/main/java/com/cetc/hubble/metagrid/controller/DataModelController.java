package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.service.DataModelService;
import com.cetc.hubble.metagrid.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/v2/api/dataModel/")
@Api(value = "/api", description = "数据建模API列表")
public class DataModelController extends BaseController{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataModelService dataModelService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "保存数据模型")
    public void save(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true)FrontModel frontModel) {
//        dataModelService.save(frontModel,getUserName());
    }

    @RequestMapping(value = "/{dataModelId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据模型")
    public FrontModel  getModel(@PathVariable @ApiParam(value = "数据模型ID", required = true) Integer dataModelId) {
        return dataModelService.get(dataModelId);
    }

    @RequestMapping(value = "/{dataModelId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ID删除数据模型")
    public void  deleteModel(@PathVariable @ApiParam(value = "数据模型ID", required = true) Integer dataModelId) {
        dataModelService.delete(dataModelId);
    }

    @RequestMapping(value = "/{dataModelId}", method = RequestMethod.PUT)
    @ApiOperation(value = "根据ID更新数据模型")
    public void  updateModel(@PathVariable @ApiParam(value = "数据模型ID", required = true) Integer dataModelId,@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) FrontModel model) {
        dataModelService.update(dataModelId,model);
    }
}


