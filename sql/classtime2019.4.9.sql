/*
Navicat MySQL Data Transfer

Source Server         : webdb
Source Server Version : 50721
Source Host           : 127.0.0.1:3306
Source Database       : classtime

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-04-09 21:36:17
*/

drop database if exists classtime ;
create database classtime DEFAULT CHARACTER SET utf8;
use classtime;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for phone_model
-- ----------------------------
DROP TABLE IF EXISTS `phone_model`;
CREATE TABLE `phone_model` (
  `model_id` int(10) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(30) NOT NULL COMMENT '用户的openId',
  `brands` varchar(50) DEFAULT '' COMMENT '设备品牌',
  `models` varchar(50) DEFAULT '' COMMENT '设备型号',
  `wechat_languages` varchar(50) DEFAULT '' COMMENT '微信设置的语言',
  `wechat_version` varchar(50) DEFAULT '' COMMENT '微信版本号',
  `phone_system` varchar(50) DEFAULT '' COMMENT '操作系统及其版本',
  `wechat_platform` varchar(50) DEFAULT '' COMMENT '客户端平台',
  `sdk_version` varchar(50) DEFAULT '' COMMENT '客户端基础库版本',
  PRIMARY KEY (`model_id`),
  KEY `phone_model_ibfk_1` (`open_id`),
  CONSTRAINT `phone_model_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='保留用户设备信息';

-- ----------------------------
-- Table structure for ranking
-- ----------------------------
DROP TABLE IF EXISTS `ranking`;
CREATE TABLE `ranking` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '系统生成的记录主键',
  `open_id` varchar(30) NOT NULL COMMENT '用户信息',
  `times` int(10) DEFAULT '0' COMMENT '总的秒数',
  PRIMARY KEY (`id`),
  KEY `ranking_ibfk_1` (`open_id`),
  CONSTRAINT `ranking_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='存放所有人的时间总记录';

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `room_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '房间id，唯一',
  `room_number` int(10) NOT null COMMENT '房间码',
  `open_id` varchar(30) NOT null COMMENT '创建者',
  `room_state` int(1) NOT null DEFAULT '1' COMMENT '是否有效，有效期为两小时，1代表有效，0代表无效',
  `room_peoples` int(5) DEFAULT '0' COMMENT '房间人数',
  `remark` char(50) DEFAULT '未说明备注信息' COMMENT '房间备注',
  `create_time` char(30) DEFAULT '' COMMENT '创建时间',
  PRIMARY KEY (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用于保存房间本身的基本信息，保留一条系统记录，用作表示未加入房间的情况，id为1，state永远为1';

-- ----------------------------
-- Table structure for room_people
-- ----------------------------
DROP TABLE IF EXISTS `room_people`;
CREATE TABLE `room_people` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '这条记录的主键，系统自动累加',
  `open_id` varchar(30) NOT NULL COMMENT '提交记录的人',
  `name` char(30) DEFAULT '' COMMENT '昵称或姓名，绑定姓名则保存姓名，否则显示昵称',
  `room_id` int(10) NOT NULL COMMENT '加入的房间id，不是房间号',
  `begain_time` char(30) DEFAULT '' COMMENT '加入房间的时间',
  `times` int(10) DEFAULT '0' COMMENT '在该房间类总共计时的时间',
  `school_id` char(15) DEFAULT '' COMMENT '学号，没有就为空',
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_people_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE,
  CONSTRAINT `room_people_ibfk_2` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='保存加入房间的人的信息，每个加入房间的人都会创建一条记录，以后可更新times字段';

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `ids` int(5) NOT NULL AUTO_INCREMENT,
  `name` char(5) DEFAULT '',
  PRIMARY KEY (`ids`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用来做测试的表，可任意更改';

-- ----------------------------
-- Table structure for time
-- ----------------------------
DROP TABLE IF EXISTS `time`;
CREATE TABLE `time` (
  `time_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '系统生成的记录主键',
  `open_id` varchar(30) NOT NULL COMMENT '提交记录的人',
  `room_id` int(10) DEFAULT '1' COMMENT '若加入房间则是房间记录的主键id，否则为1，表示未加入房间',
  `dates` char(30) DEFAULT '' COMMENT '提交记录的日期，年-月-日',
  `begain` char(30) DEFAULT '' COMMENT '本次记录开始的时间，时-分-秒',
  `ends` char(30) DEFAULT '' COMMENT '本次记录结束的时间，时-分-秒',
  `pause` int(3) DEFAULT '0' COMMENT '暂停次数，最后一次翻过手机并停止并不算暂停',
  `times` int(10) DEFAULT '0' COMMENT '保存时间记录，以秒为单位',
  `pause_msg` varchar(255) DEFAULT '0' COMMENT '该计时区间内每次开始和暂停的时刻',
  `net_work_type` char(10) DEFAULT '' COMMENT '所用网络类型网络类型',
  PRIMARY KEY (`time_id`),
  KEY `open_id` (`open_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `time_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE,
  CONSTRAINT `time_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='保存每按下stop按钮所提交的记录，即便同一房间类也可能产生多条记录';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` ( 
  `open_id` varchar(30) NOT NULL COMMENT '用户的openId',
  `school_id` char(15) DEFAULT '' COMMENT '学号或教师号，可不绑定',
  `nick_name` char(30) DEFAULT '' COMMENT '微信昵称',
  `session_key` char(30) DEFAULT '' COMMENT '系统key',
  `union_id` char(30) DEFAULT '' COMMENT '系统key',
  `school_name` char(30) DEFAULT '' COMMENT '学校的姓名，可不绑定',
  `sex` int(1) DEFAULT '0' COMMENT '0代表男，1代表女',
  `avatar_url` varchar(255) DEFAULT '' COMMENT '用户头像网络地址',
  PRIMARY KEY (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息';


-- 务必插入的两条语句
INSERT INTO `user` VALUES ('1000000000', '1000000000', '系统管理员', '', '', '系统管理员',1,'');
INSERT INTO `room` VALUES ('1', '1', '1605010310', '1', '0', '系统测试房间', '2019-4-9 22:08:07');

