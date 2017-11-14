package com.cetc.hubble.metagrid.vo;

import java.util.Map;

/**
 * Created by dahey on 2017/4/18.
 */
public class ElasticSearchParam {
    private Integer from;
    private Integer size;
    private Map query;

    public ElasticSearchParam() {
    }

    public ElasticSearchParam(Integer from, Integer size, Map query) {
        this.from = from;
        this.size = size;
        this.query = query;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Map getQuery() {
        return query;
    }

    public void setQuery(Map query) {
        this.query = query;
    }
}
