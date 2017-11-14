package com.cetc.hubble.metagrid.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/11.
 */
public class AddEdition {
    private String editionUuid;
    private String editionName;
    private String editionAudience;
    private String editionType = "2";
    private String editionCode;
    private String editionCodeJson;
    private Integer order = 1;
    private List<String> deleteEditionAttributeList = new ArrayList<>();
    private List<String> modifyEditionAttributeList = new ArrayList<>();
    private List<Map<String,Object>> editionAttributes;
    private List<Map<String,Object>> addEditionAttributeList;
    private Boolean open = true;
    private Integer count = 0;
    private Boolean visible = true;
    private Boolean editionOnly = false;

    public String getEditionUuid() {
        return editionUuid;
    }

    public void setEditionUuid(String editionUuid) {
        this.editionUuid = editionUuid;
    }

    public String getEditionName() {
        return editionName;
    }

    public void setEditionName(String editionName) {
        this.editionName = editionName;
    }

    public String getEditionAudience() {
        return editionAudience;
    }

    public void setEditionAudience(String editionAudience) {
        this.editionAudience = editionAudience;
    }

    public String getEditionType() {
        return editionType;
    }

    public void setEditionType(String editionType) {
        this.editionType = editionType;
    }

    public String getEditionCode() {
        return editionCode;
    }

    public void setEditionCode(String editionCode) {
        this.editionCode = editionCode;
    }

    public String getEditionCodeJson() {
        return editionCodeJson;
    }

    public void setEditionCodeJson(String editionCodeJson) {
        this.editionCodeJson = editionCodeJson;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<String> getDeleteEditionAttributeList() {
        return deleteEditionAttributeList;
    }

    public void setDeleteEditionAttributeList(List<String> deleteEditionAttributeList) {
        this.deleteEditionAttributeList = deleteEditionAttributeList;
    }

    public List<String> getModifyEditionAttributeList() {
        return modifyEditionAttributeList;
    }

    public void setModifyEditionAttributeList(List<String> modifyEditionAttributeList) {
        this.modifyEditionAttributeList = modifyEditionAttributeList;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getEditionOnly() {
        return editionOnly;
    }

    public void setEditionOnly(Boolean editionOnly) {
        this.editionOnly = editionOnly;
    }

    public List<Map<String, Object>> getAddEditionAttributeList() {
        return addEditionAttributeList;
    }

    public void setAddEditionAttributeList(List<Map<String, Object>> addEditionAttributeList) {
        this.addEditionAttributeList = addEditionAttributeList;
    }

    public List<Map<String, Object>> getEditionAttributes() {
        return editionAttributes;
    }

    public void setEditionAttributes(List<Map<String, Object>> editionAttributes) {
        this.editionAttributes = editionAttributes;
    }
}
