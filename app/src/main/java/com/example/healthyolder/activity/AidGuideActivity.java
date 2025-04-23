package com.example.healthyolder.activity;

import android.os.Bundle;
import android.view.View;

import com.example.healthyolder.R;
import com.example.healthyolder.adapter.FragmentAdapter;
import com.example.healthyolder.bean.MethodResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.fragment.CommonItemFragment;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.view.CommonToolBar;
import com.example.healthyolder.view.SlideViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class AidGuideActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aid_guide);
        ButterKnife.bind(this);
        bundle = getIntent().getBundleExtra("Bundle");
        mType = bundle.getString("mType");
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
                    finish();
                }else {
                    //下一步
                    locationIndex ++;
                    addViewPager.setCurrentItem(locationIndex);
                }
            }
        });
    }

    public void initFragments() {
        //初始化标题栏
        if (arrayList.size() == 1){
            rl_commonToolBar.setRightTitleText("完成");
        }else {
            rl_commonToolBar.setRightTitleText("下一步");
        }
        //加载页面，有多少个就加载多少个页面
        for (MethodResult.DataBean dataBean: arrayList){
            fragments.add(CommonItemFragment.newInstance(dataBean));
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

}
