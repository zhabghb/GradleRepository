package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by jinyi on 17-5-24.
 */
public class StdIdentifierWrapper {

    @ApiModelProperty(value = "id")
    private int id;

    @NotNull(message = "字段标识符不能为空")
    @Size(min = 1, max = 255, message = "字段标识符范围1-255字符")
    @ApiModelProperty(value = "字段标识符", required = true)
    private String identifier;

    @NotNull(message = "中文名称不能为空")
    @Size(min = 1, max = 255, message = "中文名称范围1-255字符")
    @ApiModelProperty(value = "中文名称", required = true)
    private String chName;

    @Size(max = 255, message = "国标编码范围1-255字符")
    @ApiModelProperty(value = "国标编码")
    private String internalIdentifier;

    @Size(max = 255, message = "国标文件编号范围1-255字符")
    @ApiModelProperty(value = "国标文件编号")
    private String gatCodex;

    /*@NotNull(message = "审批时间不能为空")
    @NotBlank(message = "审批时间不能为空")
    @Pattern(regexp = ValidateOperator.REGEXP_YYYYMMDD, message = "审批时间格式不符合'年-月-日'")
    @ApiModelProperty(value = "审批时间,格式:yyyy-MM-dd", required = true)
    private String approvalDate;*/

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    /*public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInternalIdentifier() {
        return internalIdentifier;
    }

    public void setInternalIdentifier(String internalIdentifier) {
        this.internalIdentifier = internalIdentifier;
    }

    public String getGatCodex() {
        return gatCodex;
    }

    public void setGatCodex(String gatCodex) {
        this.gatCodex = gatCodex;
    }
}
