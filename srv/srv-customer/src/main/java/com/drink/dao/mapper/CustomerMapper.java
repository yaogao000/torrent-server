package com.drink.dao.mapper;

import com.drink.srv.info.Customer;


public interface CustomerMapper {

	public Customer getCustomerByCid(String cid);

	public Customer getCustomerByPhone(String phone);

	public int insert(Customer customer);

}
