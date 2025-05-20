package com.example.healthyolder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.R;
import com.example.healthyolder.util.TextUtil;

import java.util.List;

public class QuestionPreviewAdapter extends RecyclerView.Adapter<QuestionPreviewAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> mQuestionIndices;
    private int mCurrentPosition = 0;
    private OnQuestionClickListener mListener;

    public QuestionPreviewAdapter(Context context, List<Integer> questionIndices) {
        this.mContext = context;
        this.mQuestionIndices = questionIndices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question_indicator, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int questionIndex = mQuestionIndices.get(position);
        holder.tvQuestionNumber.setText(String.valueOf(questionIndex + 1));

        // 检查当前题目是否被回答
        boolean isAnswered = TextUtil.isValidate(BaseApplication.GoalMap.get(questionIndex));
        
        // 设置不同状态的显示效果
        if (position == mCurrentPosition) {
            // 当前题目，高亮显示
            holder.tvQuestionNumber.setSelected(true);
            holder.tvQuestionNumber.setTextColor(mContext.getResources().getColor(R.color.white));
        } else if (isAnswered) {
            // 已回答题目，设置为已完成状态
            holder.tvQuestionNumber.setSelected(false);
            holder.tvQuestionNumber.setActivated(true);
            holder.tvQuestionNumber.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            // 未回答题目，设置为默认状态
            holder.tvQuestionNumber.setSelected(false);
            holder.tvQuestionNumber.setActivated(false);
            holder.tvQuestionNumber.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        }

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onQuestionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestionIndices != null ? mQuestionIndices.size() : 0;
    }

    public void setCurrentPosition(int position) {
        int oldPosition = mCurrentPosition;
        mCurrentPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(mCurrentPosition);
    }

    public void setOnQuestionClickListener(OnQuestionClickListener listener) {
        this.mListener = listener;
    }

    public void refreshAll() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tv_question_number);
        }
    }

    public interface OnQuestionClickListener {
        void onQuestionClick(int position);
    }
} 