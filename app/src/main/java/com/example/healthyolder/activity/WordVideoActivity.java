package com.example.healthyolder.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.CommentResult;
import com.example.healthyolder.bean.CommonResult;
import com.example.healthyolder.bean.FavResult;
import com.example.healthyolder.bean.NoteResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;

public class WordVideoActivity extends AppCompatActivity {

    ActionBar actionBar;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.rv_comment)
    RecyclerView rv_comment;
    @BindView(R.id.tv_comment)
    TextView tv_comment;
    @BindView(R.id.iv_fav)
    ImageView iv_fav;
    @BindView(R.id.rl_comment)
    RelativeLayout rl_comment;
    @BindView(R.id.vv_video)
    JZVideoPlayerStandard vv_video;
    @BindView(R.id.tv_date)
    TextView tv_date;
    private String videoPath = "";
    private List<CommentResult.DataBean> arrayList = new ArrayList<>();
    private CommonAdapter<CommentResult.DataBean> adapter;
    boolean isFav = false;              //是否点赞，默认不点赞
    private BottomSheetDialog dialog;   //评论输入弹出框
    private Bundle bundle;
    private String nid = "";        //文章id
    private String uid = "";        //作者id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_video);
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        bundle = getIntent().getBundleExtra("Bundle");
        nid = bundle.getString("nid");
        initData();
        //检查是否点过赞
        checkContentFav();
        //获取文章评论
        initComments();
    }

    private void initVideo(){
        vv_video.setUp(videoPath,
                JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN,
                "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:   //返回键的id
                this.finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.tv_comment)
    public void useComment(){
        showCommentDialog();
    }

    @OnClick(R.id.iv_fav)
    public void clickFav(){
        //提交是否点赞操作
        submitFav();
    }

    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    if (commentContent.length() > 50){
                        ToastUtil.showBottomToast("评价字数不可超过50字");
                    }else {
                        dialog.dismiss();
                        submitComment(commentContent);
                    }
                }else {
                    ToastUtil.showBottomToast("评价不可为空");
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void initData(){
        Map<String, String> params = new HashMap<>();
        params.put("nid", nid);
        HttpUtil.getResponse(Urls.NOTEDETAIL, params, null, new ObjectCallBack<NoteResult>(NoteResult.class) {
            @Override
            public void onSuccess(NoteResult response) {
                if (response.isSuccess() && response.getData().size() != 0){
                    disposeData(response);
                }else {
                    ToastUtil.showBottomToast("获取不到数据");
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    //处理文章内容数据
    private void disposeData(NoteResult result){
        uid = result.getData().get(0).getN_uid();
        videoPath = Urls.baseUrl + result.getData().get(0).getA_path();
        tv_title.setText(result.getData().get(0).getN_title());
        tv_content.setText(result.getData().get(0).getN_content());
        tv_date.setText("编辑于" + result.getData().get(0).getN_time());
        initVideo();
    }

    //处理评论数据
    private void disposeCommentData(CommentResult result){
        arrayList.clear();
        for (CommentResult.DataBean bean: result.getData()){
            arrayList.add(bean);
        }
    }

    private void initRecyclerView(){
        if (adapter == null){
            rv_comment.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommonAdapter<CommentResult.DataBean>(this, R.layout.item_rv_message, arrayList) {
                @Override
                public void convert(ViewHolder holder, CommentResult.DataBean o) {
                    TextView detail_page_userName = holder.getView(R.id.detail_page_userName);
                    TextView detail_page_time = holder.getView(R.id.detail_page_time);
                    TextView detail_page_story = holder.getView(R.id.detail_page_story);
                    ImageView detail_page_userLogo = holder.getView(R.id.detail_page_userLogo);
                    detail_page_userName.setText(o.getUsername());
                    detail_page_story.setText(o.getC_content());
                    detail_page_time.setText(o.getC_date());
                    String iconUrl = o.getIcon();
                    com.example.healthyolder.util.LogUtil.i("评论头像", "原始URL: " + (iconUrl == null ? "null" : iconUrl));
                    if (iconUrl == null || iconUrl.isEmpty()) {
                        detail_page_userLogo.setImageResource(R.drawable.default_head);
                        com.example.healthyolder.util.LogUtil.i("评论头像", "使用默认头像 - 直接设置图像资源");
                    } else if (iconUrl.contains("uploads")) {
                        String fullUrl = Urls.baseUrl + iconUrl;
                        com.example.healthyolder.util.LogUtil.i("评论头像", "完整URL: " + fullUrl);
                        Glide.with(mContext)
                            .load(fullUrl)
                            .placeholder(R.drawable.default_head)
                            .error(R.drawable.default_head)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(detail_page_userLogo);
                    } else {
                        com.example.healthyolder.util.LogUtil.i("评论头像", "使用原URL: " + iconUrl);
                        Glide.with(mContext)
                            .load(iconUrl)
                            .placeholder(R.drawable.default_head)
                            .error(R.drawable.default_head)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(detail_page_userLogo);
                    }
                }

            };
            rv_comment.setAdapter(adapter);
        } else{
            adapter.notifyDataSetChanged();
        }
    }

    //提交是否点赞操作
    private void submitFav(){
        if (isFav){
            isFav = false;
        }else {
            isFav = true;
        }
        Map<String, String> params = new HashMap<>();
        params.put("fn_id", nid);
        params.put("fu_id", BaseApplication.getUserId());
        params.put("fav", isFav + "");
        HttpUtil.getResponse(Urls.CLICKFORLIKE, params, null, new ObjectCallBack<CommonResult>(CommonResult.class) {
            @Override
            public void onSuccess(CommonResult response) {
                if (response.isSuccess()){
                    if (isFav){
                        iv_fav.setBackground(getResources().getDrawable(R.mipmap.fav_like));
                        ToastUtil.showBottomToast("点赞成功");
                    }else {
                        iv_fav.setBackground(getResources().getDrawable(R.mipmap.nav_icon_fav));
                        ToastUtil.showBottomToast("取消点赞成功");
                    }
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    //检查是否点过赞
    private void checkContentFav(){
        Map<String, String> params = new HashMap<>();
        params.put("fn_id", nid);
        params.put("fu_id", BaseApplication.getUserId());
        HttpUtil.getResponse(Urls.CHECKIFFAVNEW, params, null, new ObjectCallBack<FavResult>(FavResult.class) {
            @Override
            public void onSuccess(FavResult response) {
                if (response.isSuccess()){
                    //有记录证明有过点赞
                    iv_fav.setBackground(getResources().getDrawable(R.mipmap.fav_like));
                    isFav = true;
                }else {
                    iv_fav.setBackground(getResources().getDrawable(R.mipmap.nav_icon_fav));
                    isFav = false;
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    //提交评价，并及时刷新
    private void submitComment(String comment){
        HashMap<String, String> params = new HashMap<>();
        params.put("c_content", comment);
        params.put("cn_id", nid);
        params.put("cu_id", BaseApplication.getUserId());
        HttpUtil.getResponse(Urls.COMMENTNOTENEW, params, null, new ObjectCallBack<CommonResult>(CommonResult.class) {
            @Override
            public void onSuccess(CommonResult response) {
                if (response.isSuccess()){
                    ToastUtil.showBottomToast("评论成功");
                    initComments();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    //获取文章评论，也可用于刷新
    private void initComments(){
        HashMap<String, String> params = new HashMap<>();
        params.put("cn_id", nid);
        HttpUtil.getResponse(Urls.GETNOTECOMMENTSNEW, params, null, new ObjectCallBack<CommentResult>(CommentResult.class) {
            @Override
            public void onSuccess(CommentResult response) {
                if (response.isSuccess() && response.getData().size() != 0){
                    disposeCommentData(response);
                    initRecyclerView();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
