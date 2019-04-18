package com.dapp.core;

import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 14:55
 * describe:
 */
public class Excutor {

    private static Logger logger = LoggerFactory.getLogger(Excutor.class);

    public void parseResp(Collection<ProposalResponse> propResp, List<ProposalResponse> successful, List<ProposalResponse> failed, List<String> resList) throws InvalidArgumentException {
        for (ProposalResponse response : propResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                String payload = new String(response.getChaincodeActionResponsePayload());
                logger.info(String.format("[√] Got success response from peer %s => payload: %s", response.getPeer().getName(), payload));
                successful.add(response);
                resList.add(payload);
            } else {
                String status = response.getStatus().toString();
                String msg = response.getMessage();
                logger.warn(String.format("[×] Got failed response from peer %s => %s: %s ", response.getPeer().getName(), status, msg));
                failed.add(response);
                resList.add(msg);
            }
        }
    }

}
