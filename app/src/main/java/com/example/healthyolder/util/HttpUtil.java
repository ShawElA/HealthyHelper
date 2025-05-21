package com.example.healthyolder.util;

import com.example.healthyolder.bean.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;


public class HttpUtil {

    private static final long CONNECT_TIMEOUT = 15000;
    private static final long READ_TIMEOUT = 15000;
    private static final long WRITE_TIMEOUT = 15000;
    private static boolean isValidTime = false;

    /**
     * 简化的GET请求方法，用于新API调用
     * @param apiPath API路径，不包含baseUrl
     * @param responseCallback 回调函数，处理返回的响应
     */
    public static void get(String apiPath, final ResponseCallback responseCallback) {
        get(apiPath, null, responseCallback);
    }
    
    /**
     * GET请求方法（带参数版本）
     * @param apiPath API路径
     * @param params 请求参数
     * @param responseCallback 回调函数
     */
    public static void get(String apiPath, Map<String, String> params, final ResponseCallback responseCallback) {
        // 如果传入的是完整URL（包含http://），则直接使用
        String url;
        if (apiPath.startsWith("http://") || apiPath.startsWith("https://")) {
            url = apiPath;
        } else {
            // 确保apiPath不以/开头，baseUrl以/结尾
            String path = apiPath.startsWith("/") ? apiPath.substring(1) : apiPath;
            url = Urls.baseUrl + path;
        }
        
        LogUtil.i("GET URL", url);
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("GET params: {\n");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(",\n");
            }
            sb.append("}");
            LogUtil.i("HTTP GET Params", sb.toString());
        }
        
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .build()
                .connTimeOut(CONNECT_TIMEOUT)
                .readTimeOut(READ_TIMEOUT)
                .writeTimeOut(WRITE_TIMEOUT)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.e("HTTP GET Error", e.getMessage());
                        if (responseCallback instanceof HttpCallback) {
                            ((HttpCallback) responseCallback).onError(e);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("HTTP GET Response", response);
                        if (responseCallback != null) {
                            if (responseCallback instanceof HttpCallback) {
                                ((HttpCallback) responseCallback).onSuccess(response);
                            } else {
                                responseCallback.onResponse(response);
                            }
                        }
                    }
                });
    }

    /**
     * 简化的POST请求方法，用于新API调用
     * @param apiPath API路径，不包含baseUrl
     * @param params 请求参数
     * @param responseCallback 回调函数，处理返回的响应
     */
    public static void post(String apiPath, Map<String, Object> params, final ResponseCallback responseCallback) {
        // 如果传入的是完整URL（包含http://），则直接使用
        String url;
        if (apiPath.startsWith("http://") || apiPath.startsWith("https://")) {
            url = apiPath;
        } else {
            // 确保apiPath不以/开头，baseUrl以/结尾
            String path = apiPath.startsWith("/") ? apiPath.substring(1) : apiPath;
            url = Urls.baseUrl + path;
        }
        
        LogUtil.i("POST URL", url);
        
        // 记录请求参数
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("POST params: {\n");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(",\n");
            }
            sb.append("}");
            LogUtil.i("HTTP POST Params", sb.toString());
        }
        
        // 将参数转换为JSON字符串
        String jsonParams = new com.google.gson.Gson().toJson(params);
        LogUtil.i("HTTP POST JSON", jsonParams);
        
        OkHttpUtils.postString()
                .url(url)
                .content(jsonParams)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .connTimeOut(CONNECT_TIMEOUT)
                .readTimeOut(READ_TIMEOUT)
                .writeTimeOut(WRITE_TIMEOUT)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.e("HTTP POST Error", e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("HTTP POST Response", response);
                        if (responseCallback != null) {
                            responseCallback.onResponse(response);
                        }
                    }
                });
    }

    // 定义回调接口，仅包含一个方法，以支持lambda表达式
    public interface ResponseCallback {
        void onResponse(String response);
    }

    // 完整的回调接口，包含成功和失败方法
    public interface HttpCallback extends ResponseCallback {
        void onSuccess(String response);
        void onError(Exception e);
        
        @Override
        default void onResponse(String response) {
            onSuccess(response);
        }
        
        // 为了兼容旧代码，添加此方法
        default void onFailure(String errorMsg) {
            LogUtil.e("HTTP Error", errorMsg);
        }
    }

    /**
     * @param url      请求地址
     * @param params   请求参数
     * @param tag      绑定相应的Object,用于取消请求
     * @param callback 相应的CallBack
     */
    public static void getResponse(String url, Map<String, String> params, Object tag, Callback callback) {
        if (params != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url).append("?");
            Set<String> set = params.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String s = iterator.next();
                stringBuffer.append(s).append("=").append(params.get(s)).append("&");
            }
            LogUtil.i("url", stringBuffer.toString().substring(0, stringBuffer.toString().length() - 1));
        }else{
            LogUtil.i("url", url);
        }

        OkHttpUtils
                .get()
                .url(url)
                .tag(tag)
                .params(params)
                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);


    }

    /**
     * @param url      请求地址
     * @param params   请求参数
     * @param tag      绑定相应的Object,用于取消请求
     * @param callback 相应的CallBack
     */
    public static void postResponse(String url, Map<String, String> params, Object tag, Callback callback) {
        LogUtil.i("url", url);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{").append("\n");
        Set<String> set = params.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            stringBuffer.append("\"").append(s).append("\":\"").append(params.get(s)).append("\",\n");
        }
        stringBuffer.append("}").append("\n");
        LogUtil.i("post", stringBuffer.toString());
        OkHttpUtils
                .post()
                .url(url)
                .tag(tag)
                .params(params)
                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);


    }

    /**
     * post请求，参数是Json
     *
     * @param url
     * @param postJson
     * @param tag
     * @param callback
     */
    public static void postResponse(String url, String postJson, Object tag, Callback callback) {
        LogUtil.i("url", url);
        LogUtil.i("post", postJson);
        OkHttpUtils
                .postString()
                .tag(tag)
                .url(url)
                .content(postJson)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);


    }

    //下载文件
    public static void downLoadFile(String url, Object tag, long startPart, Callback callback) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(tag)
                .addHeader("RANGE", "bytes=" + startPart + "-")
                .build().
                connTimeOut(CONNECT_TIMEOUT)
                .readTimeOut(READ_TIMEOUT)
                .writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);
    }

    //上传图片
    public static void postImgFile(String url, String fileName, Map<String, String> params, File file, Object tag, Callback callback) {

        OkHttpUtils.post()
                .addFile("img", fileName, file)
                .url(url)
                .tag(tag)
                .params(params)
                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);
    }

    //取消请求
    public static void cancelHttpRequest(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);

    }

    //获取图片
    public static void getImgFromInternet(String url, Object tag, BitmapCallback callback) {

        OkHttpUtils
                .get()
                .url(url).tag(tag)
                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
                .execute(callback);


    }

    //添加头部
