package com.cetc.hubble.metagrid.vo;

/**
 * Created by jinyi on 17-8-3.
 */
public class HdfsLocatedBlockLocation {
    private String hostName;

    public HdfsLocatedBlockLocation(String hostname) {
        this.hostName = hostname;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostname) {
        this.hostName = hostname;
    }
}
