package com.example.healthyolder.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.DicTypeResult;
import com.example.healthyolder.util.IntentUtil;
import com.example.healthyolder.view.CommonToolBar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CommonActivity extends BaseActivity {

    @BindView(R.id.rv_list)
    RecyclerView rv_list;
    @BindView(R.id.rl_commonToolBar)
    CommonToolBar rl_commonToolBar;
    CommonAdapter<DicTypeResult.DataBean> adapter;
    List<DicTypeResult.DataBean> arrayList = new ArrayList<>();
    private String title = "抑郁课堂";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        ButterKnife.bind(this);
        rl_commonToolBar.setMiddleTitleText(title);
        if (BaseApplication.dicType != null && BaseApplication.dicType.size() != 0){
            for (DicTypeResult.DataBean dataBean: BaseApplication.dicType){
                if (Integer.valueOf(dataBean.getD_id()) < 7){
                    arrayList.add(dataBean);
                }
            }
        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        if (adapter == null){
            rv_list.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CommonAdapter<DicTypeResult.DataBean>(this, R.layout.item_list, arrayList) {
                @Override
                public void convert(ViewHolder holder, DicTypeResult.DataBean dataBean) {
                    TextView tv_name = holder.getView(R.id.tv_name);
                    tv_name.setText(dataBean.getD_name());
                    holder.getView(R.id.rl_edit_pwd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dataBean.getD_id().equals("7")){
                                Bundle b = new Bundle();
                                b.putString("mType", dataBean.getD_id());
                                IntentUtil.startActivity(CommonActivity.this, HealthyTestActivity.class, b);
                            }else {
                                Bundle b = new Bundle();
                                b.putString("mType", dataBean.getD_id());
                                IntentUtil.startActivity(CommonActivity.this, AidGuideActivity.class, b);
                            }

                        }
                    });
                }
            };
            rv_list.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }
}
