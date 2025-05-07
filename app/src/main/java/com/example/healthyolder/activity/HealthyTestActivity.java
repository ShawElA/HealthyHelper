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
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.CommonToolBar;
import com.example.healthyolder.view.SlideViewPager;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//        bundle = getIntent().getBundleExtra("Bundle");
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
                                        uploadGoal(Math.round(userGoal) + "");
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

    private void uploadGoal(String goal){
        PreferenceUtil.putString("goal", goal);
        EventBus.getDefault().post(new RefreshEvent());
        finish();
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
                                uploadGoal(Math.round(userGoal) + "");
                            }
                        }).show();
            } else {
                ToastUtil.showBottomToast("您还有测试题未答完");
            }
        }
    }
}
