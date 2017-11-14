package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.Tag;
import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.tree.RowMapper;
import java.util.List;
import java.util.Map;

/**
 * Created by tao on 16-10-27.
 */
@Repository
@Transactional
public class TagDAO extends AbstractMySQLOpenSourceDAO {

    private static Logger logger = LoggerFactory.getLogger(TagDAO.class);
    private static String INSERT_A_TAG = "INSERT INTO dict_tag (tag_name, tag_color) VALUES (?,?);";
    private static String INSERT_A_TAG_WITH_ID = "INSERT INTO dict_tag (id,tag_name) VALUES (?,?)";
    private static String LIST_ALL_TAGS = "SELECT id, tag_name, tag_color FROM dict_tag ORDER BY create_time desc;";
    private static String DELETE_A_TAG = "DELETE FROM dict_tag WHERE id = ?;";
    private static String DELETE_TAG_MAPPING = "DELETE FROM dataset_tag WHERE tag_id = ?;";
    private static String UPDATE_A_TAG = "UPDATE dict_tag SET tag_name = ?, tag_color = ? WHERE id = ?;";

    /**
     * Add in a new tag.
     *
     * @param tag
     * @return true if insert successful, vise versa.
     */
    @Transactional
    public boolean addTag(Tag tag) {

        boolean res = false;
        int row = getJdbcTemplate().update(INSERT_A_TAG, tag.getTagName(), tag.getTagColor());
        if (row > 0) res = true;
        return res;
    }

    /**
     * Add in a new tag.
     *
     * @param
     * @return true if insert successful, vise versa.
     */
    @Transactional
    public boolean addTag(int tagID, String tagName) {

        boolean res = false;
        int row = getJdbcTemplate().update(INSERT_A_TAG_WITH_ID, tagID, tagName);
        if (row > 0) res = true;
        return res;
    }

    /**
     * List all tags in the dict_tag table.
     *
     * @return List of all tags.
     */
    public List<Tag> listAllTags() {

        List tags = Lists.newArrayList();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(LIST_ALL_TAGS);
        for (Map row : rows) {
            Integer id = (Integer) row.get("id");
            String tagName = (String) row.get("tag_name");
            String tagColor = (String) row.get("tag_color");
            tags.add(new Tag(id, tagName, tagColor));
        }
        return tags;
    }

    /**
     * Delete a specific tag by id.
     *
     * @param tagID
     * @return true if delete successful, vise versa.
     */
    @Transactional
    public boolean deleteTag(Integer tagID) {

        int mappingRow = getJdbcTemplate().update(DELETE_TAG_MAPPING, tagID);
        int row = getJdbcTemplate().update(DELETE_A_TAG, tagID);
        // if mappingRow and row is larger than zero, then succeeded
        return mappingRow > 0 && row > 0;
    }

    /**
     * Update a tag by its id.
     *
     * @param tag
     * @return
     */
    @Transactional
    public boolean updateTag(Tag tag) {

        boolean res = false;
        int row = getJdbcTemplate().update(UPDATE_A_TAG, tag.getTagName(),
                tag.getTagColor(), tag.getId());
        if (row > 0) {
            res = true;
        }
        return res;
    }
}
