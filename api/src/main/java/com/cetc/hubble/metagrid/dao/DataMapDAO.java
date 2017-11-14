package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.*;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Data Quality DAO.
 *
 * Created by dahey on 17-3-10.
 */
@Repository
public class DataMapDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(DataMapDAO.class);

    private final static String QUERY_TAG_STATIS = "SELECT `name` as tagName ,total_expected as totalExpected,total_matched as totalMatched,round(total_matched/total_expected,2) as rate FROM std_tag";
    /**
     * V1版本改为动态统计
     */
    private final static String QUERY_TAG_STATIS_V1 = "select tag.name as tagName,count(table_tag.tab_id) as totalExpected,count(dataset.id) as totalMatched from std_tag tag left join std_table_tag table_tag on tag.id = table_tag.tag_id left join dict_dataset dataset on dataset.std_table_id = table_tag.tab_id group by tag.id ";

    private final static String QUERY_STD_TAGS = "SELECT id,`name` as tagName FROM std_tag";

    private final static String QUERY_STD_TABLES = "SELECT id,code,comment,datavolume as dataVolume  FROM std_table";

    private final static String QUERY_STD_TABLES_BY_TAGID = "SELECT  sta.id,sta.`code`,sta.`comment`,sta.datavolume as dataVolume FROM std_table_tag stt LEFT JOIN std_table sta ON stt.tab_id = sta.id  WHERE stt.tag_id = ";

    private final static String QUERY_DATASETS_BY_STD_TABID = "SELECT id,`name`,alias,source FROM dict_dataset WHERE std_table_id =  ";

    private final static String QUERY_DATASET_ID_BY_STD_TABID = "SELECT id FROM dict_dataset WHERE std_table_id =  ";

    private final static String QUERY_COLUMNS_BY_STD_TABID = "SELECT id,`code`,`comment`,data_type as dataType FROM std_column WHERE tab_id =  ";

    private final static String MATCH_DATASET_STD = "update dict_dataset set std_table_id = ? WHERE id =  ?";

    private final static String UPDATE_STD_TAG_TOTAL_MATCHED = "  update std_tag  st LEFT JOIN std_table_tag sta ON st.id = sta.tag_id set st.total_matched=st.total_matched+1 where sta.tab_id = ?";

    private final static String UNMATCH_DATASET_STD = "update dict_dataset set std_table_id = null WHERE id =  ?";

    private final static String QUERY_DATA_MAP = "SELECT st.id as tagId,st.`name` as tagName,sta.* FROM std_tag st LEFT JOIN std_table_tag stt ON st.id = stt.tag_id LEFT JOIN std_table sta ON stt.tab_id = sta.id";

    private final static String GET_TOTAL_MATCHED_DATASET_COUNT = "select count(id) as total from dict_dataset " +
            "where std_table_id is not null;";
    private final static String GET_TOTAL_EXPECTED_DATASET_COUNT = "select count(id) as total from std_table ";

    private final static String QUERY_STDTAG_AND_ALIAS_BY_URN = "SELECT st.`name` as tag,dd.alias FROM std_tag st LEFT JOIN std_table_tag sta ON st.id = sta.tag_id LEFT JOIN dict_dataset dd ON sta.tab_id = dd.std_table_id WHERE dd.urn= ? ";

    private final static String INSERT_STD_TAG = "INSERT INTO std_tag(name) VALUES(:tagName) ";

    private final static String INSERT_STD_TABLE = "INSERT INTO std_table(code, comment,datavolume) VALUES(:code,:comment,:datavolume) ";

    private final static String INSERT_STD_COLUMN = "INSERT INTO std_column(tab_id, code, data_type, comment) VALUES(:tabId,:code, :dataType,:comment) ";

    private final static String INSERT_STD_TABLE_TAG = "INSERT INTO std_table_tag(tab_id, tag_id) VALUES(:tabId,:tagId) ";

    private final static String UPDATE_STD_TAG_TOTAL_EXPECTED ="UPDATE std_tag SET total_expected =(SELECT count(id) FROM std_table_tag where tag_id = ?) WHERE id=?";

    public List<Map<String, Object>> queryDataMapTagStatis() {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(QUERY_TAG_STATIS_V1);
        return rows;
    }

    public List<Tag> queryStdTags () {
        List<Tag> rows = getNamedParameterJdbcTemplate().query(QUERY_STD_TAGS,new BeanPropertyRowMapper<Tag>(Tag.class));
        return rows;
    }
    public List<StdTable> queryStdTables () {
        List<StdTable> rows = getNamedParameterJdbcTemplate().query(QUERY_STD_TABLES,new BeanPropertyRowMapper<StdTable>(StdTable.class));
        return rows;
    }

    public List<StdTable> queryStdTableByTag (Integer tagId) {
        List<StdTable> rows = getNamedParameterJdbcTemplate().query(QUERY_STD_TABLES_BY_TAGID+tagId,new BeanPropertyRowMapper<StdTable>(StdTable.class));
        return rows;
    }

