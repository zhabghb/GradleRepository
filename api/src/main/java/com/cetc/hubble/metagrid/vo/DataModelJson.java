package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by tao on 13/12/16.
 */
public class DataModelJson {

    private Integer id;
    private String json;
    private String name;
    private String type;

    public DataModelJson() {

    }

    public DataModelJson(Integer id, String json, String name) {
        this.id = id;
        this.json = json;
        this.name = name;
    }

    @ApiModelProperty(name = "模型ID", example = "1")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(name = "模型JSON（String）", example = "{\'example\':\'test1\'}", required = true)
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @ApiModelProperty(name = "模型名称", example = "样例")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }
}
