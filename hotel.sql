-- MySQL dump 10.13  Distrib 8.0.44, for macos15 (arm64)
--
-- Host: localhost    Database: hotelmanagement
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `camera`
--

DROP TABLE IF EXISTS `camera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camera` (
  `id_camera` int NOT NULL AUTO_INCREMENT,
  `capienza` int NOT NULL,
  `numero` varchar(10) NOT NULL,
  `prezzo_base` decimal(10,2) NOT NULL,
  `stato` enum('DA_PULIRE','LIBERA','OCCUPATA') NOT NULL,
  `id_struttura` int NOT NULL,
  PRIMARY KEY (`id_camera`),
  KEY `FKinwstj563sky713ikoxe1kxov` (`id_struttura`),
  CONSTRAINT `FKinwstj563sky713ikoxe1kxov` FOREIGN KEY (`id_struttura`) REFERENCES `struttura` (`id_struttura`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `camera`
--

LOCK TABLES `camera` WRITE;
/*!40000 ALTER TABLE `camera` DISABLE KEYS */;
INSERT INTO `camera` VALUES (1,2,'101',50.00,'OCCUPATA',1),(2,4,'102',100.00,'OCCUPATA',1),(3,6,'103',150.00,'LIBERA',1);
/*!40000 ALTER TABLE `camera` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `luce`
--

DROP TABLE IF EXISTS `luce`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `luce` (
  `id_luce` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `stato` enum('OFF','ON') NOT NULL,
  `id_camera` int NOT NULL,
  PRIMARY KEY (`id_luce`),
  KEY `FKf285fbu48skt56jpq0fer4efy` (`id_camera`),
  CONSTRAINT `FKf285fbu48skt56jpq0fer4efy` FOREIGN KEY (`id_camera`) REFERENCES `camera` (`id_camera`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `luce`
--

LOCK TABLES `luce` WRITE;
/*!40000 ALTER TABLE `luce` DISABLE KEYS */;
INSERT INTO `luce` VALUES (1,'Luce principale','OFF',1),(2,'Luce principale','OFF',2);
/*!40000 ALTER TABLE `luce` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ospite`
--

DROP TABLE IF EXISTS `ospite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ospite` (
  `id_ospite` int NOT NULL AUTO_INCREMENT,
  `cittadinanza` varchar(50) NOT NULL,
  `cognome` varchar(50) NOT NULL,
  `data_nascita` date NOT NULL,
  `esente` bit(1) NOT NULL,
  `luogo_nascita` varchar(100) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `numero_doc` varchar(50) DEFAULT NULL,
  `tipo_doc` enum('CARTA_IDENTITA','PASSAPORTO','PATENTE') DEFAULT NULL,
  `id_prenotazione` int NOT NULL,
  PRIMARY KEY (`id_ospite`),
  KEY `FKic2mpt5hl5jat8ial46d3365q` (`id_prenotazione`),
  CONSTRAINT `FKic2mpt5hl5jat8ial46d3365q` FOREIGN KEY (`id_prenotazione`) REFERENCES `prenotazione` (`id_prenotazione`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ospite`
--

LOCK TABLES `ospite` WRITE;
/*!40000 ALTER TABLE `ospite` DISABLE KEYS */;
INSERT INTO `ospite` VALUES (1,'Italiana','Girgenti','2000-12-12',_binary '\0','Palermo','Simone','CA6769','PATENTE',1),(2,'Italiana','Guarino','2000-01-01',_binary '\0','Marsala','Domenico',NULL,NULL,1),(3,'Italiana','Girgenti','2000-01-01',_binary '\0','Palermo','Simone','CA1293','CARTA_IDENTITA',2),(4,'Italiana','Marino','2000-01-10',_binary '\0','Marsala','Marco',NULL,NULL,2),(5,'Italiana','Girgenti','2000-01-01',_binary '\0','Catanzaro','Simone','CA1293','CARTA_IDENTITA',5),(6,'Italiana','Girgenti','2010-01-01',_binary '\0','Palermo','Simone','CA6769','CARTA_IDENTITA',3),(7,'Italiana','Castro','2000-01-01',_binary '\0','Messina','Roberto',NULL,NULL,3),(8,'Italia','Girgenti','2000-01-01',_binary '\0','Palermo','Simone','CA1293','CARTA_IDENTITA',4),(9,'Italiana','Badagliacca','2000-01-01',_binary '\0','Caccamo','Dario',NULL,NULL,4),(10,'Italiana','Girgenti','2000-01-01',_binary '\0','Palermo','Simone','seeqweqw','PATENTE',6),(11,'Italiana','Selvarajah','1850-01-01',_binary '','Marsala','Senthuran',NULL,NULL,6),(12,'Italiana','Girgenti','2001-01-01',_binary '\0','Palermo','Simone','CA1293','CARTA_IDENTITA',7);
/*!40000 ALTER TABLE `ospite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prenotazione`
--

DROP TABLE IF EXISTS `prenotazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prenotazione` (
  `id_prenotazione` int NOT NULL AUTO_INCREMENT,
  `data_checkin` date NOT NULL,
  `data_checkout` date NOT NULL,
  `note_cliente` text,
  `num_ospiti` int NOT NULL,
  `num_ospiti_esenti_dichiarati` int NOT NULL,
  `prezzo_pagato` decimal(10,2) NOT NULL,
  `stato` enum('CONFERMATA','IN_CORSO','TERMINATA','CANCELLATA') DEFAULT 'CONFERMATA',
  `id_camera` int NOT NULL,
  `id_utente` int NOT NULL,
  PRIMARY KEY (`id_prenotazione`),
  KEY `FK9ceyx5p7bsltvyp2hi0wqds8x` (`id_camera`),
  KEY `FKcf4f4g2spway3a3jt5np1ac33` (`id_utente`),
  CONSTRAINT `FK9ceyx5p7bsltvyp2hi0wqds8x` FOREIGN KEY (`id_camera`) REFERENCES `camera` (`id_camera`),
  CONSTRAINT `FKcf4f4g2spway3a3jt5np1ac33` FOREIGN KEY (`id_utente`) REFERENCES `utente` (`id_utente`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prenotazione`
--

LOCK TABLES `prenotazione` WRITE;
/*!40000 ALTER TABLE `prenotazione` DISABLE KEYS */;
INSERT INTO `prenotazione` VALUES (1,'2025-12-23','2025-12-30','[23/12 00:17] Cuscini\n[23/12 00:17] Colazione a letto\n',2,0,378.00,'TERMINATA',1,2),(2,'2026-01-05','2026-01-10','[05/01 22:02] 200 pesos\n',2,0,270.00,'TERMINATA',1,2),(3,'2026-01-20','2026-01-25',NULL,2,0,270.00,'IN_CORSO',1,2),(4,'2026-02-24','2026-02-28',NULL,2,0,216.00,'IN_CORSO',1,2),(5,'2026-01-17','2026-01-18','[17/01 00:06] Cuscini nuovi\n[17/01 00:06] Spazzolino \n[17/01 00:06] Azoto liquido \n',1,0,52.00,'TERMINATA',1,2),(6,'2026-03-02','2026-03-09',NULL,2,1,364.00,'IN_CORSO',1,2),(7,'2026-01-18','2026-01-30','[18/01 12:32] Asciugamano\n',1,0,1224.00,'IN_CORSO',2,2),(8,'2026-01-25','2026-01-30',NULL,4,1,880.00,'CONFERMATA',3,2);
/*!40000 ALTER TABLE `prenotazione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prenotazione_servizio`
--

DROP TABLE IF EXISTS `prenotazione_servizio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prenotazione_servizio` (
  `id_prenotazione_servizio` int NOT NULL AUTO_INCREMENT,
  `data_acquisto` datetime(6) DEFAULT NULL,
  `prezzo_pagato` decimal(10,2) NOT NULL,
  `quantita` int NOT NULL,
  `id_prenotazione` int NOT NULL,
  `id_servizio` int NOT NULL,
  PRIMARY KEY (`id_prenotazione_servizio`),
  KEY `FK9mtkj3s1rjqro4f0vlfcbwiyx` (`id_prenotazione`),
  KEY `FKcsi0qn1bwa8u9p8es7kfdbi0d` (`id_servizio`),
  CONSTRAINT `FK9mtkj3s1rjqro4f0vlfcbwiyx` FOREIGN KEY (`id_prenotazione`) REFERENCES `prenotazione` (`id_prenotazione`),
  CONSTRAINT `FKcsi0qn1bwa8u9p8es7kfdbi0d` FOREIGN KEY (`id_servizio`) REFERENCES `servizio` (`id_servizio`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prenotazione_servizio`
--

LOCK TABLES `prenotazione_servizio` WRITE;
/*!40000 ALTER TABLE `prenotazione_servizio` DISABLE KEYS */;
INSERT INTO `prenotazione_servizio` VALUES (1,'2026-01-18 11:31:27.814046',20.00,4,8,1),(2,'2026-01-18 11:31:27.814308',5.00,4,8,2);
/*!40000 ALTER TABLE `prenotazione_servizio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servizio`
--

DROP TABLE IF EXISTS `servizio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servizio` (
  `id_servizio` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `prezzo` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_servizio`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servizio`
--

LOCK TABLES `servizio` WRITE;
/*!40000 ALTER TABLE `servizio` DISABLE KEYS */;
INSERT INTO `servizio` VALUES (1,'SPA',20.00),(2,'Colazione',5.00),(3,'Piscina',10.00);
/*!40000 ALTER TABLE `servizio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ServiziStruttura`
--

DROP TABLE IF EXISTS `ServiziStruttura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ServiziStruttura` (
  `id_struttura` int NOT NULL,
  `id_servizio` int NOT NULL,
  PRIMARY KEY (`id_struttura`,`id_servizio`),
  KEY `FK1apmvbd4v5a2gts0isuoati7l` (`id_servizio`),
  CONSTRAINT `FK1apmvbd4v5a2gts0isuoati7l` FOREIGN KEY (`id_servizio`) REFERENCES `servizio` (`id_servizio`),
  CONSTRAINT `FKj3uwqxdkvb2djt00dj21i652i` FOREIGN KEY (`id_struttura`) REFERENCES `struttura` (`id_struttura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ServiziStruttura`
--

LOCK TABLES `ServiziStruttura` WRITE;
/*!40000 ALTER TABLE `ServiziStruttura` DISABLE KEYS */;
INSERT INTO `ServiziStruttura` VALUES (1,1),(1,2),(1,3);
/*!40000 ALTER TABLE `ServiziStruttura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storico_pulizie`
--

DROP TABLE IF EXISTS `storico_pulizie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storico_pulizie` (
  `id_pulizia` int NOT NULL AUTO_INCREMENT,
  `data_ora` datetime(6) NOT NULL,
  `note` text,
  `id_camera` int NOT NULL,
  `id_staff` int NOT NULL,
  PRIMARY KEY (`id_pulizia`),
  KEY `FK83kupbyj6gqg6s10518qih24f` (`id_camera`),
  KEY `FKealgx4t4kg649nkya0dlwty2s` (`id_staff`),
  CONSTRAINT `FK83kupbyj6gqg6s10518qih24f` FOREIGN KEY (`id_camera`) REFERENCES `camera` (`id_camera`),
  CONSTRAINT `FKealgx4t4kg649nkya0dlwty2s` FOREIGN KEY (`id_staff`) REFERENCES `utente` (`id_utente`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storico_pulizie`
--

LOCK TABLES `storico_pulizie` WRITE;
/*!40000 ALTER TABLE `storico_pulizie` DISABLE KEYS */;
INSERT INTO `storico_pulizie` VALUES (1,'2025-12-22 23:18:42.237094',NULL,1,3),(2,'2026-01-05 21:03:20.216607',NULL,1,3),(3,'2026-01-16 23:08:59.221573',NULL,1,3);
/*!40000 ALTER TABLE `storico_pulizie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `struttura`
--

DROP TABLE IF EXISTS `struttura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `struttura` (
  `id_struttura` int NOT NULL AUTO_INCREMENT,
  `citta` varchar(50) NOT NULL,
  `indirizzo` varchar(250) NOT NULL,
  `nome` varchar(100) NOT NULL,
  PRIMARY KEY (`id_struttura`),
  UNIQUE KEY `uq_struttura_reale` (`indirizzo`,`citta`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `struttura`
--

LOCK TABLES `struttura` WRITE;
/*!40000 ALTER TABLE `struttura` DISABLE KEYS */;
INSERT INTO `struttura` VALUES (1,'Palermo','Via Roma, 12','Grand Palace Hotel');
/*!40000 ALTER TABLE `struttura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tapparella`
--

DROP TABLE IF EXISTS `tapparella`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tapparella` (
  `id_tapparella` int NOT NULL AUTO_INCREMENT,
  `livello` int NOT NULL,
  `nome` varchar(50) NOT NULL,
  `id_camera` int NOT NULL,
  PRIMARY KEY (`id_tapparella`),
  KEY `FKqf0bua6yfjdptc8wbyt3qnwn4` (`id_camera`),
  CONSTRAINT `FKqf0bua6yfjdptc8wbyt3qnwn4` FOREIGN KEY (`id_camera`) REFERENCES `camera` (`id_camera`),
  CONSTRAINT `tapparella_chk_1` CHECK (((`livello` <= 100) and (`livello` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tapparella`
--

LOCK TABLES `tapparella` WRITE;
/*!40000 ALTER TABLE `tapparella` DISABLE KEYS */;
INSERT INTO `tapparella` VALUES (1,70,'Principale',1);
/*!40000 ALTER TABLE `tapparella` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `termostato`
--

DROP TABLE IF EXISTS `termostato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `termostato` (
  `id_termostato` int NOT NULL AUTO_INCREMENT,
  `modalita` enum('OFF','COOL','HEAT') DEFAULT 'OFF',
  `temp` decimal(3,1) DEFAULT NULL,
  `id_camera` int NOT NULL,
  PRIMARY KEY (`id_termostato`),
  KEY `FKgu9x19lk4bhm3l9qte8sin6jc` (`id_camera`),
  CONSTRAINT `FKgu9x19lk4bhm3l9qte8sin6jc` FOREIGN KEY (`id_camera`) REFERENCES `camera` (`id_camera`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `termostato`
--

LOCK TABLES `termostato` WRITE;
/*!40000 ALTER TABLE `termostato` DISABLE KEYS */;
INSERT INTO `termostato` VALUES (1,'COOL',23.0,1),(2,'HEAT',24.0,2);
/*!40000 ALTER TABLE `termostato` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utente` (
  `id_utente` int NOT NULL AUTO_INCREMENT,
  `cognome` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `nome` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `ruolo` enum('ADMIN','CLIENTE','STAFF') NOT NULL,
  PRIMARY KEY (`id_utente`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utente`
--

LOCK TABLES `utente` WRITE;
/*!40000 ALTER TABLE `utente` DISABLE KEYS */;
INSERT INTO `utente` VALUES (1,'Tager','mohamed.tager@gmail.com','Mohamed','$2a$10$tszq6tbpePg8t9HC2c/MlueHytFXEqVo3zmnaQeFX8..aYF2u9bUy','ADMIN'),(2,'Girgenti','simone.girgenti@gmail.com','Simone','$2a$10$l2ch1wx3SCy4FSV..Y/ef.w8Q9gxgsztAkh/i6SMFXMOdp9GA1RRa','CLIENTE'),(3,'Saitta','walter.saitta@gmail.com','Walter','$2a$10$BgI/FdWQsD3pc/4SSWZM.OklYEwOgyyxkZC.RQpFT2guup25y7XJq','STAFF'),(4,'Caruso','davide.caruso@gmail.com','Davide','$2a$10$hsU2GKjo2yyT7hJ2OFzUs.A5m2P1Vp6F/gGZa1./0ndg6XizbTIiC','STAFF');
/*!40000 ALTER TABLE `utente` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-18 12:42:59
