package com.example.healthyolder.activity;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.healthyolder.R;
import com.example.healthyolder.fragment.AppointmentFragment;
import com.example.healthyolder.fragment.CalculateFragment;
import com.example.healthyolder.fragment.HomeFragment;
import com.example.healthyolder.fragment.MineFragment;
import com.example.healthyolder.fragment.NoteFragment;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private String[] tabText = {"首页", "咨询", "预约挂号", "赋能减压", "我的"};
    private int[] normalIcon = {R.mipmap.sport_normal, R.mipmap.square_normal, R.mipmap.appointment_normal, R.mipmap.pressure_normal, R.mipmap.circle_normal};
    private int[] selectIcon = {R.mipmap.sport_select, R.mipmap.square_select, R.mipmap.appointment_select, R.mipmap.pressure_select, R.mipmap.circle_select};
    private List<Fragment> fragments = new ArrayList<>();
    private EasyNavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //底部导航
        initNav();
    }

    private void initNav() {
        fragments.add(new HomeFragment());
        fragments.add(new CalculateFragment());
        fragments.add(new AppointmentFragment());
        fragments.add(new NoteFragment());
        fragments.add(new MineFragment());
        navigationBar = findViewById(R.id.navigationBar);
        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .iconSize(22)     //Tab图标大小
                .tabTextSize(12)   //Tab文字大小
                .normalTextColor(getResources().getColor(R.color.colorDefaultText))   //Tab未选中时字体颜色
                .selectTextColor(getResources().getColor(R.color.colorTheme))   //Tab选中时字体颜色
                .navigationBackground(getResources().getColor(R.color.bg_white))   //导航栏背景色
                .lineColor(getResources().getColor(R.color.colorTopLine)) //分割线颜色
                .smoothScroll(false)  //点击Tab  Viewpager切换是否有动画
                .canScroll(false)    //Viewpager能否左右滑动
//                .anim(Anim.ZoomIn)  //点击Tab时的动画
                .hintPointLeft(-3)  //调节提示红点的位置hintPointLeft hintPointTop（看文档说明）
                .hintPointTop(-7)
                .hintPointSize(6)    //提示红点的大小
                .msgPointLeft(-10)  //调节数字消息的位置msgPointLeft msgPointTop（看文档说明）
                .msgPointTop(-15)
                .msgPointTextSize(9)  //数字消息中字体大小
                .msgPointSize(18)    //数字消息红色背景的大小
                .build();
        navigationBar.getmViewPager().setOffscreenPageLimit(4);
    }
}
