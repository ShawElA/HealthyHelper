package com.example.healthyolder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.healthyolder.R;
import com.example.healthyolder.util.DensityUtil;


public class PassWordView extends View {
    private Paint paint; //画笔
    private boolean isFirstDraw = true;
    private float viewHeight;
    private float viewWidth;
    private float horizontalSpace;
    private float paintStrokeWidth;
    private StringBuffer passWord;
    private int pointNumber = 0;
    private float pointRadius;
    private OnPassWordEnterCompletedListener onPassWordEnterCompletedListener;
    public PassWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()){
            initData();
        }
    }

    private void initData() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(paintStrokeWidth);
        paintStrokeWidth = DensityUtil.dip2px(0.5f);
        passWord = new StringBuffer();
    }

    private void drawVerticalLine(Canvas canvas) {
        paint.setColor(getResources().getColor(R.color.grey));
        for (int i = 0; i < 6; i++) {
            canvas.drawLine(horizontalSpace * (i + 1) + i * paintStrokeWidth, 0, horizontalSpace * (i + 1) + i * paintStrokeWidth, viewHeight, paint);
        }
    }

    private void drawPoint(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < pointNumber; i++) {
            canvas.drawCircle(i * (paintStrokeWidth + horizontalSpace) + horizontalSpace / 2, viewHeight / 2, pointRadius, paint);
        }
    }

    //输入
    public void enter(String number) {
        if(pointNumber==6){
            return;
        }
        passWord.append(number);
        pointNumber++;
        invalidate();
        if(pointNumber==6){
            if(onPassWordEnterCompletedListener!=null){
                onPassWordEnterCompletedListener.onPassWordEnterCompleted(passWord.toString());
            }
        }
    }

    //回退
    public void delete() {
        if(pointNumber==0){
            return;
        }
        passWord.deleteCharAt(passWord.length() - 1);
        pointNumber--;
        invalidate();
    }
    //清除数据
    public void clear() {
        pointNumber = 0;
        passWord.delete(0, passWord.length());
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDraw) {
            viewWidth = getWidth();
            viewHeight = getHeight();
            horizontalSpace = (viewWidth - paintStrokeWidth * 5) / 6;
            pointRadius = horizontalSpace * 0.12f;
            isFirstDraw = false;
        }
        drawVerticalLine(canvas);
        drawPoint(canvas);
    }

    public void setOnPassWordEnterCompletedListener(OnPassWordEnterCompletedListener onPassWordEnterCompletedListener) {
        this.onPassWordEnterCompletedListener = onPassWordEnterCompletedListener;
    }

    public interface OnPassWordEnterCompletedListener{
        void onPassWordEnterCompleted(String passWord);
    }
}
