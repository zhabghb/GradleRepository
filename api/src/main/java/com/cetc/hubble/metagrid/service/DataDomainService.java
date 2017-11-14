package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.Constant;
import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataDomainDAO;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import metagrid.common.utils.Json;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/5/24.
 */
@Service
public class DataDomainService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${whitehole.api.findMyServiceDetail}")
    private String findMyServiceDetailAPI;
    @Value("${metagrid.domainStatusCheck.auto}")
    private Boolean autoCheck;

    @Autowired
    private DataDomainDAO dataDomainDAO;
    @Autowired
    private DataServiceService dataServiceService;

    public void update(Integer dataDomainId,DataDomain dataDomain) {
        dataDomain.setLastModifyTime(new Date());
        dataDomainDAO.update(dataDomainId,dataDomain);
    }

    public int save(DataDomain dataDomain) {
        dataDomain.setCreateTime(new Date());
        return dataDomainDAO.insert(dataDomain);
    }

    public void delete(Integer domainId) {
        dataDomainDAO.delete(domainId);
    }

    @Transactional
    public void saveUpdateDomainEntities(Integer domainId, List<Long> entityIds) {
        dataDomainDAO.deleteDomainEntities(domainId);
        for (Long entityId :entityIds){
            dataDomainDAO.insertDomainEntity(domainId,entityId);
        }
        dataDomainDAO.updateDomainMofifyTime(domainId,new Date());
    }

    @Transactional
    public void saveUpdateDomainIdentifiers(Integer domainId, List<Long> identifierIds) {
        dataDomainDAO.deleteDomainIdentifiers(domainId);
        for (Long identifierId :identifierIds){
            dataDomainDAO.insertDomainIdentifier(domainId,identifierId);
        }
        dataDomainDAO.updateDomainMofifyTime(domainId,new Date());
    }

    @Transactional
    public void saveUpdateDomainAttrs(Integer domainId, List<DomainAttr> attrs) {
        dataDomainDAO.deleteDomainAttrs(domainId);
        for (DomainAttr attr :attrs){
            attr.setDomainId(domainId);
            dataDomainDAO.insertDomainAttr(attr);
        }
        dataDomainDAO.updateDomainMofifyTime(domainId,new Date());
    }

    public DataDomain getById(Integer domainId) {
        return dataDomainDAO.queryById(domainId);
    }

    public DataDomainExt getDataDomainExtById(Integer domainId) {
        return dataDomainDAO.queryDataDomainExtById(domainId);
    }

    public List<DomainAttr> getDomainAttrs(Integer domainId) {
        return dataDomainDAO.queryDomainAttrs(domainId);
    }

    public List<DomainDataset> getDomainEntities(Integer domainId) {
        return dataDomainDAO.queryDomainEntities(domainId);
    }
    public List<StdIdentifier> getDomainIdentifiers(Integer domainId) {
        return dataDomainDAO.queryDomainIdentifiers(domainId);
    }

    public Map<String,Object> listPagedDataDomains(int page, int limit, String keyword, String type ) {

        logger.info("开始检测已发布whitehole的数据资源集合的状态");
        List<DataDomainExt> domains =  dataDomainDAO.queryDueDomainsWithCondition(page,limit,keyword,type);
        if (!autoCheck){
            logger.info("数据資源集合待检测数量:size:{}",domains.size());
            if (CollectionUtils.isNotEmpty(domains) && domains.size()>0){
                checkDomainStatusByDomains(domains);
            }
        }

        return dataDomainDAO.queryPagedDataDomains(page,limit,keyword,type);
    }

    public Map<String,Object> listPagedDataDomainsTest(int page,int limit,String keyword) {
        return dataDomainDAO.queryPagedDataDomainsTest(page,limit,keyword);
    }

    public Integer checkWhServiceStatus(DataDomainExt domain)  {
        ObjectNode jsonNode = Json.newObject();
        jsonNode.put("serviceId",domain.getWhServiceId());
        try {
            String result = HttpUtil.doPostJsonHaveTimeout(jsonNode.toString(), findMyServiceDetailAPI);
            int status = Json.parse(result).get("serviceDetail").get("serviceStatus").asInt();
            //whitehole接口返回0-未发布，1-已发布，2-已上架，3-已删除
            return status;
        } catch (Exception e) {
            logger.error("调用whitehole接口:{}异常:{}",findMyServiceDetailAPI,e);
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkWhetherModified(DataDomainExt domain) {
        String lastestDomainImg = dataServiceService.buildDomainImg(domain.getId());
        boolean notModified = lastestDomainImg.equals(domain.getDomainImg());
        if (notModified){
            dataDomainDAO.updateDomainStatus(domain.getId(), Constant.DATA_DOMAIN_STATUS_PUSHED);
        }else {
            logger.warn("=====检测到已推送数据資源集合发生变化,资源集合:{}======",domain);
            dataDomainDAO.updateDomainStatus(domain.getId(), Constant.DATA_DOMAIN_STATUS_MODIFIED);
        }
        return !notModified;
    }

    public boolean checkDomainStatus(DataDomainExt domain) {
        boolean isModified = false;
        logger.info("=====正在检测数据資源集合:{}=====",domain);
        boolean isDeleted=false;
        Integer whServiceStatus=checkWhServiceStatus(domain);
        if(whServiceStatus!=null && whServiceStatus==3){
            isDeleted = true ;
        }
        if (isDeleted){
            logger.info("=====检测到已推送的数据資源集合在whitehole中被删除:{}=====",domain);
            handleDeleted(domain);
        }else {
            isModified = checkWhetherModified(domain);
        }
        return isModified;
    }

    public void checkDomainStatusByDomains(List<DataDomainExt> domains) {
        boolean whInterfaceIsAvailable=true;
        for (DataDomainExt domain :domains){
            boolean isDeleted=false;
            Integer whServiceStatus=checkWhServiceStatus(domain);
            if(whServiceStatus==null){
                whInterfaceIsAvailable=false;
                break;
            }else {
                if(whServiceStatus==3){
                    isDeleted = true ;
                }
            }
            if (isDeleted){
                logger.info("=====检测到已推送的数据资源集合在whitehole中被删除:{}=====",domain);
                handleDeleted(domain);
            }else {
                checkWhetherModified(domain);
            }
        }
        //当wh查询状态接口调不通时，只检查metargrid中数据资源集合的状态
        if (!whInterfaceIsAvailable){
            for (DataDomainExt domain :domains){
                checkWhetherModified(domain);
            }

        }

    }

    @Transactional
    private void handleDeleted(DataDomainExt domain) {
        dataDomainDAO.deleteDomainService(domain.getId());
        dataDomainDAO.updateDomainStatus(domain.getId(), Constant.DATA_DOMAIN_STATUS_UNPUSHED);
    }


}
