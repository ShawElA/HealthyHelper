package com.example.healthyolder.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.healthyolder.BaseApplication;

/**
 * SharedPreferences工具类
 */
public class SPUtil {
    private static final String SP_NAME = "healthy_helper_sp";
    
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_TOKEN = "user_token";
    
    // 存储特定用户测试分数的键前缀
    private static final String USER_SCORE_PREFIX = "user_score_";
    
    /**
     * 保存字符串
     */
    public static void putString(Context context, String key, String value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
        
        // 如果是保存用户ID，同步到BaseApplication
        if (USER_ID.equals(key) && value != null) {
            BaseApplication.setUserId(value);
        }
    }
    
    /**
     * 获取字符串
     */
    public static String getString(Context context, String key, String defaultValue) {
        if (context == null) return defaultValue;
        
        // 如果是获取用户ID且BaseApplication中有值，优先使用BaseApplication中的值
        if (USER_ID.equals(key) && BaseApplication.getUserId() != null 
                && !BaseApplication.getUserId().isEmpty() && !"1".equals(BaseApplication.getUserId())) {
            String userId = BaseApplication.getUserId();
            // 顺便同步到SP
            SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            sp.edit().putString(key, userId).apply();
            return userId;
        }
        
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
    
    /**
     * 保存整数
     */
    public static void putInt(Context context, String key, int value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }
    
    /**
     * 获取整数
     */
    public static int getInt(Context context, String key, int defaultValue) {
        if (context == null) return defaultValue;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }
    
    /**
     * 保存布尔值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }
    
    /**
     * 获取布尔值
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (context == null) return defaultValue;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
    
    /**
     * 清除某个键值
     */
    public static void remove(Context context, String key) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }
    
    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }
    
    /**
     * 保存特定用户的抑郁测试分数
     * @param context 上下文
     * @param userId 用户ID
     * @param score 测试分数
     */
    public static void saveUserScore(Context context, String userId, int score) {
        if (context == null || userId == null || userId.isEmpty()) return;
        
        // 构造特定用户的键
        String userScoreKey = USER_SCORE_PREFIX + userId;
        putInt(context, userScoreKey, score);
        
        // 仅同步当前登录用户的分数到通用存储键
        if (userId.equals(getString(context, USER_ID, ""))) {
            putInt(context, "latest_depression_score", score);
        }
        
        LogUtil.i("SPUtil", "保存用户 " + userId + " 的分数: " + score);
    }
    
    /**
     * 获取特定用户的抑郁测试分数
     * @param context 上下文
     * @param userId 用户ID
     * @return 用户的测试分数，如果未找到则返回0
     */
    public static int getUserScore(Context context, String userId) {
        if (context == null || userId == null || userId.isEmpty()) return 0;
        
        // 构造特定用户的键
        String userScoreKey = USER_SCORE_PREFIX + userId;
        int score = getInt(context, userScoreKey, 0);
        
        LogUtil.i("SPUtil", "获取用户 " + userId + " 的分数: " + score);
        return score;
    }
    
    /**
     * 获取当前登录用户的抑郁测试分数
     * @param context 上下文
     * @return 当前用户的测试分数，如果未找到则返回0
     */
    public static int getCurrentUserScore(Context context) {
        if (context == null) return 0;
        
        // 获取当前用户ID
        String userId = getString(context, USER_ID, "");
        if (userId.isEmpty()) {
            userId = BaseApplication.getUserId();
        }
        
        if (userId.isEmpty() || "1".equals(userId)) {
            // 无有效用户ID，返回0或默认分数
            LogUtil.w("SPUtil", "未找到有效用户ID，无法获取用户分数");
            return 0;
        }
        
        // 获取特定用户的分数
        return getUserScore(context, userId);
    }
} 