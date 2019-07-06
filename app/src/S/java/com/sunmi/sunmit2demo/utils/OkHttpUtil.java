package com.sunmi.sunmit2demo.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * author : chrc
 * date   : 2019/6/30  7:53 PM
 * desc   :
 */
public class OkHttpUtil {

    private static OkHttpClient instance;
    private static long CONNECT_TIMEOUT = 15;
    private static long READ_TIMEOUT = 15;
    private static long WRITE_TIMEOUT = 15;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = createOKhttpClient();
        }
        return instance;
    }

    private static OkHttpClient createOKhttpClient() {
        OkHttpClient httpClient = new HttpsTrustUtil().getTrustAllClient().newBuilder()
//                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //连接超时
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //读取超时
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //写超时
                .build();
        return httpClient;
    }
}
