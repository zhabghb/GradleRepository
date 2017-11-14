package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 数据主题
 * Created by dahey on 2017/5/23.
 */
public class DataDomainExt {


    private Integer id;
    private String name;
    private String code;
    private String icon;
    private String description;
    private String status;
    private String domainImg;
    private String whServiceId;
    private Date createTime;
    private Date lastModifyTime;


    @ApiModelProperty(notes = "数据资源集合ID", example = "1", required = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(notes = "数据资源集合名称",example = "实有人口", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(notes = "数据资源集合编码", example = "syrk", required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ApiModelProperty(notes = "数据资源集合图标", example = "PNG", required = true)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @ApiModelProperty(notes = "数据资源集合描述", example = "实有人口是XXX", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(notes = "创建时间", example = "2017-05-27 09:41:22", required = false)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @ApiModelProperty(notes = "最后更新时间", example = "2017-05-27 09:41:22", required = false)
    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @ApiModelProperty(notes = "数据资源集合推送状态",example = "UNPUSHED", required = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDomainImg() {
        return domainImg;
    }

    public void setDomainImg(String domainImg) {
        this.domainImg = domainImg;
    }

    public String getWhServiceId() {
        return whServiceId;
    }

    public void setWhServiceId(String whServiceId) {
        this.whServiceId = whServiceId;
    }

    @Override
    public String toString() {
        return "DataDomain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                '}';
    }
}
