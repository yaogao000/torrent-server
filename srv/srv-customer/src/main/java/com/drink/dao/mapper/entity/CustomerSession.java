package com.drink.dao.mapper.entity;

public class CustomerSession extends BaseEntity {
	private long cid;
	private String token;
	private String secret;// 与token成对出现, 唯一对应关系, 用于做MD5计算
	private byte client;
	private double lat;
	private double lng;
	private long expireAt;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
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

	public byte getClient() {
		return client;
	}

	public void setClient(byte client) {
		this.client = client;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(long expireAt) {
		this.expireAt = expireAt;
	}

}
