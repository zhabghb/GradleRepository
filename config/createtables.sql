-- MySQL dump 10.13  Distrib 5.5.55, for debian-linux-gnu (x86_64)
--
-- Host: 172.16.50.21    Database: metagridtest
-- ------------------------------------------------------
-- Server version	5.6.32-log

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
-- Table structure for table `all_col_dict`
--

DROP TABLE IF EXISTS `all_col_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `all_col_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `tab` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11808 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_col_dict`
--

LOCK TABLES `all_col_dict` WRITE;
/*!40000 ALTER TABLE `all_col_dict` DISABLE KEYS */;
/*!40000 ALTER TABLE `all_col_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `all_col_dict_copy`
--

DROP TABLE IF EXISTS `all_col_dict_copy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `all_col_dict_copy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `tab` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11808 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_col_dict_copy`
--

LOCK TABLES `all_col_dict_copy` WRITE;
/*!40000 ALTER TABLE `all_col_dict_copy` DISABLE KEYS */;
/*!40000 ALTER TABLE `all_col_dict_copy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `all_tab_dict`
--

DROP TABLE IF EXISTS `all_tab_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `all_tab_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `tag` varchar(50) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `datavolume` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=878 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_tab_dict`
--

LOCK TABLES `all_tab_dict` WRITE;
/*!40000 ALTER TABLE `all_tab_dict` DISABLE KEYS */;
/*!40000 ALTER TABLE `all_tab_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `all_tag_dict`
--

DROP TABLE IF EXISTS `all_tag_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `all_tag_dict` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_mod` datetime DEFAULT NULL,
  `tag_color` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `total_expected` int(5) DEFAULT '0',
  `total_matched` int(5) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fi_tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10121 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `all_tag_dict`
--

