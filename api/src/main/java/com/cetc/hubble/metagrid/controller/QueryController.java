package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DatasetsService;
import com.cetc.hubble.metagrid.service.SqlService;
import com.cetc.hubble.metagrid.vo.QueryHistory;
import io.swagger.annotations.*;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/v2/api/queries/")
@Api(value = "/api", description = "SQL查询API")
public class QueryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DatasetsService datasetsService;
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "/sql", method = RequestMethod.POST)
    @ApiOperation(value = "SQL查询")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数格式不正确")
    })
    public QueryResult executeQuery(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true)QueryParam inputs) throws Exception{
        long start = System.currentTimeMillis();
        logger.info("query input {}", ReflectionToStringBuilder.toString(inputs));
        try{
            QueryResult res = sqlService.query(inputs);
            logger.info("Query Handle Success!Spend:"+(System.currentTimeMillis()-start)/1000.0+"s");
            return res;
        }catch (Exception e){
            throw new AppException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/histories", method = RequestMethod.GET)
    @ApiOperation(value = "SQL查询历史")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数格式不正确")
    })
    public List<QueryHistory> queryHistory(@RequestParam(required = false) @ApiParam(value = "数据源ID", required = true)int sourceId,@RequestParam(required = false) @ApiParam(value = "数据库", required = false)String db,@RequestParam(required = false,defaultValue = "10") @ApiParam(value = "历史条数", required = false)int limit) {
        logger.info("query input {} {} {}",sourceId, db,limit);

        try {
            List results = sqlService.listSqlHistories(sourceId,db,limit);
            return results;
        } catch (Exception e) {
            logger.error("获取SQL查询历史出错：", e);
            throw new AppException("获取SQL查询历史出错："+e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/histories", method = RequestMethod.POST)
    @ApiOperation(value = "保存SQL查询历史")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数格式不正确"),
            @ApiResponse(code = 500, message = "保存出现异常")
    })
    public void queryHistory(@RequestBody @ApiParam(value = "SQL", required = true)QueryHistory history) {
        logger.info("query input {}",history);
        try {
            sqlService.saveSql(history);
        } catch (Exception e) {
            logger.error("保存SQL查询出错：", e);
            throw new AppException("获取SQL查询出错："+e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ApiOperation(value = "保存SQL查询历史")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数格式不正确"),
            @ApiResponse(code = 500, message = "保存出现异常")
    })
    public void executeUpdate(@ApiParam @RequestBody Map param) {
        logger.info("exe update {} {}", param.get("sourceId"), param.get("sql"));
        try {
            sqlService.executeUpdate(Integer.parseInt((String)param.get("sourceId")), (List<String>)param.get("sql"));
        } catch (Exception e) {
            logger.error("执行SQL出错：", e);
            throw new AppException("执行SQL出错：" + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}


