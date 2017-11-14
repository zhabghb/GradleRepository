package com.cetc.hubble.metagrid.vo;

import java.util.List;

/**
 * Created by dahey on 2016/10/11.
 * <p>
 * 目录树参数
 */

public class TreeNodes {

    public TreeNodes() {
    }

    public TreeNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }

    private List<TreeNode> nodes;

    public List<TreeNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }
}
