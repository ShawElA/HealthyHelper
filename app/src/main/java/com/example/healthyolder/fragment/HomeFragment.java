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
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
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

import org.json.JSONObject;

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
        try {
            // 1. 获取当前登录用户ID
            String userId = getCurrentUserId();
            if (userId == null || userId.isEmpty()) {
                LogUtil.e("HomeFragment", "无法获取有效的用户ID，无法显示用户特定分数");
                return;
            }
            
            LogUtil.i("HomeFragment", "开始获取用户ID " + userId + " 的分数");
            
            // 2. 仅使用用户特定存储获取分数
            int score = SPUtil.getUserScore(getContext(), userId);
            
            // 3. 如果找到用户特定分数，则使用它
            if (score > 0) {
                currentScore = score;
                LogUtil.i("HomeFragment", "成功获取用户ID " + userId + " 的特定分数: " + currentScore);
                
                // 4. 更新UI
                if (sportStepCount != null) {
                    sportStepCount.setCurrentCount(100, currentScore);
                    changeHint(currentScore);
                } else {
                    LogUtil.e("HomeFragment", "sportStepCount为空，无法更新UI");
                }
            } else {
                // 5. 如果没有找到用户特定分数，尝试从服务器获取
                requestLatestScoreFromServer(userId);
            }
        } catch (Exception e) {
            LogUtil.e("HomeFragment", "初始化分数数据出错: " + e.getMessage());
        }
    }
    
    /**
     * 从服务器获取当前用户的最新分数
     */
    private void requestLatestScoreFromServer(String userId) {
        if (getContext() == null) return;
        
        try {
            String url = Urls.GET_LATEST_SCORE + userId;
            LogUtil.i("HomeFragment", "尝试从服务器获取用户 " + userId + " 的最新分数: " + url);
            
            HttpUtil.get(url, null, new HttpUtil.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        LogUtil.i("HomeFragment", "服务器返回: " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            int score = jsonObject.optInt("data", 0);
                            if (score > 0) {
                                // 保存到用户特定存储
                                SPUtil.saveUserScore(getContext(), userId, score);
                                
                                // 更新UI
                                currentScore = score;
                                LogUtil.i("HomeFragment", "从服务器获取到用户 " + userId + " 的最新分数: " + score);
                                
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        if (sportStepCount != null) {
                                            sportStepCount.setCurrentCount(100, currentScore);
                                            changeHint(currentScore);
                                        }
                                    });
                                }
                            } else {
                                LogUtil.w("HomeFragment", "服务器返回无效分数: " + score);
                            }
                        } else {
                            LogUtil.w("HomeFragment", "服务器返回失败: " + jsonObject.optString("result", "未知错误"));
                        }
                    } catch (Exception e) {
                        LogUtil.e("HomeFragment", "解析服务器响应失败: " + e.getMessage());
                    }
                }
                
                @Override
                public void onError(Exception e) {
                    LogUtil.e("HomeFragment", "获取最新分数失败: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            LogUtil.e("HomeFragment", "请求服务器最新分数异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录的用户ID
     */
    private String getCurrentUserId() {
        if (getContext() == null) {
            LogUtil.e("HomeFragment", "getContext() 为空，无法获取用户ID");
            return null;
        }
        
        // 1. 尝试从SPUtil获取
        String userId = SPUtil.getString(getContext(), SPUtil.USER_ID, "");
        LogUtil.i("HomeFragment", "从SPUtil获取的用户ID: " + userId);
        if (isValidUserId(userId)) {
            LogUtil.i("HomeFragment", "使用SPUtil中的有效用户ID: " + userId);
            return userId;
        }
        
        // 2. 尝试从BaseApplication获取
        userId = BaseApplication.getUserId();
        LogUtil.i("HomeFragment", "从BaseApplication获取的用户ID: " + userId);
        if (isValidUserId(userId)) {
            // 同步回SPUtil
            SPUtil.putString(getContext(), SPUtil.USER_ID, userId);
            LogUtil.i("HomeFragment", "使用BaseApplication中的有效用户ID并同步到SPUtil: " + userId);
            return userId;
        }
        
        // 3. 尝试从PreferenceUtil获取
        userId = PreferenceUtil.getString("userId", "");
        LogUtil.i("HomeFragment", "从PreferenceUtil获取的用户ID: " + userId);
        if (isValidUserId(userId)) {
            // 同步到其他存储
            SPUtil.putString(getContext(), SPUtil.USER_ID, userId);
            BaseApplication.setUserId(userId);
            LogUtil.i("HomeFragment", "使用PreferenceUtil中的有效用户ID并同步到其他存储: " + userId);
            return userId;
        }
        
        LogUtil.e("HomeFragment", "所有存储方式都未找到有效的用户ID");
        return null;
    }
    
    /**
     * 检查用户ID是否有效
     */
    private boolean isValidUserId(String userId) {
        if (userId == null) {
            LogUtil.d("HomeFragment", "用户ID为null");
            return false;
        }
        if (userId.isEmpty()) {
            LogUtil.d("HomeFragment", "用户ID为空字符串");
            return false;
        }
        
        try {
            int id = Integer.parseInt(userId);
            boolean isValid = id > 0;
            LogUtil.d("HomeFragment", "用户ID " + userId + " 解析为数字: " + id + ", 是否有效: " + isValid);
            return isValid;
        } catch (NumberFormatException e) {
            LogUtil.e("HomeFragment", "用户ID " + userId + " 不是有效的数字格式");
            return false;
        }
    }

    private void changeHint(int g){
        currentScore = g;
        if (g < 60){
            sportStepCountInfo.setText("疑重度抑郁，请遵从医嘱进行治疗\n点击此处前往预约挂号");
        }else if (g < 70){
            sportStepCountInfo.setText("疑中度抑郁，可尝试与智能医生进行对话，获得初步建议\n点击此处咨询智能医生");
        }else if (g < 80){
            sportStepCountInfo.setText("疑轻度抑郁，建议可从生活方式进行调整，或者寻求社交支持\n点击此处前往赋能减压");
        }else {
            sportStepCountInfo.setText("无或最低限度抑郁症状。保持健康生活习惯即可，如规律作息、均衡饮食、适度运动\n点击此处前往赋能减压");
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
