package com.drink.web.filter.funnel;

/**
 * 
 * @author yaogaolin
 * 
 */
public class RequestFunnelAttr {
	private String key;
	private short limit;
	private short time;
	private String[] parameters;
	private short code;
	private String msg;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public short getLimit() {
		return limit;
	}

	public void setLimit(short limit) {
		this.limit = limit;
	}

	public short getTime() {
		return time;
	}

	public void setTime(short time) {
		this.time = time;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public short getCode() {
		return code;
	}

	public void setCode(short code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
