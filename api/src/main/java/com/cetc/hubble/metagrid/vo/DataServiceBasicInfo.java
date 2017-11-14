package com.cetc.hubble.metagrid.vo;

import java.util.List;
/**
 * 数据服务基本信息vo
 *
 */
public class DataServiceBasicInfo {
	
	private String serviceId;
	private String serviceName;
	private List<String> serviceCategoryIds;
	private String serviceDescription;
	private String logoImgResult;
	private String overviewImgResult;
	private String creatorId;
	private String provider;
	private String infoIntegrity;
	private String overviewVersion;
	private String serviceCategoryLastModified;
	private Integer serviceCategoryStatus = 0;
	private String dataType = "2";
	private String serviceCategoryName;
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<String> getServiceCategoryIds() {
		return serviceCategoryIds;
	}
	public void setServiceCategoryIds(List<String> serviceCategoryIds) {
		this.serviceCategoryIds = serviceCategoryIds;
	}
	public String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	public String getLogoImgResult() {
		return logoImgResult;
	}
	public void setLogoImgResult(String logoImgResult) {
		this.logoImgResult = logoImgResult;
	}
	public String getOverviewImgResult() {
		return overviewImgResult;
	}
	public void setOverviewImgResult(String overviewImgResult) {
		this.overviewImgResult = overviewImgResult;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getInfoIntegrity() {
		return infoIntegrity;
	}
	public void setInfoIntegrity(String infoIntegrity) {
		this.infoIntegrity = infoIntegrity;
	}
	public String getOverviewVersion() {
		return overviewVersion;
	}
	public void setOverviewVersion(String overviewVersion) {
		this.overviewVersion = overviewVersion;
	}
	public String getServiceCategoryLastModified() {
		return serviceCategoryLastModified;
	}
	public void setServiceCategoryLastModified(String serviceCategoryLastModified) {
		this.serviceCategoryLastModified = serviceCategoryLastModified;
	}
	public Integer getServiceCategoryStatus() {
		return serviceCategoryStatus;
	}
	public void setServiceCategoryStatus(Integer serviceCategoryStatus) {
		this.serviceCategoryStatus = serviceCategoryStatus;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getServiceCategoryName() {
		return serviceCategoryName;
	}
	public void setServiceCategoryName(String serviceCategoryName) {
		this.serviceCategoryName = serviceCategoryName;
	}
	
	
}
