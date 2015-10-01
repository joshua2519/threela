-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: localhost    Database: threela
-- ------------------------------------------------------
-- Server version	5.6.25-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `totaletf`
--

DROP TABLE IF EXISTS `totaletf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `totaletf` (
  `StockId` varchar(10) NOT NULL,
  `ETFName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`StockId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `totaletf`
--

LOCK TABLES `totaletf` WRITE;
/*!40000 ALTER TABLE `totaletf` DISABLE KEYS */;
INSERT INTO `totaletf` VALUES ('0050','台灣50'),('0051','中100'),('0052','FB科技'),('0053','寶電子'),('0054','台商50'),('0055','寶金融'),('0056','高股息'),('0057','FB摩台'),('0058','FB發達'),('0059','FB金融'),('0060','新台灣'),('0061','寶滬深'),('006203','寶摩臺'),('006204','豐臺灣'),('006205','FB上証'),('006206','元上證'),('006207','FH滬深'),('006208','FB台50'),('00631L','T50正2'),('00632R','T50反1'),('00633L','上証2X'),('00634R','上証反'),('00635U','元黃金'),('00636','CFA50'),('00637L','滬深2X'),('00638R','滬深反'),('00639','深100'),('00640L','日本2X'),('00641R','日本反'),('00642U','元石油'),('0080','恒中國'),('0081','恒香港'),('008201','上證50');
/*!40000 ALTER TABLE `totaletf` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-30 13:50:43
