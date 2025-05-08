package com.example.healthyolder.adapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.healthyolder.BaseApplication;
import com.example.healthyolder.activity.DoctorDetailActivity;
import com.example.healthyolder.bean.GoodsItem;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.fragment.AppointmentFragment;
import com.example.healthyolder.util.IntentUtil;

import java.text.NumberFormat;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class GoodsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ArrayList<GoodsItem> dataList;
    private AppointmentFragment mContext;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public GoodsAdapter(ArrayList<GoodsItem> dataList, AppointmentFragment mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(mContext.getContext());
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_header_view, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(dataList.get(position).typeName);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return dataList.get(position).typeId;
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_goods, parent, false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        GoodsItem item = dataList.get(position);
        holder.bindData(item);
        return convertView;
    }

    class ItemViewHolder implements View.OnClickListener {
        private LinearLayout ll_item;
        private ImageView image;
        private TextView name, tvSpecialty, tvBrief;
        private Button btnConsult;
        private GoodsItem item;

        public ItemViewHolder(View itemView) {
            ll_item = itemView.findViewById(R.id.ll_item);
            name = itemView.findViewById(R.id.tvName);
            image = itemView.findViewById(R.id.img);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvBrief = itemView.findViewById(R.id.tvBrief);
            btnConsult = itemView.findViewById(R.id.btnConsult);
            
            ll_item.setOnClickListener(this);
            btnConsult.setOnClickListener(this);
        }

        public void bindData(GoodsItem item) {
            this.item = item;
            if (item.img != null && item.img.toLowerCase().contains("uploads")) {
                Glide.with(mContext)
                    .load(Urls.baseUrl + item.img)
                    .placeholder(mContext.getResources().getDrawable(R.mipmap.nav_logo))
                    .circleCrop()
                    .into(image);
            } else {
                Glide.with(mContext)
                    .load(item.img)
                    .placeholder(mContext.getResources().getDrawable(R.mipmap.nav_logo))
                    .circleCrop()
                    .into(image);
            }
            
            name.setText(item.name);
            tvSpecialty.setText(item.typeName); // 使用科室名称
            
            // 设置医生简介
            String intro = item.intro;
            if (!TextUtils.isEmpty(intro)) {
                tvBrief.setText(intro);
            } else {
                tvBrief.setText(mContext.getString(R.string.doctor_default_intro, item.name, item.typeName));
            }
            
            item.count = mContext.getSelectedItemCountById(item.id);
        }

        @Override
        public void onClick(View v) {
            openDoctorDetail();
        }
        
        private void openDoctorDetail() {
            Bundle bundle = new Bundle();
            bundle.putString("c_id", item.id + "");
            bundle.putString("title", item.name);
            bundle.putString("dname", item.typeName);
            bundle.putString("intro", item.intro);
            bundle.putString("pic", item.img);
            BaseApplication.setDoctorId(item.id + "");
            BaseApplication.setDoctorName(item.name);
            BaseApplication.setDepartmentName(item.typeName);
            IntentUtil.startActivity(mContext.getActivity(), DoctorDetailActivity.class, bundle);
        }
    }
}
