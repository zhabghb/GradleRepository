package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by dahey on 2016/10/11.
 * <p>
 * 目录树参数
 */

public class TreeParam {
    
    private String treeNodename;
    private Integer sourceId;
    private int limit;
    private int page;

    @ApiModelProperty(name = "节点名称", example = "chinacloud", required = true)
    public String getTreeNodename() {
        return treeNodename;
    }

    public void setTreeNodename(String treeNodename) {
        this.treeNodename = treeNodename;
    }

    @ApiModelProperty(name = "每次加载的个数", example = "3", dataType = "java.lang.Integer", required = false)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int db) {
        this.limit = db;
    }

    @ApiModelProperty(name = "页数", example = "1", dataType = "java.lang.Integer", required = true)
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @ApiModelProperty(name = "资源ID", example = "3", dataType = "java.lang.Integer", required = true)
    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

}
