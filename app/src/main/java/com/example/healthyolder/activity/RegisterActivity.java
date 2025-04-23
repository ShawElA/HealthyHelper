package com.example.healthyolder.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.healthyolder.R;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.Urls;
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

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText et_account;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_password_again)
    EditText et_password_again;
    @BindView(R.id.btn_register)
    AppCompatButton btn_register;
    @BindView(R.id.rg_sex)
    RadioGroup rg_sex;
    boolean isAccount = false;
    boolean isNumber = false;
    boolean isEmail = false;
    boolean isPassword = false;
    boolean isPasswordAgain = false;
    private String user_sex = Configs.SEX_GRIL + "";            //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initEvent();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_girl:
                        user_sex = Configs.SEX_GRIL + "";
                        break;
                    case R.id.rb_boy:
                        user_sex = Configs.SEX_BOY + "";
                        break;
                }
            }
        });
    }

    @OnTextChanged(R.id.et_account)
    public void onAccountChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            isAccount = true;
        } else {
            isAccount = false;
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_number)
    public void onNumberChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() == 11) {
            isNumber = true;
        } else {
            isNumber = false;
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_email)
    public void onEmailChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            isEmail = true;
        } else {
            isEmail = false;
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_password)
    public void onPasswordChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() >= 6) {
            isPassword = true;
        } else {
            isPassword = false;
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_password_again)
    public void onPasswordAgainChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() >= 6) {
            isPasswordAgain = true;
        } else {
            isPasswordAgain = false;
        }
        setLoginButtonState();
    }

    @OnClick(R.id.btn_register)
    public void register(){
        if (et_password.getText().toString().equals(et_password_again.getText().toString())){
            Map<String, String> parameter = new HashMap<>();
            parameter.put("username", et_account.getText().toString());
            parameter.put("cell_phone", et_number.getText().toString());
            parameter.put("password", et_password.getText().toString());
            parameter.put("email", et_email.getText().toString());
            parameter.put("sex", user_sex);
            parameter.put("icon", "");
            HttpUtil.getResponse(Urls.REGISTER, parameter, this, new ObjectCallBack<EmptyResult>(EmptyResult.class) {

                @Override
                public void onSuccess(EmptyResult response) {
                    if (response == null) {
                        return;
                    }
                    if (response.isSuccess()) {
                        //注册成功
                        ToastUtil.showBottomToast("注册成功");
                        IntentUtil.startActivity(RegisterActivity.this, LoginActivity.class);
                        finish();
                    } else {
                        ToastUtil.showBottomToast(response.getResult());
                    }
                }

                @Override
                public void onFail(Call call, Exception e) {
                    ToastUtil.showBottomToast(R.string.registerUnsuccessfully);
                }
            });
        }else {
            ToastUtil.showBottomToast("请确认两次密码是否一致");
        }

    }

    private void setLoginButtonState() {
        if (isAccount && isNumber && isEmail && isPassword && isPasswordAgain) {
            btn_register.setClickable(true);
            btn_register.setSelected(true);
        } else {
            btn_register.setClickable(false);
            btn_register.setSelected(false);
        }
    }
}
