package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.HdfsDao;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.util.Pager;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Maps;
import metagrid.common.Constant;
import metagrid.common.utils.Json;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jinyi on 17-8-3.
 */
@Service
public class HDFSService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataSourceManagerDAO dataSourceManagerDAO;

    @Autowired
    private HdfsDao hdfsDao;

    @Value("${data.file.download.path}")
    private String downloadpath;


    @Value("${metagrid.hdfsfileSaveTime}")
    private String HdfsSaveTime;


    public HashMap<String, String> DownloadfilesTolocal(int sourceId, String absolutepath) throws Exception {

        DataSource dataSource = dataSourceManagerDAO.getEtlJobById(sourceId);
        if (dataSource == null) {
            throw new AppException("该数据源已不存在!", ErrorCode.BAD_REQUEST);
        }

        String hdfsIp = null;
        String namenodeport = null;
        FileSystem fs = null;
        FSDataInputStream is = null;
        String downloadfilename = null;
        FileOutputStream os = null;

        String destpath = downloadpath;

        hdfsIp = getHdfsIp(sourceId);
        if (hdfsIp == null) {
            throw new AppException("数据服务IP查詢失敗", ErrorCode.NOT_FOUND);
        }

        namenodeport = getHdfsPort(sourceId);
        if (namenodeport == null) {
            throw new AppException("数据服务端口查詢失敗", ErrorCode.NOT_FOUND);
        }


        String HdfsSeverURL = "hdfs://" + hdfsIp + ":" + namenodeport + "/";

        String HdfsFileURL = "hdfs://" + hdfsIp + ":" + namenodeport + absolutepath;

        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", HdfsSeverURL);

        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            throw new AppException("创建hdfs文件系统失败", ErrorCode.STATUS_ERROR);
        }

        CheckNamenodePort(fs, namenodeport);

        Path temp = new Path(HdfsFileURL);
        boolean isfileexist = fs.exists(temp);
        if (!isfileexist) {
            throw new AppException("文件不存在", ErrorCode.NOT_FOUND);
        }

        CheckDiskSpace(temp, fs);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
        String dirnametmp = df.format(new Date());

        String destpathdir = destpath + "/" + dirnametmp;
        File DownloadDirTmp = new File(destpathdir);
        boolean isDirExist = DownloadDirTmp.exists();
        if (!isDirExist) {
            DownloadDirTmp.mkdirs();
        }

        int index = absolutepath.lastIndexOf("/");
        downloadfilename = absolutepath.substring(index + 1);
        String destpathfilename = destpathdir + "/" + downloadfilename;

        try {
            is = fs.open(temp);
            os = new FileOutputStream(destpathfilename);
            IOUtils.copyBytes(is, os, 4096, false);

        } catch (FileNotFoundException e) {
            DirAndfileDel(destpathdir);
            e.printStackTrace();
            logger.info("hdfs输出文件创建失败");

        } catch (IOException e) {
            DirAndfileDel(destpathdir);
            e.printStackTrace();
            logger.info("hdfs 下载文件失败");
        } catch (Exception e) {
            DirAndfileDel(destpathdir);
            e.printStackTrace();
            logger.info("hdfs 下载文件异常");
        } finally {

            is.close();
            os.close();
        }

        String dirname = df.format(new Date());
        String DownLoadDir = destpath + "/" + dirname;
        DownloadDirTmp.renameTo(new File(DownLoadDir));

        logger.info("hdfs成功下载文件 {} 到本地服务器 " + downloadfilename);

        String ReturnPath = dirname + "/" + downloadfilename;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("path", ReturnPath);
        hashMap.put("status", "ok");

        return hashMap;


    }

    public void CheckDiskSpace(Path path, FileSystem fs) throws Exception {
        FileStatus FSstatus = null;
        File DownloadDisk = new File(downloadpath);


        if(!DownloadDisk.exists())
        {
            DownloadDisk.mkdirs();
        }
        
        if(!DownloadDisk.exists())
        {
            throw new AppException("hdfs下载目录创建失败，请检查下载路径权限", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        long UsableSpace = DownloadDisk.getUsableSpace();
        try {
            FSstatus = fs.getFileStatus(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException("hdfs 文件信息获取异常", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        //预留100m空间，防止下载文件导致系统异常
        if (UsableSpace < (FSstatus.getLen() + 1024 * 1024 * 100)) {
            throw new AppException("服务器磁盘空间不足，请清理磁盘空间", ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    public void HdfsTimeMoniter(String DownLoadpath) {
        File DownLoaddir = new File(DownLoadpath);

        if(DownLoaddir.exists())
        {

            int Savetime = Integer.parseInt(HdfsSaveTime);
            Date now = new Date();

            File[] fileDirs = DownLoaddir.listFiles();
            for (int i = 0; i < fileDirs.length; i++)
            {
                if (fileDirs[i].isDirectory())
                {
                    File[] files = fileDirs[i].listFiles();

                    if(files.length == 0)
                    {
                        fileDirs[i].delete();
                        logger.info("删除空目录{}", fileDirs[i].getName());
                    }
                    else
                    {
                        Date filetime = new Date(files[0].lastModified());

                        int Lifetime = (int) ((now.getTime() - filetime.getTime()) / 1000);

                        if (Lifetime > Savetime)
                        {
                            String Dirpath = DownLoadpath + "/" + fileDirs[i].getName();
                            DirAndfileDel(Dirpath);

                        }
                    }

                }

            }
        }
        else
        {
            logger.info("hdfs 下载目录不存在");
        }

    }

    public void DirAndfileDel(String Dirpath) {
        File dirfile = new File(Dirpath);
        if (dirfile.isDirectory()) {
            File[] files = dirfile.listFiles();
            for (int i = 0; i < files.length; i++) {
                try {
                    files[i].delete();
                    logger.info("成功删除文件{}", files[i].getName());
                } catch (Exception e) {
                    logger.info("删除文件{}失败", files[i].getName());
                }
            }
        }
        try {
            dirfile.delete();
        } catch (Exception e) {
            logger.info("删除文件夹{}失败", dirfile.getName());
        }

        logger.info("成功删除文件夹{}", Dirpath);
    }


    public void CheckNamenodePort(FileSystem fs, String namenodeport) throws Exception {
        Path path = new Path("/");
        FileStatus[] files;
        try {
            files = fs.listStatus(path);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.info("数据端口{}连接测试失败", namenodeport);
            e.printStackTrace();
            throw new AppException("数据端口连接测试失败", ErrorCode.STATUS_ERROR);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.info("数据端口{}连接测试失败", namenodeport);
            e.printStackTrace();
            throw new AppException("数据端口连接测试失败", ErrorCode.STATUS_ERROR);
        }

        logger.info("数据端口{}连接测试成功", namenodeport);

    }

    public void DownloadfilesToCustomer(String DowonloadPath, HttpServletResponse resp) throws Exception {

        int index = DowonloadPath.lastIndexOf("/");

        String DowonloadDir = DowonloadPath.substring(0, index);
        String DownloadName = DowonloadPath.substring(index + 1);

        File Netfile = new File(DowonloadPath);
        File destdir = new File(DowonloadDir);

        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");

        resp.setHeader("Content-Disposition", "attachment;filename=" + DownloadName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream fos = null;
        try {
            fos = resp.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(Netfile));

            int length = 0;
            while (-1 != (length = bis.read(buff, 0, buff.length))) {
                fos.write(buff, 0, length);
                fos.flush();
            }
        } catch (IOException e) {
            Netfile.delete();
            destdir.delete();
            logger.info("hdfs成功删除本地文件 ：" + DowonloadDir);
            e.printStackTrace();
            throw new AppException("服务器文件传输失败", ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            Netfile.delete();
            destdir.delete();
            logger.info("hdfs成功删除本地文件 ：" + DowonloadDir);
            e.printStackTrace();
            throw new AppException("服务器文件传输失败", ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {

            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Netfile.delete();
        destdir.delete();

        logger.info("hdfs成功删除本地文件 ：" + DowonloadDir);
    }


    public String getHdfsPort(int sourceId)
    {
        String namenodePort = null;
        Map<String, Object>properties= dataSourceManagerDAO.queryProperty_value(sourceId,Constant.HDFS_NAMENODEPORT);
        if(properties.size() == 0)
        {
            throw new AppException("该ID数据信息不存在", ErrorCode.BAD_REQUEST);
        }

        String port = (String)properties.get("property_value");

        if(port == null)
        {

            logger.info(sourceId + "hdfs数据服务端口查询失败");
            throw new AppException("hdfs数据服务端口查询失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        else
        {
            namenodePort = port.trim();

            if(namenodePort.equals(""))
            {
                logger.info(sourceId + "hdfs数据服务端口查询为空");
                throw new AppException("hdfs数据服务端口查询为空", ErrorCode.INTERNAL_SERVER_ERROR);
            }

        }

        return namenodePort;
    }

    public String getHdfsIp(int sourceId) {
        String URL = null;

        Map<String, Object> properties = dataSourceManagerDAO.queryProperty_value(sourceId, Constant.HDFS_URL);

        if (properties.size() == 0) {
            throw new AppException("该ID数据信息不存在", ErrorCode.BAD_REQUEST);
        }

        if (properties.get("property_value") == null) {
            logger.info(sourceId + "hdfs ip查询失败");
        }

        URL = String.valueOf(properties.get("property_value"));

        int indexf = URL.indexOf(':');

        String tmpstr = URL.substring(indexf + 1);

        int indexl = tmpstr.indexOf(':');

        String Hdfsip = tmpstr.substring(2, indexl);


        return Hdfsip;
    }

    //throwRemoteException: true 抛出hdfs远程异常, false 不抛出hdfs远程异常
    public HdfsFileStatuses listStatus(int sourceId, String path, Boolean throwRemoteException, int pageNow, int pageSize) throws Exception {

        DataSource dataSource = dataSourceManagerDAO.getEtlJobById(sourceId);
        if (dataSource == null) {
            throw new AppException("该数据源不存在!", ErrorCode.BAD_REQUEST);
        }

        String webhdfsPrefix = null;
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            if (Constant.HDFS_URL.equals(p.getPropertyName())) {
                webhdfsPrefix = p.getPropertyValue();
                break;
            }
        }
        if (webhdfsPrefix == null) {
            throw new AppException("未找到路径!", ErrorCode.BAD_REQUEST);
        }
        String url = webhdfsPrefix + "webhdfs/v1" + path + "?op=" + Constant.WEBHDFS_OPERATION_LISTSTATUS;
        String addr = url.substring(url.indexOf("/"));
        URI uri=new URI("http",addr,null);
        String result = HttpUtil.doGet(uri.toASCIIString(), "UTF-8");
        if (result == null) {
            throw new AppException("无法连接到该HDFS!", ErrorCode.NOT_FOUND);
        }

        if (throwRemoteException) {
            parseException(result);
        }

        return parseHdfsFileStatusResponse(result, throwRemoteException, pageNow, pageSize);
    }

    public List<HdfsLocatedBlock> getBlockLocations(int sourceId, String path) throws Exception {

        DataSource dataSource = dataSourceManagerDAO.getEtlJobById(sourceId);
        if (dataSource == null) {
            throw new AppException("该数据源不存在!", ErrorCode.BAD_REQUEST);
        }

        String webhdfsPrefix = null;
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            if (Constant.HDFS_URL.equals(p.getPropertyName())) {
                webhdfsPrefix = p.getPropertyValue();
                break;
            }
        }
        if (webhdfsPrefix == null) {
            throw new AppException("未找到路径!", ErrorCode.BAD_REQUEST);
        }

        String url = webhdfsPrefix + "webhdfs/v1" + path + "?op=" + Constant.WEBHDFS_OPERATION_GET_BLOCK_LOCATIONS;
        String result = HttpUtil.doGet(url);
        if (result == null) {
            throw new AppException("无法连接到该HDFS!", ErrorCode.NOT_FOUND);
        }

        parseException(result);

        return parseHdfsLocatedBlockResponse(result);
    }

    private void parseException(String response) {

        JsonNode jsonNodeRemoteException = Json.parse(response).get("RemoteException");
        if (jsonNodeRemoteException != null) {
            if (jsonNodeRemoteException.get("exception").asText().equals("FileNotFoundException")) {
                throw new AppException("未找到路径", ErrorCode.NOT_FOUND);
            } else if (jsonNodeRemoteException.get("exception").asText().equals("AccessControlException")) {
                throw new AppException("无该路径权限", ErrorCode.FORBIDDEN);
            }
        }
    }

    private HdfsFileStatuses parseHdfsFileStatusResponse(String response, boolean throwRemoteException, int pageNow, int pageSize) {

        List<HdfsFileStatus> fileStatuses = new ArrayList<>();
        HdfsFileStatuses hdfsFileStatuses = new HdfsFileStatuses();

        JsonNode jsonNodeFileStatuses = Json.parse(response).get("FileStatuses");
        if (jsonNodeFileStatuses == null) {
            if (throwRemoteException) {
                throw new AppException("无法获取文件状态!", ErrorCode.BAD_REQUEST);
            } else {
                return hdfsFileStatuses;
            }
        }

        JsonNode jsonNodeFileStatus = jsonNodeFileStatuses.get("FileStatus");
        if (jsonNodeFileStatus == null) {
            if (throwRemoteException) {
                throw new AppException("无法获取文件状态!", ErrorCode.BAD_REQUEST);
            } else {
                return hdfsFileStatuses;
            }
        }

        Iterator<JsonNode> iterator = jsonNodeFileStatus.iterator();
        while (iterator.hasNext()) {
            ObjectNode objectNode = (ObjectNode) iterator.next();

            //排除正在上传的文件：pathSuffix是以._COPYING_结尾
            Object node = objectNode.get("pathSuffix");
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                if (textNode.asText().endsWith("._COPYING_")) {
                    continue;
                }
                ;
            }

            fileStatuses.add(buildHdfsFileStatusByObjectNode(objectNode));
        }
        //对fileStatuses进行分页
        Pager<HdfsFileStatus> pager = new Pager<>(pageNow, pageSize, fileStatuses);
        hdfsFileStatuses.setFileStatuses(pager.getList());
        return hdfsFileStatuses;
    }

    private HdfsFileStatus buildHdfsFileStatusByObjectNode(ObjectNode objectNode) {

        HdfsFileStatus hdfsFileStatus = new HdfsFileStatus();
        hdfsFileStatus.setAccessTime(objectNode.get("accessTime").asLong());
        hdfsFileStatus.setBlockSize(objectNode.get("blockSize").asLong());
        hdfsFileStatus.setGroup(objectNode.get("group").asText());
        hdfsFileStatus.setLength(objectNode.get("length").asLong());
        hdfsFileStatus.setModificationTime(objectNode.get("modificationTime").asLong());
        hdfsFileStatus.setOwner(objectNode.get("owner").asText());
        hdfsFileStatus.setPathSuffix(objectNode.get("pathSuffix").asText());
        hdfsFileStatus.setPermission(objectNode.get("permission").asText());
        hdfsFileStatus.setReplication(objectNode.get("replication").asInt());
        hdfsFileStatus.setType(objectNode.get("type").asText());

        return hdfsFileStatus;
    }

    private List<HdfsLocatedBlock> parseHdfsLocatedBlockResponse(String response) {

        JsonNode jsonNodeLocatedBlocks = Json.parse(response).get("LocatedBlocks");
        if (jsonNodeLocatedBlocks == null) {
            throw new AppException("无法获取文件详情!", ErrorCode.BAD_REQUEST);
        }

        JsonNode jsonNodelocatedBlocks = jsonNodeLocatedBlocks.get("locatedBlocks");
        if (jsonNodelocatedBlocks == null) {
            throw new AppException("无法获取文件详情!", ErrorCode.BAD_REQUEST);
        }

        List<HdfsLocatedBlock> hdfsLocatedBlocks = new ArrayList<>();
        Iterator<JsonNode> iterator = jsonNodelocatedBlocks.iterator();
        while (iterator.hasNext()) {
            ObjectNode objectNode = (ObjectNode) iterator.next();
            hdfsLocatedBlocks.add(buildHdfsLocatedBlockByObjectNode(objectNode));
        }
        return hdfsLocatedBlocks;
    }

    private HdfsLocatedBlock buildHdfsLocatedBlockByObjectNode(ObjectNode objectNode) {

        HdfsLocatedBlock hdfsLocatedBlock = new HdfsLocatedBlock();
        if (objectNode.get("block") != null) {
            hdfsLocatedBlock.setBlockId(objectNode.get("block").get("blockId").asLong());
            hdfsLocatedBlock.setBlockPoolId(objectNode.get("block").get("blockPoolId").asText());
            hdfsLocatedBlock.setGenerationStamp(objectNode.get("block").get("generationStamp").asLong());
            hdfsLocatedBlock.setNumBytes(objectNode.get("block").get("numBytes").asLong());
        }

        hdfsLocatedBlock.setCorrupt(objectNode.get("isCorrupt").asBoolean());

        if (objectNode.get("locations") != null) {
            List<HdfsLocatedBlockLocation> locations = new ArrayList<>();
            Iterator<JsonNode> iterator = objectNode.get("locations").iterator();
            while (iterator.hasNext()) {
                ObjectNode objectNodeLocation = (ObjectNode) iterator.next();
                locations.add(new HdfsLocatedBlockLocation(objectNodeLocation.get("hostName").asText()));
            }
            hdfsLocatedBlock.setLocations(locations);
        }

        return hdfsLocatedBlock;
    }

    /**
     * 添加hdfs文件属性
     *
     * @param hdfsFileAttribute
     * @return
     */
    public Integer insertHdfsFileAttr(HdfsFileAttribute hdfsFileAttribute) {
        return hdfsDao.addFileAttrReturnKey(hdfsFileAttribute);
    }

    /**
     * 修改hdfs文件属性
     *
     * @param id
     * @param keyword
     * @return
     */
    public Integer updateHdfsFileAttr(Integer id, String keyword) {
        return hdfsDao.updateFileAttr(id, keyword);
    }

    /**
     * 删除hdfs文件属性
     *
     * @param id
     * @return
     */
    public Integer deleteHdfsFileAttr(Integer id) {
        return hdfsDao.deleteFileAttr(id);
    }

    /**
     * 通过数据源Id删除hdfs文件属性
     * @param sourceId
     * @return
     */
    public Integer deleteHdfsFileAttrBySourceId(Integer sourceId) {
        return hdfsDao.deleteFileAttrBySourceId(sourceId);
    }

    /**
     * 通过keyword模糊搜索文件属性
     *
     * @param keyword
     * @return
     */
    public List<HdfsFileAttribute> searchFileAttr(String keyword) {
        return hdfsDao.searchFileAttr(keyword);
    }

    /**
     * 根据数据源ID和文件路径获取文件属性
     *
     * @param sourceId
     * @param path
     * @param fileName
     * @return
     */
    public List<HdfsFileAttribute> getFileAttr(Integer sourceId, String path,String fileName) {
        Map map = Maps.newHashMap();
        map.put("sourceId", sourceId);
        map.put("path", path);
        map.put("filename",fileName);
        return hdfsDao.getFileAttr(map);
    }


    /**
     * 根据路径和文件名称删除关键字
     * @param sourceId
     * @param path
     * @param filename
     * @return
     */
    public Integer deleteFileByPath(Integer sourceId,String path,String filename){
        return hdfsDao.deleteFileByPath(sourceId,path,filename);
    }

    /**
     * 检测所有hdfs有属性的文件在hdfs文件系统中是否还存在，不存在则删除属性记录
     */
    public void checkFileIsExist() throws Exception{

        FileSystem fs = null;
        Configuration conf = new Configuration();
        List<HdfsFileAttribute> hdfsFileAttributes = hdfsDao.getAllFileAttr();
        if(hdfsFileAttributes.size()>0 && hdfsFileAttributes!=null){
            for(HdfsFileAttribute fileAttribute :hdfsFileAttributes){
                String filePath=fileAttribute.getPath()+fileAttribute.getFileName();
                Integer sourceId = fileAttribute.getSourceId();
                Map<String,String> mapHdfsUrl= getHdfsUrlBySourceId(sourceId);
                String hdfsIp=mapHdfsUrl.get("ip");
                String namenodeport=mapHdfsUrl.get("namenodeport");
                String hdfsSeverURL = "hdfs://" + hdfsIp + ":" + namenodeport + "/";
                conf.set("fs.defaultFS", hdfsSeverURL);
                try {
                    fs = FileSystem.get(conf);
                } catch (IOException e) {
                    throw new AppException("创建hdfs文件系统失败", ErrorCode.STATUS_ERROR);
                }
                String hdfsFilePath = "hdfs://" + hdfsIp + ":" + namenodeport + filePath;
                Path realPath = new Path(hdfsFilePath);
                boolean fileIsExist = fs.exists(realPath);
                if (!fileIsExist) {
                    hdfsDao.deleteFileByPath(sourceId,fileAttribute.getPath(),fileAttribute.getFileName());

                }


            }
        }
    }

    private Map<String,String> getHdfsUrlBySourceId(Integer sourceId){
        Map<String,String> map=Maps.newHashMap();
        String hdfsIp = getHdfsIp(sourceId);
        if (hdfsIp == null) {
            throw new AppException("hdfs IP查询失败", ErrorCode.NOT_FOUND);
        }
        String namenodeport = getHdfsPort(sourceId);
        if (namenodeport == null) {
            throw new AppException("hdfs 端口查询失败", ErrorCode.NOT_FOUND);
        }
        map.put("ip",hdfsIp);
        map.put("namenodeport",namenodeport);
        return map;
    }
}
