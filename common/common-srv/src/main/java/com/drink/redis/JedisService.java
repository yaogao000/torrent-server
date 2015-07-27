package com.drink.redis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public class JedisService {
	private RedisTemplate<String, String> redisTemplate;

	// inject the template as ListOperations , can also inject as Value, Set, ZSet, and HashOperations
	private ValueOperations<String, String> valueOps;
	private ListOperations<String, String> listOps;
	private SetOperations<String, String> setOps;
	private ZSetOperations<String, String> zSetOps;
	private HashOperations<String, String, String> opsForHash;
	
	private RedisMessageListenerContainer listenerContainer;

	/**
	 * 在一段时间内是否达到次数上限()
	 * 
	 * @param key
	 *            redis的key
	 * @param upperlimit
	 *            次数上限
	 * @param timeout
	 *            一段时间
	 * @param unit
	 *            时间单位
	 * 
	 * @return true: 达到上限(包含上限); false: 未达到上限(不包含上限)
	 */
	public boolean hasReachedUpperlimitInSomeTime(String key, int upperlimit, long timeout, TimeUnit unit) {
		if (StringUtils.isBlank(key) || timeout < 0 || unit == null || upperlimit <= 0)
			throw new IllegalArgumentException(String.format("key=%s,Upperlimit=%d,timeout=%d,unit=" + unit, key, upperlimit, timeout));
		long jedCount = 0;
		jedCount = valueOps.increment(key, 1);
		if (redisTemplate.getExpire(key) == -1) {
			redisTemplate.expire(key, timeout, unit);
		}
		if (jedCount >= upperlimit) {
			return true;
		}
		return false;
	}

	/**
	 * 在一段时间内是否达到次数上限()
	 * 
	 * @param key
	 *            redis的key
	 * @param upperlimit
	 *            次数上限
	 * @param timeout
	 *            一段时间
	 * @param unit
	 *            时间单位
	 * 
	 * @return true: 达到上限(包含上限); false: 未达到上限(不包含上限)
	 */
	public boolean hasReachedUpperlimitInSomeTimeByStrict(String key, int upperlimit, long timeout, TimeUnit unit) {
		if (StringUtils.isBlank(key) || timeout < 0 || unit == null || upperlimit <= 0)
			throw new IllegalArgumentException(String.format("key=%s,Upperlimit=%d,timeout=%d,unit=" + unit, key, upperlimit, timeout));
		long length = listOps.size(key);
		if (length < upperlimit) {
			listOps.leftPush(key, String.valueOf(System.currentTimeMillis()));
		} else {
			long time = Long.parseLong(listOps.index(key, -1));
			if (System.currentTimeMillis() - time < unit.toMillis(timeout)) {
				return true;
			} else {
				listOps.leftPush(key, String.valueOf(System.currentTimeMillis()));
				listOps.trim(key, 0, upperlimit - 1);
			}

		}
		return false;
	}

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public ValueOperations<String, String> getValueOps() {
		return valueOps;
	}

	public void setValueOps(ValueOperations<String, String> valueOps) {
		this.valueOps = valueOps;
	}

	public ListOperations<String, String> getListOps() {
		return listOps;
	}

	public void setListOps(ListOperations<String, String> listOps) {
		this.listOps = listOps;
	}

	public SetOperations<String, String> getSetOps() {
		return setOps;
	}

	public void setSetOps(SetOperations<String, String> setOps) {
		this.setOps = setOps;
	}

	public ZSetOperations<String, String> getzSetOps() {
		return zSetOps;
	}

	public void setzSetOps(ZSetOperations<String, String> zSetOps) {
		this.zSetOps = zSetOps;
	}

	public HashOperations<String, String, String> getOpsForHash() {
		return opsForHash;
	}

	public void setOpsForHash(HashOperations<String, String, String> opsForHash) {
		this.opsForHash = opsForHash;
	}

	public RedisMessageListenerContainer getListenerContainer() {
		return listenerContainer;
	}

	public void setListenerContainer(RedisMessageListenerContainer listenerContainer) {
		this.listenerContainer = listenerContainer;
	}
	
}
