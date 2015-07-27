package com.drink.dao.mapper;

import com.drink.srv.info.CustomerSession;



public interface CustomerSessionMapper {
	
	public CustomerSession getSessionByToken(String token);

	public void insert(CustomerSession session);
}
