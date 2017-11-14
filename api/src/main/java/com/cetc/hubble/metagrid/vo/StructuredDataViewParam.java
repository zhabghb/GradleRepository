package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * For structured data preview.
 * <p>
 * Created by tao on 16-11-1.
 */
public class StructuredDataViewParam {

    private String urn;
    private String sourceType;
    private String fields;
    private Integer size;

    public StructuredDataViewParam() {
    }

    public StructuredDataViewParam(String urn, String sourceType, Integer size) {
        this(urn,sourceType,size,null);
    }

    public StructuredDataViewParam(String urn, String sourceType, Integer size,String fields) {
        this.urn = urn;
        this.sourceType = sourceType;
        this.size = size;
        this.fields = fields;
    }

    @ApiModelProperty(name = "需要查询的字段，多个用“,”分割", example = "id", required = false)
    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }


    @ApiModelProperty(name = "数据集URN", example = "2:///GA_TESTER1/gmy_test", required = true)
    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @ApiModelProperty(name = "数据源类型", example = "ORACLE", required = true)
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }


    @ApiModelProperty(name = "返回行数(默认20)", example = "20", required = false)
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}