package com.dapp.entity;

import lombok.*;

import java.util.Properties;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 13:58
 * describe:
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FabricOrderer {

    private String name;
    private String url;
    private String channelName;
    private Properties properties;

}
