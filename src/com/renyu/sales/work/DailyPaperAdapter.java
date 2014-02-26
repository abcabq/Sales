package com.renyu.sales.work;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.renyu.sales.R;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.DailyPaperModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DailyPaperAdapter extends BaseAdapter {
	
	ArrayList<DailyPaperModel> data_list=null;
	Context context=null;
	
	public BitmapUtils bitmapUtils;
	
	public DailyPaperAdapter(ArrayList<DailyPaperModel> data_list, Context context) {
		this.context=context;
		this.data_list=data_list;
		bitmapUtils=new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.ic_launcher);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_launcher);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DailyPaperHolder holder=null;
		if(convertView==null) {
			holder=new DailyPaperHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_dailypaper, null);
			holder.daily_name=(TextView) convertView.findViewById(R.id.daily_name);
			holder.daily_pic_layout=(LinearLayout) convertView.findViewById(R.id.daily_pic_layout);
			holder.daily_pic_1=(ImageView) convertView.findViewById(R.id.daily_pic_1);
			holder.daily_pic_2=(ImageView) convertView.findViewById(R.id.daily_pic_2);
			holder.daily_pic_3=(ImageView) convertView.findViewById(R.id.daily_pic_3);
			holder.daily_date=(TextView) convertView.findViewById(R.id.daily_date);
			convertView.setTag(holder);
		}
		else {
			holder=(DailyPaperHolder)convertView.getTag();
		}
		holder.daily_name.setText(data_list.get(position).getContent());
		holder.daily_date.setText(data_list.get(position).getCreatetime());
		if(!Util.convertNull(data_list.get(position).getPic1()).equals("")) {
			holder.daily_pic_layout.setVisibility(View.VISIBLE);
			if(!Util.convertNull(data_list.get(position).getPic1()).equals("")) {
				bitmapUtils.display(holder.daily_pic_1, data_list.get(position).getPic1());
				holder.daily_pic_1.setVisibility(View.VISIBLE);
			}
			else {
				holder.daily_pic_1.setVisibility(View.INVISIBLE);
			}
			if(!Util.convertNull(data_list.get(position).getPic2()).equals("")) {
				bitmapUtils.display(holder.daily_pic_2, data_list.get(position).getPic2());
				holder.daily_pic_2.setVisibility(View.VISIBLE);
			}
			else {
				holder.daily_pic_2.setVisibility(View.INVISIBLE);
			}
			if(!Util.convertNull(data_list.get(position).getPic3()).equals("")) {
				bitmapUtils.display(holder.daily_pic_3, data_list.get(position).getPic3());
				holder.daily_pic_3.setVisibility(View.VISIBLE);
			}
			else {
				holder.daily_pic_3.setVisibility(View.INVISIBLE);
			}
		}
		else {
			holder.daily_pic_layout.setVisibility(View.GONE);
		}
		holder.daily_pic_1.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(0);
			}});
		holder.daily_pic_2.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(1);
			}});
		holder.daily_pic_3.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click(2);
			}});
		return convertView;
	}
	
	public void click(int pos) {
		Intent intent=new Intent(context, GalleryActivity.class);
		Bundle bundle=new Bundle();
		ArrayList<String> temp_list=new ArrayList<String>();
		for(int i=0;i<data_list.size();i++) {
			if(!data_list.get(i).getPic1().equals("")) {
				temp_list.add(data_list.get(i).getPic1());
			}
			if(!data_list.get(i).getPic2().equals("")) {
				temp_list.add(data_list.get(i).getPic2());
			}
			if(!data_list.get(i).getPic3().equals("")) {
				temp_list.add(data_list.get(i).getPic3());
			}
		}
		bundle.putStringArrayList("pic", temp_list);
		bundle.putInt("pos", pos);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

}

class DailyPaperHolder {
	TextView daily_name=null;
	LinearLayout daily_pic_layout=null;
	ImageView daily_pic_1=null;
	ImageView daily_pic_2=null;
	ImageView daily_pic_3=null;
	TextView daily_date=null;
}
