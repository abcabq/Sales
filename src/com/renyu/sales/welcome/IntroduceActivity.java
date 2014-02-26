package com.renyu.sales.welcome;

import java.util.ArrayList;

import com.renyu.sales.R;
import com.renyu.sales.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class IntroduceActivity extends Activity {
	
	ImageView introduce_close=null;
	ViewPager introduce_layout=null;
	PagerAdapter adapter=null;
	ArrayList<View> views=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_introduce);
		
		views=new ArrayList<View>();
		loadViews();
		init();
	}
	
	public void init() {
		introduce_close=(ImageView) findViewById(R.id.introduce_close);
		introduce_close.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jumpToMain();
			}});
		introduce_layout=(ViewPager) findViewById(R.id.introduce_layout);
		adapter=new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return views.size();
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				// TODO Auto-generated method stub
				View view=views.get(position);
				((ViewPager) container).addView(view);
				return view;
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				// TODO Auto-generated method stub
				View view=views.get(position);
				((ViewPager) container).removeView(view);
			}
		};
		introduce_layout.setAdapter(adapter);
	}
	
	/**
	 * 加载全部索引视图
	 */
	public void loadViews() {
		for(int i=0;i<4;i++) {
			View view=LayoutInflater.from(IntroduceActivity.this).inflate(R.layout.activity_introduce_view, null);
			ImageView introduce_image=(ImageView) view.findViewById(R.id.introduce_image);
			int res_id=getResources().getIdentifier(this.getPackageName()+":drawable/guide_map"+(i+1), null, null);
			introduce_image.setBackgroundResource(res_id);
			if(i==3) {
				ImageView introduce_enter=(ImageView) view.findViewById(R.id.introduce_enter);
				introduce_enter.setVisibility(View.VISIBLE);
				introduce_enter.setOnClickListener(new ImageView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						jumpToMain();
					}});
			}
			views.add(view);
		}
	}
	
	public void jumpToMain() {
		Intent intent=new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
}
