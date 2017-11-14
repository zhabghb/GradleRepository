package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/15.
 */
public class DagNodesResult {

    private  String task_type;
    private  String task_id;
    private  String task_id_var;
    private  String task_conf;

    private DagTaskProperties task_properties;

    private DagOperatorProperties operator_properties;

    public DagTaskProperties getTask_properties () {
        return task_properties;
    }

    public void setTask_properties (DagTaskProperties task_properties) {
        this.task_properties = task_properties;
    }

    public DagOperatorProperties getOperator_properties () {
        return operator_properties;
    }

    public void setOperator_properties (DagOperatorProperties operator_properties) {
        this.operator_properties = operator_properties;
    }

    public String getTask_type () {
        return task_type;
    }

    public void setTask_type (String task_type) {
        this.task_type = task_type;
    }

    public String getTask_id () {
        return task_id;
    }

    public void setTask_id (String task_id) {
        this.task_id = task_id;
    }

    public String getTask_id_var () {
        return task_id_var;
    }

    public void setTask_id_var (String task_id_var) {
        this.task_id_var = task_id_var;
    }

    public String getTask_conf () {
        return task_conf;
    }

    public void setTask_conf (String task_conf) {
        this.task_conf = task_conf;
    }

    @Override
    public String toString () {
        return "DagNodes{" +
                "task_properties=" + task_properties +
                ", operator_properties=" + operator_properties +
                '}';
    }
}


