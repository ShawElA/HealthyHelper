package com.example.healthyolder.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.healthyolder.R;

import androidx.fragment.app.Fragment;


/**
 * Created by Administrator
 */
public class IntentUtil {
    private static long currentTime; //解决startActivityForResult导致SingleTop失效问题

 /* 以下是Activity启动Activity*/
    /**
     * 启动Activity(不带参数)
     *
     * @param activity
     * @param cls
     */
    public static void startActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * 启动Activity(带参数)
     *
     * @param activity
     * @param cls
     * @param bundle
     */
    public static void startActivity(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra("Bundle", bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    /**
     * 启动Activity(带Flags参数)
     * @param context
     * @param cls
     * @param flags
     */
    public static void startActivityWithFlags(Context context, Class<?> cls, int flags) {
        Intent intent = new Intent(context, cls);

        intent.setFlags(flags);
        context.startActivity(intent);
//        activity.overridePendingTransition(R.anim.acy_enter_anim,R.anim.acy_exit_anim);
    }

    /**
     *
     * @param context
     * @param cls
     * @param flags
     * @param bundle
     */
    public static void startActivityWithFlags(Context context, Class<?> cls, int flags, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("Bundle", bundle);
        intent.setFlags(flags);
        context.startActivity(intent);
//        context.overridePendingTransition(R.anim.acy_enter_anim,R.anim.acy_exit_anim);
    }
    /**
     * Activity启动另一个Activity并返回结果(带参数)
     *
     * @param activity
     * @param cls
     * @param requestCode
     * @param bundle
     */
    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode, Bundle bundle) {
        if ((System.currentTimeMillis() - currentTime) < 500) return;
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(activity, cls);
        intent.putExtra("Bundle", bundle);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    /**
     * Activity启动另一个Activity并返回结果(不带参数)
     *
     * @param activity
     * @param cls
     * @param requestCode
     */
    public static void startActivityForResult(Activity activity, Class<?> cls, int requestCode) {
        if ((System.currentTimeMillis() - currentTime) < 500) return;
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(activity, cls);
        activity.startActivityForResult(intent, requestCode);
    }


/* 以下是Fragment启动Activity*/

    /**
     * Fragment启动另一个Activity并返回结果(不带参数)
     *
     * @param activity
     * @param fragment
     * @param cls
     * @param requestCode
     */
    public static void startActivityForResult(Activity activity, Fragment fragment, Class<?> cls, int requestCode) {
        if ((System.currentTimeMillis() - currentTime) < 500) return;
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(activity, cls);
        fragment.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    /**
     * Fragment启动另一个Activity并返回结果(带参数)
     *
     * @param activity
     * @param fragment
     * @param cls
     * @param requestCode
     * @param bundle
     */
    public static void startActivityForResult(Activity activity, Fragment fragment, Class<?> cls, int requestCode, Bundle bundle) {
        if ((System.currentTimeMillis() - currentTime) < 500) return;
        currentTime = System.currentTimeMillis();
        Intent intent = new Intent(activity, cls);
        intent.putExtra("Bundle", bundle);
        fragment.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
