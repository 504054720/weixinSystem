/*
Navicat MySQL Data Transfer

Source Server         : localmysql
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : newdb

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2019-04-24 11:52:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_sign_record
-- ----------------------------
DROP TABLE IF EXISTS `tb_sign_record`;
CREATE TABLE `tb_sign_record` (
  `id` varchar(55) NOT NULL COMMENT '主键',
  `rule_id` int(11) DEFAULT NULL COMMENT '打卡规则ID',
  `user_id` varchar(50) DEFAULT NULL COMMENT '打卡人企业微信账号',
  `mobile` varchar(12) DEFAULT NULL COMMENT '打卡人企业微信手机号',
  `department` char(4) DEFAULT NULL COMMENT '打卡人所属部门',
  `longitude` varchar(15) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(15) DEFAULT NULL COMMENT '纬度',
  `work_time` datetime DEFAULT NULL COMMENT '上班打卡时间',
  `off_work_time` datetime DEFAULT NULL COMMENT '下班打卡时间',
  `state` varchar(20) DEFAULT NULL COMMENT '打卡状态（a：正常；b：迟到；c：早退；d 或者 null：缺打卡）',
  `sign_time` varchar(20) DEFAULT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
