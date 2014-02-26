package com.renyu.sales.welcome;

import java.util.ArrayList;
import java.util.HashMap;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.login.LoginActivity;
import com.renyu.sales.main.HomeActivity;
import com.renyu.sales.model.JsonParse;
import com.renyu.sales.model.LoginModel;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

public class SplashActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Util.setScreenInfo(SplashActivity.this, dm.heightPixels, dm.widthPixels, dm.density);
		
		init();
	}
	
	public void init() {
		Util.saveToSdCard(SplashActivity.this);
		if(!((SalesApplication) getApplication()).isAppRunning) {
			Util.getTimeOffset(SplashActivity.this);
			//如果是新安装或者升级之后，则显示产品介绍界面，否则则直接2秒进入
			((SalesApplication) getApplication()).isAppRunning=true;
			if(Util.isNewInstallOrUpdate(SplashActivity.this)) {
				Intent intent=new Intent(this, IntroduceActivity.class);
				startActivity(intent);
				finish();
				Util.setCurrentVersion(SplashActivity.this);
			}
			else {
				setContentView(R.layout.activity_splash);
				handler.sendMessageDelayed(new Message(), 3000);
			}
		}
		else {
			//在其他异常情况下打开了这个起始页，则直接关闭，同时系统会自动唤醒后台进程
			finish();
		}
	}
	
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			ArrayList<String> user_list=Util.getUserInfo(SplashActivity.this);
			//如果之前已经保存了自动登陆状态，则执行自动登录，否则3秒之后跳转到登陆界面
			if(user_list.size()!=0&&user_list.get(3).equals("true")) {
				login(user_list.get(0), user_list.get(1));
			}
			else {
				Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}			
		}
	};
	
	public void login(final String loginname, final String loginpwd) {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==-1) {
					Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
					Toast.makeText(SplashActivity.this, "登陆异常，请手动输入登陆", 2000).show();
				}
				else {
					LoginModel model=JsonParse.getLoginModel(msg.obj.toString());
					if(model==null) {
						Toast.makeText(SplashActivity.this, "数据获取异常", 2000).show();
					}
					else {
						Intent intent=null;
						if(model.isSuccess()) {
							intent=new Intent(SplashActivity.this, HomeActivity.class);							
						}
						else {
							intent=new Intent(SplashActivity.this, LoginActivity.class);
							Toast.makeText(SplashActivity.this, model.getErrorMsg(), 2000).show();
						}
						startActivity(intent);
						finish();
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
				map.put("machinaCode", Util.getDeviceId(SplashActivity.this)+Util.getMacAddress(SplashActivity.this));
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
				map.put("deviceToken", Util.getDeviceId(SplashActivity.this)+Util.getMacAddress(SplashActivity.this));
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
	
}
