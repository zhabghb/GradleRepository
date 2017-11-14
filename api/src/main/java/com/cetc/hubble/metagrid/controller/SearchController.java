package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DatasetsService;
import com.cetc.hubble.metagrid.vo.SearchParam;
import com.cetc.hubble.metagrid.vo.SearchTagParam;
import com.cetc.hubble.metagrid.vo.TreeNode;
import com.cetc.hubble.metagrid.vo.TreeNodes;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/v2/api/search/")
@Api(value = "/api", description = "搜索接口")
public class SearchController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DatasetsService datasetsService;

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    @ApiOperation(value = "根据标签搜索")
    @ApiResponses({
            @ApiResponse(code = 500, message = "搜索失败")
    })
    public TreeNodes searchByTagName(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) SearchTagParam param) {

        TreeNodes treeNodes = new TreeNodes();

        try {
            List<TreeNode> nodes = datasetsService.searchByTagName(param);
            treeNodes.setNodes(nodes);
        } catch (Exception e) {
            logger.error("搜索出错：", e);
            throw new AppException("搜索错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return treeNodes;
    }
    @RequestMapping(value = "/es", method = RequestMethod.POST)
    @ApiOperation(value = "根据表名、字段名、备注进行全文检索")
    @ApiResponses({
            @ApiResponse(code = 500, message = "搜索失败")
    })
    public TreeNodes searchES(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) SearchParam param) {
        TreeNodes treeNodes = new TreeNodes();

        try {
            if (param.getKeyword() != null && (param.getKeyword().contains("'") || param.getKeyword().contains("%")||param.getKeyword().contains("\\"))) {
                param.setKeyword(param.getKeyword().replaceAll("'", "''").replaceAll("%", "\\\\%").replaceAll("\\\\", "\\\\\\\\"));
            }
            List<TreeNode> nodes = datasetsService.useElasticSearch(param);
            treeNodes.setNodes(nodes);
        } catch (Exception e) {
            logger.error("搜索出错：", e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw new AppException("搜索错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return treeNodes;
    }
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "根据表名、字段名、备注在数据库中搜索")
    @ApiResponses({
            @ApiResponse(code = 500, message = "搜索失败")
    })
    public TreeNodes searchDB(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) SearchParam param) {
        TreeNodes treeNodes = new TreeNodes();

        try {
            if (param.getKeyword() != null && (param.getKeyword().contains("'") || param.getKeyword().contains("%")|| param.getKeyword().contains("\\")|| param.getKeyword().contains("\\\\")|| param.getKeyword().contains("_"))) {
                param.setKeyword(param.getKeyword().replace("\\","\\\\\\\\").replace("_","\\_").replaceAll("'", "''").replaceAll("%", "\\\\%"));
            }
            List<TreeNode> nodes = datasetsService.searchByKeyword(param);
            treeNodes.setNodes(nodes);
        } catch (Exception e) {
            logger.error("搜索出错：", e);
            if (e instanceof AppException)
                throw (AppException) e;
            throw new AppException("搜索错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return treeNodes;
    }

}


