package com.drink.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegixUtis {
	private RegixUtis(){
		
	}
	/**
	 * 验证手机号码
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobileNO(String mobile) {
		return matches(mobile, "^1\\d{10}$");
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isTelNO(String tel) {
		return matches(tel, "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)(\\d{7,8})(-(\\d{3,}))?$");
	}

	public static boolean isUrl(String url) {
//		return matches(url, "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*");
		return matches(url, "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
//		try {
//		      new URL(url);
//		} catch (MalformedURLException e) {
//			return false;
//		}
//		return true;
	}
	
	public static boolean isPostCode(String postCode) {
		return matches(postCode, "^[0-9]{6}$");
	}
	
	public static boolean isEmail(String email) {
		return matches(email, "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
	}
	
	public static boolean matches(String text, String regex){
		//Fix bug of NullPointException
		if(null ==text){
			return false;
		}
		
		boolean flag = false;
		try {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			flag = m.matches();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
