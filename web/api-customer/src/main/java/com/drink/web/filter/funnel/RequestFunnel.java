package com.drink.web.filter.funnel;

/**
 * 
 * @author yaogaolin
 * 
 */
public class RequestFunnel {
	private String api;

	private RequestFunnelAttr requestFunnelAttr;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public RequestFunnelAttr getRequestFunnelAttr() {
		return requestFunnelAttr;
	}

	public void setRequestFunnelAttr(RequestFunnelAttr requestFunnelAttr) {
		this.requestFunnelAttr = requestFunnelAttr;
	}
}
