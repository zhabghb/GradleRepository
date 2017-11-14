package com.cetc.hubble.metagrid.exception;

/**
 * Created by yuson on 2016-05-04.
 */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = -8634700792767837033L;

    public ErrorCode errorCode;
    public Object errorData;

    public AppException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
//        this.getStackTrace().
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getErrorData() {
        return errorData;
    }
}
