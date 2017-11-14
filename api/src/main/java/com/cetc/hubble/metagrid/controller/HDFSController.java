package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.HDFSService;
import com.cetc.hubble.metagrid.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import metagrid.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping(value = "/v2/api/hdfs/")
@Api(value = "/api", description = "HDFS API列表")
public class HDFSController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;
    @Autowired
    private HDFSService hdfsService;


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ApiOperation(value = "下载HDFS文件")
    public ResponseEntity<byte[]> download(@ApiParam(value = "HDFS文件路径", required = true) @RequestParam String path, @ApiParam(value = "数据源ID", required = true) @RequestParam int sourceId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        try {
            String webhdfsPrefix = null;
            DataSource dataSource = dataSourceManagerDAO.getEtlJobById(sourceId);
            if (dataSource == null) throw new AppException("该数据源不存在!", ErrorCode.BAD_REQUEST);
            for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
                if (Constant.HDFS_URL.equals(p.getPropertyName())) {
                    webhdfsPrefix = p.getPropertyValue();
                    break;
                }
            }
            if (webhdfsPrefix == null) throw new AppException("只能下载HDFS文件!", ErrorCode.BAD_REQUEST);
            String url = webhdfsPrefix + "webhdfs/v1" + path + "?op=OPEN";
            byte[] bytes = HttpUtil.doGetBytes(url);
            String[] split = path.split("/");
            String dfileName = split[split.length - 1];
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", dfileName);
            return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
        } catch (IOException e) {
            throw new AppException("无法连接到该HDFS!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            if (e instanceof AppException) throw e;
            logger.warn("download error:{}" + e.getMessage());
            headers.setContentType(MediaType.TEXT_PLAIN);
            throw new AppException("文件不存在!", ErrorCode.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/listStatus", method = RequestMethod.GET)
    @ApiParam(value = "浏览HDFS文件系统目录")
    public HdfsFileStatuses listStatus(@ApiParam(value = "数据源ID", required = true) @RequestParam int sourceId,
                                       @ApiParam(value = "HDFS文件系统目录", required = true) @RequestParam String path,
                                       @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "页数", required = false) int pageNow,
                                       @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "每页显示条数", required = false) int pageSize,
                                       @RequestParam(defaultValue = "false") @ApiParam(value = "是否是文件", required = false) boolean isFile) throws Exception {

        logger.info("request to listStatus, sourceId: {}, path: {}, op: {}", sourceId, path);
        String realPath = path;
        if (isFile) {
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            String path1 = path.substring(0, path.lastIndexOf("/") + 1);
            realPath = path1 + fileName;
        }
        try {
            return hdfsService.listStatus(sourceId, realPath, true, pageNow, pageSize);
        } catch (IOException e) {
            logger.error("list hdfs fileStatus error:{}" + e.getMessage());
            throw new AppException("无法连接到该HDFS!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("list hdfs fileStatus error:{}" + e.getMessage());
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException("获取HDFS文件状态异常!", ErrorCode.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/getBlockLocations", method = RequestMethod.GET)
    @ApiParam(value = "浏览HDFS文件详情")
    public List<HdfsLocatedBlock> getBlockLocations(@ApiParam(value = "数据源ID", required = true) @RequestParam int sourceId,
                                                    @ApiParam(value = "HDFS文件路径", required = true) @RequestParam String path) throws Exception {
        logger.info("request to getBlockLocations, sourceId: {}, path: {}, op: {}", sourceId, path);

        try {

            return hdfsService.getBlockLocations(sourceId, path);
        } catch (IOException e) {
            logger.warn("list hdfs fileStatus error:{}" + e.getMessage());
            throw new AppException("无法连接到该HDFS!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("list hdfs fileStatus error:{}" + e.getMessage());
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException("获取HDFS文件状态异常!", ErrorCode.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/downloadhdfstolocal", method = RequestMethod.GET)
    @ApiOperation(value = "下载HDFS的文件到本地")
    public HashMap<String, String> DownloadhdfsFileToLocal(@ApiParam(value = "数据源ID", required = true) @RequestParam int sourceId,
                                                           @ApiParam(value = "HDFS文件路径", required = true) @RequestParam String path
    ) throws Exception {

        logger.info(" download Hdfsfile, sourceId: {}, path: {}", sourceId, path);

//        String fileName = path.substring(path.lastIndexOf("/")+1);
//        String transformFileName = URLEncoder.encode(fileName, "UTF-8");
//        String Pathtmp=path.substring(0,path.lastIndexOf("/")+1);
//        String realPath=Pathtmp+transformFileName;

        try {
            return hdfsService.DownloadfilesTolocal(sourceId, path);
        } catch (Exception e) {
            if (e instanceof AppException) {
                throw e;
            }

            throw new AppException("下载HDFS文件状态异常!", ErrorCode.BAD_REQUEST);
        }


    }

    @RequestMapping(value = "/downloadToCustomer", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件到客户")
    public void DownloadFileToCustomer(@ApiParam(value = "下载文件的绝对路径", required = true) @RequestParam String path,
                                       HttpServletResponse resp) throws Exception {


        logger.info("Download File to Customer, path: {}", path);
        try {
            hdfsService.DownloadfilesToCustomer(path, resp);
        } catch (IOException e) {
            throw new AppException("下载文件异常!", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            if (e instanceof AppException) {
                throw e;
            }
            throw new AppException("下载文件异常!", ErrorCode.BAD_REQUEST);
        }


    }

    @RequestMapping(value = "/file/attribute", method = RequestMethod.POST)
    @ApiOperation(value = "添加文件属性")
    public Integer insertHdfsFileAttr(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) HdfsFileAttribute hdfsFileAttribute) {
        logger.info("add hdfs file attribute: {}", hdfsFileAttribute);
        Integer result = null;
        try {
            result = hdfsService.insertHdfsFileAttr(hdfsFileAttribute);
        } catch (Exception e) {
            logger.error("添加文件属性失败：", e.getMessage());
            e.printStackTrace();
            if(e instanceof AppException){
                throw e;
            }
            throw new AppException("添加文件属性失败!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @RequestMapping(value = "/file/attribute/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改文件属性")
    public void updateHdfsFileAttr(@PathVariable @ApiParam(value = "属性ID", required = true) Integer id,
                                   @RequestParam @ApiParam(value = "属性值", required = true) String keyword) {
        logger.info("update hdfs file attribute,id: {},keyword: {}", id, keyword);
        try {
            hdfsService.updateHdfsFileAttr(id, keyword);
        } catch (Exception e) {
            logger.error("修改文件属性失败：", e.getMessage());
            e.printStackTrace();
            throw new AppException("修改文件属性失败!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file/attribute/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除文件属性")
    public void deleteHdfsFileAttr(@PathVariable @ApiParam(value = "属性ID", required = true) Integer id) {
        logger.info("delete hdfs file attribute,id: {}", id);
        try {
            hdfsService.deleteHdfsFileAttr(id);
        } catch (Exception e) {
            logger.error("删除文件属性失败：", e.getMessage());
            e.printStackTrace();
            throw new AppException("删除文件属性失败!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file/attribute/search", method = RequestMethod.GET)
    @ApiOperation(value = "搜索文件属性")
    public List<HdfsFileAttribute> searchFileAttr(@RequestParam @ApiParam(value = "属性值关键字", required = true) String keyword) {
        logger.info("get hdfs file attribute,keyword: {}", keyword);
        List<HdfsFileAttribute> list = hdfsService.searchFileAttr(keyword);
        return list;
    }

    @RequestMapping(value = "/file/attribute", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件属性")
    public List<HdfsFileAttribute> getHdfsFileAttrByPath(@RequestParam @ApiParam(value = "数据源ID", required = true) Integer sourceId,
                                                         @RequestParam @ApiParam(value = "文件路径", required = true) String path,
                                                         @RequestParam @ApiParam(value = "文件名称", required = true) String fileName) {
        logger.info("get hdfs file attribute,sourceId: {},path:{}", sourceId, path);
        List<HdfsFileAttribute> list = hdfsService.getFileAttr(sourceId, path,fileName);
        return list;
    }

    @RequestMapping(value = "/file/attribute/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据文件名称和路径删除文件属性")
    public Integer deleteFileByPath(@RequestParam @ApiParam(value = "数据源ID", required = true) Integer sourceId,
                                    @RequestParam @ApiParam(value = "文件路径", required = true) String path,
                                    @RequestParam @ApiParam(value = "文件名称", required = true) String filename){
        return hdfsService.deleteFileByPath(sourceId,path,filename);
    }


}
