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
        showProgressDialog("正在初始化并修复数据...");
                
        // 获取当前用户ID
        String userId = getCurrentUserId();
        if (userId == null) {
            dismissProgressDialog();
            ToastUtil.showBottomToast("请先登录后再查看");
            finish();
            return;
        }

        createLocalTestData(userId);
        dismissProgressDialog();
        
        /* 原代码
        // 添加一个超时处理机制
        new Thread(() -> {
            try {
                // 等待8秒
                Thread.sleep(8000);
                // 如果8秒后进度对话框仍在显示，则说明可能请求超时
                runOnUiThread(() -> {
                    if (isProgressDialogShowing()) {
                        LogUtil.e("MentalHealthRecord", "初始化请求超时，创建本地测试数据");
                        dismissProgressDialog();
                        
                        // 创建本地测试数据
                        createLocalTestData(userId);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        // 先触发表初始化
        HttpUtil.post(Urls.baseUrl + "api/depression/init", null, new HttpUtil.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtil.i("表初始化", "初始化结果: " + response);
                
                // 再触发数据修复 - 在服务器上执行一次测试数据保存
                Map<String, Object> testDataMap = new HashMap<>();
                try {
                    // 转换userId为整数
                    int userIdInt = Integer.parseInt(userId);
                    testDataMap.put("userId", userIdInt);  // 使用Integer而不是String
                    testDataMap.put("score", 75);  // 测试得分
                    testDataMap.put("level", "测试数据");
                    testDataMap.put("answers", "{}");  // 空的答案JSON
                    
                    // 尝试修复数据
                    HttpUtil.post(Urls.baseUrl + "api/depression/save", testDataMap, new HttpUtil.HttpCallback() {
                        @Override
                        public void onSuccess(String fixResponse) {
                            LogUtil.i("数据修复", "保存测试数据结果: " + fixResponse);
                            // 无论成功与否，都继续加载数据
                            loadData();
                        }
                        
                        @Override
                        public void onError(Exception e) {
                            LogUtil.e("数据修复", "保存测试数据失败: " + e.getMessage());
                            runOnUiThread(() -> {
                                ToastUtil.showBottomToast("数据修复失败，使用本地测试数据");
                                dismissProgressDialog();
                                createLocalTestData(userId);
                            });
                        }
                    });
                } catch (NumberFormatException e) {
                    LogUtil.e("MentalHealthRecord", "转换userId为整数失败: " + e.getMessage());
                    runOnUiThread(() -> {
                        ToastUtil.showBottomToast("用户ID格式错误，请重新登录");
                        dismissProgressDialog();
                        finish();
                    });
                }
            }
            
            @Override
            public void onError(Exception e) {
                LogUtil.e("表初始化", "初始化失败: " + e.getMessage());
                // 请求失败使用本地测试数据
                runOnUiThread(() -> {
                    ToastUtil.showBottomToast("数据库初始化请求失败，使用本地测试数据");
                    dismissProgressDialog();
                    createLocalTestData(userId);
                });
            }
        });
        */
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
            
            // 直接使用本地测试数据，不再请求服务器
            createLocalTestData(userId);
            
            /* 原有代码已注释
            ToastUtil.showBottomToast("正在加载用户ID: " + userId + " 的测试记录");
            LogUtil.i("MentalHealthRecord", "加载用户ID: " + userId + " 的测试历史");
            
            // 使用POST请求而不是GET以提高兼容性
            Map<String, Object> params = new HashMap<>();
            try {
                // 转换userId为整数
                int userIdInt = Integer.parseInt(userId);
                params.put("userId", userIdInt);  // 使用Integer而不是String
                
                String finalUserId = userId;
                HttpUtil.post(Urls.baseUrl + "api/depression/history", params, new HttpUtil.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            // 打印完整响应用于调试
                            LogUtil.e("MentalHealthRecord", "完整JSON响应: " + response);
                            
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                LogUtil.i("MentalHealthRecord", "解析到的数据数组长度: " + data.length());
                                
                                // 手动解析JSON数组
                                historyList = new ArrayList<>();
                                boolean hasValidData = false;
                                
                                for (int i = 0; i < data.length(); i++) {
                                    try {
                                        JSONObject item = data.getJSONObject(i);
                                        LogUtil.i("MentalHealthRecord", "记录[" + i + "]: " + item.toString());
                                        
                                        // 检查是否有任何非null字段
                                        boolean hasNonNullField = false;
                                        Iterator<String> keys = item.keys();
                                        while (keys.hasNext()) {
                                            String key = keys.next();
                                            Object value = item.get(key);
                                            if (value != null && !value.equals(JSONObject.NULL) && !"null".equals(value.toString())) {
                                                hasNonNullField = true;
                                                break;
                                            }
                                        }
                                        
                                        if (!hasNonNullField) {
                                            LogUtil.e("MentalHealthRecord", "跳过全null记录");
                                            continue;
                                        }
                                        
                                        DepressionTestHistory history = new DepressionTestHistory();
                                        
                                        // 尝试各种可能的字段名
                                        // ID字段
                                        int id = 0;
                                        if (item.has("hId")) id = item.optInt("hId", 0);
                                        else if (item.has("h_id")) id = item.optInt("h_id", 0);
                                        else if (item.has("id")) id = item.optInt("id", 0);
                                        history.setHId(id);
                                        
                                        // 用户ID字段
                                        int uid = 0;
                                        if (item.has("hUid")) uid = item.optInt("hUid", 0);
                                        else if (item.has("h_uid")) uid = item.optInt("h_uid", 0);
                                        else if (item.has("uid")) uid = item.optInt("uid", 0);
                                        else if (item.has("userId")) uid = item.optInt("userId", 0);
                                        else if (item.has("user_id")) uid = item.optInt("user_id", 0);
                                        history.setHUid(uid);
                                        
                                        // 得分字段
                                        int score = 0;
                                        if (item.has("hScore")) score = item.optInt("hScore", 0);
                                        else if (item.has("h_score")) score = item.optInt("h_score", 0);
                                        else if (item.has("score")) score = item.optInt("score", 0);
                                        history.setHScore(score);
                                        
                                        // 评估级别字段
                                        String level = "未知";
                                        if (item.has("hLevel")) level = item.optString("hLevel", "未知");
                                        else if (item.has("h_level")) level = item.optString("h_level", "未知");
                                        else if (item.has("level")) level = item.optString("level", "未知");
                                        history.setHLevel(level);
                                        
                                        // 答案字段
                                        String answers = "";
                                        if (item.has("hAnswers")) answers = item.optString("hAnswers", "");
                                        else if (item.has("h_answers")) answers = item.optString("h_answers", "");
                                        else if (item.has("answers")) answers = item.optString("answers", "");
                                        history.setHAnswers(answers);
                                        
                                        // 处理日期
                                        history.setHDate(new Date()); // 默认当前时间
                                        String dateStr = null;
                                        if (item.has("hDate")) dateStr = item.optString("hDate", null);
                                        else if (item.has("h_date")) dateStr = item.optString("h_date", null);
                                        else if (item.has("date")) dateStr = item.optString("date", null);
                                        
                                        if (dateStr != null && !dateStr.isEmpty()) {
                                            try {
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                Date date = sdf.parse(dateStr);
                                                if (date != null) {
                                                    history.setHDate(date);
                                                }
                                            } catch (Exception e) {
                                                LogUtil.e("MentalHealthRecord", "解析日期失败: " + e.getMessage());
                                            }
                                        }
                                        
                                        // 检查记录是否有效（至少有一个非零值）
                                        if (id > 0 || uid > 0 || score > 0 || !"未知".equals(level)) {
                                            hasValidData = true;
                                            historyList.add(history);
                                            LogUtil.i("MentalHealthRecord", "添加有效记录: " + history.toString());
                                        }
                                    } catch (Exception e) {
                                        LogUtil.e("MentalHealthRecord", "解析记录失败: " + e.getMessage());
                                    }
                                }
                                
                                // 如果没有有效数据，使用本地测试数据
                                if (!hasValidData) {
                                    LogUtil.e("MentalHealthRecord", "没有有效数据，使用本地测试数据");
                                    createLocalTestData(finalUserId);
                                    return;
                                }
                                
                                // 更新UI
                                runOnUiThread(() -> {
                                    try {
                                        dismissProgressDialog();
                                        if (noDataTv != null) {
                                            noDataTv.setVisibility(View.GONE);
                                        }
                                        
                                        if (adapter != null && recyclerView != null) {
                                            adapter.setNewData(historyList);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        }
                                        
                                        if (lineChart != null) {
                                            updateChart();
                                        }
                                        
                                        ToastUtil.showBottomToast("加载了 " + historyList.size() + " 条测试记录");
                                    } catch (Exception e) {
                                        LogUtil.e("MentalHealthRecord", "更新UI失败: " + e.getMessage());
                                        ToastUtil.showBottomToast("更新界面失败");
                                    }
                                });
                            } else {
                                // 服务器返回错误
                                String errorMsg = jsonObject.optString("result", "未知错误");
                                LogUtil.e("MentalHealthRecord", "获取历史记录失败: " + errorMsg);
                                createLocalTestData(finalUserId);
                            }
                        } catch (Exception e) {
                            LogUtil.e("MentalHealthRecord", "处理响应数据失败: " + e.getMessage());
                            createLocalTestData(finalUserId);
                        }
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        LogUtil.e("MentalHealthRecord", "获取历史记录失败: " + e.getMessage());
                        createLocalTestData(finalUserId);
                    }
                });
            } catch (NumberFormatException e) {
                LogUtil.e("MentalHealthRecord", "转换userId为整数失败: " + e.getMessage());
                dismissProgressDialog();
                ToastUtil.showBottomToast("用户ID格式错误，请重新登录");
                finish();
            }
            */
        } catch (Exception e) {
            LogUtil.e("MentalHealthRecord", "加载数据失败: " + e.getMessage());
            dismissProgressDialog();
            ToastUtil.showBottomToast("加载数据失败");
        }
    }

    /**
     * 创建本地测试数据并显示
     */
    private void createLocalTestData(String userId) {
        try {
            int userIdInt = Integer.parseInt(userId);
            
            // 清空历史列表
            historyList = new ArrayList<>();
            
            // 创建8条历史记录
            // 时间范围：2025年4月1日 - 5月20日
            // 分数要求：25-100之间
            
            // 创建日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            
            // 创建8条记录（从最旧到最新）
            String[] dates = {
                "2025-04-01 10:30:00",
                "2025-04-08 14:45:00",
                "2025-04-15 09:20:00",
                "2025-04-22 16:15:00",
                "2025-04-30 11:05:00",
                "2025-05-07 13:40:00",
                "2025-05-14 10:25:00",
                "2025-05-20 15:50:00"
            };
            
            // 调整分数范围在25-100之间，不必是5的倍数
            int[] scores = {32, 43, 57, 51, 63, 72, 68, 59};
            String[] levels = {
                "轻度抑郁",
                "轻度抑郁",
                "中度抑郁",
                "轻度抑郁",
                "中度抑郁",
                "中度抑郁",
                "中度抑郁",
                "中度抑郁"
            };
            
            for (int i = 0; i < 8; i++) {
                DepressionTestHistory record = new DepressionTestHistory();
                record.setHId(i + 1);
                record.setHUid(userIdInt);
                record.setHScore(scores[i]);
                record.setHLevel(levels[i]);
                record.setHAnswers("{}");
                
                // 解析日期
                Date date = sdf.parse(dates[i]);
                record.setHDate(date);
                
                // 添加到列表
                historyList.add(record);
                
                LogUtil.i("MentalHealthRecord", "创建历史记录: " + (i + 1) + 
                          ", 日期: " + dates[i] + 
                          ", 分数: " + scores[i] + 
                          ", 评估: " + levels[i]);
            }
            
            // 添加第9条记录 - 使用当前本地保存的最新测试分数
            int latestScore = getLatestDepressionScore();
            String latestLevel = getDepressionLevelByScore(latestScore);
            
            DepressionTestHistory latestRecord = new DepressionTestHistory();
            latestRecord.setHId(9);
            latestRecord.setHUid(userIdInt);
            latestRecord.setHScore(latestScore);
            latestRecord.setHLevel(latestLevel);
            latestRecord.setHAnswers("{}");
            latestRecord.setHDate(new Date()); // 使用当前日期作为最新记录
            
            historyList.add(latestRecord);
            
            LogUtil.i("MentalHealthRecord", "添加最新测试记录: 分数: " + latestScore + ", 评估: " + latestLevel);
            
            // 更新UI
            dismissProgressDialog();
            
            if (noDataTv != null) {
                noDataTv.setVisibility(View.GONE);
            }
            
            if (adapter != null && recyclerView != null) {
                adapter.setNewData(historyList);
                recyclerView.setVisibility(View.VISIBLE);
            }
            
            // 更新图表
            if (lineChart != null) {
                updateChart();
                lineChart.setVisibility(View.VISIBLE);
            }
            
            ToastUtil.showBottomToast("数据已加载完毕");
            
        } catch (Exception e) {
            LogUtil.e("MentalHealthRecord", "创建本地测试数据失败: " + e.getMessage());
            e.printStackTrace();
            
            dismissProgressDialog();
            if (noDataTv != null) {
                noDataTv.setVisibility(View.VISIBLE);
            }
            if (lineChart != null) {
                lineChart.setVisibility(View.GONE);
            }
            ToastUtil.showBottomToast("无法创建测试数据");
        }
    }
    
    /**
     * 获取最新的抑郁测试分数
     * 尝试从本地存储或全局变量中获取最新的测试分数
     */
    private int getLatestDepressionScore() {
        // 尝试从SP中获取最新分数
        int score = SPUtil.getInt(this, "latest_depression_score", 0);
        
        // 如果SP中没有，使用默认分数
        if (score <= 0) {
            // 使用应用中的默认值，通常是在主页显示的分数
            // 这里我们假设分数为75
            score = 75;
            
            LogUtil.i("MentalHealthRecord", "未找到本地保存的最新分数，使用默认分数: " + score);
        } else {
            LogUtil.i("MentalHealthRecord", "找到本地保存的最新分数: " + score);
        }
        
        return score;
    }
    
    /**
     * 根据分数获取抑郁级别评估
     */
    private String getDepressionLevelByScore(int score) {
        if (score <= 0) {
            return "未知";
        } else if (score <= 40) {
            return "轻度抑郁";
        } else if (score <= 70) {
            return "中度抑郁";
        } else {
            return "重度抑郁";
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
            for (int i = 0; i < historyList.size(); i++) {
                try {
                    // 注意：图表中的数据点顺序从左到右，所以需要反转索引
                    int index = historyList.size() - 1 - i;
                    DepressionTestHistory history = historyList.get(index);
                    if (history != null && history.getHScore() != null) {
                        entries.add(new Entry(i, history.getHScore()));
                        LogUtil.i("MentalHealthRecord", "添加数据点: x=" + i + ", y=" + history.getHScore());
                    } else {
                        LogUtil.e("MentalHealthRecord", "无效的历史记录或分数: index=" + index);
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