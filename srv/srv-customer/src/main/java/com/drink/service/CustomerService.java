package com.drink.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.drink.cache.CacheCallback;
import com.drink.cache.RedisCache;
import com.drink.dao.mapper.CustomerMapper;
import com.drink.dao.mapper.CustomerSessionMapper;
import com.drink.redis.JedisService;
import com.drink.srv.info.Customer;
import com.drink.srv.info.CustomerSession;

@Service
public class CustomerService {
	@Autowired
	private JedisService jedisService;

	@Autowired
	private CustomerMapper customerMapper;

	@Autowired
	private CustomerSessionMapper customerSessionMapper;

	@Qualifier("customerRedisCache")
	@Autowired
	private RedisCache customerRedisCache;

	@Qualifier("customerSessionRedisCache")
	@Autowired
	private RedisCache customerSessionRedisCache;

	/**
	 * 先从 缓存里面取， 如果没有，则从 数据库里面取
	 * 
	 * @param phone
	 * @return
	 */
	public Customer getCustomerByPhone(String phone) {
		return customerRedisCache.get(phone, Customer.class, new CacheCallback() {

			@Override
			public Object load(String key) {
				return customerMapper.getCustomerByPhone(key);
			}
		});
	}

	private final int KEY_TIME_OUT_CUSTOMER = 60 * 60;// 1 hour

	/**
	 * 保存 用户信息 ，并加入 缓存， 缓存 信息为 [{cid : customer}, {mobile : customer}]
	 * 
	 * @param customer
	 */
	public void save(Customer customer) {
		customerMapper.insert(customer);
		customerRedisCache.put(new String[] { String.valueOf(customer.getCid()), customer.getMobile() }, customer, KEY_TIME_OUT_CUSTOMER, TimeUnit.SECONDS);
	}

	/**
	 * 保存 用户 session 信息，并加入缓存，缓存 信息为 [{token : session}, {token : secret}]
	 * 
	 * @param session
	 */
	public void save(CustomerSession session) {
		customerSessionMapper.insert(session);
		// 缓存 session 信息， key 为 token
		customerSessionRedisCache.put(session.getToken(), session, session.getExpireAt(), TimeUnit.MICROSECONDS);
		// 缓存 secret 信息， key 为 token
		customerSessionRedisCache.put(String.format("s_%s", session.getToken()), session.getSecret(), session.getExpireAt(), TimeUnit.MICROSECONDS);

	}

	/**
	 * 根据 用户 token 获取 对应的 secret 信息， 先从 redis里面取 ，如果不存在才从 db 上拿
	 * 
	 * @param token
	 * @return
	 */
	public String getSecretByToken(String token) {
		return customerSessionRedisCache.get(String.format("s_%s", token), String.class, new CacheCallback() {

			@Override
			public Object load(String key) {
				return customerSessionMapper.getSecretByToken(key);
			}
		});
	}
}
