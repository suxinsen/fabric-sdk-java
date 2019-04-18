package com.dapp.entity;

import lombok.*;

import java.util.Properties;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 14:03
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricPeer {

    private String name;
    private String url;
    private String protocol;
    private Properties properties;

}
