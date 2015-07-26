package com.drink.dao.mapper;

import com.drink.srv.info.CustomerSession;

public interface CustoemrSessionMapper {
	
	public CustomerSession getSessionByToken(String token);
}
