package com.drink.srv.impl;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drink.common.RandomToken;
import com.drink.redis.JedisService;
import com.drink.service.CacheKey;
import com.drink.service.CustomerService;
import com.drink.srv.CustomerSrv;
import com.drink.srv.info.Customer;
import com.drink.srv.info.CustomerSession;
import com.drink.srv.support.SrvException;

@Service("remoteCustomerSrv")
public class CustomerSrvHandler implements CustomerSrv.Iface {
	@Autowired
	private CustomerService customerService;
	@Autowired
	private JedisService jedisService;

	@Override
	public CustomerSession login(String phone, String password, short countryCode, CustomerSession session) throws SrvException, TException {
		long cid = customerService.getCustomerIdByPhone(phone);//TODO BUG: 凡是确认数据库里是否存在的，都需要直接从数据库里查询
		if (cid <= 0) {
			// save customer
			Customer customer = new Customer();
			customer.setCountryCode(countryCode);
			customer.setCityId(session.getCityId());
			customer.setMobile(phone);

			// 保存 customer, 并存入 redis 中，
			customerService.save(customer);
			cid = customer.getCid();// 取出cid
		}

		RandomToken token = RandomToken.build();
		session.setToken(token.getToken());
		session.setSecret(token.getSecret());
		session.setExpireAt(System.currentTimeMillis() + CacheKey.Timeout.CUSTOMER_SESSION * 1000);
		session.setStatus((byte) 1);

		CustomerSession local = customerService.getSessionByCid(cid);
		if (local == null) {
			session.setCid(cid);// 设置用户 cid
			// 保存 session 并存入 redis 中
			customerService.saveOrUpdate(session, true);
		} else {
			if (local.getCityId() != session.getCityId()) {
				// TODO 推送消息给用户，安全问题， 或者 系统 根据 城市id 变动，进行不同的推销活动
			}
			local.setToken(session.getToken());
			local.setSecret(session.getSecret());
			local.setExpireAt(session.getExpireAt());
			local.setAeskey(session.getAeskey());
			local.setClient(session.getClient());
			local.setCityId(session.getCityId());
			local.setLat(session.getLat());
			local.setLng(session.getLng());
			local.setStatus(session.getStatus());
			customerService.saveOrUpdate(local, false);
		}

		return session;
	}

	@Override
	public String getSecretByToken(String token) throws SrvException, TException {
		// 先从 redis里面取 ，如果不存在才从 db 上拿
		return customerService.getSecretByToken(token);
	}

	/*
	 * 如果有找到CAPTCHA_[phone], 则判断captcha是否匹配当前key，如果正确返回true,并且删除当前key
	 * 如果不正确就返回false
	 */
	@Override
	public boolean checkCaptcha(String phone, String captcha, short countryCode) throws SrvException, TException {
		return customerService.checkCaptcha(phone, captcha, countryCode);
	}
	
	/**
	 * 先检查上次发送的验证码是否还有效，如果有效，则发送上次的验证码， 如果已经失效，或者不存在，则重新生成验证码，并发送
	 */
	@Override
	public void generateCaptcha(String phone, short type, short countryCode) throws TException, SrvException {
		customerService.captcha(phone, type, countryCode);
	}

}
