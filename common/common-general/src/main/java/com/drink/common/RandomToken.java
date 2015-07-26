package com.drink.common;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomToken {
	public String token;
	public String secret;

	private RandomToken() {
		this.token = RandomStringUtils.randomAlphanumeric(16)
				+ System.currentTimeMillis() / 1000;
		this.secret = RandomStringUtils.randomAlphanumeric(8)
				+ System.currentTimeMillis();
	}

	public static RandomToken build() {
		return new RandomToken();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
