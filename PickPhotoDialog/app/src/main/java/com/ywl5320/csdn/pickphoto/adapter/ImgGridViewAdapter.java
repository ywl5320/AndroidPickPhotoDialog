
package com.ywl5320.csdn.pickphoto.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ywl5320.csdn.pickphoto.R;
import com.ywl5320.csdn.pickphoto.beans.ImgBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ywl
 *
 */
public class ImgGridViewAdapter extends BaseAdapter {
	protected Context context;
	protected LayoutInflater mlayoutInflate;
	protected List<ImgBean> mDatas = new ArrayList<ImgBean>();
	private boolean isaddpath = false;
	private String ppath = "";
	private OnImgSelectedListener onImgSelectedListener;

	public ImgGridViewAdapter(Context context, List<ImgBean> mDatas) {
		this.context = context;
		this.mDatas = mDatas;
		if(this.context!=null)
		mlayoutInflate = LayoutInflater.from(this.context);
	}

	public void setOnImgSelectedListener(OnImgSelectedListener onImgSelectedListener) {
		this.onImgSelectedListener = onImgSelectedListener;
	}

	public void setPpath(String ppath) {
		this.ppath = ppath;
	}

	public void setIsaddpath(boolean isaddpath) {
		this.isaddpath = isaddpath;
	}

	public boolean isaddpath() {
		return isaddpath;
	}

	public String getPpath() {
		return ppath;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		final ImgBean imgBean = mDatas.get(position);
		if(convertView == null)
		{
			convertView = mlayoutInflate.inflate(R.layout.item_img_adapter_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
			viewHolder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_selected);
			viewHolder.vGrayBg = convertView.findViewById(R.id.v_gray_bg);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if(!isaddpath) {
			Glide.with(context).load(imgBean.getPath()).into(viewHolder.ivImg);
		}
		else
		{
			Glide.with(context).load(ppath + "/" + imgBean.getPath()).into(viewHolder.ivImg);
		}

		if(imgBean.isSelected())
		{
			viewHolder.ivSelected.setSelected(true);
			viewHolder.vGrayBg.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.ivSelected.setSelected(false);
			viewHolder.vGrayBg.setVisibility(View.GONE);
		}

		viewHolder.ivSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(onImgSelectedListener != null)
				{
					onImgSelectedListener.onSelected(imgBean);
				}
			}
		});

		return convertView;
	}

	public class ViewHolder
	{
		public ImageView ivImg;
		public ImageView ivSelected;
		public View vGrayBg;

		public ViewHolder(){}
	}

	public interface OnImgSelectedListener
	{
		void onSelected(ImgBean imgBean);
	}

}
