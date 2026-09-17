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
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,12,'000','test test','2026-09-09 20:48:19'),(2,13,'888','123 123','2026-09-09 20:49:03'),(3,10,'456','lucky  lucky','2026-09-09 20:49:52'),(4,1,'管理員','歡迎來到 MyPC 商城留言板，如果有任何購物問題都可以留言給我們。','2026-09-01 09:10:00'),(5,2,'測試會員','無線滑鼠使用起來很順，連線速度也很穩定。','2026-09-02 10:25:00'),(6,3,'Tom','機械式鍵盤的手感很好，打字聲音也很有質感。','2026-09-03 14:18:00'),(7,4,'Jenny','USB-C 集線器很方便，筆電接螢幕和隨身碟都沒問題。','2026-09-04 16:40:00'),(8,2,'測試會員','65W USB-C 旅充體積小，出差攜帶很方便。','2026-09-05 11:05:00'),(9,3,'Tom','藍牙耳機音質比預期好，配戴也算舒服。','2026-09-06 13:22:00'),(10,4,'Jenny','網路攝影機畫質清楚，拿來開線上會議很適合。','2026-09-07 09:35:00'),(11,2,'測試會員','桌上型麥克風收音效果不錯，希望未來可以多一些顏色選擇。','2026-09-08 15:12:00'),(12,3,'Tom','256GB USB 隨身碟傳輸速度不錯，容量也很夠用。','2026-09-09 10:48:00'),(13,4,'Jenny','24 吋螢幕顯示效果很好，價格也算合理。','2026-09-10 17:20:00'),(14,2,'測試會員','筆電支架很穩，使用後散熱也有明顯改善。','2026-09-11 08:50:00'),(15,3,'Tom','商品頁面的分類很清楚，搜尋商品很方便。','2026-09-11 09:30:00'),(16,4,'Jenny','購物車和付款流程操作很直覺，希望之後可以增加更多付款方式。','2026-09-11 10:15:00'),(17,2,'測試會員','訂單完成後可以下載 PDF 訂單，這個功能很實用。','2026-09-11 11:05:00'),(18,3,'Tom','留言板即時更新速度很快，整體使用體驗不錯。','2026-09-11 12:30:00'),(19,14,'999','沒用過','2026-09-11 15:40:47'),(20,15,'111','111','2026-09-13 14:33:50'),(21,1,'管理員','123','2026-09-14 14:26:33'),(22,13,'888','收到錯誤商品','2026-09-14 16:28:36'),(23,8,'123','tets','2026-09-16 15:09:27'),(24,8,'123','test','2026-09-16 15:21:48'),(25,8,'123','JMeter TC07 留言測試','2026-09-16 16:19:01'),(26,15,'111','test','2026-09-16 20:39:35');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
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
