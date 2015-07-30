package com.drink.service;

import java.util.LinkedList;
import java.util.List;
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
import com.drink.service.constants.CacheKey;
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

	// /**
	// * 先从 缓存里面取， 如果没有，则从 数据库里面取
	// *
	// * @param phone
	// * @return
	// */
	// public Customer getCustomerByPhone(final String phone) {
	// return
	// customerRedisCache.get(String.format(CacheKey.Format.CUSTOMER_WITH_PHONE,
	// phone), Customer.class, new CacheCallback() {
	//
	// @Override
	// public String getOriginKey() {
	// return phone;
	// }
	//
	// @Override
	// public Object load(String cacheKey) {
	// return customerMapper.getCustomerByPhone(this.getOriginKey());
	// }
	//
	// @Override
	// public int getTimeout() {
	// return CacheKey.Timeout.CUSTOMER;
	// }
	// });
	// }

	/**
	 * 根据用户手机号查询用户id -- 直接从 数据库里面取
	 * 
	 * @param phone
	 * @return
	 */
	public long getCustomerIdByPhone(final String phone) {
		return customerMapper.getCustomerIdByPhone(phone);
		// return
		// customerRedisCache.get(String.format(CacheKey.Format.CUSTOMER_ID_WITH_PHONE,
		// phone), Long.class, new CacheCallback() {
		// @Override
		// public String getOriginKey(){
		// return phone;
		// }
		// @Override
		// public Object load(String cacheKey) {
		// return customerMapper.getCustomerIdByPhone(this.getOriginKey());
		// }
		//
		// @Override
		// public int getTimeout() {
		// return CacheKey.Timeout.CUSTOMER;
		// }
		// });
	}

	/**
	 * 保存 用户信息
	 * 
	 * @param customer
	 */
	public void insert(Customer customer) {
		customerMapper.insert(customer);
		// 并加入 缓存， 缓存 信息为 [{cid : customer}, {mobile : customer}, {mobile, cid}]
		// 暂时不缓存 键值对： cid : customer
		// String.format(CacheKey.Format.CUSTOMER_WITH_CID, customer.getCid()),
		// customerRedisCache.put(new String[] {
		// String.format(CacheKey.Format.CUSTOMER_WITH_PHONE,
		// customer.getMobile()) }, customer, CacheKey.Timeout.CUSTOMER,
		// TimeUnit.SECONDS);
		// 暂时不缓存 键值对：mobile, cid
		// customerRedisCache.put(String.format(CacheKey.Format.CUSTOMER_ID_WITH_PHONE,
		// customer.getMobile()), customer.getCid(), CacheKey.Timeout.CUSTOMER,
		// TimeUnit.SECONDS);
	}

	/**
	 * 保存 用户 session 信息，并加入缓存，缓存 信息为 [{token : session}, {token : secret},
	 * {token, cid}]
	 * 
	 * @param session
	 * @param save
	 *            标志是插入 还是 更新 操作
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
		customerSessionRedisCache.put(String.format(CacheKey.Format.CUSTOMER_SESSION_WITH_TOKEN, session.getToken()), session, CacheKey.Timeout.CUSTOMER_SESSION, TimeUnit.SECONDS);
		// 缓存 secret 信息， key 为 token
		customerSessionRedisCache.put(String.format(CacheKey.Format.CUSTOMER_SESSION_SECRET_WITH_TOKEN, session.getToken()), session.getSecret(), CacheKey.Timeout.CUSTOMER_SESSION, TimeUnit.SECONDS);
		// 暂时性不缓存 token, cid
		// 缓存 cid 信息， key 为 token
		// customerSessionRedisCache.put(String.format(CacheKey.Format.CUSTOMER_SESSION_CID_WITH_TOKEN,
		// session.getToken()), session.getCid(),
		// CacheKey.Timeout.CUSTOMER_SESSION, TimeUnit.SECONDS);

	}

	/**
	 * 根据 用户 token 获取 对应的 secret 信息， 先从 redis里面取 ，如果不存在才从 db 上拿
	 * 
	 * @param token
	 * @return
	 */
	public String getSecretByToken(final String token) {
		return customerSessionRedisCache.get(String.format(CacheKey.Format.CUSTOMER_SESSION_SECRET_WITH_TOKEN, token), String.class, new CacheCallback() {
			@Override
			public String getOriginKey() {
				return token;
			}

			@Override
			public Object load(String cacheKey) {
				return customerSessionMapper.getSecretByToken(this.getOriginKey());
			}

			@Override
			public int getTimeout() {
				return CacheKey.Timeout.CUSTOMER_SESSION;
			}
		});
	}

	/**
	 * 根据用户id 获取 用户 session 信息 -- 直接从 数据库里面取
	 * 
	 * @param cid
	 */
	public CustomerSession getSessionByCid(long cid) {
		return customerSessionMapper.getSessionByCid(cid);
	}

	public boolean checkCaptcha(String phone, String captcha, short countryCode) {

		String key = String.format(CacheKey.Format.CUSTOMER_CAPTCHA_WITH_PHONE, phone);// auth
																						// code
		String captchaCode = customerRedisCache.get(key, String.class);
		if (captchaCode != null) {
			if (captchaCode.equalsIgnoreCase(captcha)) {
				customerRedisCache.remove(key);
				return true;
			}
		}
		return false;
	}

	/**
	 * 先检查上次发送的验证码是否还有效，如果有效，则发送上次的验证码， 如果已经失效，或者不存在，则重新生成验证码，并发送
	 * 
	 * @param phone
	 * @param type
	 * @param countryCode
	 */
	public void captcha(final String phone, int type, int countryCode) {
		String cackey = String.format(CacheKey.Format.CUSTOMER_CAPTCHA_WITH_PHONE, phone);// auth
																							// code
		String captchaCode = customerRedisCache.get(cackey, String.class, new CacheCallback() {

			@Override
			public Object load(String cacheKey) {
				// 生成验证码
				return RandomStringUtils.randomNumeric(4);
			}

			@Override
			public String getOriginKey() {
				return phone;
			}

			@Override
			public int getTimeout() {
				return CacheKey.Timeout.CUSTOMER_CAPTCHA;
			}
		});
		logger.info("captchaCode: " + captchaCode);
		// TODO 短信发送
		// int msgType = (type == 1 ? 1: 2);
		// smsSrv.sendSms(msgType, data);
	}

	/**
	 * 清除用户缓存，并将用户session失效
	 * 
	 * @param token
	 */
	public void signout(String token) {
		// 删除session 缓存
		List<String> keys = new LinkedList<>();
		keys.add(String.format(CacheKey.Format.CUSTOMER_SESSION_WITH_TOKEN, token));
		keys.add(String.format(CacheKey.Format.CUSTOMER_SESSION_SECRET_WITH_TOKEN, token));
		customerSessionRedisCache.remove(keys);
		// expire session
		customerSessionMapper.expireSession(token);
	}
}
