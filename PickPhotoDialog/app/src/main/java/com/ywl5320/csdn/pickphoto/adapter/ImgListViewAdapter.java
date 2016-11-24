
package com.ywl5320.csdn.pickphoto.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ywl5320.csdn.pickphoto.R;
import com.ywl5320.csdn.pickphoto.beans.ImgFloderBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ywl
 *
 */
public class ImgListViewAdapter extends BaseAdapter {
	protected Context context;
	protected LayoutInflater mlayoutInflate;
	protected List<ImgFloderBean> mDatas = new ArrayList<ImgFloderBean>();

	public ImgListViewAdapter(Context context, List<ImgFloderBean> mDatas) {
		this.context = context;
		this.mDatas = mDatas;
		if(this.context!=null)
		mlayoutInflate = LayoutInflater.from(this.context);
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
		ImgFloderBean imgFloderBean = mDatas.get(position);
		if(convertView == null)
		{
			convertView = mlayoutInflate.inflate(R.layout.item_listview_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_num);
			viewHolder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_selected);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvName.setText(imgFloderBean.getDirName());
		if(imgFloderBean.getType() == 0)
		{
			viewHolder.tvNumber.setVisibility(View.GONE);
		}
		else
		{
			viewHolder.tvNumber.setVisibility(View.VISIBLE);
			viewHolder.tvNumber.setText(imgFloderBean.getSize() + "张");
		}

		if(imgFloderBean.isSelected()) {
			viewHolder.ivSelected.setSelected(true);
		}
		else
		{
			viewHolder.ivSelected.setSelected(false);
		}
		Glide.with(context).load(imgFloderBean.getFirstImgPath()).into(viewHolder.ivImg);

		return convertView;
	}

	public class ViewHolder
	{
		public ImageView ivImg;
		public TextView tvName;
		public TextView tvNumber;
		public ImageView ivSelected;
		public ViewHolder(){}
	}

}
