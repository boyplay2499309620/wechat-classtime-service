/*
Navicat MySQL Data Transfer

Source Server         : webdb
Source Server Version : 50721
Source Host           : 127.0.0.1:3306
Source Database       : classtime

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2019-02-24 20:23:26
*/
create database classtime default character set utf8;

use classtime;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `room_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '房间id，唯一',
  `room_number` int(4) NOT NULL COMMENT '房间码',
  `open_id` varchar(30) NOT NULL COMMENT '创建者',
  `room_state` int(1) NOT NULL DEFAULT '1' COMMENT '是否有效，有效期为两小时，1代表有效，0代表无效',
  `room_peoples` int(3) DEFAULT '0' COMMENT '房间人数',
  `remark` char(20) DEFAULT NULL COMMENT'房间备注',
  `create_time` char(30) DEFAULT NULL COMMENT'创建时间',
  PRIMARY KEY (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8 COMMENT='用于保存房间本身的基本信息，保留一条系统记录，用作表示未加入房间的情况，id为1，state永远为1';


-- ----------------------------
-- Records of room
-- ----------------------------

-- ----------------------------
-- Table structure for room_people
-- ----------------------------
DROP TABLE IF EXISTS `room_people`;
CREATE TABLE `room_people` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT'这条记录的主键，系统自动累加',
  `open_id` varchar(30) NOT NULL COMMENT'提交记录的人',
    `name` char(10) DEFAULT NULL COMMENT '昵称或姓名，绑定姓名则保存姓名，否则显示昵称',
  `room_id` int(10) NOT NULL COMMENT'加入的房间id，不是房间号',
  `begain_time` char(30) DEFAULT NULL COMMENT '加入房间的时间',
  `times` int(10) DEFAULT '0' COMMENT'在该房间类总共计时的时间',
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_people_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE,
  CONSTRAINT `room_people_ibfk_2` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='保存加入房间的人的信息，每个加入房间的人都会创建一条记录，以后可更新times字段';


-- ----------------------------
-- Records of room_people
-- ----------------------------

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `ids` char(5) DEFAULT NULL,
  `name` char(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用来做测试的表，可任意更改';

-- ----------------------------
-- Records of test
-- ----------------------------

-- ----------------------------
-- Table structure for time
-- ----------------------------
DROP TABLE IF EXISTS `time`;
CREATE TABLE `time` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT'系统生成的记录主键',
  `open_id` varchar(30) NOT NULL COMMENT'提交记录的人',
  `room_id` int(10) DEFAULT NULL comment'若加入房间则是房间记录的主键id，否则为1，表示为加入房间',
  `dates` char(30) DEFAULT NULL COMMENT'提交记录的日期，年-月-日',
  `begain` char(30) DEFAULT NULL COMMENT'本次记录开始的时间，时-分-秒',
  `ends` char(30) DEFAULT NULL COMMENT'本次记录结束的时间，时-分-秒',
  `pause` int(3) DEFAULT NULL comment'暂停次数，最后一次翻过手机并停止并不算暂停',
  `times` char(10) DEFAULT NULL comment'这一次计时的时间，若加入房间，在该房间内可产生多条记录，时间会累加到room_people的times字段上',
  `pause_msg` varchar(255) DEFAULT NULL comment'该计时区间内每次开始和暂停的时刻',
  `net_work_type` char(10) DEFAULT NULL comment'所用网络类型网络类型',
  PRIMARY KEY (`id`),
  KEY `open_id` (`open_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `time_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE,
  CONSTRAINT `time_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 comment='保存每按下stop按钮所提交的记录，即便同一房间类也可能产生多条记录';

-- ----------------------------
-- Records of time
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `open_id` varchar(30) NOT NULL comment'用户的openId',
  `school_id` char(15) DEFAULT NULL COMMENT '学号或教师号，可不绑定',
  `name` char(10) DEFAULT NULL comment'微信昵称',
  `session_key` char(30) DEFAULT NULL comment'系统key',
  `union_id` char(30) DEFAULT NULL comment'系统key',
  `school_name` char(10) DEFAULT NULL COMMENT '学校的姓名，可不绑定',
  PRIMARY KEY (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment='用户信息';

-- ----------------------------
-- Records of user
-- ----------------------------
insert into user value("1605010310","1000000000","系统管理员","","","系统管理员");

insert into room value(1,1,"1605010310",1,0,"系统测试房间","0000");

SET FOREIGN_KEY_CHECKS=1;