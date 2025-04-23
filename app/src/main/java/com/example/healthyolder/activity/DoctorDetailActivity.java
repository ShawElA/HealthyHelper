package com.example.healthyolder.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.MyPageAdapter;
import com.example.healthyolder.bean.CommonResult;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.RefreshAddressEvent;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.fragment.CourseCommentFrag;
import com.example.healthyolder.fragment.DoctorBriefFrag;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ScreenUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.CommonToolBar;
import com.example.healthyolder.view.TextViewPlus;
import com.google.android.material.tabs.TabLayout;
import com.zhy.android.percent.support.PercentLinearLayout;

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
import butterknife.OnClick;
import okhttp3.Call;

public class DoctorDetailActivity extends BaseFragmentAcy{
    @BindView(R.id.tl_title_courseDetailAcy)
    TabLayout tl_title;
    @BindView(R.id.vp_showFragment_courseDetailAcy)
    ViewPager vp_showFragment;
    @BindView(R.id.miv_courseImg_courseDetailAcy)
    ImageView cover;
    @BindView(R.id.tv_courseName_courseDetailAcy)
    TextView tv_title;
    @BindView(R.id.tv_purchaseNumber_courseDetailAcy)
    TextView tv_purchaseNum;
    @BindView(R.id.ctl_title)
    CommonToolBar ctl_title;
    private List<String> titleList;
    private MyPageAdapter pageAdapter;
    private int textSize;
    private DoctorBriefFrag doctorBriefFrag;
    private CourseCommentFrag courseCommentFrag;
    private String courseBrief, teachObjective, courseId, dname;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        bundle = getIntent().getBundleExtra("Bundle");
        initData();
        initTabLayout();
    }

    @Override
    protected void initData() {
        courseBrief = bundle.getString("intro");
        teachObjective = Configs.HOSPITAL_TIP;
        courseId = bundle.getString("c_id");
        dname = bundle.getString("dname");
        tv_title.setText(bundle.getString("title"));
        if (bundle.getString("pic").toLowerCase().contains("uploads")){
            Glide.with(DoctorDetailActivity.this).load(Urls.baseUrl + bundle.getString("pic")).placeholder(R.mipmap.default_head).error(R.mipmap.default_head).into(cover);
        }else {
            Glide.with(DoctorDetailActivity.this).load(bundle.getString("pic")).placeholder(R.mipmap.default_head).error(R.mipmap.default_head).into(cover);
        }

        tv_purchaseNum.setText("所属科室:" + dname);
    }

    private void initTabLayout() {
        titleList = new ArrayList<>();
        titleList.add("简介");
        titleList.add("时间");
        textSize = (int) (ScreenUtil.getScreenWidth() * 0.048);
        initViewPager();
        tl_title.setupWithViewPager(vp_showFragment);//将TabLayout和ViewPager关联起来
        initTab();
    }

    private void initTab() {
        for (int i = 0; i < titleList.size(); i++) {
            tl_title.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    public View getTabView(int position) {
        TextView tv_title = new TextView(this);
        tv_title.setTextColor(getResources().getColorStateList(R.color.sel_tablayout_title_text_color));
        tv_title.setGravity(Gravity.CENTER);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(titleList.get(position));
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(textSize), 0, titleList.get(position).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tv_title.setText(spannableStringBuilder);
        return tv_title;
    }

    private void initViewPager() {
        doctorBriefFrag = new DoctorBriefFrag();
        Bundle data = new Bundle();
        data.putString("CourseId", courseId);
        data.putString("CourseBrief", courseBrief);
        data.putString("TeachObjective", teachObjective);
        doctorBriefFrag.setArguments(data);
        courseCommentFrag = new CourseCommentFrag();
        courseCommentFrag.setArguments(data);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(doctorBriefFrag);
        fragmentList.add(courseCommentFrag);
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragmentList);
        vp_showFragment.setAdapter(pageAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComment(RefreshAddressEvent event){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
