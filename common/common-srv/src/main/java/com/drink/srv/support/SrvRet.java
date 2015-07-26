/**
 * 
 */
package com.drink.srv.support;


/**
 * 系统间相互调用系统级别公共错误
 * 
 */
public class SrvRet {
	
	public static final SrvRet SYSTEM_ERROR = new SrvRet(9999,"Invalid phone!");

	public int code;

	public String msg;

	public SrvRet() {
	}

	public SrvRet(int code) {
		this.code = code;
		this.msg = "";
	}

	public SrvRet(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
