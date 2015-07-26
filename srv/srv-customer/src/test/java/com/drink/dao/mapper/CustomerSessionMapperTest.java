package com.drink.dao.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.drink.srv.info.CustomerSession;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@ContextConfiguration(locations = { "classpath:spring-property.xml",
		"classpath:spring-mybatis.xml" })
// @ContextConfiguration(locations = { "file:src/main/resources/spring-mybatis.xml" })
// @Ignore
public class CustomerSessionMapperTest {
	@Autowired
	private CustoemrSessionMapper custoemrSessionMapper;

	@Test
	public void testGetSessionByToken() {
		CustomerSession session = custoemrSessionMapper
				.getSessionByToken("token");
		System.out.println(session);
	}
}
