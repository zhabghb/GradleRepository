package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataStandardDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.pagination.CommonUtil;
import com.cetc.hubble.metagrid.vo.DictionaryParam;
import com.cetc.hubble.metagrid.vo.StdIdentifier;
import com.cetc.hubble.metagrid.vo.StdIdentifierWrapper;
import com.cetc.hubble.metagrid.vo.TreeNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by dahey on 2017-3-21.
 */
@Service
@Transactional
public class DataStandardService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public static final Integer COLUMN_NUMBER = 4;

    public static final String  UPLOAD_FILE_NAME = "std_identifier";

    private String splitor = CommonUtil.SPLITOR;

    @Autowired
    private DataStandardDAO dataStandardDAO;

    public Map<String, Object> getDictionaryEntries(DictionaryParam param) {

        if (param.getKeyword() != null
                && (param.getKeyword().contains("'") || param.getKeyword().contains("%")||param.getKeyword().contains("\\")
                || param.getKeyword().contains("_"))) {
            param.setKeyword(param.getKeyword().
                    replaceAll("'", "''").
                    replaceAll("%", "\\\\%").
                    replaceAll("\\\\", "\\\\\\\\").
                    replaceAll("_", "\\\\_"));
        }

        return dataStandardDAO.getDictionaryEntries(param);
    }

    public List<TreeNode> getTablesByIdentifier (String identifier) {
        return dataStandardDAO.getTablesByIdentifier(identifier);
    }

    public Map<String, Object> create(StdIdentifierWrapper stdIdentifierWrapper) {
        Map<String, Object> stdParams = new HashMap<String, Object>();
        stdParams = new HashMap<String, Object>();

        String internalIdentifier = stdIdentifierWrapper.getInternalIdentifier();
        if (internalIdentifier == null) {
            internalIdentifier = "";
        }

        String gatCodex = stdIdentifierWrapper.getGatCodex();
        if (gatCodex == null) {
            gatCodex = "";
        }

        stdParams.put("internalIdentifier", internalIdentifier);
        stdParams.put("identifier", stdIdentifierWrapper.getIdentifier());
        stdParams.put("chName", stdIdentifierWrapper.getChName());
        stdParams.put("enName", "");
        stdParams.put("version", "");
        stdParams.put("descripton", "");
        stdParams.put("status", "");
        stdParams.put("submitInstitution", "");
        stdParams.put("approvalDate", new Date());
        stdParams.put("remark", "");
        stdParams.put("gatCodex", gatCodex);
        int id = dataStandardDAO.saveStdIdentifer(stdParams);

        Map<String, Object> ret = Maps.newHashMap();
        ret.put("standardId", id);
        return ret;
    }

    public boolean checkExistIdentifierOrChName(String identifier) {
        List<StdIdentifier>  list = dataStandardDAO.getStdIdentifierList(identifier);
        if(list != null && list.size() > 0) {
            return true;
        }

        return false;
    }

    public void saveStdIdentifierList(Map<String, String> stringMap) {
        Map<String, Object> stdParams = null;
        String[] arr = null;
        for(String line : stringMap.values()) {
            arr = line.split(splitor, COLUMN_NUMBER+1);     //+1的原因是最后一个cell可能是空，然后内容就会被处理为splitor
            stdParams = new HashMap<String, Object>();
            stdParams.put("internalIdentifier", arr[3]);
            stdParams.put("identifier", arr[0]);
            stdParams.put("chName", arr[1]);
            stdParams.put("enName", "");
            stdParams.put("version", "");
            stdParams.put("descripton", "");
            stdParams.put("status", "");
            stdParams.put("submitInstitution", "");
            stdParams.put("approvalDate", new Date());
            stdParams.put("remark", "");
            stdParams.put("gatCodex", arr[2]);

            dataStandardDAO.saveStdIdentifer(stdParams);
        }
    }

    private Date stringTODate(String date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse(date);
        } catch(Exception ex) {
            return null;
        }
    }

    public Map<String, Object> update(final StdIdentifierWrapper stdIdentifierWrapper, int standardId) {

        if(isStandardIdentifierDuplicated(stdIdentifierWrapper, standardId)) {
            throw new AppException("已存在数据元字段标识符："+stdIdentifierWrapper.getIdentifier(), ErrorCode.CONFLICT);
        }


        Map<String, Object> stdParams = Maps.newHashMap();
        stdParams.put("id", standardId);

        StdIdentifier stdIdentifier = dataStandardDAO.getStdIdentifierById(stdParams);

        Map<String, Object> stdParams1 = Maps.newHashMap();
        stdParams1.put("id", standardId);
        stdParams1.put("internalIdentifier", stdIdentifierWrapper.getInternalIdentifier());
        stdParams1.put("identifier", stdIdentifierWrapper.getIdentifier());
        stdParams1.put("chName", stdIdentifierWrapper.getChName());
        stdParams1.put("gatCodex", stdIdentifierWrapper.getGatCodex());
        stdParams1.put("enName", stdIdentifier.getEnName());
        stdParams1.put("version", stdIdentifier.getVersion());
        stdParams1.put("descripton", stdIdentifier.getDescripton());
        stdParams1.put("status", stdIdentifier.getStatus());
        stdParams1.put("submitInstitution", stdIdentifier.getSubmitInstitution());
        stdParams1.put("approvalDate", stdIdentifier.getApprovalDate());
        stdParams1.put("remark", stdIdentifier.getRemark());

        dataStandardDAO.updateStdIdentifier(stdParams1);

        Map<String, Object> ret = Maps.newHashMap();
        ret.put("standardId", standardId);
        return ret;
    }

    public Map<String, Object> delete(int id) {

        Map<String, Object> stdParams = Maps.newHashMap();
        stdParams.put("standardId", id);
        int result = dataStandardDAO.deleteStdIdentifier(stdParams);
        //删除业务词条后，删除主题与标准的关系记录
        if(result>0){
            dataStandardDAO.deleteDomainIdentifiers(id);
        }

        return stdParams;
    }

    private boolean isStandardIdentifierDuplicated(final StdIdentifierWrapper stdIdentifierWrapper, int standardId) {

        List<StdIdentifier>  list = dataStandardDAO.getStdIdentifierList(stdIdentifierWrapper.getIdentifier());
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).getId() != standardId) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> fileContentHandle(String uploadPath, MultipartFile file) throws IOException, InvalidFormatException {
        File dest = CommonUtil.uploadFile(uploadPath, file, UPLOAD_FILE_NAME);
        List<String> lines = CommonUtil.readFile(dest.getAbsolutePath(),splitor);
        dest.delete();
        if(lines == null || lines.size() <= 1) {
            throw new AppException("文件中存在空行或者没有内容", ErrorCode.BAD_REQUEST);
        }
        //读取文件内容并检验文件内容格式
        Map<String, String> stringMap = new HashMap<String, String>();

        String str = "";
        int countLine = 0;
        List<String> errorCells = Lists.newArrayList();
        for(int i = 1; i < lines.size(); i++) {
            str = lines.get(i);
            if(!Strings.isNullOrEmpty(str)) {
                String[] arr = str.split(splitor,COLUMN_NUMBER+1);     //+1的原因是最后一个cell可能是空，然后内容就会被处理为splitor
                if(arr.length < COLUMN_NUMBER) {
                    throw new AppException("请参考数据元标准模板格式, 填写相关内容.", ErrorCode.BAD_REQUEST);
                }

                //字符标识符：/^[\a-zA-Z0-9_\-]{1,50}$/ 字母，数字，下划线，中划线，长度范围1-50字符
                String pattern = "^[a-zA-Z0-9_\\-]{1,50}$";
                if(!Pattern.matches(pattern, arr[0])){
                    logger.info("\npattern:" + pattern+"\ncontent:"+arr[0]);
                    errorCells.add(i+1+"A");
                }
                //中文名称：/^[\u4e00-\u9fa5_\- \\(\\)\[\]\\{\\}\\(0-9)*$]{1,50}$/ 中文，数字，下划线，中划线，大括号，中括号，小括号，长度范围1-50字符
                pattern = "^[\\u4e00-\\u9fa5_\\-\\(\\)\\[\\]\\{\\}【】（）0-9]{1,50}$";
                if(!Pattern.matches(pattern, arr[1])){
                    logger.info("\npattern:" + pattern+"\ncontent:"+arr[1]);
                    errorCells.add(i+1+"B");
                }
                //国标文件编号：/^[\a-zA-Z0-9_\-\s\\.\\\\/]{0,20}$/ 字母，数字，下划线，中划线，点，左右斜线，长度范围0-20字符
                pattern = "^[a-zA-Z0-9_\\-\\.\\\\\\/]{0,20}$";
                if(!Pattern.matches(pattern, arr[2])){
                    logger.info("\npattern:" + pattern+"\ncontent:"+arr[2]);
                    errorCells.add(i+1+"C");
                }
                //国标编码：^[\a-zA-Z0-9_\-]{0,20}$/ 字母，数字，下划线，中划线，长度范围0-20字符
                pattern = "^[a-zA-Z0-9_\\-]{0,20}$";
                if(!Pattern.matches(pattern, arr[3])){

                    logger.info("\npattern:" + pattern+"\ncontent:"+arr[3]);
                    errorCells.add(i+1+"D");
                }

                stringMap.put(arr[0], str);
                countLine ++;
            }
        }

        //数据格式验证
        if(errorCells.size() > 0){
            StringBuffer sb = new StringBuffer();
            sb.append("以下单元格数据输入不符合要求：");
            for(String ic : errorCells) {
                sb.append(ic + " ");
            }
            AppException appException = new AppException(sb.toString(), ErrorCode.BAD_REQUEST);
            appException.errorData = errorCells;
            throw appException;
        }

        //logger.info("文件内容 stringMap：" + stringMap);
        //检验文件内容是否在关键内容中出现重复
        if(stringMap.size() != countLine) {
            throw new AppException("上传的业务词条内容出现重复,请检查文件内容", ErrorCode.BAD_REQUEST);
        }


        //检查数据库中是否已经存在文件中的数据元标准内容
        Set<String> existSet = new HashSet<String>();
        for(String line : stringMap.values()) {
           String[] arr = line.split(splitor);

            logger.info("key value : " + arr[0]);
            for(int i=0;i<2;i++){  //当不规则格式中存在空值时抛出该异常

                if(StringUtils.isEmpty(arr[i])){
                    throw new AppException("请参考数据元标准模板格式, 填写相关内容.", ErrorCode.BAD_REQUEST);
                }
            }

            if (checkExistIdentifierOrChName(arr[0])) {
                existSet.add("[" + arr[0] + "]" + " " + arr[1]);
            }
        }
        if(existSet.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("以下业务词条已存在：");
            for(String ic : existSet) {
                sb.append(ic);
                sb.append("  ");
            }
            throw new AppException(sb.toString(), ErrorCode.CONFLICT);
        }

        return stringMap;
    }
}
