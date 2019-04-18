package com.dapp.entity;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

/**
 * @author: SuXinSen
 * @date: 2019/4/8
 * @time: 12:40
 * describe:
 */
public class FabricEnrollement implements Enrollment, Serializable {

    private static final long serialVersionUID = -2784835212445309006L;
    private final PrivateKey privateKey;
    private final String certificate;

    public FabricEnrollement(PrivateKey privateKey, String certificate) {

        this.certificate = certificate;

        this.privateKey = privateKey;
    }

    @Override
    public PrivateKey getKey() {

        return privateKey;
    }

    @Override
    public String getCert() {
        return certificate;
    }

}
