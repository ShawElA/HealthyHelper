package com.example.healthyolder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.healthyolder.R;
import com.example.healthyolder.util.LogUtil;

import java.util.Random;

/**
 * Created by Administrator on 2021/10/15.
 */
public class TickView extends View {
    private Paint paint;
    private RectF oval;
    private boolean isFirstDraw = true;
    private float paddingScale = 0.2f;
    private float padding;
    private float viewWidth;
    private float viewHeight;
    private float startAngle;
    private float sweepAngle;
    private float tempSweepAngle;
    private float diameter;
    private float radius;
    //勾线左边部分
    private float tickLeftLineStartX;
    private float tickLeftLineStartY;
    private float tickLeftLineTempX;
    private float tickLeftLineTempY;
    private float tickLeftLineEndX;
    private float tickLeftLineEndY;
    //勾线右边部分
    private float tickRightLineTempX;
    private float tickRightLineTempY;
    private float tickRightLineEndX;
    private float tickRightLineEndY;
    //叉线左边部分
    private float errorLeftLineStartX;
    private float errorLeftLineStartY;
    private float errorLeftLineTempX;
    private float errorLeftLineTempY;
    private float errorLeftLineEndX;
    private float errorLeftLineEndY;
    //叉线右边部分
    private float errorRightLineStartX;
    private float errorRightLineStartY;
    private float errorRightLineTempX;
    private float errorRightLineTempY;
    private float errorRightLineEndX;
    private float errorRightLineEndY;
    private float moveHalfXLength;
    private float moveHalfYLength;
    private boolean isFirstAttachCenter;
    //以下布尔值主要用于控制是否开启绘画
    private boolean isStartDrawTickLeftLine;
    private boolean isStartDrawTickRightLine;
    private boolean isStartDrawErrorLeftLine;
    private boolean isStartDrawErrorRightLine;
    private boolean isSweeping = true;
    private boolean drawCircleSwitch;
    private boolean drawTickLeftLineSwitch;
    private boolean drawTickRightLineSwitch;
    private boolean drawErrorLeftLineSwitch;
    private boolean drawErrorRightLineSwitch;
    private OnDrawCompleteListener onDrawCompleteListener;


