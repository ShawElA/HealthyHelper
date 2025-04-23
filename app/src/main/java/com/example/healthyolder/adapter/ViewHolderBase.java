package com.example.healthyolder.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderBase {
	private SparseArray<View> mViews;
	@SuppressWarnings("unused")
	private int mPosition;
	private View mConvertView;
	private Context mContext;

	public View getConvertView() {
		return mConvertView;
	}

	public ViewHolderBase(Context context, ViewGroup parent, int layoutId, int position) {
		this.mContext = context;
		this.mViews = new SparseArray<View>();
		this.mPosition = position;
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		this.mConvertView.setTag(this);
	}

	public static ViewHolderBase get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		if (null == convertView) {
			return new ViewHolderBase(context, parent, layoutId, position);
		} else {
			ViewHolderBase holder = (ViewHolderBase) convertView.getTag();
			holder.mPosition = position;

			return holder;
		}
	}

	public int getPosition() {
		return mPosition;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (null == view) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public ViewHolderBase setText(int viewId, CharSequence text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	public void setTextViewBackGround(int viewId, int color) {
		TextView tv = getView(viewId);
		tv.setBackgroundColor(mContext.getResources().getColor(color));
	}

	public void setImageResource(int viewId, int resId) {
		ImageView iv = getView(viewId);
		iv.setImageResource(resId);
	}
	/**
	 * 关于事件的
	 */
	public ViewHolderBase setOnClickListener(int viewId,
										 View.OnClickListener listener) {
		View view = getView(viewId);
		view.setOnClickListener(listener);
		return this;
	}
}
