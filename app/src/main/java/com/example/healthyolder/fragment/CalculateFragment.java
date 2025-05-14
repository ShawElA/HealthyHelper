package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.ChatGptResult;
import com.example.healthyolder.bean.ChatResult;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.DeepseekChatResult;
import com.example.healthyolder.bean.DeepseekResult;
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.LogUtil;
import com.example.healthyolder.util.MarkdownUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.OkHttpChatUtil;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.CommonToolBar;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CalculateFragment extends Fragment{

    View view;
    @BindView(R.id.rv_list)
    RecyclerView rv_comment;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srl_refresh;
    @BindView(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @BindView(R.id.ctb_title)
    CommonToolBar ctb_title;
    @BindView(R.id.rl_comment)
    RelativeLayout rl_comment;
    @BindView(R.id.et_comment)
    EditText et_comment;
    @BindView(R.id.btn_send)
    View btn_send;
    
    private List<ChatResult.DataBean> arrayList = new ArrayList<>();
    private CommonAdapter<ChatResult.DataBean> adapter;
    private String gid = "1";
    private String gName = "与智能医生聊天";
    private boolean isProcessing = false;

    public static CalculateFragment newInstance(String param1, String param2) {
        CalculateFragment fragment = new CalculateFragment();
        Bundle args = new Bundle();
     
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.btn_send)
    public void send(){
        if (isProcessing) {
            ToastUtil.showBottomToast("请等待上一条消息处理完成");
            return;
        }
        
        if (TextUtil.isValidate(et_comment.getText().toString())){
            String userMessage = et_comment.getText().toString();
            
            // 先添加用户消息到UI上, 但不从服务器刷新
            addLocalUserMessage(userMessage);
            
            // 然后发送到服务器
            submitMsgToServer(BaseApplication.getUserId(), userMessage);
            
            // 禁用发送按钮，显示处理中状态
            setProcessingState(true);
            
            // 然后请求GPT响应
            getGPTResponse(userMessage);
        } else {
            ToastUtil.showBottomToast("请输入您的问题");
        }
        et_comment.setText("");
    }

    private void setProcessingState(boolean processing) {
        isProcessing = processing;
        btn_send.setEnabled(!processing);
        
        // 如果正在处理，添加一个临时的"正在输入..."消息
        if (processing) {
            addTypingIndicator();
        } else {
            removeTypingIndicator();
        }
    }
    
    private void addTypingIndicator() {
        // 临时添加一个"医生正在输入..."的消息
        ChatResult.DataBean typingIndicator = new ChatResult.DataBean();
        typingIndicator.setC_uid("-1"); // AI的ID
        typingIndicator.setC_remark("医生正在思考...");
        typingIndicator.setC_date(System.currentTimeMillis() + "");
        typingIndicator.setTemp_id("typing_indicator");
        
        arrayList.add(typingIndicator);
        if (adapter != null) {
            adapter.notifyItemInserted(arrayList.size() - 1);
            rv_comment.scrollToPosition(arrayList.size() - 1);
        }
    }
    
    private void removeTypingIndicator() {
        // 移除所有临时消息
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            ChatResult.DataBean item = arrayList.get(i);
            if (item.getTemp_id() != null && item.getTemp_id().equals("typing_indicator")) {
                arrayList.remove(i);
                if (adapter != null) {
                    adapter.notifyItemRemoved(i);
                }
            }
        }
    }

    private void getGPTResponse(String question){
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("model", Configs.MODEL);
        
        // 构造messages数组
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统消息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的医生助手，你专门面向老年人抑郁症患者或潜在患者，请以专业、友好的口吻回答用户的健康问题，不时对用户进行鼓励。" +
                "在回复时允许使用Markdown语法使内容更清晰。");
        messages.add(systemMessage);
        
        // 添加用户消息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", question);
        messages.add(userMessage);
        
        parameter.put("messages", messages);
        parameter.put("max_tokens", Configs.MAX_TOKENS);
        parameter.put("temperature", 0.2); // 温度还是0.3以下吧，否则指令遵循度太低
        
        OkHttpChatUtil.post(Urls.GPTURL, parameter, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProcessingState(false);
                        ToastUtil.showBottomToast("网络连接失败，请检查网络后重试");
                        LogUtil.i("Deepseek Error", "Network error: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string().trim();
                LogUtil.i("response", response.code() + "  " + content);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProcessingState(false);
                        
                        if (response.code() != 200) {
                            ToastUtil.showBottomToast("服务器返回错误，请稍后重试");
                            LogUtil.i("Deepseek Error", "HTTP error: " + response.code() + " - " + content);
                            return;
                        }
                        
                        try {
                            DeepseekChatResult result = new Gson().fromJson(content, DeepseekChatResult.class);
                            if (result != null && result.getChoices() != null && !result.getChoices().isEmpty()){
                                DeepseekChatResult.ChoicesBean.MessageBean message = result.getChoices().get(0).getMessage();
                                if (message != null && message.getContent() != null) {
                                    String aiResponse = message.getContent().trim();
                                    
                                    // 先在UI上显示AI回复
                                    addLocalAIMessage(aiResponse);
                                    
                                    // 然后在后台发送到服务器
                                    submitMsgToServer("-1", aiResponse);
                                } else {
                                    ToastUtil.showBottomToast("AI返回了空响应，请重新提问");
                                    LogUtil.i("Deepseek Error", "Empty message content");
                                }
                            } else {
                                ToastUtil.showBottomToast("解析AI响应失败，请重新提问");
                                LogUtil.i("Deepseek Error", "Failed to parse response: " + content);
                            }
                        } catch (Exception e) {
                            ToastUtil.showBottomToast("处理AI响应时出错");
                            LogUtil.i("Deepseek Error", "Exception: " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    // 添加本地消息显示，不从服务器刷新
    private void addLocalUserMessage(String message) {
        ChatResult.DataBean localMsg = new ChatResult.DataBean();
        localMsg.setC_uid(BaseApplication.getUserId());
        localMsg.setC_remark(message);
        localMsg.setC_date(System.currentTimeMillis() + "");
        localMsg.setTemp_id("local_msg_" + System.currentTimeMillis());
        
        arrayList.add(localMsg);
        if (adapter != null) {
            adapter.notifyItemInserted(arrayList.size() - 1);
            rv_comment.scrollToPosition(arrayList.size() - 1);
        }
    }
    
    // 添加本地AI回复，不从服务器刷新
    private void addLocalAIMessage(String message) {
        ChatResult.DataBean localMsg = new ChatResult.DataBean();
        localMsg.setC_uid("-1"); // AI的ID
        localMsg.setC_remark(message);
        localMsg.setC_date(System.currentTimeMillis() + "");
        localMsg.setTemp_id("local_ai_" + System.currentTimeMillis());
        
        arrayList.add(localMsg);
        if (adapter != null) {
            adapter.notifyItemInserted(arrayList.size() - 1);
            rv_comment.scrollToPosition(arrayList.size() - 1);
        }
    }

    // 只负责发送消息到服务器，不刷新UI
    private void submitMsgToServer(String uid, String content){
        Map<String, String> p = new HashMap<>();
        p.put("g_id", gid);
        p.put("remark", content);
        p.put("u_id", uid);
        p.put("type", "0");
        HttpUtil.getResponse(Urls.SENDMESSAGE, p, null, new ObjectCallBack<EmptyResult>(EmptyResult.class) {
            @Override
            public void onSuccess(EmptyResult response) {
                // 消息发送成功，但不立即刷新页面
                // 这样可以保持"正在思考"的提示直到AI响应到达
            }

            @Override
            public void onFail(Call call, Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showBottomToast("网络错误，消息可能未保存");
                        }
                    });
                }
            }
        });
    }

    // 原有submitMsg方法，先留着，只支持ChatGPT
    private void submitMsg(String uid, String content) {
        Map<String, String> p = new HashMap<>();
        p.put("g_id", gid);
        p.put("remark", content);
        p.put("u_id", uid);
        p.put("type", "0");
        HttpUtil.getResponse(Urls.SENDMESSAGE, p, null, new ObjectCallBack<EmptyResult>(EmptyResult.class) {
            @Override
            public void onSuccess(EmptyResult response) {
                if (response.isSuccess()){
                    initData();
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showBottomToast("消息发送失败，请重试");
                                setProcessingState(false);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showBottomToast("网络错误，请检查网络后重试");
                            setProcessingState(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.fragment_calculate, container, false);
        }
        ButterKnife.bind(this, view);
        initData();
        initEvent();
        return view;
    }

    public void initData(){
        srl_refresh.setRefreshing(true);
        HttpUtil.getResponse(Urls.SELECTCHAT, null, null, new ObjectCallBack<ChatResult>(ChatResult.class) {
            @Override
            public void onSuccess(ChatResult response) {
                srl_refresh.setRefreshing(false);
                if (response.isSuccess()){
                    disposeCommentData(response);
                    initRecyclerView();
                }else {
                    setLayoutVisible(View.VISIBLE, View.GONE);
                    ToastUtil.showBottomToast("获取聊天记录失败");
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                srl_refresh.setRefreshing(false);
                ToastUtil.showBottomToast("网络错误，请检查网络后重试");
            }
        });
    }

    public void initEvent(){
        ctb_title.setMiddleTitleText(gName);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    //处理数据
    private void disposeCommentData(ChatResult result){
        arrayList.clear();
        // 添加所有数据
        arrayList.addAll(result.getData());
        
        // 根据c_date（消息时间）对消息进行排序，确保消息按时间顺序显示
        Collections.sort(arrayList, new Comparator<ChatResult.DataBean>() {
            @Override
            public int compare(ChatResult.DataBean msg1, ChatResult.DataBean msg2) {
                return msg1.getC_date().compareTo(msg2.getC_date());
            }
        });
    }

    private void initRecyclerView(){
        if (adapter == null){
            rv_comment.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CommonAdapter<ChatResult.DataBean>(getContext(), R.layout.item_chat_message, arrayList) {
                @Override
                public void convert(ViewHolder holder, ChatResult.DataBean o) {
                    ImageView iv_other = holder.getView(R.id.iv_other);
                    ImageView iv_me = holder.getView(R.id.iv_me);
                    TextView tv_other = holder.getView(R.id.tv_other);
                    TextView tv_me = holder.getView(R.id.tv_me);
                    LinearLayout ll_other = holder.getView(R.id.ll_other);
                    LinearLayout ll_me = holder.getView(R.id.ll_me);
                    
                    // 检查消息发送者
                    // 如果消息是当前用户发送的，显示在右侧
                    if (o.getC_uid().equals(BaseApplication.getUserId())){
                        ll_me.setVisibility(View.VISIBLE);
                        ll_other.setVisibility(View.GONE);
                        tv_me.setText(o.getC_remark());
                    }
                    // 如果消息是AI（-1）发送的，显示在左侧并使用Markdown渲染
                    else {
                        ll_other.setVisibility(View.VISIBLE);
                        ll_me.setVisibility(View.GONE);
                        // 使用Markdown渲染AI回复
                        MarkdownUtil.setMarkdown(getContext(), tv_other, o.getC_remark());
                    }
                }
            };
            rv_comment.setAdapter(adapter);
        } else{
            adapter.notifyDataSetChanged();
        }
        rv_comment.scrollToPosition(arrayList.size() - 1);
        if (arrayList.size() == 0){
            setLayoutVisible(View.VISIBLE, View.GONE);
        }else {
            setLayoutVisible(View.GONE, View.VISIBLE);
        }
    }

    private void setLayoutVisible(int v1, int v2){
        rl_no_data.setVisibility(v1);
        rv_comment.setVisibility(v2);
    }
}
