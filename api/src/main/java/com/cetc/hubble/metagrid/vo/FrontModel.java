package com.cetc.hubble.metagrid.vo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by dahey on 2016/12/12.
 */
public class FrontModel {
    private ArrayList<FrontModelDataNode> nodes;
    private ArrayList<FrontModelEdge> edges;
    private ArrayList<Map> ports;
    private ArrayList groups;

    public ArrayList<FrontModelDataNode> getNodes () {
        return nodes;
    }

    public void setNodes (ArrayList<FrontModelDataNode> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<FrontModelEdge> getEdges () {
        return edges;
    }

    public void setEdges (ArrayList<FrontModelEdge> edges) {
        this.edges = edges;
    }

    public ArrayList getPorts () {
        return ports;
    }

    public void setPorts (ArrayList ports) {
        this.ports = ports;
    }

    public ArrayList getGroups () {
        return groups;
    }

    public void setGroups (ArrayList groups) {
        this.groups = groups;
    }

    @Override
    public String toString () {
        return "FrontModel{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", ports=" + ports +
                ", groups=" + groups +
                '}';
    }
}
