package com.cetc.hubble.metagrid.vo;

import java.util.List;

public class Feature {
	private String featureId;
	private String featureTitle;
	private String featureDescription;
	private String featureImgResult;
	private Integer featureImgCropStep = 1;
	private String featureImgSrc;
	private String featureVersion;
	private Integer order = 1;
	private Boolean characteristicsDisplay = false;
	private List<FeatureCharacteristics> featureCharacteristics;
	public String getFeatureId() {
		return featureId;
	}
	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}
	public String getFeatureTitle() {
		return featureTitle;
	}
	public void setFeatureTitle(String featureTitle) {
		this.featureTitle = featureTitle;
	}
	public String getFeatureDescription() {
		return featureDescription;
	}
	public void setFeatureDescription(String featureDescription) {
		this.featureDescription = featureDescription;
	}
	public String getFeatureImgResult() {
		return featureImgResult;
	}
	public void setFeatureImgResult(String featureImgResult) {
		this.featureImgResult = featureImgResult;
	}
	public Integer getFeatureImgCropStep() {
		return featureImgCropStep;
	}
	public void setFeatureImgCropStep(Integer featureImgCropStep) {
		this.featureImgCropStep = featureImgCropStep;
	}
	public String getFeatureImgSrc() {
		return featureImgSrc;
	}
	public void setFeatureImgSrc(String featureImgSrc) {
		this.featureImgSrc = featureImgSrc;
	}
	public String getFeatureVersion() {
		return featureVersion;
	}
	public void setFeatureVersion(String featureVersion) {
		this.featureVersion = featureVersion;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Boolean getCharacteristicsDisplay() {
		return characteristicsDisplay;
	}
	public void setCharacteristicsDisplay(Boolean characteristicsDisplay) {
		this.characteristicsDisplay = characteristicsDisplay;
	}
	public List<FeatureCharacteristics> getFeatureCharacteristics() {
		return featureCharacteristics;
	}
	public void setFeatureCharacteristics(
			List<FeatureCharacteristics> featureCharacteristics) {
		this.featureCharacteristics = featureCharacteristics;
	}
	
	
}
