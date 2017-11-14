package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/13.
 */
public class DomainService {

    private Integer domainID;
    private String domainImg;
    private String serviceId;
    private String version;
    private Date lastPublishTime;

    @ApiModelProperty(notes = "数据主题ID", example = "1", required = true)
    public Integer getDomainID() {
        return domainID;
    }

    public void setDomainID(Integer domainID) {
        this.domainID = domainID;
    }

    @ApiModelProperty(notes = "数据服务资源", required = true)
    public String getDomainImg() {
        return domainImg;
    }

    public void setDomainImg(String domainImg) {
        this.domainImg = domainImg;
    }

    @ApiModelProperty(notes = "数据服务ID", example = "1232434545", required = true)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @ApiModelProperty(notes = "数据服务版本号", example = "1", required = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @ApiModelProperty(notes = "数据服上一次发布时间", example = "2017-05-27 09:41:22" ,required = false)
    public Date getLastPublishTime() {
        return lastPublishTime;
    }

    public void setLastPublishTime(Date lastPublishTime) {
        this.lastPublishTime = lastPublishTime;
    }

    @Override
    public String toString() {
        return "DomainService{" +
                "domainID=" + domainID +
                ", domainImg='" + domainImg + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", version='" + version + '\'' +
                ", lastPublishTime=" + lastPublishTime +
                '}';
    }
}
