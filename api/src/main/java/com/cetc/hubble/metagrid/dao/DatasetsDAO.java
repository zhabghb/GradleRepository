/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.mapper.DatasetColumnRowMapper;
import com.cetc.hubble.metagrid.mapper.DatasetCommentRowMapper;
import com.cetc.hubble.metagrid.mapper.DatasetRowMapper;
import com.cetc.hubble.metagrid.mapper.DatasetWithUserRowMapper;
import com.cetc.hubble.metagrid.vo.Dataset;
import com.cetc.hubble.metagrid.vo.DatasetColumn;
import com.cetc.hubble.metagrid.vo.DatasetColumnComment;
import com.cetc.hubble.metagrid.vo.DatasetComment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import metagrid.common.utils.Json;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DatasetsDAO extends AbstractMySQLOpenSourceDAO
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public final static String SEARCH_DATASET_WITH_PAGINATION_B = "SELECT DISTINCT " +
			"id, `name`, `schema`, `source`, `urn`, `alias`,`wh_etl_job_id`,`parent_name` " +
			"FROM (SELECT dd.id, dd.`name`, dd.`schema`, dd.`source`, dd.`urn`, dd.`alias`,dd.`wh_etl_job_id`,dd.`parent_name`, " +
			"CASE WHEN dd.`name` = '$keyword' THEN 3000 ELSE 0 END rank_01, " +
			"CASE WHEN dd.`name` like '$keyword%' THEN 2000 ELSE 0 END rank_02, " +
			"CASE WHEN dd.`name` like '%$keyword%' THEN 1000 ELSE 0 END rank_03, " +
			"CASE WHEN dd.`alias` = '$keyword' THEN 500 ELSE 0 END rank_04, " +
			"CASE WHEN dd.`alias` like '$keyword%' THEN 300 ELSE 0 END rank_05, " +
			"CASE WHEN dd.`alias` like '%$keyword%' THEN 200 ELSE 0 END rank_06," +
			"CASE WHEN df.`field_name` = '$keyword' THEN 300 ELSE 0 END rank_07," +
			"CASE WHEN df.`field_name` like '$keyword%' THEN 200 ELSE 0 END rank_08," +
			"CASE WHEN df.`field_name` like '%$keyword%' THEN 100 ELSE 0 END rank_09, " +
			"CASE WHEN df.`field_label` = '$keyword' THEN 200 ELSE 0 END rank_10," +
			"CASE WHEN df.`field_label` like '%$keyword%' THEN 100 ELSE 0 END rank_11 " +
			" FROM dict_dataset dd LEFT JOIN dict_field_detail df ON dd.id = df.dataset_id  ) t  WHERE rank_01 + rank_02 + rank_03 + rank_04 + rank_05 + rank_06 + rank_07+ rank_08 + rank_09 + rank_10 + rank_11  > 0  " +
			"ORDER BY  rank_01 + rank_02 + rank_03 + rank_04 + rank_05 + rank_06 + rank_07 + rank_08 + rank_09 + rank_10 + rank_11 DESC, `name`, `urn` LIMIT ?, ?;";
	public final static String SEARCH_DATASET_WITH_PAGINATION = "SELECT DISTINCT id, `name`, `schema`, `source`, `urn`, `alias`,`wh_etl_job_id`,`parent_name` FROM dict_dataset WHERE `name` LIKE '%$keyword%'  OR alias like '%$keyword%' LIMIT ?, ?;";

	public final static String SEARCH_DATASET_BY_SOURCE_WITH_PAGINATION = "SELECT SQL_CALC_FOUND_ROWS " +
			"id, `name`, `schema`, `source`, `urn`, `alias`,`wh_etl_job_id`,`parent_name`, " +
			"rank_01 + rank_02 + rank_03 + rank_04 + rank_05 + rank_06 + rank_07 as rank " +
			"FROM (SELECT id, `name`, `schema`, `source`, `urn`, `alias`,`wh_etl_job_id`,`parent_name`, " +
			"CASE WHEN `name` = '$keyword' THEN 3000 ELSE 0 END rank_01, " +
			"CASE WHEN `name` like '$keyword%' THEN 2000 ELSE 0 END rank_02, " +
			"CASE WHEN `name` like '%$keyword%' THEN 1000 ELSE 0 END rank_03, " +
			"CASE WHEN `alias` = '$keyword' THEN 500 ELSE 0 END rank_04, " +
			"CASE WHEN `alias` like '$keyword%' THEN 300 ELSE 0 END rank_05, " +
			"CASE WHEN `alias` like '%$keyword%' THEN 200 ELSE 0 END rank_06, " +
			"CASE WHEN replace(REPLACE (`schema`, '\\\"name\\\":', ''),'\\\"dataType\\\":','') like '%$keyword%' THEN 100 ELSE 0 END rank_07 " +
			"FROM dict_dataset ) t  WHERE rank_01 + rank_02 + rank_03 + rank_04 + rank_05 + rank_06 + rank_07> 0  AND source in ($sources) " +
			"ORDER BY rank desc, `name`, `urn` LIMIT ?, ?;";


	public final static String SEARCH_DATASET_BY_TAG_NAME = "SELECT d.wh_etl_job_id,dt.dataset_id , d.`name`,d.alias,d.urn,d.source,d.parent_name,t.tag_name FROM dataset_tag dt " +
					"LEFT JOIN dict_tag t ON dt.tag_id = t.id LEFT JOIN dict_dataset d on d.id = dt.dataset_id WHERE t.tag_name LIKE ? limit ?,? ";


	private final String SELECT_PAGED_DATASET  = "SELECT " +
			"d.id, d.name, d.urn, d.source, d.properties, d.schema, " +
			"GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"FROM_UNIXTIME(source_created_time) as created, d.source_modified_time, " +
			"FROM_UNIXTIME(source_modified_time) as modified " +
			"FROM ( SELECT * FROM dict_dataset ORDER BY urn LIMIT ?, ? ) d " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id and (o.is_deleted is null OR o.is_deleted != 'Y')) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"GROUP BY d.id, d.name, d.urn, d.source, d.properties, d.schema, " +
			"created, d.source_modified_time, modified";

	private final String SELECT_PAGED_DATASET_BY_CURRENT_USER  = "SELECT " +
			"d.id, d.name, d.urn, d.source, d.schema, d.properties, " +
			"f.dataset_id, w.id as watch_id, " +
			"GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"FROM_UNIXTIME(source_created_time) as created, d.source_modified_time, " +
			"FROM_UNIXTIME(source_modified_time) as modified " +
			"FROM ( SELECT * FROM dict_dataset ORDER BY urn LIMIT ?, ?) d LEFT JOIN favorites f ON (" +
			"d.id = f.dataset_id and f.user_id = ?) " +
			"LEFT JOIN watch w on (d.id = w.item_id and w.item_type = 'dataset' and w.user_id = ?) " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id and (o.is_deleted is null OR o.is_deleted != 'Y')) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"GROUP BY d.id, d.name, d.urn, d.source, d.schema, d.properties, f.dataset_id, " +
			"watch_id, created, d.source_modified_time, modified";

	private final String GET_PAGED_DATASET_COUNT  = "SELECT count(*) FROM dict_dataset";

	private final String SELECT_PAGED_DATASET_BY_URN  = "SELECT " +
			"d.id, d.name, d.urn, d.source, d.properties, d.schema, " +
			"GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"FROM_UNIXTIME(source_created_time) as created, d.source_modified_time, " +
			"FROM_UNIXTIME(source_modified_time) as modified " +
			"FROM ( SELECT * FROM dict_dataset WHERE urn LIKE ? ORDER BY urn limit ?, ? ) d " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id and (o.is_deleted is null OR o.is_deleted != 'Y')) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"GROUP BY d.id, d.name, d.urn, d.source, d.properties, d.schema, created, " +
			"d.source_modified_time, modified";

	private final String SELECT_PAGED_DATASET_BY_URN_CURRENT_USER  = "SELECT " +
			"d.id, d.name, d.urn, d.source, d.schema, " +
			"GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"d.properties, f.dataset_id, w.id as watch_id, " +
			"FROM_UNIXTIME(source_created_time) as created, d.source_modified_time, " +
			"FROM_UNIXTIME(source_modified_time) as modified " +
			"FROM ( SELECT * FROM dict_dataset WHERE urn LIKE ?  ORDER BY urn LIMIT ?, ? ) d " +
			"LEFT JOIN favorites f ON (" +
			"d.id = f.dataset_id and f.user_id = ?) " +
			"LEFT JOIN watch w ON (d.id = w.item_id and w.item_type = 'dataset' and w.user_id = ?) " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id and (o.is_deleted is null OR o.is_deleted != 'Y')) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"GROUP BY d.id, d.name, d.urn, d.source, d.schema, d.properties, f.dataset_id, " +
			"watch_id, created, d.source_modified_time, modified";

	private final String GET_PAGED_DATASET_COUNT_BY_URN  = "SELECT count(*) FROM dict_dataset WHERE urn LIKE ?";

	private final String CHECK_SCHEMA_HISTORY  = "SELECT COUNT(*) FROM dict_dataset_schema_history " +
			"WHERE dataset_id = ? ";

	private final String GET_DATASET_BY_ID = "SELECT d.id, max(s.id) as schema_history_id, d.name, " +
			"d.urn, d.source, d.schema, GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"FROM_UNIXTIME(source_created_time) as created, d.source_modified_time, " +
			"FROM_UNIXTIME(source_modified_time) as modified " +
			"FROM dict_dataset d LEFT JOIN dict_dataset_schema_history s on (d.id = s.dataset_id) " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id) " +
			"WHERE d.id = ? GROUP BY d.id, d.name, d.urn, d.source, d.schema, " +
			"created, d.source_modified_time, modified";

	private final String GET_DATASET_BY_ID_CURRENT_USER  = "SELECT DISTINCT d.id, " +
			"max(s.id) as schema_history_id, " +
			"d.name, d.urn, d.source, d.schema, " +
			"GROUP_CONCAT(o.owner_id ORDER BY o.sort_id ASC SEPARATOR ',') as owner_id, " +
			"GROUP_CONCAT(IFNULL(u.display_name, '*') ORDER BY o.sort_id ASC SEPARATOR ',') as owner_name, " +
			"FROM_UNIXTIME(d.source_created_time) as created, " +
			"d.source_modified_time, " +
			"FROM_UNIXTIME(d.source_modified_time) as modified, f.dataset_id, w.id as watch_id FROM dict_dataset d " +
			"LEFT JOIN favorites f ON (d.id = f.dataset_id and f.user_id = ?) " +
			"LEFT JOIN dict_dataset_schema_history s on (d.id = s.dataset_id) " +
			"LEFT JOIN watch w ON (w.item_id = d.id and w.item_type = 'dataset' and w.user_id = ?) " +
			"LEFT JOIN dataset_owner o on (d.id = o.dataset_id) " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id) " +
			"WHERE d.id = ? GROUP BY d.id, d.name, d.urn, d.source, d.schema, created, " +
			"d.source_modified_time, modified, f.dataset_id, watch_id";

	private final String GET_DATASET_COLUMNS_BY_DATASET_ID = "select dfd.field_id, dfd.sort_id, " +
			"dfd.parent_sort_id, dfd.parent_path, dfd.field_name, dfd.data_type, " +
			"dfd.is_nullable as nullable, dfd.is_indexed as indexed, dfd.is_partitioned as partitioned, " +
			"dfd.is_distributed as distributed, c.comment, " +
			"( SELECT count(*) FROM dict_dataset_field_comment ddfc " +
			"WHERE ddfc.dataset_id = dfd.dataset_id AND ddfc.field_id = dfd.field_id ) as comment_count " +
			"FROM dict_field_detail dfd LEFT JOIN dict_dataset_field_comment ddfc ON " +
			"(ddfc.field_id = dfd.field_id AND ddfc.is_default = true) LEFT JOIN field_comments c ON " +
			"c.id = ddfc.comment_id WHERE dfd.dataset_id = ? ORDER BY dfd.sort_id";

	private final String GET_DATASET_COLUMNS_BY_DATASETID_AND_COLUMNID = "SELECT dfd.field_id, " +
			"dfd.sort_id, dfd.parent_sort_id, dfd.parent_path, dfd.field_name, dfd.data_type, " +
			"dfd.is_nullable as nullable, dfd.is_indexed as indexed, dfd.is_partitioned as partitioned, " +
			"dfd.is_distributed as distributed, c.text as comment, " +
			"( SELECT count(*) FROM dict_dataset_field_comment ddfc " +
			"WHERE ddfc.dataset_id = dfd.dataset_id AND ddfc.field_id = dfd.field_id ) as comment_count " +
			"FROM dict_field_detail dfd LEFT JOIN dict_dataset_field_comment ddfc ON " +
			"(ddfc.field_id = dfd.field_id AND ddfc.is_default = true) LEFT JOIN comments c ON " +
			"c.id = ddfc.comment_id WHERE dfd.dataset_id = ? AND dfd.field_id = ? ORDER BY dfd.sort_id";

	private final String GET_DATASET_OWNERS_BY_ID = "SELECT o.owner_id, u.display_name, o.sort_id, " +
			"o.owner_type, o.namespace, o.owner_id_type, o.owner_source, o.owner_sub_type, o.confirmed_by " +
			"FROM dataset_owner o " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"WHERE o.dataset_id = ? and (o.is_deleted is null OR o.is_deleted != 'Y') ORDER BY o.sort_id";

	private final String GET_DATASET_PROPERTIES_BY_DATASET_ID =
			"SELECT source, `properties` FROM dict_dataset WHERE id=?";

	private final String GET_DATASET_SAMPLE_DATA_BY_ID =
			"SELECT dataset_id, urn, ref_id, data FROM dict_dataset_sample WHERE dataset_id=?";

	private final String GET_DATASET_SAMPLE_DATA_BY_REFID =
			"SELECT data FROM dict_dataset_sample WHERE dataset_id=?";

	private final String GET_DATASET_URN_BY_ID =
			"SELECT urn FROM dict_dataset WHERE id=?";

	private final String GET_USER_ID = "select id FROM users WHERE username = ?";

	private final String FAVORITE_A_DATASET =
			"INSERT INTO favorites (user_id, dataset_id, created) VALUES(?, ?, NOW())";

	private final String UNFAVORITE_A_DATASET =
			"DELETE FROM favorites WHERE user_id = ? and dataset_id = ?";

	private final String GET_DATASET_OWNERS = "SELECT o.owner_id, o.namespace, " +
			"o.owner_type, o.owner_sub_type, " +
			"o.dataset_urn, u.display_name FROM dataset_owner o " +
			"LEFT JOIN dir_external_user_info u on (o.owner_id = u.user_id and u.app_id = 300) " +
			"WHERE dataset_id = ? and (o.is_deleted is null OR o.is_deleted != 'Y') ORDER BY sort_id";

	private final String UPDATE_DATASET_OWNER_SORT_ID = "UPDATE dataset_owner " +
			"set sort_id = ? WHERE dataset_id = ? AND owner_id = ? AND namespace = ?";

	private final String OWN_A_DATASET = "INSERT INTO dataset_owner (" +
			"dataset_id, owner_id, app_id, namespace, " +
			"owner_type, is_group, is_active, sort_id, created_time, modified_time, wh_etl_exec_id, dataset_urn) " +
			"VALUES(?, ?, 300, 'urn:li:corpuser', 'Producer', 'N', 'Y', 0, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), 0, ?)";

	private final String UNOWN_A_DATASET = "UPDATE dataset_owner " +
			"set is_deleted = 'Y' WHERE dataset_id = ? AND owner_id = ?  AND app_id = 300";

	private final String UPDATE_DATASET_OWNERS = "INSERT INTO dataset_owner (dataset_id, owner_id, app_id, " +
			"namespace, owner_type, is_group, is_active, is_deleted, sort_id, created_time, " +
			"modified_time, wh_etl_exec_id, dataset_urn, owner_sub_type) " +
			"VALUES(?, ?, ?, ?, ?, ?, 'Y', 'N', ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), 0, ?, ?) " +
			"ON DUPLICATE KEY UPDATE owner_type = ?, is_group = ?, is_deleted = 'N', " +
			"sort_id = ?, modified_time= UNIX_TIMESTAMP(), owner_sub_type=?";

	private final String UPDATE_DATASET_CONFIRMED_OWNERS = "INSERT INTO dataset_owner " +
			"(dataset_id, owner_id, app_id, namespace, owner_type, is_group, is_active, " +
			"is_deleted, sort_id, created_time, modified_time, wh_etl_exec_id, dataset_urn, owner_sub_type, " +
			"confirmed_by, confirmed_on) " +
			"VALUES(?, ?, ?, ?, ?, ?, 'Y', 'N', ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), 0, ?, ?, ?, ?) " +
			"ON DUPLICATE KEY UPDATE owner_type = ?, is_group = ?, is_deleted = 'N', " +
			"sort_id = ?, modified_time= UNIX_TIMESTAMP(), owner_sub_type=?, confirmed_by=?, confirmed_on=?";

	private final String MARK_DATASET_OWNERS_AS_DELETED = "UPDATE dataset_owner " +
			"set is_deleted = 'Y' WHERE dataset_id = ?";

	private final String GET_FAVORITES = "SELECT DISTINCT d.id, d.name, d.urn, d.source " +
			"FROM dict_dataset d JOIN favorites f ON d.id = f.dataset_id " +
			"JOIN users u ON f.dataset_id = d.id and f.user_id = u.id WHERE u.username = ? ORDER BY d.urn";

	private final String GET_COMMENTS_BY_DATASET_ID = "SELECT SQL_CALC_FOUND_ROWS " +
			"c.id, c.dataset_id, c.text, c.created, c.modified, c.comment_type, " +
			"u.name, u.email, u.username FROM comments c JOIN users u ON c.user_id = u.id " +
			"WHERE c.dataset_id = ? ORDER BY modified DESC, id DESC LIMIT ?, ?";

	private final String CREATE_DATASET_COMMENT = "INSERT INTO comments " +
			"(text, user_id, dataset_id, created, modified, comment_type) VALUES(?, ?, ?, NOW(), NOW(), ?)";

	private final String GET_WATCHED_URN_ID = "SELECT id FROM watch " +
			"WHERE user_id = ? and item_type = 'urn' and urn = '$URN'";

	private final String GET_WATCHED_DATASET_ID = "SELECT id FROM watch " +
			"WHERE user_id = ? and item_id = ? and item_type = 'dataset'";

	private final String WATCH_DATASET = "INSERT INTO watch " +
			"(user_id, item_id, urn, item_type, notification_type, created) VALUES(?, ?, NULL, 'dataset', ?, NOW())";

	private final String UPDATE_DATASET_WATCH = "UPDATE watch " +
			"set user_id = ?, item_id = ?, notification_type = ? WHERE id = ?";

	private final String WATCH_URN = "INSERT INTO watch " +
			"(user_id, item_id, urn, item_type, notification_type, created) VALUES(?, NULL, ?, 'urn', ?, NOW())";

	private final String UPDATE_URN_WATCH = "update watch " +
			"set user_id = ?, urn = ?, notification_type = ? WHERE id = ?";

	private final String CHECK_IF_COLUMN_COMMENT_EXIST = "SELECT id FROM field_comments " +
			"WHERE comment_crc32_checksum = CRC32(?) and comment = ?";

	private final String CREATE_COLUMN_COMMENT = "INSERT INTO field_comments " +
			"(comment, user_id, created, modified, comment_crc32_checksum) VALUES(?, ?, NOW(), NOW(), CRC32(?))";

	private final String UPDATE_DATASET_COMMENT = "UPDATE comments " +
			"SET text = ?, comment_type = ?, modified = NOW() WHERE id = ?";

	private final String UPDATE_COLUMN_COMMENT = "UPDATE field_comments " +
			"SET comment = ?, modified = NOW() WHERE id = ?";

	private final String DELETE_DATASET_COMMENT = "DELETE FROM comments WHERE id = ?";

	private final String UNWATCH_DATASET = "DELETE FROM watch WHERE id = ?";

	private final String GET_FIELD_COMMENT_BY_ID = "SELECT comment FROM dict_dataset_field_comment WHERE id = ?";

	private final String GET_COLUMN_COMMENTS_BY_DATASETID_AND_COLUMNID = "SELECT SQL_CALC_FOUND_ROWS " +
			"c.id, u.name as author, " +
			"u.email as authorEmail, u.username as authorUsername, c.comment as `text`, " +
			"c.created, c.modified, dfc.field_id, dfc.is_default FROM dict_dataset_field_comment dfc " +
			"LEFT JOIN field_comments c ON dfc.comment_id = c.id LEFT JOIN users u ON c.user_id = u.id " +
			"WHERE dataset_id = ? AND field_id = ? ORDER BY is_default DESC, created LIMIT ?,?";

	private final String CREATE_DATASET_COLUMN_COMMENT_REFERENCE =
			"INSERT IGNORE INTO dict_dataset_field_comment (dataset_id, field_id, comment_id) " +
			"VALUES (?,?,?)";

	private final String CHECK_COLUMN_COMMENT_HAS_DEFAULT =
			"SELECT comment_id FROM dict_dataset_field_comment WHERE dataset_id = ? AND field_id = ? " +
			"AND is_default = True";

	private final String SET_COLUMN_COMMENT_DEFAULT =
			"UPDATE dict_dataset_field_comment SET is_default = True " +
			"WHERE dataset_id = ? AND field_id = ? AND comment_id = ? " +
			"LIMIT 1";

	private final String GET_COUNT_COLUMN_COMMENTS_BY_ID =
			"SELECT COUNT(*) FROM dict_dataset_field_comment WHERE dataset_id = ? and comment_id = ?";

	private final String DELETE_COLUMN_COMMENT_AND_REFERENCE =
			"DELETE dfc, c FROM dict_dataset_field_comment dfc JOIN field_comments c " +
			"ON c.id = dfc.comment_id WHERE dfc.dataset_id = ? AND dfc.field_id = ? AND dfc.comment_id = ?";

	private final String DELETE_COLUMN_COMMENT_REFERENCE =
			"DELETE FROM dict_dataset_field_comment WHERE dataset_id = ? AND column_id = ? " +
			"AND comment_id = ? LIMIT 1";

	private final String GET_COLUMN_NAME_BY_ID =
			"SELECT UPPER(field_name) FROM dict_field_detail WHERE field_id = ?";

	private final String GET_SIMILAR_COMMENTS_BY_FIELD_NAME =
			"SELECT count(*) as count, f.comment_id, c.comment FROM dict_field_detail d " +
			"JOIN dict_dataset_field_comment f on d.field_id = f.field_id and d.dataset_id = f.dataset_id " +
			"JOIN field_comments c on c.id = f.comment_id WHERE d.field_name = ? and f.is_default = 1 " +
			"GROUP BY f.comment_id, c.comment ORDER BY count DESC";

	private final String SET_COLUMN_COMMENT_TO_FALSE = "UPDATE dict_dataset_field_comment " +
			"SET is_default = false WHERE dataset_id = ? AND field_id = ? AND is_default = true";

	private final String INSERT_DATASET_COLUMN_COMMENT = "INSERT INTO " +
			"dict_dataset_field_comment (dataset_id, field_id, comment_id, is_default) " +
			"VALUES (?, ?, ?, true) ON DUPLICATE KEY UPDATE is_default = true";

	private final String GET_SIMILAR_COLUMNS_BY_FIELD_NAME = "SELECT d.id as dataset_id, " +
			"d.name as dataset_name, dfd.field_id, dfd.data_type, fc.id as comment_id, fc.comment, d.source " +
			"FROM dict_field_detail dfd JOIN dict_dataset d ON dfd.dataset_id = d.id " +
			"LEFT JOIN dict_dataset_field_comment ddfc ON ddfc.dataset_id = d.id " +
			"AND ddfc.field_id = dfd.field_id AND ddfc.is_default = 1 " +
			"LEFT JOIN field_comments fc ON ddfc.comment_id = fc.id " +
			"WHERE dfd.dataset_id <> ? AND dfd.field_name = ? ORDER BY d.name asc";


	private final String GET_DATASET_OWNER_TYPES = "SELECT DISTINCT owner_type " +
			"FROM dataset_owner WHERE owner_type is not null";

	private final String GET_DATASET_DEPENDS_VIEW = "SELECT object_type, object_sub_type, " +
			"object_name, map_phrase, is_identical_map, mapped_object_dataset_id, " +
			"mapped_object_type,  mapped_object_sub_type, mapped_object_name " +
			"FROM cfg_object_name_map WHERE object_name = ?";

	private final String GET_DATASET_REFERENCES = "SELECT object_type, object_sub_type, " +
			"object_name, object_dataset_id, map_phrase, is_identical_map, mapped_object_dataset_id, " +
			"mapped_object_type,  mapped_object_sub_type, mapped_object_name " +
			"FROM cfg_object_name_map WHERE mapped_object_name = ?";

	private final String GET_DATASET_LISTVIEW_TOP_LEVEL_NODES = "SELECT DISTINCT " +
			"SUBSTRING_INDEX(urn, ':///', 1) as name, 0 as id, " +
			"concat(SUBSTRING_INDEX(urn, ':///', 1), ':///') as urn FROM dict_dataset order by 1";

	private final String GET_DATASET_LISTVIEW_NODES_BY_URN = "SELECT distinct " +
			"SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1), '/', 1) as name, " +
			"concat(?, SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1), '/', 1)) as urn, " +
			"s.id FROM dict_dataset d LEFT JOIN dict_dataset s " +
			"ON s.urn = concat(?, SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1), '/', 1)) " +
			"WHERE d.urn LIKE ? ORDER BY d.urn";

	private final String GET_DATASET_VERSIONS = "SELECT DISTINCT version " +
			"FROM dict_dataset_instance WHERE dataset_id = ? and version != '0' ORDER BY version_sort_id DESC";

	private final String GET_DATASET_NATIVE_NAME = "SELECT native_name " +
			"FROM dict_dataset_instance WHERE dataset_id = ? ORDER BY version_sort_id DESC limit 1";

	private final String GET_DATASET_SCHEMA_TEXT_BY_VERSION = "SELECT schema_text " +
			"FROM dict_dataset_instance WHERE dataset_id = ? and version = ? ORDER BY db_id DESC limit 1";

	private final String GET_DATASET_INSTANCES = "SELECT DISTINCT i.db_id, c.db_code FROM " +
			"dict_dataset_instance i JOIN cfg_database c ON i.db_id = c.db_id " +
			"WHERE i.dataset_id = ?";

	private final String GET_DATASET_ACCESS_PARTITION_GAIN = "SELECT DISTINCT partition_grain " +
			"FROM log_dataset_instance_load_status WHERE dataset_id = ? order by 1";

	private final String GET_DATASET_ACCESS_PARTITION_INSTANCES = "SELECT DISTINCT d.db_code " +
			"FROM log_dataset_instance_load_status l " +
			"JOIN cfg_database d on l.db_id = d.db_id WHERE dataset_id = ? and partition_grain = ? ORDER BY 1";

	private final String GET_DATASET_ACCESS = "SELECT l.db_id, d.db_code, l.dataset_type, l.partition_expr, " +
			"l.data_time_expr, l.data_time_epoch, l.record_count, l.size_in_byte, l.log_time_epoch, " +
			"from_unixtime(l.log_time_epoch) as log_time_str FROM log_dataset_instance_load_status l " +
			"JOIN cfg_database d on l.db_id = d.db_id WHERE dataset_id = ? and partition_grain = ? " +
			"ORDER by l.data_time_expr DESC";


	public  List<Map<String, Object>> getPagedDatasetByKeyword(String keyword, List<String> sources, int page, int size){
		final JdbcTemplate jdbcTemplate = getJdbcTemplate();

				List<Map<String, Object>> rows = null;
				if (sources != null && sources.size() == 5){
					String query = SEARCH_DATASET_WITH_PAGINATION_B.replace("$keyword", keyword);
					rows = jdbcTemplate.queryForList(query, (page-1)*size, size);
				}
				else
				{
					List<String> collect = sources.stream().map((s) -> "'" + s + "'").collect(Collectors.toList());
					String query = SEARCH_DATASET_BY_SOURCE_WITH_PAGINATION.replace("$keyword", keyword).replace("$sources", String.join(",", collect));
					rows = jdbcTemplate.queryForList(query,(page-1)*size, size);
				}

		return rows;
	}
	public  List<Map<String, Object>> getPagedDatasetByTagName(String tagName, int page, int size){
		final JdbcTemplate jdbcTemplate = getJdbcTemplate();

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(SEARCH_DATASET_BY_TAG_NAME,"%"+tagName+"%",(page-1)*size, size);

		return rows;
	}


	public List<String> getDatasetOwnerTypes()
	{
		return getJdbcTemplate().queryForList(GET_DATASET_OWNER_TYPES, String.class);
	}

	public ObjectNode getPagedDatasets(final String urn,final Integer page,final Integer size, String user)
	{
		ObjectNode result = Json.newObject();


		javax.sql.DataSource ds = getJdbcTemplate().getDataSource();
		DataSourceTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate txTemplate = new TransactionTemplate(tm);
		final Integer id = null;

		result = txTemplate.execute(new TransactionCallback<ObjectNode>() {
			public ObjectNode doInTransaction(TransactionStatus status) {

				ObjectNode resultNode = Json.newObject();
				List<Dataset> pagedDatasets = new ArrayList<Dataset>();
				List<Map<String, Object>> rows = null;
				if (id != null && id > 0)
				{
					if (StringUtils.isBlank(urn)) {
						rows = getJdbcTemplate().queryForList(
								SELECT_PAGED_DATASET_BY_CURRENT_USER,
								(page - 1) * size, size,
								id,
								id);
					} else {
						rows = getJdbcTemplate().queryForList(
								SELECT_PAGED_DATASET_BY_URN_CURRENT_USER,
								urn + "%",
								(page - 1) * size, size,
								id,
								id);
					}
				}
				else
				{
					if (StringUtils.isBlank(urn)) {
						rows = getJdbcTemplate().queryForList(
								SELECT_PAGED_DATASET,
								(page - 1) * size, size);
					} else {
						rows = getJdbcTemplate().queryForList(
								SELECT_PAGED_DATASET_BY_URN,
								urn + "%",
								(page - 1) * size, size);
					}

				}

				long count = 0;
				try {

					if (StringUtils.isBlank(urn)) {
						count = getJdbcTemplate().queryForObject(
								GET_PAGED_DATASET_COUNT,
								Long.class);
					}
					else
					{
						count = getJdbcTemplate().queryForObject(
								GET_PAGED_DATASET_COUNT_BY_URN,
								Long.class,
								urn + "%");
					}
				} catch (EmptyResultDataAccessException e) {
					logger.error("Exception = " + e.getMessage());
				}

				for (Map row : rows) {

					Dataset ds = new Dataset();
					Timestamp modified = (Timestamp)row.get(DatasetWithUserRowMapper.DATASET_MODIFIED_TIME_COLUMN);
					ds.id = (Long)row.get(DatasetWithUserRowMapper.DATASET_ID_COLUMN);
					ds.name = (String)row.get(DatasetWithUserRowMapper.DATASET_NAME_COLUMN);
					ds.source = (String)row.get(DatasetWithUserRowMapper.DATASET_SOURCE_COLUMN);
					ds.urn = (String)row.get(DatasetWithUserRowMapper.DATASET_URN_COLUMN);
					ds.schema = (String)row.get(DatasetWithUserRowMapper.DATASET_SCHEMA_COLUMN);
					String strOwner = (String)row.get(DatasetWithUserRowMapper.DATASET_OWNER_ID_COLUMN);
					String strOwnerName = (String)row.get(DatasetWithUserRowMapper.DATASET_OWNER_NAME_COLUMN);
					Long sourceModifiedTime =
							(Long)row.get(DatasetWithUserRowMapper.DATASET_SOURCE_MODIFIED_TIME_COLUMN);
					String properties = (String)row.get(DatasetWithUserRowMapper.DATASET_PROPERTIES_COLUMN);
					try
					{
						if (StringUtils.isNotBlank(properties))
						{
							ds.properties = Json.parse(properties);
						}
					}
					catch (Exception e)
					{
						logger.error(e.getMessage());
					}

					if (modified != null && sourceModifiedTime != null && sourceModifiedTime > 0)
					{
						ds.modified = modified;
						ds.formatedModified = modified.toString();
					}

					String[] owners = null;
					if (StringUtils.isNotBlank(strOwner))
					{
						owners = strOwner.split(",");
					}
					String[] ownerNames = null;
					if (StringUtils.isNotBlank(strOwnerName))
					{
						ownerNames = strOwnerName.split(",");
					}


					Integer favoriteId = (Integer)row.get(DatasetWithUserRowMapper.FAVORITE_DATASET_ID_COLUMN);
					Long watchId = (Long)row.get(DatasetWithUserRowMapper.DATASET_WATCH_ID_COLUMN);

					Long schemaHistoryRecordCount = 0L;
					try
					{
						schemaHistoryRecordCount = getJdbcTemplate().queryForObject(
								CHECK_SCHEMA_HISTORY,
								Long.class,
								ds.id);
					}
					catch (EmptyResultDataAccessException e)
					{
						logger.error("Exception = " + e.getMessage());
					}

					if (favoriteId != null && favoriteId > 0)
					{
						ds.isFavorite = true;
					}
					else
					{
						ds.isFavorite = false;
					}
					if (watchId != null && watchId > 0)
					{
						ds.watchId = watchId;
						ds.isWatched = true;
					}
					else
					{
						ds.isWatched = false;
						ds.watchId = 0L;
					}
					if (schemaHistoryRecordCount != null && schemaHistoryRecordCount > 0)
					{
						ds.hasSchemaHistory = true;
					}
					else
					{
						ds.hasSchemaHistory = false;
					}
					pagedDatasets.add(ds);
				}

				resultNode.put("count", count);
				resultNode.put("page", page);
				resultNode.put("itemsPerPage", size);
				resultNode.put("totalPages", (int) Math.ceil(count / ((double) size)));
				resultNode.set("datasets", Json.toJson(pagedDatasets));
				return resultNode;
			}
		});
		return result;
	}

	public ObjectNode ownDataset(int id, String user)
	{
		ObjectNode resultNode = Json.newObject();
		boolean result = false;
		List<Map<String, Object>> rows = null;

		rows = getJdbcTemplate().queryForList(GET_DATASET_OWNERS, id);
		int sortId = 0;
		for (Map row : rows)
		{
			String ownerId = (String)row.get(DatasetWithUserRowMapper.DATASET_OWNER_ID_COLUMN);
			String namespace = (String)row.get("namespace");
			int ret = getJdbcTemplate().update(UPDATE_DATASET_OWNER_SORT_ID, ++sortId, id, ownerId, namespace);
			if (ret <= 0)
			{
				logger.warn("ownDataset update sort_id failed. Dataset id is : " +
						Long.toString(id) + " owner_id is : " + ownerId + " namespace is : " + namespace);
			}
		}

		String urn = null;
		try
		{
			urn = (String)getJdbcTemplate().queryForObject(
					GET_DATASET_URN_BY_ID,
					String.class,
					id);
		}
		catch(EmptyResultDataAccessException e)
		{
			logger.error("Dataset ownDataset get urn failed, id = " + id);
			logger.error("Exception = " + e.getMessage());
		}
		int status = getJdbcTemplate().update(
				UPDATE_DATASET_OWNERS,
				id,
				user,
				300,
				"urn:li:corpuser",
				"Producer",
				"N",
				0,
				urn,
				"",
				"Producer",
				"N",
				0,
				"");
		if (status > 0)
		{
			result = true;
		}
		rows = getJdbcTemplate().queryForList(GET_DATASET_OWNERS, id);
		if (result)
		{
			resultNode.put("status", "success");
		}
		else
		{
			resultNode.put("status", "failed");
		}
		return resultNode;
	}

	public ObjectNode unownDataset(int id, String user)
	{
		ObjectNode resultNode = Json.newObject();
		boolean result = false;
		int ret = getJdbcTemplate().update(UNOWN_A_DATASET, id, user);
		if (ret > 0)
		{
			result = true;
		}
		List<Map<String, Object>> rows = null;
		rows = getJdbcTemplate().queryForList(GET_DATASET_OWNERS, id);
		int sortId = 0;
		for (Map row : rows)
		{
			String ownerId = (String)row.get(DatasetWithUserRowMapper.DATASET_OWNER_ID_COLUMN);
			String dislayName = (String)row.get("display_name");
			String namespace = (String)row.get("namespace");
			if (StringUtils.isBlank(dislayName))
			{
				dislayName = ownerId;
			}
			int updatedRows = getJdbcTemplate().update(UPDATE_DATASET_OWNER_SORT_ID, sortId++, id, ownerId, namespace);
			if (ret <= 0)
			{
				logger.warn("ownDataset update sort_id failed. Dataset id is : " +
						Long.toString(id) + " owner_id is : " + ownerId + " namespace is : " + namespace);
			}
		}
		if (result)
		{
			resultNode.put("status", "success");
		}
		else
		{
			resultNode.put("status", "failed");
		}
		return resultNode;
	}

	public Dataset getDatasetByID(int id, String user)
	{
		Dataset dataset = null;
		Integer userId = 0;
		if (StringUtils.isNotBlank(user))
		{
			try
			{
				userId = (Integer)getJdbcTemplate().queryForObject(
						GET_USER_ID,
						Integer.class,
						user);
			}
			catch(EmptyResultDataAccessException e)
			{
				logger.error("Dataset getDatasetByID get user id failed, username = " + user);
				logger.error("Exception = " + e.getMessage());
			}
		}
		try
		{
			if (userId != null && userId > 0)
			{
				dataset = (Dataset)getJdbcTemplate().queryForObject(
          				GET_DATASET_BY_ID_CURRENT_USER,
						new DatasetWithUserRowMapper(),
						userId,
						userId,
						id);

			}
			else
			{
				dataset = (Dataset)getJdbcTemplate().queryForObject(
						GET_DATASET_BY_ID,
						new DatasetRowMapper(),
						id);
			}
		}
		catch(EmptyResultDataAccessException e)
		{
			logger.error("Dataset getDatasetByID failed, id = " + id);
			logger.error("Exception = " + e.getMessage());
		}

		return dataset;
	}

	public List<DatasetColumn> getDatasetColumnByID(int datasetId, int columnId)
	{
		return getJdbcTemplate().query(GET_DATASET_COLUMNS_BY_DATASETID_AND_COLUMNID,
      new DatasetColumnRowMapper(), datasetId, columnId);
	}

	public List<DatasetColumn> getDatasetColumnsByID(int datasetId)
	{
		return getJdbcTemplate().query(GET_DATASET_COLUMNS_BY_DATASET_ID,
      new DatasetColumnRowMapper(), datasetId);
	}

	public JsonNode getDatasetPropertiesByID(int id)
	{
		String properties = "";
		String source = "";
		List<Map<String, Object>> rows = null;
		JsonNode propNode = null;

		rows = getJdbcTemplate().queryForList(GET_DATASET_PROPERTIES_BY_DATASET_ID, id);

		for (Map row : rows) {
			properties = (String)row.get("properties");
			source = (String)row.get("source");
			break;
		}

		if (StringUtils.isNotBlank(properties))
		{
			try {
				propNode = Json.parse(properties);

				if (propNode != null
						&& propNode.isContainerNode()
						&& propNode.has("url")
						&& StringUtils.isNotBlank(source)
						&& source.equalsIgnoreCase("pinot"))
				{
					URL url = new URL(propNode.get("url").asText());
					BufferedReader in =
							new BufferedReader(new InputStreamReader(url.openStream()));
					String resultString = "";
					String str;

					while ((str = in.readLine()) != null) {
						resultString += str;
					}

					in.close();
					JsonNode resultNode = Json.parse(resultString);

					if (resultNode == null)
					{
						return propNode;
					}
					else{
						return resultNode;
					}
				}
			}
			catch(Exception e) {
				logger.error("Dataset getDatasetPropertiesByID parse properties failed, id = " + id);
				logger.error("Exception = " + e.getMessage());
			}
		}

		return propNode;
	}

	public JsonNode getDatasetSampleDataByID(int id)
	{
		List<Map<String, Object>> rows = null;
		JsonNode sampleNode = null;
		String strSampleData = null;
		Integer refID = 0;

		rows = getJdbcTemplate().queryForList(GET_DATASET_SAMPLE_DATA_BY_ID, id);

		for (Map row : rows) {
			refID = (Integer)row.get("ref_id");
			strSampleData = (String)row.get("data");
			break;
		}

		if (refID != null && refID != 0)
		{
			rows = null;
			rows = getJdbcTemplate().queryForList(GET_DATASET_SAMPLE_DATA_BY_REFID, refID);
			for (Map row : rows) {
				strSampleData = (String)row.get("data");
				break;
			}
		}

		if (StringUtils.isNotBlank(strSampleData)) {
		}

		return sampleNode;
	}



	public ObjectNode getPagedDatasetComments(final String userName,final int id,final int page,final int size)
	{
		ObjectNode result = Json.newObject();

		javax.sql.DataSource ds = getJdbcTemplate().getDataSource();
		DataSourceTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate txTemplate = new TransactionTemplate(tm);

		result = txTemplate.execute(new TransactionCallback<ObjectNode>() {
			public ObjectNode doInTransaction(TransactionStatus status) {

				List<DatasetComment> pagedComments = getJdbcTemplate().query(
							GET_COMMENTS_BY_DATASET_ID,
							new DatasetCommentRowMapper(),
							id,
							(page - 1) * size, size);

				long count = 0;
				try {
					count = getJdbcTemplate().queryForObject(
							"SELECT FOUND_ROWS()",
							Long.class);
				} catch (EmptyResultDataAccessException e) {
					logger.error("Exception = " + e.getMessage());
				}

				if (pagedComments != null)
				{
					for(DatasetComment dc : pagedComments)
					{
						if(StringUtils.isNotBlank(userName) && userName.equalsIgnoreCase(dc.authorUserName))
						{
							dc.isAuthor = true;
						}
					}
				}

				ObjectNode resultNode = Json.newObject();
				resultNode.set("comments", Json.toJson(pagedComments));
				resultNode.put("count", count);
				resultNode.put("page", page);
				resultNode.put("itemsPerPage", size);
				resultNode.put("totalPages", (int) Math.ceil(count / ((double) size)));

				return resultNode;
			}
		});
		return result;
	}

	public boolean postComment(int datasetId, Map<String, String[]> commentMap, String user)
	{
		boolean result = false;
		if ((commentMap == null) || commentMap.size() == 0)
		{
			return false;
		}

		String text = "";
		if (commentMap.containsKey("text")) {
			String[] textArray = commentMap.get("text");
			if (textArray != null && textArray.length > 0)
			{
				text = textArray[0];
			}
		}
		if (StringUtils.isBlank(text))
		{
      return false;
		}

		String type = "Comment";
		if (commentMap.containsKey("type")) {
			String[] typeArray = commentMap.get("type");
			if (typeArray != null && typeArray.length > 0)
			{
				type = typeArray[0];
			}
		}

		Integer commentId = 0;
		if (commentMap.containsKey("id")) {
			String[] idArray = commentMap.get("id");
			if (idArray != null && idArray.length > 0)
			{
				String idStr = idArray[0];
				try
				{
					commentId = Integer.parseInt(idStr);
				}
				catch(NumberFormatException e)
				{
					logger.error("DatasetDAO postComment wrong id parameter. Error message: " +
							e.getMessage());
					commentId = 0;
				}
			}
		}

		Integer userId = 1;

		if (userId != null && userId !=0)
		{
			if (commentId != null && commentId != 0)
			{
				int row = getJdbcTemplate().update(UPDATE_DATASET_COMMENT, text, type, commentId);
				if (row > 0)
				{
					result = true;
				}
			}
			else
			{
				int row = getJdbcTemplate().update(CREATE_DATASET_COMMENT, text, userId, datasetId, type);
				if (row > 0)
				{
					result = true;
				}
			}
		}
		return result;
	}

	public boolean deleteComment(int id)
	{
		boolean result = false;
		int row = getJdbcTemplate().update(DELETE_DATASET_COMMENT, id);
		if (row > 0)
		{
			result = true;
		}
		return result;
	}


	public ObjectNode getPagedDatasetColumnComments(final String userName,final int datasetId,final int columnId,final int page,final int size)
	{
		ObjectNode result = Json.newObject();

		javax.sql.DataSource ds = getJdbcTemplate().getDataSource();
		DataSourceTransactionManager tm = new DataSourceTransactionManager(ds);
		TransactionTemplate txTemplate = new TransactionTemplate(tm);

		result = txTemplate.execute(new TransactionCallback<ObjectNode>() {
			public ObjectNode doInTransaction(TransactionStatus status) {

				ObjectNode resultNode = Json.newObject();
				long count = 0;
				int start = (page - 1) * size;
				int end = start + size;
				List<DatasetColumnComment> pagedComments = new ArrayList<DatasetColumnComment>();
				List<Map<String, Object>> rows = null;

				rows = getJdbcTemplate().queryForList(
					GET_COLUMN_COMMENTS_BY_DATASETID_AND_COLUMNID,
					datasetId,
					columnId,
					start,
					end
				);
				for (Map row : rows) {
					Long id = (Long)row.get("id");
					String author = (String)row.get("author");
					String authorEmail = (String)row.get("authorEmail");
					String authorUsername = (String)row.get("authorUsername");
					String text = (String)row.get("text");
					String created = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format((Timestamp)row.get("created"));
					String modified = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format((Timestamp)row.get("modified"));
					Long columnId = (Long)row.get("field_id");
					boolean isDefault = (Boolean)row.get("is_default");

					DatasetColumnComment datasetColumnComment = new DatasetColumnComment();
					datasetColumnComment.id = id;
					datasetColumnComment.author = author;
					datasetColumnComment.authorEmail = authorEmail;
					datasetColumnComment.authorUsername = authorUsername;
					datasetColumnComment.text = text;
					datasetColumnComment.created = created;
					datasetColumnComment.modified = modified;
					datasetColumnComment.columnId = columnId;
					datasetColumnComment.isDefault = isDefault;
					pagedComments.add(datasetColumnComment);
				}

				try {
					count = getJdbcTemplate().queryForObject("SELECT FOUND_ROWS()", Long.class);
				}
				catch (EmptyResultDataAccessException e) {
					logger.error("Exception = " + e.getMessage());
				}

				if (pagedComments != null)
				{
					for(DatasetColumnComment dc : pagedComments)
					{
						if(StringUtils.isNotBlank(userName) && userName.equalsIgnoreCase(dc.authorUsername))
						{
							dc.isAuthor = true;
						}
					}
				}

				resultNode.set("comments", Json.toJson(pagedComments));
				resultNode.put("count", count);
				resultNode.put("page", page);
				resultNode.put("itemsPerPage", size);
				resultNode.put("totalPages", (int) Math.ceil(count / ((double) size)));

				return resultNode;
			}
		});
		return result;
	}

	public boolean isSameColumnCommentExist(String text)
	{
		boolean exist = false;
		if (StringUtils.isNotBlank(text))
		{
			try {
				List<Map<String, Object>> comments = getJdbcTemplate().queryForList(
					CHECK_IF_COLUMN_COMMENT_EXIST,
					text,
					text);
				if (comments != null && comments.size() > 0)
				{
					exist = true;
				}
			} catch(DataAccessException e) {
				logger.error("Dataset isSameColumnCommentExist text is " + text);
				logger.error("Exception = " + e.getMessage());
			}
		}
		return exist;
	}

	public String postColumnComment(int datasetId, int columnId, Map<String, String[]> params, String user)
	{
    String result = "Post comment failed. Please try again.";
		if (params == null || params.size() == 0)
		{
			return result;
		}

		String text = "";
		if (params.containsKey("text")) {
			String[] textArray = params.get("text");
			if (textArray != null && textArray.length > 0)
			{
				text = textArray[0];
			}
		}
		if (StringUtils.isBlank(text))
		{
			return "Please input valid comment.";
		}

		Long commentId = 0L;
		if (params.containsKey("id")) {
			String[] idArray = params.get("id");
			if (idArray != null && idArray.length > 0)
			{
				String idStr = idArray[0];
				try
				{
					commentId = Long.parseLong(idStr);
				}
				catch(NumberFormatException e)
				{
					logger.error("DatasetDAO postColumnComment wrong id parameter. Error message: " +
							e.getMessage());
					commentId = 0L;
				}
			}
		}

		Integer userId = 0;
		try
		{
			userId = (Integer)getJdbcTemplate().queryForObject(
					GET_USER_ID,
					Integer.class,
					user);
		}
		catch(EmptyResultDataAccessException e)
		{
			logger.error("Dataset postColumnComment get user id failed, username = " + user);
			logger.error("Exception = " + e.getMessage());
		}

		if (userId != null && userId !=0)
		{
			if (commentId != null && commentId != 0)
			{
				int row = getJdbcTemplate().update(UPDATE_COLUMN_COMMENT, text, commentId);
				if (row > 0)
				{
					result = "";
				}
			}
			else
			{
				if (isSameColumnCommentExist(text))
				{
					return "Same comment already exists.";
				}
				KeyHolder keyHolder = new GeneratedKeyHolder();
				final String comment = text;
				final int authorId = userId;
				getJdbcTemplate().update(
						new PreparedStatementCreator() {
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement pst =
										con.prepareStatement(CREATE_COLUMN_COMMENT, new String[]{"id"});
								pst.setString(1, comment);
								pst.setInt(2, authorId);
								pst.setString(3, comment);
								return pst;
							}
						},
						keyHolder);
				commentId =  (Long)keyHolder.getKey();
				result = "";
			}
		}

		try
		{
			getJdbcTemplate().update(
				CREATE_DATASET_COLUMN_COMMENT_REFERENCE,
					datasetId,
					columnId,
					commentId
			);
		}
		catch(DataAccessException e)
		{
			logger.error("Dataset postColumnComment insert ignore reference, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
			logger.error("Exception = " + e.getMessage());
		}

		List<Map<String, Object>> defaultComment = null;
		try {
			defaultComment = getJdbcTemplate().queryForList(
					CHECK_COLUMN_COMMENT_HAS_DEFAULT,
					datasetId,
					columnId
			) ;
		} catch(DataAccessException e) {
			logger.error("Dataset postColumnComment - check for default, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
			logger.error("Exception = " + e.getMessage());
		}

		Boolean hasDefault = false;
		if(defaultComment.size() > 0) {
			hasDefault = true;
		}

		if(hasDefault) {
			result = "";
		} else {
			try {
				getJdbcTemplate().update(
						SET_COLUMN_COMMENT_DEFAULT,
						datasetId,
						columnId,
						commentId
				);
				result = "";
			} catch(DataAccessException e) {
				result = "Post comment failed. Please try again.";
				logger.error("Dataset postColumnComment set default comment, datasetId = " +
						Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
				logger.error("Exception = " + e.getMessage());
			}
		}
		return result;
	}

	public boolean deleteColumnComment(int datasetId, int columnId, int id)
	{
		boolean result = false;

		Integer commentCount = getJdbcTemplate().queryForObject(
			GET_COUNT_COLUMN_COMMENTS_BY_ID,
			new Object[] {datasetId, id},
			Integer.class);

		if(commentCount == null || commentCount == 0)
		{
			result = false;
		}
		else if (commentCount == 1)
		{
			try
			{
				getJdbcTemplate().update(
					DELETE_COLUMN_COMMENT_AND_REFERENCE,
					datasetId,
					columnId,
					id
				);
			}
			catch(DataAccessException e) {
				result = false;
				logger.error("Dataset deleteColumnComment remove reference and comment, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
				logger.error("Exception = " + e.getMessage());
			}
		}
		else {
			try {
				getJdbcTemplate().update(
					DELETE_COLUMN_COMMENT_REFERENCE,
					datasetId,
					columnId,
					id
				);
			}
			catch(DataAccessException e) {
				result = false;
				logger.error("Dataset deleteColumnComment remove reference, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
				logger.error("Exception = " + e.getMessage());
			}
		}

		return result;
	}


	public boolean assignColumnComment(int datasetId, int columnId, int commentId)
	{
		Boolean result = false;
		try {
			getJdbcTemplate().update(
					SET_COLUMN_COMMENT_TO_FALSE,
					datasetId,
					columnId
			);
		} catch(DataAccessException e) {
			logger.error("Dataset assignColumnComment - set current default to false, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
			logger.error("Exception = " + e.getMessage());
			return result;
		}

		try {
			getJdbcTemplate().update(
					INSERT_DATASET_COLUMN_COMMENT,
					datasetId,
					columnId,
					commentId
			);
			result = true;
		} catch(DataAccessException e) {
			logger.error("Dataset assignColumnComment - set current default to false, datasetId = " +
					Integer.toString(datasetId) + " columnId = " + Integer.toString(columnId));
			logger.error("Exception = " + e.getMessage());
			result = false;
		}
		return result;
	}


	public List<String> getDatasetVersions(Long datasetId, Integer dbId)
	{
		return getJdbcTemplate().queryForList(GET_DATASET_VERSIONS, String.class, datasetId);
	}

	public String getDatasetSchemaTextByVersion(
			Long datasetId, String version)
	{
		String schemaText = null;
		try
		{
			schemaText = getJdbcTemplate().queryForObject(
					GET_DATASET_SCHEMA_TEXT_BY_VERSION,
					String.class,
					datasetId, version);
		}
		catch (EmptyResultDataAccessException e)
		{
			schemaText = null;
		}
		return schemaText;
	}


	public List<String> getDatasetPartitionGains(Long id) {
		return getJdbcTemplate().queryForList(GET_DATASET_ACCESS_PARTITION_GAIN, String.class, id);
	}

	public List<String> getDatasetPartitionInstance(Long id, String partition) {
		return getJdbcTemplate().queryForList(GET_DATASET_ACCESS_PARTITION_INSTANCES, String.class, id, partition);
	}


}
