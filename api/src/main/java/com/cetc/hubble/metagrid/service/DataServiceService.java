package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.support.Constant;
import com.cetc.hubble.metagrid.controller.support.HttpUtil;
import com.cetc.hubble.metagrid.dao.DataDomainDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import metagrid.common.utils.Json;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by chenyanqiu on 2017/7/10.
 * 数据服务service
 */
@Service
public class DataServiceService {

    private static Logger logger = LoggerFactory.getLogger(DataServiceService.class);

    @Value("${whitehole.api.findDataServiceVersionUrl}")
    private String findDataServiceVersionUrl;
    @Value("${whitehole.api.saveBasiceInfoUrl}")
    private String saveBasiceInfoUrl;
    @Value("${whitehole.api.saveDataSourceUrl}")
    private String saveDataSourceUrl;
    @Value("${whitehole.api.saveSpecificationsUrl}")
    private String saveSpecificationsUrl;
    @Value("${whitehole.api.findEditionUrl}")
    private String findEditionUrl;
    @Value("${whitehole.api.saveAccessParameterUrl}")
    private String saveAccessParameterUrl;
    @Value("${metagrid.api.notificationUrl}")
    private String notificationUrl;
    @Value("${whitehole.api.getServiceAndServiceImgUrl}")
    private String getServiceAndServiceImgUrl;
    @Value("${whitehole.api.deleteServiceUrl}")
    private String deleteServiceUrl;
    @Value("${whitehole.api.insertServiceCategoryUrl}")
    private String insertServiceCategoryUrl;
    @Autowired
    private DataDomainService dataDomainService;

    @Autowired
    private StructuredDataService structuredDataService;

    @Autowired
    private DataDomainDAO dataDomainDAO;

