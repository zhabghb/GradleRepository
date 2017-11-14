package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.RelationshipService;
import com.cetc.hubble.metagrid.vo.DataModelJson;
import com.cetc.hubble.metagrid.vo.DataModelRelationType;
import com.cetc.hubble.metagrid.vo.DataModelStatus;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tao on 13/12/16.
 */
@RestController
@RequestMapping(value = "/v2/api/relationship")
@Api(value = "/api", description = "数据建模表关系API列表")
public class RelationshipController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(RelationshipController.class);
    @Autowired
    private RelationshipService rsService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "关联关系类型列表")
    @ApiResponses({
            @ApiResponse(code = 500, message = "关联关系列表获取失败")
    })
    public List<DataModelRelationType> getRelationTypeLs() {

        logger.info("List all relation types");
        List res;
        try {
            res = rsService.getRelationTypeLs();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get relation type list.", e);
            throw new AppException("关联关系列表获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "保存关联关系")
    @ApiResponses({
            @ApiResponse(code = 500, message = "关联关系保存失败")
    })
    public Map save(@RequestBody @ApiParam(value = "关联关系", required = true) DataModelJson json) {

        logger.info("Save a model json.");
        try {
            int modelId = rsService.save(json, getUserName());
            HashMap<String, Object> map = Maps.newHashMap();
            map.put("modelId",modelId);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to save relation json.", e);
            throw new AppException("关联关系保存失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{modelId}/update", method = RequestMethod.PUT)
    @ApiOperation(value = "修改关联关系")
    @ApiResponses({
            @ApiResponse(code = 500, message = "关联关系保存失败")
    })
    public Map update(@RequestBody @ApiParam(value = "关联关系", required = true) DataModelJson json) {

        logger.info("update a model json.");
        try {
            int modelId = rsService.update(json, getUserName());
            HashMap<String, Object> map = Maps.newHashMap();
            map.put("modelId",modelId);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to update relation json.", e);
            throw new AppException("更新关联关系失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/modelList", method = RequestMethod.GET)
    @ApiOperation(value = "查询模型列表")
    @ApiResponses({
            @ApiResponse(code = 500, message = "模型列表查询失败")
    })
    public List<DataModelStatus> getDMStatusLs() {

        logger.info("Query model json list.");
        List<DataModelStatus> statusLs;
        try {
            statusLs = rsService.getDMStatusLs();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to query relation json list.", e);
            throw new AppException("关联关系查询失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return statusLs;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "查询关联关系")
    @ApiResponses({
            @ApiResponse(code = 500, message = "关联关系查询失败")
    })
    public DataModelJson query(@PathVariable @ApiParam(value = "关联关系ID", required = true) Integer id) {

        logger.info("Query model json.");
        DataModelJson json;
        try {
            json = rsService.query(id);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to query relation json.", e);
            throw new AppException("关联关系查询失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return json;
    }
}
