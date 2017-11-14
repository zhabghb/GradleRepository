package com.cetc.hubble.metagrid.pagination;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JdbcSeachFilter {
    public enum Operator {
        EQ, LIKE, GT, LT, GTE, LTE, OR, AND
    }
  
    public String fieldName;
    public Object value;
    public Operator operator;
  
    public JdbcSeachFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }
  
    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME
     */
    public static Map parse(Map searchParams) {
        Map filters = new LinkedHashMap();
        int i = 0;
        for (Object entry : searchParams.entrySet()) {
            // 过滤掉空值
            String key = (String)(((Entry)entry).getKey());
            if(key.equals(Operator.AND.name()) || key.equals(Operator.OR.name())){
                filters.put(key+(i++), new JdbcSeachFilter("", Operator.valueOf(key), ""));
            }
            Object value = ((Entry)entry).getValue();
            if (StringUtils.isBlank((String) value)) {
                continue;
            }

            String filedName = key;
            Operator operator = Operator.EQ;
            // 拆分operator与filedAttribute
            String[] names = StringUtils.split(key, "_", 2);
            if (names.length == 2) {
                filedName = names[1];
                operator = Operator.valueOf(names[0]);
            }
  
            // 创建searchFilter
            JdbcSeachFilter filter = new JdbcSeachFilter(filedName, operator, value);
            filters.put(key, filter);
        }
          
        return filters;
    }
}