    public HashMap<Object, Object> pushDataService(Integer domainId, String userId, String username) {
        try {
            DataDomain dataDomain = dataDomainDAO.queryById(domainId);
        } catch (EmptyResultDataAccessException erd) {
            throw new AppException("数据资源集合已不存在，请刷新列表", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        HashMap<Object, Object> result = Maps.newHashMap();
        DataDomainExt domain = dataDomainService.getDataDomainExtById(domainId);
        boolean unPushed = Constant.DATA_DOMAIN_STATUS_UNPUSHED.equals(domain.getStatus());
        if (!unPushed) {
            boolean isModified = dataDomainService.checkDomainStatus(domain);
            if (isModified) {
                Integer whServiceStatus = dataDomainService.checkWhServiceStatus(domain);
                if (whServiceStatus == 1 || whServiceStatus == 2) {
                    logger.info("服务已被锁定", domain);
                    result.put("action", "LOCK");
                    result.put("serviceId", domain.getWhServiceId());
                    result.put("status", "false");
                } else {
                    String serviceId = updateDataService(domainId, userId, username);
                    logger.info("=====成功更新已推送数据服务:{}===", domain);
                    result.put("action", "UPDATE");
                    result.put("serviceId", serviceId);
                    result.put("status", "true");
                }
            } else {
                logger.info("=====检测到已推送服务未更新，放弃推送:{}===", domain);
                result.put("action", "ABORT");
                result.put("serviceId", domain.getWhServiceId());
                result.put("status", "false");
            }
        } else {
            String serviceId = createDataService(domainId, userId, username);
            logger.info("=====成功注册新数据服务:{}===", domain);
            result.put("action", "CREATE");
            result.put("serviceId", serviceId);
            result.put("status", "true");
        }
        return result;
    }

    private String createDataService(Integer domainId, String userId, String username) {

        String result1 = this.saveBasicInfo(domainId, userId, username);
        JsonNode jsonNode = Json.parse(result1);
        if (Json.parse(result1).get("status").asInt() != 200) {
            String message=jsonNode.get("message").asText();
            if (jsonNode.get("errorCode").asText().equals("10-007-1099")) {
                throw new AppException(message, ErrorCode.BAD_REQUEST);
            }
            throw new AppException("创建数据服务基本信息出错："+message, ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String serviceId = jsonNode.get("data").get("serviceId").asText();
        
        String result2 = this.saveDataSource(domainId, serviceId);
        JsonNode jsonNode2 = Json.parse(result2);
        if (jsonNode2.get("status").asInt() != 200) {
//            String message=jsonNode2.get("message").asText();
            this.deleteWhService(serviceId);
            throw new AppException("创建数据服务资源出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        long editionNumber = this.getFindEdition(serviceId);

        String result3 = this.saveSpecifications(false,editionNumber, serviceId, domainId);
        int result3_status = Json.parse(result3).get("status").asInt();
        if (result3_status != 200) {
            this.deleteWhService(serviceId);
            throw new AppException("创建服务规格出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String result4 = this.saveAccessParameter(editionNumber, serviceId);
        int result4_status = Json.parse(result4).get("status").asInt();
        if (result4_status != 200) {
            this.deleteWhService(serviceId);
            throw new AppException("创建接入参数出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        this.createDomainService(domainId, serviceId);
        return serviceId;
    }

    /**
     * @return
     * @description 查找数据服务版本号
     */
    public String findDataServiceVersion() {
        String result = "";
        try {
            result = HttpUtil.doGet(findDataServiceVersionUrl);
        } catch (Exception e) {
            logger.error("查找数据服务版本号异常：" + e.getMessage());
            e.printStackTrace();
        }
        String version_time = "";
        if (result != null) {
            Iterator<JsonNode> iterator = Json.parse(result).get("data").iterator();
            while (iterator.hasNext()) {
                com.fasterxml.jackson.databind.node.ObjectNode objectNode = (ObjectNode) iterator.next();
                if (objectNode.get("name").asText().equals("数据服务")) {
                    version_time = objectNode.get("categoryId").asText() + "_" + objectNode.get("lastModified");
                }
            }
        }
        return version_time;

    }

    public void insertServiceCategory() {
        logger.info("开始创建'数据服务'类别");
        String result = "";
        Map<String, Object> map = new HashedMap();
        map.put("categoryName", "数据服务");
        String param = Json.toJson(map).toString();
        try {
            result = HttpUtil.doPostJson(param, insertServiceCategoryUrl);
        } catch (Exception e) {
            logger.error("添加数据服务类别异常：" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("创建'数据服务'类别结果是:" + result);
    }

    /**
     * @param domainId 资源集合Id
     * @return
     * @description 保存数据服务基本信息
     */
    public String saveBasicInfo(Integer domainId, String userId, String username) {
        logger.info("开始保存服务基本信息" + domainId);
        String result = "";
        DataDomain dataDomain = dataDomainService.getById(domainId);
        DataServiceBasicInfo dataServiceBasicInfoVo = new DataServiceBasicInfo();
        dataServiceBasicInfoVo.setServiceName(dataDomain.getName());
        String desc = dataDomain.getDescription();
        if (dataDomain.getDescription() == null || dataDomain.getDescription().equals("")) {
            desc = dataDomain.getName();
        }
        dataServiceBasicInfoVo.setServiceDescription(desc);
        dataServiceBasicInfoVo.setCreatorId(userId);
        dataServiceBasicInfoVo.setProvider(username);
        List<String> categoryIds = new ArrayList<String>();
        String serviceVersion = "";
        serviceVersion = this.findDataServiceVersion();
        //如果没有服务类别为‘数据服务’的，则添加一个
//        if (serviceVersion == null || serviceVersion == "") {
//            this.insertServiceCategory();
//            serviceVersion = this.findDataServiceVersion();
//        }
        categoryIds.add(serviceVersion);
        dataServiceBasicInfoVo.setServiceCategoryIds(categoryIds);
        JsonNode jsonNode = Json.toJson(dataServiceBasicInfoVo);
        logger.info("保存服务基本信息json是：" + jsonNode);
        try {

            result = HttpUtil.doPostJson(jsonNode.toString(), saveBasiceInfoUrl);
        } catch (Exception e) {
            logger.error("保存基本信息异常" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("保存服务基本信息结果是：" + result);
        return result;

    }

    /**
     * @param dataSetID
     * @return
     * @description 根据表Id得到表信息
     */
    public String getTableInfo(long dataSetID) {
        StructuredDataSetMetaData dataSetMetaData = structuredDataService.getStructuredTableMeta(dataSetID);
        String name = dataSetMetaData.getName();
        String dataSourceName = "" + dataSetMetaData.getDataSourceName();
        long columnNum = dataSetMetaData.getColumnNum();
        String type = dataSetMetaData.getType();
        String alias = dataSetMetaData.getBiasName();
        String aliasInfo = StringUtils.isNotBlank(dataSetMetaData.getBiasName()) ? "；别名：" + dataSetMetaData.getBiasName() : "";
        List<Tag> tagList = dataSetMetaData.getTags();
        String tags = "";
        if (tagList.size() > 0) {
            for (int i = 0; i < tagList.size(); i++) {
                String tag1 = tagList.size() - 1 == i ? tagList.get(i).getTagName() : tagList.get(i).getTagName() + "，";
                tags = tags + tag1;
            }
        }
        String tagInfo = tags.equals("") ? "" : "；标签：" + tags;

        String info = "表名称：" + name + aliasInfo + "；数据源：" + dataSourceName + "；类型：" + type + "；字段个数：" + columnNum + tagInfo;
        return info;
    }

    /**
     * @param domainId  资源集合Id
     * @param serviceId 创建的服务Id
     * @return
     * @description 保存数据资源
     */
    public String saveDataSource(Integer domainId, String serviceId) {
        logger.info("开始创建数据资源：资源集合Id=" + domainId + ",服务Id=" + serviceId);
        String result = postDataSource(domainId, serviceId);

        logger.info("创建数据资源结果是：" + result);
        return result;
    }

    private String postDataSource(Integer domainId, String serviceId) {

        String result = "";

        //根据数据资源集合id查询关联的表
        List<DomainDataset> entities = dataDomainService.getDomainEntities(domainId);
        //建立一个数据资源json
        DataServiceDataReSource dataReSource = new DataServiceDataReSource();
        dataReSource.setServiceId(serviceId);
        List<Feature> features = new ArrayList<Feature>();
        int order = 0;
        if (CollectionUtils.isNotEmpty(entities)) {
            for (DomainDataset domainDataset : entities) {
                //得到数据表所属的数据源名称
                StructuredDataSetMetaData dataSetMetaData = structuredDataService.getStructuredTableMeta(domainDataset.getId().longValue());
                String dataSourceName = dataSetMetaData.getDataSourceName();
                String parentName = dataSetMetaData.getParentName();
                order++;
                Feature feature = new Feature();
                feature.setFeatureId(UUID.randomUUID().toString());
                String tableName = dataSourceName+"->"+domainDataset.getName();
                if(StringUtils.isNotBlank(parentName)){
                    tableName = dataSourceName+"->"+parentName+"->"+domainDataset.getName();
                }
                String featureTitle = tableName.length() < 40 ? tableName : tableName.substring(0, 30) + "..." + tableName.substring(tableName.length() - 3, tableName.length());
                feature.setFeatureTitle(featureTitle);
                String desc = getTableInfo(domainDataset.getId());
                feature.setFeatureDescription(desc);
                feature.setOrder(order);
                //根据表Id得到表字段
                StructuredDataSetColumnInfo setColumnInfo = structuredDataService.getColumnInfo((long) domainDataset.getId());
                List<StructuredDataSetColumn> setColumns = setColumnInfo.getColumns();
                //建立featureCharacteristicses的Json
                List<FeatureCharacteristics> featureCharacteristicses = new ArrayList<>();

                //数据集Id
                FeatureCharacteristics featureCharacter1 = new FeatureCharacteristics();
                featureCharacter1.setCharacteristicId(UUID.randomUUID().toString());
                featureCharacter1.setCharacteristicsTitle("数据集ID");
                featureCharacter1.setOrder(1);
                Map<String, Object> mapDataSetId = new HashedMap();
                mapDataSetId.put("characteristicItemId", UUID.randomUUID().toString());
                mapDataSetId.put("description", domainDataset.getId());
                List<Map<String, Object>> listSetId = new ArrayList<>();
                listSetId.add(mapDataSetId);
                featureCharacter1.setCharacteristicsDescriptions(listSetId);
                JsonNode jsonNode = Json.toJson(featureCharacter1);

                //表结构
                FeatureCharacteristics featureCharacter2 = new FeatureCharacteristics();
                featureCharacter2.setCharacteristicId(UUID.randomUUID().toString());
                featureCharacter2.setCharacteristicsTitle("表结构");
                featureCharacter2.setOrder(2);
                Map<String, Object> mapTableSt = new HashedMap();
                mapTableSt.put("characteristicItemId", UUID.randomUUID().toString());
                String dataStructure = "";
                if (CollectionUtils.isNotEmpty(setColumns)) {
                    for (StructuredDataSetColumn setColumn : setColumns) {
                        String name = setColumn.getName();
                        String type = setColumn.getType();
                        String comment = setColumn.getComment() != null ? "," + setColumn.getComment() : "";
                        String columnInfo = name + ":(" + type + comment + ")   ";

                        dataStructure = dataStructure + columnInfo;
                    }
                }
                mapTableSt.put("description", dataStructure);
                List<Map<String, Object>> listTableSt = new ArrayList<>();
                listTableSt.add(mapTableSt);
                featureCharacter2.setCharacteristicsDescriptions(listTableSt);

                featureCharacteristicses.add(featureCharacter1);
                featureCharacteristicses.add(featureCharacter2);
                feature.setFeatureCharacteristics(featureCharacteristicses);
                features.add(feature);

            }
        }

        dataReSource.setFeatures(features);
        JsonNode jsonNode = Json.toJson(dataReSource);
        logger.info("生成的数据资源Json是" + jsonNode);
        try {
            result = HttpUtil.doPostJson(jsonNode.toString(), saveDataSourceUrl);
        } catch (Exception e) {
            logger.info("生成的数据资源出现异常：" + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param domainId
     * @param serviceId
     * @description 创建DomainService
     */
    @Transactional
    public int createDomainService(Integer domainId, String serviceId) {
        DomainService domainService = buildDomainService(domainId, serviceId);
        int result = dataDomainDAO.insertDomainService(domainService);
        dataDomainDAO.updateDomainStatus(domainId, Constant.DATA_DOMAIN_STATUS_PUSHED);
        return result;

    }

    public DomainService buildDomainService(Integer domainId, String serviceId) {
        DomainService domainService = new DomainService();
        domainService.setDomainID(domainId);
        domainService.setServiceId(serviceId);
        domainService.setVersion("1");
        //创建domainImg json
        String domainImg = buildDomainImg(domainId);

        domainService.setDomainImg(domainImg);
        return domainService;
    }

    public String updateDataService(Integer domainId, String userId, String username) {

        String result = "";
        boolean updateBasicInfo = false;

        DomainService domainService = dataDomainDAO.queryDomainServiceByDomainId(domainId);
        if (!domainService.getServiceId().isEmpty()) {

            if (!domainService.getDomainImg().isEmpty()) {
                String domainServiceName = Json.parse(domainService.getDomainImg()).get("name").asText();
                String domainDescription = Json.parse(domainService.getDomainImg()).get("description").asText();

                DataDomain dataDomain = dataDomainDAO.queryById(domainId);

                //update service name
                if (!domainServiceName.equals(dataDomain.getName()) || !domainDescription.equals(dataDomain.getDescription())) {
                    result = updateBasicInfo(domainId, userId, username, domainService.getServiceId());
                    JsonNode jsonNode = Json.parse(result);
                    if (jsonNode.get("status").asInt() == 200 && jsonNode.get("data").get("result").asBoolean()) {
                        updateBasicInfo = true;
                    }
                } else {
                    updateBasicInfo = true;
                }

                //update data source
                if (updateBasicInfo) {
                    result = updateDataSource(domainId, domainService.getServiceId(), domainService.getVersion());

                    //update Specifications
                    String serviceId = domainService.getServiceId();
                    long editionNumber = this.getFindEdition(serviceId);
                    String result3 = this.saveSpecifications(true,editionNumber, serviceId, domainId);

                }


            }
        }
        return domainService.getServiceId();
    }

//    public void updateSpecifications();

    public String updateBasicInfo(Integer domainId, String userId, String username, String serviceId) {
        logger.info("开始更新服务基本信息" + domainId);
        logger.info("开始查询服务详情" + serviceId);
        String result = "";
        try {
            String strJson = "{\"serviceId\": \"" + serviceId + "\"}";
            result = HttpUtil.doPostJson(strJson, getServiceAndServiceImgUrl);

            String overviewVersion = Json.parse(result).get("data").get("overviewDto").get("overviewVersion").asText();

            DataDomain dataDomain = dataDomainService.getById(domainId);
            DataServiceBasicInfo dataServiceBasicInfoVo = new DataServiceBasicInfo();
            dataServiceBasicInfoVo.setServiceName(dataDomain.getName());
            String desc = dataDomain.getDescription();
            if (dataDomain.getDescription() == null || dataDomain.getDescription().equals("")) {
                desc = dataDomain.getName();
            }
            dataServiceBasicInfoVo.setServiceDescription(desc);
            dataServiceBasicInfoVo.setCreatorId(userId);
            dataServiceBasicInfoVo.setProvider(username);
            dataServiceBasicInfoVo.setOverviewVersion(overviewVersion);
            dataServiceBasicInfoVo.setServiceId(serviceId);
            List<String> categoryIds = new ArrayList<String>();
            categoryIds.add(this.findDataServiceVersion());
            dataServiceBasicInfoVo.setServiceCategoryIds(categoryIds);
            JsonNode jsonNode = Json.toJson(dataServiceBasicInfoVo);
            logger.info("更新服务基本信息json是：" + jsonNode);
            try {

                result = HttpUtil.doPostJson(jsonNode.toString(), saveBasiceInfoUrl);
            } catch (Exception e) {
                logger.error("更新基本信息异常" + e.getMessage());
                e.printStackTrace();
            }
            logger.info("更新服务基本信息结果是：" + domainId);

        } catch (Exception e) {
            logger.error("查询服务详情异常" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("更新服务基本信息结果是：" + result);
        return result;

    }

    public String updateDataSource(Integer domainId, String serviceId, String currentVersion) {
        logger.info("开始更新数据资源：资源集合Id=" + domainId + ",服务Id=" + serviceId);
        String result = postDataSource(domainId, serviceId);
        logger.info("更新数据资源结果是：" + result);
        //更新数据资源成功后在metagrid更新domainService version
        Boolean havaDataSource = false;
        int result_status = Json.parse(result).get("status").asInt();

        if (result_status == 200) {
            havaDataSource = true;
        }
        updateDomainServiceVersion(havaDataSource, domainId, serviceId, currentVersion);
        if (result_status != 200) {
            throw new AppException("更新数据服务资源出错", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    private void updateDomainServiceVersion(Boolean havaDataSource, Integer domainId, String serviceId, String currentVersion) {
        DomainService domainService = buildDomainService(domainId, serviceId);
        domainService.setVersion(Integer.toString(Integer.parseInt(currentVersion) + 1));
        dataDomainDAO.updateDomainService(domainService);
    }

    public String buildDomainImg(Integer domainId) {
        DataDomain dataDomain = dataDomainService.getById(domainId);
        DomainServiceVO domainServiceVO = new DomainServiceVO();
        domainServiceVO.setId(domainId);
        domainServiceVO.setName(dataDomain.getName());
        domainServiceVO.setDescription(dataDomain.getDescription());
        List<Map<String, Object>> list = new ArrayList<>();
        List<DomainDataset> entities = dataDomainService.getDomainEntities(domainId);
        for (DomainDataset domainDataset : entities) {
            Map<String, Object> map = new HashedMap();
            map.put("datasetId", domainDataset.getId());
            map.put("name", domainDataset.getName());
            map.put("alias", domainDataset.getAlias() != null ? domainDataset.getAlias() : "");
            //标签
            String tagInfo = "";
            List<Tag> tagList = structuredDataService.getDataSetTags((long) domainDataset.getId());
            if (tagList.size() > 0 && tagList != null) {
                for (int i = 0; i < tagList.size(); i++) {
                    String tags = tagList.size() - 1 == i ? tagList.get(i).getTagName() : tagList.get(i).getTagName() + ",";
                    tagInfo = tagInfo + tags;
                }
            }
            map.put("tags", tagInfo);
            String columsInfo = "";
            StructuredDataSetColumnInfo setColumnInfo = structuredDataService.getColumnInfo((long) domainDataset.getId());
            List<StructuredDataSetColumn> setColumns = setColumnInfo.getColumns();

            if (CollectionUtils.isNotEmpty(setColumns)) {
                for (StructuredDataSetColumn setColumn : setColumns) {
                    String columName = setColumn.getName();
                    String columComment = setColumn.getComment();
                    columsInfo = columsInfo + columName + "(" + columComment + ")";
                }
            }
            map.put("schema", columsInfo);
            list.add(map);
        }
        domainServiceVO.setDataResources(list);
        return Json.toJson(domainServiceVO).toString();
    }

    /**
     * @param serviceId
     * @return
     * @description 查找版本号
     */
    public long getFindEdition(String serviceId) {

        logger.info("通过serviceId创建版本号开始" + serviceId);
        String result = null;
        Map<String, Object> map = new HashedMap();
        map.put("serviceId", serviceId);
        String param = Json.toJson(map).toString();
        try {

            result = HttpUtil.doPostJson(param, findEditionUrl);
        } catch (Exception e) {
            logger.error("查找版本号异常：" + e.getMessage());
            e.printStackTrace();
        }
        long editionNumber = Json.parse(result).get("data").get("format").get("editionVersion").asLong();
        logger.info("版本号是" + editionNumber);

        return editionNumber;
    }

    /**
     * @param serviceId
     * @return
     * @description 查找EditionId
     */
    public String getEditionId(String serviceId) {
        String editionId=null;
        logger.info("通过serviceId查找EditionId开始" + serviceId);
        String result = null;
        Map<String, Object> map = new HashedMap();
        map.put("serviceId", serviceId);
        String param = Json.toJson(map).toString();
        try {

            result = HttpUtil.doPostJson(param, findEditionUrl);
        } catch (Exception e) {
            logger.error("查找EditionId异常：" + e.getMessage());
            e.printStackTrace();
        }
        JsonNode jsonNode = Json.parse(result).get("data").get("format").get("editions");
        editionId= jsonNode.get(0).get("editionId").asText();
        logger.info("EditionId是" + editionId);

        return editionId;
    }

    /**
     * @param isUpdate (true:是更新操作，false是创建操作)
     * @param editionNumber 版本号
     * @param serviceId     服务ID
     * @return
     * @description 保存数据服务服务规格
     */
    public String saveSpecifications(boolean isUpdate, long editionNumber, String serviceId, Integer domainId) {

        logger.info("开始创建/修改服务规格" + serviceId);
        String result = null;
        ServiceProfile serviceProfile = new ServiceProfile();
        serviceProfile.setServiceId(serviceId);
        serviceProfile.setEditionVersion(editionNumber);

        List<String> addEditionCommonList = new ArrayList<>();
        serviceProfile.setAddEditionCommonList(addEditionCommonList);

        List<String> modifyEditionCommonList = new ArrayList<>();
        serviceProfile.setModifyEditionCommonList(modifyEditionCommonList);
        List<String> deleteEditionCommonList = new ArrayList<>();
        serviceProfile.setDeleteEditionCommonList(deleteEditionCommonList);
        if(isUpdate){
            List<ModifyEdition> modifyEditionList = new ArrayList<>();
            ModifyEdition modifyEdition = new ModifyEdition();
            modifyEdition.setEditionId(getEditionId(serviceId));
            modifyEdition.setEditionName("所有权限");
            modifyEdition.setEditionAudience("管理员");
            modifyEdition.setEditionCode(saveCustomSpecifications(domainId));

            Map<String, Object> mapRead = new HashedMap();
            String attributeUuidTead = UUID.randomUUID().toString();
            mapRead.put("attributeUuid", attributeUuidTead);
            mapRead.put("attributeName", "读权限");
            mapRead.put("attributeValue", "有");

            Map<String, Object> mapWrite = new HashedMap();
            String attributeUuidWrite = UUID.randomUUID().toString();
            mapWrite.put("attributeUuid", attributeUuidTead);
            mapWrite.put("attributeName", "写权限");
            mapWrite.put("attributeValue", "有");


            List<Map<String, Object>> editionAttributes = new ArrayList<>();
            editionAttributes.add(mapRead);
            editionAttributes.add(mapWrite);
            modifyEdition.setEditionAttributes(editionAttributes);

            List<Map<String, Object>> modifyEditionAttributeList = new ArrayList<>();
            modifyEditionAttributeList.add(mapRead);
            modifyEditionAttributeList.add(mapWrite);
            modifyEdition.setModifyEditionAttributeList(modifyEditionAttributeList);

            modifyEditionList.add(modifyEdition);
            serviceProfile.setModifyEditionList(modifyEditionList);

        }else {
            List<AddEdition> addEditionList = new ArrayList<>();
            AddEdition addEdition = new AddEdition();
            addEdition.setEditionUuid(UUID.randomUUID().toString());
            addEdition.setEditionName("所有权限");
            addEdition.setEditionAudience("管理员");
            addEdition.setEditionCode(saveCustomSpecifications(domainId));

            Map<String, Object> mapRead = new HashedMap();
            String attributeUuidTead = UUID.randomUUID().toString();
            mapRead.put("attributeUuid", attributeUuidTead);
            mapRead.put("attributeName", "读权限");
            mapRead.put("attributeValue", "有");

            Map<String, Object> mapWrite = new HashedMap();
            String attributeUuidWrite = UUID.randomUUID().toString();
            mapWrite.put("attributeUuid", attributeUuidTead);
            mapWrite.put("attributeName", "写权限");
            mapWrite.put("attributeValue", "有");


            List<Map<String, Object>> editionAttributes = new ArrayList<>();
            editionAttributes.add(mapRead);
            editionAttributes.add(mapWrite);
            addEdition.setEditionAttributes(editionAttributes);

            List<Map<String, Object>> addEditionAttributeList = new ArrayList<>();
            addEditionAttributeList.add(mapRead);
            addEditionAttributeList.add(mapWrite);
            addEdition.setAddEditionAttributeList(addEditionAttributeList);

            addEditionList.add(addEdition);
            serviceProfile.setAddEditionList(addEditionList);
        }

        JsonNode jsonNode = Json.toJson(serviceProfile);
        logger.info("生成的服务规格json是" + jsonNode);

        try {

            result = HttpUtil.doPostJson(jsonNode.toString(), saveSpecificationsUrl);
        } catch (Exception e) {
            logger.error("生成服务规格异常：" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("创建/修改服务规格结果是：" + result);
        return result;
    }

    /**
     * 生成自定义表单
     */
    public String saveCustomSpecifications(Integer domainId) {
        WhServiceSpec whServiceSpec = new WhServiceSpec();
        Map<String, WhSeviceSpecDataSetInfo> properties = new HashedMap();
        //根据数据资源集合id查询关联的表
        List<DomainDataset> entities = dataDomainService.getDomainEntities(domainId);
        if (CollectionUtils.isNotEmpty(entities)) {
            for (int i = 0; i < entities.size(); i++) {
                WhSeviceSpecDataSetInfo whSeviceSpecDataSetInfo = new WhSeviceSpecDataSetInfo();
                whSeviceSpecDataSetInfo.setItems();
                Boolean collapsed = false;
                if (i > 0) {
                    collapsed = true;
                }
                whSeviceSpecDataSetInfo.setOptions(collapsed);

                //得到数据表所属的数据源名称
                StructuredDataSetMetaData dataSetMetaData = structuredDataService.getStructuredTableMeta(entities.get(i).getId().longValue());
                String dataSourceName = dataSetMetaData.getDataSourceName();
                String parentName = dataSetMetaData.getParentName();
                String tableName = dataSourceName+"->"+entities.get(i).getName();
                if(StringUtils.isNotBlank(parentName)){
                    tableName = dataSourceName+"->"+parentName+"->"+entities.get(i).getName();
                }
                String alias = entities.get(i).getAlias();
                String title =tableName;
                if (StringUtils.isNotBlank(alias)) {
                    title = tableName+"("+alias+")";
                }
                whSeviceSpecDataSetInfo.setTitle(title);
                List<WhServiceSpecColumn> columns = new ArrayList<>();
                //通过表id得到表字段
                StructuredDataSetColumnInfo setColumnInfo = structuredDataService.getColumnInfo((long) entities.get(i).getId());
                List<StructuredDataSetColumn> setColumns = setColumnInfo.getColumns();
                for (StructuredDataSetColumn dataSetColumn : setColumns) {
                    WhServiceSpecColumn whServiceSpecColumn = new WhServiceSpecColumn();
                    whServiceSpecColumn.setColumnName(dataSetColumn.getName());
                    String desc = dataSetColumn.getComment();
                    if (desc == null || desc.equals("")) {
                        desc = "";
                    }
                    whServiceSpecColumn.setColumnDesc(desc);
                    columns.add(whServiceSpecColumn);
                }

                whSeviceSpecDataSetInfo.setDef_wh_default___(columns);
                properties.put(entities.get(i).getId().toString(), whSeviceSpecDataSetInfo);
            }
        }
        whServiceSpec.setProperties(properties);
        JsonNode jsonNode = Json.toJson(whServiceSpec);
        String newJsonNode = jsonNode.toString().replace("def_wh_default___", "default");
        return newJsonNode;


    }

    /**
     * @param editionNumber
     * @param serviceId
     * @return
     * @description 保存数据服务接入参数
     */
    public String saveAccessParameter(long editionNumber, String serviceId) {
        logger.info("开始创建接入参数");
        Map<String, Object> map = new HashedMap();
        map.put("serviceId", serviceId);
        map.put("customVersion", editionNumber);
        map.put("serviceName", null);
        map.put("notificationUrl", "http://default.com?url={eventUrl}");
        String param = Json.toJson(map).toString();
        logger.info("创建接入参数的参数是" + param);
        String result = null;
        try {
            result = HttpUtil.doPostJson(param, saveAccessParameterUrl);
        } catch (Exception e) {
            logger.error("创建接入参数异常" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("创建接入参数结果是：" + result);
        return result;
    }

    /**
     * @param serviceId
     * @return
     * @description 删除数据服务
     */
    public String deleteWhService(String serviceId) {
        logger.info("开始删除数据服务" + serviceId);
        Map<String, Object> map = new HashedMap();
        map.put("serviceId", serviceId);
        map.put("status", 3);
        map.put("currentStatus", 0);
        String param = Json.toJson(map).toString();
        String result = null;
        try {
            result = HttpUtil.doPostJson(param, deleteServiceUrl);
        } catch (Exception e) {
            logger.error("删除服务异常" + e.getMessage());
            e.printStackTrace();
        }
        logger.info("删除数据服务的结果是" + result);
        return result;
    }
}
