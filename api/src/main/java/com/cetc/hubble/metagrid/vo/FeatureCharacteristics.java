package com.cetc.hubble.metagrid.vo;

import java.util.List;
import java.util.Map;

public class FeatureCharacteristics {
	
	private String characteristicId;
	private String characteristicsTitle;
	private Integer order;
	private List<Map<String,Object>> characteristicsDescriptions;
	public String getCharacteristicId() {
		return characteristicId;
	}
	public void setCharacteristicId(String characteristicId) {
		this.characteristicId = characteristicId;
	}
	public String getCharacteristicsTitle() {
		return characteristicsTitle;
	}
	public void setCharacteristicsTitle(String characteristicsTitle) {
		this.characteristicsTitle = characteristicsTitle;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<Map<String, Object>> getCharacteristicsDescriptions() {
		return characteristicsDescriptions;
	}

	public void setCharacteristicsDescriptions(List<Map<String, Object>> characteristicsDescriptions) {
		this.characteristicsDescriptions = characteristicsDescriptions;
	}
}
