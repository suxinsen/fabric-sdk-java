//package com.dapp;
//
//import com.dapp.core.ChainCodeExecutor;
//import com.dapp.entity.FabricUser;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Hex;
//import org.hyperledger.fabric.sdk.*;
//import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
//import org.hyperledger.fabric.sdk.exception.ProposalException;
//import org.hyperledger.fabric.sdk.security.CryptoSuite;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.util.ResourceUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.Random;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeoutException;
//
///**
// * @author: SuXinSen
// * @date: 2019/4/8
// * @time: 11:37
// * describe:
// */
//@Slf4j
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class Tests {
//
//    private static final long waitTime = 6000;
//
//    private static String channelName = "mychannel";
//    private static String userName = "Admin";
//    private static String secret = "User@1234";
//    private static String chaincodeName = "mycc";
//    private static String chaincodeVersion = "1.0.0";
//
//    @Test
//    public void init() {
//        try {
//            File configFile = ResourceUtils.getFile("classpath:crypto-config/config_tls.yaml");
//
//            NetworkConfig networkConfig = NetworkConfig.fromYamlFile(configFile);
//            NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();
//
////            NetworkConfig.CAInfo caInfo = clientOrg.getCertificateAuthorities().get(0);
////            FabricUser user = getFabricUserWithCa(clientOrg, privateKeyFile, certificateFile);
//
//            FabricUser user = getFabricUser(clientOrg);
//
//            HFClient client = HFClient.createNewInstance();
//            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
//            client.setUserContext(user);
//
//            Channel channel = client.loadChannelFromConfig(channelName, networkConfig);
//
//            //service discovery function.
//            //Peer p = channel.getPeers().iterator().next();
//            //channel.removePeer(p);
//            //channel.addPeer(p, Channel.PeerOptions.createPeerOptions().addPeerRole(Peer.PeerRole.SERVICE_DISCOVERY));
//            //Collection<String> cc = channel.getDiscoveredChaincodeNames();
//
//            channel.initialize();
//
//            channel.registerBlockListener(blockEvent -> {
//                log.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(), blockEvent.getPeer()));
//            });
//            printChannelInfo(client, channel);
//            //executeChainCode(client, channel);
//
//            log.info("Shutdown channel.");
//            channel.shutdown(true);
//        } catch (Exception e) {
//            log.error("exception", e);
//        }
//    }
//
//    private static void lineBreak() {
//        log.info("=============================================================");
//    }
//
//    private static void executeChainCode(HFClient client, Channel channel) throws
//            ProposalException, InvalidArgumentException, UnsupportedEncodingException, InterruptedException,
//            ExecutionException, TimeoutException
//    {
//        lineBreak();
//        ChainCodeExecutor executer = new ChainCodeExecutor(chaincodeName, chaincodeVersion);
//
//        String newValue = String.valueOf(new Random().nextInt(1000));
//        executer.executeTransaction(client, channel, true,"set", "baas", newValue);
//        executer.executeTransaction(client, channel, false,"query", "baas");
//
//        lineBreak();
//        newValue = String.valueOf(new Random().nextInt(1000));
//        executer.executeTransaction(client, channel, true,"set", "baas", newValue);
//        executer.executeTransaction(client, channel, false,"query", "baas");
//
//    }
//    private static void printChannelInfo(HFClient client, Channel channel) throws
//            ProposalException, InvalidArgumentException, IOException
//    {
//        lineBreak();
//        BlockchainInfo channelInfo = channel.queryBlockchainInfo();
//
//        log.info("Channel height: " + channelInfo.getHeight());
//        for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
//            BlockInfo returnedBlock = channel.queryBlockByNumber(current);
//            final long blockNumber = returnedBlock.getBlockNumber();
//
//            log.info(String.format("Block #%d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash())));
//            log.info(String.format("Block #%d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash())));
//            log.info(String.format("Block #%d has calculated block hash is %s",
//                    blockNumber, Hex.encodeHexString(SDKUtils.calculateBlockHash(client,blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash()))));
//        }
//
//    }
//
////    private static FabricUser getFabricUserFromCa(NetworkConfig.OrgInfo clientOrg, NetworkConfig.CAInfo caInfo) throws
////            MalformedURLException, org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException, InfoException,
////            EnrollmentException
////    {
////        HFCAClient hfcaClient = HFCAClient.createNewInstance(caInfo);
////        HFCAInfo cainfo = hfcaClient.info();
////        lineBreak();
////        log.info("CA name: " + cainfo.getCAName());
////        log.info("CA version: " + cainfo.getVersion());
////
////        // Persistence is not part of SDK.
////
////        log.info("Going to enroll user: " + userName);
////        Enrollment enrollment = hfcaClient.enroll(userName, secret);
////        log.info("Enroll user: " + userName +  " successfully.");
////
////        FabricUser user = new FabricUser();
////        user.setMspId(clientOrg.getMspId());
////        user.setName(userName);
////        user.setOrganization(clientOrg.getName());
////        user.setEnrollment(enrollment);
////        return user;
////    }
//
//    private static FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg) {
//        lineBreak();
//
//        // Persistence is not part of SDK.
//
//        FabricUser user = new FabricUser();
//        user.setMspId(clientOrg.getMspId());
//        user.setName(userName);
//        user.setOrganization(clientOrg.getName());
//        user.setEnrollment(clientOrg.getPeerAdmin().getEnrollment());
//        return user;
//    }
//
//}
