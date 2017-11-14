package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DatasetsService;
import com.cetc.hubble.metagrid.service.SqlService;
import com.cetc.hubble.metagrid.vo.ExportParam;
import com.cetc.hubble.metagrid.vo.ExportResult;
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
@RequestMapping(value = "/v2/api/export/")
@Api(value = "/api", description = "导出数据")
public class ExportController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/export", method = RequestMethod.POST)
    @ApiOperation(value = "SQL查询")
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数格式不正确"),
            @ApiResponse(code = 200, message = "成功")
    })
    public ExportResult export(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true)ExportParam inputs) throws Exception{
        ExportResult result = new ExportResult();
        return result;
    }

    @RequestMapping(value = "/relase", method = RequestMethod.GET)
    @ApiOperation(value = "SQL查询")
    @ApiResponses({
            @ApiResponse(code = 400, message = "没有找到相应的下载文件，或者文件已经删除"),
            @ApiResponse(code = 500, message = "程序处理出错"),
            @ApiResponse(code = 200, message = "成功")
    })
    public void relase(@ApiParam(value = "输入参数，JSON格式", required = true)String path) throws Exception{
    }
}


