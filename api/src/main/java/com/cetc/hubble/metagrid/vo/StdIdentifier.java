package com.cetc.hubble.metagrid.vo;

import java.util.Date;

/**
 * Created by dahey on 2017/3/21.
 */
public class StdIdentifier {
    private Long id;
    private String internalIdentifier;
    private String identifier;
    private String chName;
    private String enName;
    private String version;
    private String descripton;
    private String status;
    private String submitInstitution;
    private Date approvalDate;
    private String remark;
    private String gatCodex;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getInternalIdentifier () {
        return internalIdentifier;
    }

    public void setInternalIdentifier (String internalIdentifier) {
        this.internalIdentifier = internalIdentifier;
    }

    public String getIdentifier () {
        return identifier;
    }

    public void setIdentifier (String identifier) {
        this.identifier = identifier;
    }

    public String getChName () {
        return chName;
    }

    public void setChName (String chName) {
        this.chName = chName;
    }

    public String getEnName () {
        return enName;
    }

    public void setEnName (String enName) {
        this.enName = enName;
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public String getDescripton () {
        return descripton;
    }

    public void setDescripton (String descripton) {
        this.descripton = descripton;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getSubmitInstitution () {
        return submitInstitution;
    }

    public void setSubmitInstitution (String submitInstitution) {
        this.submitInstitution = submitInstitution;
    }

    public Date getApprovalDate () {
        return approvalDate;
    }

    public void setApprovalDate (Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRemark () {
        return remark;
    }

    public void setRemark (String remark) {
        this.remark = remark;
    }

    public String getGatCodex () {
        return gatCodex;
    }

    public void setGatCodex (String gatCodex) {
        this.gatCodex = gatCodex;
    }
}
