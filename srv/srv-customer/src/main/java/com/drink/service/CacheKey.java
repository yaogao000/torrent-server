package com.drink.service;

/**
 * cache key format and timeout
 * 
 * @author yaogaolin
 * 
 */
public final class CacheKey {
	private CacheKey() {
		super();
	}

	public final static class Format {
		private Format() {
			super();
		}

		// customer cache key, prefix is c_
		public final static String CUSTOMER_WITH_PHONE = "%s"; // c_[phone]
		public final static String CUSTOMER_WITH_CID = "i_%s"; // c_i_[phone]
		public final static String CUSTOMER_ID_WITH_PHONE = "c_%s"; // c_c_[phone]
		public final static String CUSTOMER_CAPTCHA_WITH_PHONE = "a_c_%s";// c_a_c_[phone]

		// customer session key, prefix is c_s_
		public final static String CUSTOMER_SESSION_WITH_TOKEN = "%s"; // c_s_[token]
		public final static String CUSTOMER_SESSION_SECRET_WITH_TOKEN = "s_%s"; // c_s_s_[token]
		public final static String CUSTOMER_SESSION_CID_WITH_TOKEN = "c_%s"; // c_s_c_[token]
	}

	/**
	 * TimeUnit is second
	 * 
	 * @author yaogaolin
	 * 
	 */
	public final static class Timeout {
		private Timeout() {
			super();
		}

		public final static int CUSTOMER = 60 * 60;// 1 hour
		public final static int CUSTOMER_CAPTCHA = 5*60; // 5 minutes
		public final static int CUSTOMER_SESSION = 60 * 60;// 1 hour
	}
}
