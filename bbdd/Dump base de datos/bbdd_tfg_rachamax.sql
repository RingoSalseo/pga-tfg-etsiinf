-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: bbdd_tfg
-- ------------------------------------------------------
-- Server version	8.0.35

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
-- Table structure for table `rachamax`
--

DROP TABLE IF EXISTS `rachamax`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rachamax` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_dia` int DEFAULT NULL,
  `periodo` varchar(10) DEFAULT NULL,
  `velocidad` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_dia` (`id_dia`),
  CONSTRAINT `rachamax_ibfk_1` FOREIGN KEY (`id_dia`) REFERENCES `dia` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rachamax`
--

LOCK TABLES `rachamax` WRITE;
/*!40000 ALTER TABLE `rachamax` DISABLE KEYS */;
INSERT INTO `rachamax` VALUES (208,0,'00-24',''),(209,0,'00-12',''),(210,0,'12-24',''),(211,0,'00-06',''),(212,0,'06-12',''),(213,0,'12-18',''),(214,0,'18-24',''),(215,1,'00-24',''),(216,1,'00-12',''),(217,1,'12-24','35'),(218,1,'00-06',''),(219,1,'06-12',''),(220,1,'12-18','35'),(221,1,'18-24','35'),(222,2,'00-24',''),(223,2,'00-12','45'),(224,2,'12-24','55'),(225,3,'00-24',''),(226,3,'00-12',''),(227,3,'12-24',''),(228,4,NULL,''),(229,5,NULL,''),(230,6,NULL,'');
/*!40000 ALTER TABLE `rachamax` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-15 17:59:40
