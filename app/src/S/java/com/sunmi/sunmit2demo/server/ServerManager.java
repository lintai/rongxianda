package com.sunmi.sunmit2demo.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.sunmit2demo.Util;
import com.sunmi.sunmit2demo.modle.AllClassAndGoodsResult;
import com.sunmi.sunmit2demo.modle.CreateOrderResult;
import com.sunmi.sunmit2demo.modle.GoodsOrderModle;
import com.sunmi.sunmit2demo.modle.PayInfo;
import com.sunmi.sunmit2demo.modle.Result;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static AllClassAndGoodsResult getClassGoodsList(String appId) {
        String currTime = Util.getCurrData();
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("requesttime", currTime);

        RequestBody requestBody = new FormBody.Builder()
                    .add("appid", appId)
                    .add("sign", Util.getSignStringBuffer(params))
                    .add("requesttime", currTime)
                    .build();

        Request request = new Request.Builder()
                        .url(Api.HOME_CLASS_GOODS_LIST)
                        .post(requestBody)
                        .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                String bodyString = response.body().string();
                return new Gson().fromJson(bodyString, new TypeToken<AllClassAndGoodsResult>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CreateOrderResult createOrder(String appId, List<GoodsOrderModle> goodsList, int totalfee) {
        String goodsListJson = new Gson().toJson(goodsList);
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("totalfee", String.valueOf(totalfee));
        params.put("goodslist", goodsListJson);

        RequestBody requestBody = new FormBody.Builder()
                .add("appid", appId)
                .add("sign", Util.getSignStringBuffer(params))
                .add("totalfee", String.valueOf(totalfee))
                .add("goodslist", goodsListJson)
                .build();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
            sb.append(((FormBody) requestBody).encodedName(i));
            sb.append(" - ");
            sb.append(((FormBody) requestBody).encodedValue(i));
            sb.append("\n");
        }
        String body = sb.toString();
        Request request = new Request.Builder()
                .url(Api.CREATE_ORDER)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                String bodyString = response.body().string();
                return new Gson().fromJson(bodyString, new TypeToken<CreateOrderResult>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result<PayInfo> pay(String appId, String orderId, int payType, String authcode, int sourcetype) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("orderid", orderId);
        params.put("paytype", String.valueOf(payType));
        params.put("authcode", authcode);
        params.put("sourcetype", String.valueOf(sourcetype));

        RequestBody requestBody = new FormBody.Builder()
                .add("appid", appId)
                .add("sign", Util.getSignStringBuffer(params))
                .add("orderid", orderId)
                .add("paytype", String.valueOf(payType))
                .add("authcode", authcode)
                .add("sourcetype", String.valueOf(sourcetype))
                .build();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
            sb.append(((FormBody) requestBody).encodedName(i));
            sb.append(" - ");
            sb.append(((FormBody) requestBody).encodedValue(i));
            sb.append("\n");
        }
        String body = sb.toString();

        Request request = new Request.Builder()
                .url(Api.PAY)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                String bodyString = response.body().string();
                return new Gson().fromJson(bodyString, new TypeToken<Result<PayInfo>>(){}.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
