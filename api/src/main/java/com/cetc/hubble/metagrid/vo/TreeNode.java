package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/10/12.
 * <p>
 * 目录树
 */
public class TreeNode {

    private Long datasetId;
    private String urn;
    private String treeNodename;
    private String parentName;
    private String sourceName;
    private String alias;
    private boolean isParent;
    private boolean checked;
    private String type;
    // actually isNamespace stands for front end to decide whether the current namespace
    // level node is a leaf, in other words, whether it is able to be expanded.
    private boolean isNamespace;

    // nodeURN, name, parentName, id,
    // alias, isParent, type, isNamespace
    public TreeNode(String urn, String treeNodename, String parentName, Long datasetId,
                    String alias, boolean isParent, String type, boolean isNamespace) {
        this.urn = urn;
        this.treeNodename = treeNodename;
        this.parentName = parentName;
        this.datasetId = datasetId;
        this.alias = alias;
        this.isParent = isParent;
        this.type = type;
        this.isNamespace = isNamespace;
    }

    public TreeNode () {
    }

    public boolean isChecked () {
        return checked;
    }

    public void setChecked (boolean checked) {
        this.checked = checked;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getTreeNodename() {
        return treeNodename;
    }

    public void setTreeNodename(String treeNodename) {
        this.treeNodename = treeNodename;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsNamespace() {
        return isNamespace;
    }

    public void setIsNamespace(boolean namespace) {
        this.isNamespace = namespace;
    }
}
