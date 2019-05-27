/*
Navicat MySQL Data Transfer

Source Server         : localmysql
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : newdb

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2019-04-26 09:21:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_user_rule
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_rule`;
CREATE TABLE `tb_user_rule` (
  `userid` varchar(30) NOT NULL,
  `ruleid` int(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
