package com.sunmi.sunmit2demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * author : chrc
 * date   : 2019/5/19  6:04 PM
 * desc   :
 */
public class PreferenceUtil {

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("rongxianda", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public interface KEY{
        String PAYING_TYPE = "paying_type";
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getInstance(context).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defValue) {
        String data = getInstance(context).getString(key, defValue);
        return data;
    }
}
