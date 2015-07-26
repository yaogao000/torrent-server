package com.drink.dao.mapper;

import com.drink.dao.mapper.entity.Customer;

public interface CustomerMapper {

	public Customer getCustomerByCid(String cid);

	public Customer getCustomerByPhone(String phone);

	public int insert(Customer customer);

}
