package com.cetc.hubble.metagrid.pagination;

import java.util.HashMap;
import java.util.Map;

public class DynamicFiltersUtil {
    private final StringBuffer queryPageSql;
    private StringBuffer orderBySql;
    private String whereSql;
    private Map filters;
  
    private DynamicFiltersUtil(String queryPageSql) {
        this.queryPageSql = new StringBuffer(queryPageSql);
    }
  
    /**
     * 实例化工具类实例.
     * 
     * @param sql
     * @return
     */
    public static DynamicFiltersUtil newInstance(String sql) {
        return new DynamicFiltersUtil(sql);
    }
  
    /**
     * 编译查询参数.
     * 
     * @param searchParams
     * @return
     */
    public DynamicFiltersUtil buildParams(Map searchParams) {
        Map filters = JdbcSeachFilter.parse(searchParams);
        StringBuffer resultBuffer = new StringBuffer();
        if (!filters.isEmpty()) {
            this.filters = new HashMap();
            for (Object obj : filters.values()) {
                JdbcSeachFilter filter = (JdbcSeachFilter)obj;
                // logic operator
                switch (filter.operator) {
                case EQ:
                    resultBuffer.append(filter.fieldName).append("=:").append(filter.fieldName);
                    break;
                case LIKE:
                    resultBuffer.append(filter.fieldName).append(" like :").append(filter.fieldName);
                    filter.value = "%"+filter.value+"%";
                    break;
                case GT:
                    resultBuffer.append(filter.fieldName).append("> :").append(filter.fieldName);
                    break;
                case LT:
                    resultBuffer.append(filter.fieldName).append("< :").append(filter.fieldName);
                    break;
                case GTE:
                    resultBuffer.append(filter.fieldName).append(">= :").append(filter.fieldName);
                    break;
                case LTE:
                    resultBuffer.append(filter.fieldName).append("<= :").append(filter.fieldName);
                    break;
                case OR:
                    resultBuffer.append(" or ");
                    break;
                    case AND:
                    resultBuffer.append(" and ");
                    break;
                }
                this.filters.put(filter.fieldName,filter.value);
            }
        }
        this.whereSql = resultBuffer.toString();
        return this;
    }
  
    /**
     * 添加排序字段,可链式操作添加多个.
     * 
     * @param byName
     * @param order
     * @return
     */
    public DynamicFiltersUtil addSoft(String byName, String order) {
        StringBuffer softSb = null;
        if (orderBySql == null || "".equals(orderBySql)) {
            softSb = new StringBuffer(" order by ");
            softSb.append(byName).append(" ").append(order);
            this.orderBySql = softSb;
            return this;
        }
        this.orderBySql.append(",").append(byName).append(" ").append(order);
  
        return this;
    }
  
    /**
     * 添加排序字符串，会覆盖之前添加的排序信息.
     * 
     * @param orderBy
     * @return
     */
    public DynamicFiltersUtil addSoft(String orderBy) {
        StringBuffer softSb = new StringBuffer(" order by ");
        this.orderBySql = softSb.append(orderBy);
        return this;
    }
  
    /**
     * 编译完整查询sql,返回String 类型sql语句.
     * 
     * @return
     */
    public String builder() {
  
        if (whereSql != null && !"".equals(whereSql)) {
            queryPageSql.append(" where ").append(whereSql);
        }
        if (orderBySql != null && !"".equals(orderBySql)) {
            queryPageSql.append(orderBySql);
        }
        return queryPageSql.toString();
    }


    public Map getFilters() {
        return filters;
    }
}