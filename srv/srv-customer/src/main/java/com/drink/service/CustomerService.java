package com.drink.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
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
	
	
	public boolean checkCaptcha(String phone, String captcha, short countryCode) {
		String key = String.format("a_c_%s", phone);// auth code 
		String captchaCode = customerRedisCache.get(key, String.class);
		if (captchaCode != null) {
			if (captchaCode.equalsIgnoreCase(captcha)) {
				customerRedisCache.remove(key);
				return true;
			}
		}
		return false;
	}

	public final static int KEY_TIME_OUT_CAPTCHA = 5*60; // 5 minutes

	/**
	 * 先检查上次发送的验证码是否还有效，如果有效，则发送上次的验证码， 如果已经失效，或者不存在，则重新生成验证码，并发送
	 * 
	 * @param phone
	 * @param type
	 * @param countryCode
	 */
	public void captcha(String phone, int type, int countryCode) {
		String key = String.format("a_c_%s", phone);// auth code
		String captchaCode = customerRedisCache.get(key, String.class);
		if (captchaCode == null) {
			// 生成验证码并发送
			captchaCode = RandomStringUtils.randomNumeric(4);
			customerRedisCache.put(key, captchaCode, KEY_TIME_OUT_CAPTCHA, TimeUnit.SECONDS);
		}
		logger.info("captchaCode: " + captchaCode);
		// TODO 短信发送
		// int msgType = (type == 1 ? 1: 2);
		// smsSrv.sendSms(msgType, data);
	}
}
