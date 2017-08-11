package com.wiseyq.rsademo;

import android.app.Application;

import com.luffyjet.netapi.NxApi;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/8/3
 * Project : Rsa
 * Site : http://www.luffyjet.com
 */

public class App extends Application {

    public static NxApi.Nx api;

    @Override
    public void onCreate() {
        super.onCreate();
        api = NxApi.initAPI(this);
    }
}
