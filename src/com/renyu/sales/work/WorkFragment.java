package com.renyu.sales.work;

import com.renyu.sales.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class WorkFragment extends Fragment {
	
	RelativeLayout announcement_list_layout=null;
	RelativeLayout daily_list_layout=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.activity_work, null);
		announcement_list_layout=(RelativeLayout) view.findViewById(R.id.announcement_list_layout);
		announcement_list_layout.setOnClickListener(lis_layout);
		daily_list_layout=(RelativeLayout) view.findViewById(R.id.daily_list_layout);
		daily_list_layout.setOnClickListener(lis_layout);
		return view;
	}
	
	RelativeLayout.OnClickListener lis_layout=new RelativeLayout.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.announcement_list_layout:
				Intent intent_announcement=new Intent(getActivity(), AnnouncementActivity.class);
				startActivity(intent_announcement);
				break;
			case R.id.daily_list_layout:
				Intent intent_daily=new Intent(getActivity(), DailyPaperActivity.class);
				startActivity(intent_daily);
				break;
			}
		}};

}
