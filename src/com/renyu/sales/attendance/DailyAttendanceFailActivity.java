package com.renyu.sales.attendance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.HttpClientUpload;
import com.renyu.sales.commons.Util;

public class DailyAttendanceFailActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	ImageView nav_right_image=null;
	
	EditText dailyattendance_fail_result=null;
	TextView dailyattendance_fail_commit=null;
	ImageView dailyattendance_fail_camera=null;
	
	Bitmap bmp=null;
	//camera中转file的url
	Uri cameraUrl=null;
	//camera中转file
	File cameraFile=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dailyattendancefail);
		
		//camera中转file
		cameraFile=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/camerafile.jpg");
		if(!cameraFile.exists()) {
			try {
				cameraFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cameraUrl=Uri.fromFile(cameraFile);
		
		init();
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
		nav_title.setImageResource(R.drawable.dailyattendance_fail_title);
		nav_right_image=(ImageView) findViewById(R.id.nav_right_image);
		nav_right_image.setVisibility(View.VISIBLE);
		nav_right_image.setImageResource(R.drawable.camera_sel);
		nav_right_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				//告诉相机拍摄完毕输出图片到指定的Uri
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraUrl);
				startActivityForResult(intent, 100);
			}});
		dailyattendance_fail_camera=(ImageView) findViewById(R.id.dailyattendance_fail_camera);
		dailyattendance_fail_result=(EditText) findViewById(R.id.dailyattendance_fail_result);
		dailyattendance_fail_commit=(TextView) findViewById(R.id.dailyattendance_fail_commit);
		dailyattendance_fail_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(dailyattendance_fail_result.getText().toString().equals("")) {
					Toast.makeText(DailyAttendanceFailActivity.this, "请您填写说明内容", 2000).show();
				}
				else {
					uploadReason();
				}
			}});
	}
	
	public void uploadReason() {
		showProgressDialog("正在提交中。。。");
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				finish();
				System.out.println(msg.obj);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", Util.getUserInfo(DailyAttendanceFailActivity.this).get(4));
				map.put("requestSiginTime", getIntent().getExtras().getString("requestSiginTime"));
				map.put("exceptionMessage", dailyattendance_fail_result.getText().toString());
				map.put("type", getIntent().getExtras().getString("inOrout"));
				String result="";
				try {
					if(bmp!=null) {
						result=HttpClientUpload.post(((SalesApplication) getApplicationContext()).requestIp+"/kaoQinMobile_exceptionRecord.do", map, cameraFile, "uploadFile");
					}
					else {
						result=HttpClientUpload.post(((SalesApplication) getApplicationContext()).requestIp+"/kaoQinMobile_exceptionRecord.do", map, null, "uploadFile");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m.obj=result;
				handler.sendMessage(m);
			}}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==100) {
			bmp=Util.compressBmp(cameraFile.getPath());
			dailyattendance_fail_camera.setImageBitmap(bmp);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bmp!=null) {
			bmp.recycle();
			bmp=null;
		}
	}

}
