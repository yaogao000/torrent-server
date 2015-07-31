package com.drink.web.filter;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.drink.common.web.filter.AbstractRequestFunnelFilter;
import com.drink.redis.JedisService;

/**
 * 请求漏斗
 * 
 * @author yaogaolin
 * 
 */
@Component("requestFunnelFilter")
public class RequestFunnelFilter extends AbstractRequestFunnelFilter {

	@Qualifier("funnelJedisService")
	@Autowired
	private JedisService requestFunnelJedisService;
	@Value("${redis.key.prefix.requestFunnel}")
	private String keyPrefix;

	@Override
	protected String getKeyPrefix() {
		return keyPrefix;
	}

	@Override
	protected boolean hasReachedUpperLimitInSomeTime(String key, int limit, int time) {
		return requestFunnelJedisService.hasReachedUpperlimitInSomeTime(key, limit, time, TimeUnit.MINUTES);
	}
}
