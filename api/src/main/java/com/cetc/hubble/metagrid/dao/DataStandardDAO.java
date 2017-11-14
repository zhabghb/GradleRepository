package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DictionaryParam;
import com.cetc.hubble.metagrid.vo.StdIdentifier;
import com.cetc.hubble.metagrid.vo.TreeNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Data Standard DAO.
 *
 * Created by dahey on 2017-3-21.
 */
@Repository
public class DataStandardDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(DataStandardDAO.class);

    private final static String QUERY_STD_ID_COUNT = "SELECT count(*) as total FROM std_identifier";
    private final static String QUERY_ALL_IDS = "SELECT id,internal_identifier as internalIdentifier ,ch_name as chName,en_name as enName,identifier,version,descripton,status,submit_institution as submitInstitution,approval_date as approvalDate,remark,gat_codex as gatCodex FROM std_identifier";
    private final static String QUERY_TABLES_BY_IDENTIFIER = "SELECT dd.id as datasetId,dd.urn as urn,dd.`name` as treeNodename,dd.parent_name as parentName,wej.data_source_name as sourceName,dd.alias as alias,wej.wh_etl_type as type,((UPPER(dfr.field_name)= ${fieldName}) and (dfr.field_name is not null)) as checked FROM dict_dataset dd LEFT JOIN  dict_field_detail dfd ON dd.id = dfd.dataset_id LEFT JOIN dataquality_field_rule dfr ON dfr.dataset_id=dd.id LEFT JOIN wh_etl_job wej ON wej.wh_etl_job_id=dfd.wh_etl_job_id WHERE UPPER(dfd.field_name) = ${fieldName}   order by checked DESC ";
    private final static String INSERT_STD_IDENTIFIER = "INSERT INTO std_identifier(internal_identifier, identifier, ch_name, en_name, version, descripton, status, submit_institution, approval_date, remark, gat_codex) VALUES(:internalIdentifier, :identifier, :chName, :enName, :version, :descripton, :status, :submitInstitution, :approvalDate, :remark, :gatCodex)";
    private final static String UPDATE_STD_IDENTIFIER =
            "UPDATE std_identifier SET internal_identifier=:internalIdentifier, ch_name=:chName, en_name=:enName, version=:version, descripton=:descripton, status=:status, submit_institution=:submitInstitution, approval_date=:approvalDate, remark=:remark, gat_codex=:gatCodex, identifier=:identifier WHERE id=:id";
    private final static String QUERY_STD_BY_ID = "SELECT internal_identifier as internalIdentifier, identifier, ch_name as chName, en_name as enName, version, descripton, status, submit_institution as submitInstitution, approval_date as approvalDate, remark, gat_codex as gatCodex FROM std_identifier WHERE id=:id";
    private final static String DELETE_STD_BY_ID = "DELETE FROM std_identifier WHERE id=:standardId";

    private static String DELETE_DATA_DOMAIN_IDENTIFIER_BY_ID = "delete from domain_identifier where identifier_id = ";

    /**
     * Get dictionary entries.
     *
     * @return
     */
    public Map<String, Object> getDictionaryEntries(DictionaryParam param) {

        int page = param.getPage();
        int limit = param.getLimit();
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(param.getKeyword())) {
            builder.append(" where internal_identifier like \"%").append(param.getKeyword()).append("%\" ");
            builder.append("or ch_name like \"%").append(param.getKeyword()).append("%\" ");
            builder.append("or identifier like \"%").append(param.getKeyword()).append("%\" ");
            builder.append("or gat_codex like \"%").append(param.getKeyword()).append("%\" ");
        }
        String builderWithoutPagination = builder.toString();
        // pagination
        builder.append(" limit ").append((page - 1) * limit).append(", ").append(limit);
        List<StdIdentifier> ls = getNamedParameterJdbcTemplate().query(QUERY_ALL_IDS + builder.toString(),new BeanPropertyRowMapper<StdIdentifier>(StdIdentifier.class));
        // result
        Map<String, Object> res = Maps.newHashMap();
        Long total = getJdbcTemplate().queryForObject(QUERY_STD_ID_COUNT + builderWithoutPagination, Long.class);
        res.put("total", total);
        res.put("results", ls);
        return res;
    }

    public List<TreeNode> getTablesByIdentifier (String identifier) {
        List<TreeNode> rows = getNamedParameterJdbcTemplate().query(QUERY_TABLES_BY_IDENTIFIER.replace("${fieldName}","'"+identifier+"'"),new BeanPropertyRowMapper<TreeNode>(TreeNode.class));
        ArrayList<Long> list = Lists.newArrayList();

        Iterator<TreeNode> it = rows.iterator();
        while (it.hasNext()) {
            TreeNode node = it.next();
            if (list.contains(node.getDatasetId())) {
                it.remove();
            }
            list.add(node.getDatasetId());
        }

        return rows;
    }

    /**
     * 保存单条记录
     * @param stdParams
     * @return
     */
    public int saveStdIdentifer(Map<String, Object> stdParams) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource stdParamsSource = new MapSqlParameterSource(stdParams);
        getNamedParameterJdbcTemplate().update(INSERT_STD_IDENTIFIER, stdParamsSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 通过标准和中文名称查询是否已经存在相关数据元标准
     *
     * @param identifier
     * @return
     */
    public List<StdIdentifier> getStdIdentifierList(String identifier) throws AppException {
        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(identifier)) {
            builder.append(" where identifier = '").append(identifier.trim()).append("' ");
        }
       return getNamedParameterJdbcTemplate().query(QUERY_ALL_IDS + builder.toString(),new BeanPropertyRowMapper<StdIdentifier>(StdIdentifier.class));
    }

    public void updateStdIdentifier(Map<String, Object> stdParams) {
        getNamedParameterJdbcTemplate().update(UPDATE_STD_IDENTIFIER, new MapSqlParameterSource(stdParams));
    }

    public StdIdentifier getStdIdentifierById(Map<String, Object> stdParams) {
        return getNamedParameterJdbcTemplate().queryForObject(
                QUERY_STD_BY_ID, new MapSqlParameterSource(stdParams), new BeanPropertyRowMapper<StdIdentifier>(StdIdentifier.class));
    }

    /**
     * 删除单条记录
     * @param stdParams
     * @return
     */
    public int deleteStdIdentifier(Map<String, Object> stdParams) {
        int result = getNamedParameterJdbcTemplate().update(DELETE_STD_BY_ID, new MapSqlParameterSource(stdParams));
        return result;
    }

    /**
     * 删除标准与主题的对应关系
     * @param identifierId
     */
    public void deleteDomainIdentifiers(Integer identifierId) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_DOMAIN_IDENTIFIER_BY_ID+identifierId,(SqlParameterSource) null);
    }


}


