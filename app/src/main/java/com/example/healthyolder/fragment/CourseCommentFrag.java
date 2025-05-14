package com.example.healthyolder.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.activity.PayActivity;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.AvailableInfo;
import com.example.healthyolder.bean.AvailableResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.DateUtil;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class CourseCommentFrag extends Fragment {
    @BindView(R.id.rv_comment_courseCommentFrag)
    RecyclerView rv_comment;
    @BindView(R.id.iv_no_data)
    ImageView iv_no_data;
    private View view;
    private View saveView;
    private Activity activity;
    private String doctorId;
    private List<AvailableResult> remarkInfos = new ArrayList<>();
    CommonAdapter<AvailableResult> adapter;
    int year = Calendar.getInstance().get(Calendar.YEAR);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (saveView != null) {
            view = saveView;
            ButterKnife.bind(this,view);
        } else {
            if (view == null) {
                view = inflater.inflate(R.layout.frag_course_comment, null);
                ButterKnife.bind(this,view);
                Bundle data = getArguments();
                doctorId = data.getString("CourseId");
                rv_comment.setLayoutManager(new LinearLayoutManager(getContext()));
//                rv_comment.addItemDecoration(new RecyclerViewLinearLayoutDivider(RecyclerViewLinearLayoutDivider.VERTICAL_LIST, getActivity().getResources().getDrawable(R.drawable.sp_divider_horizontal_rv)));
                getCourseCommentInfo();
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            saveView = view;
        }
    }

    public void getCourseCommentInfo() {
        // 当前采用模拟数据方式
        for (int j = 0; j < 7; j++){
            AvailableResult result = new AvailableResult();
            result.setDate(DateUtil.beforeAfterDate(j));
            result.setAvaiable(true);
            result.setNumber("10");
            remarkInfos.add(result);
        }
        initRecyclerView();
        
        // 获取真实的医生可预约时间
        /*
        if (remarkInfos != null) {
            remarkInfos.clear();
        }
        
        Map<String, String> params = new HashMap<>();
        params.put("doctor_id", doctorId);
        
        HttpUtil.getResponse(Urls.GET_AVAILABLE_TIMES, params, this, new ObjectCallBack<AvailableInfo>(AvailableInfo.class) {
            @Override
            public void onSuccess(AvailableInfo response) {
                if (response != null && response.isSuccess()){
                    List<AvailableInfo.DataBean> availableDataList = response.getData();
                    if (availableDataList != null && !availableDataList.isEmpty()) {
                        // 将API返回的AvailableInfo.DataBean对象转换为界面需要的AvailableResult对象
                        for (AvailableInfo.DataBean dataBean : availableDataList) {
                            AvailableResult result = new AvailableResult();
                            result.setId(dataBean.getA_id());
                            result.setDate(dataBean.getA_date());
                            result.setNumber(dataBean.getA_num());
                            result.setAvaiable(!dataBean.getA_num().equals("0")); // 如果余号为0则不可预约
                            remarkInfos.add(result);
                        }
                        iv_no_data.setVisibility(View.GONE);
                    } else {
                        iv_no_data.setVisibility(View.VISIBLE);
                    }
                    initRecyclerView();
                } else {
                    ToastUtil.showBottomToast("获取预约时间失败");
                    iv_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
                iv_no_data.setVisibility(View.VISIBLE);
            }
        });
        */
    }

    public void initRecyclerView(){
        if (adapter == null){

            adapter = new CommonAdapter<AvailableResult>(getContext(), R.layout.item_catalog, remarkInfos) {
                @Override
                public void convert(ViewHolder holder, final AvailableResult o) {
                    TextView tv_catalog = holder.getView(R.id.tv_catalog_name);
                    Button btn = holder.getView(R.id.btn_status);
                    tv_catalog.setText(o.getDate());
                    if (o.isAvaiable()){
                        btn.setVisibility(View.VISIBLE);
                        if (o.getNumber().equals("0")){
                            btn.setSelected(false);
                            btn.setText("已满");
                        }else {
                            btn.setEnabled(true);
                            btn.setSelected(true);
                            btn.setText("余号(" + o.getNumber() + ")个");
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("date", o.getDate());
                                    bundle.putString("aid", o.getId());
                                    IntentUtil.startActivity(getActivity(), PayActivity.class, bundle);
                                }
                            });
                        }

                    }
                }
            };
            rv_comment.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

}
