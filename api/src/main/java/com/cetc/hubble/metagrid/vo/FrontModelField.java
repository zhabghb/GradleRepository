package com.cetc.hubble.metagrid.vo;

import java.util.Date;

/**
 * Created by dahey on 2016/12/12.
 * TODO  为了将就前端多加几个冗余字段，以后要优化
 */
public class FrontModelField {
    private String id;
    private boolean checked;
    private Long fieldID;
    private String name;
    private String comment;
    private String type;
    private String gatCodex;
    private String internalIdentifier;
    private String ruleName;
    private Date lastCheckTime;
    private Date nextCheckTime;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public boolean isChecked () {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }

    public Long getFieldID () {
        return fieldID;
    }

    public void setFieldID (Long fieldID) {
        this.fieldID = fieldID;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getGatCodex() {
        return gatCodex;
    }

    public void setGatCodex(String gatCodex) {
        this.gatCodex = gatCodex;
    }

    public String getInternalIdentifier() {
        return internalIdentifier;
    }

    public void setInternalIdentifier(String internalIdentifier) {
        this.internalIdentifier = internalIdentifier;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Date getNextCheckTime() {
        return nextCheckTime;
    }

    public void setNextCheckTime(Date nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }

    @Override
    public String toString() {
        return "FrontModelField{" +
                "id='" + id + '\'' +
                ", checked=" + checked +
                ", fieldID=" + fieldID +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                ", gatCodex='" + gatCodex + '\'' +
                ", internalIdentifier='" + internalIdentifier + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", lastCheckTime=" + lastCheckTime +
                ", nextCheckTime=" + nextCheckTime +
                '}';
    }
}
