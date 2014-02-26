package com.renyu.sales.main;

import java.util.ArrayList;

import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.attendance.AttendanceFragment;
import com.renyu.sales.gps.GuardService;
import com.renyu.sales.reciprocal.ReciprocalFragment;
import com.renyu.sales.setup.SetupFragment;
import com.renyu.sales.work.WorkFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class HomeActivity extends FragmentActivity {
	
	ImageView nav_back=null;
	
	ViewPager home_pager=null;
	HomeAdapter adapter=null;
	
	ImageView work=null;
	ImageView attendance=null;
	ImageView reciprocal=null;
	ImageView setup=null;
	
	ArrayList<Fragment> fragment_list=null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		
		fragment_list=new ArrayList<Fragment>();
		
		init();
		
		//开启定位服务
		Intent intent_new = new Intent();
		intent_new.setClass(this, GuardService.class);
        startService(intent_new);
	}
	
	public void init() {
		nav_back=(ImageView) findViewById(R.id.nav_back);
		nav_back.setVisibility(View.GONE);
		
		home_pager=(ViewPager) findViewById(R.id.home_pager);
		home_pager.setOffscreenPageLimit(2);
		addFragment();
		adapter=new HomeAdapter(getSupportFragmentManager(), fragment_list);
		home_pager.setAdapter(adapter);
		home_pager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				work.setImageResource(R.drawable.tab_icon_work);
				attendance.setImageResource(R.drawable.tab_icon_attendance);
				reciprocal.setImageResource(R.drawable.tab_icon_reciprocal);
				setup.setImageResource(R.drawable.tab_icon_setup);
				switch(arg0) {
				case 0:
					work.setImageResource(R.drawable.tab_icon_work_selected);
					break;
				case 1:
					attendance.setImageResource(R.drawable.tab_icon_attendance_selected);
					break;
				case 2:
					reciprocal.setImageResource(R.drawable.tab_icon_reciprocal_selected);
					break;
				case 3:
					setup.setImageResource(R.drawable.tab_icon_setup_selected);
					break;
				}
			}});
		
		work=(ImageView) findViewById(R.id.work);
		work.setOnClickListener(click);
		attendance=(ImageView) findViewById(R.id.attendance);
		attendance.setOnClickListener(click);
		reciprocal=(ImageView) findViewById(R.id.reciprocal);
		reciprocal.setOnClickListener(click);
		setup=(ImageView) findViewById(R.id.setup);
		setup.setOnClickListener(click);
		
		work.performClick();
	}
	
	ImageView.OnClickListener click=new ImageView.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			work.setImageResource(R.drawable.tab_icon_work);
			attendance.setImageResource(R.drawable.tab_icon_attendance);
			reciprocal.setImageResource(R.drawable.tab_icon_reciprocal);
			setup.setImageResource(R.drawable.tab_icon_setup);
			
			switch(v.getId()) {
			case R.id.work:
				work.setImageResource(R.drawable.tab_icon_work_selected);
				home_pager.setCurrentItem(0);
				break;
			case R.id.attendance:
				attendance.setImageResource(R.drawable.tab_icon_attendance_selected);
				home_pager.setCurrentItem(1);
				break;
			case R.id.reciprocal:
				reciprocal.setImageResource(R.drawable.tab_icon_reciprocal_selected);
				home_pager.setCurrentItem(2);
				break;
			case R.id.setup:
				setup.setImageResource(R.drawable.tab_icon_setup_selected);
				home_pager.setCurrentItem(3);
				break;
			}
		}};
	
	public void addFragment() {
		if(fragment_list != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			for(Fragment f: fragment_list){
				ft.remove(f);
			}
			ft.commitAllowingStateLoss();
			ft=null;
			getSupportFragmentManager().executePendingTransactions();
		}
		fragment_list.clear();
		fragment_list.add(new WorkFragment());
		fragment_list.add(new AttendanceFragment());
		fragment_list.add(new ReciprocalFragment());
		fragment_list.add(new SetupFragment());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((SalesApplication) getApplication()).isAppRunning=false;
	}
}
