package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tao on 16-10-31.
 */
@Repository
public class StructuredDataDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(StructuredDataDAO.class);
    private static String UPDATE_DATASET_ALIAS = "UPDATE dict_dataset SET alias = ? WHERE  id = ?;";
    //is_autolabeled描述[0:默认;1:已经被自动打上注释；2:手动注释]
    private static String UPDATE_FIELD_LABEL = "UPDATE dict_field_detail SET field_label = ?,is_autolabeled = 2 " +
            "WHERE dataset_id = ? AND field_id = ?;";
    private static String GET_TABLE_COUNT_BY_ID="select count(*) as tableCount FROM dict_dataset WHERE id = ?";
    private static String INSERT_A_DATASET_TAG = "INSERT INTO dataset_tag (dataset_id, tag_id) " +
            "VALUES(?, ?);";
    private static String DELETE_DATASET_TAGS = "DELETE FROM dataset_tag WHERE dataset_id = ?;";
    private static String GET_DATASET_META = "SELECT dd.id, dd.name,dd.parent_name, wj.data_source_name, dd.created_time, " +
            "dd.urn,dd.source, dd.alias, (SELECT count(*) FROM dict_field_detail WHERE dataset_id = ?) as column_num, " +
            "dd.modified_time FROM dict_dataset dd JOIN wh_etl_job wj WHERE dd.wh_etl_job_id = wj.wh_etl_job_id " +
            "AND dd.id = ?;";
    private static String GET_DATASET_TAGS = "SELECT t.id, t.tag_name, t.tag_color FROM dict_tag t " +
            "JOIN dataset_tag dt WHERE t.id = dt.tag_id AND dt.dataset_id = ? ORDER BY t.id;";
    private static String GET_MATCHING_TAGID_LS = "select id, tag_name from dict_tag where id in (:ids) ORDER BY id;";
    private static String GET_DATASET_COLUMN_INFO_BAK = "SELECT field_id, field_name, field_label, " +
            "data_type FROM dict_field_detail WHERE dataset_id = ? ORDER BY sort_id;";
    private static String GET_DATASET_COLUMN_INFO = "SELECT dfd.field_id, dfd.field_name, dfd.field_label,dfd.data_type,si.gat_codex,si.internal_identifier,dr.rule_name,dfr.last_check_time,dfr.next_check_time  FROM dict_field_detail dfd LEFT JOIN std_identifier si ON UPPER(si.identifier) = UPPER(dfd.field_name) LEFT JOIN  dataquality_field_rule dfr ON  dfr.field_id = dfd.field_id  LEFT JOIN dataquality_rule dr ON dfr.rule_id = dr.id WHERE dfd.dataset_id = ? ORDER BY sort_id";

    // detection for the exist of a data set
    private static String QUERY_FOR_DATASET_ID = "SELECT id FROM dict_dataset WHERE id = ?;";

    private static String QUERY_OWNER_BY_DATASET_URN = "SELECT urn,name,owner,owner_platform as ownerPlatform,owner_department as ownerDepartment,owner_tel as ownerTEL FROM dataset_owner WHERE urn = ";

    private static String QUERY_OWNER_BY_JOBID = "SELECT wh_etl_job_id as dataSourceId,owner,owner_platform as ownerPlatform,owner_department as ownerDepartment,owner_tel as ownerTEL FROM data_source_owner WHERE wh_etl_job_id = ";

    private static String INSERT_DATASET_OWNER = "INSERT INTO dataset_owner (`urn`,`name`,`owner`,`owner_platform`,`owner_department`, `owner_tel`) VALUES (:urn,:name,:owner,:ownerPlatform,:ownerDepartment,:ownerTEL)";

    private static String INSERT_DATA_SOURCE_OWNER = "INSERT INTO data_source_owner (`wh_etl_job_id`,`owner`,`owner_platform`,`owner_department`, `owner_tel`) VALUES (:dataSourceId,:owner,:ownerPlatform,:ownerDepartment,:ownerTEL)";

    private static String UPDATE_DATA_SOURCE_OWNER = "UPDATE data_source_owner set `owner` = :owner,`owner_platform` = :ownerPlatform,`owner_department` = :ownerDepartment, `owner_tel` = :ownerTEL WHERE `wh_etl_job_id` = :dataSourceId";
    private static String DELETE_DATA_SOURCE_OWNER = "DELETE FROM data_source_owner WHERE wh_etl_job_id = :dataSourceId";

    private static String QUERY_ALIAS_BY_URN = "SELECT alias FROM dict_dataset WHERE urn = ?";
    private static String QUERY_FIELD_BY_DATASET_AND_NAME = "SELECT field_id,field_name from dict_field_detail  WHERE dataset_id = ? AND UPPER(field_name) = ?";
    private static String QUERY_FIELDS_BY_FIELD_ID = "SELECT field_name as fieldName,field_label as comment FROM dict_field_detail WHERE dataset_id = (SELECT dataset_id FROM dict_field_detail WHERE field_id = ?)";

    private static String INSERT_DATASET_ATTR = "INSERT INTO dict_dataset_attr (`dataset_id`,`attr_name`,`attr_value`) VALUES (:datasetId,:attrName,:attrValue)";
    private static String UPDATE_DATASET_ATTR = "UPDATE dict_dataset_attr  SET attr_name = :attrName, attr_value = :attrValue  WHERE  dataset_id = :datasetId AND id = :id ";
    private static String DELETE_DATASET_ATTR = "DELETE FROM dict_dataset_attr WHERE id = :id ";
    private static String QUERY_DATASET_ATTR_BY_DATASET_ID = "SELECT id, dataset_id as datasetId,attr_name as attrName, attr_value as attrValue FROM dict_dataset_attr where dataset_id = :datasetId ";
    private static String QUERY_DATASET_ATTR_BY_DATASET_ID_AND_ATTR_NAME = "SELECT id, dataset_id as datasetId,attr_name as attrName, attr_value as attrValue FROM dict_dataset_attr where dataset_id = :datasetId AND attr_name = :attrName";
    private static String QUERY_DATASET_ATTR_COUNT_BY_DATASET_ID = "SELECT count(id) as count FROM dict_dataset_attr where dataset_id = ? ";
    private static String QUERY_DATASET_ATTR_BY_PARAMS = "SELECT id, dataset_id as datasetId,attr_name as attrName, attr_value as attrValue FROM dict_dataset_attr where dataset_id = :datasetId AND attr_name = :attrName AND id != :id";
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Update a data set's alias by specifying its ID.
     *
     * @param dataSetID
     * @param alias
     * @return true if update successful, vise versa.
     */
    public boolean updateDataSetAlias(long dataSetID, String alias) {

        logger.info("Update data set's alias: {} {}", dataSetID, alias);
        boolean res = false;
        int row = getJdbcTemplate().update(UPDATE_DATASET_ALIAS, alias, dataSetID);
        if (row > 0) {
            res = true;
        }
        return res;
    }

    /**
     * Query for specific data set ID to evaluate a data set's existence.
     *
     * @param dataSetID
     * @return
     */
    public boolean detectDataSetExistence(long dataSetID) {

        List list = getJdbcTemplate().queryForList(QUERY_FOR_DATASET_ID, Long.class, dataSetID);
        return list.size() == 0;
    }

    /**
     * Update a field's label (comment) by specifying the field ID
     * and data set ID.
     *
     * @param dataSetID
     * @param fieldID
     * @param label     field comment String
     * @return true if update successful, vise versa.
     */
    public boolean updateFieldLabel(long dataSetID, int fieldID, String label) {

        logger.info("Update data set's field label. {} {} {}.", dataSetID,
                fieldID, label);
        boolean res = false;
        int row = getJdbcTemplate().update(UPDATE_FIELD_LABEL, label,
                dataSetID, fieldID);
        if (row > 0) {
            res = true;
        }
        return res;
    }

    public Long getTableCountById(Long fieldID){
        Map<String,Object> map = getJdbcTemplate().queryForMap(GET_TABLE_COUNT_BY_ID,fieldID);
        Long count=(Long) map.get("tableCount");
        return count;
    }

    /**
     * Delete all associated tags for a data set.
     *
     * @param dataSetID
     * @return true if update successful, vise versa.
     */
    public void deleteDatasetTags(long dataSetID) {

        logger.info("Delete tags for data set: {}", dataSetID);
        getJdbcTemplate().update(DELETE_DATASET_TAGS, dataSetID);
    }

    /**
     * First check if there is any tag been deleted.
     * If not then batch insert data set to tag mappings.
     * If there is deletion then throw out AppException with deleted IDs.
     *
     * @param dataSetID
     * @param tags      tag list for insert
     */
    public void insertDatasetTags(long dataSetID, List<Tag> tags) throws AppException {

        logger.debug("Insert tags for data set: {}", dataSetID);
        // construct input id int array
        List inputIDLs = Lists.newArrayList();
        List matchingIDLs = Lists.newArrayList();
        for (Tag tag : tags) {
            inputIDLs.add(tag.getId());
        }
        // construct matching id int array
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", inputIDLs);
        List<Map<String, Object>> matchingList =
                getNamedParameterJdbcTemplate().queryForList(GET_MATCHING_TAGID_LS, parameters);
        for (Map<String, Object> row : matchingList) {
            matchingIDLs.add(row.get("id"));
        }
        // lengths equal if no tag has been deleted then go on to do insertion
        if (inputIDLs.size() == matchingIDLs.size()) {
            getJdbcTemplate().batchUpdate(INSERT_A_DATASET_TAG, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    Tag tag = tags.get(i);
                    preparedStatement.setLong(1, dataSetID);
                    preparedStatement.setInt(2, tag.getId());
                }

                @Override
                public int getBatchSize() {
                    return tags.size();
                }
            });
        } else {
            // find all deleted ids
            List deletedIDLs = Lists.newArrayList();
            for (Object id : inputIDLs) {
                if (!matchingIDLs.contains(id)) deletedIDLs.add(id);
            }
            throw new AppException(deletedIDLs.toString(), ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * Get a data set's tag list by its ID.
     *
     * @param dataSetID
     * @return list of tags
     */
    public List<Tag> getDataSetTags(long dataSetID) {

        List<Map<String, Object>> rows =
                getJdbcTemplate().queryForList(GET_DATASET_TAGS, dataSetID);
        List<Tag> tags = Lists.newArrayList();
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            String tagName = (String) row.get("tag_name");
            String tagColor = (String) row.get("tag_color");
            tags.add(new Tag(id, tagName, tagColor));
        }
        return tags;
    }

    /**
     * Get data set meta data by data set ID.
     *
     * @param dataSetID
     * @return StructuredDataSetMetaData instance
     */
    public StructuredDataSetMetaData getDataSetMeta(Long dataSetID) {

        List<Tag> tags = getDataSetTags(dataSetID);
        Map<String, Object> row = getJdbcTemplate().queryForMap(GET_DATASET_META,
                dataSetID.longValue(), dataSetID.longValue());
        //id, name, created_time, urn, alias, column_num, modified_time
        String name = (String) row.get("name");
        String parentName = (String) row.get("parent_name");
        String dataSourceName = (String) row.get("data_source_name");
        Long creationTimeL = (Long) row.get("created_time");
        DateTime creationDT = new DateTime(creationTimeL * 1000);
        String creationTime = creationDT.toString(formatter);
        String urn = (String) row.get("urn");
        String type = (String) row.get("source");
        String alias = (String) row.get("alias");
        Long columnNum = (Long) row.get("column_num");
        Long modificationTimeL = (Long) row.get("modified_time");
        if (modificationTimeL == null) {
            modificationTimeL = creationTimeL;
        }
        DateTime modificationDT = new DateTime(modificationTimeL * 1000);
        String modificationTime = modificationDT.toString(formatter);
        StructuredDataSetMetaData metaData =
                new StructuredDataSetMetaData(dataSetID, name, parentName,dataSourceName, tags, creationTime,
                        urn, alias, columnNum, modificationTime,urn,type);
        return metaData;
    }

    /**
     * Get data set's column info by data set ID.
     *
     * @param dataSetID
     * @return StructuredDataSetColumnInfo instance
     */
    public StructuredDataSetColumnInfo getColumnInfo(Long dataSetID) {

        List<StructuredDataSetColumn> columns = Lists.newArrayList();
        List<Map<String, Object>> rows = getJdbcTemplate().
                queryForList(GET_DATASET_COLUMN_INFO, dataSetID.longValue());
        // field_id, field_name, field_label, data_type
        for (Map row : rows) {
            Long fieldID = (Long) row.get("field_id");
            String fieldName = (String) row.get("field_name");
            String comment = (String) row.get("field_label");
            String type = (String) row.get("data_type");
            String gatCodex = (String) row.get("gat_codex");
            String internalIdentifier = (String) row.get("internal_identifier");
            String ruleName = (String) row.get("rule_name");
            Date lastCheckTime = (Date) row.get("last_check_time");
            Date nextCheckTime = (Date) row.get("next_check_time");
            columns.add(new StructuredDataSetColumn(fieldID, fieldName,
                    comment, type,gatCodex,internalIdentifier,ruleName,lastCheckTime,nextCheckTime));
        }
        return new StructuredDataSetColumnInfo(dataSetID, columns);
    }

    /**
     * Get dictionary entries.
     *
     * @return
     */
    public Map<String, Object> getDictionaryEntries(DictionaryParam param) {

        String countTotal = "select count(DISTINCT `code`,`comment`) as total from std_column where char_length(trim(comment)) <> 0 ";
        String getList = "select `code`,`comment` from std_column where  char_length(trim(comment)) <> 0 ";
        int page = param.getPage();
        int limit = param.getLimit();
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(param.getKeyword())) {
            builder.append("and code like \"%").append(param.getKeyword()).append("%\" ");
            builder.append("or comment like \"%").append(param.getKeyword()).append("%\" ");
        }
        Long total = getJdbcTemplate().queryForObject(countTotal + builder.toString(), Long.class);
        // pagination
        builder.append("limit ?, ?");
        List<Map<String, Object>> ls = getJdbcTemplate().queryForList(getList + builder.toString(),
                (page - 1) * limit, limit);
        // result
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("results", ls);
        return res;
    }

    /**
     * 根据数据集urn查询数据集拥有者信息
     * @param urn
     * @return
     */
    public DatasetOwner getDatasetOwnerByUrn(String urn) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();

        StringBuilder sql = new StringBuilder(QUERY_OWNER_BY_DATASET_URN);
        sql.append("\"").append(urn).append("\"");
        List<DatasetOwner> rows = getNamedParameterJdbcTemplate().query(sql.toString(),new BeanPropertyRowMapper<DatasetOwner>(DatasetOwner.class));

        if(rows.size() != 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("dataset urn :" + urn + "不存在！", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows.get(0);
    }
    /**
     * 根据etlJobId查询数据源拥有者信息
     * @param etlJobId
     * @return
     */
    public DataSourceOwner getDataSourceOwnerByJobId(Integer etlJobId) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();

        List<DataSourceOwner> rows = getNamedParameterJdbcTemplate().query(QUERY_OWNER_BY_JOBID+etlJobId,new BeanPropertyRowMapper<DataSourceOwner>(DataSourceOwner.class));

        if(rows.size() != 1){
            logger.error("=======rows size not legal:{}=======",rows.size());
            throw new AppException("未找到该记录! job id :"+etlJobId, ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return rows.get(0);
    }

    public void insertDatasetOwner (DatasetOwner datasetOwner) {
        getNamedParameterJdbcTemplate().update(INSERT_DATASET_OWNER,new BeanPropertySqlParameterSource(datasetOwner));
    }

    public void insertDataSourceOwner (DataSourceOwner dataSourceOwner) {
        getNamedParameterJdbcTemplate().update(INSERT_DATA_SOURCE_OWNER,new BeanPropertySqlParameterSource(dataSourceOwner));
    }

    public void updateDataSourceOwner (DataSourceOwner dataSourceOwner) {
        getNamedParameterJdbcTemplate().update(UPDATE_DATA_SOURCE_OWNER,new BeanPropertySqlParameterSource(dataSourceOwner));
    }
    public void updateDataSourceOwner2 (DataSourceOwner dataSourceOwner) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_SOURCE_OWNER,new BeanPropertySqlParameterSource(dataSourceOwner));
        getNamedParameterJdbcTemplate().update(INSERT_DATA_SOURCE_OWNER,new BeanPropertySqlParameterSource(dataSourceOwner));
    }

    public String queryTableAliasByUrn (String urn) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(QUERY_ALIAS_BY_URN, urn);
        if (resultList.size() != 1) {
            logger.error("=======rows size not legal:{}=======", resultList.size());
            //即使查不到该源下的表也要返回源的owner信息，所以不抛异常
//            throw new AppException("数据集urn:" + urn + "不存在！", ErrorCode.CUSTOM_EXCEPTION);
            return null;
        }else {
            return (String) resultList.get(0).get("alias");
        }
    }
    public String queryTableTagByUrn (String urn) {
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(QUERY_ALIAS_BY_URN, urn);
        if (resultList.size() != 1) {
            logger.error("=======rows size not legal:{}=======", resultList.size());
            //即使查不到该源下的表也要返回源的owner信息，所以不抛异常
//            throw new AppException("数据集urn:" + urn + "不存在！", ErrorCode.CUSTOM_EXCEPTION);
            return null;
        }else {
            return (String) resultList.get(0).get("alias");
        }
    }

    public Map<String, Object> queryFieldByDatasetAndFieldName (Long datasetId, String fieldName) {
        Map<String, Object> row = getJdbcTemplate().queryForMap(QUERY_FIELD_BY_DATASET_AND_NAME, datasetId, fieldName);
        return row;
    }
    public List<Map<String, Object>> queryFieldNameAndLabelByFieldId (Long fieldId) {
        List<Map<String, Object>> columns = getJdbcTemplate().queryForList(QUERY_FIELDS_BY_FIELD_ID, fieldId);
        return columns;
    }

    /**
     * 保存单条记录
     * @param stdParams
     * @return
     */
    public int saveDataSetAttr(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_DATASET_ATTR, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 保存单条记录
     * @param stdParams
     * @return
     */
    public void updateDataSetAttr(Map<String, Object> stdParams) {
        getNamedParameterJdbcTemplate().update(UPDATE_DATASET_ATTR, new MapSqlParameterSource(stdParams));
    }

    /**
     * 删除单条记录
     * @param stdParams
     * @return
     */
    public void deleteDatasetAttr(Map<String, Object> stdParams) {
        getNamedParameterJdbcTemplate().update(DELETE_DATASET_ATTR, new MapSqlParameterSource(stdParams));
    }

    public List<DatasetAttr> getDatasetAttrListByDataSetId(Map<String, Object> stdParams) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATASET_ATTR_BY_DATASET_ID, new MapSqlParameterSource(stdParams),new BeanPropertyRowMapper<DatasetAttr>(DatasetAttr.class));
    }

    public List<DatasetAttr> getDatasetAttrListByDataSetIdAndAttrName(Map<String, Object> stdParams) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATASET_ATTR_BY_DATASET_ID_AND_ATTR_NAME, new MapSqlParameterSource(stdParams),new BeanPropertyRowMapper<DatasetAttr>(DatasetAttr.class));
    }

    public Long getDatasetAttrCountByDataSetId(Long datasetId) {
        return getJdbcTemplate().queryForObject(QUERY_DATASET_ATTR_COUNT_BY_DATASET_ID, Long.class, datasetId);
    }

    public List<DatasetAttr> getDatasetAttrListByParams(Map<String, Object> stdParams) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATASET_ATTR_BY_PARAMS, new MapSqlParameterSource(stdParams),new BeanPropertyRowMapper<DatasetAttr>(DatasetAttr.class));
    }

}
