package com.drink.srv.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
	protected static ClassPathXmlApplicationContext context;

	public static void bootstrap(String[] springXmls) {

		if (springXmls == null || springXmls.length == 0) {
			springXmls = new String[] { "spring-srv.xml" };
		}

		context = new ClassPathXmlApplicationContext(springXmls);

		context.start();
	}

}
