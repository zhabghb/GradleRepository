
package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/***
 * 数据主题VO
 */
public class DomainDataset {

    public Integer id;
    public String name;
    public String alias;

    @ApiModelProperty(notes = "数据集ID", example = "1", required = true)
    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    @ApiModelProperty(notes = "数据集名称", example = "oracle_to_hdfs_dic_change", required = true)
    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    @ApiModelProperty(notes = "数据集别名", example = "转换作业变化表", required = true)
    public String getAlias () {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "SlimDataset{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
