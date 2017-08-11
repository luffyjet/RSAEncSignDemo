package com.luffyjet.netapi;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/12
 * Project :
 * Site : http://www.luffyjet.com
 */
public class HashUtil {

    /**
     * get auth digest
     * @param username
     * @param password
     * @param realm
     * @param nonce
     * @return
     */
    public static String auth(String met,String username, String password, String realm, String nonce) {
        //关键一步，将UTC 时间 nonce 转换成 16进制字符串
        long time = Long.parseLong(nonce) * 1000;
        nonce = Long.toHexString(time);
        //step1 md5
        String ha1 =  Md5Str(username + ":" + realm + ":" + password);
        //step2 md5
        String method =  Md5Str(met);
        String simplified_ha2 =  Md5Str(ha1 + ":" + nonce + ":" + method);
        //step3 base64
        String str = username + ":" + nonce + ":" + simplified_ha2;
        return Base64Util.encode(str.getBytes());
    }


    /**
     *  md5加密
     * @param str
     * @return
     */
    public static String Md5Str(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(Charset.forName("UTF-8")));
            byte[] byteDigest = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (byte element : byteDigest) {
                i = element;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString();
            // 16位的加密
            // return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
