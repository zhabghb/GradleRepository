package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.DataModelJson;
import com.cetc.hubble.metagrid.vo.DataModelRelationType;
import com.cetc.hubble.metagrid.vo.DataModelStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by tao on 13/12/16.
 */
@Repository
@Transactional
public class RelationshipDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(RelationshipDAO.class);
    private static String GET_RELATION_TYPE_LIST = "select id, en_name, ch_name, description, " +
            "icon, enabled from datamodel_relation_type;";
    private static String INSERT_A_DATAMODEL_JSON = "insert into datamodel_json (id,json, `name`,type) values (?, ?, ?,?);";
    private static String UPDATE_A_DATAMODEL_JSON = "update datamodel_json set json = :json,`name` = :name,type = :type where id = :id;";
    private static String QUERY_DATAMODEL_JSON = "select id, json, `name`,`type` from datamodel_json where id = :id;";
    private static String QUERY_DATAMODEL_STATUS_LIST = "SELECT dm.id, dm.name, dm.alias, COUNT(t.id) AS tableCount, " +
            "COUNT(r.id) AS relationshipCount, COUNT(f.id) AS fieldCount FROM datamodel dm LEFT JOIN " +
            "datamodel_table t ON dm.id = t.model_id LEFT JOIN datamodel_relation r ON dm.id = r.model_id " +
            "LEFT JOIN datamodel_field f ON dm.id = f.model_id GROUP BY dm.id ORDER BY dm.createtime DESC;";
    private static String DELETE_A_DATAMODEL = "DELETE FROM datamodel WHERE id = ";
    private static String DELETE_A_DATAMODEL_TABLE = "DELETE FROM datamodel_table WHERE model_id = ";
    private static String DELETE_A_DATAMODEL_FIELD = "DELETE FROM datamodel_field WHERE model_id = ";
    private static String DELETE_A_DATAMODEL_RELATION = "DELETE FROM datamodel_relation WHERE model_id = ";

    /**
     * Get table relationship type list.
     *
     * @return
     */
    public List<DataModelRelationType> getRelationTypeLs() {

        List<DataModelRelationType> dataModelRelationLs = Lists.newArrayList();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(GET_RELATION_TYPE_LIST);
        // id, en_name, ch_name, description, icon, enabled
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            String enName = (String) row.get("en_name");
            String chName = (String) row.get("ch_name");
            String description = (String) row.get("description");
            String icon = (String) row.get("icon");
            Integer enabled = (Integer) row.get("enabled");
            dataModelRelationLs.add(new DataModelRelationType(id, enName, chName,
                    description, icon, enabled));
        }
        return dataModelRelationLs;
    }

    /**
     * Insert a relationship json.
     *
     * @param json
     */
    public void save(DataModelJson json) {

        getJdbcTemplate().update(INSERT_A_DATAMODEL_JSON, json.getId(), json.getJson(), json.getName(),json.getType());
    }
    /**
     * Insert a relationship json.
     *
     * @param json
     */
    public void update(DataModelJson json) {

        getJdbcTemplate().update(UPDATE_A_DATAMODEL_JSON, new BeanPropertySqlParameterSource(json));
    }

    /**
     * Query a relationship by its id.
     *
     * @param id
     * @return
     */
    public DataModelJson query(Integer id) {

        Map param = Maps.newHashMap();
        param.put("id", id);
        return getNamedParameterJdbcTemplate().queryForObject(QUERY_DATAMODEL_JSON, param,
                new BeanPropertyRowMapper<DataModelJson>(DataModelJson.class));
    }

    /**
     * Get data model json list.
     *
     * @return
     */
    public List<DataModelStatus> getDMStatusLs() {

        List<DataModelStatus> res = Lists.newArrayList();
        List<Map<String, Object>> rows =
                getJdbcTemplate().queryForList(QUERY_DATAMODEL_STATUS_LIST);
        if (null == rows) return null;
        // id, name. alias, tableCount, relationshipCount, fieldCount
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            String name = (String) row.get("name");
            String alias = (String) row.get("alias");
            Long tableCount = (Long) row.get("tableCount");
            Long relationshipCount = (Long) row.get("relationshipCount");
            Long fieldCount = (Long) row.get("fieldCount");
            res.add(new DataModelStatus(id, name, alias, tableCount, relationshipCount, fieldCount));
        }
        return res;
    }

    public void deleteAllOld (Integer modelId) {
        getJdbcTemplate().batchUpdate(DELETE_A_DATAMODEL+modelId,DELETE_A_DATAMODEL_TABLE+modelId,DELETE_A_DATAMODEL_FIELD+modelId,DELETE_A_DATAMODEL_RELATION+modelId);
    }
}
