package com.drink.service;

import java.util.concurrent.TimeUnit;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);
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
	
	/**
	 * 先从 缓存里面取， 如果没有，则从 数据库里面取
	 * 
	 * @param phone
	 * @return
	 */
	public long getCustomerIdByPhone(String phone) {
		return customerRedisCache.get(String.format("c_%s", phone), Long.class, new CacheCallback() {

			@Override
			public Object load(String key) {
				return customerMapper.getCustomerIdByPhone(key);
			}
		});
	}

	public final static int KEY_TIME_OUT_CUSTOMER = 60 * 60;// 1 hour
	public final static int KEY_TIME_OUT_CUSTOMER_SESSION = 60 * 60;// 1 hour

	/**
	 * 保存 用户信息 ，并加入 缓存， 缓存 信息为 [{cid : customer}, {mobile : customer}, {mobile, cid}]
	 * 
	 * @param customer
	 */
	public void save(Customer customer) {
		customerMapper.insert(customer);
		customerRedisCache.put(new String[] { String.valueOf(customer.getCid()), customer.getMobile() }, customer, KEY_TIME_OUT_CUSTOMER, TimeUnit.SECONDS);
		customerRedisCache.put(String.format("c_%s", customer.getMobile()), customer.getCid());
	}

	/**
	 * 保存 用户 session 信息，并加入缓存，缓存 信息为 [{token : session}, {token : secret}, {token, cid}]
	 * 
	 * @param session
	 * @param save 标志是插入 还是 更新 操作
	 */
	public void saveOrUpdate(CustomerSession session, boolean save) {
		if (save) {
			try {
				customerSessionMapper.insert(session);
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			customerSessionMapper.update(session);
		}
		
		// 缓存 session 信息， key 为 token
		customerSessionRedisCache.put(session.getToken(), session, KEY_TIME_OUT_CUSTOMER_SESSION, TimeUnit.SECONDS);
		// 缓存 secret 信息， key 为 token
		customerSessionRedisCache.put(String.format("s_%s", session.getToken()), session.getSecret(), KEY_TIME_OUT_CUSTOMER_SESSION, TimeUnit.SECONDS);
		// 缓存 cid 信息， key 为 token
		customerSessionRedisCache.put(String.format("c_%s", session.getToken()), session.getCid(), KEY_TIME_OUT_CUSTOMER_SESSION, TimeUnit.SECONDS);

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

	/**
	 * 根据用户id 获取 用户 session 信息
	 * 
	 * @param cid
	 */
	public CustomerSession getSessionByCid(long cid) {
		return customerSessionMapper.getSessionByCid(cid);
	}
}
