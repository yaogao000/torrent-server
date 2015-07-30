package com.drink.service.constants;

public class SrvExceptionResponse {
	private SrvExceptionResponse() {
		super();
	}

	public final static class Code {
		private Code() {
			super();
		}

		// more than one customer concurrent login
		public final static int CUSTOMER_MULTI_LOGIN = 900;
	}

	/**
	 * TimeUnit is millseconds
	 * 
	 * @author yaogaolin
	 * 
	 */
	public final static class Message {
		private Message() {
			super();
		}

		// more than one customer concurrent login
		public final static String CUSTOMER_MULTI_LOGIN = "登录太频繁";
	}
}
