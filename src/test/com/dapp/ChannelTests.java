package com.dapp;

import com.dapp.core.FabricManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: SuXinSen
 * @date: 2019/4/11
 * @time: 15:56
 * describe:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelTests {

    @Test
    public void queryBlockByHash() {
        FabricManager.obtain().queryBlockByHash("0cfab68dcf23f00fc1fc63e2886349dd7d8d9bdb436f89b92c006fbdac856947");
    }

    @Test
    public void queryBlockByNumber() {
        FabricManager.obtain().queryBlockByNumber(14);
    }
    @Test
    public void queryBlockByTransactionID() {
        FabricManager.obtain().queryBlockByTransactionID("a037981f8aca6a5587ff88e428e3037698aaf2eb8eede4a3fa8dae7a8e3e41f0");
    }

    @Test
    public void queryTransactionByID() {
        FabricManager.obtain().queryTransactionByID("a037981f8aca6a5587ff88e428e3037698aaf2eb8eede4a3fa8dae7a8e3e41f0");
    }

    @Test
    public void queryBlockchainInfo() {
        FabricManager.obtain().queryBlockchainInfo();
    }

}