LOCK TABLES `all_tag_dict` WRITE;
/*!40000 ALTER TABLE `all_tag_dict` DISABLE KEYS */;
/*!40000 ALTER TABLE `all_tag_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_domain`
--

DROP TABLE IF EXISTS `data_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_domain` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `description` text,
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `description` (`description`),
  FULLTEXT KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_domain`
--

LOCK TABLES `data_domain` WRITE;
/*!40000 ALTER TABLE `data_domain` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_domain` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_domain_attr`
--

DROP TABLE IF EXISTS `data_domain_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_domain_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `domain_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `domain_id` (`domain_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_domain_attr`
--

LOCK TABLES `data_domain_attr` WRITE;
/*!40000 ALTER TABLE `data_domain_attr` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_domain_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `data_source_owner`
--

DROP TABLE IF EXISTS `data_source_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data_source_owner` (
  `wh_etl_job_id` int(255) NOT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_platform` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_tel` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`wh_etl_job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data_source_owner`
--

LOCK TABLES `data_source_owner` WRITE;
/*!40000 ALTER TABLE `data_source_owner` DISABLE KEYS */;
/*!40000 ALTER TABLE `data_source_owner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel`
--

DROP TABLE IF EXISTS `datamodel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `owner` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `tag` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel`
--

LOCK TABLES `datamodel` WRITE;
/*!40000 ALTER TABLE `datamodel` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel_field`
--

DROP TABLE IF EXISTS `datamodel_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `field_alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel_field`
--

LOCK TABLES `datamodel_field` WRITE;
/*!40000 ALTER TABLE `datamodel_field` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel_json`
--

DROP TABLE IF EXISTS `datamodel_json`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel_json` (
  `id` int(11) NOT NULL,
  `json` text COLLATE utf8_bin,
  `name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel_json`
--

LOCK TABLES `datamodel_json` WRITE;
/*!40000 ALTER TABLE `datamodel_json` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel_json` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel_relation`
--

DROP TABLE IF EXISTS `datamodel_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) NOT NULL,
  `source_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `relation_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel_relation`
--

LOCK TABLES `datamodel_relation` WRITE;
/*!40000 ALTER TABLE `datamodel_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel_relation_type`
--

DROP TABLE IF EXISTS `datamodel_relation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel_relation_type` (
  `id` int(5) NOT NULL,
  `en_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ch_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `icon` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel_relation_type`
--

LOCK TABLES `datamodel_relation_type` WRITE;
/*!40000 ALTER TABLE `datamodel_relation_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel_relation_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datamodel_table`
--

DROP TABLE IF EXISTS `datamodel_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datamodel_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) NOT NULL,
  `table_id` int(11) NOT NULL,
  `table_alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `canvas_position` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_modelview_table_modelview` (`model_id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datamodel_table`
--

LOCK TABLES `datamodel_table` WRITE;
/*!40000 ALTER TABLE `datamodel_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `datamodel_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality`
--

DROP TABLE IF EXISTS `dataquality`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality` (
  `field_id` int(11) NOT NULL,
  `empties` bigint(20) NOT NULL,
  `valid_count` bigint(20) NOT NULL,
  `invalid_count` bigint(20) NOT NULL,
  `nulls` bigint(20) NOT NULL,
  `total_count` bigint(20) NOT NULL,
  `unique_values` bigint(20) DEFAULT NULL,
  `sample` mediumtext NOT NULL,
  `updatetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality`
--

LOCK TABLES `dataquality` WRITE;
/*!40000 ALTER TABLE `dataquality` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality1`
--

DROP TABLE IF EXISTS `dataquality1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality1` (
  `field_id` int(11) NOT NULL,
  `empties` bigint(20) NOT NULL,
  `valid_count` bigint(20) NOT NULL,
  `invalid_count` bigint(20) NOT NULL,
  `nulls` bigint(20) NOT NULL,
  `total_count` bigint(20) NOT NULL,
  `unique_values` bigint(20) DEFAULT NULL,
  `data` text,
  PRIMARY KEY (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality1`
--

LOCK TABLES `dataquality1` WRITE;
/*!40000 ALTER TABLE `dataquality1` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality2`
--

DROP TABLE IF EXISTS `dataquality2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality2` (
  `fieldId` int(11) NOT NULL,
  `empties` bigint(20) NOT NULL,
  `validCount` bigint(20) NOT NULL,
  `invalidCount` bigint(20) NOT NULL,
  `nulls` bigint(20) NOT NULL,
  `totalCount` bigint(20) NOT NULL,
  `uniqueValues` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality2`
--

LOCK TABLES `dataquality2` WRITE;
/*!40000 ALTER TABLE `dataquality2` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality_field_rule`
--

DROP TABLE IF EXISTS `dataquality_field_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality_field_rule` (
  `field_id` int(11) NOT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `dataset_id` int(11) DEFAULT NULL,
  `rule_id` int(5) DEFAULT NULL,
  `dag_id` varchar(255) DEFAULT NULL,
  `status` int(5) DEFAULT '0',
  `is_active` int(5) DEFAULT '1',
  `last_check_time` datetime DEFAULT NULL,
  `last_finish_time` datetime DEFAULT NULL,
  `next_check_time` datetime DEFAULT NULL,
  PRIMARY KEY (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality_field_rule`
--

LOCK TABLES `dataquality_field_rule` WRITE;
/*!40000 ALTER TABLE `dataquality_field_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality_field_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality_rule`
--

DROP TABLE IF EXISTS `dataquality_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality_rule` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `rule_key` varchar(255) DEFAULT NULL,
  `rule_name` varchar(255) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality_rule`
--

LOCK TABLES `dataquality_rule` WRITE;
/*!40000 ALTER TABLE `dataquality_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality_topn`
--

DROP TABLE IF EXISTS `dataquality_topn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality_topn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field_id` int(11) NOT NULL,
  `field_value` varchar(1000) DEFAULT NULL,
  `count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality_topn`
--

LOCK TABLES `dataquality_topn` WRITE;
/*!40000 ALTER TABLE `dataquality_topn` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality_topn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality_topn1`
--

DROP TABLE IF EXISTS `dataquality_topn1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality_topn1` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field_id` int(11) NOT NULL,
  `field_value` varchar(1000) DEFAULT NULL,
  `count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality_topn1`
--

LOCK TABLES `dataquality_topn1` WRITE;
/*!40000 ALTER TABLE `dataquality_topn1` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality_topn1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataquality_topn2`
--

DROP TABLE IF EXISTS `dataquality_topn2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataquality_topn2` (
  `fieldId` int(11) NOT NULL,
  `fieldValue` text,
  `count` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataquality_topn2`
--

LOCK TABLES `dataquality_topn2` WRITE;
/*!40000 ALTER TABLE `dataquality_topn2` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataquality_topn2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataset_owner`
--

DROP TABLE IF EXISTS `dataset_owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataset_owner` (
  `urn` varchar(255) COLLATE utf8_bin NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_platform` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_tel` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`urn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataset_owner`
--

LOCK TABLES `dataset_owner` WRITE;
/*!40000 ALTER TABLE `dataset_owner` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataset_owner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dataset_tag`
--

DROP TABLE IF EXISTS `dataset_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataset_tag` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `dataset_id` int(10) unsigned NOT NULL,
  `tag_id` int(5) NOT NULL,
  `createtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_dataset_tag_dict_dataset` (`dataset_id`),
  KEY `FK_dataset_tag_dict_tag` (`tag_id`),
  CONSTRAINT `FK_dataset_tag_dict_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `dict_dataset` (`id`),
  CONSTRAINT `FK_dataset_tag_dict_tag` FOREIGN KEY (`tag_id`) REFERENCES `dict_tag` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=418 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dataset_tag`
--

LOCK TABLES `dataset_tag` WRITE;
/*!40000 ALTER TABLE `dataset_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `dataset_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dict_dataset`
--

DROP TABLE IF EXISTS `dict_dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dict_dataset` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8 NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `schema` mediumtext CHARACTER SET utf8,
  `previous_schema` mediumtext CHARACTER SET latin1,
  `schema_type` varchar(50) CHARACTER SET latin1 DEFAULT 'JSON' COMMENT 'JSON, Hive, DDL, XML, CSV',
  `properties` text CHARACTER SET utf8,
  `fields` mediumtext CHARACTER SET utf8,
  `urn` varchar(200) COLLATE utf8_bin NOT NULL,
  `source` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT 'The original data source type (for dataset in data warehouse). Oracle, Kafka ...',
  `location_prefix` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `parent_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Schema Name for RDBMS, Group Name for Jobs/Projects/Tracking Datasets on HDFS ',
  `storage_type` enum('Table','View','Avro','ORC','RC','Sequence','Flat File','JSON','XML','Thrift','Parquet','Type','Index','Protobuff') CHARACTER SET latin1 DEFAULT NULL,
  `ref_dataset_id` int(11) unsigned DEFAULT NULL COMMENT 'Refer to Master/Main dataset for Views/ExternalTables',
  `status_id` smallint(6) unsigned DEFAULT NULL COMMENT 'Reserve for dataset status',
  `dataset_type` varchar(30) CHARACTER SET latin1 DEFAULT NULL COMMENT 'hdfs, hive, kafka, teradata, mysql, sqlserver, file, nfs, pinot, salesforce, oracle, db2, netezza, cassandra, hbase, qfs, zfs',
  `hive_serdes_class` varchar(300) CHARACTER SET latin1 DEFAULT NULL,
  `is_partitioned` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `partition_layout_pattern_id` smallint(6) DEFAULT NULL,
  `sample_partition_full_path` varchar(256) CHARACTER SET latin1 DEFAULT NULL COMMENT 'sample partition full path of the dataset',
  `source_created_time` int(10) unsigned DEFAULT NULL COMMENT 'source created time of the flow',
  `source_modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest source modified time of the flow',
  `created_time` int(10) unsigned DEFAULT NULL COMMENT 'wherehows created time',
  `modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest wherehows modified',
  `wh_etl_job_id` smallint(6) NOT NULL,
  `wh_etl_exec_id` bigint(20) DEFAULT NULL COMMENT 'wherehows etl execution id that modified this record',
  `std_table_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `urn` (`urn`),
  FULLTEXT KEY `fti_datasets_all` (`name`,`schema`,`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=11587 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dict_dataset`
--

LOCK TABLES `dict_dataset` WRITE;
/*!40000 ALTER TABLE `dict_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `dict_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dict_dataset_attr`
--

DROP TABLE IF EXISTS `dict_dataset_attr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dict_dataset_attr` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `attr_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `attr_value` varchar(500) COLLATE utf8_bin DEFAULT NULL,
  `dataset_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_dict_dataset_attr_dict_dataset` (`dataset_id`),
  CONSTRAINT `FK_dict_dataset_attr_dict_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `dict_dataset` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dict_dataset_attr`
--

LOCK TABLES `dict_dataset_attr` WRITE;
/*!40000 ALTER TABLE `dict_dataset_attr` DISABLE KEYS */;
/*!40000 ALTER TABLE `dict_dataset_attr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dict_field_detail`
--

DROP TABLE IF EXISTS `dict_field_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dict_field_detail` (
  `field_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `dataset_id` int(11) unsigned NOT NULL,
  `sort_id` smallint(6) unsigned NOT NULL,
  `parent_sort_id` smallint(5) unsigned NOT NULL,
  `parent_path` varchar(200) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `field_name` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT '',
  `field_label` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `data_type` varchar(50) CHARACTER SET utf8 NOT NULL,
  `data_size` int(10) unsigned DEFAULT NULL,
  `data_precision` tinyint(4) DEFAULT NULL COMMENT 'only in decimal type',
  `data_fraction` tinyint(4) DEFAULT NULL COMMENT 'only in decimal type',
  `is_nullable` char(1) CHARACTER SET utf8 DEFAULT NULL,
  `is_indexed` char(1) CHARACTER SET utf8 DEFAULT NULL COMMENT 'only in RDBMS',
  `is_partitioned` char(1) CHARACTER SET utf8 DEFAULT NULL COMMENT 'only in RDBMS',
  `is_autolabeled` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:默认;1:已经被自动打上注释;2:手动注释',
  `default_value` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `wh_etl_job_id` smallint(6) NOT NULL,
  PRIMARY KEY (`field_id`),
  UNIQUE KEY `uix_dict_field__datasetid_parentpath_fieldname` (`dataset_id`,`parent_path`,`field_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=185019 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Flattened Fields/Columns';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dict_field_detail`
--

LOCK TABLES `dict_field_detail` WRITE;
/*!40000 ALTER TABLE `dict_field_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `dict_field_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dict_tag`
--

DROP TABLE IF EXISTS `dict_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dict_tag` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_mod` datetime DEFAULT NULL,
  `tag_color` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fi_tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=10122 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dict_tag`
--

LOCK TABLES `dict_tag` WRITE;
/*!40000 ALTER TABLE `dict_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `dict_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `domain_entity`
--

DROP TABLE IF EXISTS `domain_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domain_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain_id` int(11) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `domain_id` (`domain_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `domain_entity`
--

LOCK TABLES `domain_entity` WRITE;
/*!40000 ALTER TABLE `domain_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `domain_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `roleId` varchar(36) NOT NULL,
  `roleName` varchar(20) DEFAULT NULL,
  `roleDesc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_privilege`
--

DROP TABLE IF EXISTS `role_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_privilege` (
  `roleId` varchar(36) NOT NULL,
  `privilegeName` varchar(36) NOT NULL,
  PRIMARY KEY (`roleId`,`privilegeName`),
  CONSTRAINT `FK_Role_Privilege_reference` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_privilege`
--

LOCK TABLES `role_privilege` WRITE;
/*!40000 ALTER TABLE `role_privilege` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sql_history`
--

DROP TABLE IF EXISTS `sql_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sql_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sql_name` varchar(100) COLLATE utf8_bin DEFAULT '',
  `sql` text COLLATE utf8_bin NOT NULL,
  `db` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `source_id` int(5) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `fi_sql` (`sql`)
) ENGINE=InnoDB AUTO_INCREMENT=3927 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sql_history`
--

LOCK TABLES `sql_history` WRITE;
/*!40000 ALTER TABLE `sql_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sql_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stage_log`
--

DROP TABLE IF EXISTS `stage_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stage_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `update_time` datetime NOT NULL,
  `type` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `dataset_id` int(11) DEFAULT NULL,
  `dataset_name` varchar(255) NOT NULL,
  `parent_name` varchar(255) NOT NULL,
  `dataset_alias` varchar(255) DEFAULT NULL,
  `schema` text CHARACTER SET utf8 COLLATE utf8_bin,
  `previous_schema` text CHARACTER SET utf8 COLLATE utf8_bin,
  `wh_etl_job_id` smallint(6) NOT NULL,
  `wh_etl_exec_id` int(11) NOT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5437 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stage_log`
--

LOCK TABLES `stage_log` WRITE;
/*!40000 ALTER TABLE `stage_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `stage_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `std_column`
--

DROP TABLE IF EXISTS `std_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `std_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16400 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `std_column`
--

LOCK TABLES `std_column` WRITE;
/*!40000 ALTER TABLE `std_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `std_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `std_identifier`
--

DROP TABLE IF EXISTS `std_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `std_identifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `internal_identifier` varchar(255) NOT NULL,
  `identifier` varchar(255) NOT NULL,
  `ch_name` varchar(255) DEFAULT NULL,
  `en_name` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `descripton` text,
  `status` varchar(255) DEFAULT NULL,
  `submit_institution` text,
  `approval_date` date DEFAULT NULL,
  `remark` text,
  `gat_codex` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=852 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `std_identifier`
--

LOCK TABLES `std_identifier` WRITE;
/*!40000 ALTER TABLE `std_identifier` DISABLE KEYS */;
/*!40000 ALTER TABLE `std_identifier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `std_table`
--

DROP TABLE IF EXISTS `std_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `std_table` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `datavolume` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1033 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `std_table`
--

LOCK TABLES `std_table` WRITE;
/*!40000 ALTER TABLE `std_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `std_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `std_table_tag`
--

DROP TABLE IF EXISTS `std_table_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `std_table_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=775 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `std_table_tag`
--

LOCK TABLES `std_table_tag` WRITE;
/*!40000 ALTER TABLE `std_table_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `std_table_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `std_tag`
--

DROP TABLE IF EXISTS `std_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `std_tag` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `total_expected` int(5) DEFAULT NULL,
  `total_matched` int(5) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fi_tag_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `std_tag`
--

LOCK TABLES `std_tag` WRITE;
/*!40000 ALTER TABLE `std_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `std_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stg_dict_dataset`
--

DROP TABLE IF EXISTS `stg_dict_dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stg_dict_dataset` (
  `name` varchar(200) COLLATE utf8_bin NOT NULL,
  `schema` mediumtext CHARACTER SET utf8,
  `schema_type` varchar(50) CHARACTER SET latin1 DEFAULT 'JSON' COMMENT 'JSON, Hive, DDL, XML, CSV',
  `properties` text CHARACTER SET utf8,
  `fields` mediumtext CHARACTER SET utf8,
  `wh_etl_job_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `urn` varchar(200) COLLATE utf8_bin NOT NULL,
  `source` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `location_prefix` varchar(200) CHARACTER SET latin1 DEFAULT NULL,
  `parent_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Schema Name for RDBMS, Group Name for Jobs/Projects/Tracking Datasets on HDFS',
  `storage_type` enum('Table','View','Avro','ORC','RC','Sequence','Flat File','JSON','XML','Thrift','Parquet','Protobuff') CHARACTER SET latin1 DEFAULT NULL,
  `ref_dataset_name` varchar(200) CHARACTER SET latin1 DEFAULT NULL,
  `ref_dataset_id` int(11) unsigned DEFAULT NULL COMMENT 'Refer to Master/Main dataset for Views/ExternalTables',
  `status_id` smallint(6) unsigned DEFAULT NULL COMMENT 'Reserve for dataset status',
  `dataset_type` varchar(30) CHARACTER SET latin1 DEFAULT NULL COMMENT 'hdfs, hive, kafka, teradata, mysql, sqlserver, file, nfs, pinot, salesforce, oracle, db2, netezza, cassandra, hbase, qfs, zfs',
  `hive_serdes_class` varchar(300) CHARACTER SET latin1 DEFAULT NULL,
  `is_partitioned` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `partition_layout_pattern_id` smallint(6) DEFAULT NULL,
  `sample_partition_full_path` varchar(256) CHARACTER SET latin1 DEFAULT NULL COMMENT 'sample partition full path of the dataset',
  `source_created_time` int(10) unsigned DEFAULT NULL COMMENT 'source created time of the flow',
  `source_modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest source modified time of the flow',
  `created_time` int(10) unsigned DEFAULT NULL COMMENT 'wherehows created time',
  `modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest wherehows modified',
  `wh_etl_exec_id` bigint(20) DEFAULT NULL COMMENT 'wherehows etl execution id that modified this record',
  PRIMARY KEY (`urn`,`wh_etl_job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/*!50100 PARTITION BY HASH (wh_etl_job_id)
PARTITIONS 8 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stg_dict_dataset`
--

LOCK TABLES `stg_dict_dataset` WRITE;
/*!40000 ALTER TABLE `stg_dict_dataset` DISABLE KEYS */;
/*!40000 ALTER TABLE `stg_dict_dataset` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stg_dict_field_detail`
--

DROP TABLE IF EXISTS `stg_dict_field_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stg_dict_field_detail` (
  `wh_etl_job_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `urn` varchar(200) COLLATE utf8_bin NOT NULL,
  `sort_id` smallint(5) unsigned NOT NULL,
  `parent_sort_id` smallint(5) unsigned NOT NULL,
  `parent_path` varchar(200) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `field_name` varchar(100) COLLATE utf8_bin NOT NULL,
  `field_label` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `data_type` varchar(50) CHARACTER SET latin1 NOT NULL,
  `data_size` int(10) unsigned DEFAULT NULL,
  `data_precision` tinyint(3) unsigned DEFAULT NULL,
  `data_scale` tinyint(3) unsigned DEFAULT NULL,
  `is_nullable` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `is_indexed` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `is_partitioned` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `is_distributed` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `default_value` varchar(200) CHARACTER SET latin1 DEFAULT NULL,
  `namespace` varchar(200) CHARACTER SET latin1 DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET latin1 DEFAULT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dataset_id` int(10) unsigned DEFAULT NULL COMMENT 'used to opitimize metadata ETL performance',
  PRIMARY KEY (`urn`,`sort_id`,`wh_etl_job_id`),
  KEY `idx_stg_dict_field_detail__description` (`description`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/*!50100 PARTITION BY HASH (wh_etl_job_id)
PARTITIONS 8 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stg_dict_field_detail`
--

LOCK TABLES `stg_dict_field_detail` WRITE;
/*!40000 ALTER TABLE `stg_dict_field_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `stg_dict_field_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `userId` varchar(36) NOT NULL,
  `roleId` varchar(36) NOT NULL,
  `tenantId` varchar(36) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`,`tenantId`),
  KEY `FK_user_role_reference` (`roleId`),
  CONSTRAINT `FK_user_role_reference` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wh_etl_job`
--

DROP TABLE IF EXISTS `wh_etl_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wh_etl_job` (
  `wh_etl_job_id` int(5) NOT NULL AUTO_INCREMENT COMMENT 'id of the etl job',
  `wh_etl_job_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'etl job name',
  `wh_etl_type` varchar(127) CHARACTER SET utf8 DEFAULT NULL COMMENT 'type of the etl service',
  `cron_expr` varchar(127) CHARACTER SET utf8 DEFAULT NULL COMMENT 'frequency in crob expression',
  `running` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'whether this etl job is runing  0:false,1:true',
  `next_run` int(10) unsigned DEFAULT NULL COMMENT 'next run time',
  `comments` text CHARACTER SET utf8,
  `cmd_param` varchar(500) CHARACTER SET utf8 DEFAULT '' COMMENT 'command line parameters for launch the job',
  `is_active` char(1) CHARACTER SET utf8 DEFAULT NULL COMMENT 'determine if this job is active or not',
  `data_source_name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`wh_etl_job_id`),
  UNIQUE KEY `data_source_name` (`data_source_name`)
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows ETL jobs table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wh_etl_job`
--

LOCK TABLES `wh_etl_job` WRITE;
/*!40000 ALTER TABLE `wh_etl_job` DISABLE KEYS */;
/*!40000 ALTER TABLE `wh_etl_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wh_etl_job_execution`
--

DROP TABLE IF EXISTS `wh_etl_job_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wh_etl_job_execution` (
  `wh_etl_exec_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'job execution id',
  `wh_etl_job_id` int(10) unsigned DEFAULT NULL COMMENT 'id of the etl job',
  `status` varchar(31) CHARACTER SET utf8 DEFAULT NULL COMMENT 'status of etl job execution',
  `request_time` int(10) unsigned DEFAULT NULL COMMENT 'request time of the execution',
  `start_time` int(10) unsigned DEFAULT NULL COMMENT 'start time of the execution',
  `end_time` int(10) unsigned DEFAULT NULL COMMENT 'end time of the execution',
  `message` varchar(1024) CHARACTER SET utf8 DEFAULT NULL COMMENT 'debug information message',
  `host_name` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT 'host machine name of the job execution',
  `process_id` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT 'job execution process id',
  PRIMARY KEY (`wh_etl_exec_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1593 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows ETL execution table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wh_etl_job_execution`
--

LOCK TABLES `wh_etl_job_execution` WRITE;
/*!40000 ALTER TABLE `wh_etl_job_execution` DISABLE KEYS */;
/*!40000 ALTER TABLE `wh_etl_job_execution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wh_etl_job_property`
--

DROP TABLE IF EXISTS `wh_etl_job_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
  CREATE TABLE `wh_etl_job_property` (
    `id` int(5) NOT NULL AUTO_INCREMENT,
    `wh_etl_job_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'etl job name',
    `property_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'property name',
    `property_value` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT 'property value',
    `is_encrypted` char(1) CHARACTER SET utf8 DEFAULT 'N' COMMENT 'whether the value is encrypted',
    `comments` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
    `wh_etl_job_id` int(10) DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=InnoDB AUTO_INCREMENT=643 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Etl job configuration table';
  /*!40101 SET character_set_client = @saved_cs_client */;

  --
  -- Dumping data for table `wh_etl_job_property`
  --

  LOCK TABLES `wh_etl_job_property` WRITE;
  /*!40000 ALTER TABLE `wh_etl_job_property` DISABLE KEYS */;
  /*!40000 ALTER TABLE `wh_etl_job_property` ENABLE KEYS */;
  UNLOCK TABLES;

  --
  -- Table structure for table `wh_property`
  --

  DROP TABLE IF EXISTS `wh_property`;
  /*!40101 SET @saved_cs_client     = @@character_set_client */;
  /*!40101 SET character_set_client = utf8 */;
  CREATE TABLE `wh_property` (
    `property_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'property name',
    `property_value` text CHARACTER SET utf8 COMMENT 'property value',
    `is_encrypted` char(1) CHARACTER SET utf8 DEFAULT 'N' COMMENT 'whether the value is encrypted',
    `group_name` varchar(127) CHARACTER SET utf8 DEFAULT NULL COMMENT 'group name for the property',
    PRIMARY KEY (`property_name`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows properties table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wh_property`
--

LOCK TABLES `wh_property` WRITE;
/*!40000 ALTER TABLE `wh_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `wh_property` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-06-12 14:00:32