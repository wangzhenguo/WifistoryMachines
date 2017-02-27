package com.chomp.wifistorymachine.model;
//返回结果基础类
public class BaseResponseBean {
	private String code;
	private String userid;
	private String message;

	private String pcode;


	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getCode() {
		return code;
	}

	public String getUserid() {
		return userid;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
