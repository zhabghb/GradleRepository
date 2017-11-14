package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/3/12.
 */
public class StdTable {
    private Integer id;
    private String code;
    private String comment;
    private Integer dataVolume;
    private boolean match;
    private Long matchedDatasetId;
//    private List<SlimDataset> datasets;

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getCode () {
        return code;
    }

    public void setCode (String code) {
        this.code = code;
    }

    public String getComment () {
        return comment;
    }

    public void setComment (String comment) {
        this.comment = comment;
    }

    public Integer getDataVolume () {
        return dataVolume;
    }

    public void setDataVolume (Integer dataVolume) {
        this.dataVolume = dataVolume;
    }

    public boolean isMatch () {
        return match;
    }

    public void setMatch (boolean match) {
        this.match = match;
    }

    public Long getMatchedDatasetId () {
        return matchedDatasetId;
    }

    public void setMatchedDatasetId (Long matchedDatasetId) {
        this.matchedDatasetId = matchedDatasetId;
    }

    @Override
    public String toString () {
        return "StdTable{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", comment='" + comment + '\'' +
                ", dataVolume=" + dataVolume +
                ", match=" + match +
                ", matchedDatasetId=" + matchedDatasetId +
                '}';
    }
}
