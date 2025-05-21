package com.example.healthyolder.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.RefreshIconEvent;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.bean.UserInfo;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;

public class MineActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.et_account)
    EditText et_id;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.et_email)
    EditText et_emial;
    @BindView(R.id.btn_save)
    AppCompatButton btn_save;
    
    boolean isName = false;
    boolean isNumber = false;
    boolean isEmail = false;
    private String iconPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
    }

    protected void initData(){
        Map<String, String> parameter = new HashMap<>();
        parameter.put("u_id", BaseApplication.getUserId());
        HttpUtil.getResponse(Urls.USERDATA, parameter, this, new ObjectCallBack<UserInfo>(UserInfo.class) {

            @Override
            public void onSuccess(UserInfo response) {
                if (response == null) {
                    return;
                }
                if (response.isSuccess()) {
                    ToastUtil.showBottomToast(response.getResult());
                    et_username.setText(response.getData().get(0).getUsername());
                    et_name.setText(response.getData().get(0).getNickname());
                    et_id.setText(response.getData().get(0).getU_id());
                    et_number.setText(response.getData().get(0).getU_mobile());
                    et_emial.setText(response.getData().get(0).getU_email());
                    iconPath = response.getData().get(0).getU_icon();
                    btn_save.setSelected(false);
                } else {
                    ToastUtil.showBottomToast(response.getResult());
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
            }
        });
    }

    @OnTextChanged(R.id.et_name)
    public void onNameChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            isName = true;
        } else {
            isName = false;
        }
        setEditStatus();
    }

    @OnTextChanged(R.id.et_number)
    public void onNumberChange(CharSequence s){
        if (TextUtil.isValidate(s) && s.length() == 11) {
            isNumber = true;
        } else {
            isNumber = false;
        }
        setEditStatus();
    }

    @OnTextChanged(R.id.et_email)
    public void onEmailChange(CharSequence s){
        if (TextUtil.isValidate(s)) {
            isEmail = true;
        } else {
            isEmail = false;
        }
        setEditStatus();
    }

    @OnClick(R.id.btn_save)
    public void saveProfile(){
        savePersonInfo();
    }

    private void setEditStatus(){
        if (isName || isNumber || isEmail) {
            btn_save.setEnabled(true);
            btn_save.setSelected(true);
        } else {
            btn_save.setEnabled(false);
            btn_save.setSelected(false);
        }
    }

    private void savePersonInfo(){
        Map<String, String> parameter = new HashMap<>();
        parameter.put("u_id", BaseApplication.getUserId());
        parameter.put("phone", et_number.getText().toString());
        parameter.put("username", et_name.getText().toString());
        parameter.put("e_mail", et_emial.getText().toString());
        parameter.put("icon", iconPath);
        HttpUtil.postResponse(Urls.EDITDATA, parameter, this, new ObjectCallBack<EmptyResult>(EmptyResult.class) {

            @Override
            public void onSuccess(EmptyResult response) {
                if (response == null) {
                    return;
                }
                if (response.isSuccess()) {
                    ToastUtil.showBottomToast("修改成功");
                    BaseApplication.setUserNickname(et_name.getText().toString());
                    BaseApplication.setIcon(iconPath);
                    btn_save.setSelected(false);
                    btn_save.setEnabled(false);
                } else {
                    ToastUtil.showBottomToast(response.getResult());
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshIcon(RefreshIconEvent event){
        // 保留事件监听方法
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
