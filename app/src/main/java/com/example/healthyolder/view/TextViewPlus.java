package com.example.healthyolder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.util.ScreenUtil;

public class TextViewPlus extends TextView {
    public final static int LEFT_IMG = 0;
    public final static int TOP_IMG = 1;
    public final static int RIGHT_IMG = 2;
    public final static int BOTTOM_IMG = 3;

    private String referenceObject="ScreenHeight"; //参考对象，屏幕宽度或高度
    // 需要从xml中读取的各个方向图片的宽和高的百分比
    private float leftHeightScale = -1.0f;
    private float leftWidthScale = -1.0f;
    private float rightHeightScale = -1.0f;
    private float rightWidthScale = -1.0f;
    private float topHeightScale = -1.0f;
    private float topWidthScale = -1.0f;
    private float bottomHeightScale = -1.0f;
    private float bottomWidthScale = -1.0f;


    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }


    /**
     * 初始化读取参数
     */
    private void init(Context context, AttributeSet attrs) {
        // TypeArray中含有我们需要使用的参数
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        if (a != null) {
            // 获得参数个数
            int count = a.getIndexCount();
            int index = 0;
            // 遍历参数。先将index从TypedArray中读出来，
            // 得到的这个index对应于attrs.xml中设置的参数名称在R中编译得到的数
            // 这里会得到各个方向的宽和高比例
            for (int i = 0; i < count; i++) {
                index = a.getIndex(i);
                switch (index) {
                    case R.styleable.TextViewPlus_topImgWidthScale:
                        topWidthScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_topImgHeightScale:
                        topHeightScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_bottomImgWidthScale:
                        bottomWidthScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_bottomImgHeightScale:
                        bottomHeightScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_leftImgWidthScale:
                        leftWidthScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_leftImgHeightScale:
                        leftHeightScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_rightImgWidthScale:
                        rightWidthScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_rightImgHeightScale:
                        rightHeightScale = a.getFloat(index, -1.0f);
                        break;
                    case R.styleable.TextViewPlus_referenceObject:
                        referenceObject = a.getString(index);
                        break;
                }
            }
            a.recycle();
            // 获取各个方向的图片，按照：左-上-右-下 的顺序存于数组中
            Drawable[] drawables = getCompoundDrawables();
            int dir = 0;
            // 0-left; 1-top; 2-right; 3-bottom;
            for (Drawable drawable : drawables) {
                // 设定图片大小
                setImageSize(drawable, dir++);
            }
            // 将图片放回到TextView中
            setCompoundDrawables(drawables[0], drawables[1], drawables[2],
                    drawables[3]);
        }
    }


    //以屏幕高度为标准，按一定比例设置图片大小
    public void setCompoundImg(int direction, int imgResourceId, float widthScale, float heightScale, String referenceObject) {
        Drawable drawable = BaseApplication.getContext().getResources().getDrawable(imgResourceId);
        if(referenceObject.equals("ScreenHeight")){
            drawable.setBounds(0, 0, (int) (widthScale * ScreenUtil.getScreenHeight()), (int) (heightScale * ScreenUtil.getScreenHeight()));
        }else{
            drawable.setBounds(0, 0, (int) (widthScale * ScreenUtil.getScreenWidth()), (int) (heightScale * ScreenUtil.getScreenWidth()));
        }
        switch (direction) {
            case TextViewPlus.LEFT_IMG:
                setCompoundDrawables(drawable, null, null,
                        null);
                break;
            case TextViewPlus.TOP_IMG:
                setCompoundDrawables(null, drawable, null,
                        null);
                break;
            case TextViewPlus.RIGHT_IMG:
                setCompoundDrawables(null, null, drawable,
                        null);
                break;
            case TextViewPlus.BOTTOM_IMG:
                setCompoundDrawables(null, null, null,
                        drawable);
                break;
        }
    }

    /**
     * 设定图片的大小
     */
    private void setImageSize(Drawable d, int dir) {
        if (d == null) {
            return;
        }
        float heightScale = -1.0f;
        float widthScale = -1.0f;
        // 根据方向给宽和高赋值
        switch (dir) {
            case 0:
                // left
                heightScale = leftHeightScale;
                widthScale = leftWidthScale;
                break;
            case 1:
                // top
                heightScale = topHeightScale;
                widthScale = topWidthScale;
                break;
            case 2:
                // right
                heightScale = rightHeightScale;
                widthScale = rightWidthScale;
                break;
            case 3:
                // bottom
                heightScale = bottomHeightScale;
                widthScale = bottomWidthScale;
                break;
        }
        // 如果有某个方向的宽或者高没有设定值，则不去设定图片大小
//        if (widthScale != -1.0f && heightScale != -1.0f) {
//            if(referenceObject.equals("ScreenHeight")){
//                d.setBounds(0, 0, (int) (widthScale * ScreenUtil.getScreenHeight()), (int) (heightScale * ScreenUtil.getScreenHeight()));
//            }else{
//                d.setBounds(0, 0, (int) (widthScale * ScreenUtil.getScreenWidth()), (int) (heightScale * ScreenUtil.getScreenWidth()));
//            }
//        }
    }
}
