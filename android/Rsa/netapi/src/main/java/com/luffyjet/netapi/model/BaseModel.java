package com.luffyjet.netapi.model;

import com.google.gson.Gson;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/12
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class BaseModel {
    public String error;
    public String errorString;

    @Override
    public String toString() {
        return toJSON();
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
