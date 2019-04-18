package com.dapp.enums;

public enum ErrorEnum {
    FILE_IS_NULL(10000, "file is null"),
    PARAM_ERROR(10001, "param error"),
    FILE_IS_EARGE(10002, "file is too large"),
    INSTALL_ERROR(10003, "install error"),
    KEYWORDS_ERROR(10004, "invalid keyWords");

    private int code;
    private String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
