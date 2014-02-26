package com.renyu.sales.map;

import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.attendance.DailyAttendanceFailActivity;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.DailyAttendanceModel;
import com.renyu.sales.model.JsonParse;
import com.renyu.sales.myview.MyMapView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends BaseActivity {
	
	TextView local_pos=null;
	LinearLayout upload_local=null;
	MyMapView mapView=null;
	MapController controller=null;
	LocationClient client=null;
	PopupOverlay pop=null;
	LocationData local_data=null;
	LocationOverlay local_overlay=null;
	
	TextView pop_local_name=null;
	ImageView show_map_button=null;
	RelativeLayout no_mapView=null;
	TextView no_map_tip=null;
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	ImageView nav_right_image=null;
	ProgressBar nav_right_pb=null;
	
	boolean isShowMap=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		SalesApplication app=(SalesApplication) this.getApplication();
		if(app.mBMapManager==null) {
			app.mBMapManager=new BMapManager(this);
			app.mBMapManager.init(SalesApplication.strKey, new SalesApplication.MyGeneralListener());
		}
		
		setContentView(R.layout.activity_map);
		
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
		nav_title.setImageResource(R.drawable.map_title);
		nav_right_image=(ImageView) findViewById(R.id.nav_right_image);
		nav_right_image.setVisibility(View.INVISIBLE);
		nav_right_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nav_right_pb.setVisibility(View.VISIBLE);
				nav_right_image.setVisibility(View.INVISIBLE);
				client.requestLocation();
				local_pos.setText("正在定位中。。。");
				no_map_tip.setText("正在定位中。。。");
			}});
		nav_right_pb=(ProgressBar) findViewById(R.id.nav_right_pb);
		nav_right_pb.setVisibility(View.VISIBLE);
		
		show_map_button=(ImageView) findViewById(R.id.show_map_button);
		show_map_button.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isShowMap) {
					no_mapView.setVisibility(View.VISIBLE);
					mapView.setVisibility(View.INVISIBLE);
				}
				else {
					no_mapView.setVisibility(View.INVISIBLE);
					mapView.setVisibility(View.VISIBLE);
				}
				isShowMap=!isShowMap;
			}});
		local_pos=(TextView) findViewById(R.id.local_pos);
		local_pos.setText("正在定位中。。。");
		upload_local=(LinearLayout) findViewById(R.id.upload_local);
		upload_local.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(local_data.latitude!=0) {
					sign();
				}
				else {
					Toast.makeText(MapActivity.this, "正在定位中，请定位成功后再签到", 3000).show();
				}
			}});
		no_mapView=(RelativeLayout) findViewById(R.id.no_mapView);
		mapView=(MyMapView) findViewById(R.id.mapView);
		no_map_tip=(TextView) findViewById(R.id.no_map_tip);
		no_map_tip.setText("正在定位中。。。");
		controller=mapView.getController();
		controller.enableClick(true);
		controller.setZoom(16);
		controller.setCenter(new GeoPoint((int) (32.047443*1E6), (int) (118.79065*1E6)));
		//创建 弹出泡泡图层
        createPop();
		mapView.regMapViewListener(SalesApplication.getInstance().mBMapManager, new MKMapViewListener() {

			@Override
			public void onClickMapPoi(MapPoi arg0) {
				// TODO Auto-generated method stub
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				if(arg0!=null) {
					Toast.makeText(MapActivity.this, arg0.strText, 2000).show();
				}
				controller.animateTo(arg0.geoPt);
			}

			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				// TODO Auto-generated method stub
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				// TODO Auto-generated method stub
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}

			@Override
			public void onMapLoadFinish() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMapMoveFinish() {
				// TODO Auto-generated method stub
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
			}});
		local_data=new LocationData();
		local_overlay=new LocationOverlay(mapView);
		local_overlay.setData(local_data);
		mapView.getOverlays().add(local_overlay);
		local_overlay.enableCompass();
		mapView.refresh();
		
		client=new LocationClient(MapActivity.this);
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
	
	public void sign() {
		
		showProgressDialog("正在加载中。。。");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					final DailyAttendanceModel model=JsonParse.getDailyAttendanceModel(msg.obj.toString());
					if(model==null) {
						Toast.makeText(MapActivity.this, "签到或退签数据解析异常", 3000).show();
					}
					else if(!model.isSuccess()) {
						Toast.makeText(MapActivity.this, model.getMessage(), 3000).show();
					}
					else {
						switch(model.getSignstate()) {
						case 1:
							setResult(RESULT_OK, getIntent());
							finish();
							break;
						case 2:
							setResult(RESULT_OK, getIntent());
							finish();
							break;
						case 3:
							setResult(RESULT_OK, getIntent());
							finish();
							break;
						case 4:
							final AlertDialog dialog=new AlertDialog.Builder(MapActivity.this).create();
							dialog.show();
							Window window=dialog.getWindow();
							window.setContentView(R.layout.upload_local_dialog_view);
							TextView upload_local_dialog_cancel=(TextView) window.findViewById(R.id.upload_local_dialog_cancel);
							upload_local_dialog_cancel.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dialog.cancel();
									setResult(RESULT_OK, getIntent());
									finish();
								}});
							TextView upload_local_dialog_ok=(TextView) window.findViewById(R.id.upload_local_dialog_ok);
							upload_local_dialog_ok.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									dialog.cancel();
									setResult(RESULT_OK, getIntent());
									finish();

									Intent intent=new Intent(MapActivity.this, DailyAttendanceFailActivity.class);
									Bundle bundle=new Bundle();
									bundle.putString("requestSiginTime", model.getRequestSiginTime());
									if(getIntent().getExtras().getString("inOrout").equals("0")) {
										bundle.putString("inOrout", "1");
									}
									else if(getIntent().getExtras().getString("inOrout").equals("1")) {
										bundle.putString("inOrout", "2");
									}
									intent.putExtras(bundle);
									startActivity(intent);
								}});
						}
					}
				}
				else {
					Toast.makeText(MapActivity.this, "网络异常", 3000).show();
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", Util.getUserInfo(MapActivity.this).get(4));
				map.put("inOrout", getIntent().getExtras().getString("inOrout"));
				if(getIntent().getExtras().getString("inOrout").equals("0")) {
					map.put("signin_longandlat", ""+local_data.longitude+","+local_data.latitude);
					map.put("signin_longandlat_text", local_pos.getText().toString());
				}
				else if(getIntent().getExtras().getString("inOrout").equals("1")) {
					map.put("requestSiginTime", getIntent().getExtras().getString("requestSiginTime"));
					map.put("signout_longandlat", ""+local_data.longitude+","+local_data.latitude);
					map.put("signout_longandlat_text", local_pos.getText().toString());
				}
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/kaoQinMobile_signInOut.do");
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
	
	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if(arg0!=null&&!Util.convertNull(arg0.getAddrStr()).equals("")) {
				controller.animateTo(new GeoPoint((int)(arg0.getLatitude()*1e6), (int)(arg0.getLongitude()*1e6)));
				local_data.latitude=arg0.getLatitude();
				local_data.longitude=arg0.getLongitude();
				local_data.accuracy=arg0.getRadius();
				local_data.direction=arg0.getDerect();
				local_overlay.setData(local_data);
				mapView.refresh();
				local_pos.setText(arg0.getAddrStr());
				no_map_tip.setText("定位成功");
				nav_right_pb.setVisibility(View.INVISIBLE);
				nav_right_image.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void createPop() {
		View view=LayoutInflater.from(MapActivity.this).inflate(R.layout.pop_view, null);
		pop_local_name=(TextView) view.findViewById(R.id.pop_local_name);
		PopupClickListener pop_lis=new PopupClickListener() {
			
			@Override
			public void onClickedPopup(int arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		pop=new PopupOverlay(mapView, pop_lis);
		MyMapView.pop=pop;
	}
	
	public class LocationOverlay extends MyLocationOverlay {
		
		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			pop_local_name.setText("我的位置");
			pop.showPopup(Util.getBitmapFromView(pop_local_name), ((SalesApplication) getApplication()).geo, 8);
			return true;
		}

		public LocationOverlay(MapView arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}};
	
	@Override
    protected void onPause() {
		mapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	mapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        if (client!=null) {
        	client.stop();
        }
    	mapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mapView.onRestoreInstanceState(savedInstanceState);
    }
    
}
