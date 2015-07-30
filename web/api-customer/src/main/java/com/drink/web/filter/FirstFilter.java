package com.drink.web.filter;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.drink.cache.CacheCallback;
import com.drink.cache.RedisCache;
import com.drink.common.web.ResponseMessssage;
import com.drink.common.web.filter.AbstractFirstFilter;
import com.drink.srv.CustomerSrv;

/**
 * 签名：取得token-secret 计算摘要 RSA加密：根据服务端的公钥加密客户端需要上传的内容；根据客户端的公钥，解密服务端的数据。
 * 
 * @author yaogaolin
 *
 */
@Component("firstFilter")
public class FirstFilter extends AbstractFirstFilter {

	private static final Logger logger = LoggerFactory.getLogger(FirstFilter.class);
	public final static String CUSTOMER_SESSION_SECRET_WITH_TOKEN = "s_%s"; // c_s_s_[token]

	@Qualifier("customerSessionRedisCache")
	@Autowired
	private RedisCache customerSessionRedisCache;

	@Autowired
	private CustomerSrv.Iface customerSrv;

	/**
	 * 根据token 取得 secret
	 * */
	protected String getAccessSecret(final String accessKey) {
		try {
			if (StringUtils.isBlank(accessKey)) {
				logger.info(String.format("FirstFilter-->getAccessSecret: The parameter token [%s] is null or empty.", accessKey));
				return null;
			}
			return customerSessionRedisCache.get(String.format(CUSTOMER_SESSION_SECRET_WITH_TOKEN, accessKey), String.class, new CacheCallback<String>() {
				@Override
				public String getOriginKey(){
					return accessKey;
				}
				
				@Override
				public String load(String cacheKey) {
					try {
						return customerSrv.getSecretByToken(this.getOriginKey());
					} catch (TException e) {
						return null;
					}
				}

				@Override
				public boolean needBeCached() {
					return false;
				}
			});

		} catch (Exception e) {
			logger.error(String.format("FirstFilter-->getAccessSecret for token[%s] error", accessKey), e);
			throw new RuntimeException(String.format("FirstFilter-->getAccessSecret for token[%s] error", accessKey), e);
		}
	}

	@Override
	protected String getAccessKey() {
		return ACCESS_KEY_TOKEN;
	}

	@Override
	protected String getInvalidSignature() {
		return ResponseMessssage.INVALID_SIG_STR;
	}

	@Override
	protected String getNullSignature() {
		return ResponseMessssage.NULL_SIG_STR;
	}

	@Override
	protected String getInvalidToken() {
		return ResponseMessssage.INVALID_TOKEN_STR;
	}

	@Override
	protected String getNullToken() {
		return ResponseMessssage.NULL_TOKEN_STR;
	}

	@Override
	protected boolean isAccessUri(String uri) {
		String method = StringUtils.substringAfterLast(uri, "/");
		if (method.startsWith("heartbeat"))
			return false;
		return super.isAccessUri(uri);
	}

	protected String getIdFlowOutrange() {
		return ResponseMessssage.REQ_OVERFLOW_STR;
	}

	protected String getIpFlowOutrange() {
		return ResponseMessssage.IP_REQ_OVERFLOW_STR;
	}

	protected boolean checkIpFlow(String ip) {
		return true;
	}

}
