package com.cetc.hubble.metagrid.vo;

/**
 *
 * @author Dahey
 *
 */
public class StdColumn {

	Integer id;
	String code;
	String comment;
	String dataType;

	public Integer getId () {
		return id;
	}

	public void setId (Integer id) {
		this.id = id;
	}

	public String getCode () {
		return code;
	}

	public void setCode (String code) {
		this.code = code;
	}

	public String getComment () {
		return comment;
	}

	public void setComment (String comment) {
		this.comment = comment;
	}

	public String getDataType () {
		return dataType;
	}

	public void setDataType (String dataType) {
		this.dataType = dataType;
	}
}