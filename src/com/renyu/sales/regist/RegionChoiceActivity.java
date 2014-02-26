package com.renyu.sales.regist;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.City;
import com.renyu.sales.model.Province;

public class RegionChoiceActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	ListView region_list=null;
	SimpleAdapter adapter=null;
	TextView refion_currentcity=null;
	
	ArrayList<Province> pro_list=null;
	ArrayList<City> city_list=null;
	//判断是显示省还是显示市
	boolean isPro=true;
	LocationClient client=null;
	//定位城市areacode
	int areacode=-1;
	//定位城市名称
	String areaName="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_region);
		
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
		
		refion_currentcity=(TextView) findViewById(R.id.refion_currentcity);
		refion_currentcity.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(areacode!=-1) {
					Intent intent=getIntent();
					Bundle bundle=new Bundle();
					bundle.putInt("areacode", areacode);
					bundle.putString("areaname", areaName);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}});
		region_list=(ListView) findViewById(R.id.region_list);
		region_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(isPro) {
					if(pro_list.get(position).getProvinceType()==-1) {
						Intent intent=getIntent();
						Bundle bundle=new Bundle();
						bundle.putInt("areacode", pro_list.get(position).getId());
						bundle.putString("areaname", pro_list.get(position).getName());
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						finish();
					}
					else {
						loadCity(pro_list.get(position).getId());
					}
				}
				else {
					Intent intent=getIntent();
					Bundle bundle=new Bundle();
					bundle.putInt("areacode", city_list.get(position).getId());
					bundle.putString("areaname", city_list.get(position).getName());
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		loadProvince();
		openGPS();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			if(isPro) {
				finish();
			}
			else {
				loadProvince();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void loadProvince() {
		ArrayList<HashMap<String, String>> data=new ArrayList<HashMap<String, String>>();
		pro_list=Util.getProvinceListInfo();
		for(int i=0;i<pro_list.size();i++) {
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("name", pro_list.get(i).getName());
			data.add(map);
		}
		adapter=new SimpleAdapter(RegionChoiceActivity.this, data, R.layout.adapter_region, new String[]{"name"}, new int[]{R.id.region_name});
		region_list.setAdapter(adapter);
		isPro=true;
	}
	
	public void loadCity(int provinceId) {
		city_list=Util.getCityListInfo(provinceId);
		ArrayList<HashMap<String, String>> data_=new ArrayList<HashMap<String, String>>();
		for(int i=0;i<city_list.size();i++) {
			HashMap<String, String> map=new HashMap<String, String>();
			map.put("name", city_list.get(i).getName());
			data_.add(map);
		}
		adapter=new SimpleAdapter(RegionChoiceActivity.this, data_, R.layout.adapter_region, new String[]{"name"}, new int[]{R.id.region_name});
		region_list.setAdapter(adapter);
		isPro=false;
	}
	
	public void openGPS() {
		client=new LocationClient(RegionChoiceActivity.this);
		client.setAK("BF38ac23e78c4daf79a44bfd46593dbd");
		client.registerLocationListener(new MyLocationListenner());
		
		LocationClientOption option=new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setScanSpan(60000);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiNumber(30);
		option.disableCache(true);		
		client.setLocOption(option);		
		
		client.start();
		client.requestLocation();
	}
	
	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if(arg0!=null&&!Util.convertNull(arg0.getCity()).equals("")) {
				refion_currentcity.setText("当前城市："+arg0.getCity());
				areaName=arg0.getCity();
				areacode=Util.getCityId(areaName);
			}			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(client!=null) {
			client.stop();
			client=null;
		}		
	}
	
}
