package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/12/12.
 */
public class DataModelRelationType {
    private Integer id;
    private String enName;
    private String chName;
    private String description;
    private String icon;
    private Integer enabled;

    public DataModelRelationType() {
    }

    public DataModelRelationType(Integer id, String enName, String chName,
                                 String description, String icon, Integer enabled) {
        this.id = id;
        this.enName = enName;
        this.chName = chName;
        this.description = description;
        this.icon = icon;
        this.enabled = enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "DataModelRelationType{" +
                "id=" + id +
                ", enName='" + enName + '\'' +
                ", chName='" + chName + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
