package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Created by dahey on 2016/10/11.
 * <p>
 * 搜索参数
 */

public class SearchParam {
    private String keyword;
    private List<String> types;
    private int limit;
    private int page;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "SearchParam{" +
                "keyword='" + keyword + '\'' +
                ", types=" + types +
                ", limit=" + limit +
                ", page=" + page +
                '}';
    }
}
