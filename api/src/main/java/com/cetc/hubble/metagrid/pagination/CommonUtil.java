package com.cetc.hubble.metagrid.pagination;


import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Created by ben on 17-5-26.
 */
public class CommonUtil {
    public static final String EXECL = ".xls";

    private static final String CSV = ".csv";

    /** 分割符,ASCII码:分组符 */
    public static final String SPLITOR = "\035";

    /**
     * 通过文件名自动生成新的唯一的文件名称
     *
     * @param name
     * @return
     */
    public static String bulidFileName(String name) {
        Random random = new Random();
        int s = random.nextInt(100) % 100 + 1;
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer sb = new StringBuffer(name);
        sb.append("_").append(myFmt.format(new Date())).append("_").append(s);
        return sb.toString();
    }

    /**
     * 根据文件名，上传路径等，返回上传成功后的文件
     *
     * @param uploadPath
     * @param file
     * @param name
     * @return
     * @throws IOException
     */
    public static File uploadFile(String uploadPath, MultipartFile file, String name) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        if (!EXECL.equalsIgnoreCase(suffixName)) {
            throw new AppException("数据元标准文件格式不匹配,只能上传.xls 后缀的execl文件", ErrorCode.BAD_REQUEST);
        }
        fileName = CommonUtil.bulidFileName(name) + suffixName;
        File dest = new File(uploadPath + "/" + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        //文件保存
        file.transferTo(dest);

        return dest;
    }

    /**
     * csv文件
     * @param uploadPath
     * @param file
     * @param name
     * @return
     * @throws IOException
     */
    public static File uploadCSVFile(String uploadPath, MultipartFile file, String name) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        if (!CSV.equalsIgnoreCase(suffixName)) {
            throw new AppException("数据体标准文件格式不匹配,只能上传.csv 后缀的csv文件", ErrorCode.BAD_REQUEST);
        }
        fileName = CommonUtil.bulidFileName(name) + suffixName;
        File dest = new File(uploadPath + "/" + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        //文件保存
        file.transferTo(dest);

        return dest;
    }

    public static List<String> readFile(String path,String splitor) throws IOException, InvalidFormatException {
        FileInputStream is = new FileInputStream(path);
        try {
            Workbook workbook = WorkbookFactory.create(is);
            List<String> list = new ArrayList<>();
            int sheetCount = workbook.getNumberOfSheets();  //Sheet的数量
            StringBuffer sb;
            //遍历每个Sheet
            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
                //遍历每一行,不需要遍历标题内容，因此行数从1开始
                for (int r = 0; r < rowCount; r++) {
                    sb = new StringBuffer();
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        list = null;
                        break;//如row为空,则结束循环，且清空list的数据
                    }

                    int cellCount = row.getPhysicalNumberOfCells(); //获取总列数


                    if (row.getCell(0) != null) {
                        row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
                        sb.append(row.getCell(0).getStringCellValue()).append(splitor);
                    } else {
                        throw new AppException("请参考数据元标准模板格式, 填写相关内容.", ErrorCode.BAD_REQUEST);
                    }

                    if (row.getCell(1) != null) {
                        row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
                        sb.append(row.getCell(1).getStringCellValue()).append(splitor);
                    } else {
                        throw new AppException("请参考数据元标准模板格式, 填写相关内容.", ErrorCode.BAD_REQUEST);
                    }

                    if (row.getCell(2) != null) {
                        row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
                        sb.append(row.getCell(2).getStringCellValue()).append(splitor);
                    } else {
                        sb.append(splitor);
                    }

                    if (row.getCell(3) != null) {
                        row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
                        sb.append(row.getCell(3).getStringCellValue());
                    } else {
                        sb.append(splitor);
                    }

                    list.add(sb.toString());
                }
            }
            return list;
        }finally {
            is.close();
        }
    }


    public static void downloadFile(String downloadPath, String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Resource resource = new ClassPathResource(fileName);
        // File downloadFile = resource.getFile();
        File downloadFile = new File(downloadPath, fileName);
        ServletContext context = request.getServletContext();

        // get MIME type of the file
        String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/vnd.ms-execl";
        }

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);

        // Copy the stream to the response's output stream.
        InputStream myStream = new FileInputStream(downloadFile.getAbsolutePath());
//        FileInputStream fis = new FileInputStream(downloadFile.getAbsolutePath());
//        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//        BufferedReader br = new BufferedReader(isr);
        OutputStream os = response.getOutputStream();
//        //os.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        IOUtils.copy(myStream, os);
//        String line = br.readLine();
//        while(!Strings.isNullOrEmpty(line)) {
//            os.write(line.getBytes("UTF-8"));
//            line = br.readLine();
//        }
        response.flushBuffer();
    }
}
