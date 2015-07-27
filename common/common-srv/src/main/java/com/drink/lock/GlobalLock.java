package com.drink.lock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * @ClassName: GlobalLock
 * @Description: 全局锁，用于实现分布式事务
 * @author caohx
 * @date 2014-5-6 下午3:42:18
 * 
 * @changed by Frank 2014.05.11 1.修改availablePermits判断； 2.expire 改为
 *          refresh表示自动刷新lock； 3.修复GloballLock只处理本地过期, redis上未自动处理过期的bug；
 *          4.将TRY_LOCK_WAIT,TYR_LOCK_TIMEOUT,expired三种字段含义做区分处理；
 *          5.修改tryLock,tryLock(timeoutMillis)的实现.
 * 
 */
public class GlobalLock {
	private final static int TYR_LOCK_TIMEOUT = 30 * 1000; // 30s
	private final static long TRY_LOCK_WAIT = 5 * 1000; // 5s
	private final static long TRY_WAIT_TIMEOUT = 5 * 1000; // 5s

	private final static int MAX_PERMITS = 1;
	private final Semaphore sema = new Semaphore(MAX_PERMITS);

	private final String globalKey;
	private long expired;
	RedisTemplate<String, String> jedis;

	public GlobalLock(RedisTemplate<String, String> jedisService, String globalKey, long waitMill) {
		this.jedis = jedisService;
		this.globalKey = globalKey;
		this.expired = waitMill;
	}

	public void lock() {
		try {
			sema.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			// nothing need to be done
		}
		for (;;) {
			long result = jedis.opsForValue().increment(globalKey, 1);
			if (result <= 1) {// 加锁成功
				refresh(expired);
				break;
			}
			try {
				sema.tryAcquire(TRY_WAIT_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// nothing need to be done
			}
		}
	}

	public boolean tryLock() {
		return tryLock(TYR_LOCK_TIMEOUT);
	}

	public boolean tryLock(long timeoutMillis) {
		try {
			if (!sema.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS)) {
				return false;
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		long wait = TRY_LOCK_WAIT > timeoutMillis ? timeoutMillis : TRY_LOCK_WAIT;
		int count = 0;
		while ((count += wait) <= timeoutMillis) {
			try {
				long result = jedis.opsForValue().increment(globalKey, 1);
				if (result <= 1) {
					refresh(expired);
					return true;
				}
				Thread.sleep(wait);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public void unlock() {
		jedis.delete(globalKey);
		synchronized (sema) {
			if (sema.availablePermits() < MAX_PERMITS) {
				sema.release();
			}
		}
	}

	public boolean refresh(long timeoutMillis) {
		return jedis.expire(globalKey, (int) (timeoutMillis / 1000), TimeUnit.SECONDS);
	}

}
