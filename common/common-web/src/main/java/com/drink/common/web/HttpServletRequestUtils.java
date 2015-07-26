package com.drink.common.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;

public final class HttpServletRequestUtils {

	private HttpServletRequestUtils() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static String getUri(ServletRequest req) {
		try {
			Map<String, String[]> parameMap = null;
			if (req != null) {
				parameMap = req.getParameterMap();
			}
			if (parameMap != null && parameMap.size() > 0) {
				StringBuilder builder = new StringBuilder();
				Iterator<Entry<String, String[]>> iterator = parameMap.entrySet().iterator();
				if (iterator.hasNext()) {
					builder.append(buildParame(iterator.next()));
				}
				while (iterator.hasNext()) {
					builder.append("&" + buildParame(iterator.next()));
				}
				return builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String buildParame(Entry<String, String[]> entry) {
		try {
			if (entry != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(entry.getKey() + "=");
				String[] values = entry.getValue();
				if (values != null && values.length > 0) {
					if (values.length == 1) {
						builder.append(values[0]);
					} else {
						builder.append(StringUtils.join(values, ","));
					}
				}
				return builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
