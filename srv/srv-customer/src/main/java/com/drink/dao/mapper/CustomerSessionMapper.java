package com.drink.dao.mapper;

import com.drink.srv.info.CustomerSession;

/**
 * 每次都插入一条新的session信息: good: . 可以保存用户历史登陆信息，包括
 * 城市id，经纬度是否有变化，为新业务，如城市更换，提醒用户密码是否泄漏等 . 插入操作 变得 简单, 不需要 每次都从新检查 . bad: .
 * 这张表会无限增大， 可以加入 定时器，定时删除一个月或者一周之前的session信息 . 获取 session，以及 更新 expireAt 操作
 * 变得麻烦， 需要 order by create_time desc limit 1
 * 
 * session表里只维护一条 -- 这个 比较 靠谱 good: 表不会无限增大， 同时 也可以 服务于 提醒用户密码是否泄漏等业务，前提是
 * 每次都先查询一遍 获取 session，以及 更新 expireAt 操作 变得简单 bad: 插入之前需要检查是否已经存在, 如果已经存在则进行更新
 * 
 * @author yaogaolin
 * 
 */
public interface CustomerSessionMapper {
	/**
	 * 根据用户 token 获取 session 信息， [会检查 session 是否 失效， 如果已经失效， 则返回空]
	 * 
	 * @param token
	 * @return
	 */
	public CustomerSession getSessionByToken(String token);

	/**
	 * 根据用户id 获取 session 信息, 不会检查 session 是否 失效
	 * 
	 * @param cid
	 * @return
	 */
	public CustomerSession getSessionByCid(long cid);

	/**
	 * 保存用户 session 信息
	 * 
	 * @param session
	 */
	public void insert(CustomerSession session);

	/**
	 * 根据 token 获取 secret， [会检查 session 是否 失效， 如果已经失效， 则返回空]
	 * 
	 * @param token
	 * @return
	 */
	public String getSecretByToken(String token);

	/**
	 * 根据用户id 更新 session信息
	 * 
	 * @param session
	 */
	public void update(CustomerSession session);

	/**
	 * 根据 token 失效 用户session
	 * 
	 * @param token
	 */
	public void expireSession(String token);
}
