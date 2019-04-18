package com.dapp.core;

import com.dapp.entity.FabricBlockInfo;
import com.dapp.entity.FabricBlockchainInfo;
import com.dapp.entity.FabricTransactionInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author: SuXinSen
 * @date: 2019/4/10
 * @time: 14:26
 * describe:
 */
@Data
public class ChannelExecutor extends Excutor{
    private static final String TEST_FIXTURES_PATH = "src/main/resources";
    private String chainCodeName;
    private String version;
    private ChaincodeID ccId;

    private long waitTime = 60000;
    private static final Log logger = LogFactory.getLog(ClientExecutor.class);

    public FabricBlockInfo queryBlockByHash(Channel channel, String hash) throws ProposalException, InvalidArgumentException, InvalidProtocolBufferException {
        BlockInfo blockInfo = channel.queryBlockByHash(Hex.decode(hash));
        return parseBlockInfo(blockInfo);
    }

    public FabricBlockInfo queryBlockByNumber(Channel channel, long num) throws ProposalException, InvalidArgumentException, InvalidProtocolBufferException {
        BlockInfo blockInfo = channel.queryBlockByNumber(num);
        return parseBlockInfo(blockInfo);
    }

    public FabricBlockInfo queryBlockByTransactionID(Channel channel, String txId) throws ProposalException, InvalidArgumentException, InvalidProtocolBufferException {
        BlockInfo blockInfo = channel.queryBlockByTransactionID(txId);
        return parseBlockInfo(blockInfo);
    }

    public FabricTransactionInfo queryTransactionByID(Channel channel, String txId) throws InvalidArgumentException, ProposalException {
        TransactionInfo transactionInfo = channel.queryTransactionByID(txId);
        return parseTransactionInfo(transactionInfo);
    }

    public FabricBlockchainInfo queryBlockchainInfo(Channel channel) throws InvalidArgumentException, ProposalException {
        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
        return parseBlockchainInfo(blockchainInfo);
    }

