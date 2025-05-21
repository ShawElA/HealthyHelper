package com.example.healthyolder.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Locale;
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
    @BindView(R.id.btn_voice)
    View btn_voice;
    
    private List<ChatResult.DataBean> arrayList = new ArrayList<>();
    private CommonAdapter<ChatResult.DataBean> adapter;
    private String gid = "1";
    private String gName = "与智能医生聊天";
    private boolean isProcessing = false;
    
    // 语音识别相关变量
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;
    // 语音识别功能可用状态
    private boolean isSpeechRecognitionAvailable = false;
    
    // 文本转语音相关变量
    private TextToSpeech textToSpeech;
    private boolean isTTSInitialized = false;
    private String currentSpeakingText = null;
    private boolean isSpeaking = false;
    // 当前正在朗读的视图
    private ImageView currentSpeakingView = null;

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
    
    // 处理语音输入按钮点击事件
    @OnClick(R.id.btn_voice)
    public void startVoiceInput() {
        // 如果正在处理消息，不允许语音输入
        if (isProcessing) {
            ToastUtil.showBottomToast("请等待上一条消息处理完成");
            return;
        }
        
        // 检查语音识别是否可用
        if (!isSpeechRecognitionAvailable) {
            checkSpeechRecognitionAvailability();
            if (!isSpeechRecognitionAvailable) {
                ToastUtil.showBottomToast("您的设备不支持语音识别功能");
                return;
            }
        }
        
        // 如果正在监听，点击按钮停止监听
        if (isListening) {
            stopListening();
            return;
        }
        
        // 检查并请求录音权限
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            // 已有权限，开始语音识别
            startListening();
        }
    }
    
    // 检查语音识别服务是否可用
    private void checkSpeechRecognitionAvailability() {
        // 检查设备是否支持语音识别
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        
        if (activities.size() == 0) {
            isSpeechRecognitionAvailable = false;
            LogUtil.i("SpeechRecognition", "设备不支持语音识别");
        } else {
            try {
                // 尝试创建语音识别器
                SpeechRecognizer testRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                if (testRecognizer != null) {
                    testRecognizer.destroy();
                    isSpeechRecognitionAvailable = true;
                    LogUtil.i("SpeechRecognition", "语音识别可用");
                } else {
                    isSpeechRecognitionAvailable = false;
                    LogUtil.i("SpeechRecognition", "无法创建语音识别器");
                }
            } catch (Exception e) {
                isSpeechRecognitionAvailable = false;
                LogUtil.i("SpeechRecognition", "语音识别初始化异常: " + e.getMessage());
            }
        }
    }
    
    // 使用系统语音识别活动作为备选方案
    private void startSpeechRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请说出您的问题");
        
        try {
            startActivityForResult(intent, 1000);
        } catch (Exception e) {
            ToastUtil.showBottomToast("无法启动语音识别");
            LogUtil.e("SpeechRecognition", "启动语音识别活动失败: " + e.getMessage());
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1000 && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String text = results.get(0);
                et_comment.setText(text);
                et_comment.setSelection(text.length());
            }
        }
    }
    
    // 开始语音识别
    private void startListening() {
        try {
            // 初始化SpeechRecognizer
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                speechRecognizer.setRecognitionListener(recognitionListener);
            }
            
            // 创建识别Intent
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA.toString()); // 设置为中文
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
            
            // 开始监听
            isListening = true;
            ToastUtil.showBottomToast("请开始说话...");
            LogUtil.i("SpeechRecognition", "开始语音识别");
            
            // 更改按钮状态（例如颜色或图标）以表示正在录音
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn_voice.setBackgroundTintList(
                        ContextCompat.getColorStateList(getContext(), R.color.colorAccent));
            }
            
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            LogUtil.e("SpeechRecognition", "启动语音识别失败: " + e.getMessage());
            isListening = false;
            ToastUtil.showBottomToast("启动语音识别失败，尝试备选方案");
            
            // 失败后尝试使用活动方式启动语音识别
            startSpeechRecognitionActivity();
        }
    }
    
    // 停止语音识别
    private void stopListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
                LogUtil.i("SpeechRecognition", "停止语音识别");
            } catch (Exception e) {
                LogUtil.e("SpeechRecognition", "停止语音识别出错: " + e.getMessage());
            }
        }
        isListening = false;
        
        // 恢复按钮状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btn_voice.setBackgroundTintList(
                    ContextCompat.getColorStateList(getContext(), R.color.colorTheme));
        }
    }
    
    // 语音识别监听器
    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // 准备好说话时
            LogUtil.i("SpeechRecognition", "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 开始说话时
            LogUtil.i("SpeechRecognition", "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float v) {
            // 语音音量变化
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // 接收到语音数据
            LogUtil.i("SpeechRecognition", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            // 说话结束时
            LogUtil.i("SpeechRecognition", "onEndOfSpeech");
            isListening = false;
            
            // 恢复按钮状态
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn_voice.setBackgroundTintList(
                                    ContextCompat.getColorStateList(getContext(), R.color.colorTheme));
                        }
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode) {
            // 识别错误时
            isListening = false;
            LogUtil.e("SpeechRecognition", "onError: " + errorCode);
            
            // 恢复按钮状态
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            btn_voice.setBackgroundTintList(
                                    ContextCompat.getColorStateList(getContext(), R.color.colorTheme));
                        }
                        
                        // 根据错误码显示不同提示
                        String message;
                        switch (errorCode) {
                            case SpeechRecognizer.ERROR_AUDIO:
                                message = "音频录制出错";
                                break;
                            case SpeechRecognizer.ERROR_CLIENT:
                                message = "客户端出错";
                                break;
                            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                message = "缺少录音权限";
                                break;
                            case SpeechRecognizer.ERROR_NETWORK:
                                message = "网络连接错误";
                                break;
                            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                                message = "网络连接超时";
                                break;
                            case SpeechRecognizer.ERROR_NO_MATCH:
                                message = "未识别到语音";
                                break;
                            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                message = "识别服务忙";
                                break;
                            case SpeechRecognizer.ERROR_SERVER:
                                message = "服务器错误";
                                break;
                            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                                message = "未检测到语音输入";
                                break;
                            default:
                                message = "语音识别出错";
                                break;
                        }
                        ToastUtil.showBottomToast(message);
                        
                        // 大多数错误情况下，尝试备选方案
                        if (errorCode != SpeechRecognizer.ERROR_NO_MATCH && 
                            errorCode != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            startSpeechRecognitionActivity();
                        }
                    }
                });
            }
        }

        @Override
        public void onResults(Bundle results) {
            // 识别结果返回
            LogUtil.i("SpeechRecognition", "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            
            if (matches != null && !matches.isEmpty()) {
                final String text = matches.get(0);
                LogUtil.i("SpeechRecognition", "识别结果: " + text);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 将识别结果添加到输入框
                            et_comment.setText(text);
                            // 将光标移至末尾
                            et_comment.setSelection(text.length());
                        }
                    });
                }
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            // 部分识别结果返回
            LogUtil.i("SpeechRecognition", "onPartialResults");
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // 其他事件
            LogUtil.i("SpeechRecognition", "onEvent: " + i);
        }
    };

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限获取成功，开始语音识别
                startListening();
            } else {
                // 权限被拒绝
                ToastUtil.showBottomToast("需要录音权限才能使用语音输入功能");
            }
        }
    }

    private void setProcessingState(boolean processing) {
        isProcessing = processing;
        btn_send.setEnabled(!processing);
        btn_voice.setEnabled(!processing);
        
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
        // AI的ID为当前用户ID的负数
        String userId = BaseApplication.getUserId();
        String aiUserId = "-" + userId;
        typingIndicator.setC_uid(aiUserId);
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
                "在回复时允许使用Markdown语法使内容更清晰。请不要使用表情符号和特殊字符。");
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
                                    
                                    // 过滤AI回复中的emoji和特殊字符
                                    aiResponse = filterSpecialCharacters(aiResponse);
                                    
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

    /**
     * 过滤特殊字符，移除emoji和其他可能导致数据库存储问题的字符
     * @param input 原始文本
     * @return 过滤后的文本
     */
    private String filterSpecialCharacters(String input) {
        if (input == null) {
            return "";
        }
        
        try {
            // 移除emoji和其他非BMP字符（4字节Unicode字符）
            // 这将移除大多数可能导致MySQL utf8编码问题的字符
            return input.replaceAll("[^\\u0000-\\uFFFF]", "")
                    // 替换一些可能导致问题的控制字符
                    .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        } catch (Exception e) {
            LogUtil.e("CharFilter", "过滤特殊字符出错: " + e.getMessage());
            // 如果过滤出错，则尝试更强力的过滤方法
            return input.replaceAll("[^\\p{ASCII}]", "");
        }
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
        // 将AI的c_uid设为用户ID的负数形式，而不是统一的"-1"
        String userId = BaseApplication.getUserId();
        String aiUserId = "-" + userId; // 用户对应的AI回答ID为用户ID的负数
        
        localMsg.setC_uid(aiUserId);
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
        
        // 如果是AI回复，将c_uid设为用户ID的负数形式
        if (uid.equals("-1")) {
            // 获取当前用户ID
            String userId = BaseApplication.getUserId();
            // 将AI的uid设为用户ID的负数
            uid = "-" + userId;
        }
        
        p.put("u_id", uid);
        p.put("type", "0");
        HttpUtil.getResponse(Urls.SENDMESSAGE, p, null, new ObjectCallBack<EmptyResult>(EmptyResult.class) {
            @Override
            public void onSuccess(EmptyResult response) {
                // 消息发送成功，但不立即刷新页面
                // 这样可以保持"正在思考"的提示直到AI响应到达
                
                // 对于新用户首次发送消息时确保消息会显示在界面上，不依赖服务器刷新
                if (arrayList.isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 消息已经被添加到界面，但为了确保adapter正确显示
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
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
        
        // 初始化时检查语音识别可用性
        checkSpeechRecognitionAvailability();
        
        // 初始化文本转语音
        initTextToSpeech();
        
        initData();
        initEvent();
        return view;
    }

    public void initData(){
        srl_refresh.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("u_id", BaseApplication.getUserId());
        HttpUtil.getResponse(Urls.SELECTCHAT, params, null, new ObjectCallBack<ChatResult>(ChatResult.class) {
            @Override
            public void onSuccess(ChatResult response) {
                srl_refresh.setRefreshing(false);
                if (response.isSuccess()){
                    // 即使返回的数据为空，也视为成功，并初始化RecyclerView
                    disposeCommentData(response);
                    initRecyclerView();
                }else {
                    // 服务器返回失败时，仍然初始化空的RecyclerView
                    arrayList.clear();
                    initRecyclerView();
                    ToastUtil.showBottomToast("获取聊天记录失败");
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                srl_refresh.setRefreshing(false);
                // 网络错误时，同样初始化空的RecyclerView
                arrayList.clear();
                initRecyclerView();
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
                public void convert(ViewHolder holder, final ChatResult.DataBean o) {
                    ImageView iv_other = holder.getView(R.id.iv_other);
                    ImageView iv_me = holder.getView(R.id.iv_me);
                    TextView tv_other = holder.getView(R.id.tv_other);
                    TextView tv_me = holder.getView(R.id.tv_me);
                    LinearLayout ll_other = holder.getView(R.id.ll_other);
                    LinearLayout ll_me = holder.getView(R.id.ll_me);
                    final ImageView iv_speaker = holder.getView(R.id.iv_speaker);
                    
                    // 检查消息发送者
                    String userId = BaseApplication.getUserId();
                    String aiUserId = "-" + userId; // 用户对应的AI回答ID
                    
                    // 如果消息是当前用户发送的，显示在右侧
                    if (o.getC_uid().equals(userId)){
                        ll_me.setVisibility(View.VISIBLE);
                        ll_other.setVisibility(View.GONE);
                        tv_me.setText(o.getC_remark());
                    }
                    // 如果消息是对应AI发送的（用户ID的负数），显示在左侧并使用Markdown渲染
                    else if (o.getC_uid().equals(aiUserId)){
                        ll_other.setVisibility(View.VISIBLE);
                        ll_me.setVisibility(View.GONE);
                        // 使用Markdown渲染AI回复
                        MarkdownUtil.setMarkdown(getContext(), tv_other, o.getC_remark());
                        
                        // 检查是否是正在朗读的消息
                        if (isSpeaking && o.getC_remark().equals(currentSpeakingText)) {
                            iv_speaker.setImageResource(android.R.drawable.ic_btn_speak_now);
                            iv_speaker.setAlpha(1.0f);
                            currentSpeakingView = iv_speaker;
                        } else {
                            iv_speaker.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                            iv_speaker.setAlpha(0.7f);
                        }
                        
                        // 为AI回复添加点击事件，触发朗读功能
                        View.OnClickListener speakClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                speakText(o.getC_remark(), iv_speaker);
                            }
                        };
                        tv_other.setOnClickListener(speakClickListener);
                        iv_speaker.setOnClickListener(speakClickListener);
                    }
                    // 其他情况（例如其他用户的消息）不显示
                    else {
                        ll_other.setVisibility(View.GONE);
                        ll_me.setVisibility(View.GONE);
                    }
                }
            };
            rv_comment.setAdapter(adapter);
        } else{
            adapter.notifyDataSetChanged();
        }
        
        // 滚动到最新消息
        if (arrayList.size() > 0) {
            rv_comment.scrollToPosition(arrayList.size() - 1);
        }
        
        // 即使没有聊天记录，也显示聊天界面，让用户可以发送消息
        if (arrayList.size() == 0){
            // 改为显示空白聊天界面，而不是"无数据"界面
            setLayoutVisible(View.GONE, View.VISIBLE);
            // 显示一个欢迎提示
            showWelcomeMessage();
        } else {
            setLayoutVisible(View.GONE, View.VISIBLE);
        }
    }

    // 为新用户显示欢迎提示消息
    private void showWelcomeMessage() {
        // 只在UI上显示欢迎消息，不存入数据库
        ChatResult.DataBean welcomeMsg = new ChatResult.DataBean();
        // AI的ID为当前用户ID的负数
        String userId = BaseApplication.getUserId();
        String aiUserId = "-" + userId;
        welcomeMsg.setC_uid(aiUserId);
        welcomeMsg.setC_remark("您好！我是智能医生助手，很高兴为您服务。请问有什么健康问题需要咨询吗？");
        welcomeMsg.setC_date(System.currentTimeMillis() + "");
        welcomeMsg.setTemp_id("welcome_msg");
        
        arrayList.add(welcomeMsg);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            rv_comment.scrollToPosition(arrayList.size() - 1);
        }
    }

    private void setLayoutVisible(int v1, int v2){
        rl_no_data.setVisibility(v1);
        rv_comment.setVisibility(v2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // 释放语音识别器资源
        if (speechRecognizer != null) {
            try {
                speechRecognizer.destroy();
                speechRecognizer = null;
            } catch (Exception e) {
                LogUtil.e("SpeechRecognition", "销毁语音识别器出错: " + e.getMessage());
            }
        }
        
        // 释放文本转语音资源
        if (textToSpeech != null) {
            try {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            } catch (Exception e) {
                LogUtil.e("TTS", "销毁文本转语音引擎出错: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // 页面暂停时停止朗读
        stopSpeaking();
    }
    
    /**
     * 初始化文本转语音引擎
     */
    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置语言为中文
                    int result = textToSpeech.setLanguage(Locale.CHINESE);
                    
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        LogUtil.e("TTS", "中文语言不可用，尝试使用系统默认语言");
                        result = textToSpeech.setLanguage(Locale.getDefault());
                        
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            LogUtil.e("TTS", "系统默认语言也不可用");
                            ToastUtil.showBottomToast("文本朗读功能不可用");
                            return;
                        }
                    }
                    
                    // 设置语速和音调
                    textToSpeech.setSpeechRate(0.9f);  // 稍微慢一点，适合老年人
                    textToSpeech.setPitch(1.0f);      // 正常音调
                    
                    // 设置完成监听器
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {
                                isSpeaking = true;
                            }

                            @Override
                            public void onDone(String utteranceId) {
                                isSpeaking = false;
                                
                                // 在UI线程中更新UI
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (currentSpeakingView != null) {
                                                currentSpeakingView.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                                                currentSpeakingView.setAlpha(0.7f);
                                                currentSpeakingView = null;
                                            }
                                            currentSpeakingText = null;
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(String utteranceId) {
                                isSpeaking = false;
                                
                                // 在UI线程中更新UI
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (currentSpeakingView != null) {
                                                currentSpeakingView.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                                                currentSpeakingView.setAlpha(0.7f);
                                                currentSpeakingView = null;
                                            }
                                            currentSpeakingText = null;
                                            ToastUtil.showBottomToast("朗读出错");
                                        }
                                    });
                                }
                            }
                        });
                    }
                    
                    isTTSInitialized = true;
                    LogUtil.i("TTS", "文本转语音初始化成功");
                } else {
                    LogUtil.e("TTS", "文本转语音初始化失败");
                }
            }
        });
    }
    
    /**
     * 朗读文本内容
     * @param text 要朗读的文本
     * @param speakerIcon 喇叭图标视图
     */
    private void speakText(String text, ImageView speakerIcon) {
        if (!isTTSInitialized) {
            ToastUtil.showBottomToast("文本朗读引擎未初始化");
            return;
        }
        
        // 如果正在朗读同一段文本，则停止
        if (isSpeaking && text.equals(currentSpeakingText)) {
            stopSpeaking();
            return;
        }
        
        // 停止当前朗读，准备朗读新内容
        stopSpeaking();
        
        // 处理文本，去除Markdown语法等
        String cleanText = cleanTextForSpeech(text);
        currentSpeakingText = text;
        currentSpeakingView = speakerIcon;
        
        // 更新UI，显示正在朗读的状态
        if (currentSpeakingView != null) {
            currentSpeakingView.setImageResource(android.R.drawable.ic_btn_speak_now);
            currentSpeakingView.setAlpha(1.0f);
        }
        
        // 开始朗读
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageId");
            textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, bundle, "messageId");
        } else {
            HashMap<String, String> hash = new HashMap<>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageId");
            textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, hash);
        }
        
        ToastUtil.showBottomToast("正在朗读...");
    }
    
    /**
     * 停止朗读
     */
    private void stopSpeaking() {
        if (isTTSInitialized && textToSpeech != null) {
            textToSpeech.stop();
            isSpeaking = false;
            
            // 更新UI，重置喇叭图标
            if (currentSpeakingView != null) {
                currentSpeakingView.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                currentSpeakingView.setAlpha(0.7f);
                currentSpeakingView = null;
            }
            
            currentSpeakingText = null;
        }
    }
    
    /**
     * 清理文本，去除Markdown语法和其他不适合朗读的内容
     */
    private String cleanTextForSpeech(String text) {
        if (text == null) return "";
        
        // 移除Markdown标题符号
        String cleaned = text.replaceAll("#+ ", "");
        
        // 移除Markdown链接，只保留链接文本
        cleaned = cleaned.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1");
        
        // 移除Markdown列表符号
        cleaned = cleaned.replaceAll("^[*-] ", "").replaceAll("\n[*-] ", "\n");
        
        // 移除Markdown粗体和斜体标记
        cleaned = cleaned.replaceAll("\\*\\*([^*]+)\\*\\*", "$1").replaceAll("\\*([^*]+)\\*", "$1");
        
        // 移除Markdown代码块
        cleaned = cleaned.replaceAll("```[^`]*```", "代码块已省略");
        
        // 移除Markdown行内代码
        cleaned = cleaned.replaceAll("`([^`]+)`", "$1");
        
        return cleaned;
    }
}
