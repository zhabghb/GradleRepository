package com.cetc.hubble.metagrid.vo;

import java.util.Date;

/**
 * Created by dahey on 2016/12/12.
 */
public class DataModel {
    private Integer id;
    private String name;
    private String alias;
    private Date createtime;
    private String owner;
    private String tag;

    public DataModel () {
    }

    public DataModel (String name, Date createtime, String owner,String tag) {
        this.name = name;
        this.createtime = createtime;
        this.owner = owner;
        this.tag = tag;
    }
    public DataModel (String name,Date createtime, String owner) {
        this.name = name;
        this.createtime = createtime;
        this.owner = owner;
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getAlias () {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }

    public Date getCreatetime () {
        return createtime;
    }

    public void setCreatetime (Date createtime) {
        this.createtime = createtime;
    }

    public String getOwner () {
        return owner;
    }

    public void setOwner (String owner) {
        this.owner = owner;
    }

    public String getTag () {
        return tag;
    }

    public void setTag (String tag) {
        this.tag = tag;
    }

    @Override
    public String toString () {
        return "DataModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", createtime=" + createtime +
                ", owner='" + owner + '\'' +
                '}';
    }
}
