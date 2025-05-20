package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.activity.HealthyTestActivity;
import com.example.healthyolder.adapter.CommonAdapter;
import com.example.healthyolder.adapter.ViewHolder;
import com.example.healthyolder.bean.MethodResult;
import com.example.healthyolder.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TestItemFragment extends Fragment {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.rv_list)
    RecyclerView rv_list;
    View view;
    MethodResult.DataBean result;
    CommonAdapter<String> adapter;
    List<String> arrayList = new ArrayList<>();
    private int mIndex = 0;

    public static TestItemFragment newInstance(int index, MethodResult.DataBean result) {
        TestItemFragment fragment = new TestItemFragment();
        fragment.result = result;
        fragment.mIndex = index;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null){
            view = inflater.inflate(R.layout.frag_test_item, container, false);
        }
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData(){
        if (result != null){
            arrayList.clear();
            tv_title.setText(result.getM_title());
            if (result.getM_content().contains("/")){
                String[] items = result.getM_content().split("/");
                for (String item: items){
                    arrayList.add(item);
                }
            }
            initRecyclerView();
        }
    }

    private void initRecyclerView(){
        if (adapter == null){
            rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CommonAdapter<String>(getContext(), R.layout.item_choose, arrayList) {
                @Override
                public void convert(ViewHolder holder, String s) {
                    TextView tv_name = (TextView)holder.getView(R.id.tv_name);
                    ImageView iv_image = (ImageView) holder.getView(R.id.iv_image);
                    switch (holder.getAbsoluteAdapterPosition()){
                        case 0:
                            tv_name.setText("A." + s);
                            break;
                        case 1:
                            tv_name.setText("B." + s);
                            break;
                        case 2:
                            tv_name.setText("C." + s);
                            break;
                        case 3:
                            tv_name.setText("D." + s);
                            break;
                    }
                    iv_image.setBackground(getResources().getDrawable(R.mipmap.right));
                    refreshLayout(holder.getAbsoluteAdapterPosition(), iv_image);
                    holder.getView(R.id.rl_edit_pwd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 检查当前选项是否已经被选中
                            boolean isAlreadySelected = false;
                            if (TextUtil.isValidate(BaseApplication.GoalMap.get(mIndex))) {
                                int score = Integer.valueOf(BaseApplication.GoalMap.get(mIndex));
                                int selectedPosition = 4 - score;
                                if (selectedPosition == holder.getAbsoluteAdapterPosition()) {
                                    isAlreadySelected = true;
                                }
                            }
                            
                            //A、B、C、D选项分数递减
                            BaseApplication.GoalMap.put(mIndex, (4 - holder.getAbsoluteAdapterPosition()) + "");
                            adapter.notifyDataSetChanged();
                            
                            // 通知Activity更新题目预览状态
                            if (getActivity() instanceof HealthyTestActivity) {
                                ((HealthyTestActivity) getActivity()).updateQuestionPreview();
                            }
                            
                            // 如果该选项已经被选中，则自动进入下一题
                            if (isAlreadySelected && getActivity() instanceof HealthyTestActivity) {
                                HealthyTestActivity activity = (HealthyTestActivity) getActivity();
                                activity.goToNextQuestion();
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

    private void refreshLayout(int position, ImageView imageView){
        if (TextUtil.isValidate(BaseApplication.GoalMap.get(mIndex))){
            int score = Integer.valueOf(BaseApplication.GoalMap.get(mIndex));
            int selectedPosition = 4 - score;
            if (selectedPosition == position){
                imageView.setVisibility(View.VISIBLE);
                return;
            }
        }
        imageView.setVisibility(View.INVISIBLE);
    }
}
