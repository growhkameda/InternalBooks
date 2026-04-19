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
(1, 10010001, '2026-01-10 10:00:00', '2026-01-17 10:00:00', '2026-01-16 15:00:00', 1, '非常に参考になった'),
(2, 10010001, '2026-01-20 10:00:00', '2026-01-27 10:00:00', '2026-01-27 09:00:00', 2, 'ストリームAPIの箇所を重点的に読んだ'),
(3, 10010001, '2026-02-01 10:00:00', '2026-02-08 10:00:00', '2026-02-08 12:00:00', 1, '写経して理解が深まった'),
(4, 10010001, '2026-02-15 10:00:00', '2026-02-22 10:00:00', '2026-02-21 18:00:00', 2, '再読。ラムダ式の理解に役立つ'),
(5, 10010001, '2026-03-01 10:00:00', '2026-03-08 10:00:00', '2026-03-07 10:00:00', 1, '入門書として最適'),
(6, 10010001, '2026-03-20 17:00:00', '2026-03-27 17:00:00', NULL, 1, NULL),
(7, 10010002, '2026-03-10 10:00:00', '2026-03-17 10:00:00', NULL, 1, NULL),
(8, 10020001, '2026-03-10 10:00:00', '2026-03-17 10:00:00', NULL, 2, NULL),
(9, 10020003, '2026-03-20 10:00:00', '2026-03-27 10:00:00', NULL, 1, NULL);
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
