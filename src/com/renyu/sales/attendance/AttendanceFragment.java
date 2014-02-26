package com.renyu.sales.attendance;

import com.renyu.sales.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class AttendanceFragment extends Fragment {
	
	RelativeLayout daily_attendance_layout=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.activity_attendance, null);
		daily_attendance_layout=(RelativeLayout) view.findViewById(R.id.daily_attendance_layout);
		daily_attendance_layout.setOnClickListener(lis);
		return view;
	}
	
	RelativeLayout.OnClickListener lis=new RelativeLayout.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.daily_attendance_layout:
				Intent intent=new Intent(getActivity(), DailyAttendanceActivity.class);
				startActivity(intent);
				break;
			}
		}};

}
