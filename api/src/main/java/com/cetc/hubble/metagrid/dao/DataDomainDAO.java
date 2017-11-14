package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by dahey on 2017-5-26.
 */
@Repository
public class DataDomainDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(DataDomainDAO.class);
    private static String INSERT_DATA_DOMAIN = " INSERT INTO data_domain(`name`, code,`type`,icon, description,create_time) VALUES (:name, :code,:type, :icon, :description,:createTime)";
    private static String INSERT_DATA_DOMAIN_ATTR = "INSERT INTO data_domain_attr(`key`,`value`,domain_id) VALUES (:key, :value, :domainId)";
    private static String INSERT_DOMAIN_ENTITY = "INSERT INTO domain_entity(domain_id,entity_id) VALUES (:domainId,:entityId)";
    private static String INSERT_DOMAIN_IDENTIFIER = "INSERT INTO domain_identifier(domain_id,identifier_id) VALUES (:domainId,:identifierId)";
    private static String QUERY_DATA_DOMAINS = "SELECT id, `name`,code,`status`,icon, description,create_time as createTime,last_modify_time as lastModifyTime from data_domain";
    private static String QUERY_DUE_DATA_DOMAINS = "SELECT dd.id, dd.`name`,dd.`code`,dd.`status`, dd.description, ds.domain_img AS domainImg,ds.wh_service_id AS whServiceId from data_domain dd LEFT JOIN domain_service ds ON dd.id = ds.domain_id  WHERE `status` != 'UNPUSHED'";
    private static String QUERY_DATA_DOMAIN_EXT_BY_ID = "SELECT dd.id, dd.`name`,dd.`code`,dd.`status`, dd.description, ds.domain_img AS domainImg,ds.wh_service_id AS whServiceId from data_domain dd LEFT JOIN domain_service ds ON dd.id = ds.domain_id  WHERE dd.id = ";
    private static String QUERY_DATA_DOMAIN_BY_ID = "SELECT id, `name`,code,`status`,icon, description,create_time as createTime,last_modify_time as lastModifyTime from data_domain where id = ";
    private static String QUERY_DATA_DOMAIN_ATTR_BY_ID = "SELECT `key`,`value`,domain_id from data_domain_attr where domain_id = ";
    private static String QUERY_DOMAIN_ENTITY_BY_ID = "select de.entity_id as id,dd.`name`,dd.alias from domain_entity de LEFT JOIN dict_dataset dd ON de.entity_id =dd.id  where de.domain_id = ";
    private static String QUERY_DOMAIN_IDENTIFIER_BY_ID = "select si.* from domain_identifier di LEFT JOIN std_identifier si ON di.identifier_id =si.id  where di.domain_id = ";
    private static String UPDATE_DATA_DOMAIN_BY_ID = "update data_domain set `name`=:name,code=:code,icon=:icon,description=:description,last_modify_time = :lastModifyTime where id = ";
    private static String UPDATE_DATA_DOMAIN_MODIFY_TIME_BY_ID = "update data_domain set last_modify_time = :lastModifyTime where id = :domainId";
    private static String UPDATE_DATA_DOMAIN_STATUS_BY_ID = "update data_domain set `status` = :status where id = :domainId";
    private static String DELETE_DATA_DOMAIN_BY_ID = "delete from data_domain where id = ";
    private static String DELETE_DATA_DOMAIN_ATTR_BY_ID = "delete from data_domain_attr where domain_id = ";
    private static String DELETE_DATA_DOMAIN_ENTITY_BY_ID = "delete from domain_entity where domain_id = ";
    private static String DELETE_DATA_DOMAIN_ENTITY_COUNT_BY_ID = "select count(id) as totalEntities FROM domain_entity WHERE domain_id = ?";
    private static String DELETE_DATA_DOMAIN_IDENTIFIER_BY_ID = "delete from domain_identifier where domain_id = ";
    private static String DELETE_DATA_DOMAIN_SERVICE_BY_ID = "delete from domain_service where domain_id = ";
    private final static String COUNT_DATA_DOMAIN = "SELECT count(id) as total FROM data_domain";
    private static final String QUERY_DOMAIN_ID_FROM_DOMAIN_ATTR = "SELECT DISTINCT domain_id FROM data_domain_attr";
    private static final String QUERY_DOMAIN_ID_FROM_DOMAIN_IDENTIFIER = "SELECT DISTINCT di.domain_id FROM domain_identifier di JOIN std_identifier si ON di.identifier_id=si.id";
    private static final String QUERY_DOMAIN_ID_FROM_DOMAIN_ENTITY = "SELECT DISTINCT de.domain_id FROM domain_entity de JOIN dict_dataset ds ON de.entity_id=ds.id";
    private static final String INSERT_DOMAIN_SERVICE = "INSERT INTO domain_service(domain_id,`domain_img`,`wh_service_id`,`version`) VALUES (:domainID,:domainImg,:serviceId,:version)";
    private static final String QUERY_SERVICE_BY_DOMAIN_ID = "SELECT ds.domain_id domainID, ds.domain_img domainImg, ds.wh_service_id serviceId, ds.version version, ds.last_publish_time lastPublishTime FROM domain_service ds WHERE ds.domain_id = ";
    private static final String UPDATE_DOMAIN_SERVICE = "UPDATE domain_service SET `domain_img`=:domainImg,`version`=:version WHERE domain_id=:domainID";

    public int insertDomainService(DomainService domainService){
        return getNamedParameterJdbcTemplate().update(INSERT_DOMAIN_SERVICE,new BeanPropertySqlParameterSource(domainService));
    }

    public int insert(DataDomain dataDomain) {
        // 获取主表生成的主键Id
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_DATA_DOMAIN,new BeanPropertySqlParameterSource(dataDomain),keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void insertDomainAttr (DomainAttr attr) {
        getNamedParameterJdbcTemplate().update(INSERT_DATA_DOMAIN_ATTR,new BeanPropertySqlParameterSource(attr));
    }
    public void insertDomainEntity(Integer domainId,Long entityId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("domainId",domainId);
        paramMap.put("entityId",entityId);
        getNamedParameterJdbcTemplate().update(INSERT_DOMAIN_ENTITY,paramMap);
    }
    public void insertDomainIdentifier(Integer domainId,Long identifierId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("domainId",domainId);
        paramMap.put("identifierId",identifierId);
        getNamedParameterJdbcTemplate().update(INSERT_DOMAIN_IDENTIFIER,paramMap);
    }

    public void deleteDomainAttrs (Integer domainId) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_DOMAIN_ATTR_BY_ID+domainId,(SqlParameterSource) null);
    }

    public void deleteDomainEntities(Integer domainId) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_DOMAIN_ENTITY_BY_ID+domainId,(SqlParameterSource) null);
    }

    public void deleteDomainIdentifiers(Integer domainId) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_DOMAIN_IDENTIFIER_BY_ID+domainId,(SqlParameterSource) null);
    }

    @Transactional
    public void delete(Integer dataDomainId) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
        namedParameterJdbcTemplate.update(DELETE_DATA_DOMAIN_ENTITY_BY_ID+dataDomainId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_DOMAIN_ATTR_BY_ID+dataDomainId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_DOMAIN_IDENTIFIER_BY_ID+dataDomainId, (SqlParameterSource) null);
        namedParameterJdbcTemplate.update(DELETE_DATA_DOMAIN_BY_ID+dataDomainId, (SqlParameterSource) null);
    }


    public void update(Integer dataDomainId, DataDomain dataDomain) {
        getNamedParameterJdbcTemplate().update(UPDATE_DATA_DOMAIN_BY_ID + dataDomainId, new BeanPropertySqlParameterSource(dataDomain));
    }
    public void updateDomainMofifyTime(Integer domainId, Date lastModifyTime) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("domainId",domainId);
        paramMap.put("lastModifyTime",lastModifyTime);
        getNamedParameterJdbcTemplate().update(UPDATE_DATA_DOMAIN_MODIFY_TIME_BY_ID, paramMap);
    }

    public DataDomain queryById (Integer domainId) {
        return getNamedParameterJdbcTemplate().queryForObject(QUERY_DATA_DOMAIN_BY_ID+domainId,(SqlParameterSource)null,new BeanPropertyRowMapper<DataDomain>(DataDomain.class));
    }
    public DataDomainExt queryDataDomainExtById (Integer domainId) {
        return getNamedParameterJdbcTemplate().queryForObject(QUERY_DATA_DOMAIN_EXT_BY_ID+domainId,(SqlParameterSource)null,new BeanPropertyRowMapper<DataDomainExt>(DataDomainExt.class));
    }

    public List<DomainAttr> queryDomainAttrs(Integer domainId) {
        return getNamedParameterJdbcTemplate().query(QUERY_DATA_DOMAIN_ATTR_BY_ID+domainId,new BeanPropertyRowMapper<DomainAttr>(DomainAttr.class));
    }

    public List<DomainDataset> queryDomainEntities(Integer domainId) {
        return getNamedParameterJdbcTemplate().query(QUERY_DOMAIN_ENTITY_BY_ID + domainId, new BeanPropertyRowMapper<DomainDataset>(DomainDataset.class));
    }
    public Long queryDomainEntitiesCount(Integer domainId) {
        Map<String, Object> res = getJdbcTemplate().queryForMap(DELETE_DATA_DOMAIN_ENTITY_COUNT_BY_ID, domainId);
        return (Long) res.get("totalEntities");
    }
    public List<StdIdentifier> queryDomainIdentifiers(Integer domainId) {
        return getNamedParameterJdbcTemplate().query(QUERY_DOMAIN_IDENTIFIER_BY_ID + domainId, new BeanPropertyRowMapper<StdIdentifier>(StdIdentifier.class));
    }
    public List<DataDomainExt> queryDueDomains() {
        return getNamedParameterJdbcTemplate().query(QUERY_DUE_DATA_DOMAINS, new BeanPropertyRowMapper<DataDomainExt>(DataDomainExt.class));
    }
    public List<DataDomainExt> queryDueDomainsWithCondition(int page, int limit, String keyword, String type) {
        //拼接where条件，返回值带有where
        StringBuilder whereSQL = buildWhereSQL(keyword, type);
        //拼接limit条件
        String conditionSQL = buildOrderAndLimitSQLWithWhereSQL(page, limit, whereSQL);
        //替换返回的where为and
        conditionSQL = conditionSQL.replaceFirst("where","and");
        return getNamedParameterJdbcTemplate().query(QUERY_DUE_DATA_DOMAINS+conditionSQL, new BeanPropertyRowMapper<DataDomainExt>(DataDomainExt.class));
    }

    public Map<String, Object> queryPagedDataDomains(int page, int limit, String keyword, String type) {

        StringBuilder builder = buildWhereSQL(keyword, type);

        String builderWithoutPagination = builder.toString();

        // order and pagination
        buildOrderAndLimitSQLWithWhereSQL(page, limit, builder);
        List<DataDomain> ls = getNamedParameterJdbcTemplate().query(QUERY_DATA_DOMAINS + builder.toString(),new BeanPropertyRowMapper<DataDomain>(DataDomain.class));
        ArrayList<DataDomainVO> domainList = Lists.newArrayList();
        for (DataDomain dataDomain:ls) {
            DataDomainVO dataDomainVO = new DataDomainVO();
            dataDomainVO.setId(dataDomain.getId());
            dataDomainVO.setName(dataDomain.getName());
            dataDomainVO.setDescription(dataDomain.getDescription());
            dataDomainVO.setCode(dataDomain.getCode());
            dataDomainVO.setCreateTime(dataDomain.getCreateTime());
            dataDomainVO.setLastModifyTime(dataDomain.getLastModifyTime());
            dataDomainVO.setIcon(dataDomain.getIcon());
            dataDomainVO.setStatus(dataDomain.getStatus());
            dataDomainVO.setTotalEntities(queryDomainEntitiesCount(dataDomain.getId()));
            domainList.add(dataDomainVO);
        }
        // result
        Map<String, Object> res = Maps.newHashMap();
        Long total = getJdbcTemplate().queryForObject(COUNT_DATA_DOMAIN+builderWithoutPagination.toString(), Long.class);
        res.put("total", total);
        res.put("results", domainList);
        return res;
    }


    /**
     * 拼接limit条件
     * @param page
     * @param limit
     * @param whereSQL
     * @return
     */
    private String buildOrderAndLimitSQLWithWhereSQL(int page, int limit, StringBuilder whereSQL) {
        whereSQL.append(" order by create_time desc").append(" limit ").append((page - 1) * limit).append(", ").append(limit);
        return whereSQL.toString();
    }

    /**
     * 拼接where条件，返回值带有where
     * @param keyword
     * @param type
     * @return
     */
    private StringBuilder buildWhereSQL(String keyword, String type) {
        StringBuilder builder = new StringBuilder(" where 1=1");

        //处理关键字查询
        if (!Strings.isNullOrEmpty(keyword)) {
            builder.append(" and (");

            builder.append(" name like \"%").append(keyword).append("%\" ");
//            builder.append("or code like \"%").append(keyword).append("%\" ");
            builder.append("or description like \"%").append(keyword).append("%\" ");

            //附加查询条件
            appendQueryConditions(keyword, builder);

            builder.append(" )");
        }

        //处理type的查询，type为必填，但是考虑到接口兼容，允许为空
        if (!Strings.isNullOrEmpty(type)) {
            builder.append(" and type = '").append(type).append("'");
        }
        return builder;
    }

    public Map<String, Object> queryPagedDataDomainsTest(int page,int limit,String keyword) {

        StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(keyword)) {
            builder.append(" where name like \"%").append(keyword).append("%\" ");
//            builder.append("or code like \"%").append(keyword).append("%\" ");
            builder.append("or description like \"%").append(keyword).append("%\" ");

            //附加查询条件
            builder.append("OR id IN (SELECT DISTINCT dda.domain_id FROM data_domain_attr dda WHERE dda.value LIKE \"%").append(keyword).append("%\" ")
                    .append("UNION SELECT DISTINCT di.domain_id FROM domain_identifier di JOIN std_identifier si ON di.identifier_id=si.id WHERE si.ch_name LIKE \"%").append(keyword).append("%\" ")
                    .append("OR si.identifier LIKE \"%").append(keyword).append("%\" ")
                    .append("OR si.internal_identifier LIKE \"%").append(keyword).append("%\" ")
                    .append("UNION SELECT DISTINCT de.domain_id FROM domain_entity de JOIN dict_dataset dictd ON de.entity_id=dictd.id WHERE dictd.name LIKE \"%").append(keyword).append("%\" ")
                    .append("OR dictd.alias LIKE \"%").append(keyword).append("%\") ");
        }

        String builderWithoutPagination = builder.toString();

        // pagination
        builder.append(" limit ").append((page - 1) * limit).append(", ").append(limit);
        List<DataDomain> ls = getNamedParameterJdbcTemplate().query(QUERY_DATA_DOMAINS + builder.toString(),new BeanPropertyRowMapper<DataDomain>(DataDomain.class));
        ArrayList<DataDomainVO> domainList = Lists.newArrayList();
        for (DataDomain dataDomain:ls) {
            DataDomainVO dataDomainVO = new DataDomainVO();
            dataDomainVO.setId(dataDomain.getId());
            dataDomainVO.setName(dataDomain.getName());
            dataDomainVO.setDescription(dataDomain.getDescription());
            dataDomainVO.setCode(dataDomain.getCode());
            dataDomainVO.setCreateTime(dataDomain.getCreateTime());
            dataDomainVO.setLastModifyTime(dataDomain.getLastModifyTime());
            dataDomainVO.setIcon(dataDomain.getIcon());
            dataDomainVO.setTotalEntities(queryDomainEntitiesCount(dataDomain.getId()));
            domainList.add(dataDomainVO);
        }
        // result
        Map<String, Object> res = Maps.newHashMap();
        Long total = getJdbcTemplate().queryForObject(COUNT_DATA_DOMAIN+builderWithoutPagination.toString(), Long.class);
        res.put("total", total);
        res.put("results", domainList);
        return res;
    }

    private void appendQueryConditions(String keyword, StringBuilder builder) {
        List<Integer> idsWithDomainAttr = queryDomainIdsWithDomainAttr(keyword);
        List<Integer> idWithStdIdentifier = queryDomainIdsWithStdIdentifier(keyword);
        List<Integer> idWithTableNameAndAlias = queryDomainIdsWithTableNameAndAlias(keyword);
        List<Integer> domainIds = merge(merge(idsWithDomainAttr,idWithStdIdentifier), idWithTableNameAndAlias);

        if(!domainIds.isEmpty()) {
            builder.append(" OR id IN (");
            for(int i=0; i<domainIds.size()-1; i++) {
                builder.append(domainIds.get(i)).append(",");
            }
            builder.append(domainIds.get(domainIds.size()-1)).append(")");
        }
    }

    private List<Integer> queryDomainIdsWithDomainAttr(String keyword) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(keyword)) {
            stringBuilder.append(" WHERE value LIKE \"%").append(keyword).append("%\"");
        }
        List<Map<String, Object>> domainIds = getJdbcTemplate().queryForList(QUERY_DOMAIN_ID_FROM_DOMAIN_ATTR+stringBuilder.toString());
        return getDomainIdsFromMap(domainIds);
    }


    private List<Integer> queryDomainIdsWithStdIdentifier(String keyword) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(keyword)) {
            stringBuilder.append(" WHERE si.ch_name LIKE \"%").append(keyword).append("%\"")
            .append(" OR si.identifier LIKE \"%").append(keyword).append("%\"")
            .append(" OR si.internal_identifier LIKE \"%").append(keyword).append("%\"");
        }
        List<Map<String, Object>> domainIds = getJdbcTemplate().queryForList(QUERY_DOMAIN_ID_FROM_DOMAIN_IDENTIFIER+stringBuilder.toString());
        return getDomainIdsFromMap(domainIds);
    }

    private List<Integer> queryDomainIdsWithTableNameAndAlias(String keyword) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(keyword)) {
            stringBuilder.append(" WHERE ds.name LIKE \"%").append(keyword).append("%\"")
                    .append(" OR ds.alias LIKE \"%").append(keyword).append("%\"");
        }
        List<Map<String, Object>> domainIds = getJdbcTemplate().queryForList(QUERY_DOMAIN_ID_FROM_DOMAIN_ENTITY+stringBuilder.toString());
        return getDomainIdsFromMap(domainIds);
    }

    private List<Integer> getDomainIdsFromMap(List<Map<String, Object>> domainIds) {
        List<Integer> ids = new ArrayList<Integer>();
        for(int i=0; i<domainIds.size(); i++) {
            Set<Map.Entry<String, Object>> set = domainIds.get(i).entrySet();
            for(Map.Entry<String, Object> en : set) {
                ids.add(Integer.parseInt(en.getValue().toString()));
            }
        }
        return ids;
    }

    private<T> List<T> merge(List<T> ids1, List<T> ids2) {
        for(int i=0; i<ids2.size(); i++) {
            boolean found = false;
            for(int j=0; j<ids1.size(); j++) {
                if(ids2.get(i).equals(ids1.get(j))) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                ids1.add(ids2.get(i));
            }
        }
        return ids1;
    }

    public void deleteDomainService(Integer domainId) {
        getNamedParameterJdbcTemplate().update(DELETE_DATA_DOMAIN_SERVICE_BY_ID+domainId,(SqlParameterSource) null);
    }

    public void updateDomainStatus(Integer domainId, String status) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("domainId",domainId);
        paramMap.put("status",status);
        getNamedParameterJdbcTemplate().update(UPDATE_DATA_DOMAIN_STATUS_BY_ID, paramMap);
    }

    public DomainService queryDomainServiceByDomainId(Integer domainId) {
        return getNamedParameterJdbcTemplate().queryForObject(QUERY_SERVICE_BY_DOMAIN_ID+domainId, (SqlParameterSource)null, new BeanPropertyRowMapper<DomainService>(DomainService.class));
    }

    public void updateDomainService(DomainService domainService) {
        getNamedParameterJdbcTemplate().update(UPDATE_DOMAIN_SERVICE, new BeanPropertySqlParameterSource(domainService));
    }
}
