package com.drink.cache;

import java.util.concurrent.TimeUnit;

public abstract class CacheCallback<T> {
	/**
	 * 
	 * @return the key is used to laod resource from database or other container
	 */
	public abstract String getOriginKey();

	/**
	 * load data from container
	 * 
	 * @param cacheKey
	 *            has add cachePrefix to originKey
	 * 
	 * @return
	 */
	public abstract T load(String cacheKey);

	/**
	 * mark the loaded object should be cached or not
	 * 
	 * @return
	 */
	public boolean needBeCached() {
		return true;
	}

	/**
	 * if need set timeout, please set the timeout value
	 * 
	 * @return
	 */
	public int getTimeout() {
		return 0;
	}

	/**
	 * timeout unit
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnit() {
		return TimeUnit.MINUTES;
	}
}
