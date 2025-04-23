package com.example.healthyolder.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.healthyolder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/5/28.
 */
public class CommonToolBar extends RelativeLayout {
    @BindView(R.id.rl_commonToolBar)
    RelativeLayout rl;
    @BindView(R.id.iv_leftImg_commonToolBar)
    ImageView iv_leftImg;
    @BindView(R.id.iv_rightImg_commonToolBar)
    ImageView iv_rightImg;
    @BindView(R.id.tv_leftTitle_commonToolBar)
    TextView tv_leftTitle;
    @BindView(R.id.tv_middleTitle_commonToolBar)
    TextView tv_middleTitle;
    @BindView(R.id.tv_rightTitle_commonToolBar)
    TextView tv_rightTitle;
    private Context context;

    public CommonToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if(!isInEditMode()){
            initToolBar();
            initAttrs(attrs);
        }

    }

    private void initToolBar() {
        View view = LayoutInflater.from(context).inflate(R.layout.cv_common_toolbar, this);
        ButterKnife.bind(view);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonToolBar);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CommonToolBar_titleBackGround:
                    rl.setBackgroundColor(a.getColor(attr, Color.WHITE));
                    break;
                case R.styleable.CommonToolBar_isShowLeftImage:
                    if (a.getBoolean(attr, false)) {
                        iv_leftImg.setVisibility(View.VISIBLE);
                    } else {
                        iv_leftImg.setVisibility(View.GONE);
                    }
                    break;
                case R.styleable.CommonToolBar_leftImage:
                    iv_leftImg.setImageResource(a.getResourceId(attr, -1));
                    break;


                case R.styleable.CommonToolBar_isShowLeftText:
                    if (a.getBoolean(attr, false)) {
                        tv_leftTitle.setVisibility(View.VISIBLE);
                    } else {
                        tv_leftTitle.setVisibility(View.GONE);
                    }
                    break;
                case R.styleable.CommonToolBar_leftText:
                    tv_leftTitle.setText(a.getString(attr));
                    break;
                case R.styleable.CommonToolBar_leftTextColor:
                    tv_leftTitle.setTextColor(a.getColor(attr, Color.BLACK));
                    break;


                case R.styleable.CommonToolBar_isShowMiddleText:
                    if (a.getBoolean(attr, false)) {
                        tv_middleTitle.setVisibility(View.VISIBLE);
                    } else {
                        tv_middleTitle.setVisibility(View.GONE);
                    }
                    break;
                case R.styleable.CommonToolBar_middleText:
                    tv_middleTitle.setText(a.getString(attr));
                    break;
                case R.styleable.CommonToolBar_middleTextColor:
                    tv_middleTitle.setTextColor(a.getColor(attr, Color.WHITE));
                    break;

                case R.styleable.CommonToolBar_isShowRightText:
                    if (a.getBoolean(attr, false)) {
                        tv_rightTitle.setVisibility(View.VISIBLE);
                    } else {
                        tv_rightTitle.setVisibility(View.GONE);
                    }
                    break;
                case R.styleable.CommonToolBar_rightText:
                    tv_rightTitle.setText(a.getString(attr));
                    break;
                case R.styleable.CommonToolBar_rightTextColor:
                    tv_rightTitle.setTextColor(a.getColor(attr, Color.BLACK));
                    break;


                case R.styleable.CommonToolBar_isShowRightImage:
                    if (a.getBoolean(attr, false)) {
                        iv_rightImg.setVisibility(View.VISIBLE);
                    } else {
                        iv_rightImg.setVisibility(View.GONE);
                    }
                    break;
                case R.styleable.CommonToolBar_rightImage:
                    iv_rightImg.setImageResource(a.getResourceId(attr, -1));
                    break;
                case R.styleable.CommonToolBar_isLeftImgClickToFinishAcy:
                    if (a.getBoolean(attr, false)) {
                       iv_leftImg.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               if(context instanceof Activity){
                                   ((Activity)context).finish();
                               }
                           }
                       });
                    }
                    break;
            }
        }
        a.recycle();
    }

    //设置左边图片点击事件
    public void setLeftImgOnClickListener(View.OnClickListener listener) {
        iv_leftImg.setOnClickListener(listener);
    }

    //设置右边图片点击事件
    public void setRightImgOnClickListener(View.OnClickListener listener) {
        iv_rightImg.setOnClickListener(listener);
    }

    //设置左边文字点击事件
    public void setLeftTitleOnClickListener(View.OnClickListener listener) {
        tv_leftTitle.setOnClickListener(listener);
    }

    //设置右边文字点击事件
    public void setRightTitleOnClickListener(View.OnClickListener listener) {
        tv_rightTitle.setOnClickListener(listener);
    }

    //设置右边文字内容
    public void setRightTitleText(String text) {
        tv_rightTitle.setText(text);
    }

    public TextView getRightTitleTextView() {
        return tv_rightTitle;
    }

    public ImageView getRightImage() {
        return iv_rightImg;
    }

    //设置标题栏文字内容
    public void setMiddleTitleText(String text){
        tv_middleTitle.setText(text);
    }

    public void setRightTitleColor(int color){
        tv_rightTitle.setTextColor(color);
    }

    //设置右上角图标状态
    public void setRightImgVisible(int arg){
        iv_rightImg.setVisibility(arg);
    }

    //设置左上角图标状态
    public void setLeftImgVisible(int arg){
        iv_leftImg.setVisibility(arg);
    }

    public void setRightImg(int arg){
        iv_rightImg.setImageResource(arg);
    }
}
