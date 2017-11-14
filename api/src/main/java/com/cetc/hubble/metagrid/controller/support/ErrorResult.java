package com.cetc.hubble.metagrid.controller.support;

public class ErrorResult {

	public int code;
	public String message;
	public String detail;
	public Object data;

	public ErrorResult() {
	}

	public ErrorResult(int code, String message) {
		this.code = code;
		if(message == null)
			message = "";
		int idx = message.indexOf("\n");
		if(idx !=-1 || message.length()>200){
			this.message = message.substring(0,idx == -1 ? 200 : idx);
			this.detail = message;
		}else
			this.message = message;
	}
}