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
-- Table structure for table `t_lendinghistory`
--

DROP TABLE IF EXISTS `t_lendinghistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_lendinghistory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `lending_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `scheduled_return_date` timestamp NULL DEFAULT NULL,
  `return_date` timestamp NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  `review` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_lendinghistory`
--

LOCK TABLES `t_lendinghistory` WRITE;
/*!40000 ALTER TABLE `t_lendinghistory` DISABLE KEYS */;
INSERT INTO `t_lendinghistory` VALUES 
(1,10010001,'2025-05-03 13:21:09','2025-05-17 15:00:00','2025-05-16 15:00:00',1,'面白かったよ'),
(2,10010002,'2025-06-03 13:21:09','2025-06-17 15:00:00','2025-06-16 15:00:00',2,'ちゃんと返せるじゃん'),
(3,10010003,'2025-06-05 13:21:11','2025-06-22 15:00:00',NULL,1,'はよ返せ'),
(4,10020001,'2025-06-03 13:21:09','2025-06-17 15:00:00','2025-06-16 15:00:00',2,'ポインタ難しい…メモリリークやめろ'),
(5,10030001,'2025-06-03 13:21:09','2025-06-17 15:00:00','2025-06-16 15:00:00',2,'中括弧いらないとか逆に難しそう…慣れか？'),
(6,10090001,'2025-06-03 13:21:09','2025-06-17 15:00:00','2025-06-16 15:00:00',2,'ITエンジニアとりあえず勉強しとけ'),
(7,10020002,'2025-06-03 13:21:09','2025-06-17 15:00:00',NULL,2,NULL);
/*!40000 ALTER TABLE `t_lendinghistory` ENABLE KEYS */;
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
