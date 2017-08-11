/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.luffyjet.gweb.signature;

public class Constants {

    public static final String SIGN_TYPE                      = "sign_type";

    public static final String SIGN_TYPE_RSA                  = "RSA";

    /**
     * sha256WithRsa 算法请求类型
     */
    public static final String SIGN_TYPE_RSA2                 = "RSA2";

    public static final String SIGN_ALGORITHMS                = "SHA1WithRSA";

    public static final String SIGN_SHA256RSA_ALGORITHMS      = "SHA256WithRSA";

    public static final String TIMESTAMP                      = "timestamp";

    public static final int TIMELIMIT                      = 2;//时间戳过期时间

    public static final String SIGN                           = "sign";

    //-----===-------///

    public static final String BIZ_CONTENT_KEY                = "biz_content";

    /** 默认时间格式 **/
    public static final String DATE_TIME_FORMAT               = "yyyy-MM-dd HH:mm:ss";

    /**  Date默认时区 **/
    public static final String DATE_TIMEZONE                  = "GMT+8";

    /** UTF-8字符集 **/
    public static final String CHARSET_UTF8                   = "UTF-8";

    /** GBK字符集 **/
    public static final String CHARSET_GBK                    = "GBK";
}
