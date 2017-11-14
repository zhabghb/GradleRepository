package com.cetc.hubble.metagrid.vo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 数据质量调用artflow接口所需传递参数
 *
 * Created by dahey on 2017/2/15.
 */
@Component
public class DagOperatorProperties implements Cloneable {


    @Value("${metagrid.datacheck.className}")
    private String className;
    @Value("${metagrid.datacheck.jarName}")
    private String jarName;
    @Value("${metagrid.datacheck.master}")
    private String master;
    @Value("${metagrid.datacheck.sparkApiPort}")
    private String sparkApiPort;
    //根据需要传递spark-submit提供的一些额外的选项，比如package，exclude-packages
    @Value("${metagrid.datacheck.sparkoption}")
    private String sparkOption;

    private String runOption;

    public String getClassName () {
        return className;
    }

    public void setClassName (String className) {
        this.className = className;
    }

    public String getJarName () {
        return jarName;
    }

    public void setJarName (String jarName) {
        this.jarName = jarName;
    }

    public String getMaster () {
        return master;
    }

    public void setMaster (String master) {
        this.master = master;
    }

    public String getRunOption () {
        return runOption;
    }

    public void setRunOption (String runOption) {
        this.runOption = runOption;
    }

    public String getSparkOption () {
        return sparkOption;
    }

    public void setSparkOption (String sparkOption) {
        this.sparkOption = sparkOption;
    }

    public String getSparkApiPort () {
        return sparkApiPort;
    }

    public void setSparkApiPort (String sparkApiPort) {
        this.sparkApiPort = sparkApiPort;
    }

    @Override
    public Object clone () throws CloneNotSupportedException {
        return super.clone();
    }
}
