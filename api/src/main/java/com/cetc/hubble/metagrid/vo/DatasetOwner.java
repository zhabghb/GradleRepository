/**
 * Created by dahey on 2017/02/20.
 */
package com.cetc.hubble.metagrid.vo;

public class DatasetOwner {

    private String urn;
    private String name;
    private String owner;
    private String ownerTEL;
    private String ownerPlatform;
    private String ownerDepartment;

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

    public String getUrn () {
        return urn;
    }

    public void setUrn (String urn) {
        this.urn = urn;
    }
}
