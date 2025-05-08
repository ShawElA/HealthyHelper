package com.example.healthyolder.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.AddToShoppingCartMessageEvent;
import com.example.healthyolder.bean.PayResult;
import com.example.healthyolder.bean.RefreshAddressEvent;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.EnterPayPassWordPpw;
import com.example.healthyolder.view.PassWordView;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;



public class PayActivity extends BaseActivity {

    @BindView(R.id.iv_wechat_select)
    ImageView iv_wechat;
    @BindView(R.id.iv_ali_select)
    ImageView iv_ali;
    @BindView(R.id.tv_time_count)
    TextView tv_count;
    @BindView(R.id.btn_pay)
    Button btn_pay;
    @BindView(R.id.pll_parent)
    PercentLinearLayout pll_parent;
    private CountDownTimer timer;
    private int m, s;
    EnterPayPassWordPpw enterPayPassWordPpw;
    private Bundle bundle;
    private String order_id, sc_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
//        EventBus.getDefault().register(this);
        bundle = getIntent().getBundleExtra("Bundle");
        order_id = bundle.getString("o_id");
//        sc_id = bundle.getString("sc_id");
        btn_pay.setText("确认支付￥20");
        initCountTimer();
    }

    @OnClick(R.id.btn_pay)
    public void sure(){
        showEnterPayPassWordPpw();
    }

    //输入支付密码进行支付
    private void showEnterPayPassWordPpw() {
        if (enterPayPassWordPpw != null && !enterPayPassWordPpw.isShowing()) {
            enterPayPassWordPpw.restore();
            enterPayPassWordPpw.showAtLocation(pll_parent, 0, 0, Gravity.BOTTOM);
        } else if (enterPayPassWordPpw != null && enterPayPassWordPpw.isShowing()) {
            return;
        } else {
            enterPayPassWordPpw = new EnterPayPassWordPpw(this);
            enterPayPassWordPpw.setOnPassWordEnterCompletedListener(new PassWordView.OnPassWordEnterCompletedListener() {
                @Override
                public void onPassWordEnterCompleted(String passWord) {
                    enterPayPassWordPpw.startLoading();
                    if (passWord.equals("123456")){
                        payOrder();
//                        finish();
                    }else {
                        enterPayPassWordPpw.completeLoading(false);
                        enterPayPassWordPpw.setToastMessage("支付失败");
                    }

                }
            });

            enterPayPassWordPpw.setOnDialogClickListener(new EnterPayPassWordPpw.OnDialogClickListener() {
                @Override
                public void onDialogClick() {
                    enterPayPassWordPpw.dismiss();
//                    EventBus.getDefault().post(new GoToSetPayPassWordMessageEvent());
                    finish();
                }
            });
            enterPayPassWordPpw.setOnOperateCompletedListener(new EnterPayPassWordPpw.OnOperateCompletedListener() {
                @Override
                public void onOperateCompleted(boolean isSuccess) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            EventBus.getDefault().post(new OrderPaySuccessfullyMessageEvent());
                            finish();
                        }
                    }, 1500);
                }
            });
            enterPayPassWordPpw.showAtLocation(pll_parent, 0, 0, Gravity.BOTTOM);
        }
    }

    private void initCountTimer(){

        timer = new CountDownTimer(900*1000, 1000) {
            @Override
            public void onTick(long l) {
                m = (int)l/(1000*60);
                s = ((int)l/1000)%60;
                tv_count.setText(m + ":" + s);
            }

            @Override
            public void onFinish() {
                tv_count.setText("00:00");
            }
        };
        timer.start();
    }

    @OnClick(R.id.prl_wechat_item)
    public void wechatItem(){
        iv_wechat.setVisibility(View.VISIBLE);
        iv_ali.setVisibility(View.GONE);
        btn_pay.setSelected(true);
        btn_pay.setEnabled(true);
    }

    @OnClick(R.id.prl_ali_item)
    public void aliItem(){
        iv_wechat.setVisibility(View.GONE);
        iv_ali.setVisibility(View.VISIBLE);
        btn_pay.setSelected(true);
        btn_pay.setEnabled(true);
    }

    private void payOrder(){
        operateDelay(true, "支付成功");
        EventBus.getDefault().post(new RefreshAddressEvent());

    }

    //延迟操作
    private void operateDelay(final boolean isSuccess, final String toastMessage) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterPayPassWordPpw.completeLoading(isSuccess);
                enterPayPassWordPpw.setToastMessage(toastMessage);
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

}
