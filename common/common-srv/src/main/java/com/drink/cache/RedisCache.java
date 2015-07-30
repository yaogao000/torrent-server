package com.drink.cache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ValueOperations;

import com.drink.common.JSONUtils;

public class RedisCache implements ICache {
	private ValueOperations<String, String> valueOps;
	private String cacheKeyPrefix;

	@Override
	public void put(String key, Object value) throws CacheException {
		try {
			String json = JSONUtils.readObject2String(value);
			valueOps.set(buildKey(key), json);
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	private String buildKey(String key) {
		if (StringUtils.isBlank(cacheKeyPrefix)) {
			throw new NullPointerException("please setting the cacheKeyPrefix parameter");
		}
		return cacheKeyPrefix + key;
	}

	@Override
	public void put(String key, Object value, long timeout, TimeUnit unit) throws CacheException {
		try {
			String json = JSONUtils.readObject2String(value);
			valueOps.set(buildKey(key), json, timeout, unit);
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public <T> T get(String key, Class<T> type) throws CacheException {
		String json = valueOps.get(buildKey(key));
		if (StringUtils.isNotBlank(json)) {
			try {
				return JSONUtils.readJson2POJO(json, type);
			} catch (Exception e) {
				throw new CacheException(e);
			}
		} else {
			return null;
		}
	}

	@Override
	public <T> T get(String key, Class<T> type, CacheCallback<T> callback) throws CacheException {
		String cacheKey = buildKey(key);
		String json = valueOps.get(cacheKey);
		if (StringUtils.isNotBlank(json)) {
			try {
				return JSONUtils.readJson2POJO(json, type);
			} catch (Exception e) {
				throw new CacheException(e);
			}
		} else {
			T result = callback.load(cacheKey);

			if (null != result && callback.needBeCached()) {
				try {
					String callbackJson = JSONUtils.readObject2String(result);
					// 加入 缓存
					if (callback.getTimeout() > 0) {
						valueOps.set(cacheKey, callbackJson, callback.getTimeout(), callback.getTimeUnit());
					} else {
						valueOps.set(cacheKey, callbackJson);
					}
				} catch (Exception e) {
					throw new CacheException(e);
				}
			}

			return result;
		}
	}

	@Override
	public void remove(String key) throws CacheException {
		valueOps.getOperations().delete(buildKey(key));
	}

	@Override
	public void put(String[] keys, Object value) throws CacheException {
		try {
			String json = JSONUtils.readObject2String(value);
			for (String key : keys) {
				valueOps.set(buildKey(key), json);
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public void put(String[] keys, Object value, long timeout, TimeUnit unit) throws CacheException {
		try {
			String json = JSONUtils.readObject2String(value);
			for (String key : keys) {
				valueOps.set(buildKey(key), json, timeout, unit);
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}

	}

	@Override
	public void remove(Collection<String> keys) throws CacheException {
		List<String> keyList = new LinkedList<>();
		for (String key : keys) {
			keyList.add(buildKey(key));
		}
		valueOps.getOperations().delete(keyList);
	}

	public void setValueOps(ValueOperations<String, String> valueOps) {
		this.valueOps = valueOps;
	}

	public String getCacheKeyPrefix() {
		return cacheKeyPrefix;
	}

	public void setCacheKeyPrefix(String cacheKeyPrefix) {
		this.cacheKeyPrefix = cacheKeyPrefix;
	}
}
