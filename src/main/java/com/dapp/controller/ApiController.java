package com.dapp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dapp.core.FabricManager;
import com.dapp.entity.*;
import com.dapp.enums.ErrorEnum;
import com.dapp.exceptions.BaseException;
import com.dapp.utils.GenUUID;
import com.dapp.utils.JSONUtil;
import com.dapp.utils.ResponseUtil;
import com.dapp.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: SuXinSen
 * @date: 2019/4/15
 * @time: 10:47
 * describe:
 */
@Slf4j
@RestController
public class ApiController {

    private static final String INVOKE = "invoke";
    private static final String CHAINCODE = "chaincode";
    private static final String GOLANG = "go";
    private static final String YAML = "yaml";
    private static final String INSTALL_PATH = "src/main/resources/src/github.com/chaincode/";
    private static final String ENDORSEMENT_PATH = "src/main/resources/endorsement/";
    public static long PHOTO_LIMIT_SIZE = 1024 * 1024 * 2;

    @RequestMapping(value = "/list/peer", method = RequestMethod.GET)
    public ResponseVo peers() {
        List<FabricPeer> peers = FabricManager.obtain().queryPeerList();
        return ResponseUtil.buildSuccessResponse(peers);
    }

    @RequestMapping(value = "/list/orderer", method = RequestMethod.GET)
    public ResponseVo orderers() {
        List<FabricOrderer> orderers = FabricManager.obtain().queryOrdererList();
        return ResponseUtil.buildSuccessResponse(orderers);
    }

    @RequestMapping(value = "/list/installed/{peerHostName}", method = RequestMethod.GET)
    public ResponseVo installed(@PathVariable String peerHostName) {
        List<FabricChaincodeInfo> chainCodeInfoList = FabricManager.obtain().queryInstalledChainCode(peerHostName);
        return ResponseUtil.buildSuccessResponse(chainCodeInfoList);
    }

    @RequestMapping(value = "/list/instantiated/{peerHostName}", method = RequestMethod.GET)
    public ResponseVo instantiated(@PathVariable String peerHostName) {
        List<FabricChaincodeInfo> chainCodeInfoList = FabricManager.obtain().queryInstantiateChainCode(peerHostName);
        return ResponseUtil.buildSuccessResponse(chainCodeInfoList);
    }

    @RequestMapping(value = "/chaincode/{chainCodeName}/{version}/{func}/{args}", method = RequestMethod.GET)
    public ResponseVo chainCodeInvoke(@PathVariable String chainCodeName, @PathVariable String version, @PathVariable String func, @PathVariable String args) {
        try {
            JSONObject jsonObject= JSON.parseObject(args);
            ArrayList<String> argsList = JSONUtil.jsonToArrayCollection(jsonObject);
            if (INVOKE.equals(func)) {
                List<String> codeInvoke = FabricManager.obtain().chainCodeInvoke(chainCodeName, version, func, argsList);
                return ResponseUtil.buildSuccessResponse(codeInvoke);
            }
            List<String> codeQuery = FabricManager.obtain().chainCodeQuery(chainCodeName, version, func, argsList);
            return ResponseUtil.buildSuccessResponse(codeQuery);
        } catch (Exception e) {
            log.error("chainCodeInvoke-fail, error info:{}", new Object[]{e.getMessage()});
            return ResponseUtil.buildFailResponse();
        }
    }

    @RequestMapping(value = "/block/{type}/{value}", method = RequestMethod.GET)
    public ResponseVo block(@PathVariable String type, @PathVariable String value) {
        FabricBlockInfo fabricBlockInfo;
        if (FabricBlockInfo.TypeEum.HASH.type().equals(type)) {
            fabricBlockInfo = FabricManager.obtain().queryBlockByHash(value);
        } else if (FabricBlockInfo.TypeEum.NUMBER.type().equals(type)) {
            fabricBlockInfo = FabricManager.obtain().queryBlockByNumber(Long.parseLong(value));
        } else if (FabricBlockInfo.TypeEum.TRANSACTION_ID.type().equals(type)) {
            fabricBlockInfo = FabricManager.obtain().queryBlockByTransactionID(value);
        } else {
            return ResponseUtil.buildFailResponse();
        }
        return ResponseUtil.buildSuccessResponse(fabricBlockInfo);
    }

