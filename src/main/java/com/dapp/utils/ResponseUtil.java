package com.dapp.utils;


import com.dapp.enums.ErrorEnum;
import com.dapp.enums.ResponseCodeEnum;
import com.dapp.exceptions.BaseException;
import com.dapp.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.util.Date;

@Slf4j
public class ResponseUtil {

    public static ResponseVo buildFailResponse(BaseException e){
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(e.getCode());
        responseVO.setMessage(e.getMessage());
        responseVO.setServerTime(new Date());
        return responseVO;
    }

    public static ResponseVo buildFailResponse(BindingResult bindingResult){
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(ErrorEnum.PARAM_ERROR.getCode());
        responseVO.setMessage(bindingResult.getFieldError().getDefaultMessage());
        responseVO.setServerTime(new Date());
        return responseVO;
    }

    public static ResponseVo buildFailResponse(){
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(ResponseCodeEnum.FAIL.getCode());
        responseVO.setMessage("system error");
        responseVO.setServerTime(new Date());
        return responseVO;
    }

    public static ResponseVo buildSuccessResponse(Object data){
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseVO.setServerTime(new Date());
        responseVO.setData(data);
        return responseVO;
    }

    public static ResponseVo buildSuccessResponse(){
        ResponseVo responseVO = new ResponseVo();
        responseVO.setCode(ResponseCodeEnum.SUCCESS.getCode());
        responseVO.setServerTime(new Date());
        responseVO.setData(null);
        return responseVO;
    }

}
