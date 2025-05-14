package com.example.healthyolder.activity;

import android.os.Bundle;

import com.example.healthyolder.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;


public class BaseFragmentAcy extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置所有FragmentActivity的背景为app_background
        getWindow().setBackgroundDrawableResource(R.drawable.app_background);
    }

    protected void initData() {
    }
    protected void initEvent() {
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.acy_enter_anim,R.anim.acy_exit_anim);
    }
}
