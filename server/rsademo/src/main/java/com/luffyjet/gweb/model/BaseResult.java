package com.luffyjet.gweb.model;

/**
 * Title :
 * Author : luffyjet
 * Date : 2017/8/3
 * Project : voiceproxy
 * Site : http://www.luffyjet.com
 */
public class BaseResult<T> extends BaseModel {

    public BaseResult() {
    }

    public BaseResult(boolean flag) {
        result = flag;
    }

    public boolean result;
    public T data;
    public String errorMsg;
}
