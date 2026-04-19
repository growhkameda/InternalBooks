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
-- Java (1001xxxx)
(10010001, '【限界テスト】1234567890！@＃＄％＾＆＊（）＿＋ー＝［］｛｝；’：”＜＞？，．／。ここから英単語突き抜けチェックを開始します：aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'Java', 1, 2, '【限界テスト】1234567890！@＃＄％＾＆＊（）＿＋ー＝［］｛｝；’：”＜＞？，．／。ここから英単語突き抜けチェックを開始します：aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'test1'),
(10010002, 'JavaTest02', 'Java', 1, 2, '【検証開始：255文字以内】𠮷（つちよし）𩸽（ほっけ）𠀋。😀😁😂絵文字とサロゲートペアが混在するこの文章は、レイアウト崩れやDBの文字数制限をチェックするためのものです。！@＃＄％＾＆＊（）＿＋ー＝［］｛｝；’：”＜＞？，．／。改行も含むさらに禁則事項を無視した非常に長い英単語で、右端の突き抜けを確認します：aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa。', 'test2'),
(10010003, 'JavaTest03', 'Java', NULL, 2, 'test3', 'test3'),
(10010004, 'JavaTest04', 'Java', NULL, 2, 'test4', 'test4'),
(10010005, 'JavaTest05', 'Java', NULL, 2, 'test5', 'test5'),
(10010006, 'JavaTest06', 'Java', NULL, 2, 'test6', 'test6'),
(10010007, 'JavaTest07', 'Java', NULL, 2, 'test7', 'test7'),
(10010008, 'JavaTest08', 'Java', NULL, 2, 'test8', 'test8'),
(10010009, 'JavaTest09', 'Java', NULL, 2, 'test9', 'test9'),
(10010010, 'JavaTest10', 'Java', NULL, 2, 'test10', 'test10'),
(10010011, 'JavaTest11', 'Java', NULL, 2, 'test11', 'test11'),
(10010012, 'JavaTest12', 'Java', NULL, 2, 'test12', 'test12'),
(10010013, 'JavaTest13', 'Java', NULL, 2, 'test13', 'test13'),
-- C言語 (1002xxxx)
(10020001, 'C言語Test01', 'C言語', NULL, 2, 'test1', 'test1'),
(10020002, 'C言語Test02', 'C言語', NULL, 2, 'test2', 'test2'),
(10020003, 'C言語Test03', 'C言語', 1, 2, 'test3', 'test3'),
(10020004, 'C言語Test04', 'C言語', NULL, 2, 'test4', 'test4'),
(10020005, 'C言語Test05', 'C言語', NULL, 2, 'test5', 'test5'),
(10020006, 'C言語Test06', 'C言語', NULL, 2, 'test6', 'test6'),
(10020007, 'C言語Test07', 'C言語', NULL, 2, 'test7', 'test7'),
(10020008, 'C言語Test08', 'C言語', NULL, 2, 'test8', 'test8'),
(10020009, 'C言語Test09', 'C言語', NULL, 2, 'test9', 'test9'),
(10020010, 'C言語Test10', 'C言語', NULL, 2, 'test10', 'test10'),
(10020011, 'C言語Test11', 'C言語', NULL, 2, 'test11', 'test11'),
(10020012, 'C言語Test12', 'C言語', NULL, 2, 'test12', 'test12'),
(10020013, 'C言語Test13', 'C言語', NULL, 2, 'test13', 'test13'),
-- Python (1003xxxx)
(10030001, 'PythonTest01', 'Python', NULL, 2, 'test1', 'test1'),
(10030002, 'PythonTest02', 'Python', NULL, 2, 'test2', 'test2'),
(10030003, 'PythonTest03', 'Python', NULL, 2, 'test3', 'test3'),
(10030004, 'PythonTest04', 'Python', NULL, 2, 'test4', 'test4'),
(10030005, 'PythonTest05', 'Python', NULL, 2, 'test5', 'test5'),
(10030006, 'PythonTest06', 'Python', NULL, 2, 'test6', 'test6'),
(10030007, 'PythonTest07', 'Python', NULL, 2, 'test7', 'test7'),
(10030008, 'PythonTest08', 'Python', NULL, 2, 'test8', 'test8'),
(10030009, 'PythonTest09', 'Python', NULL, 2, 'test9', 'test9'),
-- ITパスポート (1007xxxx)
(10070001, 'ITパスポートTest01', 'ITパスポート', NULL, 2, 'test1', 'test1'),
(10070002, 'ITパスポートTest02', 'ITパスポート', NULL, 2, 'test2', 'test2'),
(10070003, 'ITパスポートTest03', 'ITパスポート', NULL, 2, 'test3', 'test3'),
(10070004, 'ITパスポートTest04', 'ITパスポート', NULL, 2, 'test4', 'test4'),
(10070005, 'ITパスポートTest05', 'ITパスポート', NULL, 2, 'test5', 'test5'),
(10070006, 'ITパスポートTest06', 'ITパスポート', NULL, 2, 'test6', 'test6'),
(10070007, 'ITパスポートTest07', 'ITパスポート', NULL, 2, 'test7', 'test7'),
(10070008, 'ITパスポートTest08', 'ITパスポート', NULL, 2, 'test8', 'test8'),
(10070009, 'ITパスポートTest09', 'ITパスポート', NULL, 2, 'test9', 'test9'),
(10070010, 'ITパスポートTest10', 'ITパスポート', NULL, 2, 'test10', 'test10'),
-- 試験参考書 (1009xxxx)
(10090001, '試験参考書01', '試験参考書', NULL, 2, 'test1', 'test1'),
(10090002, '試験参考書02', '試験参考書', NULL, 2, 'test2', 'test2'),
(10090003, '試験参考書03', '試験参考書', NULL, 2, 'test3', 'test3'),
(10090004, '試験参考書04', '試験参考書', NULL, 2, 'test4', 'test4'),
(10090005, '試験参考書05', '試験参考書', NULL, 2, 'test5', 'test5'),
(10090006, '試験参考書06', '試験参考書', NULL, 2, 'test6', 'test6'),
(10090007, '試験参考書07', '試験参考書', NULL, 2, 'test7', 'test7'),
(10090008, '試験参考書08', '試験参考書', NULL, 2, 'test8', 'test8');
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
