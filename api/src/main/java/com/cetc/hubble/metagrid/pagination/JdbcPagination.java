package com.cetc.hubble.metagrid.pagination;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JdbcPagination implements Serializable {
  
    /** slf日至类 **/
    //private final Logger logger = LoggerFactory.getLogger(JdbcPagination.class);
  
    /** 默认分页数 **/
    public static final int DEFAULT_PAGE_SIZE = 10;
  
    // 单页记录数
    private int size;
    // 记录总数
    private int total;
    // 总页数
    private int totalPages;
    // 当前页码
    private int currentPage = 1;
    // 起始行数
    private int begin;
    // 结束行数
    private int end;
  
    // 结果集存放List
    private List<Map<String,Object>> content;
    // JdbcTemplate jdbcTemplate
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * 私有构造器，喜欢这样写.
     */
    private JdbcPagination() {
  
    }
      
    /**
     * 实例化分页对象，并设置分页使用的jdbcTemplate,在我的项目里有JdbcTemplate的子类,用来实现多数据源。
     * @param jdbcTemplate
     * @return
     */
    public static JdbcPagination newInstance(NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcPagination page = new JdbcPagination();
        page.setJdbcTemplate(jdbcTemplate);
        return page;
    }

    public Pagination findPage(DynamicFiltersUtil fu, Pagination p) {
        String sql = fu.builder();

        // 设置每页显示记录数
        setSize(p.getPageSize());
        // 设置要显示的页数
        setCurrentPage(p.getPageNum());
        // 计算总记录数
        StringBuffer totalSQL = new StringBuffer(" SELECT count(*) as count FROM ( ");
        int orderIndex = sql.toUpperCase().indexOf("ORDER");
        if (orderIndex != -1) {
            totalSQL.append(sql.substring(0, orderIndex));
        } else {
            totalSQL.append(sql);
        }
        totalSQL.append(" ) totalTable ");

        // 总记录数
        setTotal(getJdbcTemplate().queryForObject(totalSQL.toString(),fu.getFilters(),Integer.class));
        // 计算总页数
        setTotalPages();
        // 计算起始行数
        setBegin();
        // 计算结束行数
        setEnd();

        String queryPageSql = generatePageSql(sql, this);
        // 装入结果集
        p.setContent(getJdbcTemplate().queryForList(queryPageSql,fu.getFilters()));
        p.setTotalCount(this.total);
        return p;
    }

    public String generatePageSql(String sql, JdbcPagination page) {
        StringBuffer pageSql = new StringBuffer(sql);
        if (page != null)
            pageSql.append(" limit " + page.getBegin() + "," + page.getSize());
        return pageSql.toString();
    }

    public int getCurrentPage() {
        return currentPage;
    }
  
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
  
    public int getSize() {
        return size;
    }
  
    public void setSize(int size) {
        this.size = size;
    }
  
    public List<Map<String,Object>> getContent() {
        return content;
    }
  
    public void setContent(List<Map<String,Object>> content) {
        this.content = content;
    }
  
    public int getTotalPages() {
        return totalPages;
    }
  
    public void setTotalPages() {
        if (total % size == 0) {
            this.totalPages = total / size;
        } else {
            this.totalPages = (total / size) + 1;
        }
    }
  
    public int getTotal() {
        return total;
    }
  
    public void setTotal(int total) {
        this.total = total;
    }
  
    public int getBegin() {
        return begin;
    }
  
    public void setBegin() {
        this.begin = (currentPage - 1) * size;
    }
  
    public int getEnd() {
        return end;
    }
  
    // 计算结束时候的索引
    public void setEnd() {
        if (total < size) {
            this.end = total;
        } else if ((total % size == 0) || (total % size != 0 && currentPage < totalPages)) {
            this.end = currentPage * size;
        } else if (total % size != 0 && currentPage == totalPages) {
            // 最后一页
            this.end = total;
        }
    }
  
    public Iterator<Map<String,Object>> iterator() {
        return content.iterator();
    }
  
    public boolean hasContent() {
        return !content.isEmpty();
    }
  
    public boolean hasNextPage() {
        return getCurrentPage() < getTotalPages();
    }
  
    public boolean isLastPage() {
        return !hasNextPage();
    }
  
    public boolean hasPreviousPage() {
        return getCurrentPage() > 1;
    }
  
    public boolean isFirstPage() {
        return !hasPreviousPage();
    }
  
    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
  
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
  
}