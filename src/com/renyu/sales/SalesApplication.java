package com.renyu.sales;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.renyu.sales.commons.Util;

import android.app.Application;
import android.widget.Toast;

public class SalesApplication extends Application {
	
	private static SalesApplication instance=null;
	public boolean isAppRunning=false;
	public BMapManager mBMapManager=null;
	
	public static final String strKey="BF38ac23e78c4daf79a44bfd46593dbd";
	public String requestIp="http://122.96.155.87:8081/waiqinservice";
	
	//GeoPoint历史
	public GeoPoint geo=null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance=this;
		if(mBMapManager==null) {
			mBMapManager=new BMapManager(this);
		}
		if(!mBMapManager.init(strKey, new MyGeneralListener() {})) {
			Toast.makeText(this, "初始化地图引擎失败", 3000).show();
		}
		
		System.out.println("别名："+Util.getDeviceId(this)+Util.getMacAddress(this));
		JPushInterface.setAlias(this, Util.getDeviceId(this)+Util.getMacAddress(this), new TagAliasCallback() {

			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				System.out.println(arg0+"  "+arg1);
			}});
        JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
	}
	
	public static SalesApplication getInstance() {
		return instance;
	}
	
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPermissionState(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
