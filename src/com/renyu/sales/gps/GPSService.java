package com.renyu.sales.gps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Dao;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.GPSModel;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {
    
    LocationClient client=null;

	public final static String SendGPSAction="SendGPSAction";
	
    @Override
    public void onDestroy() {
        // Make sure our notification is gone.
        System.out.println("服务已经被关闭");
		client.stop();
		client=null;
		//重复打开
		Intent intent = new Intent();
        intent.setClass(this, GPSService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	if(client==null) {
    		System.out.println("服务已经开启");
    		
    		client=new LocationClient(GPSService.this);
    		client.setAK("BF38ac23e78c4daf79a44bfd46593dbd");
    		client.registerLocationListener(new MyLocationListenner());
    		
    		LocationClientOption option=new LocationClientOption();
    		option.setOpenGps(true);
    		option.setCoorType("bd09ll");
    		option.setServiceName("com.baidu.location.service_v2.9");
    		option.setPoiExtraInfo(true);
    		option.setAddrType("all");
    		option.setScanSpan(120000);
    		option.setPriority(LocationClientOption.NetWorkFirst);
    		option.setPoiNumber(30);
    		option.disableCache(true);		
    		client.setLocOption(option);		
    		
    		client.start();
    		client.requestLocation();
    	}
        return START_STICKY;
    }

    class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if(arg0!=null&&!Util.convertNull(arg0.getAddrStr()).equals("")) {
				((SalesApplication) getApplication()).geo=new GeoPoint((int) (arg0.getLatitude()*1E6), (int) (arg0.getLongitude()*1E6));
				System.out.println(arg0.getAddrStr()+"  "+arg0.getLongitude()+"  "+arg0.getLatitude()+"  "+arg0.getTime());
				Intent intent=new Intent();
				intent.setAction(SendGPSAction);
				Bundle bundle=new Bundle();
				bundle.putDouble("Longitude", arg0.getLongitude());
				bundle.putDouble("Latitude", arg0.getLatitude());
				bundle.putFloat("Radius", arg0.getRadius());
				bundle.putFloat("Derect", arg0.getDerect());
				intent.putExtras(bundle);
				sendBroadcast(intent);
				//Util.writeLog(arg0.getAddrStr(), ""+arg0.getLongitude(), ""+arg0.getLatitude());
				//获取到时间差值之后开始记录
				if(Util.isTimeOffsetExists(GPSService.this)!=-1l) {
					System.out.println("已获取到时间差值");
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//未登录的时候不上传
					if(Util.getUserInfo(GPSService.this)!=null) {
						//数据库表为空的时候，直接插入
						if(Dao.getInstance(GPSService.this).readLastGPS()!=null) {
							try {
								//当前获取到的时间与数据库的时间进行比较，如果超过15分钟，则记录上报
								Date date_db=format.parse(Dao.getInstance(GPSService.this).readLastGPS().getTime());
								if(System.currentTimeMillis()+Util.isTimeOffsetExists(GPSService.this)-date_db.getTime()>1000*60*15) {
									//连续两次获取到的坐标没有发生变动的情况下，gps获取到的时间是不会有变化的
									if(date_db.getTime()==format.parse(arg0.getTime()).getTime()) {
										Dao.getInstance(GPSService.this).insertGPS(arg0.getLatitude(), arg0.getLongitude(), arg0.getAddrStr(), format.format(new Date(System.currentTimeMillis()+Util.isTimeOffsetExists(GPSService.this))));
									}
									else {
										Dao.getInstance(GPSService.this).insertGPS(arg0.getLatitude(), arg0.getLongitude(), arg0.getAddrStr(), arg0.getTime());
									}
									uploadGPS();								
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else {
							Dao.getInstance(GPSService.this).insertGPS(arg0.getLatitude(), arg0.getLongitude(), arg0.getAddrStr(), arg0.getTime());
							uploadGPS();
						}
					}
				}	
				else {
					System.out.println("未获取到时间差值");
				}
			}			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * GPS上传
	 */
	public void uploadGPS() {
		final ArrayList<GPSModel> model_list=Dao.getInstance(GPSService.this).getAllUploadGPSModelList();
		new Thread(new Runnable() {
	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("开始上传位置信息");
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", Util.getUserInfo(GPSService.this).get(4));
				GPSModel model=model_list.get(model_list.size()-1);
				map.put("longitude", model.getLongitude());
				map.put("latitude", model.getLatitude());
				map.put("Upload_time", model.getTime());
//				String temp_lat="";
//				String temp_lon="";
//				String temp_time="";
//				for(int i=0;i<model_list.size();i++) {
//					GPSModel model=model_list.get(i);
//					temp_lat+=model.getLatitude()+";";
//					temp_lon+=model.getLongitude()+";";
//					temp_time+=model.getTime()+";";
//				}
//				if(temp_lat.indexOf(";")!=-1) {
//					temp_lat=temp_lat.substring(0, temp_lat.length()-1);
//				}
//				if(temp_lon.indexOf(";")!=-1) {
//					temp_lon=temp_lon.substring(0, temp_lon.length()-1);
//				}
//				if(temp_time.indexOf(";")!=-1) {
//					temp_time=temp_time.substring(0, temp_time.length()-1);
//				}
				Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/kaoQinMobile_trackrecord.do");
				System.out.println("完成上传");
			}}).start();
	}
	
}
