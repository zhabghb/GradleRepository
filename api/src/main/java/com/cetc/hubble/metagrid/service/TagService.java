package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.dao.TagDAO;
import com.cetc.hubble.metagrid.vo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tao on 16-10-27.
 */
@Service
public class TagService {

    @Autowired
    private TagDAO tagDAO;

    /**
     * Encapsulation of DAO's add tag method.
     *
     * @param tag
     * @return
     */
    public boolean addTag(Tag tag) {

        return tagDAO.addTag(tag);
    }

    /**
     * Encapsulation of DAO's list all tags method.
     *
     * @return All tags in a list.
     */
    public List<Tag> list() {

        return tagDAO.listAllTags();
    }

    /**
     * Encapsulation of DAO's delete tag method.
     *
     * @param id tag id
     * @return
     */
    public boolean delete(Integer id) {

        return tagDAO.deleteTag(id);
    }

    /**
     * Encapsulation of DAO's update tag method.
     *
     * @param tag
     * @return
     */
    public boolean update(Tag tag) {

        return tagDAO.updateTag(tag);
    }
}
