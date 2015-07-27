package com.drink.cache;

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
}
