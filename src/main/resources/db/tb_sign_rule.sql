/*
Navicat MySQL Data Transfer

Source Server         : localmysql
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : newdb

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2019-04-26 09:21:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_sign_rule
-- ----------------------------
DROP TABLE IF EXISTS `tb_sign_rule`;
CREATE TABLE `tb_sign_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` char(1) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `department` varchar(10) DEFAULT NULL,
  `longitude` varchar(11) DEFAULT NULL,
  `latitude` varchar(11) DEFAULT NULL,
  `scope` int(3) DEFAULT NULL,
  `work_time` varchar(10) DEFAULT NULL,
  `off_work_time` varchar(10) DEFAULT NULL,
  `creat_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
