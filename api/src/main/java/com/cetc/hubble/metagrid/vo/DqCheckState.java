package com.cetc.hubble.metagrid.vo;

/**
 * Created by dahey on 2016-06-15.
 */
public enum DqCheckState {
    /**
     * 状态：
     * 0:等待检测，1:正在检测，2：检测成功，3：检测失败
     */
    WAITING(0),RUNNING(1),SUCCESS(2),ERROR(3),DELETED(4);
    public int state;

    DqCheckState (int state){
        this.state = state;
    }

    public static String getStatus(int state) {
        for (DqCheckState s:DqCheckState.values()) {
            if (s.state == state){
                return s.name();
            }
        }
        return null;
    }




}
