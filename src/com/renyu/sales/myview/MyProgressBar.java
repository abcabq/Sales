package com.renyu.sales.myview;

import java.util.ArrayList;
import java.util.LinkedList;

import com.renyu.sales.R;
import com.renyu.sales.commons.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class MyProgressBar extends View {
	
	Context context=null;

	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public MyProgressBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public void init(Context context) {
		this.context=context;
		
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		drawProcess(100, Color.GRAY, canvas);
		drawProcess(50, Color.RED, canvas);
		postInvalidate();
	}
	
	/**
	 * »­Í¼°åÆðÊ¼×ø±ê
	 * @param context
	 * @return
	 */
	public LinkedList<Integer> getStartAndEnd(Context context) {
		LinkedList<String> list=Util.getScreenInfo(context);
		int start=(int) (Float.parseFloat(list.get(2))*30);
		int end=Integer.parseInt(list.get(1))-(int) (Float.parseFloat(list.get(2))*50);
		LinkedList<Integer> canvasInfo=new LinkedList<Integer>();
		canvasInfo.add(start);
		canvasInfo.add(end);
		return canvasInfo;
	}
	
	public void drawProcess(int process, int color, Canvas canvas) {
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		
		RectF rect=new RectF();
		rect.left=getStartAndEnd(context).get(0);                                
		rect.top=(int) (Float.parseFloat(Util.getScreenInfo(context).get(2))*20);                                  
		rect.right=(getStartAndEnd(context).get(1)-getStartAndEnd(context).get(0))*process/100;                                   
		rect.bottom=(int) (Float.parseFloat(Util.getScreenInfo(context).get(2))*28); 
		canvas.drawRoundRect(rect, 10, 10, paint);
	}

}
