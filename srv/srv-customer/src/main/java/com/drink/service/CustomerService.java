package com.drink.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private final static String CUSTOMER_KEY_PREFIX = "c_";
	public Customer getCustomerByPhone(String phone) {
		String customerJson = jedisService.getValueOps().get(CUSTOMER_KEY_PREFIX + phone);
		if(StringUtils.isNotBlank(customerJson)){
			
		}else{
			
		}
		
		return null;
	}
	
	public void insert(Customer customer) {
		
	}
	private final static String CUSTOMER_SESSION_KEY_PREFIX = "c_s_";
	public void insert(CustomerSession session) {
		
	}
}
