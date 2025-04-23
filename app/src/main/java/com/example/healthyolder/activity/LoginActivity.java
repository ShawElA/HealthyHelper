package com.example.healthyolder.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.bean.UserInfo;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.btn_login)
    AppCompatButton btn_login;
    @BindView(R.id.tv_register)
    TextView tv_register;
    @BindView(R.id.et_account)
    EditText et_account;
    @BindView(R.id.et_password)
    EditText et_password;
    private boolean isEnterName = false;
    private boolean isEnterPassWord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void clickToLogin(){
        userLogin();
    }

    @OnClick(R.id.tv_register)
    public void clickToRegister(){
        IntentUtil.startActivity(LoginActivity.this, RegisterActivity.class);
    }

    @OnTextChanged(R.id.et_account)
    public void onCellPhoneNumberChange(CharSequence s) {
//        setHeadPortrait(s);
        if (TextUtil.isValidate(s)) {
            isEnterName = true;
        } else {
            isEnterName = false;
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_password)
    public void onPassWordChange(CharSequence s) {
        if (TextUtil.isValidate(s)) {
            isEnterPassWord = true;
        } else {
            isEnterPassWord = false;
        }
        setLoginButtonState();
    }

    private void userLogin(){
        Map<String, String> parameter = new HashMap<>();
        parameter.put("username", et_account.getText().toString());
        parameter.put("password", et_password.getText().toString());
        HttpUtil.getResponse(Urls.LOGIN, parameter, this, new ObjectCallBack<UserInfo>(UserInfo.class) {

            @Override
            public void onSuccess(UserInfo response) {
                if (response == null) {

                    return;
                }
                if (response.isSuccess()) {
                    ToastUtil.showBottomToast("登录成功");
                    //登录成功
                    BaseApplication.setUserId(response.getData().get(0).getU_id());
                    BaseApplication.setUserName(response.getData().get(0).getU_name());
                    BaseApplication.setPassword(et_password.getText().toString());
                    BaseApplication.setIcon(response.getData().get(0).getU_icon());
                    IntentUtil.startActivity(LoginActivity.this, MainActivity.class);
                    finish();

                } else {
                    ToastUtil.showBottomToast(response.getResult());
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loginUnsuccessfully);
            }
        });

    }

    private void setLoginButtonState() {
        if (isEnterName && isEnterPassWord) {
            btn_login.setEnabled(true);
            btn_login.setSelected(true);
        } else {
            btn_login.setEnabled(false);
            btn_login.setSelected(false);
        }
    }
}
