package com.drink.common.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Frank
 * 
 */
public class CookieUtils {
	private static final Logger logger = LoggerFactory.getLogger(CookieUtils.class);

	private CookieUtils() {
	}

	public static String getCookieValue(HttpServletRequest request,
			String cookieName, String defaultValue) {
		Cookie cookieList[] = request.getCookies();

		if (cookieList == null || cookieName == null)
			return defaultValue;

		String cookieValue;

		for (int i = 0, length = cookieList.length; i < length; i++) {
			if (cookieList[i].getName().equals(cookieName)) {
				cookieValue = cookieList[i].getValue();
				if (cookieValue == null || "null".equals(cookieValue)) {
					return null;
				} else {
					try {
						return URLDecoder.decode(cookieValue, "UTF8");
					} catch (UnsupportedEncodingException e) {
						logger.error("", e);
					}
				}
			}
		}

		return defaultValue;
	}

	public static void setCookie(HttpServletResponse response,
			String cookieName, String cookieValue)
			throws UnsupportedEncodingException {
		setCookie(response, cookieName, cookieValue, -1);
	}

	public static void setCookie(HttpServletResponse response,
			String cookieName, String cookieValue, int cookieMaxage)
			throws UnsupportedEncodingException {
		if (null == cookieName || null == cookieValue) {
			throw new NullPointerException(
					"cookieName and cookieValue can't be null!");
		}
		Cookie theCookie = new Cookie(URLEncoder.encode(cookieName, "UTF8"),
				cookieValue == null ? null : URLEncoder.encode(cookieValue,
						"UTF8"));
		if (cookieMaxage >= 0) {
			theCookie.setMaxAge(cookieMaxage);
		}
		theCookie.setPath("/");
		response.addCookie(theCookie);
	}
}
