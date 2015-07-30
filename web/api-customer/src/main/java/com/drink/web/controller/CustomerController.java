package com.drink.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.drink.common.RegixUtis;
import com.drink.common.security.AESUtils;
import com.drink.common.security.RSAUtils;
import com.drink.common.web.ResponseMessssage;
import com.drink.srv.info.CustomerSession;
import com.drink.srv.support.SrvException;

/**
 * 
 * @author yaogaolin
 *
 */
@Controller
@RequestMapping(value = "/customer")
public class CustomerController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@RequestMapping(value = "login.do", produces = "application/json", method=RequestMethod.POST)
	@ResponseBody
	public ResponseMessssage login(@RequestParam(value = Constants.CONS_PHONE) String phone, @RequestParam(value = Constants.CONS_PASSWORD, required = false) String password,
			@RequestParam(value = Constants.CONS_CAPTCHA) String captcha, @RequestParam(value = Constants.CONS_AESKEY) String __aeskey,
			@RequestParam(value = Constants.CONS_CITY_ID, required = false, defaultValue = "0") int cityId,
			@RequestParam(value = Constants.CONS_COUNTRY_CODE, required = false, defaultValue = "86") short countryCode,
			@RequestParam(value = Constants.CONS_LAT, required = false, defaultValue = "0") double lat, @RequestParam(value = Constants.CONS_LNG, required = false, defaultValue = "0") double lng,
			HttpServletRequest request) throws SrvException, TException {
		// 验证 手机号码
		if (!RegixUtis.isMobileNO(phone)) {
			return ResponseMessssage.ERROR(ResponseMessssage.buildIllegalMessage(Constants.CONS_PHONE, phone));
		}
		
		String aeskey = "";
		// 验证 aes 是否 被 有效的 rsa public key 加密
		//测试阶段 不做 加密处理

//		try {
//			aeskey = new String(RSAUtils.decryptByPrivateKey(__aeskey.getBytes(), getRSAPrivateKey()));
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			return ResponseMessssage.INVALID_AESKEY;
//		}

		// 验证 captcha
		if (!customerSrv.checkCaptcha(phone, captcha, countryCode)) {
			return ResponseMessssage.ERROR(ResponseMessssage.buildIllegalMessage(Constants.CONS_CAPTCHA, captcha));
		}

		CustomerSession session = new CustomerSession();
		session.setAeskey(aeskey);
		session.setCityId(cityId);
		session.setLat(lat);
		session.setLng(lng);

		session = customerSrv.login(phone, password, countryCode, session);

		Map<String, Object> result = new HashMap<>();
		result.put(Constants.CONS_TOKEN, session.getToken());
		try {
			result.put(Constants.CONS_SECRET, AESUtils.encrypt(session.getSecret(), aeskey));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResponseMessssage.INVALID_AESKEY;
		}

		return ResponseMessssage.OK(result);
	}

	/**
	 * 获取验证码
	 * 
	 * @param type
	 *            1-乘客一键登陆验证码;2-修改手机号验证码
	 * @throws TException
	 * @throws SrvException
	 * 
	 * */
	@RequestMapping(value = "captcha.do", produces = "application/json", method=RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage getCaptcha(@RequestParam(value = Constants.CONS_PHONE, required = true) String phone,
			@RequestParam(value = Constants.CONS_COUNTRY_CODE, required = false, defaultValue = "86") short countryCode,
			@RequestParam(value = Constants.CONS_SMS_TYPE, required = false, defaultValue = "1") short type, HttpServletRequest request) throws SrvException, TException {
		// 验证 手机号码
		if (!RegixUtis.isMobileNO(phone)) {
			return ResponseMessssage.ERROR(ResponseMessssage.buildIllegalMessage(Constants.CONS_PHONE, phone));
		}

		// 生成验证码
		customerSrv.generateCaptcha(phone, type, countryCode);

		return ResponseMessssage.OK();
	}

	@RequestMapping(value = "signout.do", produces = "application/json", method=RequestMethod.POST)
	@ResponseBody
	public ResponseMessssage signout(@RequestParam(value = Constants.CONS_PHONE, required = true) String phone,
			@RequestParam(value = Constants.CONS_COUNTRY_CODE, required = false, defaultValue = "86") short countryCode, @RequestParam(value = Constants.CONS_TOKEN, required = true) String token,
			HttpServletRequest request) throws SrvException, TException {
		// 验证 手机号码
		if (!RegixUtis.isMobileNO(phone)) {
			return ResponseMessssage.ERROR(ResponseMessssage.buildIllegalMessage(Constants.CONS_PHONE, phone));
		}

		// 注销登录
//		customerSrv.signout(phone, countryCode, token);

		return ResponseMessssage.OK();
	}
}