//    public static String httpHeader(String url, Object tag){
//        String header = "";
//        if (timeValid(SharedPreferencesUtil.readSharedPreferences("AccountInfo", "EndTime", ""))){
//
//            switch (url.split("api")[0]){
//                //内网
//                case "http://" + Url.header + "8082/":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "WisdomToken", "");
//                    break;
//
//                //教师端
//                case "http://" + Url.header + "8033/":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "TeacherToken", "");
//                    break;
//
//                //官网
//                case "http://" + Url.header + "8035/":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "APIToken", "");
//                    break;
//
//                //内网（域名原为http://wisdom.api.pelway.cn/api/，但其中已经包含有一个api，为防止截取矛盾，故取http://wisdom.）
//                case "http://wisdom.":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "WisdomToken", "");
//                    break;
//
//                //官网（域名原为http://api.pelway.cn/api/，但其中已经包含有一个api，为防止截取矛盾，故取http://）
//                case "http://":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "APIToken", "");
//                    break;
//
//                //教师端
//                case "http://teacher.pelway.cn/":
//                    header = SharedPreferencesUtil.readSharedPreferences("AccountInfo", "TeacherToken", "");
//                    break;
//
//                default:
//                    break;
//            }
//        }else {
//            //失效后通过refreshToken获取新token
//            postRefreshToken(url, tag);
//        }
//
//        return header;
//    }

    /**
     * 重新获取新token
     * @param url   请求地址
     */
