package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by dahey on 2016/10/13.
 * <p>
 * 标签
 */

public class Tag {
    private Integer id;
    private String tagName;
    private String tagColor;

    public Tag() {
    }

    public Tag(Integer id, String tagName, String tagColor) {
        this.id = id;
        this.tagName = tagName;
        this.tagColor = tagColor;
    }

    @ApiModelProperty(name = "标签ID", example = "3")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(name = "标签名称", example = "刑侦")
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @ApiModelProperty(name = "标签颜色", example = "red")
    public String getTagColor() { return tagColor; }

    public void setTagColor(String tagColor) { this.tagColor = tagColor; }
}