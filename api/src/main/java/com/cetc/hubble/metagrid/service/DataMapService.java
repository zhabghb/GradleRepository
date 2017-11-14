package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.DataMapDAO;
import com.cetc.hubble.metagrid.dao.TreeDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.vo.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dahey on 2017/3/10.
 */
@Service
@Transactional
public class DataMapService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataMapDAO dataMapDAO;
    @Autowired
    private TreeDAO treeDAO;
    @Autowired
    private StructuredDataService structuredDataService;


    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public Map getDataMapCoverage () {
        HashMap<String, Object> map = Maps.newHashMap();
        Long totalMatched = Long.valueOf(0);
        float coverRate = (float)0;

//        Long totalLoaded = treeDAO.getTotalDatasetCount();
//        Integer sourceId = 0;
        Long totalLoaded = treeDAO.getTotalDatasetCount();

        Long totalExpected = dataMapDAO.getTotalExpectedDatasetCount();
        if(totalExpected == 0 )
        {
            totalMatched = Long.valueOf(0);
            coverRate = 0;
        }
        else
        {
            totalMatched = dataMapDAO.getTotalMatchedDatasetCount();
            coverRate = (float)totalMatched / totalExpected;
        }

        map.put("totalLoaded",totalLoaded);
        map.put("totalMatched",totalMatched);
        map.put("totalExpected",totalExpected);
        map.put("coverage", String.format("%.2f", coverRate));
        return map;
    }

    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public List<Map<String, Object>> getDataMapTagStatis () {
        List<Map<String, Object>> maps = dataMapDAO.queryDataMapTagStatis();

        //查询包括tagName, totalExpected, totalMatched。但不包括rate，下面处理rate
        for (Map<String,Object> map:maps) {
            Number totalMatched = (Number) map.get("totalMatched");
            Number totalExpected = (Number) map.get("totalExpected");
            float rate = 0;
            if(totalExpected.intValue() > 0){
                rate = totalMatched.floatValue()/totalExpected.intValue();
            }
            map.put("rate",String.format("%.2f", rate));
        }
        return maps;
    }

    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public ArrayList<DataMap> getDataMapTree () {
        ArrayList<DataMap> dataMaps = Lists.newArrayList();
        List<Tag> tags = dataMapDAO.queryStdTags();
        for (Tag tag:tags) {

            if ("其他".equals(tag.getTagName())){
                continue;
            }

            List<StdTable> stdTables = dataMapDAO.queryStdTableByTag(tag.getId());
            Integer matchCount = 0;

            for (StdTable stdTable:  stdTables) {
                List<Map<String, Object>> dataset = dataMapDAO.queryDatasetByStdTab(stdTable.getId());
                boolean match = dataset.size() == 1;
                stdTable.setMatch(match);
                if (match) {
                    stdTable.setMatchedDatasetId((Long)dataset.get(0).get("id"));
                    matchCount++;
                }
            }
            DataMap dataMap = new DataMap(matchCount,tag.getTagName(), stdTables);
            dataMaps.add(dataMap);
        }
        return dataMaps;
    }

    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public List<StdColumn> getFields (Integer stdTableId) {
        List<StdColumn> columns  = dataMapDAO.queryStdColumnByTableId(stdTableId);
        return columns;
    }

    public void matchDatasetAndStdTable (Long dataSetId, Integer stdTableId) {
        Preconditions.checkArgument(dataSetId != null && stdTableId != null);
        List<Map<String, Object>> dataset = dataMapDAO.queryDatasetByStdTab(stdTableId);
        boolean matched = dataset.size() == 1;
        if(matched) {
            dataMapDAO.unmatchDatasetWithStd((Long)dataset.get(0).get("id"));
        }else {
            dataMapDAO.updateStdTagTotalMatched(stdTableId);
        }
        dataMapDAO.matchDatasetWithStd(dataSetId,stdTableId);
    }

    public void unMatchDatasetAndStdTable (Integer stdTableId) {
        Preconditions.checkArgument( stdTableId != null);
        List<Map<String, Object>> dataset = dataMapDAO.queryDatasetByStdTab(stdTableId);
        if(dataset.size() > 0) {
            dataMapDAO.unmatchDatasetWithStd((Long)dataset.get(0).get("id"));
        }
    }

    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public List<DataResourcePlus> fillByResouce (List<DataResourcePlus> dataResourcePlusList) {
       for (DataResourcePlus drs :dataResourcePlusList ){
           Integer etlJobId = null;
           try {
               etlJobId = structuredDataService.matchEtlJobIdByResouce(new DataResource(drs.getIp(),drs.getPort(),drs.getDbType(),drs.getDbName(),drs.getTableName(),drs.getAdditional()) );
           } catch (AppException e) {
               logger.warn("===failed to match etlJobId  for the record :{}===",drs);
               logger.info("===continue to match next record:{}===",drs);
               continue;
           }
           String urn = String.format("%s:///%s/%s", etlJobId, drs.getDbName(), drs.getTableName());
           Map<String, Object> tagAndAlias = dataMapDAO.queryStdTagAndAliasByUrn(urn);
           drs.setTag(String.valueOf(tagAndAlias.get("tag")));
           drs.setAlias(String.valueOf(tagAndAlias.get("alias")));
       }
        return dataResourcePlusList;
    }

    /**
     * 如果tagName已经存在，则直接返回对应id；不存在，保存成功后，返回id；
     * @return
     */
    private int getOrSaveStdTagByName(String tagName) {
        StdTag tag = dataMapDAO.queryStdTagByName(tagName);
        if(tag != null) {
            return tag.getId();
        } else {
            Map<String, Object> stdParams = new HashMap<String, Object>();
            stdParams.put("tagName", tagName);
            return dataMapDAO.saveStdTag(stdParams);
        }
    }

    /**
     * 通过code，确认std table 是否已经存在
     * @param code
     * @return true：表示已经存在,false表示不存在
     */
    @Transactional(readOnly=true,propagation = Propagation.NOT_SUPPORTED)
    public boolean checkStdTableExistByCode(String code) {
        StdTable stdTable = dataMapDAO.queryStdTableByCode(code);
        if(stdTable != null) {
            return true;
        }

        return false;
    }

    public void saveStdTableAndTags(Map<String, StdTable> tabMap, Map<String, String> tabTagMap, Map<String, List<StdColumn>> tabColumnMap) {
        for(String tabCode : tabMap.keySet()) {
            //保存std table
            StdTable stdTable = tabMap.get(tabCode);
            Map<String, Object> stdParams = new HashMap<String, Object>();
            stdParams.put("code", stdTable.getCode());
            stdParams.put("comment", stdTable.getComment());
            stdParams.put("datavolume",stdTable.getDataVolume());
            int tabId = dataMapDAO.saveStdTable(stdParams);

            //保存std tag
            int tagId = getOrSaveStdTagByName(tabTagMap.get(tabCode));

            //保存std table tag内容
            stdParams = new HashMap<String, Object>();
            stdParams.put("tabId", tabId);
            stdParams.put("tagId", tagId);
            int tabTagId = dataMapDAO.saveStdTableTag(stdParams);

            //插入std tag的total_expected
            dataMapDAO.updateStdTagTotalExpected(tagId);

            //保存std column
            List<StdColumn> columnList = tabColumnMap.get(tabCode);
            for(StdColumn stdColumn : columnList) {
                stdParams = new HashMap<String, Object>();
                stdParams.put("tabId", tabId);
                stdParams.put("code", stdColumn.getCode());
                stdParams.put("dataType", stdColumn.getDataType());
                stdParams.put("comment", stdColumn.getComment());

                dataMapDAO.saveStdColumn(stdParams);
            }

        }
    }
}
