package com.drink.redis;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-property.xml", "classpath:spring-redis.xml" })
// @Ignore
public class JedisServiceTest {
	@Autowired
	private JedisService jedisService;

	@Test
	public void testJedisService() {
		jedisService.getValueOps().set("t_k", "test_key", 20, TimeUnit.SECONDS);
	}
}
