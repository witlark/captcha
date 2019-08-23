package com.selfimpr.captcha.exception;


import com.selfimpr.captcha.exception.code.ServiceExceptionCode;

/**
 * @Description: service异常
 * -------------------
 * @Author: YangXingfu
 * @Date: 2019/07/24 17:18
 */

public class ServiceException extends Exception {

    /**
     * 异常代码
     */
    private String code;

    /**
     * 异常信息
     */
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ServiceException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(ServiceExceptionCode serviceExceptionCode) {
        this.code = serviceExceptionCode.getCode();
        this.msg = serviceExceptionCode.getName();
    }

    public ServiceException(String msg) {
        this.msg = msg;
    }
}