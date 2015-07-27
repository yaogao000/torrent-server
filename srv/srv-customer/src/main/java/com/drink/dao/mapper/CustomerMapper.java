package com.drink.dao.mapper;

import com.drink.srv.info.Customer;

public interface CustomerMapper {
	/**
	 * 根据 用户 id 获取用户 信息
	 * 
	 * @param cid
	 * @return
	 */
	public Customer getCustomerByCid(long cid);

	/**
	 * 根据用户 手机号 获取用户信息
	 * 
	 * @param phone
	 * @return
	 */
	public Customer getCustomerByPhone(String phone);

	/**
	 * 保存 用户信息
	 * 
	 * @param customer
	 * @return
	 */
	public int insert(Customer customer);

	/**
	 * 根据用户手机号查询 用户id
	 * 
	 * @param key
	 * @return
	 */
	public long getCustomerIdByPhone(String phone);

}
