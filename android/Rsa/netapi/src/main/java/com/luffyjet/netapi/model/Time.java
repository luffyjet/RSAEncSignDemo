package com.luffyjet.netapi.model;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/12
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class Time extends BaseModel {

    public Reply reply;

    /**
     * Call /gettime request to get realm and nonce from server (current server time MUST be used as a nonce value, nonce is valid for about 5 minutes)
     */
    public static class Reply {
        public String realm;
        public String timeZoneOffset;
        public String timezoneId;
        /**
         * used as a nonce value
         */
        public String utcTime;
    }
}
