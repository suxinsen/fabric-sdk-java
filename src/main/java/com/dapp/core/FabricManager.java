package com.dapp.core;

import com.dapp.entity.*;
import com.dapp.utils.LineUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * FabricManager管理器,包含两个简单部分，第一部分是项目配置，第二部分是调用方法。
 * @author suxinsen
 * @date 2019-04-10 14:15
 */
@Slf4j
public class FabricManager {

    private static FabricManager instance = null;
    private static ChannelExecutor channelExecutor = null;
    private static ClientExecutor clientExecutor = null;
    private static NetworkConfig networkConfig = null;
    private static HFClient hfClient = null;
    private static Channel channel= null;

    private static String channelName = "mychannel";
    private static String userName = "Admin";

    public static FabricManager obtain() {
        if (null == instance) {
            synchronized (FabricManager.class) {
                if (null == instance) {
                    instance = new FabricManager();
                }
            }
        }
        return instance;
    }

    private FabricManager() {
        try {
            // 初始化配置
            initConfig();
            // 配置chainCode executor
            initChainCodeExecutor();
            // 配置channel executor
            initChannelExecutor();
		} catch (IOException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (NetworkConfigurationException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initConfig() throws InvalidArgumentException, IOException, NetworkConfigurationException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        File configFile = ResourceUtils.getFile("classpath:crypto-config/config_tls.yaml");

        networkConfig = NetworkConfig.fromYamlFile(configFile);
        NetworkConfig.OrgInfo clientOrg = networkConfig.getClientOrganization();

        FabricUser user = getFabricUser(clientOrg);

        hfClient = HFClient.createNewInstance();
        hfClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        hfClient.setUserContext(user);
    }

    private void initChainCodeExecutor() {
        clientExecutor = new ClientExecutor();
    }

    private void initChannelExecutor() throws NetworkConfigurationException, InvalidArgumentException, TransactionException, IOException, ProposalException {
        channelExecutor = new ChannelExecutor();
        channel = hfClient.loadChannelFromConfig(channelName, networkConfig);
        channel.initialize();

        // add block register event
        channel.registerBlockListener(blockEvent -> {
            log.info(String.format("Receive block event (number %s) from %s", blockEvent.getBlockNumber(), blockEvent.getPeer()));
        });
    }

    private FabricUser getFabricUser(NetworkConfig.OrgInfo clientOrg) {
        // Persistence is not part of SDK.
        FabricUser user = new FabricUser();
        user.setMspId(clientOrg.getMspId());
        user.setName(userName);
        user.setOrganization(clientOrg.getName());
        user.setEnrollment(clientOrg.getPeerAdmin().getEnrollment());
        return user;
    }

    public void printChannelInfo() {
        LineUtil.Begin.Build();
        try {
            BlockchainInfo channelInfo = null;
            channelInfo = channel.queryBlockchainInfo();
            log.info("Channel height: " + channelInfo.getHeight());
            // 获取所有的区块信息
            for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
                BlockInfo returnedBlock = channel.queryBlockByNumber(current);
                final long blockNumber = returnedBlock.getBlockNumber();

                log.info(String.format("Block #%d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash())));
                log.info(String.format("Block #%d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash())));
                log.info(String.format("Block #%d has calculated block hash is %s",
                        blockNumber, Hex.encodeHexString(SDKUtils.calculateBlockHash(hfClient, blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash()))));
            }
        } catch (ProposalException e) {
            LineUtil.lineError(e);
        } catch (InvalidArgumentException e) {
            LineUtil.lineError(e);
        } catch (IOException e) {
            LineUtil.lineError(e);
        }
        LineUtil.End.Build();
    }

    public List<FabricPeer> queryPeerList() {
        Collection<Peer> peers = channel.getPeers();
        List<FabricPeer> peerList = new ArrayList<>();
        if (!peers.isEmpty()) {
            for (Peer peer : peers
                    ) {
                peerList.add(FabricPeer.builder()
                        .name(peer.getName())
                        .url(peer.getUrl())
                        .protocol(peer.getProtocol())
                        .properties(peer.getProperties())
                        .build());
            }
        }
        return peerList;
    }

    public List<FabricOrderer> queryOrdererList() {
        Collection<Orderer> orderers = channel.getOrderers();
        List<FabricOrderer> ordererList = new ArrayList<>();
        if (!orderers.isEmpty()) {
            for (Orderer order : orderers
                 ) {
                ordererList.add(FabricOrderer.builder()
                        .name(order.getName())
                        .url(order.getUrl())
                        .properties(order.getProperties())
                        .build());
            }
        }
        return ordererList;
    }

    public List<String> installChainCode(String codePath, String chainCodeName, String version) {
        try {
            File file = ResourceUtils.getFile("classpath:");
            clientExecutor.setChainCodeName(chainCodeName);
            clientExecutor.setVersion(version);
            return clientExecutor.installChainCode(hfClient,channel,codePath, file);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ChaincodeEndorsementPolicyParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ChaincodeCollectionConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FabricChaincodeInfo> queryInstalledChainCode(String peerHostName) {
        try {
            List<Query.ChaincodeInfo> chaincodeInfoList = clientExecutor.queryInstalledChainCode(hfClient, channel, peerHostName);
            List<FabricChaincodeInfo> chaincodeInfos = new ArrayList<>();
            if (Objects.nonNull(chaincodeInfoList)) {
                for (Query.ChaincodeInfo chainCodeInfo : chaincodeInfoList
                     ) {
                    FabricChaincodeInfo build = FabricChaincodeInfo.builder()
                            .id(Hex.encodeHexString(chainCodeInfo.getId().toByteArray()))
                            .name(chainCodeInfo.getName())
                            .path(chainCodeInfo.getPath())
                            .version(chainCodeInfo.getVersion())
                            .escc(chainCodeInfo.getEscc())
                            .vscc(chainCodeInfo.getVscc())
                            .build();
                    chaincodeInfos.add(build);
                }
            }
            return chaincodeInfos;
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FabricChaincodeInfo> queryInstantiateChainCode(String peerHostName) {
        try {
            List<Query.ChaincodeInfo> chaincodeInfoList =  channelExecutor.queryInstantiateChainCode(channel, peerHostName);
            List<FabricChaincodeInfo> chaincodeInfos = new ArrayList<>();
            if (Objects.nonNull(chaincodeInfoList)) {
                for (Query.ChaincodeInfo chainCodeInfo : chaincodeInfoList
                        ) {
                    FabricChaincodeInfo build = FabricChaincodeInfo.builder()
                            .id(Hex.encodeHexString(chainCodeInfo.getId().toByteArray()))
                            .name(chainCodeInfo.getName())
                            .path(chainCodeInfo.getPath())
                            .version(chainCodeInfo.getVersion())
                            .escc(chainCodeInfo.getEscc())
                            .vscc(chainCodeInfo.getVscc())
                            .build();
                    chaincodeInfos.add(build);
                }
            }
            return chaincodeInfos;
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> upgradeChainCode(String chainCodeName, String version, ArrayList<String> args) {
        try {
            clientExecutor.setChainCodeName(chainCodeName);
            clientExecutor.setVersion(version);
            return channelExecutor.upgradeChainCode(hfClient,channel,args);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> instantiateChainCode(String chainCodeName, String version, String func, ArrayList<String> args, File endorsementFile) {
        try {
            clientExecutor.setChainCodeName(chainCodeName);
            clientExecutor.setVersion(version);
            return channelExecutor.instantiateChainCode(hfClient, channel, func, args, endorsementFile);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ChaincodeEndorsementPolicyParseException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> chainCodeInvoke(String chainCodeName, String version, String func, ArrayList<String> args) {
        try {
            clientExecutor.setChainCodeName(chainCodeName);
            clientExecutor.setVersion(version);
            return channelExecutor.chainCodeInvoke(hfClient, channel, func, args);
        } catch (InvalidArgumentException e) {
            LineUtil.lineError(e);
        } catch (ProposalException e) {
            LineUtil.lineError(e);
        } catch (InterruptedException e) {
            LineUtil.lineError(e);
        } catch (ExecutionException e) {
            LineUtil.lineError(e);
        } catch (TimeoutException e) {
            LineUtil.lineError(e);
        }
        return null;
    }

    public FabricBlockInfo queryBlockByHash(String hash) {
        try {
            return channelExecutor.queryBlockByHash(channel, hash);
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FabricBlockInfo queryBlockByNumber(long num) {
        try {
            return channelExecutor.queryBlockByNumber(channel, num);
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FabricBlockInfo queryBlockByTransactionID(String txId) {
        try {
            return channelExecutor.queryBlockByTransactionID(channel, txId);
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FabricTransactionInfo
    queryTransactionByID(String txId) {
        try {
            return channelExecutor.queryTransactionByID(channel, txId);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FabricBlockchainInfo queryBlockchainInfo() {
        try {
            return channelExecutor.queryBlockchainInfo(channel);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        } catch (ProposalException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> chainCodeQuery(String chainCodeName, String version, String func, ArrayList<String> args) {
        try {
            channelExecutor.setChainCodeName(chainCodeName);
            channelExecutor.setVersion(version);
            return channelExecutor.chainCodeQuery(hfClient, channel, func, args);
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
