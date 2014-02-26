package com.renyu.sales.map;

import java.util.ArrayList;
import java.util.LinkedList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Dao;
import com.renyu.sales.model.GPSModel;
import com.renyu.sales.myview.MyMapView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MapPathActivity extends Activity {
	
	MyMapView mapView=null;
	MapController controller=null;
	GraphicsOverlay graphicsOverlay=null;
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
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
		nav_title.setImageResource(R.drawable.login_title);
		
		mapView=(MyMapView) findViewById(R.id.mapView);
		graphicsOverlay=new GraphicsOverlay(mapView);
		mapView.getOverlays().add(graphicsOverlay);
		controller=mapView.getController();
		controller.enableClick(true);
		controller.setZoom(16);
		if(((SalesApplication) getApplication()).geo!=null) {
			controller.setCenter(new GeoPoint(((SalesApplication) getApplication()).geo.getLatitudeE6(), ((SalesApplication) getApplication()).geo.getLongitudeE6()));
		}
		else {
			controller.setCenter(new GeoPoint((int) (32.047443*1E6), (int) (118.79065*1E6)));
		}
		
		//临时用来比较的变量
		GeoPoint tempGP=null;
		//保存用来画折现的集合
		LinkedList<GeoPoint> list=new LinkedList<GeoPoint>();
		ArrayList<GPSModel> model_list=Dao.getInstance(MapPathActivity.this).getAllUploadGPSModelList();
		for(int i=0;i<model_list.size();i++) {
			GPSModel model=model_list.get(i);
			GeoPoint tempGP_=new GeoPoint((int) (Double.parseDouble(model.getLatitude())*1E6), (int) (Double.parseDouble(model.getLongitude())*1E6));
			if(tempGP==null) {
				graphicsOverlay.setData(drawPoint(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude())));
				tempGP=tempGP_;
				list.add(tempGP_);
			}
			else {
				double distance=DistanceUtil.getDistance(tempGP_, tempGP);
				if(Math.abs(distance)>500) {
					graphicsOverlay.setData(drawPoint(Double.parseDouble(model.getLatitude()), Double.parseDouble(model.getLongitude())));
					tempGP=tempGP_;
					list.add(tempGP_);
				}
			}
		}
		//graphicsOverlay.setData(drawLine(list));
		mapView.refresh();
	}
	
	/**
     * 绘制单点
     * @return 点对象
     */
    public Graphic drawPoint(double mLat, double mLon){
    	int lat = (int) (mLat*1E6);
	   	int lon = (int) (mLon*1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);	   	
	   	//构建点
  		Geometry pointGeometry = new Geometry();
  		//设置坐标
  		pointGeometry.setPoint(pt1, 10);
  		//设定样式
  		Symbol pointSymbol = new Symbol();
 		Symbol.Color pointColor = pointSymbol.new Color();
 		pointColor.red = 0;
 		pointColor.green = 126;
 		pointColor.blue = 255;
 		pointColor.alpha = 255;
 		pointSymbol.setPointSymbol(pointColor);
  		//生成Graphic对象
  		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
  		return pointGraphic;
    }
    
    /**
     * 绘制折线，该折线状态随地图状态变化
     * @return 折线对象
     */
    public Graphic drawLine(LinkedList<GeoPoint> list){    
	    //构建线
  		Geometry lineGeometry = new Geometry();
  		//设定折线点坐标
  		GeoPoint[] linePoints = new GeoPoint[list.size()];
  		for(int i=0;i<list.size();i++) {
  			linePoints[i]=list.get(i);
  		}
  		lineGeometry.setPolyLine(linePoints);
  		//设定样式
  		Symbol lineSymbol = new Symbol();
  		Symbol.Color lineColor = lineSymbol.new Color();
  		lineColor.red = 255;
  		lineColor.green = 0;
  		lineColor.blue = 0;
  		lineColor.alpha = 255;
  		lineSymbol.setLineSymbol(lineColor, 10);
  		//生成Graphic对象
  		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
  		return lineGraphic;
    }
	
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
