package com.cetc.hubble.metagrid.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * 数据服务数据资源
 * @author Administrator
 *
 */
public class DataServiceDataReSource {
	
	private String serviceId;
	private List<Feature>  features;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
}
