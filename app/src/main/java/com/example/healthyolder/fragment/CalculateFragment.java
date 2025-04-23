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
import com.example.healthyolder.bean.EmptyResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.LogUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.OkHttpChatUtil;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.example.healthyolder.view.CommonToolBar;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
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
    private List<ChatResult.DataBean> arrayList = new ArrayList<>();
    private CommonAdapter<ChatResult.DataBean> adapter;
    private String gid = "1";
    private String gName = "与智能医生聊天";

    public static CalculateFragment newInstance(String param1, String param2) {
        CalculateFragment fragment = new CalculateFragment();
        Bundle args = new Bundle();
     
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.btn_send)
    public void send(){
        if (TextUtil.isValidate(et_comment.getText().toString())){
            submitMsg(BaseApplication.getUserId(), et_comment.getText().toString());
            getGPTResponse(et_comment.getText().toString());
        }else {
            ToastUtil.showBottomToast("请输入您的问题");
        }
        et_comment.setText("");
    }

    private void getGPTResponse(String question){
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("model", Configs.MODEL);
        parameter.put("prompt", question);
        parameter.put("max_tokens", Configs.MAX_TOKENS);
        parameter.put("temperature",0);
        OkHttpChatUtil.post(Urls.GPTURL, parameter, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string().trim();
                LogUtil.i("response", response.code() + "  " + content);

                ChatGptResult result = new Gson().fromJson(content, ChatGptResult.class);
                if (result != null){
                    if (result.getChoices().get(0) != null && result.getChoices().size() != 0){
                        LogUtil.i("reply", result.getChoices().get(0).getText().replace("\n", "").trim());
                        submitMsg("-1", result.getChoices().get(0).getText().replace("\n", "").trim());
                    }
                }
            }
        });
    }

    private void submitMsg(String uid, String content){
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
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

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
        HttpUtil.getResponse(Urls.SELECTCHAT, null, null, new ObjectCallBack<ChatResult>(ChatResult.class) {
            @Override
            public void onSuccess(ChatResult response) {
                srl_refresh.setRefreshing(false);
                if (response.isSuccess()){
                    disposeCommentData(response);
                    initRecyclerView();
                }else {
                    setLayoutVisible(View.VISIBLE, View.GONE);
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

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
        arrayList.addAll(result.getData());
    }

    private void initRecyclerView(){
        if (adapter == null){
            rv_comment.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_comment.smoothScrollToPosition(arrayList.size() - 1);
            adapter = new CommonAdapter<ChatResult.DataBean>(getContext(), R.layout.item_chat_message, arrayList) {
                @Override
                public void convert(ViewHolder holder, ChatResult.DataBean o) {
                    ImageView iv_other = holder.getView(R.id.iv_other);
                    ImageView iv_me = holder.getView(R.id.iv_me);
                    TextView tv_other = holder.getView(R.id.tv_other);
                    TextView tv_me = holder.getView(R.id.tv_me);
                    LinearLayout ll_other = holder.getView(R.id.ll_other);
                    LinearLayout ll_me = holder.getView(R.id.ll_me);
                    if (o.getC_uid().equals(BaseApplication.getUserId())){
                        ll_me.setVisibility(View.VISIBLE);
                        ll_other.setVisibility(View.GONE);
                        tv_me.setText(o.getC_remark());
                    }else {
                        ll_other.setVisibility(View.VISIBLE);
                        ll_me.setVisibility(View.GONE);
                        tv_other.setText(o.getC_remark());
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
