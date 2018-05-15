package com.hekung.nxweather.util;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wenzh on 2018/5/9.
 */

public class HttpRequest {
    /**
     * 异步的Get请求
     *
     * @param url url
     */
    public static void getAsyn(String url,Callback callback) {
        // 创建OKHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(callback);

    }


}
