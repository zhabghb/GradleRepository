package com.cetc.hubble.metagrid.controller.support;

/**
 * Created by dahey on 2017/5/25.
 */
public class ResponseEntity {

    private Object results;

    public ResponseEntity(Object results) {
        this.results = results;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }
}
