package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.pagination.CommonUtil;
import com.cetc.hubble.metagrid.service.DataStandardService;
import com.cetc.hubble.metagrid.vo.DictionaryParam;
import com.cetc.hubble.metagrid.vo.StdIdentifierWrapper;
import com.cetc.hubble.metagrid.vo.TreeNode;
import io.swagger.annotations.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/3/21.
 */
@RestController
@RequestMapping(value = "/v2/api/dataStandard/")
@Api(value = "/api", description = "元数据元标准API列表")
public class DataStandardController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${data.file.upload.path}")
    public String uploadPath;

    @Value("${data.file.download.path}")
    public String downloadPath;

    public static final String TEMPLATE_FILE_NAME = "std_identifier.xls";

    @Autowired
    private DataStandardService dataStandardService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "数据元标准列表信息获取")
    @ApiResponses({@ApiResponse(code = 500, message = "数据元标准列表信息获取失败")})
    public Map<String, Object> getDictionaryEntries(
            @RequestBody @ApiParam(value = "数据元标准查询参数", required = true) DictionaryParam param) {

        logger.info("dataStandard list information get: {}",param);
        try {
            return  dataStandardService.getDictionaryEntries(param);
        } catch (Exception e) {
            logger.error("Failed to get dataStandard list information.", e);
            throw new AppException("数据元标准列表信息获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{identifier}/tables", method = RequestMethod.GET)
    @ApiOperation(value = "匹配包含此数据元标准字段的表")
    @ApiResponses({@ApiResponse(code = 500, message = "匹配包含此数据元标准字段的表失败")})
    public List<TreeNode> getTablesByIdentifier(
            @PathVariable @ApiParam(value = "数据元标准查询参数", required = true) String identifier) {

        logger.info("get tables by identifier : {}",identifier);
        try {
            return  dataStandardService.getTablesByIdentifier(identifier);
        } catch (Exception e) {
            logger.error("Failed to get tables by identifier.", e);
            throw new AppException("匹配包含此数据元标准字段的表失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元标准")
    @ApiResponses({@ApiResponse(code = 400, message = "新增数据元标准数据校验失败"),
            @ApiResponse(code = 409, message = "新增数据元标准标识符已存在"),
            @ApiResponse(code = 500, message = "新增数据元标准失败")})
    public Map<String, Object> create(
            @RequestBody @Valid @ApiParam(value = "新增数据元标准参数", required = true) StdIdentifierWrapper stdIdentifierWrapper,
            BindingResult result
    ) {
        logger.info("dataStandard create: {}", stdIdentifierWrapper);

        //check duplicated record by field identifier
        if (dataStandardService.checkExistIdentifierOrChName(stdIdentifierWrapper.getIdentifier())) {
            throw new AppException("已存在数据元标字段标识符："+stdIdentifierWrapper.getIdentifier(), ErrorCode.CONFLICT);
        }

        try {
            return dataStandardService.create(stdIdentifierWrapper);
        } catch (Exception e) {
            logger.error("新增数据元标准失败，失败原因：" + e.getMessage());
            throw new AppException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{standardId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元标准")
    @ApiResponses({@ApiResponse(code = 400, message = "数据元标准数据校验失败"),
            @ApiResponse(code = 500, message = "修改数据元标准失败")})
    public Map<String, Object> update(
            @PathVariable @Valid @ApiParam(value = "数据元标准id", required = true) int standardId,
            @RequestBody @Valid @ApiParam(value = "修改数据元标准参数", required = true) StdIdentifierWrapper stdIdentifierWrapper,
            BindingResult result
    ) {
        logger.info("dataStandard update:%d, {}", stdIdentifierWrapper);

        try {
            return dataStandardService.update(stdIdentifierWrapper, standardId);
        } catch (Exception e) {
            logger.error("修改数据元标准失败，失败原因：" + e.getMessage());
            if(e instanceof AppException) {
                throw new AppException(e.getMessage(), ((AppException)e).getErrorCode());
            } else {
                throw new AppException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ApiOperation(value = "业务词条execl文件导入")
    @ApiResponses({
            @ApiResponse(code = 400, message = "业务词条execl文件模板格式校验失败"),
            @ApiResponse(code = 409, message = "上传的业务词条内容已经存在"),
            @ApiResponse(code = 500, message = "业务词条execl文件批量导入失败")})
    public void upload(@ApiParam(name="file",value="待上传文件", required = true) MultipartFile file) {
        logger.info("dataStandard batch:%d, {}", file.getOriginalFilename());
        try {
            Map<String, String>  stringMap = dataStandardService.fileContentHandle(downloadPath, file);
            //插入数据到数据库
            dataStandardService.saveStdIdentifierList(stringMap);

        } catch (IOException e) {
            logger.error("业务词条上传文件出错：" + e.getMessage());
            throw new AppException("业务词条上传文件出错", ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (InvalidFormatException ie) {
            logger.error("业务词条上传文件出错：" + ie.getMessage());
            throw new AppException("业务词条上传文件出错", ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("后台在处理业务词条上传文件内容时出错："+ e.getMessage());
            e.printStackTrace();
            if (e instanceof AppException) throw e;
            throw new AppException("后台在处理业务词条上传文件内容时出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    @ApiOperation(value = "下载业务词条execl文件模板")
    @ApiResponses({@ApiResponse(code = 500, message = "下载业务词条execl文件模板失败")})
    public void downloadStdIdentifierFile(HttpServletRequest request, HttpServletResponse response) {
        logger.info("dataStandard template download.");
        try {
            CommonUtil.downloadFile(downloadPath, TEMPLATE_FILE_NAME, request, response);
        } catch (IOException e) {
            logger.error("下载业务词条execl文件模板失败，失败原因:" + e.getMessage());
            throw new AppException("下载业务词条execlv文件模板失败，失败原因:", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/{standardId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元标准")
    @ApiResponses({
            @ApiResponse(code = 500, message = "删除数据元标准失败")})
    public void delete(
            @PathVariable @ApiParam(value = "数据元标准id", required = true) int standardId
    ) {
        logger.info("dataStandard delete:%d, {}", standardId);

        try {
            dataStandardService.delete(standardId);
        } catch (Exception e) {
            logger.error("删除数据元标准失败，失败原因：" + e.getMessage());
            throw new AppException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
