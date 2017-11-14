package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by dahey on 2016/10/27.
 *
 *  标签树参数
 */

public class SearchTagParam {
    private String tagName;
    private int limit;
    private int page;

    @ApiModelProperty(name = "标签名称",example = "1",required = true)
    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName) {this.tagName = tagName;}

    @ApiModelProperty(name = "每次加载的个数",example = "3",required = false)
    public int getLimit() { return limit;}
    public void setLimit(int db) {this.limit = db;}

    @ApiModelProperty(name = "页数",example = "1",required = true)
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }


}
