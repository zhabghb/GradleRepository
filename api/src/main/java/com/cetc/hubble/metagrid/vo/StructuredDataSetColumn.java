package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Column information, sub to StructuredDataColumnInfo.
 * 
 * @author tao
 *
 */
public class StructuredDataSetColumn {

	// field identification code
	private Long fieldID;
	private String name;
	private String comment;
	private String type;
	private String gatCodex;
	private String internalIdentifier;
	private String ruleName;
	private Date lastCheckTime;
	private Date nextCheckTime;

	public StructuredDataSetColumn() {
	}

	public StructuredDataSetColumn(Long fieldID, String name, String comment, String type) {
		this.fieldID = fieldID;
		this.name = name;
		this.comment = comment;
		this.type = type;
	}

	public StructuredDataSetColumn (Long fieldID, String name, String comment, String type, String gatCodex, String internalIdentifier, String ruleName, Date lastCheckTime, Date nextCheckTime) {
		this.fieldID = fieldID;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.gatCodex = gatCodex;
		this.internalIdentifier = internalIdentifier;
		this.ruleName = ruleName;
		this.lastCheckTime = lastCheckTime;
		this.nextCheckTime = nextCheckTime;
	}

	@ApiModelProperty(name = "字段ID", example = "1", required = true)
	public Long getFieldID() {
		return fieldID;
	}

	public void setFieldID(Long fieldID) {
		this.fieldID = fieldID;
	}

	@ApiModelProperty(name = "字段名称", example = "XM", required = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiModelProperty(name = "字段描述", example = "姓名", required = true)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ApiModelProperty(name = "字段类型", example = "VARCHAR2", required = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGatCodex () {
		return gatCodex;
	}

	public void setGatCodex (String gatCodex) {
		this.gatCodex = gatCodex;
	}

	public String getInternalIdentifier () {
		return internalIdentifier;
	}

	public void setInternalIdentifier (String internalIdentifier) {
		this.internalIdentifier = internalIdentifier;
	}

	public String getRuleName () {
		return ruleName;
	}

	public void setRuleName (String ruleName) {
		this.ruleName = ruleName;
	}

	public Date getLastCheckTime () {
		return lastCheckTime;
	}

	public void setLastCheckTime (Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public Date getNextCheckTime () {
		return nextCheckTime;
	}

	public void setNextCheckTime (Date nextCheckTime) {
		this.nextCheckTime = nextCheckTime;
	}
}