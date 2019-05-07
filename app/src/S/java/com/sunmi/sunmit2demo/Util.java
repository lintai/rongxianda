package com.sunmi.sunmit2demo;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.sunmit2demo.modle.Dto;
import com.sunmi.sunmit2demo.utils.Utils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author : chrc
 * date   : 2019/5/7  8:26 PM
 * desc   :
 */
public class Util {

    private static final boolean DEFAULT_IS_URL_ENCODE = false;
    public static final String SIGN_KEY_SIGN = "sign";
    public static final String SIGN_KEY = "954AD4611FB576FE5C6D805B3CF290B8";

    public static final String appId = "100";

    private static Set<String> getReuqestKeySet(Dto request) {
        String jsonString = new Gson().toJson(request);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Gson gson = new Gson();
        Map<String, Object> map2 = gson.fromJson(jsonString, type);
        return map2.keySet();
    }

    private static Object getReuqestValue(Dto request,String key) {
        String jsonString = new Gson().toJson(request);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Gson gson = new Gson();
        Map<String, Object> map2 = gson.fromJson(jsonString, type);
        return map2.get(key);
    }

//    private static StringBuilder getSignStringBuffer(Dto request) {
//        List<String> keyList = new ArrayList<>(getReuqestKeySet(request));
//        Collections.sort(keyList);
//        StringBuilder sb = new StringBuilder();
//        // 生成参数签名
//        for (String paramName : keyList) {
//            // sign签名不需要计算
//            if (!paramName.equals(SIGN_KEY_SIGN)) {
//                Object value = getReuqestValue(request,paramName);
//                if (value instanceof String && TextUtils.isEmpty(value.toString()) ) {
//                    break;
//                } else{
//                    sb.append(paramName).append(converValueToString(value));
//                }
//            }
//        }
//        // 加上MD5key
//        sb.append(SIGN_KEY);
//
//        return sb;
//    }

    public static String getSignStringBuffer(Map<String, String> params) {
        List<String> keyList = new ArrayList<>(params.keySet());
        Collections.sort(keyList);
        StringBuilder sb = new StringBuilder();
        // 生成参数签名
        for (String paramName : keyList) {
            // sign签名不需要计算
            if (!paramName.equals(SIGN_KEY_SIGN)) {
                String value = params.get(paramName);
                if (!TextUtils.isEmpty(value) ) {
                    sb.append(paramName).append(value);
                }
            }
        }
        // 加上MD5key
        sb.append(SIGN_KEY);
        String result = MD5.stringMD5(sb.toString());
        Log.i("signResult===", result);
        return result;
    }

    public static String getCurrData() {
        Date date = new Date();
        long currTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        date.setTime(currTime);//毫秒
        return sdf.format(date);

    }

}
