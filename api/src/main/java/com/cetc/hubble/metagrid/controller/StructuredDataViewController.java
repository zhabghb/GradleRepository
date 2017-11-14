package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.SqlService;
import com.cetc.hubble.metagrid.service.StructuredDataService;
import com.cetc.hubble.metagrid.vo.*;
import com.cetc.hubble.metagrid.vo.Tag;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v2/api/structuredData")
@Api(value = "/api", description = "结构化数据展示")
public class StructuredDataViewController {

    private Logger logger = LoggerFactory.getLogger(StructuredDataViewController.class);
    @Autowired
    private StructuredDataService structuredDataService;
    @Autowired
    private SqlService sqlService;

    @RequestMapping(value = "{dataSetID}", method = RequestMethod.GET)
    @ApiOperation(value = "结构化数据元数据获取")
    @ApiResponses({@ApiResponse(code = 500, message = "数据集属性获取失败")})
    public StructuredDataSetMetaData getStructuredTableMeta(
            @PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID) {

        logger.info("Dataset meta data get: " + dataSetID);
        StructuredDataSetMetaData metaData;
        try {
            metaData = structuredDataService.getStructuredTableMeta(dataSetID);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("该表或类型已被删除!", ErrorCode.CUSTOM_EXCEPTION);
        } catch (Exception e) {
            logger.error("Failed to get data set: " + dataSetID + "'s tags.", e);
            throw new AppException("数据集属性获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return metaData;
    }

    @RequestMapping(value = "{dataSetID}/tags", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集标签")
    @ApiResponses({@ApiResponse(code = 500, message = "获取数据集标签失败")})
    public List<Tag> getDataSetTags(@PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID) {

        logger.info("Get data set tags: " + dataSetID);
        List<Tag> tags;
        try {
            tags = structuredDataService.getDataSetTags(dataSetID);
        } catch (Exception e) {
            logger.error("Failed to get data set: " + dataSetID + "'s tags.", e);
            throw new AppException("设置数据集标签失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return tags;
    }

    @RequestMapping(value = "{dataSetID}/tags", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集标签")
    @ApiResponses({
            @ApiResponse(code = 403, message = "数据集已被删除"),
            @ApiResponse(code = 409, message = "数据集或标签已失效"),
            @ApiResponse(code = 500, message = "设置数据集标签失败")
    })
    public void setDataSetTags(@PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID,
                               @RequestBody @ApiParam(value = "字段信息", required = true) List<Tag> tags) {

        logger.info("Set data set tags: " + dataSetID);
        try {
            structuredDataService.setDataSetTags(dataSetID, tags);
        }
//        catch (DataIntegrityViolationException ive) {
//            ive.printStackTrace();
//            logger.error("Failed to update data set: {}'s tag. {}", dataSetID + "", ive);
//            throw new AppException("修改失败:其中有标签已不存在", ErrorCode.BAD_REQUEST);
//        }
        catch (Exception e) {
            logger.error("Failed to update data set: {}'s tag cause of {}", dataSetID, e);
            if (e instanceof AppException) throw e;
            throw new AppException("设置数据集标签失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{dataSetID}/bias", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集别名")
    @ApiResponses({
            @ApiResponse(code = 403, message = "数据集已被删除"),
            @ApiResponse(code = 500, message = "设置数据集别名失败")
    })
    public void setDataSetAlias(@PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID,
                                @RequestBody @ApiParam(value = "字段信息", required = true) Map bias) {

        logger.info("Alter data set bias: " + bias + " for " + dataSetID);
        try {
            boolean res = structuredDataService.setDataSetAlias(dataSetID, (String) bias.get("bias"));
            if (!res) {
                throw new AppException("数据集已被删除", ErrorCode.CONFLICT);
            }
        } catch (Exception e) {
            logger.error("Failed to update data set's alias: {} cause of {}", dataSetID, e);
            if (e instanceof AppException) throw e;
            throw new AppException("设置数据集别名失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{dataSetID}/sample", method = RequestMethod.POST)
    @ApiOperation(value = "数据集样例获取")
    @ApiResponses({@ApiResponse(code = 500, message = "数据样例获取失败")})
    public StructuredDataSetSample getDataSample(
            @PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID,
            @RequestBody @ApiParam StructuredDataViewParam param) {

        logger.info("Data set sample get: " + dataSetID);
        StructuredDataSetSample dataSample;
        try {
            dataSample = structuredDataService.getDataSample(param,dataSetID);
        }catch (SQLException e1){
            String message = e1.getMessage();
            if(message.indexOf("Could not open client transport") > -1){

                throw new AppException("数据样例获取失败:不能连接到服务器", ErrorCode.INTERNAL_SERVER_ERROR);
            }else if(message.indexOf("Table not found") > -1){

                throw new AppException("数据样例获取失败:表不存在了，请重新同步元数据后尝试", ErrorCode.INTERNAL_SERVER_ERROR);
            }else if (message.indexOf("ORA-00904") > -1){
                throw new AppException("数据样例获取失败:字段已改变，请重新同步元数据后尝试", ErrorCode.INTERNAL_SERVER_ERROR);
            }else if (message.indexOf("ORA-00942") > -1){
                throw new AppException("数据样例获取失败:表或视图已不存在了，请重新同步元数据后尝试", ErrorCode.INTERNAL_SERVER_ERROR);
            }else {
                throw new AppException("数据样例获取失败<br>" + message, ErrorCode.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error("Failed to get data set sample: " + dataSetID, e);
            throw new AppException("数据样例获取失败<br>" + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return dataSample;
    }


    @RequestMapping(value = "{dataSetID}/fields", method = RequestMethod.GET)
    @ApiOperation(value = "字段信息获取")
    @ApiResponses({@ApiResponse(code = 500, message = "字段信息获取失败")})
    public StructuredDataSetColumnInfo getColumnInfo(
            @PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID) {

        logger.info("Dataset information get: ", dataSetID);
        StructuredDataSetColumnInfo columnInfo;
        try {
            columnInfo = structuredDataService.getColumnInfo(dataSetID);
        } catch (Exception e) {
            logger.error("Failed to get column information." + dataSetID, e);
            throw new AppException("字段信息获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return columnInfo;
    }

    @RequestMapping(value = "{dataSetID}/fields/{fieldID}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字段注释")
    @ApiResponses({
            @ApiResponse(code = 403, message = "数据集已被删除"),
            @ApiResponse(code = 500, message = "修改字段注释失败")
    })
    public void setFieldComment(@PathVariable @ApiParam(value = "数据集ID", required = true) Long dataSetID,
                                @PathVariable @ApiParam(value = "字段ID", required = true) Integer fieldID,
                                @RequestBody @ApiParam(value = "字段注释", required = true) Map comment) {

        logger.info("Alter column comment: ", dataSetID, fieldID, comment);
        try {
            boolean res = structuredDataService.setFieldComment(dataSetID, fieldID, (String) comment.get("comment"));
        } catch (Exception e) {
            if (e instanceof AppException) throw e;
            logger.error("Failed to update field's alias. {}'s {} {}", dataSetID, fieldID, e);
            throw new AppException("修改字段注释失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/dictionary", method = RequestMethod.POST)
    @ApiOperation(value = "字典记录列表查询")
    @ApiResponses({
            @ApiResponse(code = 500, message = "字典记录列表获取查询")
    })
    public Map<String, Object> getDictionaryEntries(
            @RequestBody @ApiParam(value = "字典查询参数", required = true) DictionaryParam param) {

        Map<String, Object> res;
        try {
            res = structuredDataService.getDictionaryEntries(param);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get dictionary entries. {}", e);
            throw new AppException("字典记录列表获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return res;
    }
    @RequestMapping(value = "/owner", method = RequestMethod.POST)
    @ApiOperation(value = "数据集资源拥有者信息查询")
    @ApiResponses({
            @ApiResponse(code = 500, message = "数据集资源拥有者信息查询")
    })
    public DatasetOwner getDatasetOwner(
            @RequestBody @ApiParam(value = "数据集资源拥有者查询参数", required = true) DataResource dataResource) {

        try {
            return structuredDataService.getDatasetOwnerByResouce(dataResource);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get dataset owner. {}", e);
            throw new AppException("数据集资源拥有者信息获取失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{datasetId}/owner", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集资源拥有者信息")
    @ApiResponses({
            @ApiResponse(code = 500, message = "新增数据集资源拥有者信息")
    })
    public void addDatasetOwner(
            @RequestBody @ApiParam(value = "数据集资源拥有者信息参数", required = true) DatasetOwner datasetOwner) {

        try {
             structuredDataService.addDatasetOwner(datasetOwner);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to add dataset owner. {}", e);
            throw new AppException("新增数据集资源拥有者信息失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "{dataSetID}/attr", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集属性")
    @ApiResponses({
            @ApiResponse(code = 400, message = "数据集属性名不能为空"),
            @ApiResponse(code = 409, message = "数据集属性名已经存在"),
            @ApiResponse(code = 500, message = "新增数据集属性失败")})
    public void saveDataSetAttr(
            @PathVariable @Valid @ApiParam(value = "数据集ID", required = true) Long dataSetID,
            @RequestBody @Valid @ApiParam DatasetAttr param, BindingResult result) {
        logger.info("Add dataset attr: ", dataSetID, param.getAttrName(), param.getAttrValue());
        try {
            param.setDatasetId(dataSetID);
            structuredDataService.saveDatasetAttr(param);
        } catch (Exception e) {
            logger.error("Failed to add dataset attr. {}", e);
            if(e instanceof AppException) throw e;
            throw new AppException("新增数据集属性失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "{dataSetID}/attr/{attrID}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集属性")
    @ApiResponses({
            @ApiResponse(code = 400, message = "数据集属性名不能为空"),
            @ApiResponse(code = 409, message = "数据集属性名已经存在"),
            @ApiResponse(code = 500, message = "修改数据集属性失败")})
    public void updateDataSetAttr(
            @PathVariable @Valid @ApiParam(value = "数据集ID", required = true) Long dataSetID,
            @PathVariable @Valid @ApiParam(value = "属性ID", required = true) Integer attrID,
            @RequestBody @Valid @ApiParam DatasetAttr param, BindingResult result) {
        logger.info("Update dataset attr: ", attrID, dataSetID, param.getAttrName(), param.getAttrValue());
        try {
            param.setDatasetId(dataSetID);
            param.setId(attrID);
            structuredDataService.updateDatasetAttr(param);
        } catch (Exception e) {
            logger.error("Failed to update dataset attr. {}", e);
            if(e instanceof AppException) throw e;
            throw new AppException("修改数据集属性失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "attr/{attrID}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集属性")
    @ApiResponses({
            @ApiResponse(code = 500, message = "删除数据集属性失败")})
    public void deleteDataSetAttr(
            @PathVariable @Valid @ApiParam(value = "属性ID", required = true) Integer attrID) {
        logger.info("Delete dataset attr: ", attrID);
        try {
            structuredDataService.deleteDatasetAttr(attrID);
        } catch (Exception e) {
            logger.error("Failed to delete dataset attr. {}", e);
            throw new AppException("删除数据集属性失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "{dataSetID}/attr", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集属性列表")
    @ApiResponses({@ApiResponse(code = 500, message = "查询数据集属性列表失败")})
    public Map<String, Object> getDataSetAttrList(
            @PathVariable @Valid @ApiParam(value = "数据集ID", required = true) Long dataSetID) {
        logger.info("Get dataset attr list : ", dataSetID);
        List<DatasetAttr> daList = null;
        Long count = 0l;
        try {
            daList =  structuredDataService.getDatasetAttrListByDataSetId(dataSetID);
            count = structuredDataService.getDatasetAttrCountByDataSetId(dataSetID);
        } catch (Exception e) {
            logger.error("Failed to get dataset attr list. {}", e);
            throw new AppException("查询数据集属性列表失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("datasetAttrList", daList);
        result.put("count", count);
        return result;
    }
}