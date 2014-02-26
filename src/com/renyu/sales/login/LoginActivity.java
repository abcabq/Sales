package com.renyu.sales.login;

import java.util.ArrayList;
import java.util.HashMap;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.main.HomeActivity;
import com.renyu.sales.model.JsonParse;
import com.renyu.sales.model.LoginModel;
import com.renyu.sales.regist.RegistActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	EditText login_name=null;
	EditText login_pass=null;
	CheckBox remember_pass=null;
	CheckBox auto_login=null;
	TextView login_button=null;
	TextView regist_button=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		init();
	}
	
	public void init() {
		nav_back=(ImageView) findViewById(R.id.nav_back);
		nav_back.setVisibility(View.GONE);
		nav_title=(ImageView) findViewById(R.id.nav_title);
		nav_title.setImageResource(R.drawable.login_title);
		
		login_name=(EditText) findViewById(R.id.login_name);
		login_pass=(EditText) findViewById(R.id.login_pass);
		remember_pass=(CheckBox) findViewById(R.id.remember_pass);
		auto_login=(CheckBox) findViewById(R.id.auto_login);
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					remember_pass.setChecked(true);
				}
			}
		});
		ArrayList<String> user_list=Util.getUserInfo(LoginActivity.this);
		if(user_list!=null) {
			login_name.setText(user_list.get(0));
			login_pass.setText(user_list.get(1));
			if(user_list.get(2).equals("true")) {
				remember_pass.setChecked(true);
			}
			else {
				remember_pass.setChecked(false);
			}
			if(user_list.get(3).equals("true")) {
				auto_login.setChecked(true);
			}
			else {
				auto_login.setChecked(false);
			}
		}
		login_button=(TextView) findViewById(R.id.login_button);
		login_button.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login(login_name.getText().toString(), login_pass.getText().toString());
			}});
		regist_button=(TextView) findViewById(R.id.regist_button);
		regist_button.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LoginActivity.this, RegistActivity.class);
				startActivityForResult(intent, 200);
			}});
		
	}
	
	public void login(final String loginname, final String loginpwd) {
		
		showProgressDialog("正在登录中。。。");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==-1) {
					Toast.makeText(LoginActivity.this, "网络异常", 2000).show();
				}
				else {
					LoginModel model=JsonParse.getLoginModel(msg.obj.toString());
					if(model==null) {
						Toast.makeText(LoginActivity.this, "数据获取异常", 2000).show();
					}
					else {
						if(model.isSuccess()) {
							Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
							startActivity(intent);
							finish();
							Util.saveUserInfo(LoginActivity.this, model.getUserId(), model.getHead_img(), loginname, loginpwd, remember_pass.isChecked(), auto_login.isChecked());
						}
						else {
							Toast.makeText(LoginActivity.this, model.getErrorMsg(), 2000).show();
						}
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("loginname", loginname);
				map.put("loginpwd", loginpwd);
				map.put("machinaCode", Util.getDeviceId(LoginActivity.this)+Util.getMacAddress(LoginActivity.this));
				map.put("platform", "1");
				try {
					PackageManager manager=getPackageManager();
					PackageInfo info=manager.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
					map.put("info", info.versionName);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					map.put("info", "");
				}
				map.put("deviceToken", Util.getDeviceId(LoginActivity.this)+Util.getMacAddress(LoginActivity.this));
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/userMobile_login.do");
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			((SalesApplication) getApplication()).isAppRunning=false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK) {
			if(requestCode==200) {
				login_name.setText(data.getExtras().getString("regist_name"));
				login_pass.setText(data.getExtras().getString("regist_pass"));
				remember_pass.setChecked(true);
				auto_login.setChecked(true);
			}
		}
	}

}
