DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer` (
  `cid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'customer_id',
  `mobile` char(11) NOT NULL COMMENT '个人账户手机',
  `nickname` varchar(60) NOT NULL DEFAULT '' COMMENT '个人昵称',
  `gender` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '性别(0-无; 1-男; 2-女)',
  `password` varchar(50) NOT NULL DEFAULT '' COMMENT '账户密码',
  `photo` varchar(120) NOT NULL DEFAULT '',
  `country_id` smallint(5) unsigned NOT NULL DEFAULT '1' COMMENT '国家ID',
  `city_id` int(20) unsigned NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cid`),
  UNIQUE KEY `Index_phone` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='乘客信息表';

DROP TABLE IF EXISTS `t_customer_session`;
CREATE TABLE `t_customer_session` (
  `cid` int(10) unsigned NOT NULL COMMENT '用户 id',
  `token` char(26) NOT NULL,
  `secret` char(21) NOT NULL COMMENT '与token成对出现, 唯一对应关系, 用于做MD5计算',
  `client` tinyint(3) unsigned DEFAULT '0' COMMENT '登陆来源: 0-WEB; 1-APP; ..',
  `lat` double DEFAULT NULL COMMENT '纬度 ',
  `lng` double DEFAULT NULL COMMENT '经度 ',
  `expire_at` bigint unsigned NOT NULL COMMENT 'token 失效 时间点',
  `status` tinyint(3) unsigned DEFAULT '1' COMMENT 'token 是否有效: 0-失效; 1-有效;',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cid`),
  UNIQUE KEY `AK_Key_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='乘客的登陆session 流水表\n';

insert into t_customer values(null, '13621700250', 'yaogao000',1,'dddddd','',1,1,sysdate(),sysdate());


