package com.example.healthyolder.util;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by marlon
 */
public abstract class ObjectCallBack<T> extends Callback<T> {
    private Class<T> cls;

    public ObjectCallBack(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        LogUtil.i("response", string);
        T object;
        try {
            object = new Gson().fromJson(string, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LogUtil.i("call", "call:" + call.isCanceled());
        LogUtil.i("Exception", "Exception:" + e.toString());
        if (call.isCanceled()) { //Http取消的时候会进入到这里，先进行判断，如果取消了请求直接返回
            return;
        }
        onFail(call, e);

    }

    @Override
    public void onResponse(T response, int id) {
        if (response == null) {
            ToastUtil.showBottomToast("很抱歉，数据解析失败");
            return;
        }
        LogUtil.i("response", response.toString());
        onSuccess(response);
    }

    public abstract void onSuccess(T response);

    public abstract void onFail(Call call, Exception e);
}
