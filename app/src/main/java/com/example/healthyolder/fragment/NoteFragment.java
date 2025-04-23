package com.example.healthyolder.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.healthyolder.R;
import com.example.healthyolder.activity.WordActivity;
import com.example.healthyolder.activity.WordPicActivity;
import com.example.healthyolder.activity.WordVideoActivity;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.NoteResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.util.ToastUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class NoteFragment extends Fragment{

    View view;
    @BindView(R.id.rv_list)
    RecyclerView rv_list;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;
    List<NoteResult.DataBean> arrayList = new ArrayList<>();
    CommonAdapter<NoteResult.DataBean> adapter;

    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_all, null);
        ButterKnife.bind(this, view);
        initData();
        initSwipeLayout();
        return view;
    }

    private void initData(){
        HttpUtil.getResponse(Urls.GETALLNOTES, null, this, new ObjectCallBack<NoteResult>(NoteResult.class) {
            @Override
            public void onSuccess(NoteResult response) {
                if (response.isSuccess() && response.getData().size() != 0){
                    disposeData(response.getData());
                    initRecyclerView();
                }
            }

            @Override
            public void onFail(Call call, Exception e) {

            }
        });
    }

    private void disposeData(List<NoteResult.DataBean> result){
        arrayList.clear();
        for (int i = 0; i < result.size(); i++){
            if (i == 0){
                arrayList.add(result.get(i));
            }else {
                if (!result.get(i - 1).getN_id().equals(result.get(i).getN_id())){
                    arrayList.add(result.get(i));
                }
            }
        }
    }

    //初始化刷新控件
    private void initSwipeLayout(){
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_layout.setRefreshing(true);
                initData();
                refresh_layout.setRefreshing(false);
                ToastUtil.showBottomToast("刷新成功");
            }
        });
    }

    //初始化适配器
    private void initRecyclerView(){
        if (adapter == null){
            // 设置瀑布流适配器
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            // 绑定布局管理器
            rv_list.setLayoutManager(layoutManager);
            adapter = new CommonAdapter<NoteResult.DataBean>(getContext(), R.layout.item_main, arrayList) {
                @Override
                public void convert(ViewHolder holder, NoteResult.DataBean noteResult) {
                    ImageView iv_play = holder.getView(R.id.iv_play);
                    ImageView iv_icon = holder.getView(R.id.iv_icon);
                    ImageView iv_author = holder.getView(R.id.iv_author);
                    TextView tv_title = holder.getView(R.id.tv_title);
                    TextView tv_name = holder.getView(R.id.iv_name);
                    tv_title.setText(noteResult.getN_title());
                    tv_name.setText(noteResult.getU_name());
                    if (TextUtil.isValidate(noteResult.getU_icon())){
                        if (noteResult.getN_type().equals(Configs.NOTE_WORD + "")){
                            iv_icon.setVisibility(View.GONE);
                        }else if (noteResult.getN_type().equals(Configs.NOTE_VIDEO + "")){
                            iv_play.setVisibility(View.VISIBLE);
                        }
                    }

                    Glide.with(mContext).load(Urls.baseUrl + noteResult.getA_frame()).into(iv_icon);
                    holder.setIsRecyclable(false);
                    if (TextUtil.isValidate(noteResult.getU_icon())){
                        if (noteResult.getU_icon().contains("uploads")){
                            Glide.with(mContext).load(Urls.baseUrl + noteResult.getU_icon()).into(iv_author);
                        }else {
                            Glide.with(mContext).load(noteResult.getU_icon()).into(iv_author);
                        }
                    }

                    holder.getView(R.id.cv_item).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle b = new Bundle();
                            b.putString("nid", noteResult.getN_id());
                            switch (noteResult.getN_type()){
                                case Configs.NOTE_WORD + "":
                                    IntentUtil.startActivity(getActivity(), WordActivity.class, b);
                                    break;
                                case Configs.NOTE_PIC + "":
                                    IntentUtil.startActivity(getActivity(), WordPicActivity.class, b);
                                    break;
                                case Configs.NOTE_VIDEO + "":
                                    IntentUtil.startActivity(getActivity(), WordVideoActivity.class, b);
                                    break;
                            }
                        }
                    });

                }
            };
            //加载适配器
            rv_list.setAdapter(adapter);
        }else {
            //当适配器存在时，进行数据更新即可，避免重复初始化
            adapter.notifyDataSetChanged();
        }
    }

}
