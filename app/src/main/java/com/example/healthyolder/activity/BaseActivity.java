package com.example.healthyolder.activity;

import android.os.Bundle;

import com.example.healthyolder.R;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
        // 设置所有Activity的背景为app_background
        getWindow().setBackgroundDrawableResource(R.drawable.app_background);
    }

    protected void initData() {
    }

    protected void initEvent() {
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
