package com.drink.cache;

import java.util.concurrent.TimeUnit;

public abstract class CacheCallback {
	/**
	 * load data from container
	 * 
	 * @param key
	 * @return
	 */
	public abstract Object load(String key);

	/**
	 * mark the loaded object should be cached or not
	 * 
	 * @return
	 */
	public boolean needBeCached() {
		return true;
	}

	public int getTimeout() {
		return 0;
	}

	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}
}
