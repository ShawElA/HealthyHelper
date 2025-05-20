package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.activity.CommonActivity;
import com.example.healthyolder.activity.HealthyTestActivity;
import com.example.healthyolder.activity.MainActivity;
import com.example.healthyolder.activity.MentalHealthRecordActivity;
import com.example.healthyolder.bean.RefreshEvent;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.LogUtil;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.SPUtil;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.SportStepView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    View view;
    @BindView(R.id.sportStepCount)
    SportStepView sportStepCount;
    @BindView(R.id.sportStepCountInfo)
    TextView sportStepCountInfo;
    private int currentScore = 0; // 存储当前分数

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.tv_test)
    public void test(){
        IntentUtil.startActivity(getActivity(), HealthyTestActivity.class);
    }

    @OnClick(R.id.tv_test1)
    public void test1(){
        IntentUtil.startActivity(getActivity(), CommonActivity.class);
    }

    @OnClick(R.id.tv_test2)
    public void test2(){
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).switchToEmpowerPage();
        } else {
            ToastUtil.showBottomToast("即将前往赋能减压界面");
            Bundle args = new Bundle();
            args.putInt("tabIndex", 3); // 赋能减压在底部导航中的索引
            IntentUtil.startActivity(getActivity(), MainActivity.class, args);
        }
    }

    @OnClick(R.id.tv_health_record)
    public void openHealthRecord() {
        IntentUtil.startActivity(getActivity(), MentalHealthRecordActivity.class);
    }

    @OnClick(R.id.sportStepCountInfo)
    public void onScoreInfoClick() {
        // 根据分数区间跳转到不同页面
        if (currentScore < 60) {
            // 疑重度抑郁，跳转到预约挂号界面
            // 通过MainActivity的底部导航切换到预约挂号页面（索引为2）
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToAppointmentPage();
            } else {
                ToastUtil.showBottomToast("即将前往预约挂号界面");
                // 如果不在MainActivity中，可以直接启动MainActivity并传入参数指定跳转到预约挂号页
                Bundle args = new Bundle();
                args.putInt("tabIndex", 2); // 预约挂号在底部导航中的索引
                IntentUtil.startActivity(getActivity(), MainActivity.class, args);
            }
        } else if (currentScore < 70) {
            // 疑中度抑郁，跳转到智能医生聊天（咨询）界面
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToConsultPage();
            } else {
                ToastUtil.showBottomToast("即将前往智能医生聊天界面");
                Bundle args = new Bundle();
                args.putInt("tabIndex", 1); // 智能医生聊天在底部导航中的索引
                IntentUtil.startActivity(getActivity(), MainActivity.class, args);
            }
        } else {
            // 疑轻度抑郁或"无或最低限度抑郁症状"，跳转到赋能减压界面
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToEmpowerPage();
            } else {
                ToastUtil.showBottomToast("即将前往赋能减压界面");
                Bundle args = new Bundle();
                args.putInt("tabIndex", 3); // 赋能减压在底部导航中的索引
                IntentUtil.startActivity(getActivity(), MainActivity.class, args);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
        };
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initData();
        return view;
    }


    private void initData() {
        // 尝试从多个来源获取分数，确保数据一致性
        try {
            // 首先尝试从BaseApplication获取分数
            if (BaseApplication.getDepressionScore() != null && !BaseApplication.getDepressionScore().isEmpty() 
                    && !"0".equals(BaseApplication.getDepressionScore())) {
                currentScore = Integer.parseInt(BaseApplication.getDepressionScore());
                LogUtil.i("HomeFragment", "使用BaseApplication分数: " + currentScore);
            }
            // 然后尝试从PreferenceUtil获取
            else if (TextUtil.isValidate(PreferenceUtil.getString("goal"))) {
                currentScore = Integer.parseInt(PreferenceUtil.getString("goal"));
                LogUtil.i("HomeFragment", "使用PreferenceUtil分数: " + currentScore);
                // 同步到BaseApplication
                BaseApplication.setDepressionScore(String.valueOf(currentScore));
            }
            // 最后尝试从SPUtil获取
            else if (getContext() != null) {
                String score = SPUtil.getString(getContext(), "depression_score", "0");
                if (TextUtil.isValidate(score) && !"0".equals(score)) {
                    currentScore = Integer.parseInt(score);
                    LogUtil.i("HomeFragment", "使用SPUtil分数: " + currentScore);
                    // 同步数据
                    BaseApplication.setDepressionScore(score);
                    PreferenceUtil.putString("goal", score);
                }
            }
            
            // 更新UI
            if (currentScore > 0) {
                sportStepCount.setCurrentCount(100, currentScore);
                changeHint(currentScore);
            }
        } catch (Exception e) {
            LogUtil.e("HomeFragment", "初始化分数数据出错: " + e.getMessage());
        }
    }

    private void changeHint(int g){
        currentScore = g;
        if (g < 60){
            sportStepCountInfo.setText("疑重度抑郁，请遵从医嘱进行治疗");
        }else if (g < 70){
            sportStepCountInfo.setText("疑中度抑郁，可尝试从心理治疗再到药物治疗进行恢复");
        }else if (g < 80){
            sportStepCountInfo.setText("疑轻度抑郁，建议可从生活方式进行调整，或者寻求社交支持，与朋友、家人保持良好沟通");
        }else {
            sportStepCountInfo.setText("无或最低限度抑郁症状。保持健康生活习惯，如规律作息、均衡饮食、适度运动");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event){
        initData();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次界面重新显示时刷新数据
        initData();
    }
    
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
