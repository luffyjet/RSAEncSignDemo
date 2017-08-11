package com.luffyjet.netapi.model;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/13
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class Position extends BaseModel {

    public Pos reply;

    public static class Pos extends BaseModel {
        /**
         * x : 0.07038900256156921
         * y : -0.8428890109062195
         * z : 0
         */
        public double x;
        public double y;
        public double z;
    }
}
