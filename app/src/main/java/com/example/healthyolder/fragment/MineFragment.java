package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.activity.AboutActivity;
import com.example.healthyolder.activity.EditPasswordActivity;
import com.example.healthyolder.activity.LoginActivity;
import com.example.healthyolder.activity.MineActivity;
import com.example.healthyolder.activity.MyFavActivity;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.EmptyResult;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.SPUtil;
import com.example.healthyolder.util.ToastUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class MineFragment extends Fragment {
    @BindView(R.id.settingNickname)
    TextView settingNickname;
    @BindView(R.id.settingAvatar)
    ImageView settingAvatar;
    View view;

    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
     
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.rl_fav)
    public void editMyFav(){
        IntentUtil.startActivity(getActivity(), MyFavActivity.class);
    }

    @OnClick(R.id.rl_edit_mine)
    public void editMine(){
        IntentUtil.startActivity(getActivity(), MineActivity.class);
    }

    @OnClick(R.id.rl_edit_pwd)
    public void editPwd(){
        IntentUtil.startActivity(getActivity(), EditPasswordActivity.class);
    }

    @OnClick(R.id.rl_about)
    public void about(){
        IntentUtil.startActivity(getActivity(), AboutActivity.class);
    }

    @OnClick(R.id.rl_logout)
    public void logout(){
        new XPopup.Builder(getContext())
                .asConfirm("温馨提示", "确定退出吗",
                "取消", "确定",
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        getActivity().finish();
                    }
                }, null, false)
                .show();
    }
    
    @OnClick(R.id.rl_delete_account)
    public void deleteAccount(){
        new XPopup.Builder(getContext())
                .asConfirm("注销账号", "注销账号后，您的所有数据将被删除且无法恢复，确定要注销账号吗？",
                "取消", "确定注销",
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        // 执行账号注销
                        performAccountDeletion();
                    }
                }, null, false)
                .show();
    }
    
    /**
     * 执行账号注销操作
     */
    private void performAccountDeletion() {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("u_id", BaseApplication.getUserId());
        
        HttpUtil.postResponse(Urls.DELETEACCOUNT, parameter, getActivity(), new ObjectCallBack<EmptyResult>(EmptyResult.class) {
            @Override
            public void onSuccess(EmptyResult response) {
                if (response == null) {
                    return;
                }
                
                if (response.isSuccess()) {
                    ToastUtil.showBottomToast("账号已成功注销");
                    
                    // 清除本地用户数据
                    clearLocalUserData();
                    
                    // 返回登录页面
                    IntentUtil.startActivityAndFinishAll(getActivity(), LoginActivity.class);
                } else {
                    ToastUtil.showBottomToast(response.getResult());
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast("注销失败，请重试");
            }
        });
    }
    
    /**
     * 清除本地用户数据
     */
    private void clearLocalUserData() {
        // 清空BaseApplication中的用户数据
        BaseApplication.setUserId("");
        BaseApplication.setUserNickname("");
        BaseApplication.setPassword("");
        BaseApplication.setIcon("");
        
        // 清空SPUtil中的用户数据
        if (getContext() != null) {
            SPUtil.putString(getContext(), SPUtil.USER_ID, "");
            SPUtil.putString(getContext(), SPUtil.USER_NAME, "");
        }
        
        // 清空PreferenceUtil中的用户数据
        PreferenceUtil.putString("userId", "");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.fragment_mine, container, false);
        }
        ButterKnife.bind(this, view);
        init();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        settingNickname.setText(BaseApplication.getUserName());
        if (BaseApplication.getIcon().contains(Configs.PIC)){
            Glide.with(getActivity()).load(Urls.baseUrl + BaseApplication.getIcon()).
                    placeholder(R.drawable.bg_head_circle).error(R.drawable.default_head).into(settingAvatar);
        }else {
            Glide.with(getActivity()).load(BaseApplication.getIcon()).
                    placeholder(R.drawable.bg_head_circle).error(R.drawable.default_head).into(settingAvatar);
        }
    }

    private void initListener() {

    }
}
