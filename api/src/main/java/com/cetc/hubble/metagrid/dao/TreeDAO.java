package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.TagResult;
import com.cetc.hubble.metagrid.vo.TreeNode;
import com.cetc.hubble.metagrid.vo.TreeNodes;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Tree view DAO.
 * <p>
 * Created by tao on 16-10-25.
 */
@Repository
public class TreeDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(TreeDAO.class);
    private final static String GET_DATASOURCE_LIST = "SELECT wj.wh_etl_job_id AS data_source_id, wj.data_source_name, " +
            "wj.running, wj.comments, wj.wh_etl_type AS data_source_type FROM wh_etl_job wj ORDER BY create_time DESC;";
    private final static String GET_PAGED_TREENODES_BY_URN = "SELECT DISTINCT " +
            "s.id, SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1), '/', 1) AS name, " +
            "s.alias, s.parent_name, s.source as type, " +
            "concat(?, SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1), '/', 1 )) AS urn " +
            "FROM dict_dataset d LEFT JOIN dict_dataset s " +
            "ON s.urn = concat(?, SUBSTRING_INDEX(SUBSTRING_INDEX(d.urn, ?, -1) , '/', 1 )) " +
            "WHERE d.urn LIKE ? ORDER BY d.urn LIMIT ?, ?;";
    private final static String GET_TAG_NODES = "SELECT t.id, t.tag_name AS tagName, t.tag_color AS tagColor, st.num AS total " +
            "FROM dict_tag t LEFT JOIN (SELECT tag_id, COUNT(DISTINCT dataset_id) AS num " +
            "FROM dataset_tag st GROUP BY tag_id) AS st ON t.id = st.tag_id WHERE t.id IS NOT NULL ORDER BY t.create_time desc;";
    private final static String GET_PAGED_DATASET_NODES_BY_TAG_ID = "SELECT ds.id, ds.urn, ds.name, ds.parent_name, " +
            "ds.alias, ds.source AS type FROM (SELECT DISTINCT st.dataset_id FROM dict_tag t JOIN dataset_tag st " +
            "ON t.id = st.tag_id WHERE t.id = ?) AS st JOIN dict_dataset ds ON st.dataset_id = ds.id " +
            "ORDER BY ds.urn LIMIT ?, ?;";
    private final static String GET_DATASET_COUNT_BY_SOURCE_ID = "select count(id) as total from dict_dataset " +
            "where wh_etl_job_id= ? and parent_name is not null and char_length(trim(parent_name)) <> 0 ;";
    private final static String GET_TOTAL_DATASET_COUNT = "select count(id) as total from dict_dataset " +
            "where parent_name is not null and char_length(trim(parent_name)) <> 0 " +
            "AND parent_name <> 'null';";

    /**
     * Get data set total count by their data source ID.
     *
     * @param sourceId
     * @return
     */
    public Long getDatasetCountBySourceId(Integer sourceId) {

        Map<String, Object> res = getJdbcTemplate().queryForMap(GET_DATASET_COUNT_BY_SOURCE_ID, sourceId);
        return (Long) res.get("total");
    }

    /**
     * Get data set total count
     * @return
     */
    public Long getTotalDatasetCount() {
        Map<String, Object> res = getJdbcTemplate().queryForMap(GET_TOTAL_DATASET_COUNT);
        return (Long) res.get("total");
    }

    /**
     * Get data source statistics.
     *
     * @return null if no data source id exists.
     */
    public List<Map<String, Object>> getDataSourceStatistics() {

        List<Map<String, Object>> dataSourceLs = getDataSourceList();
        // check if get list of data source IDs is null or not
        if (null == dataSourceLs) {
            return null;
        }
        List<Map<String, Object>> res = Lists.newArrayList();
        for (Map dataSource : dataSourceLs) {

            // get datasource total by its source id
            Long total = getDatasetCountBySourceId((Integer) dataSource.get("data_source_id"));
            Map dataSourceNode = createDataSourceNode(total,
                    (String) dataSource.get("data_source_name"),
                    (Integer) dataSource.get("data_source_id"),
                    (String) dataSource.get("data_source_type"),
                    (Integer) dataSource.get("running"),
                    (String) dataSource.get("comments")
            );
            res.add(dataSourceNode);
        }
        return res;
    }

    /**
     * Get next level of tree nodes in the tree view.
     * When urn is a data source then its namespaces (dbs or schemas) are returned.
     * When urn is a namespace (db or schema) then its data set nodes are returned.
     *
     * @param urn
     * @param page        the current page number
     * @param limit       pagination size
     * @param isNamespace whether the urn is a Namespace level entity or not?
     * @return TreeNodes view object
     */
    public TreeNodes getSubNodesByURN(String urn, int page, int limit, boolean isNamespace) {

        logger.info("Query for sub nodes of URN: {}", urn);
        // id, urn, name, parent_name, alias, type
        List<Map<String, Object>> rows = getPagedTreeNodesByURN(urn, page, limit);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for (Map row : rows) {
            Long id = (Long) row.get("id");
            String nodeURN = (String) row.get("urn");
            String name = (String) row.get("name");
            String parentName = (String) row.get("parent_name");
            String alias = (String) row.get("alias");
            // distinguish between namespace node & data set node
            boolean isParent = isNamespace ?
                    null == id : Strings.isNullOrEmpty(parentName);
            String type = (String) row.get("type");
            // add in a new node
            treeNodes.add(new TreeNode(nodeURN, name, parentName, id,
                    alias, isParent, type, isNamespace));
        }
        return new TreeNodes(treeNodes);
    }

    /**
     * Get all tag nodes (TagResult).
     *
     * @return List of TagResult.
     */
    public List<TagResult> getTagNodes() {

        logger.info("Query for tag nodes.");
        // id, tagName, total
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(GET_TAG_NODES);
        List<TagResult> tagResults = Lists.newArrayList();
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            String tagName = (String) row.get("tagName");
            String tagColor = (String) row.get("tagColor");
            Long total = (Long) row.get("total");
            tagResults.add(new TagResult(id, tagName, tagColor, total));
        }
        return tagResults;
    }

    /**
     * Get paged data set nodes for a specific tag by its ID.
     *
     * @param tagID
     * @param page
     * @param limit
     * @return TreeNodes instance.
     */
    public TreeNodes getDataSetNodesByTagID(Integer tagID, int page, int limit) {

        logger.info("Query for data set of tag ID: {}", tagID);
        // id, urn, name, parent_name, alias, source
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(GET_PAGED_DATASET_NODES_BY_TAG_ID,
                tagID.intValue(), (page - 1) * limit, limit);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for (Map row : rows) {
            Long id = (Long) row.get("id");
            String nodeURN = (String) row.get("urn");
            String name = (String) row.get("name");
            String parentName = (String) row.get("parent_name");
            String alias = (String) row.get("alias");
            // distinguish between namespace node & data set node
            boolean isParent = Strings.isNullOrEmpty(parentName);
            String type = (String) row.get("type");
            // this is for front end display an intermediate node.
            // node is a namespace or not
            boolean isNamespace = false;
            // add in a new node
            treeNodes.add(new TreeNode(nodeURN, name, parentName, id,
                    alias, isParent, type, isNamespace));
        }
        return new TreeNodes(treeNodes);
    }

    /**
     * Help method for querying paged tree nodes list.
     *
     * @param urn URN string for querying
     * @return corresponding result to the urn string
     */
    private List<Map<String, Object>> getPagedTreeNodesByURN(String urn, int page, int limit) {

        return getJdbcTemplate().queryForList(GET_PAGED_TREENODES_BY_URN,
                urn,
                urn,
                urn,
                urn,
                urn,
                urn + "%",
                (page - 1) * limit,
                limit);
    }

    /**
     * Get list of data source's id to its name and type mapping.
     *
     * @return List of maps which contains data sources' id to name and type mappings.
     */
    private List<Map<String, Object>> getDataSourceList() {

        return getJdbcTemplate().queryForList(GET_DATASOURCE_LIST);
    }

    /**
     * * Helper method to create a data source node for final result.
     *
     * @param total          total number of this node's tables
     * @param dataSourceName
     * @param dataSourceID
     * @param dataSourceType
     * @param running
     * @return an data source node
     */
    private Map<String, Object> createDataSourceNode(Long total, String dataSourceName, Integer dataSourceID,
                                                     String dataSourceType, Integer running, String comments) {

        Map<String, Object> dataSourceNode = Maps.newHashMap();
        dataSourceNode.put("total", total);
        dataSourceNode.put("source", dataSourceName);
        dataSourceNode.put("id", dataSourceID);
        dataSourceNode.put("type", dataSourceType);
        dataSourceNode.put("running", running);
        dataSourceNode.put("lastRunError", comments != null);
        dataSourceNode.put("comments", comments);
        return dataSourceNode;
    }
}
