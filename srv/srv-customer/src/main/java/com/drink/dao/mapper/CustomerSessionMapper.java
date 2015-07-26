package com.drink.dao.mapper;

import com.drink.dao.mapper.entity.CustomerSession;


public interface CustomerSessionMapper {
	
	public CustomerSession getSessionByToken(String token);

	public void insert(CustomerSession session);
}
