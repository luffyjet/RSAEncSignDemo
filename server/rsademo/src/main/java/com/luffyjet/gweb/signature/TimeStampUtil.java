package com.luffyjet.gweb.signature;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Title :
 * Author : luffyjet
 * Date : 2017/8/10
 * Project : voiceproxy
 * Site : http://www.luffyjet.com
 */
public class TimeStampUtil {

    private static DateFormat format = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

    public static boolean checkTime(Map<String, String> params) throws ParseException {
        String time = params.get(Constants.TIMESTAMP);
        if (StringUtils.isEmpty(time)) {
            return false;
        }
        return checkTime(time);
    }


    public static boolean checkTime(String time) throws ParseException {
        if (StringUtils.isEmpty(time)) {
            return false;
        }

        format.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
        Date requestTime = format.parse(time);
        return requestTime.getTime() + Constants.TIMELIMIT * 60 * 1000 < System.currentTimeMillis();
    }
}
