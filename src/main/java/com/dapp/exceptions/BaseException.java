package com.dapp.exceptions;


import com.dapp.enums.ErrorEnum;

public class BaseException extends RuntimeException {

    private int code;

    public BaseException() {
    }

    public BaseException(ErrorEnum errorEnum) {
        this(errorEnum.getCode(),errorEnum.getMessage());
    }

    public BaseException(int code, String message) {
        super(message);
        this.code=code;
    }

    public BaseException(int code) {
        this.code = code;
    }

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BaseException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public BaseException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
