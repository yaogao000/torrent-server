package com.drink.srv;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drink.srv.info.CustomerSession;
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
		CustomerSession session = new CustomerSession();
		session.setCityId(1);

		try {
			TTransport transport = new TSocket("localhost", 9020);
			TProtocol protocol = new TBinaryProtocol(transport);
			CustomerSrv.Client client = new CustomerSrv.Client(protocol);
			transport.open();
			System.out.println(client.login("13621700250",null, (short)86, session));
			transport.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void testLoginByDirect() {
		CustomerSession session = new CustomerSession();
		session.setCityId(1);

		try {
			CustomerSrv.Client client = context.getBean(CustomerSrv.Client.class);
			System.out.println(client.login("13621700250",null, (short)86, session));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != context) {
				context.close();
			}

		}
	}

}
