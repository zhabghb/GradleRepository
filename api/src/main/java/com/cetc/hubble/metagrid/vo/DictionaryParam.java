package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by tao on 11/01/17.
 */
public class DictionaryParam {

    Integer page;
    Integer limit;
    String keyword;

    public DictionaryParam() {
        page = 1;
        limit = 20;
    }

    public DictionaryParam(Integer page, Integer limit, String keyword) {
        this.page = page;
        this.limit = limit;
        this.keyword = keyword;
    }

    @ApiModelProperty(name = "页数", example = "1", required = true)
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @ApiModelProperty(name = "翻页行数", example = "20", required = true)
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @ApiModelProperty(name = "搜索关键词", example = "XM")
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
