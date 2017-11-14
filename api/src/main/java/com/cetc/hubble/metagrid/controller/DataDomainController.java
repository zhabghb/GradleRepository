package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.controller.support.ResponseEntity;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.DataDomainService;
import com.cetc.hubble.metagrid.service.DataServiceService;
import com.cetc.hubble.metagrid.vo.DataDomain;
import com.cetc.hubble.metagrid.vo.DomainAttr;
import com.cetc.hubble.metagrid.vo.DomainDataset;
import com.cetc.hubble.metagrid.vo.StdIdentifier;
import com.chinacloud.oneaa.common.entity.UserInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/5/24.
 */
@RestController
@RequestMapping(value = "/v2/api/dataDomain/")
@Api(value = "/api", description = "数据资源集合API列表")
public class DataDomainController extends  BaseController{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataDomainService dataDomainService;

    @Autowired
    private DataServiceService dataServiceService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据资源集合列表")
    public Map<String, Object> getAllDataDomains(@RequestParam(required = false,defaultValue = "1") @ApiParam(value = "页数", required = false)int page,
                                                 @RequestParam(required = false,defaultValue = "10") @ApiParam(value = "每页显示条数", required = false)int limit,
                                                 @RequestParam(required = false,defaultValue = "") @ApiParam(value = "类型：原始数据origin、资源集合数据domain、服务数据service", required = true)String type,
                                                 @RequestParam(required = false,defaultValue = "") @ApiParam(value = "搜索关键字", required = false)String keyword){
        logger.info("request to get paged DataDomains by page:{},limit:{},keyword:{}",page,limit,keyword);
        try {
           return dataDomainService.listPagedDataDomains(page,limit,keyword,type);
        } catch (Exception e) {
            logger.error("获取数据资源集合列表出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据資源集合列表错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{domainId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据资源集合")
    public DataDomain get(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId){
        logger.info("request to get  DataDomain by id:{}",domainId);
        try {
           return dataDomainService.getById(domainId);
        } catch (Exception e) {
            logger.error("获取数据资源集合出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据資源集合错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据资源集合")
    public Map save(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) DataDomain dataDomain){
        logger.info("request to save DataDomain:{}",dataDomain);
        try {
            int domainId = dataDomainService.save(dataDomain);
            HashMap<String, Object> res = Maps.newHashMap();
            res.put("domainId", domainId);
            return res;
        }catch (DuplicateKeyException e1) {
            e1.printStackTrace();
            throw new AppException("数据资源集合名称不能重复!", ErrorCode.CONFLICT);
        } catch (Exception e) {
            logger.error("新增数据资源集合出错：", e);
            e.printStackTrace();
            throw new AppException("新增数据資源集合错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{domainId}", method = RequestMethod.PUT)
    @ApiOperation(value = "根据ID更新数据资源集合")
    public void update(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId,@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) DataDomain dataDomain){
        logger.info("request to update DataDomain:{} and id:{}",dataDomain,domainId);
        try {
            dataDomainService.update(domainId,dataDomain);
        } catch (DuplicateKeyException e1) {
            e1.printStackTrace();
            throw new AppException("数据资源集合名称不能重复!", ErrorCode.CONFLICT);
        }catch (Exception e) {
            logger.error("更新数据资源集合出错：", e);
            e.printStackTrace();
            throw new AppException("更新数据资源集合错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{domainId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ID删除数据资源集合")
    public void delete(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId){
        logger.info("request to delete DataDomain by id:{}",domainId);
        try {
            dataDomainService.delete(domainId);
        } catch (Exception e) {
            logger.error("删除数据资源集合出错：", e);
            e.printStackTrace();
            throw new AppException("删除数据資源集合错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}/attrs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据资源集合属性集")
    public ResponseEntity getDomainAttrs(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId){
        logger.info("request to get DataDomain Attrs  by id:{}",domainId);
        try {
            List<DomainAttr> attrs = dataDomainService.getDomainAttrs(domainId);
            return new ResponseEntity(attrs);
        } catch (Exception e) {
            logger.error("根据ID获取数据资源集合属性集失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID获取数据資源集合属性集错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}/attrs", method = RequestMethod.PUT)
    @ApiOperation(value = "根据ID更新数据资源集合属性集")
    public void saveUpdateDomainAttrs(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId,@RequestBody @ApiParam(value = "数据资源集合属性集", required = true)List<DomainAttr> attrs){
        logger.info("request to save or update DataDomain Attrs  by id:{}",domainId);
        try {
            dataDomainService.saveUpdateDomainAttrs(domainId,attrs);
        } catch (DuplicateKeyException e1) {
            e1.printStackTrace();
            throw new AppException("数据資源集合自定义属性键不能重复!", ErrorCode.CONFLICT);
        } catch (Exception e) {
            logger.error("根据ID更新数据资源集合属性集失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID更新数据資源集合属性集错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}/entities", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据资源集合关联表")
    public ResponseEntity getDomainEntities(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId){
        logger.info("request to get DataDomain Attrs  by id:{}",domainId);
        try {
            List<DomainDataset> entities = dataDomainService.getDomainEntities(domainId);
            return new ResponseEntity(entities);
        } catch (Exception e) {
            logger.error("根据ID获取数据资源集合关联表失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID获取数据资源集合关联表错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}/entities", method = RequestMethod.PUT)
    @ApiOperation(value = "根据ID更新数据资源集合关联表")
    public void saveUpdateDomainEntities(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId,@RequestBody @ApiParam(value = "数据资源集合实体ID集", required = true)List<Long> entityIds){
        logger.info("request to save or update DataDomain Entities  by id:{}",domainId);
        try {
            dataDomainService.saveUpdateDomainEntities(domainId,entityIds);
        } catch (DuplicateKeyException e1) {
            e1.printStackTrace();
            throw new AppException("数据资源集合关联表不能重复!", ErrorCode.CONFLICT);
        } catch (Exception e) {
            logger.error("根据ID更新数据资源集合关联表失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID更新数据资源集合关联表错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{domainId}/identifiers", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据资源集合元标准集")
    public ResponseEntity getDomainIdentifiers(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId){
        logger.info("request to get DataDomain Identifiers  by id:{}",domainId);
        try {
            List<StdIdentifier> identifiers = dataDomainService.getDomainIdentifiers(domainId);
            return new ResponseEntity(identifiers);
        } catch (Exception e) {
            logger.error("根据ID获取数据资源集合元标准集失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID获取数据资源集合元标准集错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{domainId}/identifiers", method = RequestMethod.PUT)
    @ApiOperation(value = "根据ID更新数据资源集合元标准集")
    public void saveUpdateDomainIdentifiers(@PathVariable  @ApiParam(value = "数据资源集合ID", required = true)Integer domainId,@RequestBody @ApiParam(value = "数据资源集合元标准ID集", required = true)List<Long> identifierIds){
        logger.info("request to save or update DataDomain Identifiers  by id:{}",domainId);
        try {
            dataDomainService.saveUpdateDomainIdentifiers(domainId,identifierIds);
        } catch (DuplicateKeyException e1) {
            e1.printStackTrace();
            throw new AppException("业务词条不能重复添加!", ErrorCode.CONFLICT);
        } catch (Exception e) {
            logger.error("根据ID更新数据资源集合元标准集失败：", e);
            e.printStackTrace();
            throw new AppException("根据ID更新数据资源集合元标准集错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/testDataDomains", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据资源集合列表测试接口")
    public Map<String, Object> getAllDataDomainsTest(@RequestParam(required = false,defaultValue = "1") @ApiParam(value = "页数", required = false)int page,
                                                 @RequestParam(required = false,defaultValue = "10") @ApiParam(value = "每页显示条数", required = false)int limit,
                                                 @RequestParam(required = false,defaultValue = "") @ApiParam(value = "搜索关键字", required = false)String keyword){
        logger.info("request to get paged DataDomains by page:{},limit:{},keyword:{}",page,limit,keyword);
        try {
            return dataDomainService.listPagedDataDomainsTest(page,limit,keyword);
        } catch (Exception e) {
            logger.error("获取数据资源集合列表出错：", e);
            e.printStackTrace();
            throw new AppException("获取数据资源集合列表错误!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/{domainId}/dataservice",method = RequestMethod.POST)
    @ApiOperation(value = "根据资源集合推送数据服务")
    public HashMap<Object, Object> pushDataService(@PathVariable @ApiParam(value = "输入参数，domainId",required = true) Integer domainId){
        UserInfo userInfo=getUserInfo();
        String userId=userInfo.getId();
        String username=userInfo.getUsername();
//        String userId="7d3457e7-f7aa-4230-915d-3d68b1dca47e";
//        String username="liqiang@chinacloud.com.cn";
        HashMap<Object,Object> map = new HashMap<>();
        try {
            map = dataServiceService.pushDataService(domainId,userId,username);
        } catch (Exception e) {
            logger.error("创建数据服务出错：", e);
            e.printStackTrace();
            if (e instanceof EmptyResultDataAccessException) throw e;
            if (e instanceof AppException) throw e;
            throw new AppException("创建数据服务出错!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return map;

    }



}
