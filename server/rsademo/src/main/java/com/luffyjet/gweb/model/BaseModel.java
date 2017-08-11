package com.luffyjet.gweb.model;

import com.google.gson.Gson;

/**
 * Title :
 * Author : luffyjet
 * Date : 2017/6/22
 * Project : voiceproxy
 * Site : http://www.luffyjet.com
 */
public class BaseModel {
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
