package com.drink.srv.impl;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drink.common.RandomToken;
import com.drink.service.CustomerService;
import com.drink.srv.CustomerSrv;
import com.drink.srv.info.Customer;
import com.drink.srv.info.CustomerSession;
import com.drink.srv.support.SrvException;

@Service("remoteCustomerSrv")
public class CustomerSrvHandler implements CustomerSrv.Iface {
	@Autowired
	private CustomerService customerService;

	private final int KEY_TIME_OUT_CUSTOMER_SESSION = 60 * 60 * 1000;// 1 hour

	@Override
	public CustomerSession login(String phone, String password, short countryCode, CustomerSession session) throws SrvException, TException {
		long cid = customerService.getCustomerIdByPhone(phone);
		if (cid <= 0) {
			// save customer
			Customer customer = new Customer();
			customer.setCountryCode(countryCode);
			customer.setCityId(session.getCityId());
			customer.setMobile(phone);

			// 保存 customer, 并存入 redis 中，
			customerService.save(customer);
			cid= customer.getCid();// 取出cid
		}

		RandomToken token = RandomToken.build();
		session.setToken(token.getToken());
		session.setSecret(token.getSecret());
		session.setExpireAt(System.currentTimeMillis() + KEY_TIME_OUT_CUSTOMER_SESSION);
		
		CustomerSession local = customerService.getSessionByCid(cid);
		if(local == null){
			session.setCid(cid);// 设置用户 cid
			// 保存 session 并存入 redis 中
			customerService.saveOrUpdate(session, true);
		}else{
			if(local.getCityId() != session.getCityId()){
				//TODO 推送消息给用户，安全问题， 或者 系统 根据 城市id 变动，进行不同的推销活动
			}
			local.setToken(session.getToken());
			local.setSecret(session.getSecret());
			local.setExpireAt(session.getExpireAt());
			local.setAeskey(session.getAeskey());
			local.setClient(session.getClient());
			local.setCityId(session.getCityId());
			local.setLat(session.getLat());
			local.setLng(session.getLng());
			customerService.saveOrUpdate(local, false);
		}
		

		return session;
	}

	@Override
	public String getSecretByToken(String token) throws SrvException, TException {
		// 先从 redis里面取 ，如果不存在才从 db 上拿
		return customerService.getSecretByToken(token);
	}

}
