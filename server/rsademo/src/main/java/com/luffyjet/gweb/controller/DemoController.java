package com.luffyjet.gweb.controller;

import com.luffyjet.gweb.model.BaseResult;
import com.luffyjet.gweb.signature.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.util.Base64;
import java.util.Map;


/**
 * Title :
 * Author : luffyjet
 * Date : 2017/5/12
 * Project : gweb001
 * Site : http://www.luffyjet.com
 */
@RestController
@RequestMapping("/rsa")
public class DemoController {

    // 实际项目 这些都应该有独立的服务进行管理，存放在文件或者数据库
    //这一对 用于处理签名,openssl 生成的签名文件里获得这些值，Java使用时要去除换行和第一行，最后一行的标记
    private static final String publickeyInSignature = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3hZLRlSwHtUBrlaa2POz3xkb92VzhsFh9XgCpDfpYhXqgB/PWy54Ns5eFk60osgAvPGE5/uUKhGpqw50n907s+n3VnEb4RVKpAEgnYTrnCPIJd4ErcQYWGbgCrJdqou6PQBSbbd3ui6Phb0SNbNGQuBZqG2Yh6xokWfIBNSs6WwIDAQAB";
    //pkcs8类型
    private static final String privatekeyInSignature = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALeFktGVLAe1QGuVprY87PfGRv3ZXOGwWH1eAKkN+liFeqAH89bLng2zl4WTrSiyAC88YTn+5QqEamrDnSf3Tuz6fdWcRvhFUqkASCdhOucI8gl3gStxBhYZuAKsl2qi7o9AFJtt3e6Lo+FvRI1s0ZC4FmobZiHrGiRZ8gE1KzpbAgMBAAECgYEAn8I01CPSp4ceZElrTjttYiiGBlehJorYDZK2anRTmZng7MzfdP3eQjkzz0GGPOXviS87yDvfcS9iYDyXY4JDfkjJ /fi/ckVtc2uJtZI6zK9OAGh+eUsdSsBdph1nM8+W4lS7Kvr9LFfHLYWSzLEBQBDk6Yd/OYUZFfFP9pcI4SkCQQDhayEuEVLIOn9dCrAE8EeOUDFT7FvAJfIwYCyTcNrRQRoLCtZrnVV3akdLZW9USxOvsW13SXFLP4wTPcoG0OYfAkEA0Gta8ppYCjfuzABZlENpktwt6hG341qkTRndoc2EPjPCr2SsPkALZ4lpC/CW7s7YKt2Hi585D6neNSaBW/RMRQJAeb1jw/9zF9QP6O3WtjQWUROaMFrcCl/z9pBaQp6WbqCcMg5+Usw71iw9qMh1Ya7SSPanyd6OIzeErPeX3ip/vQJAB8AGOME+htq/mXxl2FqNYXWoi2yvPtgPBgLxN+QRh9Ka6bS/puzwv5/fdR80LZspdKaaNLnuAEQbzDQrWUUDVQJBAL+nzBlRp5HWeGThcju17raX9u3qI5UrnvJLBP5K+nWrOU849F331XPowV8fwkApMygC3z1NWgq0kfWkG6iXcIg=";


    //这一对 用于处理部分加密解密
    private static final String publickeyInRSA = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYHKR0ABEmHFGkEnOZDKlFjbDFtvBiTqZLe7pTLMERHGz4VknWgLqylbI6ezYNIbKBYSLUeGcx8jFS5roirJXJZGzzO597QbrWoWcV2t1oq9KHzjzvvSL/QlncnNtY5eLG/Lj8UpD8yFIJ5/o8w7FX4kUnLTlfVle3xdJ+TjAMxwIDAQAB";

    private static final String privatekeyInRSA = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJgcpHQAESYcUaQSc5kMqUWNsMW28GJOpkt7ulMswREcbPhWSdaAurKVsjp7Ng0hsoFhItR4ZzHyMVLmuiKslclkbPM7n3tButahZxXa3Wir0ofOPO+9Iv9CWdyc21jl4sb8uPxSkPzIUgnn+jzDsVfiRSctOV9WV7fF0n5OMAzHAgMBAAECgYAfKNbjUFQy+2AZ/RGjC33tWo4YolXxvWzBT7ImcaeHCsyjvoFXqH0WMiYVZsH2xK1dZXmODrANAjDqa4s7qSDxlFQjeodBO4P1JprtpUMsCymG7T86FB9QsvA8NM6nxa+DVGv5/HiVN5jm5WI1u0ZF0PiY2zvLPZX7o+ydayz30QJBANLA/0Mv+tFuIfq81BZB8aK4UbIyrrAO32UElTG6P7M+gyQsPL2ak8sWPS8wIdfvVk2H6ZFgXcFCKlllpSlXcBkCQQC4xLBliKh83McPlKWlszgQTK9fe9xD6ct/uaLG2nTHfPHU8J+dJryVdVcZcp1mcnLw5C7B4LNzKAagkuJW0X/fAkA40EQ/1XhU6s6ILE00LtS3295SHm2PEoNQVRrvC9lONaTepHo/VqNO1RDrXptQ/bkTL3dcdFDx3ydRcCBE88hJAkEAimTIu0Rg7yVu9HyiljpnA85sjVh442V6x3CEO+3lCobLnHOfGlapakk5tBXlT5bqAQTpAM8NmnOggyWB4wWIgQJAXpTy5PMBOZSmAGHK5JbQEHA5fMPyYb1d2nJ1aqe+qF54WtaROIR2DSddnk2sdpgaeQXDIsj9ZKSFJzRSByrUyA==";


    @RequestMapping(
            value = "/decode",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResult decodePsw(String username, String password) {
        System.out.println(username);
        System.out.println(password);

        BaseResult baseResult = new BaseResult<String>(true);

        try {
            String pwd = SignatureUtil.rsaDecrypt(password, privatekeyInRSA, Constants.CHARSET_UTF8);
            System.out.println("out: " + pwd);
            baseResult.data = pwd;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baseResult;
    }


    @RequestMapping(
            value = "/signatureTest",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResult test(@RequestParam Map<String, String> params) {

        System.err.println(params);

        BaseResult baseResult = new BaseResult<String>();
        try {
            //step 1. 验证时间戳，防范重放
            boolean isExpire = TimeStampUtil.checkTime(params);
            baseResult.result = isExpire;
            if (!isExpire) {
                //step 2. 验证数字签名 防范参数被篡改
                boolean isValid = SignatureUtil.rsa256Check(params, publickeyInSignature, Constants.CHARSET_UTF8);
                baseResult.result = isValid;
                if (isValid) {
                    baseResult.data = "签名验证通过";
                } else {
                    baseResult.errorMsg = "签名签证没有通过";
                }
            } else {
                baseResult.errorMsg = "请求已过期";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseResult;
    }
}
