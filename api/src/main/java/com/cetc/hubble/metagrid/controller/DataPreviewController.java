package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.SqlService;
import com.cetc.hubble.metagrid.service.StructuredDataService;
import com.cetc.hubble.metagrid.vo.HBaseColFamilyResult;
import com.cetc.hubble.metagrid.vo.HBaseColResult;
import com.cetc.hubble.metagrid.vo.HBaseRowResult;
import io.swagger.annotations.*;
import metagrid.common.vo.PreviewParam;
import metagrid.common.vo.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/v2/api/dataPreview/")
@Api(value = "/api", description = "数据预览API列表")
public class DataPreviewController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StructuredDataService dataService;

    @RequestMapping(value = "/hbase", method = RequestMethod.POST)
    @ApiOperation(value = "hbase数据预览查询")
    public List<HBaseRowResult> executeQuery(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) PreviewParam param)
            throws Exception {
        try {
            List<HBaseRowResult> result = new ArrayList<HBaseRowResult>();
            // 最大的取出rowid的个数
            int maxRowNum = param.getLimit();
            QueryResult queryResult = dataService.preview(param);
            if (queryResult != null && queryResult.getResults() != null && !queryResult.getResults().isEmpty()) {
                for (List<Object> rowList : queryResult.getResults()) {
                    boolean isHaveColFamily = false;
                    boolean isHaveRow = false;
                    HBaseColResult hBaseColResult = new HBaseColResult();
                    hBaseColResult.setColName((String) rowList.get(2));
                    hBaseColResult.setColValue((String) rowList.get(4));

                    for (HBaseRowResult rowResult : result) {
                        if (rowResult.getRowId().equals(rowList.get(0))) {
                            isHaveRow = true;
                            for (HBaseColFamilyResult familyResult : rowResult.getColFamily()) {
                                if (familyResult.getName().equals(rowList.get(1))) {
                                    isHaveColFamily = true;
                                    familyResult.getDetail().add(hBaseColResult);
                                    break;
                                }
                            }
                            if (!isHaveColFamily) {
                                HBaseColFamilyResult hBaseColFamilyResult = new HBaseColFamilyResult();
                                hBaseColFamilyResult.setName((String) rowList.get(1));
                                hBaseColFamilyResult.getDetail().add(hBaseColResult);
                                rowResult.getColFamily().add(hBaseColFamilyResult);
                            }
                            break;
                        }
                    }
                    if (!isHaveRow) {
                        HBaseRowResult hBaseRowResult = new HBaseRowResult();
                        hBaseRowResult.setRowId((String) rowList.get(0));
                        HBaseColFamilyResult hBaseColFamilyResult = new HBaseColFamilyResult();
                        hBaseColFamilyResult.setName((String) rowList.get(1));
                        hBaseColFamilyResult.getDetail().add(hBaseColResult);
                        hBaseRowResult.getColFamily().add(hBaseColFamilyResult);
                        result.add(hBaseRowResult);
                    }

                    // 临时增加如果查询的rowid个数大于50了就抛弃掉后面的数据
                    if (result.size() > maxRowNum) {
                        break;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof AppException) throw e;
            throw new AppException(e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}


