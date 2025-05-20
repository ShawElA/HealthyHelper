package com.example.healthyolder.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * RecyclerView通用适配器
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    private int layoutId;
    private List<T> data;
    private OnItemClickListener onItemClickListener;

    public BaseAdapter(int layoutId, List<T> data) {
        this.layoutId = layoutId;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        convert(holder, data.get(position));
        
        // 设置点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> 
                onItemClickListener.onItemClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 设置新数据
     */
    public void setNewData(List<T> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<T> moreData) {
        if (this.data != null && moreData != null) {
            this.data.addAll(moreData);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 设置Item点击事件
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * 子类实现此方法来绑定数据
     */
    protected abstract void convert(ViewHolder holder, T item);

    /**
     * ViewHolder基类
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> viewCache;
        private View convertView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.convertView = itemView;
            viewCache = new SparseArray<>();
        }

        /**
         * 获取ItemView
         */
        public View getConvertView() {
            return convertView;
        }

        /**
         * 获取控件
         */
        public <T extends View> T getView(int viewId) {
            View view = viewCache.get(viewId);
            if (view == null) {
                view = convertView.findViewById(viewId);
                viewCache.put(viewId, view);
            }
            return (T) view;
        }

        /**
         * 设置文本
         */
        public ViewHolder setText(int viewId, String text) {
            TextView textView = getView(viewId);
            textView.setText(text);
            return this;
        }

        /**
         * 设置图片资源
         */
        public ViewHolder setImageResource(int viewId, int resourceId) {
            ImageView imageView = getView(viewId);
            imageView.setImageResource(resourceId);
            return this;
        }

        /**
         * 加载网络图片
         */
        public ViewHolder setImageUrl(int viewId, String url) {
            ImageView imageView = getView(viewId);
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
            return this;
        }

        /**
         * 设置点击事件
         */
        public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            getView(viewId).setOnClickListener(listener);
            return this;
        }

        /**
         * 设置可见性
         */
        public ViewHolder setVisibility(int viewId, int visibility) {
            getView(viewId).setVisibility(visibility);
            return this;
        }

        /**
         * 获取上下文
         */
        public Context getContext() {
            return convertView.getContext();
        }
    }

    /**
     * Item点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
} 