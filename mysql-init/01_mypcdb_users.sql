-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: mypcdb
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@example.com','123456','管理員','台北市'),(2,'user@example.com','123456','測試會員','新北市'),(3,'tom@demo.com','888888','Tom','Taipei'),(4,'jenny@demo.com','999999','Jenny','Taipei'),(5,'vivi@demo.com','$2a$10$4IoJde1srV1b2oBFBA9sOupBZw92TQ2CkAW4G4ivsiXaIsuiMGMma','vivi','Taipei'),(6,'john@demo.com','$2a$10$S1WZ3.Z5EVjri7M8oEKQm.ma5awHG86GwWVaa0PXoBwj3zxAOLb3W','John','Taiwan'),(7,'tommy@demo.com','$2a$10$d6xssVs4Un7SmXVS4P1bOuBkEg.vUW0p6JvemLvCr0IT2kn.8emJS','Tommy','Tainan'),(8,'123@demo.com','$2a$10$/Ato6IRXkaTOfDAau1um6uvMswdVEcavAB4uAEezXySAyiJdrOrQa','123','456'),(10,'456@demo.com','$2a$10$kn90PsJewBSN4pYF5p3r9.7pDL7vujCDM6Uj.xt7nua74Qqy2H.zC','456','456'),(11,'789@demo.com','$2a$10$c0xBlQFB0G7Mz3fXD2/xvO3e2uPiRRwFvGjXWpsSbExrDfGbvRtz6','789','789'),(12,'000@demo.com','$2a$10$dwL03KNVVE35NTJ8q9dCf.vdD.GO4pQDakg3n5tQnLE6vtlbdCTc6','000','000'),(13,'888@demo.com','$2a$10$/umhjs1nadwZmKQRwH8kjeW9i17rgI//cx8YMhYQlGIuc7Yv0A11m','888','888'),(14,'999@demo.com','$2a$10$ATA1iwoMiu6BjPHg/6.YbuTzh9sAbfbqn1clMLLL0Mznq8TJFxuc.','999','999'),(15,'111@demo.com','$2a$10$SkqzUqrZfP/9q49EaXZxr.hjqZWHBn2yOHYzUujaujy1YvL6GYnCy','111','111'),(16,'mary@demo.com','$2a$10$8uYp2hSsSnk9n5UKPji2W.UGmFBZxYRmR4cLivA27/cjxIX05r4Im','Mary','台北市'),(17,'peter@demo.com','$2a$10$HkxfABiRkVu9xs7dzeTrYu2K6oYRAPtiehffkPUJuGQ8P0QFvoNJO','Peter','新北市'),(18,'amy@demo.com','$2a$10$8Xi9MD8E/HYJpgUmpHndIOMkbEKN02xXFXmrNBlIK1RBlrFrAOGJ2','Amy','桃園市');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 21:38:57
