package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2017/2/15.
 */
public class DagNodes {

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

    @Override
    public String toString () {
        return "DagNodes{" +
                "task_properties=" + task_properties +
                ", operator_properties=" + operator_properties +
                '}';
    }
}


