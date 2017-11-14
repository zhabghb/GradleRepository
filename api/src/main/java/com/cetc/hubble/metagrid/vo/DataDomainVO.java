package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 数据资源集合
 * Created by dahey on 2017/6/7.
 */
public class DataDomainVO extends DataDomain {

    private Long totalEntities;

    @ApiModelProperty(notes = "数据资源集合包含的实体个数",example = "12", required = true)
    public Long getTotalEntities() {
        return totalEntities;
    }

    public void setTotalEntities(Long totalEntities) {
        this.totalEntities = totalEntities;
    }

}
