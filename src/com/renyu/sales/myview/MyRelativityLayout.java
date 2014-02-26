package com.renyu.sales.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyRelativityLayout extends RelativeLayout {
	
	boolean touchFlag=false;

	public MyRelativityLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyRelativityLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyRelativityLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return touchFlag;
	}

	public boolean isTouchFlag() {
		return touchFlag;
	}

	public void setTouchFlag(boolean touchFlag) {
		this.touchFlag = touchFlag;
	}

}