    public TickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    private void initData() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE); // 设置空心
        paint.setColor(getResources().getColor(R.color.middleBlue));
        startAnimation();
    }

    public void startAnimation() {
        drawCircleSwitch = true;
        drawTickLeftLineSwitch = true;
        drawTickRightLineSwitch = true;
        drawErrorLeftLineSwitch = true;
        drawErrorRightLineSwitch = true;
        isStartDrawTickLeftLine = false;
        isStartDrawTickRightLine = false;
        isStartDrawErrorLeftLine = false;
        isStartDrawErrorRightLine = false;
        drawCircle();
    }
    private void drawCircle() {
        startAngle = produceRandomStartAngle();
        sweepAngle = produceRandomSweepAngle();
        tempSweepAngle = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!drawCircleSwitch) {
                        startAngle = 0;
                        tempSweepAngle = 360;
                        if (isStartDrawTickLeftLine) {
                            drawTickLeftLine();
                        } else if (isStartDrawErrorLeftLine) {
                            drawErrorLeftLine();
                        }
                        break;
                    }
                    if (isSweeping) {
                        tempSweepAngle++;
                    } else {
                        tempSweepAngle -= 0.2;
                        if (tempSweepAngle <= 0) {
                            tempSweepAngle = 0;
                            isSweeping = true;
                            sweepAngle = produceRandomSweepAngle();
                        }
                    }
                    startAngle++;
                    if (startAngle >= 360) {
                        startAngle -= 360;
                    }
                    if (tempSweepAngle >= sweepAngle) {
                        isSweeping = false;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void drawTickLeftLine() {
        tickLeftLineStartX = padding + radius * 0.6f;
        tickLeftLineStartY = padding + radius;
        tickLeftLineEndY = padding + radius * 1.3f;
        tickLeftLineTempX = tickLeftLineStartX;
        tickLeftLineTempY = tickLeftLineStartY;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!drawTickLeftLineSwitch) {
                        break;
                    }
                    tickLeftLineTempX += 0.5;
                    tickLeftLineTempY += 0.5;

                    if (tickLeftLineTempY >= tickLeftLineEndY) {
                        tickLeftLineEndX = tickLeftLineTempX;
                        tickLeftLineTempX += 1.5;
                        tickLeftLineTempY += 1.5;
                        isStartDrawTickRightLine = true;
                        drawTickRightLine();
                        break;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void drawTickRightLine() {
        tickRightLineTempX = tickLeftLineEndX;
        tickRightLineTempY = tickLeftLineEndY;
        tickRightLineEndY = padding + radius * 0.7f;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!drawTickRightLineSwitch) {
                        break;
                    }
                    tickRightLineTempX += 1;
                    tickRightLineTempY -= 1;
//                    LogUtil.i("test", "drawTickRightLine");
//                    LogUtil.i("test", "tickRightLineTempX:" + tickRightLineTempX);
//                    LogUtil.i("test", "tickRightLineEndX:" + tickRightLineEndX);
//                    LogUtil.i("test", "tickRightLineTempY:" + tickRightLineTempY);
//                    LogUtil.i("test", "tickRightLineEndY:" + tickRightLineEndY);
                    if (tickRightLineTempY <= tickRightLineEndY) {
                        tickRightLineEndX = tickRightLineTempX;
                        if (onDrawCompleteListener != null) {
                            onDrawCompleteListener.onDrawComplete(true);
                        }
                        break;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void drawErrorLeftLine() {
        isFirstAttachCenter = true;
        moveHalfXLength = 0;
        moveHalfYLength = 0;
        errorLeftLineStartX = padding + radius * 0.6f;
        errorLeftLineStartY = padding + radius * 0.6f;
        errorLeftLineTempX = errorLeftLineStartX;
        errorLeftLineTempY = errorLeftLineStartY;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!drawErrorLeftLineSwitch) {
                        break;
                    }
                    errorLeftLineTempX += 1;
                    errorLeftLineTempY += 1;
                    if (isFirstAttachCenter) {
                        LogUtil.i("test", "errorLeftLineTempX1:" + errorLeftLineTempX);
                        LogUtil.i("test", "padding + radius:" + (padding + radius));
                        if (errorLeftLineTempX >= (padding + radius)) {
                            moveHalfXLength = errorLeftLineTempX - errorLeftLineStartX;
                            moveHalfYLength = errorLeftLineTempY - errorLeftLineStartY;
                            isFirstAttachCenter = false;
                        }
                    } else {
                        LogUtil.i("test", "errorLeftLineTempX2:" + errorLeftLineTempX);
                        LogUtil.i("test", "moveHalfXLength:" + moveHalfXLength * 2f);
                        if (errorLeftLineTempX >= (errorLeftLineStartX + moveHalfXLength * 2f)) {
                            isStartDrawErrorRightLine = true;
                            drawErrorRightLine();
                            break;
                        }
                    }


                    postInvalidate();
                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void drawErrorRightLine() {
        errorRightLineStartX = errorLeftLineTempX;
        errorRightLineStartY = errorLeftLineTempY - moveHalfYLength * 2;
        errorRightLineTempX = errorRightLineStartX;
        errorRightLineTempY = errorRightLineStartY;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!drawErrorRightLineSwitch) {
                        break;
                    }
                    errorRightLineTempX -= 0.5;
                    errorRightLineTempY += 0.5;
                    if (errorRightLineTempX <= errorLeftLineStartX) {
                        if (onDrawCompleteListener != null) {
                            onDrawCompleteListener.onDrawComplete(false);
                        }
                        break;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDraw) {
            viewWidth = getWidth();
            viewHeight = getHeight();
            padding = viewWidth * paddingScale;
            if (viewWidth >= viewHeight) {
                diameter = (viewHeight - padding * 2);
            } else {
                diameter = (viewWidth - padding * 2);
            }
            radius = diameter / 2;
            oval = new RectF(padding, padding, padding + diameter, padding + diameter);
            paint.setStrokeWidth(viewWidth*0.025f);
            isFirstDraw = false;
        }
        //绘制圆
        canvas.drawArc(oval, startAngle, tempSweepAngle, false, paint);
        //绘制勾线左边部分
        if (isStartDrawTickLeftLine) {
            canvas.drawLine(tickLeftLineStartX, tickLeftLineStartY, tickLeftLineTempX, tickLeftLineTempY, paint);
        }
        //绘制勾线右边部分
        if (isStartDrawTickRightLine) {
            canvas.drawLine(tickLeftLineEndX, tickLeftLineEndY, tickRightLineTempX, tickRightLineTempY, paint);
        }
       //绘制叉线左边部分
        if (isStartDrawErrorLeftLine) {
            canvas.drawLine(errorLeftLineStartX, errorLeftLineStartY, errorLeftLineTempX, errorLeftLineTempY, paint);
        }
        //绘制叉线右边部分
        if (isStartDrawErrorRightLine) {
            canvas.drawLine(errorRightLineStartX, errorRightLineStartY, errorRightLineTempX, errorRightLineTempY, paint);
        }
    }

    public void setOnDrawCompleteListener(OnDrawCompleteListener onDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener;
    }

    private float produceRandomStartAngle() {
        Random random = new Random();
        return (random.nextInt(720 + 1) - 360);
    }

    private float produceRandomSweepAngle() {
        Random random = new Random();
        return 90 + random.nextInt(180 + 1);
    }

    public void setDrawCircleSwitch(boolean drawCircleSwitch) {
        this.drawCircleSwitch = drawCircleSwitch;
    }

    public void setStartDrawTickLeftLine(boolean startDrawTickLeftLine) {
        isStartDrawTickLeftLine = startDrawTickLeftLine;
    }

    public void setStartDrawErrorLeftLine(boolean startDrawErrorLeftLine) {
        isStartDrawErrorLeftLine = startDrawErrorLeftLine;
    }

    public interface OnDrawCompleteListener {
        void onDrawComplete(boolean isSuccess);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        drawCircleSwitch = false;
        drawTickLeftLineSwitch = false;
        drawTickRightLineSwitch = false;
        drawErrorLeftLineSwitch = false;
        drawErrorRightLineSwitch = false;
    }
}
