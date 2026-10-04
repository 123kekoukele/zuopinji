-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: hadluo-lvyou
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `banjixinxi`
--

DROP TABLE IF EXISTS `banjixinxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banjixinxi` (
  `id` bigint NOT NULL COMMENT '主键',
  `banjimingcheng` varchar(100) DEFAULT NULL COMMENT '班级名称',
  `zhuanye` varchar(200) DEFAULT NULL COMMENT '所属专业',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banjixinxi`
--

LOCK TABLES `banjixinxi` WRITE;
/*!40000 ALTER TABLE `banjixinxi` DISABLE KEYS */;
INSERT INTO `banjixinxi` VALUES (1774150328376,'2','风景园林','2026-03-22 11:32:07'),(1779447779146,'1','地理信息科学','2026-05-22 19:02:58'),(1779447821050,'2','地理信息科学','2026-05-22 19:03:40');
/*!40000 ALTER TABLE `banjixinxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '配置参数值',
  `addtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='配置文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (4,'system_open_start','2026-03-20 21:00:00',NULL),(5,'system_open_end','2026-06-03 00:00:00',NULL),(6,'system_graduation_year','2026',NULL),(7,'system_college_name','地理与空间信息学院',NULL);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dabianlunwen`
--

DROP TABLE IF EXISTS `dabianlunwen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dabianlunwen` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `lunwenjianjie` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '论文简介',
  `lunwenfujian` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '论文附件',
  `tijiaoshijian` datetime DEFAULT NULL COMMENT '提交时间',
  `pingfenzhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '评分状态',
  `shenhezhuangtai` varchar(200) DEFAULT '未审核' COMMENT '审核状态',
  `shenheyuanyin` varchar(500) DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773320165249 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='答辩论文';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dabianlunwen`
--

LOCK TABLES `dabianlunwen` WRITE;
/*!40000 ALTER TABLE `dabianlunwen` DISABLE KEYS */;
INSERT INTO `dabianlunwen` VALUES (1751080298887,'2025-06-28 03:11:38','1751077910324','基于深度学习的电商平台虚假评论检测系统设计与实现','计算机科学','计算机科学与技术（或人工智能、软件工程等相关专业）','应用研究（结合深度学习技术解决实际问题）\n系统开发（需完成算法设计、数据处理、系统实现及测试）','110111','hadluo老师','110110','hadluo','','file/1751080297588.pdf','2025-06-28 11:11:30','已评分'),(1751248489591,'2025-06-30 01:54:49','1751078340973','基于博弈论的共享经济平台动态定价策略研究','博弈论','经济学（或应用数学、计算机科学、管理科学与工程等相关专业）','理论研究（博弈模型构建与均衡分析）\n实证研究（结合实际数据验证模型有效性）','110111','hadluo老师','110110','hadluo','','file/1751248487991.pdf','2025-06-30 09:54:40','已评分'),(1751249595861,'2025-06-30 02:13:15','1751078470981','短视频成瘾行为的心理机制及干预策略研究——基于大学生群体的实证调查','心理学','应用心理学（或临床心理学、教育心理学等相关方向）','实证研究（问卷调查/实验研究）\n应用研究（干预方案设计）','110111','hadluo老师','110110','hadluo','','file/1751249594130.pdf','2025-06-30 10:13:04','已评分'),(1768377618668,'2026-01-14 08:00:18','1751078340973','基于博弈论的共享经济平台动态定价策略研究','博弈论','经济学（或应用数学、计算机科学、管理科学与工程等相关专业）','理论研究（博弈模型构建与均衡分析）\n实证研究（结合实际数据验证模型有效性）','110111','hadluo老师','110110','hadluo','','','2026-01-14 16:00:16','未评分'),(1773320165248,'2026-03-12 12:56:05','1','2012-2024年鄱阳湖叶绿素a时空特征分析','GIS空间分析与建模类','地理信息','','admin','','110114','赵六','最终初稿','','2026-03-12 20:55:12','已评分');
/*!40000 ALTER TABLE `dabianlunwen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daibanxinxi`
--

DROP TABLE IF EXISTS `daibanxinxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daibanxinxi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `biaoti` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '标题',
  `neirong` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '内容',
  `tixingshijian` datetime DEFAULT NULL COMMENT '提醒时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=157 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='待办信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daibanxinxi`
--

LOCK TABLES `daibanxinxi` WRITE;
/*!40000 ALTER TABLE `daibanxinxi` DISABLE KEYS */;
INSERT INTO `daibanxinxi` VALUES (151,'2024-02-26 20:32:51','学号1','学生姓名1','标题1','内容1','2024-02-27 12:32:51'),(152,'2024-02-26 20:32:51','学号2','学生姓名2','标题2','内容2','2024-02-27 12:32:51'),(153,'2024-02-26 20:32:51','学号3','学生姓名3','标题3','内容3','2024-02-27 12:32:51'),(154,'2024-02-26 20:32:51','学号4','学生姓名4','标题4','内容4','2024-02-27 12:32:51'),(155,'2024-02-26 20:32:51','学号5','学生姓名5','标题5','内容5','2024-02-27 12:32:51'),(156,'2024-02-26 20:32:51','学号6','学生姓名6','标题6','内容6','2024-02-27 12:32:51');
/*!40000 ALTER TABLE `daibanxinxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jiaoshi`
--

DROP TABLE IF EXISTS `jiaoshi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jiaoshi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '教师工号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '教师姓名',
  `touxiang` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '头像',
  `lianxidianhua` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '联系电话',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '性别',
  `zhuanye` varchar(200) DEFAULT NULL COMMENT '专业',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `jiaoshigonghao` (`jiaoshigonghao`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1776435248811 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='教师';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jiaoshi`
--

LOCK TABLES `jiaoshi` WRITE;
/*!40000 ALTER TABLE `jiaoshi` DISABLE KEYS */;
INSERT INTO `jiaoshi` VALUES (1772774022771,'2026-03-06 05:13:42','admin','$2a$10$1eoaJovuM80UwPL7x3.oFeWkhp.l8FVThD6jk1x7b/PO5mtFxjdzK','教师2','',NULL,'男','地理信息科学'),(1772774138454,'2026-03-06 05:15:38','teacher','123456','教师1','',NULL,'女','地理信息科学'),(1773229806734,'2026-03-11 11:50:06','admin3','$2a$10$UqL9wkhlrSWqJ4P77wS2wu0jMFb8hgXQ6RRQaSzZC6e5O8w1Pb5eu','teacher4','','17557687699','女','地理信息科学'),(1773229880263,'2026-03-11 11:51:20','admin1','123456','教师5','','17318907654','男','地理信息科学'),(1773229922193,'2026-03-11 11:52:02','admin2','$2a$10$IldiIGAKzTN.EqRDEFHgYu3iLzlUtmmnMs9BztttEZLEIFPlIJFX.','教师1','','','男','地理信息科学'),(1773229979515,'2026-03-11 11:52:59','admin4','$2a$10$2B0EQxt.Zwu4FEgKdIb9ROZRl9vqfjEM03qFpVbenx.QQPwxAN532','教师6','','','女','地理信息科学'),(1773230047379,'2026-03-11 11:54:07','admin5','123456','教师7','','','女','地理信息科学'),(1773230063120,'2026-03-11 11:54:23','admin8','123456','教师8','','','男','地理信息科学'),(1773230079387,'2026-03-11 11:54:39','admin9','$2a$10$3j4WC/rHHVvUtFUyi2.yQermpiXoe3RZFN6/IaLNUHqSGgZNmd1JG','教师9','','','男','地理信息科学'),(1773230111878,'2026-03-11 11:55:11','admin11','123456','教师11','','','女','地理信息科学'),(1776435244320,'2026-04-17 14:14:08','T2024001','$2a$10$KnwkF4U8dFMAkmAGErjK6OkUiVpOEOCgyluzMFr.DYfHi9UAZH5UG','张建国',NULL,'13800131234','男','地理信息科学'),(1776435244746,'2026-04-17 14:14:08','T2024002','$2a$10$XV8jRWkfZzSiADxvkakrx.XGrfv5Q7nra6a2LQuluV1ERCZP8w7Ze','李美玲',NULL,'13900235678','女','地理信息科学'),(1776435245157,'2026-04-17 14:14:08','T2024005','$2a$10$HG2KxcPaig3Zj5Uwu752RO/G5MjKZLFNJxd21j8hr4IzxO/s8/3aK','陈明辉',NULL,'13500537788','男','风景园林'),(1776435245226,'2026-04-17 14:14:08','T2024004','$2a$10$i2AIrYgywkbqZyTFZPLnju84lGBuZjGn2TmWEUQJEm1/M6z3kLfwy','刘芳',NULL,'13600434455','女','地理科学'),(1776435245635,'2026-04-17 14:14:08','T2024003','$2a$10$LiLLgWsIQq1XI4EykoEgt.6xhmeUgP9HSKB4KWUtADJzveCP1iuPa','王志强',NULL,'13700331122','男','地理信息科学'),(1776435246806,'2026-04-17 14:14:08','T2024006','$2a$10$Dd8crR./sM03mI58T6UcR.sOdgYhMM8HX7D88tclz90axWKF1fMNG','赵晓燕',NULL,'13400631237','女','地理信息科学'),(1776435247210,'2026-04-17 14:14:08','T2024007','$2a$10$sfIVya5uamNbuob0bnhY5eihf9kMzCq1LE4AeVVZMH9UvQLB3Nqzy','孙海波',NULL,'13300734561','男','地理科学'),(1776435247492,'2026-04-17 14:14:08','T2024008','$2a$10$HmnRP.NWDWCrwzE2pm0Iz.LjwOUOhlHAOUlwldXGA0TdKNOIGoc3W','周雪梅',NULL,'13200837894','女','测绘工程'),(1776435248577,'2026-04-17 14:14:08','T2024009','$2a$10$KJond9NDwFbr0xJCgmlR2.nxG1q7uPQGEn.5cjLQGgr90FK49jdDK','吴国强',NULL,'13100933216','男','城乡规划'),(1776435248810,'2026-04-17 14:14:08','T2024010','$2a$10$ZVOeMZcPNQx0I.G.LMcEAuFElCH3eBQ5XysvIaUZ7b4O6gVTtxDZ6','郑小红',NULL,'13001036549','女','地理信息科学');
/*!40000 ALTER TABLE `jiaoshi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kaitibaogao`
--

DROP TABLE IF EXISTS `kaitibaogao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `kaitibaogao` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `kaitibaogao` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '开题报告',
  `tijiaoshijian` datetime DEFAULT NULL COMMENT '提交时间',
  `shenhezhuangtai` varchar(200) DEFAULT '未审核' COMMENT '审核状态',
  `shenheyuanyin` varchar(500) DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773588100278 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='开题报告';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kaitibaogao`
--

LOCK TABLES `kaitibaogao` WRITE;
/*!40000 ALTER TABLE `kaitibaogao` DISABLE KEYS */;
INSERT INTO `kaitibaogao` VALUES (1751079034892,'2025-06-28 02:50:33','1751077910324','基于深度学习的电商平台虚假评论检测系统设计与实现','计算机科学','计算机科学与技术（或人工智能、软件工程等相关专业）','应用研究（结合深度学习技术解决实际问题）\n系统开发（需完成算法设计、数据处理、系统实现及测试）','110111','hadluo老师','110110','hadluo','file/1751079032765.pdf','2025-06-28 10:49:59'),(1751247661680,'2025-06-30 01:41:01','1751078340973','基于博弈论的共享经济平台动态定价策略研究','博弈论','经济学（或应用数学、计算机科学、管理科学与工程等相关专业）','理论研究（博弈模型构建与均衡分析）\n实证研究（结合实际数据验证模型有效性）','110111','hadluo老师','110110','hadluo','file/1751247659895.pdf','2025-06-30 09:40:51'),(1751249502672,'2025-06-30 02:11:41','1751078470981','短视频成瘾行为的心理机制及干预策略研究——基于大学生群体的实证调查','心理学','应用心理学（或临床心理学、教育心理学等相关方向）','实证研究（问卷调查/实验研究）\n应用研究（干预方案设计）','110111','hadluo老师','110110','hadluo','file/1751249500538.pdf','2025-06-30 10:11:37'),(1768396159073,'2026-01-14 13:09:18','1768310556714','微信小程序 + 高德地图 API 的校园导览系统','WebGIS / 移动GIS 开发类','地信','本课题属于地理信息科学专业中的“移动GIS应用开发类”实践型毕业设计，具有鲜明的技术集成性、应用导向性和空间服务特征，符合地信专业培养“地理+信息技术”复合能力的目标。','110111','hadluo老师','110110','hadluo','file/1768396154999.pdf','2026-01-14 21:09:02'),(1773318769918,'2026-03-12 12:32:49','1','2012-2024年鄱阳湖叶绿素a时空特征分析','GIS空间分析与建模类','地理信息','','admin','','110114','赵六','file/1773320322998.pptx','2026-03-12 20:32:25'),(1773413602088,'2026-03-13 14:53:21','1773235419536','多源时空数据融合下的郑州市城市空间结构动态演化与机制分析','GIS空间分析与建模类','地理信息','应用研究（公共健康地理）','admin','教师2','110210','赵十一','file/1773413600183.pptx','2026-03-13 22:52:46'),(1773588100277,'2026-03-15 15:21:39','1773235105013','基于多源遥感数据的中国十大湖泊时空变化及原因分析','遥感图像处理与解译类','遥感','应用基础研究（宏观地理）','admin','教师2','110121','李九','file/1773588097956.docx','2026-03-15 23:20:15');
/*!40000 ALTER TABLE `kaitibaogao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lunwenchugao`
--

DROP TABLE IF EXISTS `lunwenchugao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lunwenchugao` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `chugaojianjie` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '初稿简介',
  `chugaofujian` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '初稿附件',
  `chugaoshijian` datetime DEFAULT NULL COMMENT '初稿时间',
  `shenhezhuangtai` varchar(200) DEFAULT '未审核' COMMENT '审核状态',
  `shenheyuanyin` varchar(500) DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773644547662 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='论文初稿';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lunwenchugao`
--

LOCK TABLES `lunwenchugao` WRITE;
/*!40000 ALTER TABLE `lunwenchugao` DISABLE KEYS */;
INSERT INTO `lunwenchugao` VALUES (141,'2024-02-26 20:32:51','题目编号1','课题名称1','题目类型1','专业1','课题性质1','教师工号1','教师姓名1','学号1','学生姓名1','初稿简介1','','2024-02-27 12:32:51'),(142,'2024-02-26 20:32:51','题目编号2','课题名称2','题目类型2','专业2','课题性质2','教师工号2','教师姓名2','学号2','学生姓名2','初稿简介2','','2024-02-27 12:32:51'),(143,'2024-02-26 20:32:51','题目编号3','课题名称3','题目类型3','专业3','课题性质3','教师工号3','教师姓名3','学号3','学生姓名3','初稿简介3','','2024-02-27 12:32:51'),(144,'2024-02-26 20:32:51','题目编号4','课题名称4','题目类型4','专业4','课题性质4','教师工号4','教师姓名4','学号4','学生姓名4','初稿简介4','','2024-02-27 12:32:51'),(145,'2024-02-26 20:32:51','题目编号5','课题名称5','题目类型5','专业5','课题性质5','教师工号5','教师姓名5','学号5','学生姓名5','初稿简介5','','2024-02-27 12:32:51'),(146,'2024-02-26 20:32:51','题目编号6','课题名称6','题目类型6','专业6','课题性质6','教师工号6','教师姓名6','学号6','学生姓名6','初稿简介6','','2024-02-27 12:32:51'),(1751080170270,'2025-06-28 03:09:29','1751077910324','基于深度学习的电商平台虚假评论检测系统设计与实现','计算机科学','计算机科学与技术（或人工智能、软件工程等相关专业）','应用研究（结合深度学习技术解决实际问题）\n系统开发（需完成算法设计、数据处理、系统实现及测试）','110111','hadluo老师','110110','hadluo','','file/1751080167792.pdf','2025-06-28 11:09:08'),(1751247717221,'2025-06-30 01:41:56','1751078340973','基于博弈论的共享经济平台动态定价策略研究','博弈论','经济学（或应用数学、计算机科学、管理科学与工程等相关专业）','理论研究（博弈模型构建与均衡分析）\n实证研究（结合实际数据验证模型有效性）','110111','hadluo老师','110110','hadluo','','file/1768377602642.jpg','2025-06-30 09:41:45'),(1751249544215,'2025-06-30 02:12:23','1751078470981','短视频成瘾行为的心理机制及干预策略研究——基于大学生群体的实证调查','心理学','应用心理学（或临床心理学、教育心理学等相关方向）','实证研究（问卷调查/实验研究）\n应用研究（干预方案设计）','110111','hadluo老师','110110','hadluo','撒大苏打实打实','file/1751249538970.pdf','2025-06-30 10:12:12'),(1773319791738,'2026-03-12 12:49:51','1','2012-2024年鄱阳湖叶绿素a时空特征分析','GIS空间分析与建模类','地理信息','','admin','','110114','赵六','上交初稿','file/1773320075984.txt','2026-03-12 20:49:30'),(1773644547661,'2026-03-16 07:02:26','1773235105013','基于多源遥感数据的中国十大湖泊时空变化及原因分析','遥感图像处理与解译类','遥感','应用基础研究（宏观地理）','admin','教师2','110121','李九','','file/1773644533840.docx','2026-03-16 15:01:43');
/*!40000 ALTER TABLE `lunwenchugao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `menujson` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '菜单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'2024-02-26 20:32:51','[{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-qrcode\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"学生\",\"menuJump\":\"列表\",\"tableName\":\"xuesheng\"},{\"appFrontIcon\":\"cuIcon-team\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"班级信息管理\",\"menuJump\":\"列表\",\"tableName\":\"banji\"}],\"fontClass\":\"icon-user2\",\"menu\":\"学生管理\",\"unicode\":\"&#xef98;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-similar\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"教师\",\"menuJump\":\"列表\",\"tableName\":\"jiaoshi\"}],\"fontClass\":\"icon-user8\",\"menu\":\"教师管理\",\"unicode\":\"&#xef9e;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-news\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"题目类型\",\"menuJump\":\"列表\",\"tableName\":\"timuleixing\"},{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"查看\",\"修改\",\"删除\"],\"menu\":\"题目信息\",\"menuJump\":\"列表\",\"tableName\":\"timuxinxi\"}],\"fontClass\":\"icon-common6\",\"menu\":\"题目信息管理\",\"unicode\":\"&#xedad;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\"],\"menu\":\"选题申请\",\"menuJump\":\"列表\",\"tableName\":\"xuantishenqing\"}],\"fontClass\":\"icon-common6\",\"menu\":\"选题申请管理\",\"unicode\":\"&#xedad;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-discover\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"开题报告\",\"menuJump\":\"列表\",\"tableName\":\"kaitibaogao\"}],\"fontClass\":\"icon-common44\",\"menu\":\"开题报告管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"论文初稿\",\"menuJump\":\"列表\",\"tableName\":\"lunwenchugao\"}],\"fontClass\":\"icon-common1\",\"menu\":\"论文初稿管理\",\"unicode\":\"&#xeda3;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-pic\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"答辩论文\",\"menuJump\":\"列表\",\"tableName\":\"dabianlunwen\"}],\"fontClass\":\"icon-common24\",\"menu\":\"论文答辩管理\",\"unicode\":\"&#xee07;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-qrcode\",\"buttons\":[\"查看\",\"删除\",\"评分统计\",\"首页统计\",\"首页总数\"],\"menu\":\"评分审核\",\"menuJump\":\"列表\",\"tableName\":\"pingfenshenhe\"}],\"fontClass\":\"icon-common10\",\"menu\":\"评分审核管理\",\"unicode\":\"&#xedd1;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"申请\"],\"menu\":\"题目信息\",\"menuJump\":\"列表\",\"tableName\":\"timuxinxi\"}],\"menu\":\"题目信息管理\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"选题申请\",\"menuJump\":\"列表\",\"tableName\":\"xuantishenqing\"}],\"fontClass\":\"icon-common6\",\"menu\":\"选题申请管理\",\"unicode\":\"&#xedad;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-discover\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"提交\",\"初稿\"],\"menu\":\"开题报告\",\"menuJump\":\"列表\",\"tableName\":\"kaitibaogao\"}],\"fontClass\":\"icon-common44\",\"menu\":\"开题报告管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"答辩论文\"],\"menu\":\"论文初稿\",\"menuJump\":\"列表\",\"tableName\":\"lunwenchugao\"}],\"fontClass\":\"icon-common1\",\"menu\":\"论文初稿管理\",\"unicode\":\"&#xeda3;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-pic\",\"buttons\":[\"查看\",\"删除\",\"修改\"],\"menu\":\"答辩论文\",\"menuJump\":\"列表\",\"tableName\":\"dabianlunwen\"}],\"fontClass\":\"icon-common24\",\"menu\":\"论文答辩管理\",\"unicode\":\"&#xee07;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-qrcode\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"评分审核\",\"menuJump\":\"列表\",\"tableName\":\"pingfenshenhe\"}],\"fontClass\":\"icon-common10\",\"menu\":\"评分审核管理\",\"unicode\":\"&#xedd1;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-clothes\",\"buttons\":[\"查看\"],\"menu\":\"我的收藏\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common10\",\"menu\":\"我的收藏管理\",\"unicode\":\"&#xedd1;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"申请\"],\"menu\":\"题目信息\",\"menuJump\":\"列表\",\"tableName\":\"timuxinxi\"}],\"menu\":\"题目信息管理\"}],\"hasBackLogin\":\"否\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"是\",\"hasFrontRegister\":\"是\",\"roleName\":\"学生\",\"tableName\":\"xuesheng\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"题目信息\",\"menuJump\":\"列表\",\"tableName\":\"timuxinxi\"},{\"appFrontIcon\":\"cuIcon-news\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"题目类型\",\"menuJump\":\"列表\",\"tableName\":\"timuleixing\"}],\"fontClass\":\"icon-common6\",\"menu\":\"题目信息管理\",\"unicode\":\"&#xedad;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"选题申请\",\"menuJump\":\"列表\",\"tableName\":\"xuantishenqing\"}],\"fontClass\":\"icon-common6\",\"menu\":\"选题申请管理\",\"unicode\":\"&#xedad;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-discover\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"开题报告\",\"menuJump\":\"列表\",\"tableName\":\"kaitibaogao\"}],\"fontClass\":\"icon-common44\",\"menu\":\"开题报告管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"论文初稿\",\"menuJump\":\"列表\",\"tableName\":\"lunwenchugao\"}],\"fontClass\":\"icon-common1\",\"menu\":\"论文初稿管理\",\"unicode\":\"&#xeda3;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-pic\",\"buttons\":[\"查看\",\"删除\",\"评分审核\"],\"menu\":\"答辩论文\",\"menuJump\":\"列表\",\"tableName\":\"dabianlunwen\"}],\"fontClass\":\"icon-common24\",\"menu\":\"论文答辩管理\",\"unicode\":\"&#xee07;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-qrcode\",\"buttons\":[\"查看\",\"修改\",\"删除\",\"评分统计\",\"首页统计\",\"首页总数\"],\"menu\":\"评分审核\",\"menuJump\":\"列表\",\"tableName\":\"pingfenshenhe\"}],\"fontClass\":\"icon-common10\",\"menu\":\"评分审核管理\",\"unicode\":\"&#xedd1;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"申请\"],\"menu\":\"题目信息\",\"menuJump\":\"列表\",\"tableName\":\"timuxinxi\"}],\"menu\":\"题目信息管理\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"教师\",\"tableName\":\"jiaoshi\"}]');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '标题',
  `introduction` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '简介',
  `picture` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图片',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773228943761 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='公告信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1768141775547,'2026-01-11 14:29:35','居民视角下公园绿地访问影响因素分析','随着城市化进程不断加快，城市居民对高品质公共空间的需求日益增长，公园绿地作为重要的生态与休闲载体，在提升人居环境、促进公众健康和增强社会凝聚力方面发挥着不可替代的作用。然而，并非所有居民都能公平、便捷地享受到公园绿地服务，其使用频率与满意度受到多种因素的综合影响。','file/1773227999842.jpg','<p>本研究以“居民视角”为核心，聚焦于个体层面的社会属性（如年龄、职业、收入）、空间感知（如距离、可达性、安全性）以及绿地自身特征（如面积、设施、景观质量）等因素，通过问卷调查、实地访谈与空间数据分析相结合的方法，探究影响居民访问公园绿地的关键驱动机制。研究以典型城市区域为案例，借助GIS空间分析与统计模型（如Logistic回归），量化各因素的影响程度，识别服务盲区与需求热点。</p>'),(1768143347127,'2026-01-11 14:55:46','基于多源数据的城市森林储量估算及时空变化规律研究——以沈阳市为例 ','城市森林在固碳释氧、改善人居环境方面具有重要生态价值。本研究以沈阳市为案例，融合遥感影像（Landsat/Sentinel-2）、高分辨率航拍、绿地普查及地面样地等多源数据，构建城市森林生物量估算模型，反演2000–2023年森林碳储量时空分布，并分析其演变规律与驱动机制。研究结合GIS空间分析与机器学习方法，识别绿化增减热点区域，旨在为沈阳市生态修复、“双碳”目标实现及绿色城市规划提供科学依据。','file/1773227891082.jpg','<h3 style=\"text-align: left;\"><span style=\"color: var(--color-heading);\">研究内容（精简版）</span></h3><ol><li style=\"text-align: left;\"><strong>数据整合</strong><span style=\"color: var(--color-fg-default);\">：收集2000–2023年沈阳市遥感影像、绿地矢量、POI及样地实测等多源数据。</span></li><li style=\"text-align: left;\"><strong>森林提取与建模</strong><span style=\"color: var(--color-fg-default);\">：利用NDVI、机器学习等方法提取城市森林，构建生物量反演模型。</span></li><li style=\"text-align: left;\"><strong>碳储量估算</strong><span style=\"color: var(--color-fg-default);\">：基于生物量计算碳储量，生成多年份空间分布图。</span></li><li style=\"text-align: left;\"><strong>时空变化分析</strong><span style=\"color: var(--color-fg-default);\">：通过趋势分析、景观指数和热点探测，揭示森林碳储量的演变特征。</span></li><li style=\"text-align: left;\"><strong>驱动机制与建议</strong><span style=\"color: var(--color-fg-default);\">：结合城市扩张与绿化政策，提出生态优化建议。</span></li></ol><p style=\"text-align: left;\"><img src=\"http://localhost:8080/hadluo-xt/file/1768143343992.png\" alt=\"\" data-href=\"\" style=\"width: 492.93px;height: 284.17px;\"></p>'),(1773228299029,'2026-03-11 11:24:58','基于RS与GIS的郑州市土地利用及景观格局变化分析','本研究利用多时相遥感影像（RS）与地理信息系统（GIS）技术，定量分析郑州市近20年土地利用类型的数量增减与空间转移规律。通过计算景观格局指数，揭示城市化进程中景观破碎化、连通性变化等特征，旨在为郑州国土空间规划与生态保护提供数据支撑。','file/1773228219118.jpg','<ol><li style=\"text-align: left;\"><strong>数据获取与分类</strong><span style=\"color: var(--color-fg-default);\">：选取Landsat/Sentinel卫星影像，解译提取耕地、林地、水域、建设用地等类型，并验证精度。</span></li><li style=\"text-align: left;\"><strong>时空变化分析</strong><span style=\"color: var(--color-fg-default);\">：统计各类用地面积变化，构建</span><strong>转移矩阵</strong><span style=\"color: var(--color-fg-default);\">，明确“谁变成了谁”（如耕地转建设用地的规模）。</span></li><li style=\"text-align: left;\"><strong>景观格局量化</strong><span style=\"color: var(--color-fg-default);\">：计算斑块密度、聚集度、分离度等指数，评估景观是趋向破碎还是连片。</span></li><li style=\"text-align: left;\"><strong>驱动机制探讨</strong><span style=\"color: var(--color-fg-default);\">：结合人口、GDP等数据，简要分析导致上述变化的自然与社会经济驱动因子。</span></li></ol>'),(1773228689956,'2026-03-11 11:31:29','基于GIS与RS的县域城乡居民点时空演变特征及影响因素研究','本研究利用多期遥感影像和地理信息系统（GIS），定量分析特定县域内城乡居民点在近几十年间的扩张模式、形态变化及空间分布规律。通过识别演变驱动因子，为县域城乡统筹规划和乡村振兴提供科学参考。','file/1773228669668.jpg','<ol><li style=\"text-align: left;\"><strong>数据与方法</strong><span style=\"color: var(--color-fg-default);\">：获取县域多时相高分辨率遥感影像，提取不同时期的城乡居民点范围。</span></li><li style=\"text-align: left;\"><strong>时空演变分析</strong><span style=\"color: var(--color-fg-default);\">：计算居民点面积变化率，分析其扩张方向、形态紧凑度及空间集聚特征。</span></li><li style=\"text-align: left;\"><strong>影响因素探究</strong><span style=\"color: var(--color-fg-default);\">：结合地形、交通、政策及社会经济数据，运用地理探测器等模型，量化自然与人文因素对居民点演变的影响程度。</span></li></ol>'),(1773228761121,'2026-03-11 11:32:40','颍河流域生态系统健康评估','本研究构建一套综合评价指标体系，对颍河流域的生态系统健康状况进行定量评估与空间分异分析。旨在诊断流域生态问题，为水资源保护和流域综合治理提供决策支持。','file/1773228744719.jpg','<ol><li style=\"text-align: left;\"><strong>指标体系构建</strong><span style=\"color: var(--color-fg-default);\">：从“活力-组织结构-恢复力”（VOR）框架出发，选取水质、水量、生物多样性、土地利用等指标。</span></li><li style=\"text-align: left;\"><strong>健康指数计算</strong><span style=\"color: var(--color-fg-default);\">：利用遥感与地面监测数据，计算各子流域的生态系统健康指数（EHI）。</span></li><li style=\"text-align: left;\"><strong>空间格局与诊断</strong><span style=\"color: var(--color-fg-default);\">：绘制健康状况空间分布图，识别健康、亚健康及不健康区域，并分析其成因。</span></li></ol>'),(1773228942487,'2026-03-11 11:35:42','基于卫星光学影像配准的无人机SAR影像几何精校正方法','针对无人机合成孔径雷达（SAR）影像几何畸变严重的问题，提出一种利用高精度卫星光学影像作为基准进行配准的精校正方法，以提升无人机SAR影像的定位精度和应用价值。','file/1773228913601.jpg','<ol><li style=\"text-align: left;\"><strong>影像预处理</strong><span style=\"color: var(--color-fg-default);\">：对无人机SAR影像和卫星光学影像分别进行去噪、辐射校正等处理。</span></li><li style=\"text-align: left;\"><strong>特征匹配与配准</strong><span style=\"color: var(--color-fg-default);\">：提取两类影像中的同名控制点，建立精确的几何变换模型。</span></li><li style=\"text-align: left;\"><strong>几何校正与验证</strong><span style=\"color: var(--color-fg-default);\">：利用该模型对SAR影像进行重采样校正，并通过野外实测点验证其最终精度。</span></li></ol>'),(1773228943760,'2026-03-11 11:35:43','基于卫星光学影像配准的无人机SAR影像几何精校正方法','针对无人机合成孔径雷达（SAR）影像几何畸变严重的问题，提出一种利用高精度卫星光学影像作为基准进行配准的精校正方法，以提升无人机SAR影像的定位精度和应用价值。','file/1773228913601.jpg','<ol><li style=\"text-align: left;\"><strong>影像预处理</strong><span style=\"color: var(--color-fg-default);\">：对无人机SAR影像和卫星光学影像分别进行去噪、辐射校正等处理。</span></li><li style=\"text-align: left;\"><strong>特征匹配与配准</strong><span style=\"color: var(--color-fg-default);\">：提取两类影像中的同名控制点，建立精确的几何变换模型。</span></li><li style=\"text-align: left;\"><strong>几何校正与验证</strong><span style=\"color: var(--color-fg-default);\">：利用该模型对SAR影像进行重采样校正，并通过野外实测点验证其最终精度。</span></li></ol>');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pingfenshenhe`
--

DROP TABLE IF EXISTS `pingfenshenhe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pingfenshenhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `lunwenjianjie` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '论文简介',
  `shenhejieguo` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '审核结果',
  `pingfen` int DEFAULT NULL COMMENT '评分',
  `pingjianeirong` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '评价内容',
  `pingfenshijian` datetime DEFAULT NULL COMMENT '评分时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773320184212 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='评分审核';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pingfenshenhe`
--

LOCK TABLES `pingfenshenhe` WRITE;
/*!40000 ALTER TABLE `pingfenshenhe` DISABLE KEYS */;
INSERT INTO `pingfenshenhe` VALUES (131,'2024-02-26 20:32:50','题目编号1','课题名称1','题目类型1','专业1','课题性质1','教师工号1','教师姓名1','学号1','学生姓名1','论文简介1','通过',1,'评价内容1','2024-02-27 12:32:50'),(132,'2024-02-26 20:32:50','题目编号2','课题名称2','题目类型2','专业2','课题性质2','教师工号2','教师姓名2','学号2','学生姓名2','论文简介2','通过',2,'评价内容2','2024-02-27 12:32:50'),(133,'2024-02-26 20:32:50','题目编号3','课题名称3','题目类型3','专业3','课题性质3','教师工号3','教师姓名3','学号3','学生姓名3','论文简介3','通过',3,'评价内容3','2024-02-27 12:32:50'),(134,'2024-02-26 20:32:50','题目编号4','课题名称4','题目类型4','专业4','课题性质4','教师工号4','教师姓名4','学号4','学生姓名4','论文简介4','通过',4,'评价内容4','2024-02-27 12:32:50'),(135,'2024-02-26 20:32:50','题目编号5','课题名称5','题目类型5','专业5','课题性质5','教师工号5','教师姓名5','学号5','学生姓名5','论文简介5','通过',5,'评价内容5','2024-02-27 12:32:50'),(136,'2024-02-26 20:32:50','题目编号6','课题名称6','题目类型6','专业6','课题性质6','教师工号6','教师姓名6','学号6','学生姓名6','论文简介6','通过',6,'评价内容6','2024-02-27 12:32:50'),(1773320184211,'2026-03-12 12:56:23','1','2012-2024年鄱阳湖叶绿素a时空特征分析','GIS空间分析与建模类','地理信息','','admin','','110114','赵六','最终初稿','',95,'很优秀','2026-03-12 20:56:11');
/*!40000 ALTER TABLE `pingfenshenhe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shenhejianyi`
--

DROP TABLE IF EXISTS `shenhejianyi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shenhejianyi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `shenpijieguo` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '审批结果',
  `jianyineirong` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '建议内容',
  `zhidaoyijian` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '指导意见',
  `shenheshijian` datetime DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773645332999 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='审核建议';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shenhejianyi`
--

LOCK TABLES `shenhejianyi` WRITE;
/*!40000 ALTER TABLE `shenhejianyi` DISABLE KEYS */;
INSERT INTO `shenhejianyi` VALUES (1768311826158,'2026-01-13 13:43:45','1768310556714','微信小程序 + 高德地图 API 的校园导览系统','WebGIS / 移动GIS 开发类','地理信息科学','本课题属于地理信息科学专业中的“移动GIS应用开发类”实践型毕业设计，具有鲜明的技术集成性、应用导向性和空间服务特征，符合地信专业培养“地理+信息技术”复合能力的目标。','110111','hadluo老师','110110','hadluo','通过','','','2026-01-13 21:43:41');
/*!40000 ALTER TABLE `shenhejianyi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storeup`
--

DROP TABLE IF EXISTS `storeup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storeup` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `refid` bigint DEFAULT NULL COMMENT 'refid',
  `tablename` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '表名',
  `name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '名称',
  `picture` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图片',
  `type` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '类型(1:收藏,21:赞,22:踩,31:竞拍参与,41:关注)',
  `inteltype` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '推荐类型',
  `remark` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '备注',
  `userid` bigint NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='我的收藏';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storeup`
--

LOCK TABLES `storeup` WRITE;
/*!40000 ALTER TABLE `storeup` DISABLE KEYS */;
/*!40000 ALTER TABLE `storeup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timuleixing`
--

DROP TABLE IF EXISTS `timuleixing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timuleixing` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '题目类型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `timuleixing` (`timuleixing`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1768308431935 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='题目类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timuleixing`
--

LOCK TABLES `timuleixing` WRITE;
/*!40000 ALTER TABLE `timuleixing` DISABLE KEYS */;
INSERT INTO `timuleixing` VALUES (1768308294466,'2026-01-13 12:44:54','测绘与空间数据采集类'),(1768308322876,'2026-01-13 12:45:22','遥感图像处理与解译类'),(1768308336035,'2026-01-13 12:45:35','GIS空间分析与建模类'),(1768308348135,'2026-01-13 12:45:47','WebGIS / 移动GIS 开发类'),(1768308362078,'2026-01-13 12:46:01','时空大数据与城市计算类'),(1768308381150,'2026-01-13 12:46:20','自然资源与生态环境应用类'),(1768308391910,'2026-01-13 12:46:31','三维GIS 与 数字孪生类'),(1768308415814,'2026-01-13 12:46:55','行业应用专题类'),(1768308431934,'2026-01-13 12:47:11','方法创新与交叉融合类');
/*!40000 ALTER TABLE `timuleixing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timuxinxi`
--

DROP TABLE IF EXISTS `timuxinxi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timuxinxi` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `timufengmian` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '题目封面',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `timufanwei` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '题目范围',
  `xuantishijian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '选题时间',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `fabushijian` datetime DEFAULT NULL COMMENT '发布时间',
  `storeupnum` int DEFAULT NULL COMMENT '收藏数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `timubianhao` (`timubianhao`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1773587213698 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='题目信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timuxinxi`
--

LOCK TABLES `timuxinxi` WRITE;
/*!40000 ALTER TABLE `timuxinxi` DISABLE KEYS */;
INSERT INTO `timuxinxi` VALUES (1773235008642,'2026-03-11 13:16:48','1773234910969','禹州市生态功能区划研究','自然资源与生态环境应用类','地理信息科学','file/1773234915354.jpg','应用基础研究','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">以禹州市为研究区，基于自然本底（地形、水文、植被等）和社会经济数据，划分生态保护、农业生产、城镇发展等功能区，并提出管控策略。</span></p>','','admin','教师2','2026-03-11 21:15:10',0),(1773235098034,'2026-03-11 13:18:17','1773235018092','2012-2024年鄱阳湖叶绿素a时空特征分析','GIS空间分析与建模类','地理信息科学','file/1773235071741.jpg','应用基础研究（宏观地理）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">选取中国十大主要湖泊，利用长时序遥感影像，量化其面积、水位等变化，并结合气候与人类活动数据，诊断变化驱动机制。</span></p>','','admin','教师2','2026-03-11 21:16:58',0),(1773235483100,'2026-03-11 13:24:42','1773235419536','多源时空数据融合下的郑州市城市空间结构动态演化与机制分析','GIS空间分析与建模类','地理信息科学','file/1773235461580.jpg','应用研究（公共健康地理）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">以郑州市为案例，基于路网和急救站点数据，运用GIS网络分析法，评估不同区域居民在黄金救援时间内获得急救服务的可达性水平</span></p>','','admin','教师2','2026-03-11 21:23:39',0),(1773235789666,'2026-03-11 13:29:49','1773235708607',' 面向景区打卡点的无人机航拍任务规划方法','测绘与空间数据采集类','测绘工程','file/1773235764342.jpg','技术开发研究（智能摄影）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">研究一种自动化方法，根据景区热门打卡点的位置和美学要求，为无人机规划最优的飞行路径与拍摄参数，以生成高质量航拍素材。</span></p>','','admin2','教师1','2026-03-11 21:28:28',0),(1773235878452,'2026-03-11 13:31:17','1773235805248','基于GRACE卫星的西北干旱区近20年水量蓄量变化分析','方法创新与交叉融合类','地理信息科学','file/1773235846685.jpg','应用基础研究（水文遥感','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">利用GRACE重力卫星数据，反演中国西北干旱区近二十年陆地水储量的时空变化趋势，并分析其对气候变化的响应。</span></p>','','admin2','教师1','2026-03-11 21:30:05',0),(1773236166224,'2026-03-11 13:36:05','1773236105650','基于夜光遥感数据的中国东北三省城镇化时空变化分析','时空大数据与城市计算类','地理i信息科学','file/1773236145522.jpg','应用基础研究（宏观地理/城市遥感）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">利用长时序夜光遥感数据，量化2000年以来辽宁、吉林、黑龙江三省的城镇化水平、空间格局演变及区域差异。</span></p>','','admin2','教师1','2026-03-11 21:35:05',0),(1773236261402,'2026-03-11 13:37:40','1773236186108','郑州市生态系统网络构建及韧性评估','行业应用专题类','地理信息科学','file/1773236229522.jpg','应用基础研究（景观生态学/城市规划）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">识别郑州市核心生态源地，构建由廊道连接的生态网络，并评估其抵抗干扰和恢复的能力（韧性）。</span></p>','','admin2','教师1','2026-03-11 21:36:26',0),(1773236750188,'2026-03-11 13:45:49','1773236683774','郑州市养老设施空间公平性评价与布局优化研究','三维GIS 与 数字孪生类','地理信息科学','file/1773236724600.jpg','应用研究（社会地理/公共政策）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">分析郑州市养老设施的空间分布与老年人口需求的匹配度，识别服务盲区，并提出优化布局方案。</span></p>','','admin3','teacher4','2026-03-11 21:44:43',0),(1773236830242,'2026-03-11 13:47:09','1773236754452','人地互动视角下的城市活力时空模式与影响因素分析','行业应用专题类','地理信息科学','file/1773236795935.jpg','应用基础研究（城市地理/大数据分析）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">融合手机信令、POI等多源数据，刻画城市活力的时空分异特征，并从“人”（活动）与“地”（空间）互动角度解析其成因。</span></p>','','admin3','teacher4','2026-03-11 21:45:54',0),(1773236961690,'2026-03-11 13:49:20','1773236925469','顾及人类活动强度的城市功能区识别与分析——以郑州市核心区为例','行业应用专题类','地理科学','file/1773236935846.jpg','技术应用研究（城市计算）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">利用手机信令、社交媒体等数据表征人类活动强度，以此为核心指标自动识别并分析郑州市中心区的功能分区</span></p>','','admin3','teacher4','2026-03-11 21:48:45',0),(1773237051819,'2026-03-11 13:50:51','1773236984383','城市公园绿地使用现状及影响因素分析——基于许昌市中心城区调查数据','时空大数据与城市计算类','地理信息科学','file/1773237035913.jpg','应用研究（城市社会学/景观规划）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">通过实地问卷调查，分析许昌市民使用公园绿地的行为特征，并探究个人属性、公园特征及周边环境等因素的影响</span></p>','','admin3','teacher4','2026-03-11 21:49:44',0),(1773307872272,'2026-03-12 09:31:11','1773307783776','集成XGBoost-SHAP模型的祁连山生态环境质量评估及驱动因素分析','三维GIS 与 数字孪生类','地理信息科学','file/1773307854873.jpg','应用基础研究（机器学习/山地生态','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">构建XGBoost机器学习模型评估祁连山生态质量，并利用SHAP方法定量解释气候、地形、人类活动等因子对生态质量变化的贡献度。</span></p>','','admin4','教师6','2026-03-12 17:29:43',0),(1773307940440,'2026-03-12 09:32:20','1773307881352','郑州市中心城区公共充电站服务覆盖评估与布局优化研究','方法创新与交叉融合类','地理信息科学','file/1773307924494.jpg','应用研究（城市交通/能源规划）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">评估现有公共充电站对电动汽车用户的服务覆盖能力，识别覆盖盲区，并结合未来需求预测提出科学的布局优化策略。</span></p>','','admin4','教师6','2026-03-12 17:31:21',0),(1773308012767,'2026-03-12 09:33:32','1773307957602','基于VUE的地理专业毕业论文选题系统','WebGIS / 移动GIS 开发类','地理信息科学','file/1773308054361.jpg','技术开发研究（教育信息化/GIS应用）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">设计并开发一个基于VUE框架的Web系统，实现地理专业毕业论文题目的在线发布、检索、智能推荐与管理。</span></p>','','admin4','教师6','2026-03-12 17:32:37',0),(1773308261677,'2026-03-12 09:37:40','1773308162456','综合视角下城市公园绿地使用时空影响因素探析','时空大数据与城市计算类','地理信息科学','file/1773308232575.jpg','应用基础研究（行为地理/城市规划）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">综合考虑自然（天气、季节）、社会（节假日）、个体（年龄、职业）及设施（类型、品质）等多维度因素，系统性探究其对公园使用时空模式的交互影响</span></p>','','admin4','教师6','2026-03-12 17:36:02',0),(1773308526420,'2026-03-12 09:42:06','1773308303008','基于RSEI的长江流域生态质量评价与分析','遥感图像处理与解译类','地理科学','file/1773308507778.jpg','应用基础研究（宏观生态/国家战略）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">在长江大保护背景下，利用RSEI对整个长江流域的生态质量进行系统性、大尺度的评估，识别生态问题突出区域。</span></p>','','admin5','教师7','2026-03-12 17:38:23',0),(1773308662391,'2026-03-12 09:44:22','1773308586017','基于遥感生态指数的颍河流域生态环境质量变化','遥感图像处理与解译类','地理科学','file/1773308641303.jpg','应用基础研究（环境遥感/流域管理）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">运用RSEI模型，基于遥感数据对颍河流域整体及各子区域的生态环境质量进行长时间序列的动态监测与评价</span></p>','','admin5','教师7','2026-03-12 17:43:06',0),(1773308777612,'2026-03-12 09:46:16','1773308669382','社会公平视角下郑州市零售药店的空间分布现状与可达性分析','GIS空间分析与建模类','地理信息科学','file/1773308756278.jpg','应用研究（健康地理/社会公平）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">从社会公平角度，评估郑州市不同社区（尤其弱势群体聚居区）居民获取零售药品服务的便利程度和空间障碍。</span></p>','','admin5','教师7','2026-03-12 17:44:29',0),(1773308889487,'2026-03-12 09:48:09','1773308799935','颍河流域土地利用/土地覆被时空演变过程及其生态效应分析','时空大数据与城市计算类','地理信息科学','file/1773308870768.jpg','应用基础研究（流域地理/生态评估）','<p><span style=\"color: rgb(6, 10, 38); background-color: rgb(255, 255, 255); font-size: 16px;\">分析颍河流域近几十年土地利用变化过程，并定量评估其对生态系统服务（如水源涵养、生物多样性）的正面或负面影响。</span></p>','','admin5','教师7','2026-03-12 17:46:39',0),(1773310234802,'2026-03-12 10:10:33','1773309632551','基于PSR模型的松嫩平原黑土地恢复力评价分析','GIS空间分析与建模类','地理信息科学','file/1773310273312.jpg','应用基础研究（生态安全/土地科学）','<ul><li style=\"text-align: left;\"><strong>研究区</strong><span style=\"color: var(--color-fg-default);\">：黑龙江、吉林、内蒙古交界的松嫩平原核心区（约20万km²）。</span></li><li style=\"text-align: left;\"><strong>指标体系</strong><span style=\"color: var(--color-fg-default);\">：构建“压力(P)-状态(S)-响应(R)”三级指标（如化肥施用量、土壤有机质含量、保护性耕作面积）。</span></li><li style=\"text-align: left;\"><strong>评估目标</strong><span style=\"color: var(--color-fg-default);\">：量化黑土地生态系统在退化干扰下的自我修复能力与时空演变趋势。</span></li><li style=\"text-align: left;\"><strong>输出成果</strong><span style=\"color: var(--color-fg-default);\">：恢复力等级分区图、关键限制因子识别、差异化治理建议。</span></li></ul>','','admin8','教师8','2026-03-12 18:00:32',0),(1773310415522,'2026-03-12 10:13:35','1773310330418',' 胖东来客源地时空分布特征及影响因素分析','GIS空间分析与建模类','地理信息科学','file/1773310395876.jpg','应用研究（商业地理/消费行为）','<ul><li style=\"text-align: left;\"><strong>数据来源</strong><span style=\"color: var(--color-fg-default);\">：手机信令、百度迁徙、大众点评评论地理位置、问卷调查。</span></li><li style=\"text-align: left;\"><strong>分析维度</strong><span style=\"color: var(--color-fg-default);\">：客源地辐射半径、节假日vs平日客流波动、不同年龄段/收入群体偏好。</span></li><li style=\"text-align: left;\"><strong>影响机制</strong><span style=\"color: var(--color-fg-default);\">：交通可达性（高铁/高速）、品牌知名度、周边竞争商圈、社交媒体热度。</span></li><li style=\"text-align: left;\"><strong>应用场景</strong><span style=\"color: var(--color-fg-default);\">：为许昌市商业规划、胖东来门店扩张提供决策支持。</span></li></ul>','','admin8','教师8','2026-03-12 18:12:10',0),(1773310654148,'2026-03-12 10:17:33','1773310423266','许昌市土地利用及其生态系统服务价值时空变化分析','GIS空间分析与建模类','地理信息科学','file/1773310565493.jpg','应用基础研究（景观生态学/国土空间规划）','<ul><li style=\"text-align: left;\"><strong>时间序列</strong><span style=\"color: var(--color-fg-default);\">：1990、2000、2010、2020、2024年多期 Landsat/Sentinel 影像解译。</span></li><li style=\"text-align: left;\"><strong>服务类型</strong><span style=\"color: var(--color-fg-default);\">：供给（粮食生产）、调节（碳汇、水源涵养）、文化（休闲旅游）、支持（生物多样性）。</span></li><li style=\"text-align: left;\"><strong>驱动分析</strong><span style=\"color: var(--color-fg-default);\">：城镇化进程、农业结构调整、生态保护政策（如退耕还林）的影响权重。</span></li><li style=\"text-align: left;\"><strong>可视化表达</strong><span style=\"color: var(--color-fg-default);\">：ESV热力图、转移矩阵桑基图、敏感性分析雷达图。</span></li></ul>','','admin8','教师8','2026-03-12 18:13:43',0),(1773311043407,'2026-03-12 10:24:02','1773310704609','多遥感指数的中亚地区植被状况评价研究','遥感图像处理与解译类','地理科学','file/1773311017734.jpg','应用基础研究（干旱区生态/遥感监测）','<ul><li style=\"text-align: left;\"><strong>研究区</strong><span style=\"color: var(--color-fg-default);\">：哈萨克斯坦、乌兹别克斯坦等五国草原与荒漠过渡带。</span></li><li style=\"text-align: left;\"><strong>指数组合</strong><span style=\"color: var(--color-fg-default);\">：NDVI（绿度）、EVI（增强植被）、LAI（叶面积指数）、VCI（植被健康指数）融合分析。</span></li><li style=\"text-align: left;\"><strong>时空分辨率</strong><span style=\"color: var(--color-fg-default);\">：MODIS 16天合成产品（250m），覆盖2000–2024年。</span></li><li style=\"text-align: left;\"><strong>关联因子</strong><span style=\"color: var(--color-fg-default);\">：降水、温度、放牧强度、灌溉工程分布。</span></li></ul>','','admin8','教师8','2026-03-12 18:18:24',0),(1773311297631,'2026-03-12 10:28:17','1773311146779','中亚地区NPP时空演变及原因分析','方法创新与交叉融合类','地理信息科学','file/1773311279754.jpg','应用基础研究（全球变化/碳循环）','<ul><li style=\"text-align: left;\"><strong>数据源</strong><span style=\"color: var(--color-fg-default);\">：MOD17A3H NPP产品（500m，2000–2024）。</span></li><li style=\"text-align: left;\"><strong>分析方法</strong><span style=\"color: var(--color-fg-default);\">：Theil-Sen斜率估计+Mann-Kendall检验趋势显著性；地理探测器识别主导因子（气候vs人类活动）。</span></li><li style=\"text-align: left;\"><strong>重点区域</strong><span style=\"color: var(--color-fg-default);\">：咸海流域、天山北坡绿洲、图兰平原农田区。</span></li><li style=\"text-align: left;\"><strong>延伸方向</strong><span style=\"color: var(--color-fg-default);\">：NPP与粮食产量、碳排放的耦合关系。</span></li></ul>','','admin9','教师9','2026-03-12 18:25:46',0),(1773311411333,'2026-03-12 10:30:10','1773311308239','许昌市公共充电桩的空间分布格局与可达性研究','GIS空间分析与建模类','地理信息科学','file/1773311371421.jpg','应用研究（城市交通/新能源设施规划）','<ul><li style=\"text-align: left;\"><strong>数据采集</strong><span style=\"color: var(--color-fg-default);\">：高德地图API抓取全市充电站POI（含快充/慢充、运营商、电价）。</span></li><li style=\"text-align: left;\"><strong>评价指标</strong><span style=\"color: var(--color-fg-default);\">：核密度分析热点区、两步移动搜索法（2SFCA）计算居民区15分钟覆盖率。</span></li><li style=\"text-align: left;\"><strong>公平性视角</strong><span style=\"color: var(--color-fg-default);\">：对比老城区vs新区、高档小区vs保障房社区的服务均等化水平。</span></li><li style=\"text-align: left;\"><strong>优化方案</strong><span style=\"color: var(--color-fg-default);\">：基于需求预测（电动汽车保有量增长）提出新增站点选址建议。</span></li></ul>','','admin9','教师9','2026-03-12 18:28:28',0),(1773311547320,'2026-03-12 10:32:26','1773311434763','开封市多级医疗服务设施的多模式交通可达性研究','GIS空间分析与建模类','地理信息科学','file/1773311530718.jpg','应用研究（公共卫生/交通地理）','<ul><li style=\"text-align: left;\"><strong>设施层级</strong><span style=\"color: var(--color-fg-default);\">：三甲医院→社区卫生服务中心→村卫生室三级网络。</span></li><li style=\"text-align: left;\"><strong>交通方式</strong><span style=\"color: var(--color-fg-default);\">：步行（15min生活圈）、公交（等车+乘车时间）、私家车（高峰/平峰路况）。</span></li><li style=\"text-align: left;\"><strong>人群细分</strong><span style=\"color: var(--color-fg-default);\">：老年人、儿童、残障人士等特殊群体的无障碍路径模拟。</span></li><li style=\"text-align: left;\"><strong>政策启示</strong><span style=\"color: var(--color-fg-default);\">：识别“医疗荒漠”区域，提出公交线路优化或移动诊所布点策略。</span></li></ul>','','admin9','教师9','2026-03-12 18:30:34',0),(1773311620463,'2026-03-12 10:33:39','1773311551418','颍河流域植被净初级生产力（NPP）时空格局演变及其驱动因素研究','时空大数据与城市计算类','地理信息科学','file/1773311600977.jpg','应用基础研究（流域生态/气候变化响应）','<ul><li style=\"text-align: left;\"><strong>流域界定</strong><span style=\"color: var(--color-fg-default);\">：河南段为主，涵盖沙河、贾鲁河等支流，总面积约3.5万km²。</span></li><li style=\"text-align: left;\"><strong>NPP估算</strong><span style=\"color: var(--color-fg-default);\">：CASA模型结合气象站数据与遥感植被参数。</span></li><li style=\"text-align: left;\"><strong>驱动分解</strong><span style=\"color: var(--color-fg-default);\">：气候因子（降水、辐射）贡献率 vs 人类活动（耕地扩张、造林工程）贡献率。</span></li><li style=\"text-align: left;\"><strong>情景模拟</strong><span style=\"color: var(--color-fg-default);\">：未来RCP4.5/RCP8.5情景下NPP变化预测。</span></li></ul>','','admin9','教师9','2026-03-12 18:32:31',0),(1773311753786,'2026-03-12 10:35:53','1773311643604','基于服务空间可达性的郑州市急救医疗资源配置现状分析','GIS空间分析与建模类','地理信息科学','file/1773311738661.jpg','应用研究（应急管理和公共健康）','<ul><li style=\"text-align: left;\"><strong>核心指标</strong><span style=\"color: var(--color-fg-default);\">：“黄金4分钟”救援圈内人口覆盖比例、救护车平均响应时间。</span></li><li style=\"text-align: left;\"><strong>数据整合</strong><span style=\"color: var(--color-fg-default);\">：120调度记录、路网拓扑、实时拥堵指数、医院床位容量。</span></li><li style=\"text-align: left;\"><strong>脆弱性评估</strong><span style=\"color: var(--color-fg-default);\">：识别高风险区域（如老旧小区、城乡结合部）的资源配置缺口。</span></li><li style=\"text-align: left;\"><strong>动态优化</strong><span style=\"color: var(--color-fg-default);\">：基于机器学习预测突发事件热点，动态调整救护车站位置。</span></li></ul>','','admin11','教师11','2026-03-12 18:34:03',0),(1773311814062,'2026-03-12 10:36:53','1773311778264','沙颍河流域玉米物候时空演变及影响因素分析','时空大数据与城市计算类','地理信息科学','','应用基础研究（农业遥感/作物生理）','<ul><li style=\"text-align: left;\"><strong>物候阶段</strong><span style=\"color: var(--color-fg-default);\">：播种期、抽雄期、成熟期的提取（基于MODIS EVI曲线拟合）。</span></li><li style=\"text-align: left;\"><strong>时空精度</strong><span style=\"color: var(--color-fg-default);\">：县级单元，2000–2024年逐年分析。</span></li><li style=\"text-align: left;\"><strong>影响因子</strong><span style=\"color: var(--color-fg-default);\">：积温、降水、品种改良、种植结构调整（如“粮改饲”政策）。</span></li><li style=\"text-align: left;\"><strong>灾害关联</strong><span style=\"color: var(--color-fg-default);\">：物候异常与干旱、洪涝、病虫害发生的相关性分析。</span></li></ul>','','admin11','教师11','2026-03-12 18:36:18',0),(1773311902860,'2026-03-12 10:38:22','1773311832231','许昌市不透水面提取及时空演变特征分析','时空大数据与城市计算类','地理信息科学','file/1773311884993.jpg','技术应用研究（城市遥感/扩张监测）','<ul><li style=\"text-align: left;\"><strong>技术路线</strong><span style=\"color: var(--color-fg-default);\">：随机森林分类器融合Sentinel-2多光谱+NDSI（归一化差值不透水面指数）。</span></li><li style=\"text-align: left;\"><strong>验证方法</strong><span style=\"color: var(--color-fg-default);\">：Google Earth高清影像目视解译抽样验证（Kappa系数&gt;0.85）。</span></li><li style=\"text-align: left;\"><strong>演变规律</strong><span style=\"color: var(--color-fg-default);\">：扩张速度、方向（轴向蔓延vs填充式发展）、形态紧凑度指数。</span></li><li style=\"text-align: left;\"><strong>生态效应</strong><span style=\"color: var(--color-fg-default);\">：不透水面增加对地表温度（UHI）、径流系数的影响量化。</span></li></ul>','','admin11','教师11','2026-03-12 18:37:12',0),(1773312017832,'2026-03-12 10:40:17','1773311907037','基于“细胞”异质假说的土地利用约束下精细时空尺度人口空间化方法','GIS空间分析与建模类','地理信息科学','file/1773311972649.jpg','技术开发研究（空间统计/人口制图）','<ul><li style=\"text-align: left;\"><strong>理论创新</strong><span style=\"color: var(--color-fg-default);\">：将地块视为“细胞”，考虑其内部功能混合度（居住/商业/工业比例）对人口密度的非线性影响。</span></li><li style=\"text-align: left;\"><strong>数据融合</strong><span style=\"color: var(--color-fg-default);\">：WorldPop网格数据 + 房屋轮廓矢量 + POI兴趣点 + 手机信令校准。</span></li><li style=\"text-align: left;\"><strong>输出精度</strong><span style=\"color: var(--color-fg-default);\">：100m×100m格网，误差控制在±15%以内。</span></li><li style=\"text-align: left;\"><strong>应用场景</strong><span style=\"color: var(--color-fg-default);\">：疫情管控、应急疏散、公共服务精准投放。</span></li></ul>','','admin11','教师11','2026-03-12 18:38:27',0),(1773312256402,'2026-03-12 10:44:16','1773312087309','基于多源遥感数据融合的开封市耕地生质量时空演变及驱动因素分析','方法创新与交叉融合类','地理信息科学','file/1773312237804.jpg','应用基础研究（耕地保护/智慧农业）','<ul><li style=\"text-align: left;\"><strong>质量维度</strong><span style=\"color: var(--color-fg-default);\">：土壤肥力（反演有机质）、灌溉保证率（水体提取）、连片度（景观指数）、污染风险（重金属光谱特征）。</span></li><li style=\"text-align: left;\"><strong>数据源</strong><span style=\"color: var(--color-fg-default);\">：Sentinel-2（10m）、GF-6（2m）、无人机高光谱（试验田验证）。</span></li><li style=\"text-align: left;\"><strong>驱动解析</strong><span style=\"color: var(--color-fg-default);\">：高标准农田建设、轮作休耕、面源污染治理政策的实施效果评估。</span></li></ul>','','teacher','教师1','2026-03-12 18:41:27',0),(1773312369637,'2026-03-12 10:46:09','1773312261680','许昌市生态网络构建与评价研究','GIS空间分析与建模类','地理信息科学','file/1773312353629.jpg','应用基础研究（景观连通性/生态修复）','<ul><li style=\"text-align: left;\"><strong>源地识别</strong><span style=\"color: var(--color-fg-default);\">：基于MCR模型选取大型公园、湿地、林地作为生态源。</span></li><li style=\"text-align: left;\"><strong>廊道提取</strong><span style=\"color: var(--color-fg-default);\">：最小累积阻力路径（Least Cost Path）连接源地，形成“蓝绿骨架”。</span></li><li style=\"text-align: left;\"><strong>韧性评估</strong><span style=\"color: var(--color-fg-default);\">：模拟道路扩建、房地产开发等情景下网络断裂风险。</span></li><li style=\"text-align: left;\"><strong>优化策略</strong><span style=\"color: var(--color-fg-default);\">：提出生态踏脚石（Stepping Stone）布设方案提升连通性。</span></li></ul>','','teacher','教师1','2026-03-12 18:44:21',0),(1773312472750,'2026-03-12 10:47:52','1773312379429','融合多源数据的土地利用约束下精细时空尺度GDP空间化方法','自然资源与生态环境应用类','地理信息科学','file/1773312447392.jpg','技术开发研究（经济地理/大数据建模）','<ul><li style=\"text-align: left;\"><strong>输入数据</strong><span style=\"color: var(--color-fg-default);\">：夜间灯光（VIIRS）、POI密度、建筑体积（LiDAR）、企业注册信息。</span></li><li style=\"text-align: left;\"><strong>模型选择</strong><span style=\"color: var(--color-fg-default);\">：XGBoost回归 + 地理加权回归（GWR）处理空间非平稳性。</span></li><li style=\"text-align: left;\"><strong>输出尺度</strong><span style=\"color: var(--color-fg-default);\">：街道办级别（1km²），年度更新。</span></li><li style=\"text-align: left;\"><strong>验证方式</strong><span style=\"color: var(--color-fg-default);\">：与统计局分县GDP数据交叉验证，相对误差&lt;10%。</span></li></ul>','','teacher','教师1','2026-03-12 18:46:19',0),(1773312566811,'2026-03-12 10:49:26','1773312504878','基于2D视角的城市绿色空间公平性研究——以许昌市为例','行业应用专题类','地理信息科学','file/1773312550925.jpg','应用研究（社会公平/城市规划）','<ul><li style=\"text-align: left;\"><strong>公平性维度</strong><span style=\"color: var(--color-fg-default);\">：数量公平（人均绿地面积）、质量公平（植被覆盖度、设施完善度）、可达性公平（步行10分钟覆盖率）。</span></li><li style=\"text-align: left;\"><strong>人群分层</strong><span style=\"color: var(--color-fg-default);\">：按收入、年龄、户籍（本地/外来务工）分组比较。</span></li><li style=\"text-align: left;\"><strong>空间正义</strong><span style=\"color: var(--color-fg-default);\">：识别“绿色贫困”社区（低收入+低绿地+高污染叠加区）。</span></li><li style=\"text-align: left;\"><strong>政策建议</strong><span style=\"color: var(--color-fg-default);\">：制定差异化绿地配建标准，优先向弱势群体倾斜。</span></li></ul>','','teacher','教师1','2026-03-12 18:48:24',0),(1773414447338,'2026-03-13 15:07:27','T1773414447338','城市规划与社会经济方向（适合擅长空间分析与可视化的同学）',NULL,'风景园林',NULL,NULL,NULL,NULL,'admin',NULL,'2026-03-13 23:07:27',NULL);
/*!40000 ALTER TABLE `timuxinxi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint NOT NULL COMMENT '用户id',
  `username` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `tablename` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '表名',
  `role` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '角色',
  `token` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,41,'学号1','xuesheng','学生','oqfkv3e9bnzkg74p8sqgccs911ddrww7','2024-02-26 20:42:14','2024-02-26 21:42:15'),(2,1,'admin','users','管理员','erfj85c3730jwfl8iayx9kvn8sfakuy0','2024-02-26 20:42:59','2026-05-22 13:58:54'),(3,1751017240379,'110110','xuesheng','学生','cyoq7doelhvexsqoh2p06jen7xka4fdp','2025-06-27 09:40:47','2026-03-12 11:49:35'),(4,1751077737664,'110111','jiaoshi','管理员','9xevfgd8juneuxvpanjyvapuzros839w','2025-06-28 02:31:35','2026-03-06 06:18:39'),(5,1772774022771,'admin','jiaoshi','管理员','3zrqo9vm79wgq10aq6rc3b3wgg3krkax','2026-03-06 05:13:48','2026-05-22 13:39:00'),(6,1772774138454,'teacher','jiaoshi','管理员','28dx063nbfj6qyvt3q41gfgr93aoeyix','2026-03-06 05:15:52','2026-03-12 11:41:25'),(7,1773229922193,'admin2','jiaoshi','管理员','31qbgexd49d7k9skrpg4si5p848vhz3y','2026-03-11 13:28:22','2026-05-22 14:17:11'),(8,1773229806734,'admin3','jiaoshi','管理员','q7493qk3lkvzsxskdza83z5vylgq78kr','2026-03-11 13:44:23','2026-03-22 05:53:48'),(9,1773229979515,'admin4','jiaoshi','管理员','cixnlbaapq5ckto03ouh37hjk7kj2lvg','2026-03-12 09:29:40','2026-05-22 13:59:35'),(10,1773230047379,'admin5','jiaoshi','管理员','igf6e5rdb8q4lo835x9f87r792ktk8lp','2026-03-12 09:38:17','2026-03-12 10:38:17'),(11,1773230063120,'admin8','jiaoshi','管理员','h9dxgn7x0xo2ix4ae7107g2cs3yge59g','2026-03-12 09:59:36','2026-03-12 10:59:36'),(12,1773230079387,'admin9','jiaoshi','管理员','frb436xne7wq6et6491ggblryu2qrdit','2026-03-12 10:25:41','2026-03-16 08:37:52'),(13,1773230111878,'admin11','jiaoshi','管理员','2kxfzb1a95ga4m32abs200poqse2o1ar','2026-03-12 10:34:00','2026-03-12 12:33:28'),(14,1773229185726,'110112','xuesheng','学生','lntpxkiavgdb0tn7ieajtp3drzwc3w8r','2026-03-12 11:34:31','2026-03-20 17:07:35'),(15,1773229294782,'110113','xuesheng','学生','erez0ff9b3xzlv2zonxeqfxlb6pbtdog','2026-03-12 11:35:32','2026-03-20 17:08:11'),(16,1773229351214,'110114','xuesheng','学生','uh2id5y8g0cmp7piu9qhcylr9ia83kr6','2026-03-12 11:40:02','2026-03-13 14:42:02'),(17,1773229446575,'110115','xuesheng','学生','ulifi7pi0r1n8lpr2c6lw0su1v44waeq','2026-03-13 13:48:10','2026-03-22 04:28:43'),(18,1773412142554,'110210','xuesheng','学生','8sxfnpbu6ekus0nsyt8wx0uwecehnwl9','2026-03-13 14:29:13','2026-03-15 15:41:38'),(19,1773229719543,'110121','xuesheng','学生','irhszuobcdxl9wpz31mp8ag6gnwc8s93','2026-03-15 15:19:19','2026-03-16 08:01:06'),(20,1773644779711,'110122','xuesheng','学生','ces2z0fmyq5fzhxa3jtjuo8v7fxqdvv7','2026-03-16 07:06:29','2026-03-22 04:42:01'),(21,1773646263578,'560222','xuesheng','学生','9prtmy0ya9uw17b6nsgorhr6mba3p595','2026-03-16 07:31:54','2026-05-22 14:14:46'),(22,2,'admin_dlgx','users','管理员','qwaki5pef75oaloiv79h6d5dlxr1qu7w','2026-03-17 15:32:44','2026-03-22 05:53:09'),(23,6,'admin_fjyl','users','管理员','6q1qg1nf4nez6bebwqwbcxu04y6j47r4','2026-03-17 15:41:36','2026-05-21 11:18:36'),(24,1774150299621,'490222','xuesheng','学生','ol9pgvuy8jszmdog06i182gge33nxfxy','2026-03-22 03:34:45','2026-03-22 04:34:46'),(25,1773229544772,'110119','xuesheng','学生','ynyqfaibmdy6vk3txp64cllbrms89eh5','2026-03-22 03:42:33','2026-05-22 13:54:46');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `username` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `role` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '角色',
  `zhuanye` varchar(200) DEFAULT NULL COMMENT '专业（为空表示学院级管理员）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='管理员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2024-02-26 20:32:51','admin','$2a$10$1Maq4xp9CGYv6nza4pOxRu9/jH4N7P9pYlqei3MU7dSV79iWmcjKi','管理员',NULL),(2,'2026-03-17 15:24:17','admin_dlxx','$2a$10$.sMAVWslDDYoFSI2LUYxn.fRX2mZ8NJKnkWjJ/pwIeScHdSalftam','管理员','地理信息科学'),(3,'2026-03-17 15:24:17','admin_chgc','123456','管理员','测绘工程'),(4,'2026-03-17 15:24:17','admin_ygkx','123456','管理员','遥感科学与技术'),(5,'2026-03-17 15:24:17','admin_zrdl','123456','管理员','自然地理与资源环境'),(6,'2026-03-17 15:41:08','admin_fjyl','$2a$10$BXMXww10XVd.q2P3o2E1hugHHPOcMN.OvEOB0e0P801sidAbM3d6.','管理员','风景园林');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xuantishenqing`
--

DROP TABLE IF EXISTS `xuantishenqing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xuantishenqing` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '课题性质',
  `xuantishijian` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '选题时间',
  `jiaoshigonghao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '学生姓名',
  `shenqingyuanyin` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '申请原因',
  `shenqingshijian` datetime DEFAULT NULL COMMENT '申请时间',
  `shenhezhuangtai` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '审核状态',
  `shenheshijian` datetime DEFAULT NULL COMMENT '审核时间',
  `shenhejilu` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '审核流程记录JSON',
  `crossuserid` bigint DEFAULT NULL COMMENT '跨表用户id',
  `crossrefid` bigint DEFAULT NULL COMMENT '跨表主键id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1779455288820 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='选题申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xuantishenqing`
--

LOCK TABLES `xuantishenqing` WRITE;
/*!40000 ALTER TABLE `xuantishenqing` DISABLE KEYS */;
INSERT INTO `xuantishenqing` VALUES (1768311348618,'2026-01-13 13:35:47','1768310556714','微信小程序 + 高德地图 API 的校园导览系统','WebGIS / 移动GIS 开发类','地信','本课题属于地理信息科学专业中的“移动GIS应用开发类”实践型毕业设计，具有鲜明的技术集成性、应用导向性和空间服务特征，符合地信专业培养“地理+信息技术”复合能力的目标。','2026.1.5','110111','hadluo老师','110110','hadluo','与我方向一致','2026-01-13 21:35:24','已审核',NULL,1751017240379,1768311201547),(1773315164730,'2026-03-12 11:32:44','1773311832231','许昌市不透水面提取及时空演变特征分析','时空大数据与城市计算类','地理信息','技术应用研究（城市遥感/扩张监测）','','admin11','教师11','110110','张三','喜欢这个方向','2026-03-12 19:32:33','未审核',NULL,1751017240379,1773311902860),(1773315252128,'2026-03-12 11:34:11','1773311907037','基于“细胞”异质假说的土地利用约束下精细时空尺度人口空间化方法','GIS空间分析与建模类','地理信息','技术开发研究（空间统计/人口制图）','','admin11','教师11','110110','张三','','2026-03-12 19:34:10','未审核',NULL,1751017240379,1773312017832),(1773646335096,'2026-03-16 07:32:14','1773307957602','基于VUE的地理专业毕业论文选题系统','WebGIS / 移动GIS 开发类','地理信息','技术开发研究（教育信息化/GIS应用）','','admin4','教师6','560222','张四','\n[驳回原因]: 有同学已经预选了','2026-03-16 15:32:11','否',NULL,1773646263578,1773308012767),(1773646564636,'2026-03-16 07:36:04','1773311308239','许昌市公共充电桩的空间分布格局与可达性研究','GIS空间分析与建模类','地理信息','应用研究（城市交通/新能源设施规划）','','admin9','教师9','560222','张四','','2026-03-16 15:36:03','否',NULL,1773646263578,1773311411333),(1773646714064,'2026-03-16 07:38:33','1773235708607',' 面向景区打卡点的无人机航拍任务规划方法','测绘与空间数据采集类','测绘','技术开发研究（智能摄影）','','admin2','教师1','560222','张四','[驳回原因]: 导师撤销已通过申请','2026-03-16 15:38:32','否',NULL,1773646263578,1773235789666),(1774075736052,'2026-03-21 06:48:55','1773312504878','基于2D视角的城市绿色空间公平性研究——以许昌市为例','行业应用专题类','地理信息','应用研究（社会公平/城市规划）','','teacher','教师1','','','','2026-03-21 14:48:53','未审核',NULL,1,1773312566811),(1774151324006,'2026-03-22 03:48:43','1773236754452','人地互动视角下的城市活力时空模式与影响因素分析','行业应用专题类','地理信息科学','应用基础研究（城市地理/大数据分析）','','admin3','teacher4','110119','陈五','','2026-03-22 11:47:06','否',NULL,1773229544772,1773236830242),(1779455288819,'2026-05-22 13:08:08','1773307881352','郑州市中心城区公共充电站服务覆盖评估与布局优化研究','方法创新与交叉融合类','地理信息科学','应用研究（城市交通/能源规划）',NULL,'admin4','教师6','110119','陈五',NULL,'2026-05-22 21:08:09','已审核',NULL,NULL,NULL);
/*!40000 ALTER TABLE `xuantishenqing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xuesheng`
--

DROP TABLE IF EXISTS `xuesheng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xuesheng` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `xuehao` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '学号',
  `mima` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '密码',
  `xueshengxingming` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '学生姓名',
  `touxiang` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '头像',
  `xingbie` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '性别',
  `shoujihaoma` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '手机号码',
  `zhuanye` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '专业',
  `banji` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '班级',
  `xueyuan` varchar(255) DEFAULT NULL COMMENT '学院',
  `biyejie` varchar(20) DEFAULT NULL COMMENT '毕业届别（年份，如2026表示2026届）',
  `nianji` varchar(20) DEFAULT NULL COMMENT '年级（入学年份，如2022表示2022级）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `xuehao` (`xuehao`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1774150299622 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='学生';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xuesheng`
--

LOCK TABLES `xuesheng` WRITE;
/*!40000 ALTER TABLE `xuesheng` DISABLE KEYS */;
INSERT INTO `xuesheng` VALUES (1751017240379,'2025-06-27 09:40:41','110110','123456','张三','','男','13423222222','风景园林','1',NULL),(1773229185726,'2026-03-11 11:39:45','110112','$2a$10$xWW8FngmKpT0meJVuXuAyuCm4G/W0XCzaHqXV6irRZAWxN1/kU77G','李四','','女','17534567890','地理信息科学','1',NULL),(1773229294782,'2026-03-11 11:41:34','110113','$2a$10$o0aw/h3IUFQ6VW.f0SvrSeMmiKUQ00D04un3wrVkRGyUzm4qaThyS','刘能','','男','13954364772','地理信息科学','2',NULL),(1773229351214,'2026-03-11 11:42:31','110114','123456','赵六','','女','17633544562','地理信息科学','2',NULL),(1773229446575,'2026-03-11 11:44:06','110115','$2a$10$HMj5Rn6dDx1B8013CJFbHORg8V1HxqzFuLLU702KRmIewx.RdvGDS','郭三','','男','17689624316','地理信息科学','1',NULL),(1773229544772,'2026-03-11 11:45:44','110119','$2a$10$/KUU4cFjtzoc.HL6ma2xs.vTWRzB6x6oekXpQekW7YUptsUd/3PDG','陈五','','男','17645632457','地理信息科学','1',NULL),(1773229599742,'2026-03-11 11:46:39','110120','123456','王五','','女','17580932145','地理信息科学','2',NULL),(1773229719543,'2026-03-11 11:48:39','110121','$2a$10$J3XMmgDZifl5Mu54xKlJMeiiMhAoHzevHPhdHOeKHCybUSb9N6SaK','李九','','女','15367908231','地理信息科学','2',NULL),(1773412142554,'2026-03-13 14:29:02','110210','$2a$10$bKNtofsPlKaEGi8WaL53S.2R6ACte9dcvnVhzO8RU/JoPX8be6IJq','赵十一','','女','17645780982','地理信息科学','2班',NULL),(1773644779711,'2026-03-16 07:06:19','110122','$2a$10$sH2iqP4LDk6G4ZaLbUfHd.6McBFQdlSnSwV/j.vREXDRIiFqQZfCi','刘四','','女','18754213568','地理信息科学','2班',NULL),(1773646263578,'2026-03-16 07:31:03','560222','$2a$10$1HwRaqob1QvSecFTbB1u..NC3FDB7hH8v3AxvW3/9cGvJc0dS9Jem','张四','','女','17667890765','地理科学','2',NULL),(1774150299621,'2026-03-22 03:31:39','490222','$2a$10$BbctNpXTBZ.CMTF3ooq9deyj1WZ/9GpVI2rHhq9jMuefwvYCj6FEC','朋1',NULL,'男','17614578097','风景园林','2班',NULL);
/*!40000 ALTER TABLE `xuesheng` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-22 21:34:26

--
-- Table structure for table `zhongqijiancha`
--
DROP TABLE IF EXISTS `zhongqijiancha`;
CREATE TABLE `zhongqijiancha` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `timubianhao` varchar(200) DEFAULT NULL COMMENT '题目编号',
  `ketimingcheng` varchar(200) DEFAULT NULL COMMENT '课题名称',
  `timuleixing` varchar(200) DEFAULT NULL COMMENT '题目类型',
  `zhuanye` varchar(200) DEFAULT NULL COMMENT '专业',
  `ketixingzhi` longtext COMMENT '课题性质',
  `jiaoshigonghao` varchar(200) DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) DEFAULT NULL COMMENT '教师姓名',
  `xuehao` varchar(200) DEFAULT NULL COMMENT '学号',
  `xueshengxingming` varchar(200) DEFAULT NULL COMMENT '学生姓名',
  `zhongqijianjie` longtext COMMENT '中期检查简介',
  `zhongqifujian` longtext COMMENT '中期检查附件',
  `zhongqishijian` datetime DEFAULT NULL COMMENT '提交时间',
  `shenhezhuangtai` varchar(200) DEFAULT '未审核' COMMENT '审核状态',
  `shenheyuanyin` varchar(500) DEFAULT NULL COMMENT '审核意见',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='中期检查';
