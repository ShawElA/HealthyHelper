package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.activity.CommonActivity;
import com.example.healthyolder.activity.HealthyTestActivity;
import com.example.healthyolder.activity.MainActivity;
import com.example.healthyolder.bean.RefreshEvent;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.PreferenceUtil;
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
            // 疑中度抑郁，跳转到心理治疗相关页面
            IntentUtil.startActivity(getActivity(), CommonActivity.class);
        } else if (currentScore < 80) {
            // 疑轻度抑郁，跳转到生活方式调整页面
            // 这里可以替换为实际的生活方式建议页面
            ToastUtil.showBottomToast("跳转到生活方式调整页面");
        } else {
            // 无或最低限度抑郁症状，可以跳转到健康生活习惯页面
            ToastUtil.showBottomToast("跳转到健康生活习惯页面");
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


    private void initData(){
        if (TextUtil.isValidate(PreferenceUtil.getString("goal"))){
            currentScore = Integer.valueOf(PreferenceUtil.getString("goal"));
            sportStepCount.setCurrentCount(100, currentScore);
            changeHint(currentScore);
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
}
