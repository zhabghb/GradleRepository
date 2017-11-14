/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.cetc.hubble.metagrid.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class DatasetAttr {

    public Integer id;
    public Long datasetId;
    public String attrName;
    public String attrValue;


    @ApiModelProperty(notes = "数据集属性ID", example = "1", required = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NotNull(message = "数据集ID不能为空")
    @ApiModelProperty(notes = "数据集ID", example = "1", required = true)
    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    @NotNull(message = "数据集属性名称不能为空")
    @Size(min = 1, max = 25, message = "中文名称范围1-25字符")
    @ApiModelProperty(notes = "数据集属性名称", example = "用途", required = true)
    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    @Size(min = 1, max = 250, message = "中文名称范围1-250字符")
    @ApiModelProperty(notes = "数据集属性值", example = "临时表", required = false)
    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