//    public List<SlimDataset> queryDatasetsByStdTab (Integer stdTableId) {
//        List<SlimDataset> rows = getNamedParameterJdbcTemplate().query(QUERY_DATASETS_BY_STD_TABID+stdTableId,new BeanPropertyRowMapper<SlimDataset>(SlimDataset.class));
//        return rows;
//    }
    public List<Map<String, Object>> queryDatasetByStdTab (Integer stdTableId) {
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(QUERY_DATASET_ID_BY_STD_TABID + stdTableId);
        if(rows.size() > 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("元数据标准表匹配到多条记录! std tableId id :"+stdTableId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows;
    }

    public List<StdColumn> queryStdColumnByTableId (Integer stdTableId) {
        List<StdColumn> rows = getNamedParameterJdbcTemplate().query(QUERY_COLUMNS_BY_STD_TABID+stdTableId,new BeanPropertyRowMapper<StdColumn>(StdColumn.class));
        return rows;
    }

    public void matchDatasetWithStd (Long dataSetId, Integer stdTableId) {
        getJdbcTemplate().update(MATCH_DATASET_STD, stdTableId,dataSetId);
    }
    public void unmatchDatasetWithStd(Long dataSetId) {
        getJdbcTemplate().update(UNMATCH_DATASET_STD,dataSetId);
    }

    public Long getTotalMatchedDatasetCount () {
        Map<String, Object> res = getJdbcTemplate().queryForMap(GET_TOTAL_MATCHED_DATASET_COUNT);
        return (Long) res.get("total");
    }
    public Long getTotalExpectedDatasetCount () {
        Map<String, Object> res = getJdbcTemplate().queryForMap(GET_TOTAL_EXPECTED_DATASET_COUNT);
        return (Long) res.get("total");
    }

    public void updateStdTagTotalMatched (Integer stdTableId) {
        getJdbcTemplate().update(UPDATE_STD_TAG_TOTAL_MATCHED,stdTableId);
    }

    public Map<String, Object> queryStdTagAndAliasByUrn (String urn) {
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(QUERY_STDTAG_AND_ALIAS_BY_URN, urn);
        if (list.size() != 1){
            logger.warn("为查询到该记录 urn:{}",urn);
            return Maps.newHashMap();
        }else {
            return list.get(0);
        }
    }

    /**
     * 通过名称查询std tag
     * @param name
     * @return
     */
    public StdTag queryStdTagByName(String name) {
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(name)) {
            builder.append(" where name = '").append(name.trim()).append("' ");
        }
        List<StdTag> stdTagList = getNamedParameterJdbcTemplate().query(QUERY_STD_TAGS + builder.toString(),new BeanPropertyRowMapper<StdTag>(StdTag.class));
        if(stdTagList != null && stdTagList.size() > 0) {
            return stdTagList.get(0);
        }

        return null;
    }

    /**
     * 通过code查询std table
     * @param code
     * @return
     */
    public StdTable queryStdTableByCode(String code) {
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(code)) {
            builder.append(" where code = '").append(code.trim()).append("' ");
        }
        List<StdTable> stdTableList = getNamedParameterJdbcTemplate().query(QUERY_STD_TABLES + builder.toString(),new BeanPropertyRowMapper<StdTable>(StdTable.class));
        if(stdTableList != null && stdTableList.size() > 0) {
            return stdTableList.get(0);
        }

        return null;
    }


    /**
     * 保存std tag
     * @param stdParams
     * @return
     */
    public int saveStdTag(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_STD_TAG, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 保存std table
     * @param stdParams
     * @return
     */
    public int saveStdTable(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_STD_TABLE, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }


    /**
     * 更新StdTag中的total_expected
     * @param tagId
     * @return
     */
    public int updateStdTagTotalExpected(int tagId){
        return getJdbcTemplate().update(UPDATE_STD_TAG_TOTAL_EXPECTED,tagId,tagId);
    }

    /**
     * 保存std column
     * @param stdParams
     * @return
     */
    public int saveStdColumn(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_STD_COLUMN, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 保存std tab tag
     * @param stdParams
     * @return
     */
    public int saveStdTableTag(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_STD_TABLE_TAG, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }
}
