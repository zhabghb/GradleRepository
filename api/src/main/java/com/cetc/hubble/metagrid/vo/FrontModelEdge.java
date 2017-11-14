package com.cetc.hubble.metagrid.vo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by dahey on 2016/12/12.
 */
public class FrontModelEdge {
    private String source;
    private String target;
    private Map data;

    public String getSource () {
        return source;
    }

    public void setSource (String source) {
        this.source = source;
    }

    public String getTarget () {
        return target;
    }

    public void setTarget (String target) {
        this.target = target;
    }

    public Map getData () {
        return data;
    }

    public void setData (Map data) {
        this.data = data;
    }

    @Override
    public String toString () {
        return "ModelEdge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", data=" + data +
                '}';
    }
}
