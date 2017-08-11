package com.luffyjet.netapi;

import android.app.Application;
import android.content.Context;
import android.os.StatFs;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.luffyjet.netapi.model.Result;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/12
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class NxApi {

    public static final String BASE_API_URL = "http://192.168.118.61:8090";

    public static final String GET_PUBLICKEY = "/rsa/publickey";
    public static final String DECODE = "/rsa/decode";
    public static final String SIGNATURE = "/rsa/signatureTest";

    public interface Nx {
        @GET(GET_PUBLICKEY)
        void publickey(Callback<Result<String>> callback);

        @FormUrlEncoded
        @POST(DECODE)
        void decode(@Field("username") String uname, @Field("password") String password, Callback<Result<String>> callback);

        @FormUrlEncoded
        @POST(SIGNATURE)
        void signature(@Field("username") String uname, @Field("nickname") String nickname, @Field("desp") String desp,
                       @Field("timestamp") String timestamp, @Field("sign") String sign, Callback<Result<String>> callback);
    }

    public static Nx initAPI(Application context){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_API_URL)
                .setClient(new OkClient(NxApi.ClientUtil.getOkHttpClient(context)))
                .setConverter(new GsonConverterEx(new Gson()))
                .build();

        //调试工具
        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build());

        // Create an instance of our Nx API interface.
       return restAdapter.create(NxApi.Nx.class);
    }



    public static class ClientUtil {
        static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
        static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000; // 20s
        static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
        private static final String PICASSO_CACHE = "picasso-cache";
        private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
        private static OkHttpClient mHttpClient;
        static File cacheDir;

        public static OkHttpClient getOkHttpClient(Context context) {
            if (null == mHttpClient) {
                mHttpClient = new OkHttpClient();
                mHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                mHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                mHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                cacheDir = createDefaultCacheDir(context);
                mHttpClient.setCache(new com.squareup.okhttp.Cache(cacheDir, calculateDiskCacheSize(cacheDir)));
                setStetho(mHttpClient);
            }
            return mHttpClient;
        }

        private static File createDefaultCacheDir(Context context) {
            File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
            if (!cache.exists()) {
                //noinspection ResultOfMethodCallIgnored
                cache.mkdirs();
            }
            return cache;
        }

        private static long calculateDiskCacheSize(File dir) {
            long size = MIN_DISK_CACHE_SIZE;
            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
                // Target 2% of the total space.
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            // Bound inside min/max size for disk cache.
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }

        /**
         * 开启Stetho 进行网络调试 需要如下依赖<br/><br/>
         * compile 'com.facebook.stetho:stetho:1.1.1'
         * compile 'com.facebook.stetho:stetho-okhttp:1.1.1'
         * @param client OkHttpClient
         */
        private static void setStetho(OkHttpClient client) {
            client.networkInterceptors().add(new StethoInterceptor());
        }
    }

}
