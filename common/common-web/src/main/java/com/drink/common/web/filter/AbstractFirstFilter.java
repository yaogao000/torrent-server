package com.drink.common.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drink.common.web.IpUtils;
import com.drink.common.web.ResponseMessssage;

/**
 * 
 * MD5签名：取得token-secret计算摘要 RSA加密：根据服务端的公钥加密客户端需要上传的内容；根据客户端的公钥，解密服务端的数据。
 */
public abstract class AbstractFirstFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractFirstFilter.class);

	public static final String CHAEACTER_ENCODING = "utf-8";
	public static final String ACCESS_KEY_TOKEN = "token";
	public static final String ACCESS_SECRET = "secret";
	public static final String SIG = "sig";
	public static final String NONCE = "nonce";

	/**
	 * 必须检查token及signature
	 */
	private Set<String> noChecks = new HashSet<String>();
	private Set<String> mustChecks = new HashSet<String>(); // 预留将来对某些请求必须签权处理

	/**
	 * 请求参数必须加密，如果参数中带有
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String requestUri = request.getRequestURI();
		response.setContentType("application/json;charset=UTF-8");

		String ip = IpUtils.getIpAddr(request);
		if (ip != null && !checkIpFlow(ip)) {
			response.getWriter().println(getIpFlowOutrange());
			return;
		}

		try {
			// 记录请求日志
			String contentType = request.getContentType();
			if (contentType != null && contentType.startsWith("multipart/form-data")) {
				// TODO 先处理文件上传
			}

			// 检查是否是加密的字符串
			if (isEncryptUri(requestUri)) {
				
			}

			// response.setCharacterEncoding("UTF-8");
			if (isAccessUri(requestUri)) {
				
				String accessKey = request.getParameter(getAccessKey());
				if (StringUtils.isNotBlank(accessKey)) {
					String secret = getAccessSecret(accessKey);
					// 计算摘要值,判断是否有效
					if (StringUtils.isNotBlank(secret)) {
						String reqSig = request.getParameter(SIG);
						if (StringUtils.isNotBlank(reqSig)) {
							@SuppressWarnings("unchecked")
							String sig = renderSignature(req.getParameterMap(), secret);
							if (reqSig.equals(sig)) {
								filterChain.doFilter(req, resp);
							} else {
								response.getWriter().println(getInvalidSignature());
							}
						} else {
							response.getWriter().println(getNullSignature());
						}
					} else {
						response.getWriter().println(getInvalidToken());
					}
				} else {// 没有传access_key进来
					response.getWriter().println(getNullToken());
				}
			} else {
				filterChain.doFilter(req, resp);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 写死，临时方案
			response.getWriter().println(ResponseMessssage.FILTER_UNKNOW_ERROR);
		} finally {

		}
	}
	
	protected abstract String getAccessKey();
	
	protected abstract String getAccessSecret(String key);

	protected abstract String getInvalidSignature();

	protected abstract String getIpFlowOutrange();

	protected abstract String getNullSignature();

	protected abstract String getInvalidToken();

	protected abstract String getNullToken();

	protected abstract boolean checkIpFlow(String ip);

	protected boolean isEncryptUri(String uri) {
		if (mustChecks == null || mustChecks.size() <= 0) {
			return false;
		}

		if (StringUtils.isBlank(uri)) {
			return false;
		}
		String method = StringUtils.substringAfter(uri, "/");
		return mustChecks.contains(method);
	}

	protected boolean isAccessUri(String uri) {
		if (noChecks == null || noChecks.size() <= 0) {
			return true;
		}

		if (StringUtils.isBlank(uri)) {
			return false;
		}
		String method = StringUtils.substringAfterLast(uri, "/");
		
		// 去除 .do 后缀
		if(StringUtils.isNotBlank(method) && method.endsWith(".do")){
			method = method.substring(0, method.lastIndexOf(".do"));
		}
		return !noChecks.contains(method);
	}

	protected String renderSignature(Map<String, String[]> params, String secret) {
		List<String> names = new ArrayList<String>();
		names.addAll(params.keySet());
		names.remove(SIG);
		names.remove(ACCESS_SECRET);
		Collections.sort(names);
		StringBuilder sb = new StringBuilder();

		sb.append(secret);
		for (String name : names) {
			sb.append("&" + name + "=");
			String[] paramValues = params.get(name);
			sb.append(StringUtils.join(paramValues, ","));
		}
		String sig;
		try {
			sig = Base64.encodeBase64String(DigestUtils.md5Hex(sb.toString()).getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			sig = Base64.encodeBase64String(DigestUtils.md5Hex(sb.toString()).getBytes());
		}
		return sig;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String noCheckStr = config.getInitParameter("nocheck");
		if (StringUtils.isNotBlank(noCheckStr)) {
			if (StringUtils.indexOf(noCheckStr, ",") != -1) {
				for (String str : noCheckStr.split(",")) {
					noChecks.add(str);
				}
			} else {
				noChecks.add(noCheckStr);
			}
		}
		// 需要加密的请求
		String encrypt = config.getInitParameter("encrypt");
		if (StringUtils.isNotBlank(encrypt)) {
			if (StringUtils.indexOf(encrypt, ",") != -1) {
				for (String str : encrypt.split(",")) {
					mustChecks.add(str);
				}
			} else {
				mustChecks.add(encrypt);
			}
		}

	}

	@Override
	public void destroy() {
		noChecks = null;
		mustChecks = null;
	}
}
