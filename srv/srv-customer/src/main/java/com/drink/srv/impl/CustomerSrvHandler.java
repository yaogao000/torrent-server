package com.drink.srv.impl;

import java.util.Map;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import com.drink.srv.CustomerSrv;
import com.drink.srv.support.SrvException;

@Service("customerService")
public class CustomerSrvHandler implements CustomerSrv.Iface {

	@Override
	public Map<String, String> login(String phone,
			Map<String, String> customerSession) throws SrvException,
			TException {
		return null;
	}

	@Override
	public String getSecretByToken(String token) throws SrvException,
			TException {
		return null;
	}

	@Override
	public short signIn(String phone, String password) throws TException {
		System.out.println("signIn method is called");
		return 0;
	}


}
