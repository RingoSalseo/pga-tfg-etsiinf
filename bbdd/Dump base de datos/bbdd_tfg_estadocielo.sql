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
-- Table structure for table `estadocielo`
--

DROP TABLE IF EXISTS `estadocielo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estadocielo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_dia` int DEFAULT NULL,
  `valor` varchar(10) DEFAULT NULL,
  `periodo` varchar(10) DEFAULT NULL,
  `descripcion` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_dia` (`id_dia`),
  CONSTRAINT `estadocielo_ibfk_1` FOREIGN KEY (`id_dia`) REFERENCES `dia` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=254 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estadocielo`
--

LOCK TABLES `estadocielo` WRITE;
/*!40000 ALTER TABLE `estadocielo` DISABLE KEYS */;
INSERT INTO `estadocielo` VALUES (231,0,'','00-24',''),(232,0,'','00-12',''),(233,0,'15','12-24','Muy nuboso'),(234,0,'','00-06',''),(235,0,'12','06-12','Poco nuboso'),(236,0,'15','12-18','Muy nuboso'),(237,0,'14','18-24','Nuboso'),(238,1,'25','00-24','Muy nuboso con lluvia'),(239,1,'26','00-12','Cubierto con lluvia'),(240,1,'25','12-24','Muy nuboso con lluvia'),(241,1,'81n','00-06','Niebla '),(242,1,'46','06-12','Cubierto con lluvia escasa'),(243,1,'25','12-18','Muy nuboso con lluvia'),(244,1,'25','18-24','Muy nuboso con lluvia'),(245,2,'25','00-24','Muy nuboso con lluvia'),(246,2,'53','00-12','Muy nuboso con tormenta'),(247,2,'53','12-24','Muy nuboso con tormenta'),(248,3,'25','00-24','Muy nuboso con lluvia'),(249,3,'53','00-12','Muy nuboso con tormenta'),(250,3,'25','12-24','Muy nuboso con lluvia'),(251,4,'45',NULL,'Muy nuboso con lluvia escasa'),(252,5,'44',NULL,'Nuboso con lluvia escasa'),(253,6,'13',NULL,'Intervalos nubosos');
/*!40000 ALTER TABLE `estadocielo` ENABLE KEYS */;
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
