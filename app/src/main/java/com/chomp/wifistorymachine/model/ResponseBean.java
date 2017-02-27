package com.chomp.wifistorymachine.model;
//用户中心返回信息基础类
public class ResponseBean extends BaseResponseBean {
	private ResponseResult result;

	public ResponseResult getResult() {
		return result;
	}

	public void setResult(ResponseResult result) {
		this.result = result;
	}

}
