package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.SchemaHistoryDAO;
import com.cetc.hubble.metagrid.vo.StageLog;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import metagrid.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by dahey on 16-10-27.
 */
@Service
public class SchemaHistoryService {

    @Autowired
    private SchemaHistoryDAO schemaHistoryDAO;


    public Map<String,Object> getDatasetDiff(Long stageLogId) throws IOException {

        Map<String,Object> diff = schemaHistoryDAO.queryDatasetDiff(stageLogId);

        return getUnEscapedMap(diff);
    }


    private Map<String,Object> getUnEscapedMap(Map<String,Object> res) throws IOException, JsonParseException, JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        String schema = (String)res.get("schema");
        String previousSchema = (String)res.get("previous_schema");
        String replacedSchema = schema.replace("\\\"", "\"").replace("\\","\\\\");
        String replacedPreviousSchema = previousSchema.replace("\\\"", "\"").replace("\\","\\\\");
        ArrayList<Map> schemaList = mapper.readValue(replacedSchema, new TypeReference<ArrayList<Map>>(){});
        ArrayList<Map> previousSchemaList = mapper.readValue(replacedPreviousSchema, new TypeReference<ArrayList<Map>>(){});

        if(replacedSchema.contains("\\u")){
            revertUnicode(schemaList);
        }

        if (replacedPreviousSchema.contains("\\u")){
            revertUnicode(previousSchemaList);
        }

        res.put("schema", schemaList);
        res.put("previous_schema", previousSchemaList);
        return res;
    }

    private void revertUnicode(ArrayList<Map> list)  {
        for (Map map:list) {
            String name = (String) map.get("name");
            if (name.contains("\\u")){
                map.put("name", StringUtil.decodeUnicode(name));
            }else{
                continue;
            }
        }
    }


    public List<StageLog> getUpdateLogByDate(String date) {
        return schemaHistoryDAO.getUpdateLogByDate(date);
    }

    public List<Map> getUpdateDates(Integer limit) {
        ArrayList<String> datesDB = Lists.newArrayList();
        ArrayList<String> datesExec = Lists.newArrayList();

        ArrayList resultDates = Lists.newArrayList();
        List<Map<String, Object>> updateDates = schemaHistoryDAO.getUpdateDates();
        List<Map<String, Object>> syncDates = schemaHistoryDAO.getSyncDates();
        for (Map<String, Object> map:  updateDates) {
            datesDB.add((String)map.get("updateDate"));
        }
        for (Map<String, Object> map:  syncDates) {
            datesExec.add((String)map.get("syncDate"));
        }


        if (datesExec.size() == 0){
            HashMap<String, Object> updateDate = Maps.newHashMap();
            String formatedDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            updateDate.put("stageDate", formatedDate);
            updateDate.put("updated","NONE");
            resultDates.add(updateDate);
        }else{
            String firstSyncDate = datesExec.get(0);//DB查出来的第一条则是第一次同步成功的日期
            Calendar c = Calendar.getInstance();
            for (int i = 0; i < limit; i++) {
                HashMap<String, Object> updateDate = Maps.newHashMap();
                Date date = c.getTime();
                String formatedDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
                updateDate.put("stageDate", formatedDate);
                updateDate.put("updated",datesDB.contains(formatedDate)?"Y":(datesExec.contains(formatedDate)?"N":"NONE"));
                resultDates.add(updateDate);
                if (formatedDate.equals(firstSyncDate)){
                    break;
                }
                c.add(Calendar.DATE, -1);
            }
        }


        return resultDates;
    }
}
