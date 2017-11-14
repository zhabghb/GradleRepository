package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by dahey on 2016/10/11.
 * <p>
 * 标签树参数
 */

public class TagParam {

    private Integer tagId;
    private int limit;
    private int page;

    @ApiModelProperty(name = "标签ID", example = "1", required = true)
    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    @ApiModelProperty(name = "每次加载的个数", example = "3", required = false)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int db) {
        this.limit = db;
    }

    @ApiModelProperty(name = "页数", example = "1", required = true)
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


}
