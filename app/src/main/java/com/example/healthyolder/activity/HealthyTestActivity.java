package com.example.healthyolder.activity;

import android.os.Bundle;
import android.view.View;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.FragmentAdapter;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.MethodResult;
import com.example.healthyolder.bean.RefreshEvent;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.fragment.TestItemFragment;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.LogUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.SPUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.CommonToolBar;
import com.example.healthyolder.view.SlideViewPager;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class HealthyTestActivity extends BaseActivity {

    @BindView(R.id.rl_commonToolBar)
    public CommonToolBar rl_commonToolBar;
    @BindView(R.id.svp_common)
    SlideViewPager addViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;
    private List<MethodResult.DataBean> arrayList = new ArrayList<>();
    private String mType = "1";
    Bundle bundle;
    private int locationIndex = 0;
    private Float userGoal = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_test);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mType = "7";
        initData();
        initEvent();
    }

    @Override
    protected void initData() {
        super.initData();
        HttpUtil.getResponse(Urls.METHODARRAY, null, null, new ObjectCallBack<MethodResult>(MethodResult.class) {
            @Override
            public void onSuccess(MethodResult response) {
                if (response != null && response.isSuccess()){
                    for (MethodResult.DataBean dataBean: response.getData()){
                        if (dataBean.getM_type().equals(mType)){
                            arrayList.add(dataBean);
                        }
                    }
                }

                if (arrayList.size() != 0){
                    rl_commonToolBar.setMiddleTitleText("1/" + arrayList.size() + "");
                    initFragments();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        rl_commonToolBar.setLeftImgOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationIndex == 0){
                    //当前为首页
                    finish();
                } else {
                    //上一步
                    locationIndex --;
                    addViewPager.setCurrentItem(locationIndex);
                }
            }
        });
        rl_commonToolBar.setRightTitleOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayList.size() - 1 == locationIndex){
                    //仅有一页
                    if (BaseApplication.GoalMap.size() == 20){
                        new XPopup.Builder(HealthyTestActivity.this)
                                .asConfirm("测试已结束", disposeGoal(), new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        saveTestResult();
                                    }
                                }).show();
                    }else {
                        ToastUtil.showBottomToast("您还有测试题未答完");
                    }

                }else {
                    //下一步
                    locationIndex ++;
                    addViewPager.setCurrentItem(locationIndex);
                }
            }
        });
    }

    private String disposeGoal(){
        userGoal = 0f;
        for (int i = 0; i < BaseApplication.GoalMap.size(); i++){
            userGoal += Integer.valueOf(BaseApplication.GoalMap.get(i));
        }
        userGoal = userGoal * 1.25f;
        return "您的分数是" + Math.round(userGoal);
    }

    private String getDepressionLevel(int score) {
        if (score < 60) {
            return "疑重度抑郁";
        } else if (score < 70) {
            return "疑中度抑郁";
        } else if (score < 80) {
            return "疑轻度抑郁";
        } else {
            return "无或最低限度抑郁症状";
        }
    }

    private void saveTestResult() {
        try {
            // 显示保存中的对话框
            showProgressDialog("正在保存测试结果...");
            
            int score = Math.round(userGoal);
            String level = getDepressionLevel(score);
            String userId = SPUtil.getString(this, SPUtil.USER_ID, "");
            
            if (userId == null || userId.isEmpty()) {
                // 尝试从BaseApplication获取
                userId = BaseApplication.getUserId();
                if (userId == null || userId.isEmpty() || "1".equals(userId)) {
                    dismissProgressDialog();
                    ToastUtil.showBottomToast("用户ID不能为空，请重新登录");
                    return;
                }
            }
            
            // 先预先更新本地数据，即使网络请求失败，至少本地UI能显示新分数
            String scoreStr = String.valueOf(score);
            BaseApplication.setDepressionScore(scoreStr);
            PreferenceUtil.putString("goal", scoreStr);
            SPUtil.putString(getApplicationContext(), "depression_score", scoreStr);
            SPUtil.putString(getApplicationContext(), SPUtil.USER_ID, userId);
            
            // 记录当前时间为最近测试时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            BaseApplication.setLastTestTime(currentTime);
            
            // 准备测试结果数据
            Map<String, Object> params = new HashMap<>();
            try {
                params.put("userId", Integer.parseInt(userId));
            } catch (NumberFormatException e) {
                dismissProgressDialog();
                ToastUtil.showBottomToast("用户ID格式错误，请重新登录");
                return;
            }
            params.put("score", score);
            params.put("level", level);
            params.put("answers", new Gson().toJson(BaseApplication.GoalMap));

            // 记录调试信息
            LogUtil.i("Depression Test", "Saving test result: score=" + score + ", level=" + level + ", userId=" + userId);
            
            // 保存测试结果到服务器，使用预定义的URL常量
            HttpUtil.post(Urls.SAVE_DEPRESSION_TEST, params, response -> {
                dismissProgressDialog();
                try {
                    LogUtil.i("Depression Test Response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("success")) {
                        // 发送刷新事件通知首页和其他界面更新
                        EventBus.getDefault().post(new RefreshEvent());
                        
                        runOnUiThread(() -> {
                            ToastUtil.showBottomToast("保存成功，得分：" + score);
                            // 返回首页
                            finish();
                        });
                    } else {
                        // 即使保存失败，本地UI也会显示新分数
                        runOnUiThread(() -> {
                            try {
                                ToastUtil.showBottomToast("服务器保存失败：" + jsonObject.getString("result") + "，但本地已更新");
                                // 仍然返回首页，因为本地数据已更新
                                finish();
                            } catch (Exception e) {
                                ToastUtil.showBottomToast("服务器保存失败，但本地已更新");
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        ToastUtil.showBottomToast("网络错误：" + e.getMessage() + "，但本地已更新");
                        // 仍然返回首页，因为本地数据已更新
                        finish();
                    });
                }
            });
        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
            ToastUtil.showBottomToast("保存失败：" + e.getMessage());
        }
    }

    public void initFragments() {
        //初始化标题栏
        if (arrayList.size() == 1){
            rl_commonToolBar.setRightTitleText("完成");
        }else {
            rl_commonToolBar.setRightTitleText("下一步");
        }
        //加载页面，有多少个就加载多少个页面
        for (int i = 0; i < arrayList.size(); i++){
            fragments.add(TestItemFragment.newInstance(i, arrayList.get(i)));
        }
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        fragmentAdapter.setFragments(fragments);
        addViewPager.setAdapter(fragmentAdapter);
        addViewPager.setSlide(false);
        addViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (arrayList.size() - 1 == locationIndex){
                    //最后一页
                    rl_commonToolBar.setRightTitleText("完成");
                }else {
                    if (locationIndex + 1 == arrayList.size()) {
                        //最后一页
                        rl_commonToolBar.setRightTitleText("完成");
                    }else {
                        rl_commonToolBar.setRightTitleText("下一步");
                    }
                }
                rl_commonToolBar.setMiddleTitleText((position+1) + "/" + arrayList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event){

    }
    
    /**
     * 进入下一题的方法，供Fragment调用
     */
    public void goToNextQuestion() {
        // 如果不是最后一题，就进入下一题
        if (locationIndex < arrayList.size() - 1) {
            locationIndex++;
            addViewPager.setCurrentItem(locationIndex);
        } else {
            // 如果是最后一题并且所有题目都已完成
            if (BaseApplication.GoalMap.size() == 20) {
                new XPopup.Builder(HealthyTestActivity.this)
                        .asConfirm("测试已结束", disposeGoal(), new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                saveTestResult();
                            }
                        }).show();
            } else {
                ToastUtil.showBottomToast("您还有测试题未答完");
            }
        }
    }
}
