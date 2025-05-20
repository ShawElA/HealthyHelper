package com.example.healthyolder;

import android.app.Application;
import android.content.Context;

import com.example.healthyolder.bean.DicTypeResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.PreferenceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class BaseApplication extends Application {

    private static Context context;
    private static String userId = "1";
    public static String password = "123456";
    public static String userNickname = "evan";
    public static String icon = "";
    public static String depressionScore = "0";
    public static String lastTestTime = "";
    public static boolean isTest = true;
    private static String doctorId = "";                //预约医生id
    private static String doctorName = "";              //预约医生名
    private static String departmentName = "";          //预约科室
    public static Map<Integer, String> GoalMap = new HashMap<>();
    public static Context getContext() {
        return context;
    }
    public static List<DicTypeResult.DataBean> dicType;
    public PreferenceUtil preferenceUtil;
    public static void setContext(Context context) {
        BaseApplication.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        context = getApplicationContext();
        preferenceUtil = new PreferenceUtil(context);
        HttpUtil.getResponse(Urls.GETTYPEDIC, null, null, new ObjectCallBack<DicTypeResult>(DicTypeResult.class) {
            @Override
            public void onSuccess(DicTypeResult response) {
                if (response != null && response.isSuccess()){
                    BaseApplication.dicType = response.getData();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        BaseApplication.userId = userId;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        BaseApplication.password = password;
    }

    public static String getUserNickname() {
        return userNickname;
    }

    public static void setUserNickname(String nickname) {
        BaseApplication.userNickname = nickname;
    }

    public static String getIcon() {
        return icon;
    }

    public static void setIcon(String icon) {
        BaseApplication.icon = icon;
    }

    public static String getDepressionScore() {
        return depressionScore;
    }

    public static void setDepressionScore(String score) {
        BaseApplication.depressionScore = score;
    }

    public static String getLastTestTime() {
        return lastTestTime;
    }

    public static void setLastTestTime(String time) {
        BaseApplication.lastTestTime = time;
    }

    public static String getDoctorId() {
        return doctorId;
    }

    public static void setDoctorId(String doctorId) {
        BaseApplication.doctorId = doctorId;
    }

    public static String getDoctorName() {
        return doctorName;
    }

    public static void setDoctorName(String doctorName) {
        BaseApplication.doctorName = doctorName;
    }

    public static String getDepartmentName() {
        return departmentName;
    }

    public static void setDepartmentName(String departmentName) {
        BaseApplication.departmentName = departmentName;
    }
    
    // 兼容旧代码，避免修改太多文件
    @Deprecated
    public static String getUserName() {
        return userNickname;
    }

    @Deprecated
    public static void setUserName(String userName) {
        BaseApplication.userNickname = userName;
    }
}
