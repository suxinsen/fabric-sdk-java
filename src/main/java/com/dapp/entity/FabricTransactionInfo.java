package com.dapp.entity;

import lombok.*;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 11:16
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricTransactionInfo {

    private String envelope;
    private String transactionId;
    private String validationCode;
    private String processedTransaction;

}
