package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/15.
 */
public class DagTaskProperties {

    private String id;
    private String type;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    @Override
    public String toString () {
        return "DagTaskProperties{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