//    public static void postRefreshToken(final String url, final Object tag){
//        Map<String, String> params = new HashMap<>();
////        params.put("AccountRefreshToken", SharedPreferencesUtil.readSharedPreferences("AccountInfo", "AccountRefreshToken", ""));
//        params.put("TeacherRefreshToken", SharedPreferencesUtil.readSharedPreferences("AccountInfo", "TeacherRefreshToken", ""));
//        params.put("WisdomRefreshToken", SharedPreferencesUtil.readSharedPreferences("AccountInfo", "WisdomRefreshToken", ""));
//        params.put("APIRefreshToken", SharedPreferencesUtil.readSharedPreferences("AccountInfo", "APIRefreshToken", ""));
////        params.put("LearnRefreshToken", SharedPreferencesUtil.readSharedPreferences("AccountInfo", "LearnRefreshToken", ""));
//
//        LogUtil.i("refreshUrl", Url.getRefreshToken);
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("{").append("\n");
//        Set<String> set = params.keySet();
//        Iterator<String> iterator = set.iterator();
//        while (iterator.hasNext()) {
//            String s = iterator.next();
//            stringBuffer.append("\"").append(s).append("\":\"").append(params.get(s)).append("\",\n");
//        }
//        stringBuffer.append("}").append("\n");
//        LogUtil.i("post", stringBuffer.toString());
//        OkHttpUtils
//                .post()
//                .url(Url.getRefreshToken)
//                .tag(tag)
//                .params(params)
//                .build().connTimeOut(CONNECT_TIMEOUT).readTimeOut(READ_TIMEOUT).writeTimeOut(WRITE_TIMEOUT)
//                .execute(new ObjectCallBack<GetRefreshTokenResult>(GetRefreshTokenResult.class) {
//                    @Override
//                    public void onSuccess(GetRefreshTokenResult response) {
//                        if (response.isSuccess()){
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "Expires_in", response.getData().getExpires_in());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "EndTime", response.getData().getEndTime());
////                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "AccountToken", response.getData().getAccountToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "TeacherToken", response.getData().getTeacherToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "WisdomToken", response.getData().getWisdomToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "APIToken", response.getData().getAPIToken());
////                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "LearnToken", response.getData().getLearnToken());
//
////                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "AccountRefreshToken", response.getData().getAccountRefreshToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "TeacherRefreshToken", response.getData().getTeacherRefreshToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "WisdomRefreshToken", response.getData().getWisdomRefreshToken());
//                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "APIRefreshToken", response.getData().getAPIRefreshToken());
////                            SharedPreferencesUtil.writeSharedPreferences("AccountInfo", "LearnRefreshToken", response.getData().getLearnRefreshToken());
//                            httpHeader(url, tag);
//                        }else {
//                            EventBus.getDefault().post(new ExitLoginMessageEvent());
//                            EMClient.getInstance().logout(true);
//                        }
//                    }
//
//                    @Override
//                    public void onFail(Call call, Exception e) {
////                        ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
//                    }
//                });
//    }

    //检查是否在有效时间内
//    private static boolean timeValid(String time){
//        try {
//            isValidTime = false;
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = null;
//            date = format.parse(time);
//            long ts = date.getTime() - 8 * 60 * 60 * 1000;
//            long timeStamp = new Date().getTime();
//            if (ts - timeStamp >= 0)
//                isValidTime = true;
//            else
//                isValidTime = false;
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return isValidTime;
//    }

}
