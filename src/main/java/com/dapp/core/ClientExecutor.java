/*
 *  Copyright 2018 Aliyun.com All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.dapp.core;

import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.protos.peer.Query;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.ChaincodeCollectionConfigurationException;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Data
public class ClientExecutor extends Excutor{
    private static final Log logger = LogFactory.getLog(ClientExecutor.class);
    private static final String TEST_FIXTURES_PATH = "src/main/resources";

    private String chainCodeName;
    private String version;
    private ChaincodeID ccId;
    private long waitTime = 60000;

    public ClientExecutor() {}

    /**
     * 安装链码 需要指定链码 语言，名称，版本，路径，请求等待时间
     * @param client
     * @param channel
     * @param chainCodeSource
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws IOException
     * @throws ChaincodeEndorsementPolicyParseException
     * @throws ChaincodeCollectionConfigurationException
     */
    public List<String> installChainCode(HFClient client, Channel channel, String codePath, File chainCodeSource) throws InvalidArgumentException, ProposalException, IOException, ChaincodeEndorsementPolicyParseException, ChaincodeCollectionConfigurationException {
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        ChaincodeID.Builder chainCodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chainCodeName)
                .setVersion(version);
        ccId = chainCodeIDBuilder.build();
        installProposalRequest.setChaincodeID(ccId);
        installProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
        installProposalRequest.setChaincodePath(codePath);
        installProposalRequest.setChaincodeSourceLocation(chainCodeSource);
        installProposalRequest.setProposalWaitTime(waitTime);
        Collection<ProposalResponse> installProposalResp = client.sendInstallProposal(installProposalRequest, channel.getPeers());

        List<ProposalResponse> successful = new LinkedList();
        List<ProposalResponse> failed = new LinkedList();
        List<String> resList = new LinkedList();
        parseResp(installProposalResp, successful, failed, resList);
        return resList;
    }

    /**
     * 查询已安装的链码
     * @param client
     * @param channel
     * @param peerHostName
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public List<Query.ChaincodeInfo> queryInstalledChainCode(HFClient client, Channel channel, String peerHostName) throws ProposalException, InvalidArgumentException {
        Collection<Peer> peers = channel.getPeers();
        Iterator<Peer> iterator = peers.iterator();
        while (iterator.hasNext()) {
            Peer peer = iterator.next();
            String name = peer.getName();
            if (peerHostName.equals(name)) {
                List<Query.ChaincodeInfo> chainCodeList = client.queryInstalledChaincodes(peer);
                chainCodeList.stream().forEach(chainCode -> {
                    logger.info(chainCode.toString());
                });
                return chainCodeList;
            }
        }
        return null;
    }

}
