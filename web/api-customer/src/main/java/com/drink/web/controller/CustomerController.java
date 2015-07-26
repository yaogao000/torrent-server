package com.drink.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.drink.common.web.ResponseMessssage;

@Controller
@RequestMapping(value = "/customer")
public class CustomerController extends BaseController {


	@RequestMapping(value = "login", produces = "application/json")
	@ResponseBody
	public ResponseMessssage signInUp(
			@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "captcha", required = true) String captcha,
			@RequestParam(value = "__aeskey", required = true) String __aeskey,
			@RequestParam(value = "cityId", required = false, defaultValue = "0") int cityID,
			@RequestParam(value = "countryCode", required = false, defaultValue = "86") int countryCode,
			@RequestParam(value = "lat", required = false, defaultValue = "0") double lat,
			@RequestParam(value = "lng", required = false, defaultValue = "0") double lng,
			HttpServletRequest request) {
		return ResponseMessssage.OK();

	}
}
