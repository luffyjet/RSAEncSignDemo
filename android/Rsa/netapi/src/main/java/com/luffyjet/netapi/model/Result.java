package com.luffyjet.netapi.model;

import com.google.gson.Gson;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/8/3
 * Project : Rsa
 * Site : http://www.luffyjet.com
 */

public class Result<T> {
    public boolean result;
    public T data;
    public String errorMsg;
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
