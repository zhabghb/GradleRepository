package com.cetc.hubble.metagrid.exception;

public enum ErrorCode {

    NO_CONTENT(204, 204),BAD_REQUEST(400, 400), UNAUTHORIZED(401, 401), FORBIDDEN(403, 403), NOT_FOUND(404, 404),
    CONFLICT(409, 409), STATUS_ERROR(406, 406),INTERNAL_SERVER_ERROR(500, 500), CUSTOM_EXCEPTION(1000, 500),
    NO_TOKEN(1102, 401);

    public int code;
    public int httpStatus;

    ErrorCode(int code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

}
