package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.StageLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Schema History DAO.
 * <p>
 * Created by dahey on 16-10-27.
 */
@Repository
public class SchemaHistoryDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(SchemaHistoryDAO.class);

    private final static String GET_UPDATE_LOG_BY_DATE = "SELECT id,date_format(update_time,'%Y-%m-%d %H:%i:%S') as updateTime,type,`status`,dataset_id as datasetId,dataset_name as datasetName,parent_name as parentName,dataset_alias as datasetAlias, username,j.data_source_name as sourceName FROM stage_log sl " +
            "LEFT JOIN wh_etl_job j ON sl.wh_etl_job_id = j.wh_etl_job_id " +
            "WHERE date_format(update_time,'%Y/%m/%d') =";

    private final static String GET_SYNC_DATES = "SELECT DISTINCT FROM_UNIXTIME(end_time,'%Y/%m/%d') as syncDate FROM  wh_etl_job_execution  where `status` = 'SUCCEEDED' and end_time is not null ORDER BY syncDate";

    private final static String GET_UPDATE_DATES = "SELECT distinct date_format(update_time,'%Y/%m/%d') as updateDate FROM stage_log  order by updateDate";

    public final static String GET_DATASET_SCHEMA_DIFF = "SELECT `schema`,previous_schema FROM stage_log where id = ?";

    public Map<String,Object> queryDatasetDiff(Long stageLogId) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_DATASET_SCHEMA_DIFF,stageLogId);

        if(rows.size() != 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("stageLog id:" + stageLogId + "不存在！", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows.get(0);
    }

    public List<StageLog> getUpdateLogByDate(String date) {
        List<StageLog> rows = getNamedParameterJdbcTemplate().query(GET_UPDATE_LOG_BY_DATE+"'"+date+"'"+" ORDER BY  update_time DESC ",new BeanPropertyRowMapper<StageLog>(StageLog.class));
        return rows;
    }

    public List<Map<String, Object>> getSyncDates() {
        return getNamedParameterJdbcTemplate().queryForList(GET_SYNC_DATES, (SqlParameterSource) null);
    }
    public List<Map<String, Object>> getUpdateDates() {
        return getNamedParameterJdbcTemplate().queryForList(GET_UPDATE_DATES, (SqlParameterSource) null);
    }
}
