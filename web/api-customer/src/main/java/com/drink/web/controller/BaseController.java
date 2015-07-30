package com.drink.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.drink.cache.CacheCallback;
import com.drink.cache.RedisCache;
import com.drink.common.web.filter.AbstractFirstFilter;
import com.drink.srv.CustomerSrv;
import com.drink.srv.info.Customer;

public class BaseController {
	private final static Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected final static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGnaxT2GDlshZuNQyiYpU6geZ2rv04XkKyWFoQlD8Hi25O8rIxuKF7CVpQwITBotj29yJrIevzzYeiz5s0PtY/FM4keQXYZS/SxINDm0g8ixNVJqk4pK8tBjqvHNFALUMAVn4bhkXzS5xKEJTmv7cXX8gISSR7ksg1hU6YO9nAtAgMBAAECgYBHKUeM5yswqw+fzuV36RQilQ619Nozehnp6C46A61uKbrjtDONLKi2mzYXkmg0sYQQKC/hR3+nI/W67mId38XxKkjWlL+1GgiuekAzp18RJt0OhztgxomlflTbAGA97lhifGB88JFCQVLvFomKVFbhOoZhi7VPwRmdXHjG9RGs9QJBAMV/uzmxZFeSM1+8gOszx2/kfJVlespIHn3xMBSo6SqEvsrYMjzZ1b8pI0wpfMNPc24G5sEWVaL0U0UJKGPMCZcCQQC8zE2EL4kvFBLMUDyzpbILTVvP4JCsyKJ5css7fDt/ybhB41wFyUJ1z2illdLVDgThayZuC+Cn1mmjOcsalyTbAkEAqIL5zpoLEaUO7Iq8PPLrIg+ENTxm7p1mVvMpoRcWC09EM3MQOldrRzXkhiGH8GWkCacd0HYsD2QEgIz1x2DadQJBAIjph5VCe7N+VRuBXGPS10jG8rvPWFtUrC5yhts+fk9vU4XEgSlZCC8zF+psuhXzMqrd72KCjopoPPc3pAhGb10CQA+ZIb/RJcdiBUGtfXi2p0+VJtOUDH043P9vgoPsFEob8Zbki+xnrmU1W8qnfQo8qUlqvW5K2G3szHTe5NmrIqg=";
	protected final static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRp2sU9hg5bIWbjUMomKVOoHmdq79OF5CslhaEJQ/B4tuTvKyMbihewlaUMCEwaLY9vciayHr882Hos+bND7WPxTOJHkF2GUv0sSDQ5tIPIsTVSapOKSvLQY6rxzRQC1DAFZ+G4ZF80ucShCU5r+3F1/ICEkke5LINYVOmDvZwLQIDAQAB";
	protected final static ThreadLocal<Customer> CUSTOMER_THREAD_LOCAL = new ThreadLocal<>();
	private final static String CUSTOMER_WITH_TOKEN = "t_%s"; // c_[token]
	
	@Autowired
	protected CustomerSrv.Iface customerSrv;
	@Qualifier("customerSessionRedisCache")
	@Autowired
	private RedisCache customerSessionRedisCache;
	
	public final String getRSAPublicKey() {
		return PUBLIC_KEY;
	}

	public final String getRSAPrivateKey() {
		return PRIVATE_KEY;
	}

	/**
	 * 根据 用户session的token 获取 用户信息
	 * 
	 * @param token
	 * @return
	 */
	public final Customer getLoginCustomer(final String token) {
		Customer customer = CUSTOMER_THREAD_LOCAL.get();
		if (null == customer) {
			customer = customerSessionRedisCache.get(String.format(CUSTOMER_WITH_TOKEN, token), Customer.class, new CacheCallback<Customer>(){

				@Override
				public String getOriginKey() {
					return token;
				}

				@Override
				public Customer load(String cacheKey) {
					try {
						return customerSrv.getCustomerByToken(this.getOriginKey());
					} catch (Exception e) {
						logger.error("getLoginCustomer error: ", e);
						return null;
					}
				}
				
				@Override
				public boolean needBeCached() {
					return false;
				}
				
			});
			
			CUSTOMER_THREAD_LOCAL.set(customer);
		}
		return customer;
	}

	protected final static class Constants {
		public final static String CONS_PHONE = "phone";
		public final static String CONS_PASSWORD = "password";
		public final static String CONS_CAPTCHA = "captcha";
		public final static String CONS_AESKEY = "aeskey";
		public final static String CONS_CITY_ID = "cityId";
		public final static String CONS_COUNTRY_CODE = "countryCode";
		public final static String CONS_LAT = "lat";
		public final static String CONS_LNG = "lng";

		public final static String CONS_TOKEN = AbstractFirstFilter.ACCESS_KEY_TOKEN;
		public final static String CONS_SECRET = AbstractFirstFilter.ACCESS_SECRET;

		public static final String CONS_SMS_TYPE = "type";
	}
}
