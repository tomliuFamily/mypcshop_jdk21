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
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` double NOT NULL,
  `category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `sales_count` int NOT NULL DEFAULT '1000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'無線滑鼠',599,'滑鼠','2.4G 無線滑鼠，適合辦公與日常使用','/mouse.png',965,1035),(2,'機械式鍵盤',1290,'鍵盤','USB 機械式鍵盤，適合辦公與遊戲','/keyboard.png',982,1020),(3,'USB-C 集線器',890,'集線器','多功能 USB-C Hub，支援 USB 與 HDMI','/usb-c-hub.png',997,1006),(4,'65W USB-C 旅充',1090,'充電器','65W USB-C 快速充電器，適合手機與筆電','/charger-65w.png',999,1001),(5,'藍牙耳機',1590,'耳機','無線藍牙耳機，適合通勤與日常使用','/headphones.png',999,1001),(6,'1080P 網路攝影機',799,'攝影機','1080P USB 網路攝影機，適合視訊會議','/webcam.png',999,1002),(7,'桌上型麥克風',1190,'麥克風','USB 桌上型麥克風，適合會議與直播','/microphone.png',999,1002),(8,'256GB USB 隨身碟',499,'儲存裝置','256GB USB 3.0 隨身碟','/usb-drive.png',997,1003),(9,'24 吋 LED 螢幕',3990,'螢幕','24 吋 Full HD LED 顯示器','/monitor.png',999,1002),(10,'筆電支架',699,'周邊','鋁合金可調式筆電支架','/laptop-stand.png',999,1003);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 21:38:56
