package com.example.healthyolder.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.BaseAdapter;
import com.example.healthyolder.bean.DepressionTestHistory;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.LogUtil;
import com.example.healthyolder.util.PreferenceUtil;
import com.example.healthyolder.util.SPUtil;
import com.example.healthyolder.util.ToastUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MentalHealthRecordActivity extends BaseActivity {
    private LineChart lineChart;
    private RecyclerView recyclerView;
    private BaseAdapter<DepressionTestHistory> adapter;
    private List<DepressionTestHistory> historyList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private TextView noDataTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mental_health_record);
        
        initView();
        
        // 在加载数据前，先触发表初始化并修复数据问题
        initializeServerTablesAndFixData();
    }

    private void initView() {
        // 初始化标题栏
        TextView titleTv = findViewById(R.id.titleTv);
        titleTv.setText("心理健康档案");
        findViewById(R.id.backIv).setOnClickListener(v -> finish());

        // 初始化无数据提示
        noDataTv = findViewById(R.id.noDataTv);
        if (noDataTv == null) {
            // 如果布局中没有此控件，动态添加
            noDataTv = new TextView(this);
            noDataTv.setText("暂无测试记录，请先进行抑郁自测");
            noDataTv.setTextSize(16);
            noDataTv.setTextColor(Color.GRAY);
            noDataTv.setVisibility(View.GONE);
        }

        // 初始化图表
        lineChart = findViewById(R.id.lineChart);
        setupChart();

        // 初始化列表
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseAdapter<DepressionTestHistory>(R.layout.item_depression_history, historyList) {
            @Override
            protected void convert(ViewHolder holder, DepressionTestHistory item) {
                // 安全处理日期, 避免空指针异常
                String dateText = "未知日期";
                if (item != null && item.getHDate() != null) {
                    try {
                        dateText = dateFormat.format(item.getHDate());
                    } catch (Exception e) {
                        LogUtil.e("MentalHealthRecord", "格式化日期出错: " + e.getMessage());
                    }
                }
                holder.setText(R.id.dateTv, dateText);
                
                // 安全处理得分
                String scoreText = "得分：未知";
                if (item != null && item.getHScore() != null) {
                    scoreText = "得分：" + item.getHScore();
                }
                holder.setText(R.id.scoreTv, scoreText);
                
                // 安全处理评估
                String levelText = "评估：未知";
                if (item != null && item.getHLevel() != null && !item.getHLevel().isEmpty()) {
                    levelText = "评估：" + item.getHLevel();
                }
                holder.setText(R.id.levelTv, levelText);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f); // 抑郁分数满分100

        lineChart.getAxisRight().setEnabled(false);
    }

    /**
     * 触发服务器表初始化并修复数据问题
     */
    private void initializeServerTablesAndFixData() {
        // 显示初始化进度对话框
        showProgressDialog("正在初始化并加载测试记录...");
                
        // 获取当前用户ID
        String userId = getCurrentUserId();
        if (userId == null) {
            dismissProgressDialog();
            ToastUtil.showBottomToast("请先登录后再查看");
            finish();
            return;
        }

        // 添加一个超时处理机制，延长到15秒以给服务器足够响应时间
        new Thread(() -> {
            try {
                // 等待15秒
                Thread.sleep(15000);
                // 如果15秒后进度对话框仍在显示，则说明可能请求超时
                runOnUiThread(() -> {
                    if (isProgressDialogShowing()) {
                        LogUtil.e("MentalHealthRecord", "服务器请求超时，尝试直接获取历史记录");
                        dismissProgressDialog();
                        
                        // 直接尝试获取历史记录
                        loadData();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        // 确保使用正确的API路径
        String initUrl = Urls.INIT_DEPRESSION_TABLES;
        LogUtil.i("MentalHealthRecord", "调用API初始化: " + initUrl);
        
        // 先触发表初始化
        HttpUtil.post(initUrl, null, new HttpUtil.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtil.i("表初始化", "初始化结果: " + response);
                
                // 初始化成功后直接加载历史数据
                loadData();
            }
            
            @Override
            public void onError(Exception e) {
                LogUtil.e("表初始化", "初始化失败: " + e.getMessage());
                // 初始化失败也尝试加载历史数据
                loadData();
            }
        });
    }

    private void loadData() {
        try {
            // 首先获取并显示用户ID
            String userId = getCurrentUserId();
            
            if (userId == null) {
                dismissProgressDialog();
                ToastUtil.showBottomToast("请先登录后再查看");
                LogUtil.e("MentalHealthRecord", "未找到有效用户ID，需要登录");
                finish();
                return;
            }
            
            ToastUtil.showBottomToast("正在加载用户ID: " + userId + " 的测试记录");
            LogUtil.i("MentalHealthRecord", "加载用户ID: " + userId + " 的测试历史");
            
            // 确保使用正确的API路径
            String historyUrl = Urls.GET_DEPRESSION_HISTORY + userId;
            LogUtil.i("MentalHealthRecord", "调用API获取历史: " + historyUrl);
            
            // 直接使用GET请求获取历史数据
            HttpUtil.get(historyUrl, null, new HttpUtil.HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        // 打印完整响应用于调试
                        LogUtil.i("MentalHealthRecord", "历史数据响应: " + response);
                        
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            LogUtil.i("MentalHealthRecord", "解析到的数据数组长度: " + data.length());
                            
                            // 手动解析JSON数组
                            historyList = new ArrayList<>();
                            
                            // 解析每条记录
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject item = data.getJSONObject(i);
                                    LogUtil.i("MentalHealthRecord", "记录[" + i + "]: " + item.toString());
                                    
                                    DepressionTestHistory history = new DepressionTestHistory();
                                    
                                    // 尝试各种可能的字段名 - 添加全小写字段名支持
                                    // ID字段
                                    int id = 0;
                                    if (item.has("hId")) id = item.optInt("hId", 0);
                                    else if (item.has("h_id")) id = item.optInt("h_id", 0);
                                    else if (item.has("id")) id = item.optInt("id", 0);
                                    else if (item.has("hid")) id = item.optInt("hid", 0);
                                    history.setHId(id);
                                    
                                    // 用户ID字段
                                    int uid = 0;
                                    if (item.has("hUid")) uid = item.optInt("hUid", 0);
                                    else if (item.has("h_uid")) uid = item.optInt("h_uid", 0);
                                    else if (item.has("uid")) uid = item.optInt("uid", 0);
                                    else if (item.has("userId")) uid = item.optInt("userId", 0);
                                    else if (item.has("user_id")) uid = item.optInt("user_id", 0);
                                    else if (item.has("huid")) uid = item.optInt("huid", 0);
                                    history.setHUid(uid);
                                    
                                    // 得分字段
                                    int score = 0;
                                    if (item.has("hScore")) score = item.optInt("hScore", 0);
                                    else if (item.has("h_score")) score = item.optInt("h_score", 0);
                                    else if (item.has("score")) score = item.optInt("score", 0);
                                    else if (item.has("hscore")) score = item.optInt("hscore", 0);
                                    history.setHScore(score);
                                    
                                    // 评估级别字段
                                    String level = "未知";
                                    if (item.has("hLevel")) level = item.optString("hLevel", "未知");
                                    else if (item.has("h_level")) level = item.optString("h_level", "未知");
                                    else if (item.has("level")) level = item.optString("level", "未知");
                                    else if (item.has("hlevel")) level = item.optString("hlevel", "未知");
                                    history.setHLevel(level);
                                    
                                    // 答案字段
                                    String answers = "";
                                    if (item.has("hAnswers")) answers = item.optString("hAnswers", "");
                                    else if (item.has("h_answers")) answers = item.optString("h_answers", "");
                                    else if (item.has("answers")) answers = item.optString("answers", "");
                                    else if (item.has("hanswers")) answers = item.optString("hanswers", "");
                                    history.setHAnswers(answers);
                                    
                                    // 处理日期
                                    String dateStr = null;
                                    if (item.has("hDate")) dateStr = item.optString("hDate", null);
                                    else if (item.has("h_date")) dateStr = item.optString("h_date", null);
                                    else if (item.has("date")) dateStr = item.optString("date", null);
                                    else if (item.has("hdate")) dateStr = item.optString("hdate", null);
                                    
                                    // 尝试多种日期格式
                                    Date recordDate = null;
                                    if (dateStr != null && !dateStr.isEmpty()) {
                                        try {
                                            // 尝试ISO 8601格式 (带T的格式)
                                            if (dateStr.contains("T")) {
                                                SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                                recordDate = sdfISO.parse(dateStr);
                                            } else {
                                                // 尝试标准格式
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                recordDate = sdf.parse(dateStr);
                                            }
                                        } catch (Exception e1) {
                                            try {
                                                // 尝试不带秒的格式
                                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                                recordDate = sdf2.parse(dateStr);
                                            } catch (Exception e2) {
                                                try {
                                                    // 尝试只带日期的格式
                                                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                    recordDate = sdf3.parse(dateStr);
                                                } catch (Exception e3) {
                                                    LogUtil.e("MentalHealthRecord", "解析日期失败: " + dateStr);
                                                }
                                            }
                                        }
                                    }
                                    
                                    // 只有在成功解析到日期时才设置，否则保持为null
                                    if (recordDate != null) {
                                        history.setHDate(recordDate);
                                        LogUtil.i("MentalHealthRecord", "成功解析日期: " + dateFormat.format(recordDate));
                                    } else {
                                        LogUtil.w("MentalHealthRecord", "无法解析日期，该记录将被跳过");
                                        continue; // 跳过没有有效日期的记录
                                    }
                                    
                                    // 检查记录是否有效（至少有一个非零值）
                                    if (id > 0 || uid > 0 || score > 0 || !"未知".equals(level)) {
                                        historyList.add(history);
                                        LogUtil.i("MentalHealthRecord", "添加真实测试记录: " + history.toString());
                                    }
                                } catch (Exception e) {
                                    LogUtil.e("MentalHealthRecord", "解析记录失败: " + e.getMessage());
                                }
                            }
                            
                            // 更新UI
                            runOnUiThread(() -> {
                                try {
                                    dismissProgressDialog();
                                    if (noDataTv != null) {
                                        noDataTv.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
                                    }
                                    
                                    if (adapter != null && recyclerView != null) {
                                        adapter.setNewData(historyList);
                                        recyclerView.setVisibility(historyList.isEmpty() ? View.GONE : View.VISIBLE);
                                    }
                                    
                                    if (lineChart != null) {
                                        updateChart();
                                        lineChart.setVisibility(historyList.isEmpty() ? View.GONE : View.VISIBLE);
                                    }
                                    
                                    // 如果有有效数据，保存最新分数到用户特定存储
                                    if (!historyList.isEmpty()) {
                                        DepressionTestHistory latestRecord = historyList.get(0);
                                        if (latestRecord != null && latestRecord.getHScore() != null && 
                                                latestRecord.getHUid() != null && latestRecord.getHUid() > 0) {
                                            // 保存到用户特定存储
                                            String userId = String.valueOf(latestRecord.getHUid());
                                            int score = latestRecord.getHScore();
                                            SPUtil.saveUserScore(MentalHealthRecordActivity.this, userId, score);
                                            LogUtil.i("MentalHealthRecord", "保存最新分数到用户特定存储: 用户ID=" + userId + ", 分数=" + score);
                                        }
                                    }
                                    
                                    if (historyList.isEmpty()) {
                                        ToastUtil.showBottomToast("暂无测试记录");
                                    } else {
                                        ToastUtil.showBottomToast("加载了 " + historyList.size() + " 条测试记录");
                                    }
                                } catch (Exception e) {
                                    LogUtil.e("MentalHealthRecord", "更新UI失败: " + e.getMessage());
                                    ToastUtil.showBottomToast("更新界面失败");
                                }
                            });
                        } else {
                            // 服务器返回错误
                            String errorMsg = jsonObject.optString("result", "未知错误");
                            LogUtil.e("MentalHealthRecord", "获取历史记录失败: " + errorMsg);
                            showNoData();
                        }
                    } catch (Exception e) {
                        LogUtil.e("MentalHealthRecord", "处理响应数据失败: " + e.getMessage());
                        showNoData();
                    }
                }
                
                @Override
                public void onError(Exception e) {
                    LogUtil.e("MentalHealthRecord", "获取历史记录失败: " + e.getMessage());
                    showNoData();
                }
            });
        } catch (Exception e) {
            LogUtil.e("MentalHealthRecord", "加载数据失败: " + e.getMessage());
            dismissProgressDialog();
            ToastUtil.showBottomToast("加载数据失败");
        }
    }

    /**
     * 显示无数据状态
     */
    private void showNoData() {
        runOnUiThread(() -> {
            try {
                dismissProgressDialog();
                if (noDataTv != null) {
                    noDataTv.setVisibility(View.VISIBLE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
                if (lineChart != null) {
                    lineChart.setVisibility(View.GONE);
                }
                ToastUtil.showBottomToast("暂无测试记录");
            } catch (Exception e) {
                LogUtil.e("MentalHealthRecord", "显示无数据状态失败: " + e.getMessage());
            }
        });
    }

    /**
     * 获取最新的抑郁测试分数
     * 尝试从抑郁测试历史记录中获取最新的测试分数
     */
    private int getLatestDepressionScore() {
        // 获取当前用户ID
        String userId = getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            LogUtil.e("MentalHealthRecord", "无法获取有效用户ID，返回默认分数");
            return 75; // 默认分数
        }
        
        // 从用户特定存储中获取分数
        int score = SPUtil.getUserScore(this, userId);
        LogUtil.i("MentalHealthRecord", "尝试获取用户ID " + userId + " 的特定分数: " + score);
        
        // 如果用户特定存储中没有，尝试从historyList中获取
        if (score <= 0 && historyList != null && !historyList.isEmpty()) {
            // 检查historyList中的第一条记录（最新记录）
            DepressionTestHistory latestRecord = historyList.get(0);
            if (latestRecord != null && latestRecord.getHScore() != null && latestRecord.getHUid() != null) {
                // 确保记录属于当前用户
                int recordUid = latestRecord.getHUid();
                int currentUid = Integer.parseInt(userId);
                
                if (recordUid == currentUid) {
                    score = latestRecord.getHScore();
                    LogUtil.i("MentalHealthRecord", "从历史记录中获取当前用户的最新分数: " + score);
                    
                    // 保存到用户特定存储中
                    SPUtil.saveUserScore(this, userId, score);
                } else {
                    LogUtil.w("MentalHealthRecord", "历史记录用户ID (" + recordUid + ") 与当前用户 (" + currentUid + ") 不匹配");
                }
            }
        }
        
        // 尝试从服务器获取最新分数
        if (score <= 0) {
            String latestScoreUrl = Urls.GET_LATEST_SCORE + userId;
            LogUtil.i("MentalHealthRecord", "尝试从服务器获取最新分数: " + latestScoreUrl);
            
            // 注意：这是同步请求，仅在无其他数据源时使用
            try {
                // 创建一个用于同步的标志
                final boolean[] completed = {false};
                final int[] serverScore = {0};
                
                HttpUtil.get(latestScoreUrl, null, new HttpUtil.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Object data = jsonObject.opt("data");
                                if (data != null && !(data instanceof JSONObject)) {
                                    serverScore[0] = jsonObject.optInt("data", 0);
                                    LogUtil.i("MentalHealthRecord", "服务器返回分数: " + serverScore[0]);
                                }
                            }
                        } catch (Exception e) {
                            LogUtil.e("MentalHealthRecord", "解析服务器分数失败: " + e.getMessage());
                        }
                        completed[0] = true;
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        LogUtil.e("MentalHealthRecord", "获取服务器分数失败: " + e.getMessage());
                        completed[0] = true;
                    }
                });
                
                // 等待最多2秒
                for (int i = 0; i < 20 && !completed[0]; i++) {
                    Thread.sleep(100);
                }
                
                if (serverScore[0] > 0) {
                    score = serverScore[0];
                    // 保存到用户特定存储
                    SPUtil.saveUserScore(this, userId, score);
                }
            } catch (Exception e) {
                LogUtil.e("MentalHealthRecord", "获取服务器分数异常: " + e.getMessage());
            }
        }
        
        // 如果仍然没有，使用默认分数
        if (score <= 0) {
            score = 75; // 默认分数
            LogUtil.i("MentalHealthRecord", "未找到用户 " + userId + " 的有效分数记录，使用默认分数: " + score);
        } else {
            LogUtil.i("MentalHealthRecord", "最终使用的分数: " + score + " (用户ID: " + userId + ")");
        }
        
        return score;
    }
    
    /**
     * 根据分数获取抑郁级别评估
     */
    private String getDepressionLevelByScore(int score) {
        if (score <= 0) {
            return "未知";
        } else if (score < 60) {
            return "重度抑郁";
        } else if (score < 70) {
            return "中度抑郁";
        } else if (score < 80) {
            return "轻度抑郁";
        } else {
            return "无抑郁";
        }
    }

    /**
     * 更新图表，仅当确认解析和显示列表正常后调用
     */
    private void updateChart() {
        try {
            if (lineChart == null) {
                LogUtil.e("MentalHealthRecord", "图表对象为空");
                return;
            }
            
            if (historyList == null || historyList.isEmpty()) {
                LogUtil.i("MentalHealthRecord", "没有历史数据，清空图表");
                lineChart.clear();
                lineChart.invalidate();
                return;
            }
            
            // 确保列表中的记录有有效的分数
            boolean hasValidScore = false;
            for (DepressionTestHistory history : historyList) {
                if (history != null && history.getHScore() != null && history.getHScore() > 0) {
                    hasValidScore = true;
                    break;
                }
            }
            
            if (!hasValidScore) {
                LogUtil.e("MentalHealthRecord", "没有有效的分数数据，不显示图表");
                lineChart.setVisibility(View.GONE);
                return;
            }
            
            List<Entry> entries = new ArrayList<>();
            // 修改：从最新的记录开始添加数据点，这样最新的数据会显示在右侧
            for (int i = historyList.size() - 1; i >= 0; i--) {
                try {
                    DepressionTestHistory history = historyList.get(i);
                    if (history != null && history.getHScore() != null) {
                        // 使用 (historyList.size() - 1 - i) 作为 x 轴的值，确保最新的数据在右侧
                        entries.add(new Entry(historyList.size() - 1 - i, history.getHScore()));
                        LogUtil.i("MentalHealthRecord", "添加数据点: x=" + (historyList.size() - 1 - i) + ", y=" + history.getHScore());
                    } else {
                        LogUtil.e("MentalHealthRecord", "无效的历史记录或分数: index=" + i);
                    }
                } catch (Exception e) {
                    LogUtil.e("MentalHealthRecord", "添加数据点时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            if (entries.isEmpty()) {
                LogUtil.e("MentalHealthRecord", "没有有效的数据点，清空图表");
                lineChart.clear();
                lineChart.setVisibility(View.GONE);
                lineChart.invalidate();
                return;
            }

            try {
                LineDataSet dataSet = new LineDataSet(entries, "抑郁测试得分");
                dataSet.setColor(getResources().getColor(R.color.colorPrimary));
                dataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
                dataSet.setLineWidth(2f);
                dataSet.setCircleRadius(4f);
                dataSet.setDrawValues(true);
                dataSet.setValueTextSize(10f);
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.setVisibility(View.VISIBLE);
                lineChart.invalidate();
                
                LogUtil.i("MentalHealthRecord", "图表更新成功，数据点数量: " + entries.size());
            } catch (Exception e) {
                LogUtil.e("MentalHealthRecord", "设置图表数据时出错: " + e.getMessage());
                e.printStackTrace();
                lineChart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogUtil.e("MentalHealthRecord", "更新图表时出错: " + e.getMessage());
            e.printStackTrace();
            if (lineChart != null) {
                lineChart.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 综合从多个位置获取当前用户ID
     */
    private String getCurrentUserId() {
        // 首先从静态变量获取
        String userId = BaseApplication.getUserId();
        LogUtil.i("获取用户ID", "从BaseApplication获取: " + userId);
        
        // 检查是否有效
        if (isValidUserId(userId)) {
            return userId;
        }
        
        // 尝试从SPUtil获取
        userId = SPUtil.getString(this, SPUtil.USER_ID, "");
        LogUtil.i("获取用户ID", "从SPUtil获取: " + userId);
        if (isValidUserId(userId)) {
            // 同步到BaseApplication
            BaseApplication.setUserId(userId);
            return userId;
        }
        
        // 尝试从PreferenceUtil获取
        userId = PreferenceUtil.getString("userId", "");
        LogUtil.i("获取用户ID", "从PreferenceUtil获取: " + userId);
        if (isValidUserId(userId)) {
            // 同步到其他地方
            BaseApplication.setUserId(userId);
            SPUtil.putString(this, SPUtil.USER_ID, userId);
            return userId;
        }
        
        // 所有方法都失败，返回null
        LogUtil.e("获取用户ID", "所有方法都失败，未找到有效用户ID");
        return null;
    }
    
    /**
     * 检查用户ID是否有效
     */
    private boolean isValidUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        
        // 检查是否为数字且大于0
        try {
            int id = Integer.parseInt(userId);
            return id > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 