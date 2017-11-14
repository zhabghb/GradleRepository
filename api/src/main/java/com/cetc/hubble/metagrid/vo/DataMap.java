package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Created by dahey on 2017/3/12.
 */
public class DataMap {
    private String name;
    private Integer matchCount;
    private List<StdTable> children;

    public DataMap (Integer matchCount,String name, List<StdTable> children) {
        this.matchCount = matchCount;
        this.name = name;
        this.children = children;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public List<StdTable> getChildren () {
        return children;
    }

    public void setChildren (List<StdTable> children) {
        this.children = children;
    }

    public Integer getMatchCount () {
        return matchCount;
    }

    public void setMatchCount (Integer matchCount) {
        this.matchCount = matchCount;
    }
}
