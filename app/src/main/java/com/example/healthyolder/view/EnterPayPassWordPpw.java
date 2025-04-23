package com.example.healthyolder.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.adapter.CommonBaseAdapter;
import com.example.healthyolder.adapter.ViewHolderBase;
import com.example.healthyolder.util.ToastUtil;
import com.zhy.android.percent.support.PercentFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2021/11/2.
 */
public class EnterPayPassWordPpw extends PopupWindow {
    private Context context;
    private GridView gv_keyboard;
    private PassWordView passWordView;
    private List<String> numberList;
    private CommonToolBar cb_title;
    private TextView tv_forgetPassword;
    private TickView tickView;
    private AlertDialog forgetPasswordDialog;
    private OnDialogClickListener onDialogClickListener;
    private PercentFrameLayout pfl_forgetPassword;
    private TextView tv_tip;
    private OnOperateCompletedListener onOperateCompletedListener;
    private String toastMessage;
    private InterceptPercentLinearLayout ipl;
    private View v_divider;
    private boolean isLoading;
    private Button btn_go_to_recharge;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                tv_tip.setText("订单支付成功");
                if(onOperateCompletedListener!=null){
                    onOperateCompletedListener.onOperateCompleted(true);
                }
            } else if (msg.what == 0x456) {
                //余额不足时是显示去充值的引导界面，需要判断
                if (toastMessage.equals("余额不足，请去充值")){
                    //取消拦截
                    ipl.setIntercept(false);
                    btn_go_to_recharge.setVisibility(View.VISIBLE);
                    tv_tip.setText("余额不足，请去充值");
                    cb_title.setLeftImgOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                    btn_go_to_recharge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            context.startActivity(new Intent(context, RechargeAcy.class));
                        }
                    });
                }else {
                    tv_tip.setText("订单支付失败");
                    ToastUtil.showBottomToast(toastMessage);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLoading = false;
                            ipl.setIntercept(false);
                            dismiss();
                        }
                    }, 2000);
                }

            }
            passWordView.clear();
        }
    };

    public EnterPayPassWordPpw(Context context) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.context = context;
        initEnterPayPassWordPpw();
        initEvent();
    }

    private void initEnterPayPassWordPpw() {
        View view = LayoutInflater.from(context).inflate(R.layout.ppw_enter_pay_password, null);
        gv_keyboard = (GridView) view.findViewById(R.id.gv_keyboard_enterPayPassWordPpw);
        passWordView = (PassWordView) view.findViewById(R.id.pw_enterPayPassWordPpw);
        pfl_forgetPassword = (PercentFrameLayout) view.findViewById(R.id.pfl_forgetPassword_enterPayPassWordPpw);
        tv_forgetPassword = (TextView) view.findViewById(R.id.tv_forgetPassword_enterPayPassWordPpw);
        cb_title = (CommonToolBar) view.findViewById(R.id.cb_title_enterPayPassWordPpw);
        tickView = (TickView) view.findViewById(R.id.tiv_enterPayPassWordPpw);
        tv_tip = (TextView) view.findViewById(R.id.tv_tip_enterPayPassWordPpw);
        ipl = (InterceptPercentLinearLayout) view.findViewById(R.id.ipl_enterPayPassWordPpw);
        v_divider = view.findViewById(R.id.v_divider_enterPayPassWordPpw);
        btn_go_to_recharge = (Button) view.findViewById(R.id.btn_go_to_recharge);
        btn_go_to_recharge.setVisibility(View.GONE);
        initGridView();
        setContentView(view);
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.transparentGrey));
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.SharePopupWindowAnimation);
    }

    private void initGridView() {
        numberList = new ArrayList<>();
        numberList.add("1");
        numberList.add("2");
        numberList.add("3");
        numberList.add("4");
        numberList.add("5");
        numberList.add("6");
        numberList.add("7");
        numberList.add("8");
        numberList.add("9");
        numberList.add("-1");
        numberList.add("0");
        numberList.add("-2");
        gv_keyboard.setAdapter(new CommonBaseAdapter<String>(context, R.layout.item_textview_gv_keyboard, numberList) {
            @Override
            public void convert(final ViewHolderBase holder, String s) {
                TextViewPlus tvp_number = holder.getView(R.id.tvp_number_itemTextViewKeyboardGv);
                PercentFrameLayout pfl = holder.getView(R.id.pfl_itemTextViewKeyboardGv);
                if (holder.getPosition() == 9) {
                    pfl.setBackgroundColor(context.getResources().getColor(R.color.middleGrey));
                } else if (holder.getPosition() == 11) {
                    pfl.setBackgroundColor(context.getResources().getColor(R.color.middleGrey));
                    tvp_number.setCompoundImg(TextViewPlus.LEFT_IMG, R.mipmap.delete_keyboard, 0.05f, 0.05f, "ScreenHeight");
                } else {
                    tvp_number.setText(s);
                }
                pfl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (holder.getPosition()){
                            case 9:
                                return;
                            case 11:
                                passWordView.delete();
                                break;
                            default:
                                passWordView.enter(numberList.get(holder.getPosition()));
                                break;
                        }
                    }
                });
            }
        });
    }

    private void initEvent() {
        cb_title.setLeftImgOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetPasswordDialog();
            }
        });

        tickView.setOnDrawCompleteListener(new TickView.OnDrawCompleteListener() {
            @Override
            public void onDrawComplete(boolean isSuccess) {
                if (isSuccess) {
                    handler.sendEmptyMessage(0x123);
                } else {
                    handler.sendEmptyMessage(0x456);
                }
            }
        });
    }



    private void showForgetPasswordDialog() {
        if (forgetPasswordDialog != null && !forgetPasswordDialog.isShowing()) {
            forgetPasswordDialog.show();
        } else if (forgetPasswordDialog != null && forgetPasswordDialog.isShowing()) {
            return;
        } else {
            forgetPasswordDialog = new AlertDialog.Builder(context).setTitle("提示").setMessage("您是否要前往修改密码？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (onDialogClickListener != null) {
                                onDialogClickListener.onDialogClick();
                            }
                            forgetPasswordDialog.dismiss();
                            EnterPayPassWordPpw.this.dismiss();
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            forgetPasswordDialog.dismiss();
                        }
                    }).create();
            forgetPasswordDialog.show();
        }
    }

    //开始对话框加载
    public void startLoading() {
        isLoading = true;
        ipl.setIntercept(true);
        passWordView.setVisibility(View.GONE);
        pfl_forgetPassword.setVisibility(View.GONE);
        v_divider.setVisibility(View.GONE);
        gv_keyboard.setVisibility(View.INVISIBLE);
        tickView.setVisibility(View.VISIBLE);
        tv_tip.setText("处理中...");
        tv_tip.setVisibility(View.VISIBLE);
        tickView.startAnimation();
    }

    public boolean isLoading() {
        return isLoading;
    }

    //完成加载
    public void completeLoading(boolean isSuccess) {
        toastMessage = null;
        tickView.setDrawCircleSwitch(false);
        if (isSuccess) {
            tickView.setStartDrawTickLeftLine(true);
        } else {
            tickView.setStartDrawErrorLeftLine(true);
        }
    }
    //恢复状态
    public void restore() {
        passWordView.setVisibility(View.VISIBLE);
        pfl_forgetPassword.setVisibility(View.VISIBLE);
        v_divider.setVisibility(View.VISIBLE);
        gv_keyboard.setVisibility(View.VISIBLE);
        tickView.setVisibility(View.GONE);
        tv_tip.setVisibility(View.GONE);
        btn_go_to_recharge.setVisibility(View.GONE);
    }
    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }
    public void setOnPassWordEnterCompletedListener(PassWordView.OnPassWordEnterCompletedListener onPassWordEnterCompletedListener) {
        passWordView.setOnPassWordEnterCompletedListener(onPassWordEnterCompletedListener);
    }

    public interface OnDialogClickListener {
        void onDialogClick();
    }

    public void setOnOperateCompletedListener(OnOperateCompletedListener onOperateCompletedListener) {
        this.onOperateCompletedListener = onOperateCompletedListener;
    }

    public interface OnOperateCompletedListener {
        void onOperateCompleted(boolean isSuccess);
    }
}
