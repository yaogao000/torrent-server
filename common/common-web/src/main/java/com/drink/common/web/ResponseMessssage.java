package com.drink.common.web;

import java.io.IOException;
import java.text.MessageFormat;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drink.common.JSONUtils;

@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "status_code", "message", "data" })
public class ResponseMessssage {
	private final static Logger logger = LoggerFactory.getLogger(ResponseMessssage.class);
	
	@JsonProperty("status_code")
	private int statusCode;
	private String message;
	private Object data;

	public static final int STATUS_CODE_200 = 200;
	public static final int STATUS_CODE_400 = 400;
	public static final int STATUS_CODE_403 = 403;
	public static final int STATUS_CODE_404 = 404;
	public static final String STATUS_MESSAGE_200 = "Request Successfully";
	public static final String STATUS_MESSAGE_400 = "Request Error!";

	public final static String FORMAT_ERROR_ILLEGAL = "Invalid {0} with {1}";
	public final static String FORMAT_ERROR_REQUIRED = "Required parameter {0} is not present";
	public final static String FORMAT_ERROR_UNSUPPORT = "Unsupport operation";
	
	public static final ResponseMessssage NULL_SIG = new ResponseMessssage(100,"Invalid signature,is null!");
	public static final ResponseMessssage INVALID_SIG = new ResponseMessssage(100,"Invalid signature");
	public static final ResponseMessssage INVALID_TOKEN = new ResponseMessssage(101,"Invalid token or appkey!");
	public static final ResponseMessssage NULL_TOKEN = new ResponseMessssage(101,"Invalid token or appkey,is null!");
	public static final ResponseMessssage INVALID_AESKEY = new ResponseMessssage(102,"Invalid __aeskey!");
	public static final ResponseMessssage IP_REQ_OVERFLOW = new ResponseMessssage(103,"Request ip over flow");
	public static final ResponseMessssage REQ_OVERFLOW = new ResponseMessssage(103,"Request over flow");
	public static final ResponseMessssage FILTER_UNKNOW_ERROR = new ResponseMessssage(104,"Filter Unkown Error");
	
	public static String NULL_SIG_STR;
	public static String INVALID_SIG_STR;
	public static String INVALID_TOKEN_STR;
	public static String NULL_TOKEN_STR;
	public static String INVALID_AESKEY_STR;
	public static String IP_REQ_OVERFLOW_STR;
	public static String REQ_OVERFLOW_STR;
	public static String FILTER_UNKNOW_ERROR_STR;
	static{
		try {
			NULL_SIG_STR = JSONUtils.readObject2String(NULL_SIG);
			INVALID_SIG_STR = JSONUtils.readObject2String(INVALID_SIG);
			INVALID_TOKEN_STR = JSONUtils.readObject2String(INVALID_TOKEN);
			NULL_TOKEN_STR = JSONUtils.readObject2String(NULL_TOKEN);
			INVALID_AESKEY_STR = JSONUtils.readObject2String(INVALID_SIG);
			IP_REQ_OVERFLOW_STR = JSONUtils.readObject2String(IP_REQ_OVERFLOW);
			REQ_OVERFLOW_STR = JSONUtils.readObject2String(REQ_OVERFLOW);
			FILTER_UNKNOW_ERROR_STR = JSONUtils.readObject2String(FILTER_UNKNOW_ERROR);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
	}

	public static String buildIllegalMessage(String key, String value) {
		return MessageFormat.format(FORMAT_ERROR_ILLEGAL, "'" + key + "'", value);
	}

	public static String buildRequiredErrorMessage(String key) {
		return MessageFormat.format(FORMAT_ERROR_REQUIRED, "'" + key + "'");
	}
	
	public static String buildUnsupportedOperationMessage() {
		return FORMAT_ERROR_UNSUPPORT;
	}

	public static ResponseMessssage OK() {
		return new ResponseMessssage(STATUS_CODE_200, STATUS_MESSAGE_200);
	}

	public static ResponseMessssage OK(Object data) {
		return new ResponseMessssage(STATUS_CODE_200, STATUS_MESSAGE_200, data);
	}

	public static ResponseMessssage ERROR(int code) {
		return new ResponseMessssage(code);
	}

	public static ResponseMessssage ERROR(Object data) {
		return new ResponseMessssage(STATUS_CODE_400, STATUS_MESSAGE_400, data);
	}

	public static ResponseMessssage ERROR(int code, String message) {
		return new ResponseMessssage(code, message);
	}

	public static ResponseMessssage ERROR(String message) {
		return new ResponseMessssage(STATUS_CODE_400, message);
	}

	public static ResponseMessssage ERROR() {
		return new ResponseMessssage(STATUS_CODE_400, STATUS_MESSAGE_400);
	}

	// public ResponseMessssage() {
	// super();
	// }

	public ResponseMessssage(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public ResponseMessssage(int statusCode, String message, Object data) {
		super();
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}

	public ResponseMessssage(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
