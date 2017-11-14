package com.cetc.hubble.metagrid.controller;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.service.TagService;
import com.cetc.hubble.metagrid.vo.Tag;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v2/api/tags/")
@Api(value = "/api", description = "标签API列表")
public class TagController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TagService tagService;

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "新增标签")
    @ApiResponses({
            @ApiResponse(code = 400, message = "标签已存在"),
            @ApiResponse(code = 500, message = "新增标签失败")
    })
    public void add(@RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) Tag tag) {

        logger.info("add tag : " + tag.getTagName());
        try {
            boolean res = tagService.addTag(tag);
        } catch (DuplicateKeyException de) {
            logger.error("标签已存在", tag.getTagName());
            throw new AppException("标签已存在", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed to add a tag: ", e);
            throw new AppException("新增标签失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "标签列表")
    @ApiResponses({
            @ApiResponse(code = 500, message = "查询标签列表失败")
    })
    public List<Tag> list() {

        logger.info("List all tags.");
        List tags;
        try {
            tags = tagService.list();
        } catch (Exception e) {
            logger.error("Failed to list all tags: ", e);
            throw new AppException("查询标签列表失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return tags;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标签")
    @ApiResponses({
            @ApiResponse(code = 500, message = "删除标签失败")
    })
    public void delete(@PathVariable @ApiParam(value = "标签ID", required = true) Integer id) {

        logger.info("delete tag id = " + id);
        try {
            boolean res = tagService.delete(id);
        } catch (Exception e) {
            logger.error("Failed to delete a tag: ", e);
            throw new AppException("删除标签失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标签")
    @ApiResponses({
            @ApiResponse(code = 400, message = "标签已存在"),
            @ApiResponse(code = 500, message = "修改标签失败")
    })
    public void update(@PathVariable @ApiParam(value = "标签ID", required = true) Integer id,
                       @RequestBody @ApiParam(value = "输入参数，JSON格式", required = true) Tag tag) {

        logger.info("update tag id = " + id);
        try {
            boolean res = tagService.update(tag);
        } catch (DuplicateKeyException de) {
            logger.error("标签已存在", tag.getTagName());
            throw new AppException("标签已存在", ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Failed to update a tag: ", e);
            throw new AppException("修改标签失败", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}