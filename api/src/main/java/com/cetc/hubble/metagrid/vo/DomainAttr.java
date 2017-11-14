package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 数据资源集合属性
 * Created by dahey on 2017/5/24.
 */
public class DomainAttr {
    private Integer domainId;
    private String key;
    private String value;

    @ApiModelProperty(notes = "数据资源集合ID", example = "1", required = false)
    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    @ApiModelProperty(notes = "数据资源集合属性key", example = "分类", required = true)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ApiModelProperty(notes = "数据资源集合属性value", example = "社会", required = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DomainAttr{" +
                "domainId=" + domainId +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
