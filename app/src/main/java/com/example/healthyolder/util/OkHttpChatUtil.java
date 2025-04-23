package com.example.healthyolder.util;

import com.example.healthyolder.bean.Configs;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpChatUtil {
    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void post(String url, Map<String, Object> params, Callback callback) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(new Gson().toJson(params));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringBuffer.toString());
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + Configs.API_KEY)
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
