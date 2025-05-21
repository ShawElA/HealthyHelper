package com.example.healthyolder.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.healthyolder.R;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.LogUtil;
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
    @BindView(R.id.et_nickname)
    EditText et_nickname;
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
    private String user_sex = Configs.SEX_GRIL + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }
    
    private void initView() {
        // 设置注册按钮初始状态
        btn_register.setEnabled(false);
        btn_register.setSelected(false);
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
        
        // 为注册按钮添加点击监听器，避免使用注解方式
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    @OnTextChanged(R.id.et_account)
    public void onAccountChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            isAccount = true;
            et_account.setError(null);
            // 当账号改变时，如果昵称为空，则自动填充昵称
            if (!TextUtil.isValidate(et_nickname.getText())) {
                et_nickname.setText(s);
                // 将光标移到文本末尾
                et_nickname.setSelection(s.length());
            }
        } else {
            isAccount = false;
            et_account.setError("账号不能为空");
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_number)
    public void onNumberChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() == 11) {
            isNumber = true;
            et_number.setError(null);
        } else {
            isNumber = false;
            if (!TextUtil.isValidate(s)) {
                et_number.setError("手机号不能为空");
            } else if (s.length() != 11) {
                et_number.setError("请输入11位手机号");
            }
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_email)
    public void onEmailChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            // 添加邮箱格式验证
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (s.toString().matches(emailPattern)) {
                isEmail = true;
                et_email.setError(null);
            } else {
                isEmail = false;
                et_email.setError("请输入有效的邮箱地址");
            }
        } else {
            isEmail = false;
            et_email.setError("邮箱不能为空");
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_password)
    public void onPasswordChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() >= 6) {
            isPassword = true;
            et_password.setError(null);
        } else {
            isPassword = false;
            if (!TextUtil.isValidate(s)) {
                et_password.setError("密码不能为空");
            } else if (s.length() < 6) {
                et_password.setError("密码长度至少6位");
            }
        }
        setLoginButtonState();
    }

    @OnTextChanged(R.id.et_password_again)
    public void onPasswordAgainChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() >= 6) {
            if (s.toString().equals(et_password.getText().toString())) {
                isPasswordAgain = true;
                et_password_again.setError(null);
            } else {
                isPasswordAgain = false;
                et_password_again.setError("两次输入的密码不一致");
            }
        } else {
            isPasswordAgain = false;
            if (!TextUtil.isValidate(s)) {
                et_password_again.setError("请再次输入密码");
            } else if (s.length() < 6) {
                et_password_again.setError("密码长度至少6位");
            }
        }
        setLoginButtonState();
    }

    // 移除注解，避免重复注册
    public void register(){
        LogUtil.d("RegisterActivity", "Register button clicked");
        
        if (!isAccount || !isNumber || !isEmail || !isPassword || !isPasswordAgain) {
            ToastUtil.showBottomToast("请完成所有必填项");
            return;
        }
        
        if (et_password.getText().toString().equals(et_password_again.getText().toString())){
            Map<String, String> parameter = new HashMap<>();
            String username = et_account.getText().toString();
            String nickname = et_nickname.getText().toString().trim();
            parameter.put("username", username);
            // 只有当昵称为空时才使用用户名作为昵称
            parameter.put("nickname", nickname.isEmpty() ? username : nickname);
            parameter.put("cell_phone", et_number.getText().toString());
            parameter.put("password", et_password.getText().toString());
            parameter.put("email", et_email.getText().toString());
            parameter.put("sex", user_sex);
            parameter.put("icon", "");
            
            // 显示加载对话框
            showProgressDialog("正在注册...");
            
            HttpUtil.getResponse(Urls.REGISTER, parameter, this, new ObjectCallBack<EmptyResult>(EmptyResult.class) {

                @Override
                public void onSuccess(EmptyResult response) {
                    // 隐藏对话框
                    dismissProgressDialog();
                    
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
                    // 隐藏对话框
                    dismissProgressDialog();
                    LogUtil.e("RegisterActivity", "Register failed: " + e.getMessage());
                    ToastUtil.showBottomToast(R.string.registerUnsuccessfully);
                }
            });
        }else {
            ToastUtil.showBottomToast("请确认两次密码是否一致");
        }
    }

    private void setLoginButtonState() {
        if (isAccount && isNumber && isEmail && isPassword && isPasswordAgain) {
            btn_register.setEnabled(true);
            btn_register.setSelected(true);
        } else {
            btn_register.setEnabled(false);
            btn_register.setSelected(false);
        }
    }
}
