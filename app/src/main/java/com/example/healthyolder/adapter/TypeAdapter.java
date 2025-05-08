package com.example.healthyolder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.healthyolder.R;
import com.example.healthyolder.bean.GoodsItem;
import com.example.healthyolder.fragment.AppointmentFragment;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
    public int selectTypeId;
    public AppointmentFragment activity;
    public ArrayList<GoodsItem> dataList;

    public TypeAdapter(AppointmentFragment activity, ArrayList<GoodsItem> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        if (dataList != null && dataList.size() > 0) {
            selectTypeId = dataList.get(0).typeId;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GoodsItem item = dataList.get(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCount, type;
        View selectedIndicator;
        private GoodsItem item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCount = itemView.findViewById(R.id.tvCount);
            type = itemView.findViewById(R.id.type);
            selectedIndicator = itemView.findViewById(R.id.selectedIndicator);
            itemView.setOnClickListener(this);
        }

        public void bindData(GoodsItem item) {
            this.item = item;
            type.setText(item.typeName);
            
            int count = activity.getSelectedGroupCountByTypeId(item.typeId);
            tvCount.setText(String.valueOf(count));
            
            if (count < 1) {
                tvCount.setVisibility(View.GONE);
            } else {
                tvCount.setVisibility(View.VISIBLE);
            }
            
            // Handle selected state
            if (item.typeId == selectTypeId) {
                type.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                type.setTextSize(16);
                type.setTypeface(null, android.graphics.Typeface.BOLD);
                selectedIndicator.setVisibility(View.VISIBLE);
                itemView.setBackgroundResource(R.color.white);
            } else {
                type.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                type.setTextSize(14);
                type.setTypeface(null, android.graphics.Typeface.NORMAL);
                selectedIndicator.setVisibility(View.INVISIBLE);
                itemView.setBackgroundResource(android.R.color.transparent);
            }
        }

        @Override
        public void onClick(View v) {
            if (selectTypeId != item.typeId) {
                selectTypeId = item.typeId;
                notifyDataSetChanged();
                activity.onTypeClicked(item.typeId);
            }
        }
    }
}
