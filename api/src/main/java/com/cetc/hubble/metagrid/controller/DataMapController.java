package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.pagination.CommonUtil;
import com.cetc.hubble.metagrid.service.DataMapService;
import com.cetc.hubble.metagrid.vo.DataMap;
import com.cetc.hubble.metagrid.vo.DataResourcePlus;
import com.cetc.hubble.metagrid.vo.StdColumn;
import com.cetc.hubble.metagrid.vo.StdTable;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dahey on 2017/3/10.
 */
@RestController
@RequestMapping(value = "/v2/api/dataMap/")
@Api(value = "/api", description = "元数据地图API列表")
public class DataMapController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${data.file.upload.path}")
    public String uploadPath;

    @Value("${data.file.download.path}")
    public String downloadPath;

    public static final Integer COLUMN_NUMBER = 5;

    public static final String  COLUMN_SEPARATOR = "####";

    public static final String  COLUMN_FIELD_SEPARATOR = "&&&";

    public static final Integer COLUMN_FIELD_NUMBER = 3;

    @Autowired
    private DataMapService dataMapService;

    @RequestMapping(value = "/coverage", method = RequestMethod.GET)
    @ApiOperation(value = "获取元数据地图覆盖率信息")
    public Map dataMapCoverage(){
        try {
            return dataMapService.getDataMapCoverage();
        } catch (Exception e) {
            logger.error("获取元数据地图覆盖率信息出错：", e);
            e.printStackTrace();
            throw new AppException("获取元数据地图覆盖率信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/tagstatis", method = RequestMethod.GET)
    @ApiOperation(value = "获取元数据地图分类统计信息")
    public List<Map<String, Object>> dataMapTagStatis(){
        try {
            return dataMapService.getDataMapTagStatis();
        } catch (Exception e) {
            logger.error("获取元数据地图分类统计信息出错：", e);
            e.printStackTrace();
            throw new AppException("获取元数据地图分类统计信息错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @ApiOperation(value = "获取元数据地图数据")
    public ArrayList<DataMap> dataMapTree(){
        try {
            return dataMapService.getDataMapTree();
        } catch (Exception e) {
            logger.error("获取元数据地图数据出错：", e);
            e.printStackTrace();
            throw new AppException("获取元数据地图数据错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{stdTableId}/fields", method = RequestMethod.GET)
    @ApiOperation(value = "元数据标准表字段信息获取")
    @ApiResponses({@ApiResponse(code = 500, message = "元数据标准表字段信息获取失败")})
    public List<StdColumn> getColumnInfo(
            @PathVariable @ApiParam(value = "元数据标准表ID", required = true) Integer stdTableId) {

        logger.info("stdTable information get: ", stdTableId);
        try {
            return  dataMapService.getFields(stdTableId);
        } catch (Exception e) {
            logger.error("Failed to get column information." + stdTableId, e);
            throw new AppException("元数据标准表字段信息获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{dataSetId}/match", method = RequestMethod.PUT)
    @ApiOperation(value = "匹配元数据标准表")
    @ApiResponses({@ApiResponse(code = 500, message = "匹配元数据标准表失败")})
    public void matchDatasetAndStdTable(
            @RequestBody @ApiParam(value = "元数据标准表ID", required = true) Map ids) {

        logger.info("ids : ", ids);
        try {
           dataMapService.matchDatasetAndStdTable(Long.parseLong(String.valueOf(ids.get("dataSetId"))),(Integer)ids.get("stdTableId"));
        } catch (Exception e) {
            logger.error("Failed to update dataset information.");
            e.printStackTrace();
            throw new AppException("匹配元数据标准表失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{stdTableId}/unmatch", method = RequestMethod.GET)
    @ApiOperation(value = "取消匹配元数据标准")
    @ApiResponses({@ApiResponse(code = 500, message = "取消匹配元数据标准表失败")})
    public void unMatchDatasetAndStdTable(
            @PathVariable @ApiParam(value = "元数据标准表ID", required = true) Integer stdTableId) {

        logger.info("ids : ", stdTableId);
        try {
            dataMapService.unMatchDatasetAndStdTable(stdTableId);
        } catch (Exception e) {
            logger.error("Failed to update dataset information.");
            e.printStackTrace();
            throw new AppException("取消匹配元数据标准表失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/targetTables", method = RequestMethod.POST)
    @ApiOperation(value = "元数据地图活跃度")
    @ApiResponses({@ApiResponse(code = 500, message = "获取元数据地图活跃度失败")})
    public List<DataResourcePlus> getgetTargetTables(
            @RequestBody List<DataResourcePlus> dataResourcePlusList) {

        logger.info("dataResourcePlusList count : {}", dataResourcePlusList.size());
        try {
            return dataMapService.fillByResouce(dataResourcePlusList);
        } catch (Exception e) {
            logger.error("Failed to get target tables information.");
            e.printStackTrace();
            throw new AppException("获取元数据地图活跃度失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ApiOperation(value = "数据体标准csv文件导入")
    @ApiResponses({
            @ApiResponse(code = 400, message = "数据体标准csv文件模板格式校验失败"),
            @ApiResponse(code = 409, message = "上传的数据体标准内容已经存在"),
            @ApiResponse(code = 500, message = "数据体标准csv文件批量导入失败")})
    public void upload(@ApiParam(name="file",value="待上传文件", required = true) MultipartFile file) {
        try {
            File dest = CommonUtil.uploadCSVFile(uploadPath, file, "std_table");
            List<String> lines = IOUtils.readLines(new FileInputStream(dest), "UTF-8");
            if(lines == null || lines.size() <= 1) {
                throw new AppException("数据体标准模板不匹配", ErrorCode.BAD_REQUEST);
            }
            //读取文件内容并检验文件内容格式
            Map<String, String> tabTagMap = new HashMap<String, String>();
            Map<String, StdTable> tabMap = new HashMap<String, StdTable>();
            Map<String, List<StdColumn>> tabColumnMap = new HashMap<String, List<StdColumn>>();

            String str = "";
            int countLine = 0;
            StdTable stdTable = null;
            for(int i = 1; i < lines.size(); i++) {
                str = lines.get(i);
                if(!Strings.isNullOrEmpty(str)) {
                    String[] arr = str.split(",", COLUMN_NUMBER);
                    if(arr.length != COLUMN_NUMBER) {
                        throw new AppException("数据体标准模板格式不匹配", ErrorCode.BAD_REQUEST);
                    }
                    //如果std table code是空，直接返回错误
                    if(Strings.isNullOrEmpty(arr[0])) {
                        throw new AppException("数据体标准code为空", ErrorCode.BAD_REQUEST);
                    }
                    //解析tag到内存
                    tabTagMap.put(arr[0], arr[3]);
                    //解析table到内存
                    stdTable = new StdTable();
                    stdTable.setCode(arr[0]);
                    stdTable.setComment(arr[1]);
                    stdTable.setDataVolume(Integer.parseInt(arr[2]));
                    tabMap.put(arr[0], stdTable);
                    //解析列的内容到内存
                    if(!Strings.isNullOrEmpty(arr[4])) {
                        int columnCount = 0;
                        Map<String, String> columnCodeMap = new HashMap<String, String>();
                        List<StdColumn> stdColumnList = new ArrayList<StdColumn>();
                        StdColumn stdColumn = null;
                        String[] columnArrs = arr[4].trim().split(COLUMN_SEPARATOR);
                        //解析列中的内容到列对应的list
                        for(String columns : columnArrs) {
                            if(!Strings.isNullOrEmpty(columns)) {
                                String[] arrs = columns.split(COLUMN_FIELD_SEPARATOR, COLUMN_FIELD_NUMBER);
                                if(Strings.isNullOrEmpty(arrs[0])) {
                                    throw new AppException("上传的数据体列字段code为空", ErrorCode.BAD_REQUEST);
                                }
                                if(arrs.length != COLUMN_FIELD_NUMBER) {
                                    throw new AppException("上传的数据体列字段列数不匹配", ErrorCode.BAD_REQUEST);
                                }
                                columnCodeMap.put(arrs[0], arrs[0]);

                                stdColumn = new StdColumn();
                                stdColumn.setCode(arrs[0]);
                                stdColumn.setDataType(arrs[1]);
                                stdColumn.setComment(arrs[2]);

                                stdColumnList.add(stdColumn);
                                columnCount++;
                            }
                        }
                        //判断列中内容是否出现重复
                        if(columnCodeMap.size() != columnCount) {
                            throw new AppException("上传的数据体标准列字段code出现重复", ErrorCode.BAD_REQUEST);
                        }

                        tabColumnMap.put(arr[0], stdColumnList);
                    }

                    countLine ++;
                }
            }
            //logger.info("文件内容 stringMap：" + stringMap);
            //检验文件内容是否在关键内容中出现重复
            if(tabMap.size() != countLine) {
                throw new AppException("上传的数据体标准内容出现重复,请检查文件内容", ErrorCode.BAD_REQUEST);
            }


            //检查数据库中是否已经存在文件中的数据体标准内容
            Set<String> existSet = new HashSet<String>();
            for(String line : tabMap.keySet()) {
                if(dataMapService.checkStdTableExistByCode(line)) {
                    existSet.add(line);
                }
            }
            if(existSet.size() > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("以下数据体标准已存在：");
                for(String ic : existSet) {
                    sb.append(ic).append("  ");
                }
                throw new AppException(sb.toString(), ErrorCode.CONFLICT);
            }

            //插入数据到数据库
            dataMapService.saveStdTableAndTags(tabMap, tabTagMap, tabColumnMap);

        } catch (IOException e) {
            logger.error("数据体标准上传文件出错：" + e.getMessage());
            throw new AppException("数据体标准上传文件出错：", ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("后台在处理数据体标准上传文件内容时出错："+ e.getMessage());
            if (e instanceof AppException) throw e;
            throw new AppException("后台在处理数据体标准上传文件内容时出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(value = "下载数据体标准csv文件模板")
    @ApiResponses({@ApiResponse(code = 500, message = "下载数据体标准csv文件模板失败")})
    public void downloadStdTableFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            CommonUtil.downloadFile(downloadPath,"std_table.csv", request, response);
        } catch (IOException e) {
            logger.error("下载数据体标准csv文件模板失败，失败原因:" + e.getMessage());
            throw new AppException("下载数据体标准csv文件模板失败，失败原因:", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
}
