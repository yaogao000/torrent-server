package com.drink.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-property.xml", "classpath:spring-redis.xml" })
// @Ignore
public class GlobalLockTest {
	@Autowired
	private GlobalLockRedisFactory globalLockRedisFactory;
	
	@Test
	public void testGlobalLock(){
		GlobalLock lock = globalLockRedisFactory.getLock("order_status", 20*1000);
		try{
			lock.lock();
			//service
			Thread.sleep(10*1000);

			throw new NumberFormatException();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
}
