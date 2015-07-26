package com.drink.srv;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drink.srv.main.Bootstrap;

public class CustomerClientMain extends Bootstrap {
	private static final Logger logger = LoggerFactory
			.getLogger(CustomerClientMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testLoginByRemote();
		// testLoginByDirect();
	}

	private static void testLoginByRemote() {
		Map<String, String> session = new HashMap<String,String>(4);
		session.put("aeskey", "aeskey");

		try {
			TTransport transport = new TSocket("localhost", 9020);
			TProtocol protocol = new TBinaryProtocol(transport);
			CustomerSrv.Client client = new CustomerSrv.Client(protocol);
			transport.open();
			System.out.println(client.login("13621700250", session));
			transport.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void testLOginByDirect() {
		Map<String, String> session = new HashMap<String,String>(4);
		session.put("aeskey", "aeskey");

		try {
			CustomerSrv.Client client = context.getBean("customerService",
					CustomerSrv.Client.class);
			System.out.println(client.login("13621700250", session));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != context) {
				context.close();
			}

		}
	}

}
