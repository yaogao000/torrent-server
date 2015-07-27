package com.drink.srv.impl;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drink.common.RandomToken;
import com.drink.dao.mapper.CustomerMapper;
import com.drink.dao.mapper.CustomerSessionMapper;
import com.drink.service.CustomerService;
import com.drink.srv.CustomerSrv;
import com.drink.srv.info.Customer;
import com.drink.srv.info.CustomerSession;
import com.drink.srv.support.SrvException;

@Service("remoteCustomerSrv")
public class CustomerSrvHandler implements CustomerSrv.Iface {
	@Autowired
	private CustomerService customerService;

	@Override
	public CustomerSession login(String phone, String password, short countryCode,
			CustomerSession session) throws SrvException,
			TException {
		Customer customer = customerService.getCustomerByPhone(phone);
		if (null == customer) {
			// save customer
			customer = new Customer();
			customer.setCountryCode(countryCode);
			customer.setCityId(session.getCityId());
			customer.setMobile(phone);

			// 保存 customer, 并存入 redis 中，
			customerService.insert(customer);
		}

		session.setCid(customer.getCid());

		RandomToken token = RandomToken.build();
		session.setToken(token.getToken());
		session.setSecret(token.getSecret());
		session.setExpireAt(generateExpireAt());
		
		// 保存 session 并存入 redis 中
		customerService.insert(session);

		return session;
	}

	private long generateExpireAt() {
		long expire = 60 * 60 * 100; // 1 hour
		return System.currentTimeMillis() + expire;
	}

	@Override
	public String getSecretByToken(String token) throws SrvException,
			TException {
		// TODO 先从 redis里面取 ，如果不存在才从 db 上拿

		return null;
	}

}
