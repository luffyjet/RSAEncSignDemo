/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.wiseyq.rsademo.signature;


import com.wiseyq.rsademo.signature.codec.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;



public class SignatureUtil {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 将参数拼接用于加签
     *
     * @param sortedParams
     * @return
     */
    public static String getSignContent(Map<String, String> sortedParams) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);
        int index = 0;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sortedParams.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                content.append((index == 0 ? "" : "&") + key + "=" + value);
                index++;
            }
        }
        return content.toString();
    }



    public static String getSignCheckContent(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        params.remove(Constants.SIGN);

        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }

        return content.toString();
    }


    /**
     * rsa内容签名
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @throws RSAException
     */
    public static String rsaSign(String content, String privateKey, String charset,
                                 String signType) throws RSAException {

        if (Constants.SIGN_TYPE_RSA.equals(signType)) {

            return rsaSign(content, privateKey, charset);
        } else if (Constants.SIGN_TYPE_RSA2.equals(signType)) {

            return rsa256Sign(content, privateKey, charset);
        } else {

            throw new RSAException("Sign Type is Not Support : signType=" + signType);
        }

    }

    /**
     * sha256WithRsa 加签
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @throws RSAException
     */
    public static String rsa256Sign(String content, String privateKey,
                                    String charset) throws RSAException {

        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (Exception e) {
            throw new RSAException("RSAcontent = " + content + "; charset = " + charset, e);
        }

    }

    /**
     * sha1WithRsa 加签
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @throws RSAException
     */
    public static String rsaSign(String content, String privateKey,
                                 String charset) throws RSAException {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_ALGORITHMS);

            signature.initSign(priKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            byte[] signed = signature.sign();

            return new String(Base64.encodeBase64(signed));
        } catch (InvalidKeySpecException ie) {
            throw new RSAException("RSA私钥格式不正确，请检查是否正确配置了PKCS8格式的私钥", ie);
        } catch (Exception e) {
            throw new RSAException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }

    public static String rsaSign(Map<String, String> params, String privateKey,
                                 String charset) throws RSAException {
        String signContent = getSignContent(params);

        return rsaSign(signContent, privateKey, charset);

    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,
                                                    InputStream ins) throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    /**
     * 验证参数和签名
     * @param params 参数map
     * @param publicKey  公钥
     * @param charset
     * @return
     * @throws RSAException
     */
    public static boolean rsa256Check(Map<String, String> params, String publicKey,
                                     String charset) throws RSAException {
        String sign = params.get(Constants.SIGN);
        String content = getSignCheckContent(params);
        System.out.println(content);
        return rsa256CheckContent(content, sign, publicKey, charset);
    }

    /**
     * 验证参数和签名
     * @param content  拼接好的参数
     * @param sign  签名
     * @param publicKey  公钥
     * @param charset
     * @param signType
     * @return
     * @throws RSAException
     */
    public static boolean rsaCheck(String content, String sign, String publicKey, String charset,
                                   String signType) throws RSAException {

        if (Constants.SIGN_TYPE_RSA.equals(signType)) {

            return rsaCheckContent(content, sign, publicKey, charset);

        } else if (Constants.SIGN_TYPE_RSA2.equals(signType)) {

            return rsa256CheckContent(content, sign, publicKey, charset);

        } else {

            throw new RSAException("Sign Type is Not Support : signType=" + signType);
        }

    }

    public static boolean rsa256CheckContent(String content, String sign, String publicKey,
                                             String charset) throws RSAException {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                    new ByteArrayInputStream(publicKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new RSAException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    public static boolean rsaCheckContent(String content, String sign, String publicKey,
                                          String charset) throws RSAException {
        try {
            PublicKey pubKey = getPublicKeyFromX509("RSA",
                    new ByteArrayInputStream(publicKey.getBytes()));

            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_ALGORITHMS);

            signature.initVerify(pubKey);

            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            throw new RSAException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }

    public static PublicKey getPublicKeyFromX509(String algorithm,
                                                 InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


    /**
     * 公钥加密
     *
     * @param content   待加密内容
     * @param publicKey 公钥
     * @param charset   字符集，如UTF-8, GBK, GB2312
     * @return 密文内容
     * @throws RSAException
     */
    public static String rsaEncrypt(String content, String publicKey,
                                    String charset) throws RSAException {
        try {
            PublicKey pubKey = getPublicKeyFromX509(Constants.SIGN_TYPE_RSA,
                    new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance(Constants.SIGN_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes()
                    : content.getBytes(charset);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();

            return StringUtils.isEmpty(charset) ? new String(encryptedData)
                    : new String(encryptedData, charset);
        } catch (Exception e) {
            throw new RSAException("EncryptContent = " + content + ",charset = " + charset,
                    e);
        }
    }

    /**
     * 私钥解密
     *
     * @param content    待解密内容
     * @param privateKey 私钥
     * @param charset    字符集，如UTF-8, GBK, GB2312
     * @return 明文内容
     * @throws RSAException
     */
    public static String rsaDecrypt(String content, String privateKey,
                                    String charset) throws RSAException {
        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance(Constants.SIGN_TYPE_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData = StringUtils.isEmpty(charset)
                    ? Base64.decodeBase64(content.getBytes())
                    : Base64.decodeBase64(content.getBytes(charset));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密  
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();

            return StringUtils.isEmpty(charset) ? new String(decryptedData)
                    : new String(decryptedData, charset);
        } catch (Exception e) {
            throw new RSAException("EncodeContent = " + content + ",charset = " + charset, e);
        }
    }


    public static void main(String[] args) {

        String src = "abcdefg";

        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCc4Fho7nhfnOc9yI7c+zjbxK+VMO2QYS0n4x8BaJxebsQtpfTRBHi9kvpak79oJ7N1FoarOu2xA+Javs8mMkO9A19Qvi6aAZojzsr14cUGRrBR0g7/+51xVDRVTxiwWm7E9k9rdzU5ic0hCAiLE06NTdGFBv6k62NtLuzqn/h3rwIDAQAB";

        String privatekey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJzgWGjueF+c5z3Ijtz7ONvEr5Uw7ZBhLSfjHwFonF5uxC2l9NEEeL2S+lqTv2gns3UWhqs67bED4lq+zyYyQ70DX1C+LpoBmiPOyvXhxQZGsFHSDv/7nXFUNFVPGLBabsT2T2t3NTmJzSEICIsTTo1N0YUG/qTrY20u7Oqf+HevAgMBAAECgYBEV7xMYm+Qf+OB2AjdHpkDrSktHrawKpWohdqxG2jb/vd6R41jLcaIGCr3INzHPFyDCwA6Qp7geie3jt7h7g3x8BNevwzjKP+wJXkWdl5TPPMVnGTB64BXVDxN4RjRYHmEF5UKiX6Wn5ttufzHRHSHKKSjBrCS2JN+0zRT0BrNYQJBANg/rB573HjIfodHrzlU61SFYjLb2zDL7ZXwW0cleGrPlaPEUZb51TY1d5T+dkozmXaa5km2Xdw0KtYk1lS+0t8CQQC5trOoHvaLPcCZOwZ2nQxmZXtOKHY/8b3N9cVHLxU+CwKEMrogtVWDJA4TX0ll+8MGqA6ejI+a7plJrkYW5EUxAkEAzTzhJq13ukrPi6VFcKxgDX/qi0qO5ekmPMA6YXP2rakG5L9WkGvdJ+3m6Mn5isMeS6sIFb23p177qPKdWSEjEQJAHoWjg0cLeBj/FW/5APeQuSeGm3LU9G9zpWz2LlvTnu3KTRXVN1j2I+aCFbb8ZjF5fReTx4UMeQcr1Es7I7oCkQJAfbDQATmW76c2CIKD1ncekKvXZghNOacn1uhE0CqXXQtUOygCUZ5VFO4zvDcyZ/uhesQ49Sz/ENdqebx46HG0bg==";

        try {
            String sign = rsaSign(src, privatekey, Constants.CHARSET_UTF8);

            System.out.println(sign);

            boolean rs = rsaCheckContent(src, sign, publickey, Constants.CHARSET_UTF8);

            System.out.println(rs);
        } catch (RSAException e) {
            e.printStackTrace();
        }
    }
}
