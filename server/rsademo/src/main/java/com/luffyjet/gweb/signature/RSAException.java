/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.luffyjet.gweb.signature;


/**
 * 
 * @author runzhi
 */
public class RSAException extends Exception {

    private static final long serialVersionUID = -238091758285157331L;

    private String            errCode;
    private String            errMsg;

    public RSAException() {
        super();
    }

    public RSAException(String message, Throwable cause) {
        super(message, cause);
    }

    public RSAException(String message) {
        super(message);
    }

    public RSAException(Throwable cause) {
        super(cause);
    }

    public RSAException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

}