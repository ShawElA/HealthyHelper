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
import com.example.healthyolder.util.SPUtil;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.LogUtil;

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
                    String userId = response.getData().get(0).getU_id();
                    
                    // 1. 更新BaseApplication
                    BaseApplication.setUserId(userId);
                    BaseApplication.setUserNickname(response.getData().get(0).getNickname());
                    BaseApplication.setPassword(et_password.getText().toString());
                    BaseApplication.setIcon(response.getData().get(0).getU_icon());
                    
                    // 2. 使用多种方式存储用户ID，确保各处能获取到
                    SPUtil.putString(LoginActivity.this, SPUtil.USER_ID, userId);
                    SPUtil.putString(LoginActivity.this, SPUtil.USER_NAME, response.getData().get(0).getNickname());
                    
                    // 3. 额外使用PreferenceUtil存储用户ID
                    PreferenceUtil.putString("userId", userId);
                    
                    // 4. 检查是否存储成功
                    String spUserId = SPUtil.getString(LoginActivity.this, SPUtil.USER_ID, "");
                    String prefUserId = PreferenceUtil.getString("userId");
                    LogUtil.i("Login", "用户ID同步: BaseApplication=" + BaseApplication.getUserId() 
                            + ", SPUtil=" + spUserId 
                            + ", PreferenceUtil=" + prefUserId);
                    
                    // 改为从depression_test_history表获取最新抑郁测试信息
                    // 临时兼容，使用旧字段，后续应替换为API调用获取历史记录
                    // TODO: 在未来版本中完全移除此兼容代码，改为直接查询depression_test_history表
                    if (response.getData().get(0).getDepression_score() != null) {
                        String score = response.getData().get(0).getDepression_score();
                        // 转换为用户特定存储，而不是全局存储
                        try {
                            int scoreInt = Integer.parseInt(score);
                            // 保存到用户特定存储
                            SPUtil.saveUserScore(LoginActivity.this, userId, scoreInt);
                            LogUtil.i("Login", "保存用户 " + userId + " 的抑郁分数: " + scoreInt);
                        } catch (NumberFormatException e) {
                            LogUtil.e("Login", "分数格式错误: " + score);
                        }
                        
                        // 兼容老代码，同步保存到全局变量
                        BaseApplication.setDepressionScore(score);
                        PreferenceUtil.putString("goal", score);
                    }
                    
                    // TODO: 在下一版本中移除此兼容代码
                    if (response.getData().get(0).getLast_test_time() != null) {
                        BaseApplication.setLastTestTime(response.getData().get(0).getLast_test_time());
                    }
                    
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
