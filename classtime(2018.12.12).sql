/*
Navicat MySQL Data Transfer

Source Server         : webdb
Source Server Version : 50721
Source Host           : 127.0.0.1:3306
Source Database       : classtime

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-12-12 09:14:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `room_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '房间id，唯一',
  `room_number` int(4) NOT NULL COMMENT '房间码',
  `open_id` varchar(30) NOT NULL COMMENT '创建者',
  `room_state` int(1) NOT NULL DEFAULT '1' COMMENT '是否有效，有效期为两小时',
  `room_peoples` int(3) DEFAULT '0' COMMENT '房间人数',
  `remark` char(20) DEFAULT NULL,
  `create_time` char(30) DEFAULT NULL,
  PRIMARY KEY (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for room_people
-- ----------------------------
DROP TABLE IF EXISTS `room_people`;
CREATE TABLE `room_people` (
  `open_id` varchar(30) NOT NULL,
  `room_id` int(10) NOT NULL,
  `begain_time` char(30) DEFAULT NULL COMMENT '加入房间的时间',
  `times` int(10) DEFAULT '0',
  `id` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `open_id` (`open_id`),
  CONSTRAINT `room_people_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE,
  CONSTRAINT `room_people_ibfk_2` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `ids` char(5) DEFAULT NULL,
  `name` char(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for time
-- ----------------------------
DROP TABLE IF EXISTS `time`;
CREATE TABLE `time` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `open_id` varchar(30) NOT NULL,
  `dates` char(30) DEFAULT NULL,
  `begain` char(30) DEFAULT NULL,
  `ends` char(30) DEFAULT NULL,
  `pause` int(3) DEFAULT NULL,
  `times` char(10) DEFAULT NULL,
  `pause_msg` varchar(255) DEFAULT NULL,
  `net_work_type` char(10) DEFAULT NULL,
  `room_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `open_id` (`open_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `time_ibfk_1` FOREIGN KEY (`open_id`) REFERENCES `user` (`open_id`) ON UPDATE CASCADE,
  CONSTRAINT `time_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `open_id` varchar(30) NOT NULL,
  `school_id` char(10) DEFAULT NULL,
  `name` char(10) DEFAULT NULL,
  `session_key` char(30) DEFAULT NULL,
  `union_id` char(30) DEFAULT NULL,
  PRIMARY KEY (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table room_people add column name char(10) comment"昵称或姓名";
alter table room_people add foreign key(name) references user(name) on update cascade;
alter table room_people add column state int(1) default"0" comment"状态，0为下线，1为在线";

alter table room_people drop state;










