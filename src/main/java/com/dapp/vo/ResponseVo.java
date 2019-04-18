package com.dapp.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseVo<T> {

    private int code;
    private String message;
    private Date serverTime;
    private T data;
}
