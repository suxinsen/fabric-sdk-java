package com.dapp;

import com.dapp.core.FabricManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;

/**
 * @author: SuXinSen
 * @date: 2019/4/10
 * @time: 15:05
 * describe:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChainCodeTests {

    @Test
    public void printChannelInfo() {
        FabricManager.obtain().printChannelInfo();
    }

    @Test
    public void chainCodeExecuteQuery() {
        ArrayList<String> args = new ArrayList<>();
        args.add("n");
        FabricManager.obtain().chainCodeQuery("cc09","1.0","query",args);
    }

    @Test
    public void chainCodeExecuteInvoke() {
        ArrayList<String> args = new ArrayList<>();
        args.add("m");
        args.add("n");
        args.add("20");
        FabricManager.obtain().chainCodeInvoke("cc09","1.0","invoke",args);
    }

    @Test
    public void installChainCode() {
        FabricManager.obtain().installChainCode("github/com/chaincode/cc02/go/", "cc_ticket","1.0");
    }

    @Test
    public void queryInstalledChainCode() {
        FabricManager.obtain().queryInstalledChainCode("peer0.org1.example.com");
    }

    @Test
    public void queryInstantiateChainCode() {
        FabricManager.obtain().queryInstantiateChainCode("peer0.org1.example.com");
    }

    @Test
    public void upgradeChainCode() {
        ArrayList<String> args = new ArrayList<>();
        args.add("sxs");
        args.add("98");
        FabricManager.obtain().upgradeChainCode("cc01","1.1",args);
    }

    @Test
    public void instantiateChainCode() {
        ArrayList<String> args = new ArrayList<>();
        args.add("sxs");
        args.add("98");
        File endorsementFile = new File("src/main/resources/endorsement/123.yaml");
        FabricManager.obtain().instantiateChainCode("cc_ticket", "1.0", "init",args, endorsementFile);
        //FabricManager.obtain().instantiateChainCode("cc_account", "1.0", "init", "sxs", "100");
    }
}
