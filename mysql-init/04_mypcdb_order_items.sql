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
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `price` double NOT NULL,
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,3,599,NULL,NULL,NULL),(2,1,2,1,1290,NULL,NULL,NULL),(3,2,5,1,1590,NULL,NULL,NULL),(4,2,6,1,799,NULL,NULL,NULL),(5,2,4,1,1090,NULL,NULL,NULL),(6,2,8,1,499,NULL,NULL,NULL),(7,3,1,1,599,NULL,NULL,NULL),(8,3,2,1,1290,NULL,NULL,NULL),(9,3,3,1,890,NULL,NULL,NULL),(10,3,6,1,799,NULL,NULL,NULL),(11,3,5,1,1590,NULL,NULL,NULL),(12,3,4,1,1090,NULL,NULL,NULL),(13,3,8,1,499,NULL,NULL,NULL),(14,4,2,2,1290,NULL,NULL,NULL),(15,4,10,2,699,NULL,NULL,NULL),(16,4,8,2,499,NULL,NULL,NULL),(17,4,7,2,1190,NULL,NULL,NULL),(18,4,9,2,3990,NULL,NULL,NULL),(19,5,4,4,1090,NULL,NULL,NULL),(20,5,5,3,1590,NULL,NULL,NULL),(21,5,8,5,499,NULL,NULL,NULL),(22,5,7,2,1190,NULL,NULL,NULL),(23,6,1,1,599,NULL,NULL,NULL),(24,6,2,1,1290,NULL,NULL,NULL),(25,6,3,1,890,NULL,NULL,NULL),(26,7,8,1,499,NULL,NULL,NULL),(27,7,7,1,1190,NULL,NULL,NULL),(28,7,9,1,3990,NULL,NULL,NULL),(29,8,7,1,1190,NULL,NULL,NULL),(30,8,8,1,499,NULL,NULL,NULL),(31,8,5,1,1590,NULL,NULL,NULL),(32,8,4,1,1090,NULL,NULL,NULL),(33,9,10,3,699,NULL,NULL,NULL),(34,9,8,3,499,NULL,NULL,NULL),(35,9,5,2,1590,NULL,NULL,NULL),(36,10,1,1,599,NULL,NULL,NULL),(37,10,2,1,1290,NULL,NULL,NULL),(38,10,3,1,890,NULL,NULL,NULL),(39,10,6,1,799,NULL,NULL,NULL),(40,10,5,1,1590,NULL,NULL,NULL),(41,10,4,1,1090,NULL,NULL,NULL),(42,10,7,1,1190,NULL,NULL,NULL),(43,10,8,1,499,NULL,NULL,NULL),(44,10,9,1,3990,NULL,NULL,NULL),(45,10,10,1,699,NULL,NULL,NULL),(46,11,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(47,11,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(48,11,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(49,12,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(50,12,7,3,1190,'桌上型麥克風','/microphone.png','麥克風'),(51,12,9,3,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(52,13,4,1,1090,'65W USB-C 旅充','/charger-65w.png','充電器'),(53,13,5,1,1590,'藍牙耳機','/headphones.png','耳機'),(54,13,6,1,799,'1080P 網路攝影機','/webcam.png','攝影機'),(55,13,9,1,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(56,13,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(57,13,7,1,1190,'桌上型麥克風','/microphone.png','麥克風'),(58,14,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(59,14,9,1,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(60,14,10,2,699,'筆電支架','/laptop-stand.png','周邊'),(61,14,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(62,15,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(63,15,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(64,15,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(65,15,4,1,1090,'65W USB-C 旅充','/charger-65w.png','充電器'),(66,15,5,1,1590,'藍牙耳機','/headphones.png','耳機'),(67,15,6,1,799,'1080P 網路攝影機','/webcam.png','攝影機'),(68,15,7,1,1190,'桌上型麥克風','/microphone.png','麥克風'),(69,15,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(70,15,9,1,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(71,16,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(72,16,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(73,16,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(74,16,6,1,799,'1080P 網路攝影機','/webcam.png','攝影機'),(75,17,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(76,17,7,1,1190,'桌上型麥克風','/microphone.png','麥克風'),(77,17,9,1,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(78,17,10,1,699,'筆電支架','/laptop-stand.png','周邊'),(79,18,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(80,18,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(81,18,4,1,1090,'65W USB-C 旅充','/charger-65w.png','充電器'),(82,19,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(83,19,2,3,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(84,19,4,1,1090,'65W USB-C 旅充','/charger-65w.png','充電器'),(85,19,3,4,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(86,19,6,2,799,'1080P 網路攝影機','/webcam.png','攝影機'),(87,19,5,1,1590,'藍牙耳機','/headphones.png','耳機'),(88,19,8,1,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(89,19,10,3,699,'筆電支架','/laptop-stand.png','周邊'),(90,19,9,2,3990,'24 吋 LED 螢幕','/monitor.png','螢幕'),(91,19,7,2,1190,'桌上型麥克風','/microphone.png','麥克風'),(92,20,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(93,20,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(94,21,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(95,21,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(96,21,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(97,22,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(98,22,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(99,23,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(100,23,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(101,24,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(102,24,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(103,25,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(104,26,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(105,27,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(106,28,3,1,890,'USB-C 集線器','/usb-c-hub.png','集線器'),(107,29,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(108,29,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(109,30,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(110,31,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(111,32,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(112,33,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(113,34,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(114,35,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(115,35,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(116,36,8,2,499,'256GB USB 隨身碟','/usb-drive.png','儲存裝置'),(117,37,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(118,38,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(119,38,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(120,39,1,4,599,'無線滑鼠','/mouse.png','滑鼠'),(121,40,1,2,599,'無線滑鼠','/mouse.png','滑鼠'),(122,41,2,7,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(123,41,1,2,599,'無線滑鼠','/mouse.png','滑鼠'),(124,42,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(125,43,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(126,44,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(127,45,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(128,46,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(129,47,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(130,48,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(131,49,2,1,1290,'機械式鍵盤','/keyboard.png','鍵盤'),(132,50,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(133,51,1,1,599,'無線滑鼠','/mouse.png','滑鼠'),(134,52,1,1,599,'無線滑鼠','/mouse.png','滑鼠');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 21:38:58
