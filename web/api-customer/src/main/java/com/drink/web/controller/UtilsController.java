package com.drink.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.drink.common.security.AESUtils;
import com.drink.common.security.Base64Utils;
import com.drink.common.security.RSAUtils;
import com.drink.common.web.ResponseMessssage;
import com.drink.common.web.filter.AbstractFirstFilter;
import com.drink.srv.security.IdEncoder;

/**
 * 重要！ 此controller只在测服打开，现有和新增方法必须判断isDebug
 */
@Controller
@RequestMapping(value = "/utils")
public class UtilsController extends BaseController {
	@Value("${debug.mode.controller}")
	private boolean isDebug;

	@RequestMapping(value = "/encoder.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage Encoder(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id", required = true) long id) {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}
		return ResponseMessssage.OK(IdEncoder.EncodeId(id));
	}

	@RequestMapping(value = "/decoder.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage Decoder(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id", required = true) long id) {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}

		return ResponseMessssage.OK(IdEncoder.DecodeId(id));
	}

	@RequestMapping(value = "/rsa_enc.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage RSAEncrypt(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "data", required = true) String data,
			@RequestParam(value = "key_type", required = true) String kt) throws Exception {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}

		if ("pri".equalsIgnoreCase(kt)) {
			return ResponseMessssage.OK(Base64Utils.encode(RSAUtils.encryptByPrivateKey(data.getBytes(), getRSAPrivateKey())));
		} else {
			return ResponseMessssage.OK(Base64Utils.encode(RSAUtils.encryptByPublicKey(data.getBytes(), getRSAPublicKey())));
		}
	}

	@RequestMapping(value = "/rsa_dec.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public Object RSADecrypt(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "data", required = true) String data,
			@RequestParam(value = "key_type", required = true) String kt) throws Exception {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}
		if ("pri".equalsIgnoreCase(kt)) {
			return ResponseMessssage.OK(Base64Utils.encode(RSAUtils.decryptByPrivateKey(data.getBytes(), getRSAPrivateKey())));
		} else {
			return ResponseMessssage.OK(Base64Utils.encode(RSAUtils.decryptByPrivateKey(data.getBytes(), getRSAPublicKey())));
		}
	}

	@RequestMapping(value = "/aes_enc.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage AESEncrypt(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "data", required = true) String data,
			@RequestParam(value = "pwd", required = true) String pwd) throws Exception {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}

		if (pwd.length() % 16 == 0) {
			return ResponseMessssage.INVALID_AESKEY;
		}

		return ResponseMessssage.OK(Base64Utils.encode(AESUtils.encrypt(data, pwd)));
	}

	@RequestMapping(value = "/aes_dec.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage AESDecrypt(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "data", required = true) String data,
			@RequestParam(value = "pwd", required = true) String pwd) throws Exception {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}

		return ResponseMessssage.OK(Base64Utils.encode(AESUtils.decrypt(data, pwd)));
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sig.do", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ResponseMessssage sig(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "secret", required = true) String secret) {
		if (!isDebug) {
			return ResponseMessssage.ERROR();
		}

		return ResponseMessssage.OK(AbstractFirstFilter.renderSignature(request.getParameterMap(), secret));
	}
}
