package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * hdfs 文件属性
 */
public class HdfsFileAttribute {

    private Integer id;

    private Integer sourceId;

    private String keyword;

    private String path;

    private String fileName;

    @ApiModelProperty(name = "文件属性ID", example = "3")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ApiModelProperty(name = "文件属性所属数据源ID", example = "3")
    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    @ApiModelProperty(name = "文件关键字", example = "name")
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @ApiModelProperty(name = "文件路径", example = "/test/")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @ApiModelProperty(name = "文件名称", example = "test.txt")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
