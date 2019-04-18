package com.dapp.entity;

import lombok.*;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 11:07
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricBlockInfo {

    private long index;
    private String hash;
    private String previousHash;
    private int envelopeCount;
    private int transactionCount;
    private String transActionsMetaData;
    private String channelId;


    public enum TypeEum {

        HASH("hash"), NUMBER("number"), TRANSACTION_ID("transaction_id");

        private String type;

        TypeEum(String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }

    }

}
