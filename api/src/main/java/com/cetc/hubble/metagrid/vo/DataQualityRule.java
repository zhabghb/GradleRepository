package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/3/23.
 */
public class DataQualityRule {

    private Integer id;
    private String ruleKey;
    private String ruleName;
    private String description;

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getRuleKey () {
        return ruleKey;
    }

    public void setRuleKey (String ruleKey) {
        this.ruleKey = ruleKey;
    }

    public String getRuleName () {
        return ruleName;
    }

    public void setRuleName (String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }


}
