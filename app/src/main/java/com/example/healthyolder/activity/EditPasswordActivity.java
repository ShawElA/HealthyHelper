package com.example.healthyolder.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;

public class EditPasswordActivity extends BaseActivity {

    @BindView(R.id.et_enterOriginalPassWord_modifyLoginPassWordDig)
    EditText et_enterOriginalPassWord;
    @BindView(R.id.et_enterNewPassWord_modifyLoginPassWordDig)
    EditText et_enterNewPassWord;
    @BindView(R.id.et_enterNewPassWordAgain_modifyLoginPassWordDig)
    EditText et_enterNewPassWordAgain;
    @BindView(R.id.bt_confirm_modifyLoginPassWordDig)
    Button bt_confirm;
    private boolean isEnterOriginalPassWord = false;
    private boolean isEnterNewPassWord = false;
    private boolean isEnterNewPassWordAgain = false;
    private BasePopupView loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ButterKnife.bind(this);
        bt_confirm.setClickable(false);
        loadingDialog = new XPopup.Builder(this).asLoading("修改中...");
    }

    @OnTextChanged(R.id.et_enterOriginalPassWord_modifyLoginPassWordDig)
    public void onOriginalPassWordTextChanged(CharSequence s) {
        if (TextUtil.isValidate(s)) {
            isEnterOriginalPassWord = true;
        } else {
            isEnterOriginalPassWord = false;
        }
        if (isEnterOriginalPassWord && isEnterNewPassWord && isEnterNewPassWordAgain) {
            bt_confirm.setClickable(true);
            bt_confirm.setSelected(true);
        } else {
            bt_confirm.setClickable(false);
            bt_confirm.setSelected(false);
        }
    }
    @OnTextChanged(R.id.et_enterNewPassWord_modifyLoginPassWordDig)
    public void onNewPassWordTextChanged(CharSequence s) {
        if (TextUtil.isValidate(s)) {
            isEnterNewPassWord = true;
        } else {
            isEnterNewPassWord = false;
        }
        if (isEnterOriginalPassWord && isEnterNewPassWord && isEnterNewPassWordAgain) {
            bt_confirm.setClickable(true);
            bt_confirm.setSelected(true);
        } else {
            bt_confirm.setClickable(false);
            bt_confirm.setSelected(false);
        }
    }
    @OnTextChanged(R.id.et_enterNewPassWordAgain_modifyLoginPassWordDig)
    public void onNewPassWordAgainTextChanged(CharSequence s) {
        if (TextUtil.isValidate(s)) {
            isEnterNewPassWordAgain = true;
        } else {
            isEnterNewPassWordAgain = false;
        }
        if (isEnterOriginalPassWord && isEnterNewPassWord && isEnterNewPassWordAgain) {
            bt_confirm.setClickable(true);
            bt_confirm.setSelected(true);
        } else {
            bt_confirm.setClickable(false);
            bt_confirm.setSelected(false);
        }
    }
    @OnClick(R.id.bt_confirm_modifyLoginPassWordDig)
    public void onConfirmClick(){
        if (!et_enterOriginalPassWord.getText().toString().trim().equals(BaseApplication.getPassword())){
            Log.i("nice", et_enterOriginalPassWord.getText().toString() + " " + BaseApplication.getPassword());
            ToastUtil.showBottomToast("原密码输入不正确");
            return;
        }
        if (!et_enterNewPassWord.getText().toString().equals(et_enterNewPassWordAgain.getText().toString())) {
            ToastUtil.showBottomToast("请确保两次输入的新密码一致");
            return;
        }
        submitEditRequest();
    }

    //提交修改的请求
    private void submitEditRequest(){
        loadingDialog.show();
        Map<String, String> parameter = new HashMap<>();
        parameter.put("u_id", BaseApplication.getUserId());
        parameter.put("new_psd", et_enterNewPassWord.getText().toString());
        HttpUtil.postResponse(Urls.EDITPASSWORD, parameter, this, new ObjectCallBack<EmptyResult>(EmptyResult.class) {

            @Override
            public void onSuccess(EmptyResult response) {
                loadingDialog.dismiss();
                if (response == null) {
                    return;
                }
                if (response.isSuccess()) {
                    BaseApplication.setPassword(et_enterNewPassWord.getText().toString());
                    finish();
                    ToastUtil.showBottomToast(response.getResult());
                } else {
                    ToastUtil.showBottomToast(response.getResult());
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                loadingDialog.dismiss();
                ToastUtil.showBottomToast(R.string.loginUnsuccessfully);
            }
        });
    }
}
