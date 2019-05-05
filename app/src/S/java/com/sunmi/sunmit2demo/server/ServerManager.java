package com.sunmi.sunmit2demo.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * author : chrc
 * date   : 2019/5/5  8:33 PM
 * desc   :
 */
public class ServerManager {

    public static AllClassAndGoodsResult getClassGoodsList(String appId, String sign) {
        RequestBody requestBody = new FormBody.Builder()
                    .add("appid", appId)
                    .add("sign", sign)
                    .add("requesttime", String.valueOf(System.currentTimeMillis()))
                    .build();

        Request request = new Request.Builder()
                        .url(Api.HOME_CLASS_GOODS_LIST)
                        .post(requestBody)
                        .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                return new Gson().fromJson(response.body().string(), new TypeToken<AllClassAndGoodsResult>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