    public List<String> chainCodeQuery(HFClient client, Channel channel, String func, ArrayList<String> args) throws ProposalException, InvalidArgumentException {
        ChaincodeID.Builder chainCodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chainCodeName)
                .setVersion(version);
        ccId = chainCodeIDBuilder.build();
        QueryByChaincodeRequest queryByChaincodeRequest = QueryByChaincodeRequest.newInstance(client.getUserContext());
        Map<String, byte[]> tm = new HashMap<>(3);
        tm.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm);
        queryByChaincodeRequest.setChaincodeID(ccId);

        queryByChaincodeRequest.setFcn(func);
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        Collection<ProposalResponse> proposalResponses = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());
        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        List<String> resList = new LinkedList();
        parseResp(proposalResponses, successful, failed, resList);
        return resList;
    }

    /**
     * 执行链码
     * @param client
     * @param channel
     * @param func
     * @param args
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public List<String> chainCodeInvoke(HFClient client, Channel channel, String func, ArrayList<String> args)
            throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException, TimeoutException {
        ChaincodeID.Builder chainCodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chainCodeName)
                .setVersion(version);
        ccId = chainCodeIDBuilder.build();
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(ccId);
        transactionProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        transactionProposalRequest.setFcn(func);
        transactionProposalRequest.setArgs(args);
        transactionProposalRequest.setProposalWaitTime(waitTime);
        Map<String, byte[]> tm = new HashMap<>(3);
        tm.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        transactionProposalRequest.setTransientMap(tm);
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        List<String> resList = new LinkedList();
        parseResp(transactionPropResp, successful, failed, resList);

        sendTransaction(channel, successful);
        return resList;
    }

    /**
     * 更新链码 需要先安装新版本的链码，然后指定链码新版本号和名称进行更新，只能对已经实例化的链码进行更新，需要指定新版的初始化参数
     * @param client
     * @param channel
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws UnsupportedEncodingException
     */
    public List<String> upgradeChainCode(HFClient client, Channel channel, ArrayList<String> args)
            throws InvalidArgumentException, ProposalException, InterruptedException, ExecutionException, TimeoutException {
        UpgradeProposalRequest upgradeProposalRequest = client.newUpgradeProposalRequest();
        ChaincodeID.Builder chainCodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chainCodeName)
                .setVersion(version);
        ccId = chainCodeIDBuilder.build();
        upgradeProposalRequest.setChaincodeID(ccId);
        upgradeProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        upgradeProposalRequest.setChaincodePath("github.com/chaincode/cc01/go/");
        upgradeProposalRequest.setProposalWaitTime(waitTime);
        upgradeProposalRequest.setArgs(args);
        Map<String, byte[]> tm = new HashMap<>(3);
        tm.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        upgradeProposalRequest.setTransientMap(tm);
        Collection<ProposalResponse> upgradeProposalResp = channel.sendUpgradeProposal(upgradeProposalRequest, channel.getPeers());

        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        List<String> resList = new LinkedList();
        parseResp(upgradeProposalResp, successful, failed, resList);
        sendTransaction(channel, successful);
        return resList;
    }

    /**
     * 查询已实例化的链码
     * @param channel
     * @param peerHostName
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public List<Query.ChaincodeInfo> queryInstantiateChainCode(Channel channel, String peerHostName) throws ProposalException, InvalidArgumentException {
        Collection<Peer> peers = channel.getPeers();
        Iterator<Peer> iterator = peers.iterator();
        while (iterator.hasNext()) {
            Peer peer = iterator.next();
            String name = peer.getName();
            if (peerHostName.equals(name)) {
                List<Query.ChaincodeInfo> chainCodeList = channel.queryInstantiatedChaincodes(peer);
                chainCodeList.stream().forEach(chainCode -> {
                    logger.info(chainCode.toString());
                });
                return chainCodeList;
            }
        }
        return null;
    }

    /**
     * 实例化链码，指定链码 名称，版本，初始化函数，参数，语言
     * @param client
     * @param channel
     * @param func
     * @param args
     * @throws InvalidArgumentException
     * @throws IOException
     * @throws ChaincodeEndorsementPolicyParseException
     * @throws ProposalException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public List<String> instantiateChainCode(HFClient client, Channel channel, String func, ArrayList<String> args, File endorsementPolicy) throws InvalidArgumentException, IOException, ChaincodeEndorsementPolicyParseException, ProposalException, InterruptedException, ExecutionException, TimeoutException {
        ChaincodeID.Builder chainCodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chainCodeName)
                .setVersion(version);
        ccId = chainCodeIDBuilder.build();
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setChaincodeID(ccId);
        instantiateProposalRequest.setProposalWaitTime(waitTime);
        instantiateProposalRequest.setFcn(func);
        instantiateProposalRequest.setArgs(args);
        instantiateProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        Map<String, byte[]> tm = new HashMap<>(3);
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(endorsementPolicy);
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        Collection<ProposalResponse> proposalResponses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        List<String> resList = new LinkedList();
        parseResp(proposalResponses, successful, failed, resList);
        sendTransaction(channel, successful);
        return resList;
    }

    public void sendTransaction(Channel channel, List<ProposalResponse> successful) throws InterruptedException, ExecutionException, TimeoutException {
        logger.info("Sending transaction to orderers...");
        channel.sendTransaction(successful).thenApply(transactionEvent -> {
            logger.info("Orderer response: txid" + transactionEvent.getTransactionID());
            logger.info("Orderer response: block number: " + transactionEvent.getBlockEvent().getBlockNumber());
            return null;
        }).exceptionally(e -> {
            logger.error("Orderer exception happened: ", e);
            return null;
        }).get(waitTime, TimeUnit.SECONDS);
    }

    public FabricBlockInfo parseBlockInfo(BlockInfo blockInfo) throws InvalidProtocolBufferException {
        logger.info("            Data：" + blockInfo.getBlock());
        logger.info("        DataHash：" + Hex.toHexString(blockInfo.getDataHash()));
        logger.info("       ChannelId：" + blockInfo.getChannelId());
        logger.info("     BlockNumber：" + blockInfo.getBlockNumber());
        logger.info("    PreviousHash：" + Hex.toHexString(blockInfo.getPreviousHash()));
        logger.info("   EnvelopeCount：" + blockInfo.getEnvelopeCount());
        logger.info("TransactionCount：" + blockInfo.getTransactionCount());
        FabricBlockInfo fabricBlockInfo = FabricBlockInfo.builder()
                .index(blockInfo.getBlockNumber())
                .hash(Hex.toHexString(blockInfo.getDataHash()))
                .previousHash(Hex.toHexString(blockInfo.getPreviousHash()))
                .envelopeCount(blockInfo.getEnvelopeCount())
                .transactionCount(blockInfo.getTransactionCount())
                .transActionsMetaData(new String(blockInfo.getTransActionsMetaData()))
                .channelId(blockInfo.getChannelId()).build();
        return fabricBlockInfo;

    }

    public FabricTransactionInfo parseTransactionInfo(TransactionInfo transactionInfo) {
        logger.info("            Envelope：" + transactionInfo.getEnvelope().toString());
        logger.info("       TransactionID：" + transactionInfo.getTransactionID());
        logger.info("      ValidationCode：" + transactionInfo.getValidationCode().toString());
        logger.info("ProcessedTransaction：" + transactionInfo.getProcessedTransaction().toString());
        FabricTransactionInfo fabricTransactionInfo = FabricTransactionInfo.builder()
                .envelope(transactionInfo.getEnvelope().toString())
                .transactionId(transactionInfo.getTransactionID())
                .validationCode(transactionInfo.getValidationCode().toString())
                .processedTransaction(transactionInfo.getProcessedTransaction().toString())
                .build();
        return fabricTransactionInfo;
    }

    public FabricBlockchainInfo parseBlockchainInfo(BlockchainInfo blockchainInfo) {
        logger.info("           Height：" + blockchainInfo.getHeight());
        logger.info(" CurrentBlockHash：" + Hex.toHexString(blockchainInfo.getCurrentBlockHash()));
        logger.info("PreviousBlockHash：" + Hex.toHexString(blockchainInfo.getPreviousBlockHash()));
        FabricBlockchainInfo fabricBlockchainInfo = FabricBlockchainInfo.builder()
                .height(blockchainInfo.getHeight())
                .currentBlockHash(Hex.toHexString(blockchainInfo.getCurrentBlockHash()))
                .previousBlockHash(Hex.toHexString(blockchainInfo.getPreviousBlockHash()))
                .build();
        return fabricBlockchainInfo;
    }

}
