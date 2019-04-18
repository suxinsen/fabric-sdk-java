package com.dapp.entity;

import lombok.*;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 13:39
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricChaincodeInfo {

    private String id;
    private String name;
    private String version;
    private String path;
    private String escc;
    private String vscc;

}
