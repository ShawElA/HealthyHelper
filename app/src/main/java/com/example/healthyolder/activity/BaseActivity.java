package com.example.healthyolder.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.healthyolder.R;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

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

    /**
     * 显示加载对话框
     */
    protected void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        if (!isFinishing() && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    /**
     * 检查进度对话框是否正在显示
     */
    protected boolean isProgressDialogShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