    @RequestMapping(value = "/transaction/{id}", method = RequestMethod.GET)
    public ResponseVo transaction(@PathVariable String id) {
        FabricTransactionInfo transaction = FabricManager.obtain().queryTransactionByID(id);
        return ResponseUtil.buildSuccessResponse(transaction);
    }

    @RequestMapping(value = "/blockchain/info", method = RequestMethod.GET)
    public ResponseVo blockchain() {
        FabricBlockchainInfo blockchainInfo = FabricManager.obtain().queryBlockchainInfo();
        return ResponseUtil.buildSuccessResponse(blockchainInfo);
    }

    @RequestMapping(value = "/chaincode/install", method = RequestMethod.POST)
    public ResponseVo install(HttpServletRequest request) {
        ResponseVo responseVo;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        String version = multipartRequest.getParameter("version");
        String chainCodeName = multipartRequest.getParameter("chainCodeName");
        if (Objects.isNull(file)) {
            responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.FILE_IS_NULL));
        }else if (file.getSize() > PHOTO_LIMIT_SIZE){
            responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.FILE_IS_EARGE));
        }else{
            String genPath = GenUUID.uuid();
            File installDir = new File(INSTALL_PATH + genPath);
            if (!installDir.exists()) {
                installDir.mkdir();
            }
            String filename = CHAINCODE + "." + GOLANG;
            try{
                File chaincodeFile = new File(INSTALL_PATH + genPath + filename);
                file.transferTo(chaincodeFile);
                List<String> resList = FabricManager.obtain().installChainCode("github.com/chaincode/" + genPath, chainCodeName, version);
                if (resList != null) {
                    responseVo = ResponseUtil.buildSuccessResponse(filename);
                } else {
                    responseVo = ResponseUtil.buildFailResponse();
                }
            }catch (Exception e){
                log.error("执行 [chaincode/install] 失败, error info:{}",new Object[]{e.toString()});
                responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.INSTALL_ERROR));
            }
        }
        return responseVo;
    }

    @RequestMapping(value = "/chaincode/instantiate", method = RequestMethod.POST)
    public ResponseVo instantiate(HttpServletRequest request) {
        ResponseVo responseVo;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        String version = multipartRequest.getParameter("version");
        String chainCodeName = multipartRequest.getParameter("chainCodeName");
        String func = multipartRequest.getParameter("func");
        String args = multipartRequest.getParameter("args");
        JSONObject jsonObject= JSON.parseObject(args);
        ArrayList<String> argsList = JSONUtil.jsonToArrayCollection(jsonObject);
        if (Objects.isNull(file)) {
            responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.FILE_IS_NULL));
        }else if (file.getSize() > PHOTO_LIMIT_SIZE){
            responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.FILE_IS_EARGE));
        }else{
            String filename = GenUUID.uuid() + "." + YAML;
            try{
                File endorsementFile = new File(ENDORSEMENT_PATH + filename);
                file.transferTo(endorsementFile);
                List<String> resList = FabricManager.obtain().instantiateChainCode(chainCodeName, version, func, argsList, endorsementFile);
                if (resList != null) {
                    responseVo = ResponseUtil.buildSuccessResponse(filename);
                } else {
                    responseVo = ResponseUtil.buildFailResponse();
                }
            }catch (Exception e){
                log.error("执行 [chaincode/install] 失败, error info:{}",new Object[]{e.toString()});
                responseVo = ResponseUtil.buildFailResponse(new BaseException(ErrorEnum.INSTALL_ERROR));
            }
        }
        return responseVo;
    }






}
