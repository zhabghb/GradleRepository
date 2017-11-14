package com.cetc.hubble.metagrid.vo;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 数据质量调用artflow接口所需传递参数
 *
 * Created by dahey on 2017/2/15.
 */
public class ArtFlowDAG {

    private String id;
    private String schedule;
    private String is_active;
    private String name;
    private List<DagNodes> nodes;
    private List<FrontModelEdge> edges = Lists.newArrayList();
    private String desc;


    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getSchedule () {
        return schedule;
    }

    public void setSchedule (String schedule) {
        this.schedule = schedule;
    }

    public String getIs_active () {
        return is_active;
    }

    public void setIs_active (String is_active) {
        this.is_active = is_active;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public List<DagNodes> getNodes () {
        return nodes;
    }

    public void setNodes (List<DagNodes> nodes) {
        this.nodes = nodes;
    }

    public List<FrontModelEdge> getEdges () {
        return edges;
    }

    public void setEdges (List<FrontModelEdge> edges) {
        this.edges = edges;
    }

    public String getDesc () {
        return desc;
    }

    public void setDesc (String desc) {
        this.desc = desc;
    }

    @Override
    public String toString () {
        return "ArtFlowDAG{" +
                "id='" + id + '\'' +
                ", schedule='" + schedule + '\'' +
                ", is_active='" + is_active + '\'' +
                ", name='" + name + '\'' +
                ", nodes=" + nodes +
                ", edges=" + edges +
                ", desc='" + desc + '\'' +
                '}';
    }
}
