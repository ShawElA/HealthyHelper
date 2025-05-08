package com.example.healthyolder.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.adapter.CommonBaseAdapter;
import com.example.healthyolder.adapter.ViewHolderBase;
import com.example.healthyolder.util.TextUtil;
import com.example.healthyolder.view.TextViewPlus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorBriefFrag extends Fragment {
    @BindView(R.id.lv_brief_courseBriefFrag)
    ListView lv_brief;
    private Activity activity;
    private View view;
    private View saveView;
    private List<String> courseBriefInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (saveView != null) {
            view = saveView;
            ButterKnife.bind(this, view);
        } else {
            if (view == null) {
                view = inflater.inflate(R.layout.frag_course_brief, null);
                ButterKnife.bind(this, view);
                initData();
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    protected void initData() {
        Bundle data = getArguments();
        String courseBrief = data.getString("CourseBrief", "");
        String teachObjective = data.getString("TeachObjective", "");
//        if (!TextUtil.isValidate(courseBrief.replaceAll("\\s*", "")) &&
//                !TextUtil.isValidate(courseFeature.replaceAll("\\s*", "")) &&
//                !TextUtil.isValidate(courseArrange.replaceAll("\\s*", "")) &&
//                !TextUtil.isValidate(teachObjective.replaceAll("\\s*", "")) &&
//                !TextUtil.isValidate(applyObject.replaceAll("\\s*", ""))) {
//            View emptyView = CreateViewUtil.createEmptyView();
//            PercentLinearLayout pll = (PercentLinearLayout) emptyView.findViewById(R.id.pll_container_noDataLayout);
//            pll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(ScreenUtil.getScreenHeight()*0.52f)));
//            ((ViewGroup) lv_brief.getParent()).addView(emptyView);
//            lv_brief.setEmptyView(emptyView);
//            return;
//        }
        courseBriefInfo = new ArrayList<>();
        if (TextUtil.isValidate(courseBrief)) {
            courseBriefInfo.add("COURSE_BRIEF" + courseBrief.replace("\\n", "\n"));
        }
        if (TextUtil.isValidate(teachObjective)) {
            courseBriefInfo.add("TEACH_OBJECTIVE" + teachObjective.replace("\\n", "\n"));
        }
        if (courseBriefInfo.size() == 1) {
            lv_brief.setDivider(new ColorDrawable(activity.getResources().getColor(R.color.transparent)));
        }

        lv_brief.setAdapter(new CommonBaseAdapter<String>(activity, R.layout.item_lv_course_brief, courseBriefInfo) {

            @Override
            public void convert(ViewHolderBase holder, String s) {
                TextViewPlus tvp_title = holder.getView(R.id.tvp_title_courseBriefItemLv);
                TextView tv_content = holder.getView(R.id.tv_content_courseBriefItemLv);
                if (s.contains("COURSE_BRIEF")) {
                    tvp_title.setCompoundImg(TextViewPlus.LEFT_IMG, R.mipmap.course_brief, 0.025f, 0.025f, "ScreenHeight");
                    tvp_title.setText("医师简介");
                    tv_content.setText(s.replace("COURSE_BRIEF", ""));
                } else if (s.contains("TEACH_OBJECTIVE")) {
                    tvp_title.setCompoundImg(TextViewPlus.LEFT_IMG, R.mipmap.teach_objective, 0.025f, 0.025f, "ScreenHeight");
                    tvp_title.setText("温馨提示");
                    tv_content.setText(s.replace("TEACH_OBJECTIVE", ""));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            saveView = view;
        }
    }
}
