package com.example.healthyolder.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.RefreshIconEvent;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.bean.UserInfo;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.util.VideoUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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
    @BindView(R.id.et_account)
    EditText et_id;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.et_email)
    EditText et_emial;
    @BindView(R.id.et_register_date)
    EditText et_register_date;
    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.btn_edit)
    AppCompatButton btn_edit;
    boolean isName = false;
    boolean isNumber = false;
    boolean isEmail = false;
    boolean isIcon = false;
    private String iconPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
    }

    @OnClick(R.id.iv_head)
    public void changeIcon(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "image/png");
        startActivityForResult(intent, Configs.REQUESTCODE_CHOOSEIMAGE);
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
                    et_name.setText(response.getData().get(0).getNickname());
                    et_id.setText(response.getData().get(0).getU_id());
                    et_number.setText(response.getData().get(0).getU_mobile());
                    et_emial.setText(response.getData().get(0).getU_email());
                    et_register_date.setText(response.getData().get(0).getU_time());
                    if (response.getData().get(0).getU_icon().toLowerCase().contains("img")){
                        Glide.with(MineActivity.this).load(Urls.baseUrl + response.getData().get(0).getU_icon())
                                .placeholder(R.drawable.default_head).into(iv_head);
                    }else {
                        Glide.with(MineActivity.this).load(response.getData().get(0).getU_icon())
                                .placeholder(R.drawable.default_head).into(iv_head);
                    }
                    iconPath = response.getData().get(0).getU_icon();
                    btn_edit.setSelected(false);
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

    @OnClick(R.id.btn_edit)
    public void edit(){
        if (isIcon){
            //如果有修改头像，需要先上传头像
            saveImg();
        }else {
            savePersonInfo();
        }
    }

    private void setEditStatus(){
        if (isName || isNumber || isEmail || isIcon) {
            btn_edit.setEnabled(true);
            btn_edit.setSelected(true);
        } else {
            btn_edit.setEnabled(false);
            btn_edit.setSelected(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Configs.REQUESTCODE_CHOOSEIMAGE && data != null && data.getData() != null){
            Uri uri=data.getData();
            iconPath = VideoUtil.getRealPathFromURI(this, uri);
            initIcon();
        }
        setEditStatus();
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
                    btn_edit.setSelected(false);
                    btn_edit.setEnabled(false);
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

    private void saveImg(){
        Map<String, String> params = new HashMap<>();
        params.put("name",  (int)(Math.random()*(10000000-1+1)+10000000) + "");
        HttpUtil.postImgFile(Urls.UPLOADIMGS, "1615.jpg", params, new File(iconPath), null, new ObjectCallBack<EmptyResult>(EmptyResult.class) {
            @Override
            public void onSuccess(EmptyResult response) {
                ToastUtil.showBottomToast(response.getResult());
                if (response.isSuccess()){
                    iconPath = response.getData();
                    isIcon = false;
                    savePersonInfo();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
//                progressDialog.dismiss();
            }
        });
    }

    //更新头像
    public void initIcon(){
//        ToastUtil.showBottomToast("路径为" + iconPath);
        Glide.with(this).load(iconPath).placeholder(R.drawable.default_head).into(iv_head);
        isIcon = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshIcon(RefreshIconEvent event){

    }
}
