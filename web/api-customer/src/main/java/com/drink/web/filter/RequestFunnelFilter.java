package com.drink.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.drink.cache.RedisCache;
import com.drink.common.JSONUtils;
import com.drink.web.filter.funnel.RequestFunnel;

/**
 * 请求漏斗
 * 
 * @author yaogaolin
 * 
 */
@Component("requestFunnelFilter")
public class RequestFunnelFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(RequestFunnelFilter.class);

	// @Qualifier("requestFunnelRedisCache")
	// @Autowired
	private RedisCache requestFunnelRedisCache;

	private Map<String, RequestFunnel> requestFunnels;

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
		
		RequestFunnel requestFunnel = requestFunnels.get(requestUri);
		
//		requestFunnelRedisCache.
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
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
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
			requestFunnels.put(requestFunnel.getApi(), requestFunnel);
		}
	}

	@Override
	public void destroy() {
		requestFunnels = null;
	}
}
