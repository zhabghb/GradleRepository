package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/3/23.
 */
public class DqAnalyseParam {

    private Long datasetId;
    private String tableName;
    private String parent;
    private Integer ruleId;
    private String ruleKey;
    private Integer jobId;
    private String dataType;
    private boolean checked;


    public Long getDatasetId () {
        return datasetId;
    }

    public void setDatasetId (Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getTableName () {
        return tableName;
    }

    public void setTableName (String tableName) {
        this.tableName = tableName;
    }

    public String getParent () {
        return parent;
    }

    public void setParent (String parent) {
        this.parent = parent;
    }

    public Integer getRuleId () {
        return ruleId;
    }

    public void setRuleId (Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleKey () {
        return ruleKey;
    }

    public void setRuleKey (String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public Integer getJobId () {
        return jobId;
    }

    public void setJobId (Integer jobId) {
        this.jobId = jobId;
    }

    public String getDataType () {
        return dataType;
    }

    public void setDataType (String dataType) {
        this.dataType = dataType;
    }

    public boolean isChecked () {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }


    @Override
    public String toString () {
        return "DqAnalyseParam{" +
                "datasetId=" + datasetId +
                ", tableName='" + tableName + '\'' +
                ", parent='" + parent + '\'' +
                ", ruleId=" + ruleId +
                ", ruleKey='" + ruleKey + '\'' +
                ", jobId=" + jobId +
                ", dataType='" + dataType + '\'' +
                ", checked=" + checked +
                '}';
    }
}
