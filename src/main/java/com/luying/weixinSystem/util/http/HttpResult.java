package com.luying.weixinSystem.util.http;

public class HttpResult {
	
	private String code;
	
	private String body;

	public HttpResult(String code, String body) {
		super();
		this.code = code;
		this.body = body;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	

}
