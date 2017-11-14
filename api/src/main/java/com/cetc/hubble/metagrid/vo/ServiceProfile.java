package com.cetc.hubble.metagrid.vo;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by cyq on 2017/7/11.
 */
public class ServiceProfile {
    private String serviceId;
    private long editionVersion;
    private List<String> addEditionCommonList;
    private List<String> modifyEditionCommonList;
    private List<String> deleteEditionCommonList;
    private List<AddEdition> addEditionList;
    private List<ModifyEdition> modifyEditionList;


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getEditionVersion() {
        return editionVersion;
    }

    public void setEditionVersion(long editionVersion) {
        this.editionVersion = editionVersion;
    }

    public List<AddEdition> getAddEditionList() {
        return addEditionList;
    }

    public void setAddEditionList(List<AddEdition> addEditionList) {
        this.addEditionList = addEditionList;
    }

    public List<String> getAddEditionCommonList() {
        return addEditionCommonList;
    }

    public void setAddEditionCommonList(List<String> addEditionCommonList) {
        this.addEditionCommonList = addEditionCommonList;
    }

    public List<String> getModifyEditionCommonList() {
        return modifyEditionCommonList;
    }

    public void setModifyEditionCommonList(List<String> modifyEditionCommonList) {
        this.modifyEditionCommonList = modifyEditionCommonList;
    }

    public List<String> getDeleteEditionCommonList() {
        return deleteEditionCommonList;
    }

    public void setDeleteEditionCommonList(List<String> deleteEditionCommonList) {
        this.deleteEditionCommonList = deleteEditionCommonList;
    }

    public List<ModifyEdition> getModifyEditionList() {
        return modifyEditionList;
    }

    public void setModifyEditionList(List<ModifyEdition> modifyEditionList) {
        this.modifyEditionList = modifyEditionList;
    }
}
