package com.drink.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.drink.common.security.AESUtils;
import com.drink.common.security.RSAUtils;
import com.drink.common.web.ResponseMessssage;
import com.drink.srv.CustomerSrv;
import com.drink.srv.info.CustomerSession;
import com.drink.srv.support.SrvException;

@Controller
@RequestMapping(value = "/customer")
public class CustomerController extends BaseController {
	private final static Logger logger = LoggerFactory
			.getLogger(CustomerController.class);

	@Autowired
	private CustomerSrv.Iface customerSrv;

	@RequestMapping(value = "login", produces = "application/json")
	@ResponseBody
	public ResponseMessssage login(
			@RequestParam(value = Constants.CONS_PHONE) String phone,
			@RequestParam(value = Constants.CONS_PASSWORD, required = false) String password,
			@RequestParam(value = Constants.CONS_CAPTCHA) String captcha,
			@RequestParam(value = Constants.CONS_AESKEY) String __aeskey,
			@RequestParam(value = Constants.CONS_CITY_ID, required = false, defaultValue = "0") int cityID,
			@RequestParam(value = Constants.CONS_COUNTRY_CODE, required = false, defaultValue = "86") short countryCode,
			@RequestParam(value = Constants.CONS_LAT, required = false, defaultValue = "0") double lat,
			@RequestParam(value = Constants.CONS_LNG, required = false, defaultValue = "0") double lng,
			HttpServletRequest request) throws SrvException, TException {
		// TODO 验证 手机号码
		
		// 验证 aes 是否 被 有效的 rsa public key 加密
		String aeskey = null;
		try {
			aeskey = new String(RSAUtils.decryptByPrivateKey(
					__aeskey.getBytes(), getRSAPrivateKey()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResponseMessssage.INVALID_AESKEY;
		}

		// TDODO 验证 captcha -- redis 缓存

		CustomerSession session = new CustomerSession();
		session.setAeskey(aeskey);
		session.setCityId(cityID);
		session.setLat(lat);
		session.setLng(lng);

		session = customerSrv.login(phone, password, countryCode, session);

		Map<String, Object> result = new HashMap<>();
		result.put(Constants.CONS_TOKEN, session.getToken());
		try {
			result.put(Constants.CONS_SECRET, AESUtils.encrypt(
					session.getSecret(), aeskey));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResponseMessssage.INVALID_AESKEY;
		}
		return ResponseMessssage.OK(result);

	}
}
