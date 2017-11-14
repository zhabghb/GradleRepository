package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.TreeService;
import com.cetc.hubble.metagrid.vo.TagParam;
import com.cetc.hubble.metagrid.vo.TreeNodes;
import com.cetc.hubble.metagrid.vo.TreeParam;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping(value = "/v2/api/tree/")
@Api(value = "/api", description = "目录树API列表")
public class TreeController extends  BaseController{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TreeService treeService;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "获取目录树数据源")
    @ApiResponses({
            @ApiResponse(code = 500, message = "获取目录树失败")
    })
    public Map getSources() {

        logger.info("Getting the roots of structured source tree.");
        Map res;
        try {
            res = treeService.getDataSourceStatistics();
        } catch (Exception e) {
            logger.error("Failed to get the roots of structured source tree.", e);
            throw new AppException("查询数据源信息失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return res;
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取标签")
    @ApiResponses({
            @ApiResponse(code = 500, message = "获取标签失败")
    })
    public Map getTags() {

        logger.info("Getting the root level of structured tag tree.");
        Map res;
        try {
            res = treeService.getTags();
        } catch (Exception e) {
            logger.error("Failed to get the roots of structured tag tree.", e);
            throw new AppException("获取目录树失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    @ApiOperation(value = "根据标签获取子节点")
    @ApiResponses({
            @ApiResponse(code = 500, message = "根据标签获取子节点失败")
    })
    public TreeNodes getDatasetsByTag(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) TagParam param) {

        logger.info("Getting the sub nodes of tag: " + param.getTagId());
        TreeNodes treeNodes;
        try {
            treeNodes = treeService.getDatasetsByTag(param);
        } catch (Exception e) {
            logger.error("Failed to get the sub nodes of tag: " + param.getTagId(), e);
            throw new AppException("获取目录树失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return treeNodes;
    }

    @RequestMapping(value = "/{sourceId}", method = RequestMethod.POST)
    @ApiOperation(value = "按资源ID获取子节点")
    @ApiResponses({
            @ApiResponse(code = 500, message = "根据资源ID获取子节点失败")
    })
    public TreeNodes getSubNodes(@PathVariable @ApiParam(value = "资源ID", required = true) Integer sourceId,
                                 @RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) TreeParam param) {

        logger.info("Get the sub nodes of data source: " + param.getSourceId());
        TreeNodes treeNodes;
        try {
            treeNodes = treeService.getSubNodes(param);
        } catch (Exception e) {
            logger.error("Failed to get the sub nodes of node: " + param.getSourceId(), e);
            throw new AppException("查询数据源信息失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return treeNodes;
    }



    @RequestMapping(value = "/{sourceId}/fresh", method = RequestMethod.GET)
    @ApiOperation(value = "手动同步元数据")
    public void syncMetadata(@PathVariable @ApiParam int sourceId) throws Exception {
        logger.info("the front:{} is trying to fresh etl job id: {} ,at :{} " ,new Object[]{request.getRemoteAddr(),sourceId,new Date()});
        try {
            treeService.syncMetadata(sourceId,getUserName());
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw  e;
            throw new AppException("同步失败!", ErrorCode.CUSTOM_EXCEPTION);
        }

    }

    @RequestMapping(value = "/{sourceId}/status", method = RequestMethod.GET)
    @ApiOperation(value = "查看元数据最近一次的同步状态")
    public Map jobStatus(@PathVariable int sourceId) throws Exception {
        logger.info("the front :{} is querying etl job id: {} at:{} " ,new Object[]{request.getRemoteAddr(),sourceId,new Date()});
        return treeService.getSourceStatus(sourceId);
    }


    @RequestMapping(value = "/{sourceId}/notify", method = RequestMethod.GET)
    @ApiOperation(value = "手动同步元数据后backend-service主动调用")
    public void notify(@PathVariable @ApiParam int sourceId, @RequestParam @ApiParam(value = "资源ID", required = true) Boolean success) {
        logger.info("backend-service:{} has finished etl job id: {} ,and the status is :{},at :{}" ,new Object[]{request.getRemoteAddr(),sourceId,success,new Date()});
        treeService.sendMessage(sourceId, success);
    }

    @RequestMapping(value = "/{sourceId}/notifyRunning", method = RequestMethod.GET)
    @ApiOperation(value = "手动同步元数据后backend-service主动调用")
    public void notify(@PathVariable @ApiParam int sourceId, @RequestParam @ApiParam(value = "资源ID", required = true) int running) {
        logger.info("backend-service:{} has acknowledged etl job id: {} ,and the running status is :{} ,at :{}" ,new Object[]{request.getRemoteAddr(),sourceId,running,new Date()});
        treeService.ackRunning(sourceId, running);
    }

}
