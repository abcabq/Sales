package com.renyu.sales.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

public class MyMapView extends MapView {
	
	public static PopupOverlay pop=null;

	public MyMapView(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public MyMapView(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public MyMapView(Context arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (!super.onTouchEvent(arg0)){
			//ÏûÒþÅÝÅÝ
			if(pop!= null&&arg0.getAction()==MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}

}
