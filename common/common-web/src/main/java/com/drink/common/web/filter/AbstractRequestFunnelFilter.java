package com.drink.common.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.drink.common.JSONUtils;
import com.drink.common.web.IpUtils;
import com.drink.common.web.ResponseMessssage;
import com.drink.common.web.filter.funnel.RequestFunnel;

/**
 * 请求漏斗
 * 
 * @author yaogaolin
 * 
 */
public abstract class AbstractRequestFunnelFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRequestFunnelFilter.class);

	private Map<String, List<RequestFunnel>> requestFunnels;

	protected abstract String getKeyPrefix();

	protected abstract boolean hasReachedUpperLimitInSomeTime(String key, int limit, int time);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
		if (null == requestFunnels || requestFunnels.isEmpty()) {
			filterChain.doFilter(req, resp);
			return;
		}

		HttpServletRequest request = (HttpServletRequest) req;

		String requestUri = request.getRequestURI();
		if (!requestFunnels.containsKey(requestUri)) {
			filterChain.doFilter(req, resp);
			return;
		}

		HttpServletResponse response = (HttpServletResponse) resp;
		response.setContentType("application/json;charset=UTF-8");

		List<RequestFunnel> requestFunnelList = requestFunnels.get(requestUri);

		for (RequestFunnel requestFunnel : requestFunnelList) {
			String[] paramers = requestFunnel.getParameters();
			Object[] values = new String[paramers.length];
			for (int i = 0, length = paramers.length; i < length; i++) {
				String value = null;

				if ("ip".equals(paramers[i])) {// ip 做特别处理
					value = IpUtils.getIpAddr(request);
				} else {
					value = request.getParameter(paramers[i]);
				}

				if (StringUtils.isBlank(value)) {// spring mvc
													// 请求参数有默认值，如果值没有设置，则用
													// DEFAULT
													// 代替，这样，只要是同一个请求，就会被处理
					values[i] = "df";// default
				} else {
					values[i] = value;
				}
			}

			String key = String.format(getKeyPrefix() + requestFunnel.getKey(), values);

			if (!hasReachedUpperLimitInSomeTime(key, requestFunnel.getLimit(), requestFunnel.getTime())) {
				filterChain.doFilter(req, resp);
			} else {
				String message = JSONUtils.readObject2String(ResponseMessssage.ERROR(requestFunnel.getCode(), requestFunnel.getMsg()));
				response.getWriter().println(message);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		ClassPathResource resouce = new ClassPathResource("funnels.json");
		if (!resouce.exists()) {
			logger.warn("No funnel file!");
			return;
		}

		final StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(resouce.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			logger.error("unable to load funnels file", e);
			return;
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					return;
				}
				reader = null;
			}
		}

		String funnels = builder.toString();
		if (StringUtils.isBlank(funnels)) {
			logger.warn("No settign funnels");
			return;
		}

		List<RequestFunnel> requestFunnelList = null;
		try {
			requestFunnelList = JSONUtils.readJson2List(funnels, RequestFunnel.class);
		} catch (Exception e) {
			logger.error("funnels setting error", e);
			return;
		}

		if (null == requestFunnelList || requestFunnelList.isEmpty()) {
			logger.warn("No settign funnels");
			return;
		}

		requestFunnels = new HashMap<>(requestFunnelList.size());
		for (RequestFunnel requestFunnel : requestFunnelList) {
			// 验证 requestFunnelList 各个字段的值是否符合格式
			if (StringUtils.isBlank(requestFunnel.getApi()) || StringUtils.isBlank(requestFunnel.getKey())) {
				logger.warn("funnel setting error, api or key should not be null or empty, and the value is " + requestFunnel.toString());
				continue;
			}
			if (requestFunnel.getLimit() <= 0 || requestFunnel.getTime() <= 0) {
				logger.warn("funnel setting error, limit or time should be great than 0, and the value is " + requestFunnel.toString());
				continue;
			}
			String[] parameters = requestFunnel.getParameters();
			if (null == parameters || parameters.length == 0) {
				logger.warn("funnel setting error, paramters should not be null or empty, and the value is " + requestFunnel.toString());
				continue;
			}

			// 补全 code
			if (requestFunnel.getCode() <= 0) {
				requestFunnel.setCode(ResponseMessssage.REQ_OVERFLOW.getStatusCode());
			}

			// 补全 message
			if (StringUtils.isBlank(requestFunnel.getMsg())) {
				requestFunnel.setMsg(ResponseMessssage.REQ_OVERFLOW.getMessage());
			}

			String key = requestFunnel.getApi();
			int index = -1;
			if ((index = key.lastIndexOf(".do")) != -1) {
				key = key.substring(0, index);
			}

			List<RequestFunnel> list = requestFunnels.get(key);
			if (null == list) {
				list = new ArrayList<>();
				list.add(requestFunnel);
				requestFunnels.put(key, list);
			} else {
				list.add(requestFunnel);
			}

		}
	}

	@Override
	public void destroy() {
		requestFunnels = null;
	}
}
