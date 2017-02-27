package com.chomp.wifistorymachine.model;
//登录注册返回类
public class ResponseResult {
	private String flushToken;
	private String accessToken;
	private long validTime;
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getFlushToken() {
		return flushToken;
	}

	public void setFlushToken(String flushToken) {
		this.flushToken = flushToken;
	}

	public long getValidTime() {
		return validTime;
	}

	public void setValidTime(long validTime) {
		this.validTime = validTime;
	}
}
