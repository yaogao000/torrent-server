package com.drink.cache;

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
			throw new NullPointerException(
					"please setting the cacheKeyPrefix parameter");
		}
		return cacheKeyPrefix + key;
	}

	@Override
	public void put(String key, Object value, long timeout, TimeUnit unit)
			throws CacheException {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> type, CacheCallback callback)
			throws CacheException {
		String json = valueOps.get(buildKey(key));
		if (StringUtils.isNotBlank(json)) {
			try {
				return JSONUtils.readJson2POJO(json, type);
			} catch (Exception e) {
				throw new CacheException(e);
			}
		} else {
			T result = (T) callback.load(key);
			
			if(null != result && callback.needBeCached()){
				// 加入 缓存
				this.put(key, result);
			}
			
			return result;
		}
	}

	@Override
	public void remove(String key) throws CacheException {
		valueOps.getOperations().delete(buildKey(key));
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
	public void put(String[] keys, Object value, long timeout, TimeUnit unit)
			throws CacheException {
		try {
			String json = JSONUtils.readObject2String(value);
			for (String key : keys) {
				valueOps.set(buildKey(key), json, timeout, unit);
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}

	}

}
