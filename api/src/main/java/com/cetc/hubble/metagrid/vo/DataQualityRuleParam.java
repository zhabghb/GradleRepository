package com.cetc.hubble.metagrid.vo;

/**
 * Created by cyq on 2017/9/27.
 * 数据质量规则参数
 */
public class DataQualityRuleParam {

    private Integer id;

    private String name;

    private String optionValue;

    private Integer ruleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }
}
