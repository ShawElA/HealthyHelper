package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.bean.MethodResult;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonItemFragment extends Fragment {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_remark)
    TextView tv_remark;
    View view;
    MethodResult.DataBean result;

    public static CommonItemFragment newInstance(MethodResult.DataBean result) {
        CommonItemFragment fragment = new CommonItemFragment();
        fragment.result = result;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.frag_common_item, container, false);
        }
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData(){
        if (result != null){
            tv_title.setText(result.getM_title());
            tv_remark.setText(result.getM_content());
        }
    }
}
