package com.drink.common.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils {
	private final static Logger logger = LoggerFactory.getLogger(IpUtils.class);

	private final static long INNER_10_START = ipStr2Long("10.0.0.0");
	private final static long INNER_10_END = ipStr2Long("10.255.255.255");

	private final static long INNER_172_START = ipStr2Long("172.16.0.0");
	private final static long INNER_172_END = ipStr2Long("172.31.255.255");

	private final static long INNER_192_START = ipStr2Long("192.168.0.0");
	private final static long INNER_192_END = ipStr2Long("192.168.255.255");

	/**
	 * get ip from request, please referrence to
	 * http://hi.baidu.com/lucene1853/item/5f7431c356017c2e47d5c079
	 * http://www.360doc.com/content/12/0409/15/1073512_202196789.shtml
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return ip String
	 * @throws Exception
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (isUnknown(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		} else {
			String[] ips = ip.split(",");

			// set to null
			ip = null;

			for (String iterator : ips) {
				iterator = iterator.trim();
				if (!isUnknown(iterator) && !isInnerNetwork(iterator)) {
					ip = iterator;
					break;
				}
			}
		}

		if (isUnknown(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isUnknown(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (isUnknown(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (isUnknown(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private static boolean isInnerNetwork(String ip) {
		long ipLong = ipStr2Long(ip);

		if ((ipLong >= INNER_10_START) && (ipLong <= INNER_10_END) || (ipLong >= INNER_172_START) && (ipLong <= INNER_172_END)
				|| (ipLong >= INNER_192_START) && (ipLong <= INNER_192_END)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isUnknown(String ip) {
		return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
	}

	public static long ipStr2Long(String ipStr) {
		// find '.' poistion
		int position1 = ipStr.indexOf(".");
		int position2 = ipStr.indexOf(".", position1 + 1);
		int position3 = ipStr.indexOf(".", position2 + 1);

		if (position1 == -1 || position2 == -1 || position3 == -1) {
			// estimate this ipStr is invalid
			return 0;
		}

		long[] ip = new long[4];

		try {
			ip[0] = Long.parseLong(ipStr.substring(0, position1));
			ip[1] = Long.parseLong(ipStr.substring(position1 + 1, position2));
			ip[2] = Long.parseLong(ipStr.substring(position2 + 1, position3));
			ip[3] = Long.parseLong(ipStr.substring(position3 + 1));
		} catch (NumberFormatException e) {
			logger.error("Invalid ip:" + ipStr, e);
			// if ip[2] meet numberFormatException, the return value would cause
			// ip[0]<<24 and ip[1]<<16, cause the return value would be
			// unbalanced
			// so when meet exception, just return 0 code
			return 0;
		}
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}
	
	public static String long2IpStr(long longIp) {
		StringBuilder sb = new StringBuilder("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String ip = "unknown, 192.168.1.120, 113.57.187.105, 10.13.12.128, 192.168.1.100";
//		String ip = "unknown, 192.168.1.120, , 10.13.12.128, 192.168.1.100";
		if (isUnknown(ip)) {
			System.out.println(">>>>");
		} else {
			String[] ips = ip.split(",");

			// TODO use error to record this ip
			System.out.println("Has been proxyed, and the original ip is " + ip);

			// set to null
			ip = null;
			for (String iterator : ips) {
				iterator = iterator.trim();
				if (!isUnknown(iterator) && !isInnerNetwork(iterator)) {
					ip = iterator;
					break;
				}
			}

			System.out.println("And the parsed ip is " + ip);
		}
		System.out.println("192.168.1.120".split(",")[0]);
	}
}
