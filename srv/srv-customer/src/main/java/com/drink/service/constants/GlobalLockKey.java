package com.drink.service.constants;

public class GlobalLockKey {
	private GlobalLockKey() {
		super();
	}

	public final static class Format {
		private Format() {
			super();
		}

		// customer cache key, prefix is gl_
		public final static String CUSTOMER_WITH_PHONE = "c_%s"; // c_[phone]

		// customer session key, prefix is gl_
		public final static String CUSTOMER_SESSION_WITH_TOKEN = "c_s_%s"; // c_s_[token]
	}

	/**
	 * TimeUnit is millseconds
	 * 
	 * @author yaogaolin
	 * 
	 */
	public final static class Timeout {
		private Timeout() {
			super();
		}

		public final static int CUSTOMER = 2 * 60 * 1000;// 2 minutes
		public final static int CUSTOMER_SESSION = 2 * 60 * 1000;// 2 minutes 
	}
}
