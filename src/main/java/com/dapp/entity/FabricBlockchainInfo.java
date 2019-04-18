package com.dapp.entity;

import lombok.*;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 11:19
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricBlockchainInfo {

    private long height;
    private String currentBlockHash;
    private String previousBlockHash;
}
