package com.drink.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface ICache {
	/**
	 * put a key value pair to cache
	 * 
	 * @param key
	 * @param value
	 */
	void put(String key, Object value) throws CacheException;

	/**
	 * put multiple key value pair to cache
	 * 
	 * @param key
	 * @param value
	 */
	void put(String[] keys, Object value) throws CacheException;

	/**
	 * put a key value pair to cache
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 */
	void put(String key, Object value, long timeout, TimeUnit unit) throws CacheException;

	/**
	 * put multiple key value pair to cache
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 */
	void put(String[] keys, Object value, long timeout, TimeUnit unit) throws CacheException;

	/**
	 * get value by key
	 * 
	 * @param key
	 * @param type
	 * @return if contain the value return it, or return null.
	 */
	<T> T get(String key, Class<T> type) throws CacheException;

	/**
	 * get value by key
	 * 
	 * @param key
	 * @param type
	 * @param callback
	 * @return if contain the value return it, else load it from other
	 *         containers, include database
	 */
	<T> T get(String key, Class<T> type, CacheCallback callback) throws CacheException;

	/**
	 * remove key from cache
	 * 
	 * @param key
	 */
	void remove(String key) throws CacheException;

	/**
	 * remove keys from cache
	 * 
	 * @param keys
	 * @throws CacheException
	 */
	void remove(Collection<String> keys) throws CacheException;
}
