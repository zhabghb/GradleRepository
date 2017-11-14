/*
Navicat MySQL Data Transfer

Source Server         : ubuntu16-work
Source Server Version : 50719
Source Host           : 10.111.134.76:3306
Source Database       : metagrid

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2017-10-20 10:24:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for all_col_dict
-- ----------------------------
DROP TABLE IF EXISTS `all_col_dict`;
CREATE TABLE `all_col_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `tab` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of all_col_dict
-- ----------------------------

-- ----------------------------
-- Table structure for all_col_dict_copy
-- ----------------------------
DROP TABLE IF EXISTS `all_col_dict_copy`;
CREATE TABLE `all_col_dict_copy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `tab` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of all_col_dict_copy
-- ----------------------------

-- ----------------------------
-- Table structure for all_tab_dict
-- ----------------------------
DROP TABLE IF EXISTS `all_tab_dict`;
CREATE TABLE `all_tab_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `tag` varchar(50) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `datavolume` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of all_tab_dict
-- ----------------------------

-- ----------------------------
-- Table structure for all_tag_dict
-- ----------------------------
DROP TABLE IF EXISTS `all_tag_dict`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of all_tag_dict
-- ----------------------------

-- ----------------------------
-- Table structure for data_domain
-- ----------------------------
DROP TABLE IF EXISTS `data_domain`;
CREATE TABLE `data_domain` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL COMMENT '鍘熷?鏁版嵁origin銆佷富棰樻暟鎹甦omain銆佹湇鍔℃暟鎹畇ervice',
  `code` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT 'UNPUSHED',
  `icon` varchar(255) DEFAULT NULL,
  `description` text,
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_name_type` (`name`,`type`),
  FULLTEXT KEY `description` (`description`),
  FULLTEXT KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of data_domain
-- ----------------------------

-- ----------------------------
-- Table structure for data_domain_attr
-- ----------------------------
DROP TABLE IF EXISTS `data_domain_attr`;
CREATE TABLE `data_domain_attr` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `domain_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_key_domain` (`key`,`domain_id`),
  KEY `domain_id` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of data_domain_attr
-- ----------------------------

-- ----------------------------
-- Table structure for data_source_owner
-- ----------------------------
DROP TABLE IF EXISTS `data_source_owner`;
CREATE TABLE `data_source_owner` (
  `wh_etl_job_id` int(255) NOT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_platform` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_tel` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`wh_etl_job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of data_source_owner
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel
-- ----------------------------
DROP TABLE IF EXISTS `datamodel`;
CREATE TABLE `datamodel` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `owner` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `tag` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel_field
-- ----------------------------
DROP TABLE IF EXISTS `datamodel_field`;
CREATE TABLE `datamodel_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `field_alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel_field
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel_json
-- ----------------------------
DROP TABLE IF EXISTS `datamodel_json`;
CREATE TABLE `datamodel_json` (
  `id` int(11) NOT NULL,
  `json` text COLLATE utf8_bin,
  `name` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel_json
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel_relation
-- ----------------------------
DROP TABLE IF EXISTS `datamodel_relation`;
CREATE TABLE `datamodel_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) NOT NULL,
  `source_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `relation_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel_relation
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel_relation_type
-- ----------------------------
DROP TABLE IF EXISTS `datamodel_relation_type`;
CREATE TABLE `datamodel_relation_type` (
  `id` int(5) NOT NULL,
  `en_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `ch_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `icon` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel_relation_type
-- ----------------------------

-- ----------------------------
-- Table structure for datamodel_table
-- ----------------------------
DROP TABLE IF EXISTS `datamodel_table`;
CREATE TABLE `datamodel_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(8) NOT NULL,
  `table_id` int(11) NOT NULL,
  `table_alias` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `canvas_position` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_modelview_table_modelview` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of datamodel_table
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality
-- ----------------------------
DROP TABLE IF EXISTS `dataquality`;
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

-- ----------------------------
-- Records of dataquality
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality_field_rule
-- ----------------------------
DROP TABLE IF EXISTS `dataquality_field_rule`;
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

-- ----------------------------
-- Records of dataquality_field_rule
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality_rule
-- ----------------------------
DROP TABLE IF EXISTS `dataquality_rule`;
CREATE TABLE `dataquality_rule` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `rule_key` varchar(255) DEFAULT NULL,
  `rule_name` varchar(255) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dataquality_rule
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality_topn
-- ----------------------------
DROP TABLE IF EXISTS `dataquality_topn`;
CREATE TABLE `dataquality_topn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field_id` int(11) NOT NULL,
  `field_value` varchar(1000) DEFAULT NULL,
  `count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of dataquality_topn
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality_topn1
-- ----------------------------
DROP TABLE IF EXISTS `dataquality_topn1`;
CREATE TABLE `dataquality_topn1` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `field_id` int(11) NOT NULL,
  `field_value` varchar(1000) DEFAULT NULL,
  `count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of dataquality_topn1
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality_topn2
-- ----------------------------
DROP TABLE IF EXISTS `dataquality_topn2`;
CREATE TABLE `dataquality_topn2` (
  `fieldId` int(11) NOT NULL,
  `fieldValue` text,
  `count` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dataquality_topn2
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality1
-- ----------------------------
DROP TABLE IF EXISTS `dataquality1`;
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

-- ----------------------------
-- Records of dataquality1
-- ----------------------------

-- ----------------------------
-- Table structure for dataquality2
-- ----------------------------
DROP TABLE IF EXISTS `dataquality2`;
CREATE TABLE `dataquality2` (
  `fieldId` int(11) NOT NULL,
  `empties` bigint(20) NOT NULL,
  `validCount` bigint(20) NOT NULL,
  `invalidCount` bigint(20) NOT NULL,
  `nulls` bigint(20) NOT NULL,
  `totalCount` bigint(20) NOT NULL,
  `uniqueValues` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of dataquality2
-- ----------------------------

-- ----------------------------
-- Table structure for dataset_owner
-- ----------------------------
DROP TABLE IF EXISTS `dataset_owner`;
CREATE TABLE `dataset_owner` (
  `urn` varchar(255) COLLATE utf8_bin NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_platform` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_department` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `owner_tel` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`urn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of dataset_owner
-- ----------------------------

-- ----------------------------
-- Table structure for dataset_tag
-- ----------------------------
DROP TABLE IF EXISTS `dataset_tag`;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of dataset_tag
-- ----------------------------

-- ----------------------------
-- Table structure for dict_dataset
-- ----------------------------
DROP TABLE IF EXISTS `dict_dataset`;
CREATE TABLE `dict_dataset` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8 NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `schema` mediumtext CHARACTER SET utf8,
  `previous_schema` mediumtext CHARACTER SET utf8,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of dict_dataset
-- ----------------------------

-- ----------------------------
-- Table structure for dict_field_detail
-- ----------------------------
DROP TABLE IF EXISTS `dict_field_detail`;
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
  `is_autolabeled` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:榛樿?;1:宸茬粡琚?嚜鍔ㄦ墦涓婃敞閲?2:鎵嬪姩娉ㄩ噴',
  `default_value` varchar(200) CHARACTER SET utf8 DEFAULT NULL,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `wh_etl_job_id` smallint(6) NOT NULL,
  PRIMARY KEY (`field_id`),
  UNIQUE KEY `uix_dict_field__datasetid_parentpath_fieldname` (`dataset_id`,`parent_path`,`field_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Flattened Fields/Columns';

-- ----------------------------
-- Records of dict_field_detail
-- ----------------------------

-- ----------------------------
-- Table structure for dict_tag
-- ----------------------------
DROP TABLE IF EXISTS `dict_tag`;
CREATE TABLE `dict_tag` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_mod` datetime DEFAULT NULL,
  `tag_color` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fi_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of dict_tag
-- ----------------------------

-- ----------------------------
-- Table structure for domain_entity
-- ----------------------------
DROP TABLE IF EXISTS `domain_entity`;
CREATE TABLE `domain_entity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain_id` int(11) DEFAULT NULL,
  `entity_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_domain_entity` (`domain_id`,`entity_id`),
  KEY `domain_id` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of domain_entity
-- ----------------------------

-- ----------------------------
-- Table structure for domain_identifier
-- ----------------------------
DROP TABLE IF EXISTS `domain_identifier`;
CREATE TABLE `domain_identifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `domain_id` int(11) DEFAULT NULL,
  `identifier_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_domain_identifier` (`domain_id`,`identifier_id`),
  KEY `domain_id` (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of domain_identifier
-- ----------------------------

-- ----------------------------
-- Table structure for domain_service
-- ----------------------------
DROP TABLE IF EXISTS `domain_service`;
CREATE TABLE `domain_service` (
  `domain_id` int(5) NOT NULL,
  `domain_img` text,
  `wh_service_id` varchar(36) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `last_publish_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`domain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of domain_service
-- ----------------------------

-- ----------------------------
-- Table structure for hdfs_file_attr
-- ----------------------------
DROP TABLE IF EXISTS `hdfs_file_attr`;
CREATE TABLE `hdfs_file_attr` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sourceId` int(5) NOT NULL,
  `keyword` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `path` varchar(255) COLLATE utf8_bin NOT NULL,
  `filename` varchar(255) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of hdfs_file_attr
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `roleId` varchar(36) NOT NULL,
  `roleName` varchar(20) DEFAULT NULL,
  `roleDesc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'dataAdmin', '');

-- ----------------------------
-- Table structure for role_privilege
-- ----------------------------
DROP TABLE IF EXISTS `role_privilege`;
CREATE TABLE `role_privilege` (
  `roleId` varchar(36) NOT NULL,
  `privilegeName` varchar(36) NOT NULL,
  PRIMARY KEY (`roleId`,`privilegeName`),
  CONSTRAINT `FK_Role_Privilege_reference` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_privilege
-- ----------------------------
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'dataDomain');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'dataMap');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'dataStandard');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'freshDataSource');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'hdfs');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'manageDataSource');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'viewDataSource');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'viewSchemaHistory');
INSERT INTO `role_privilege` VALUES ('679b5a19-ba52-4976-9e82-6b009ebb2958', 'viewTree');

-- ----------------------------
-- Table structure for sql_history
-- ----------------------------
DROP TABLE IF EXISTS `sql_history`;
CREATE TABLE `sql_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sql_name` varchar(100) COLLATE utf8_bin DEFAULT '',
  `sql` text COLLATE utf8_bin NOT NULL,
  `db` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `source_id` int(5) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `fi_sql` (`sql`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of sql_history
-- ----------------------------

-- ----------------------------
-- Table structure for stage_log
-- ----------------------------
DROP TABLE IF EXISTS `stage_log`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of stage_log
-- ----------------------------

-- ----------------------------
-- Table structure for std_column
-- ----------------------------
DROP TABLE IF EXISTS `std_column`;
CREATE TABLE `std_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `data_type` varchar(100) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of std_column
-- ----------------------------

-- ----------------------------
-- Table structure for std_identifier
-- ----------------------------
DROP TABLE IF EXISTS `std_identifier`;
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `identifier` (`identifier`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of std_identifier
-- ----------------------------

-- ----------------------------
-- Table structure for std_table
-- ----------------------------
DROP TABLE IF EXISTS `std_table`;
CREATE TABLE `std_table` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `datavolume` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of std_table
-- ----------------------------

-- ----------------------------
-- Table structure for std_table_tag
-- ----------------------------
DROP TABLE IF EXISTS `std_table_tag`;
CREATE TABLE `std_table_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tab_id` int(11) NOT NULL,
  `tag_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of std_table_tag
-- ----------------------------

-- ----------------------------
-- Table structure for std_tag
-- ----------------------------
DROP TABLE IF EXISTS `std_tag`;
CREATE TABLE `std_tag` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `total_expected` int(5) DEFAULT NULL,
  `total_matched` int(5) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fi_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of std_tag
-- ----------------------------

-- ----------------------------
-- Table structure for stg_dict_dataset
-- ----------------------------
DROP TABLE IF EXISTS `stg_dict_dataset`;
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

-- ----------------------------
-- Records of stg_dict_dataset
-- ----------------------------

-- ----------------------------
-- Table structure for stg_dict_field_detail
-- ----------------------------
DROP TABLE IF EXISTS `stg_dict_field_detail`;
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

-- ----------------------------
-- Records of stg_dict_field_detail
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `userId` varchar(36) NOT NULL,
  `roleId` varchar(36) NOT NULL,
  `tenantId` varchar(36) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`,`tenantId`),
  KEY `FK_user_role_reference` (`roleId`),
  CONSTRAINT `FK_user_role_reference` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------

-- ----------------------------
-- Table structure for wh_etl_job
-- ----------------------------
DROP TABLE IF EXISTS `wh_etl_job`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows ETL jobs table';

-- ----------------------------
-- Records of wh_etl_job
-- ----------------------------

-- ----------------------------
-- Table structure for wh_etl_job_execution
-- ----------------------------
DROP TABLE IF EXISTS `wh_etl_job_execution`;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows ETL execution table';

-- ----------------------------
-- Records of wh_etl_job_execution
-- ----------------------------

-- ----------------------------
-- Table structure for wh_etl_job_property
-- ----------------------------
DROP TABLE IF EXISTS `wh_etl_job_property`;
CREATE TABLE `wh_etl_job_property` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `wh_etl_job_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'etl job name',
  `property_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'property name',
  `property_value` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT 'property value',
  `is_encrypted` char(1) CHARACTER SET utf8 DEFAULT 'N' COMMENT 'whether the value is encrypted',
  `comments` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `wh_etl_job_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Etl job configuration table';

-- ----------------------------
-- Records of wh_etl_job_property
-- ----------------------------

-- ----------------------------
-- Table structure for wh_property
-- ----------------------------
DROP TABLE IF EXISTS `wh_property`;
CREATE TABLE `wh_property` (
  `property_name` varchar(127) CHARACTER SET utf8 NOT NULL COMMENT 'property name',
  `property_value` text CHARACTER SET utf8 COMMENT 'property value',
  `is_encrypted` char(1) CHARACTER SET utf8 DEFAULT 'N' COMMENT 'whether the value is encrypted',
  `group_name` varchar(127) CHARACTER SET utf8 DEFAULT NULL COMMENT 'group name for the property',
  PRIMARY KEY (`property_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='WhereHows properties table';

-- ----------------------------
-- Records of wh_property
-- ----------------------------
INSERT INTO `wh_property` VALUES ('wherehows.app_folder', '/tmp/metagrid', 'N', '');
INSERT INTO `wh_property` VALUES ('wherehows.db.driver', 'com.mysql.jdbc.Driver', 'N', '');
INSERT INTO `wh_property` VALUES ('wherehows.db.jdbc.url', 'jdbc:mysql://172.16.50.21/metagridtest', 'N', '');
INSERT INTO `wh_property` VALUES ('wherehows.db.password', 'metagrid', 'N', '');
INSERT INTO `wh_property` VALUES ('wherehows.db.username', 'metagrid', 'N', '');
INSERT INTO `wh_property` VALUES ('wherehows.encrypt.master.key.loc', '/home/cloudera/.wherehows/master_key', 'N', '');
