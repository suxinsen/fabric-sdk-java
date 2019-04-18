package com.dapp.utils;

import java.util.UUID;

/**
 * Copyright(c),2018-2020,http://www.dappworks.cn.
 * @FileName GenUUID.java
 * 给app用户生成id
 * @author 刘建军
 * @Time 2018/6/12
 * @version 1.0.0
 * */
public class GenUUID {

    /**
     * 生成uuid
     * @return uuid
     * */
    public static String uuid(){
        /**获取UUID并转化为String对象*/
        String uuid = UUID.randomUUID().toString();
        /**因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可*/
        uuid = uuid.replace("-", "");
        return uuid;
    }

    /**
     * 生成16位的uuid
     * @return
     */
    public static String uuid_16(){
        /**获取UUID并转化为String对象*/
        String uuid = UUID.randomUUID().toString();
        /**因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可*/
        uuid = uuid.replace("-", "").substring(16);
        return uuid;
    }

}
