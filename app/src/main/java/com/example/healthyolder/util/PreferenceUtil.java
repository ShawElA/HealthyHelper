package com.example.healthyolder.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {
    private final String SHARED_PREFERENCE_NAME;
    private static SharedPreferences sp;

    public PreferenceUtil(Context context) {
        SHARED_PREFERENCE_NAME = context.getPackageName();
        sp = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean flag) {
        return sp.getBoolean(key, flag);
    }

    public static void putString(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void clearData() {
        sp.edit().clear().commit();
    }

    public void remove(String key) {
        sp.edit().remove(key).commit();
    }

    public void commit() {
        sp.edit().commit();
    }


}
