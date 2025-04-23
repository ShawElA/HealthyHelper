package com.example.healthyolder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.healthyolder.R;
import com.example.healthyolder.bean.Configs;

import java.util.ArrayList;

/**
 * @author haizhuo
 */
public class BitmapAdapter extends BaseAdapter {

    private ArrayList<String> urlList;
    private LayoutInflater inflater;
    private Context mContext;

    public BitmapAdapter(ArrayList<String> urlList, Context mContext) {
        this.urlList = urlList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public Object getItem(int i) {
        return urlList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {


        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_grid_image, viewGroup, false);
            holder.iv = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String path = urlList.get(position);
        if (path.equals(Configs.PLUS)) {
            holder.iv.setImageResource(R.mipmap.plus);
        } else {
            Glide.with(mContext)
                    .load(path)
                    .placeholder(R.mipmap.image_thub)
                    .error(R.mipmap.image_thub)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.iv);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
    }
}
