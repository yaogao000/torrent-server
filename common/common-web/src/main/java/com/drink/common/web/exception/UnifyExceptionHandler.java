package com.drink.common.web.exception;

import java.io.PrintWriter;
import java.security.InvalidParameterException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.drink.common.JSONUtils;
import com.drink.common.web.ResponseMessssage;


public class UnifyExceptionHandler implements HandlerExceptionResolver {
	private static final Logger logger = LoggerFactory.getLogger(UnifyExceptionHandler.class);

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		// logger.error("Request URI with " + request.getRequestURI(), ex);
		ResponseMessssage resonMessssage = null;
		if (ex instanceof InvalidParameterException || ex instanceof IllegalArgumentException
				|| ex instanceof MissingServletRequestParameterException || ex instanceof UnsupportedOperationException) {
			resonMessssage = ResponseMessssage.ERROR(ex.getMessage());
		} else {
			resonMessssage = ResponseMessssage.ERROR();
			// only log the exception which is not InvalidParameterException or
			// IllegalArgumentException
			logger.error(resonMessssage.getMessage(), ex);
		}
		responseJson(response, resonMessssage, null);

		return null;
	}

	public void responseJson(HttpServletResponse response, Object data, String contentType) {

		// Specifies the format of the data returned as Json
		if (StringUtils.isEmpty(contentType)) {
			response.setContentType("application/json;charset=UTF-8");
		} else {
			response.setContentType(contentType);
		}

		PrintWriter printWriter = null;
		try {
			printWriter = response.getWriter();
			printWriter.print(JSONUtils.readObject2String(data));
			// Out put Json data
			printWriter.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (null != printWriter) {
				printWriter.close();
				printWriter = null;
			}
		}

	}
}
