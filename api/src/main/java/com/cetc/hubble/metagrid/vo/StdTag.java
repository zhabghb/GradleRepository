package com.cetc.hubble.metagrid.vo;

/**
 * Created by ben on 17-5-25.
 */
public class StdTag {
    private Integer id;
    private String tagName;
    private Integer totalExpected;
    private Integer totalMatched;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getTotalExpected() {
        return totalExpected;
    }

    public void setTotalExpected(Integer totalExpected) {
        this.totalExpected = totalExpected;
    }

    public Integer getTotalMatched() {
        return totalMatched;
    }

    public void setTotalMatched(Integer totalMatched) {
        this.totalMatched = totalMatched;
    }

    @Override
    public String toString () {
        return "StdTag{" +
                "id=" + id +
                ", name='" + tagName + '\'' +
                ", totalExpected='" + totalExpected + '\'' +
                ", totalMatched=" + totalMatched +
                '}';
    }
}
