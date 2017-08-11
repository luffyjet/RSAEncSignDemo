package com.luffyjet.netapi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/13
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class GsonConverterEx extends GsonConverter {

    public GsonConverterEx(Gson gson) {
        super(gson);
    }

    public GsonConverterEx(Gson gson, String charset) {
        super(gson, charset);
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        if (type.equals(String.class)) {
            try {
                return convertStreamToString(body.in());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.fromBody(body, type);
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
