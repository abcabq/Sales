package com.renyu.sales.regist;

import java.util.HashMap;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.JsonParse;
import com.renyu.sales.model.RegistModel;
import com.renyu.sales.myview.MyRelativityLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	MyRelativityLayout dialog_view_comp=null;
	MyRelativityLayout dialog_view_load=null;
	TextView dialog_view_return=null;
	
	EditText regist_name=null;
	EditText regist_pass=null;
	EditText regist_contact=null;
	EditText regist_email=null;
	EditText regist_phone=null;
	//EditText regist_identify=null;
	//TextView get_regist_idetify=null;
	TextView regist_region=null;
	TextView regist_now=null;
	
	//默认城市代码
	int areacode=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist);
		
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
		nav_title.setImageResource(R.drawable.regist_title);
		
		dialog_view_comp=(MyRelativityLayout) findViewById(R.id.dialog_view_comp);
		dialog_view_load=(MyRelativityLayout) findViewById(R.id.dialog_view_load);
		dialog_view_return=(TextView) findViewById(R.id.dialog_view_return);
		dialog_view_return.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				Bundle bundle=new Bundle();
				bundle.putString("regist_name", regist_name.getText().toString());
				bundle.putString("regist_pass", regist_pass.getText().toString());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}});
		
		regist_name=(EditText) findViewById(R.id.regist_name);
		regist_pass=(EditText) findViewById(R.id.regist_pass);
		regist_contact=(EditText) findViewById(R.id.regist_contact);
		regist_email=(EditText) findViewById(R.id.regist_email);
		regist_phone=(EditText) findViewById(R.id.regist_phone);
		//regist_identify=(EditText) findViewById(R.id.regist_identify);
		//get_regist_idetify=(TextView) findViewById(R.id.get_regist_idetify);
		regist_region=(TextView) findViewById(R.id.regist_region);
		regist_region.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(RegistActivity.this, RegionChoiceActivity.class);
				startActivityForResult(intent, 100);
			}});
		regist_now=(TextView) findViewById(R.id.regist_now);
		regist_now.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(regist_name.getText().toString().equals("")||
						regist_pass.getText().toString().equals("")||
						regist_contact.getText().toString().equals("")||
						regist_email.getText().toString().equals("")||
						regist_phone.getText().toString().equals("")) {
					Toast.makeText(RegistActivity.this, "请完整填写您的注册信息", 3000).show();
					return;
				}
				if(!Util.checkPhoneNum(regist_phone.getText().toString())) {
					Toast.makeText(RegistActivity.this, "请正确填写您的手机号码", 3000).show();
					return;
				}
				if(!Util.checkEmail(regist_email.getText().toString())) {
					Toast.makeText(RegistActivity.this, "请正确填写您的邮箱", 3000).show();
					return;
				}
				if(areacode==-1) {
					Toast.makeText(RegistActivity.this, "请选择所属区域", 3000).show();
					return;
				}
				regist();
			}});
	}
	
	public void regist() {
		dialog_view_load.setVisibility(View.VISIBLE);
		dialog_view_load.setTouchFlag(true);
		dialog_view_comp.setVisibility(View.GONE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dialog_view_load.setVisibility(View.GONE);
				dialog_view_load.setTouchFlag(false);
				if(msg.what==1) {
					RegistModel model=JsonParse.getRegistModel(msg.obj.toString());
					if(model.isSuccess()) {
						dialog_view_comp.setVisibility(View.VISIBLE);
						dialog_view_comp.setTouchFlag(true);
					}
					Toast.makeText(RegistActivity.this, model.getMessage(), 3000).show();
				}
				else {
					Toast.makeText(RegistActivity.this, "网络异常", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("loginname", regist_name.getText().toString());
				map.put("loginpwd", regist_pass.getText().toString());
				map.put("username", regist_contact.getText().toString());
				map.put("tel", regist_phone.getText().toString());
				map.put("email", regist_email.getText().toString());
				map.put("areacode", ""+areacode);
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/userMobile_regist.do");
				if(Util.convertNull(result).equals("")) {
					m.what=-1;
				}
				else {
					m.what=1;
				}
				m.obj=result;
				handler.sendMessage(m);
			}}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK) {
			if(requestCode==100) {
				areacode=data.getExtras().getInt("areacode");
				regist_region.setText(data.getExtras().getString("areaname"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
