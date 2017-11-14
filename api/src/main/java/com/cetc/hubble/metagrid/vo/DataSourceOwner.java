/**
 * Created by dahey on 2017/02/22.
 *
 */
package com.cetc.hubble.metagrid.vo;

public class DataSourceOwner {

    private Integer dataSourceId;
    private String name;
    private String owner;
    private String ownerTEL;
    private String ownerPlatform;
    private String ownerDepartment;

    public DataSourceOwner (Integer dataSourceId, String name, String owner, String ownerTEL, String ownerPlatform, String ownerDepartment) {
        this.dataSourceId = dataSourceId;
        this.name = name;
        this.owner = owner;
        this.ownerTEL = ownerTEL;
        this.ownerPlatform = ownerPlatform;
        this.ownerDepartment = ownerDepartment;
    }

    public DataSourceOwner () {
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getOwner () {
        return owner;
    }

    public void setOwner (String owner) {
        this.owner = owner;
    }

    public String getOwnerTEL () {
        return ownerTEL;
    }

    public void setOwnerTEL (String ownerTEL) {
        this.ownerTEL = ownerTEL;
    }

    public String getOwnerPlatform () {
        return ownerPlatform;
    }

    public void setOwnerPlatform (String ownerPlatform) {
        this.ownerPlatform = ownerPlatform;
    }

    public String getOwnerDepartment () {
        return ownerDepartment;
    }

    public void setOwnerDepartment (String ownerDepartment) {
        this.ownerDepartment = ownerDepartment;
    }

    public Integer getDataSourceId () {
        return dataSourceId;
    }

    public void setDataSourceId (Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
}
