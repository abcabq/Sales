package com.renyu.sales.attendance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.map.MapActivity;
import com.renyu.sales.model.DailyAttendanceModel;
import com.renyu.sales.model.JsonParse;

public class DailyAttendanceActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	ImageView nav_right_image=null;
	ProgressBar nav_right_pb=null;
	
	ImageView hour_l=null;
	ImageView hour_r=null;
	ImageView minute_l=null;
	ImageView minute_r=null;
	TextView dailyattendance_in=null;
	TextView dailyattendance_out=null;
	TextView dailyattendance_time=null;
	TextView dailyattendance_desp=null;
	
	Intent intent=null;
	PendingIntent pi=null;
	//签到模型
	DailyAttendanceModel model=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dailyattendance);
		
		intent=new Intent("refresh_clock");
		
		init();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction("refresh_clock");
		registerReceiver(receiver, filter);
	}
	
	public void init() {
		nav_back=(ImageView) findViewById(R.id.nav_back);
		nav_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		nav_title=(ImageView) findViewById(R.id.nav_title);
		nav_title.setImageResource(R.drawable.dailyattendance_title);
		nav_right_image=(ImageView) findViewById(R.id.nav_right_image);
		nav_right_image.setVisibility(View.INVISIBLE);
		nav_right_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadRefreshData();
			}});
		nav_right_pb=(ProgressBar) findViewById(R.id.nav_right_pb);
		nav_right_pb.setVisibility(View.VISIBLE);
		
		dailyattendance_desp=(TextView) findViewById(R.id.dailyattendance_desp);
		hour_l=(ImageView) findViewById(R.id.hour_l);
		hour_r=(ImageView) findViewById(R.id.hour_r);
		minute_l=(ImageView) findViewById(R.id.minute_l);
		minute_r=(ImageView) findViewById(R.id.minute_r);
		dailyattendance_in=(TextView) findViewById(R.id.dailyattendance_in);
		dailyattendance_in.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(model.getSignstate()==1) {
					Intent intent=new Intent(DailyAttendanceActivity.this, MapActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("inOrout", "0");
					intent.putExtras(bundle);
					startActivityForResult(intent, 100);
				}
			}});
		dailyattendance_out=(TextView) findViewById(R.id.dailyattendance_out);
		dailyattendance_out.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(model.getSignstate()==2) {
					Intent intent=new Intent(DailyAttendanceActivity.this, MapActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("inOrout", "1");
					bundle.putString("requestSiginTime", model.getRequestSiginTime());
					intent.putExtras(bundle);
					startActivityForResult(intent, 100);
				}
			}});
		dailyattendance_time=(TextView) findViewById(R.id.dailyattendance_time);
		
		loadRefreshData();
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("refresh_clock")) {
				setTime(0);
			}			
		}
		
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==100) {
			loadRefreshData();
		}
	};	
	
	public void loadRefreshData() {
		showProgressDialog("正在加载中。。。");
		nav_right_pb.setVisibility(View.VISIBLE);
		nav_right_image.setVisibility(View.INVISIBLE);
		if(pi!=null) {
			stopShowTime();
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				nav_right_pb.setVisibility(View.INVISIBLE);
				nav_right_image.setVisibility(View.VISIBLE);
				if(msg.what==1) {
					model=JsonParse.getDailyAttendanceModel(msg.obj.toString());
					if(model==null) {
						Toast.makeText(DailyAttendanceActivity.this, "每日签到数据解析异常", 3000).show();
					}
					else if(!model.isSuccess()) {
						Toast.makeText(DailyAttendanceActivity.this, model.getMessage(), 3000).show();
					}
					else {
						switch(model.getSignstate()) {
						case 1:
							dailyattendance_desp.setVisibility(View.GONE);
							dailyattendance_in.setBackgroundResource(R.drawable.confirm_button_sel);
							dailyattendance_out.setBackgroundResource(R.drawable.confirm_button_noable);
							dailyattendance_time.setText("");
							break;
						case 2:
							dailyattendance_desp.setVisibility(View.VISIBLE);
							dailyattendance_desp.setText("今日已经工作");
							dailyattendance_in.setBackgroundResource(R.drawable.confirm_button_noable);
							dailyattendance_out.setBackgroundResource(R.drawable.confirm_button_sel);
							dailyattendance_time.setText(Html.fromHtml("上一次签到时间：<font color='green'>"+model.getTimeHHMM().split(";")[0]+"</font>"));
							startShowTime();
							break;
						case 3:
							dailyattendance_desp.setVisibility(View.VISIBLE);
							dailyattendance_desp.setText("今日累计工作");
							dailyattendance_in.setBackgroundResource(R.drawable.confirm_button_noable);
							dailyattendance_out.setBackgroundResource(R.drawable.confirm_button_noable);
							dailyattendance_time.setText(Html.fromHtml("上一次签退时间：<font color='green'>"+model.getTimeHHMM().split(";")[1]+"</font>"));
							setTime(1);
							break;
						}
					}
				}
				else {
					Toast.makeText(DailyAttendanceActivity.this, "网络异常", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", Util.getUserInfo(DailyAttendanceActivity.this).get(4));
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/kaoQinMobile_signState.do");
				if(Util.convertNull(result).equals("")) {
					m.what=-1;
				}
				else {
					m.what=1;
				}
				m.obj=result;
				handler.sendMessage(m);
			}
		}).start();
	}
	
	/**
	 * 设置定时器
	 */
	public void startShowTime() {
		pi=PendingIntent.getBroadcast(DailyAttendanceActivity.this, 0, intent, 0);
		AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi);
	}
	
	/**
	 * 关闭定时器
	 */
	public void stopShowTime() {
		AlarmManager manager=(AlarmManager) getSystemService(ALARM_SERVICE);
		manager.cancel(pi);
		pi=null;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		stopShowTime();
	}
	
	/**
	 * 根据不同终点时间类型设置时间显示 
	 * 1 当前时间
	 * 2 指定时间
	 * @param typeTime
	 */
	public void setTime(int typeTime) {

		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date sign_=format.parse(model.getRequestSiginTime()+" "+model.getTimeHHMM().split(";")[0]);
			int totalSecond=0;
			if(typeTime==1) {
				Date sign__=format.parse(model.getRequestSiginTime()+" "+model.getTimeHHMM().split(";")[1]);
				totalSecond=(int) ((sign__.getTime()-sign_.getTime())/1000);
			}
			else {
				totalSecond=(int) ((System.currentTimeMillis()+Util.isTimeOffsetExists(DailyAttendanceActivity.this)-sign_.getTime())/1000);
			}
			int hourTime=totalSecond/3600;
			int minuteTime=(totalSecond-hourTime*3600)/60;
			if(hourTime<100) {
				hour_l.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/mask_number_"+hourTime/10, null,null));
				hour_r.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/mask_number_"+hourTime%10, null,null));				
				minute_l.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/mask_number_"+minuteTime/10, null,null));
				minute_r.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/mask_number_"+minuteTime%10, null,null));
			}
			else {
				hour_l.setImageResource(R.drawable.mask_number_9);
				hour_r.setImageResource(R.drawable.mask_number_9);
				minute_l.setImageResource(R.drawable.mask_number_9);
				minute_l.setImageResource(R.drawable.mask_number_9);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

}
