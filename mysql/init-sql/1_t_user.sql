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
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_user` (
  `user_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `mail_address` varchar(255) DEFAULT NULL,
  `role` int DEFAULT '0',
  `department_id` int DEFAULT NULL,
  `delete_flg` int DEFAULT 0,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_user`
--

LOCK TABLES `t_user` WRITE;
/*!40000 ALTER TABLE `t_user` DISABLE KEYS */;
INSERT INTO `t_user` VALUES
(1, 'admin', '$2a$10$QNW1k/8Ngi8cX0i6Zel2HOwFVZPmW9Qn1SGizlEizKzKIkOIzO/Tq', 'admin@gmail.com', 1, 3, 0),
(2, 'admin02', '$2a$10$jKTjxduxe9XTUaCD5jjV8.kk3xP0BTOWI5DryZX5Ymie790mPbFc6', 'admin02@gmail.com', 1, 3, 0),
(3, 'deleteadmin03', '$2a$10$miU5HkpUaycizfCe2zjibueTZv0tK9IapXYtUxXHRb605s8Rk9qDG', 'admin03@gmail.com', 1, 3, 1),
(4, 'normal001', '$2a$10$9VfY0QMx8w4WFyeh..rGlu9Mk.IHjEmco8Wv017qzGWP6sHYOVp96', 'normal001@gmail.com', 0, 1, 0),
(5, 'normal002', '$2a$10$NBiQMrRhWGlEhr9DkiuGn.Ie/2vvd7/4oPTq1MDcYpOgSXGkHvQWC', 'normal002@gmail.com', 0, 2, 0),
(6, 'normal003', '$2a$10$dVkyl0dHm/xpa/64PdKbTeYSTU0ohyhcHrMmlHA4fCpIEFrJhJ9VK', 'normal003@gmail.com', 0, 3, 0),
(7, 'normal004', '$2a$10$PR4yqu2Y8WokAxQOMDY.pud5av9TsBXoA/Eq6PfYqwH5MBC9TGVTG', 'normal004@gmail.com', 0, 4, 0),
(8, 'normal005', '$2a$10$jzQLro4MCHSaituJ.httKu30UsLi/sebv37ip15QUUugUhhl219Ni', 'normal005@gmail.com', 0, 5, 0),
(9, 'normal006', '$2a$10$AwedasE9nYncTsM69yM5YeAk7Ps.HTH1FHR5TluIZ6K.tiq0xIOxu', 'normal006@gmail.com', 0, 6, 0),
(10, 'normal007', '$2a$10$bxGv6xFWRWeNy3nPBIt40eZcLmM66d19f24eP3iqxgQhaRekDHt.O', 'normal007@gmail.com', 0, 7, 0),
(11, 'normal008', '$2a$10$Za7Yy.6OVbf7frrlHzPU3.AxLAQ7YqAT5qpES./jIXte00mVt1xUe', 'normal008@gmail.com', 0, 8, 0),
(12, 'normal009', '$2a$10$WPkCAGkwQAIyeyI05YzhS.c2vfdWu2l/awNSxeYp6PPyBWYugSLvi', 'normal009@gmail.com', 0, 9, 0),
(13, 'normal010', '$2a$10$7ejSAFuowY5zYmT18EMjiuifjxPtifB3D.U9Zo5gf9wBxFc5Jl4ji', 'normal010@gmail.com', 0, 10, 0),
(14, 'normal011', '$2a$10$KC6xuv2CQNHUxdat.d.2VOrSCvPnI4f6qq1pjdZhlIgdlUi4w0AqG', 'normal011@gmail.com', 0, 11, 0),
(15, 'normal012', '$2a$10$Hs/GgXhLca/VddKJtVOdGOxwaU6HZUTvWPlV3tehmBLn2IaWRNEEO', 'normal012@gmail.com', 0, 12, 0),
(16, 'normal013', '$2a$10$xmKFHaFg7OlMCdNQ7TShE.t3.KgbttxEHQbu0bckbEeolp/jBvuKC', 'normal013@gmail.com', 0, 13, 0),
(17, 'normal014', '$2a$10$e4T/V8OxC5ZnzbmLyCa8xeBKFlo.5yIvyOTFuQuLd/Nug4azIStXO', 'normal014@gmail.com', 0, 14, 0),
(18, 'normal015', '$2a$10$/NNtgEjM2SHrGZD7mrQNE.fKKXec.RH7X.C4Oi4aM7RdY1uADf6Ai', 'normal015@gmail.com', 0, 15, 0),
(19, 'normal016', '$2a$10$f5KpDW1HsC5ykstJYT4TpeCoYSMtunHyzzKSF8FC/nptCsF8T7GcC', 'normal016@gmail.com', 0, 16, 0),
(20, 'normal017', '$2a$10$PPpqQh5EtdYD90jJtDZehO.bB9dtmc9zKZxn8iCuqihOWOTa/EanS', 'normal017@gmail.com', 0, 17, 0),
(21, 'normal018', '$2a$10$.fABARdUETThgozGtARNqu67xdecQh20hTHnL/hNvQmp9DkEvJrhu', 'normal018@gmail.com', 0, 18, 0),
(22, 'normal019', '$2a$10$toiyFZbrytLdVnv8TwnuSuPWg6RQAcZY11spDEMgEPiNUd9TngPvS', 'normal019@gmail.com', 0, 19, 0),
(23, 'normal020', '$2a$10$yjQO8oVMdqaOsscmvjJ7buD6WBjCzQzpTJPZfc.2PnAd8M8GG0IgS', 'normal020@gmail.com', 0, 20, 0),
(24, 'normal021', '$2a$10$59FXiO4bmpOn9CMzqGNvzu0HGEoQRNUiJld63lbkIaUhMktqlO/AO', 'normal021@gmail.com', 0, 21, 0),
(25, 'normal022', '$2a$10$rjpI0RG08BKp1pUlirby6uvh3p7V5mjUKBxZDl8OxkiMszzoLQmj.', 'normal022@gmail.com', 0, 22, 0),
(26, 'deletenormal001', '$2a$10$LKHDPEx5JVwrRrDCwB10M.fZVFuRb39Ma7sjkZwj/dI4esLEyNX8W', 'deletenormal001@gmail.com', 0, 10, 1),
(27, 'deletenormal002', '$2a$10$EfAEXmoyNU7CxeNe/pr21OpxggX9Ky/npHfiFoTlovaQsRbUIl1SC', 'deletenormal002@gmail.com', 0, 11, 1),
(28, 'deletenormal003', '$2a$10$o46.JhHDIJA1zD21xJb79OaB2FDLRLjICXgh.UKPp/c/Lyvw/fJ7O', 'deletenormal003@gmail.com', 0, 12, 1);
/*!40000 ALTER TABLE `t_user` ENABLE KEYS */;
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
