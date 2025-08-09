-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: internalbooks
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `t_book`
--

DROP TABLE IF EXISTS `t_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_book` (
  `book_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `categories` varchar(255) NOT NULL,
  `borrower_id` int DEFAULT NULL,
  `provider_id` int DEFAULT NULL,
  `provider_comment` varchar(255) DEFAULT NULL,
  `memo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_book`
--

LOCK TABLES `t_book` WRITE;
/*!40000 ALTER TABLE `t_book` DISABLE KEYS */;
INSERT INTO `t_book` VALUES 
(1001,'Javaテスト1','Java,資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(1002,'Javaテスト2','Java',2,3,'基本情報とらせたい！','一度読んでほしい'),
(2002,'プログラミングテスト3','プログラミング',2,3,'基本情報とらせたい！','一度読んでほしい'),
(2002,'プログラミングテスト4','プログラミング',2,3,'基本情報とらせたい！','一度読んでほしい'),
(3001,'基本情報テスト5','基本情報,資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(3002,'基本情報テスト6','基本情報,資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(4001,'資格テスト7','資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(4002,'資格テスト8','資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(5001,'ITパスポートテスト9','ITパスポート,資格',2,3,'基本情報とらせたい！','一度読んでほしい'),
(5002,'ITパスポートテスト10','ITパスポート,資格',2,3,'基本情報とらせたい！','一度読んでほしい'),;
/*!40000 ALTER TABLE `t_book` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-05 21:23:15
