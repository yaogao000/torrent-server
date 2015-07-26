package com.drink.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class EncodeAndDecodeUtil {
	// The encode and decode pattern.
	public static final String CHARSET_GBK = "GBK";
	public static final String CHARSET_UTF8 = "UTF-8";

	private EncodeAndDecodeUtil() {
	}

	public static String encode(String message, String encoding) throws UnsupportedEncodingException {
		return URLEncoder.encode(message, encoding);
	}

	public static String decode(String message, String encoding) throws UnsupportedEncodingException {
		return URLDecoder.decode(message, encoding);
	}

}
