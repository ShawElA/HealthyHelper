package com.example.healthyolder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhy.android.percent.support.PercentLinearLayout;


public class InterceptPercentLinearLayout extends PercentLinearLayout {
    boolean isIntercept = false;

    public InterceptPercentLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;

    }

    public void setIntercept(boolean intercept) {
        isIntercept = intercept;
    }
}
