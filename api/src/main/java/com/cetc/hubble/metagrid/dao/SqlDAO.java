package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.QueryHistory;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by tao on 16-10-27.
 */
@Repository
@Transactional
public class SqlDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(SqlDAO.class);
    private static String INSERT_SQL_HISTORY = "INSERT INTO sql_history (`sql`,sql_name,db,source_id) VALUES (:sql,:sqlName,:db,:sourceId)";
    private static String LIST_HISTORY_BY_SOURCE = "SELECT sh.`create_time` as createTime,sh.sql_name as sqlName,sh.`sql`,sh.db as db,sh.source_id as sourceId,ej.data_source_name as sourceName FROM sql_history sh LEFT JOIN wh_etl_job ej ON ej.wh_etl_job_id = sh.source_id WHERE sh.source_id =  ";


    public void insert(QueryHistory queryHistory) {
        getNamedParameterJdbcTemplate().update(INSERT_SQL_HISTORY,new BeanPropertySqlParameterSource(queryHistory));
    }

    public boolean isHbaseTable(String tableName, String parent) {
        String sql = "select count(id) as count from dict_dataset where name = '" + tableName + "' and parent_name='" + parent + "' and source='Hbase'";
        return 1 == Integer.parseInt(String.valueOf(getJdbcTemplate().queryForMap(sql).get("count")));
    }

    public List<QueryHistory> getSqlHistories(int sourceId,String db,int limit) {
        String dbfilter = "";
        if (!Strings.isNullOrEmpty(db)){
             dbfilter = " AND sh.db = "+db;
        }
        StringBuilder query = new StringBuilder(LIST_HISTORY_BY_SOURCE).append(sourceId).append(dbfilter).append(" ORDER BY sh.`create_time` DESC ").append(" limit ").append(limit);
        List<QueryHistory> rows = getNamedParameterJdbcTemplate().query(query.toString(),new BeanPropertyRowMapper<QueryHistory>(QueryHistory.class));
        return rows;
    }
}
