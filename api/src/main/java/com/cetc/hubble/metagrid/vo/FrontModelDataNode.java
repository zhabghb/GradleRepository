package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016/12/12.
 */
public class FrontModelDataNode {
    private String type;
    private int left;
    private int top;
    private int w;
    private int h;
    private String id;
    private String text;
    private FrontModelData data;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public int getLeft () {
        return left;
    }

    public void setLeft (int left) {
        this.left = left;
    }

    public int getTop () {
        return top;
    }

    public void setTop (int top) {
        this.top = top;
    }

    public int getW () {
        return w;
    }

    public void setW (int w) {
        this.w = w;
    }

    public int getH () {
        return h;
    }

    public void setH (int h) {
        this.h = h;
    }

    public String getText () {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }

    public FrontModelData getData () {
        return data;
    }

    public void setData (FrontModelData data) {
        this.data = data;
    }

    @Override
    public String toString () {
        return "FrontModelDataNode{" +
                "type='" + type + '\'' +
                ", left=" + left +
                ", top=" + top +
                ", w=" + w +
                ", h=" + h +
                ", text='" + text + '\'' +
                ", data=" + data +
                '}';
    }
}
