package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.SchemaHistoryService;
import com.cetc.hubble.metagrid.vo.StageLog;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/v2/api/schemaHistory/")
@Api(value = "/api", description = "更新历史API列表")
public class SchemaHistoryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SchemaHistoryService schemaHistoryService;


    @RequestMapping(value = "/stageDates", method = RequestMethod.GET)
    @ApiOperation(value = "获取同步的时间和状态列表")
    @ApiResponses({
            @ApiResponse(code = 500, message = "获取同步的时间和状态信息失败")
    })
    public List<Map> getUpdateDates(@RequestParam @ApiParam(value = "输入参数，默认条数",defaultValue = "10",required = false)Integer limit) {
        try {
            return schemaHistoryService.getUpdateDates(limit);
        } catch (Exception e) {
            logger.error("获取更新日期出错：", e);
            throw new AppException("获取更新日期错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/stageLog", method = RequestMethod.GET)
    @ApiOperation(value = "根据日期获取更新历史")
    @ApiResponses({
            @ApiResponse(code = 500, message = "获取更新历史失败")
    })
    public List<StageLog> getUpdateLogByDate(@RequestParam @ApiParam(value = "输入参数，搜索日期",defaultValue = "2016/12/20",required = true)String date) {

        try {
            return schemaHistoryService.getUpdateLogByDate(date);
        } catch (Exception e) {
            logger.error("获取更新日志出错：", e);
            throw new AppException("获取更新日志错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }


    @RequestMapping(value = "/datasetDiff", method = RequestMethod.GET)
    @ApiOperation(value = "根据stageLogID获取两次同步的字段信息")
    @ApiResponses({
            @ApiResponse(code = 500, message = "获取字段信息失败")
    })
    public Map<String,Object> getDatasetDiff(@RequestParam @ApiParam(value = "输入参数，stageLogID",defaultValue = "11",required = true)Long stageLogId) {
        try {
            return schemaHistoryService.getDatasetDiff(stageLogId);
        } catch (Exception e) {
            logger.error("获取数据集更新信息出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据集更新信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}


