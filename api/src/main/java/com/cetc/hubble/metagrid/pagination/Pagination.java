package com.cetc.hubble.metagrid.pagination;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yuson on 2016-04-14.
 */
public class Pagination implements Serializable{
    private int pageSize;
    private int pageNum;
    private int totalCount;
    private List<Map<String,Object>> content;

    public Pagination(){
        this.pageSize = 10;
        this.pageNum = 1;
        this.totalCount = 0;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Map<String, Object>> getContent() {
        return content;
    }

    public void setContent(List<Map<String, Object>> content) {
        this.content = content;
    }
}
