package com.renyu.sales.work;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.DailyPaperModel;
import com.renyu.sales.model.JsonParse;

public class DailyPaperActivity extends BaseActivity {
	
	int showCount=20;
	int page=0;
	boolean isLoad=false;
	ArrayList<DailyPaperModel> data_list=null;
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	DailyPaperAdapter adapter=null;
	PullToRefreshListView daily_listview=null;
	ListView actualListView=null;
	View footerView=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_announcement);
		
		data_list=new ArrayList<DailyPaperModel>();
		
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
		nav_title.setImageResource(R.drawable.logo_title);
		nav_title.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(!isLoad) {
					page=0;
					daily_listview.manualRefresh();
					loadData();
				}
				
			}});
		
		adapter=new DailyPaperAdapter(data_list, DailyPaperActivity.this);
		daily_listview=(PullToRefreshListView) findViewById(R.id.announcement_listview);
		daily_listview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				// Do work to refresh the list here.
				if(!isLoad) {
					page=0;
					loadData();
				}
			}
		});
		// Add an end-of-list listener
		daily_listview.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				footerView.findViewById(R.id.loadMore_bar).setVisibility(View.VISIBLE);
				loadData();
			}
		});
		
		actualListView=daily_listview.getRefreshableView();
		footerView=loadFooterView();
		actualListView.setAdapter(adapter);
		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);
		
		doRefresh();
	}
	
	/**
	 * 页面需要加载完毕之后才能显示headerview
	 */
	public void doRefresh() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				nav_title.performClick();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	public void loadData() {
		isLoad=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					ArrayList<DailyPaperModel> model_list=JsonParse.getDailyPaperModelList(msg.obj.toString());
					if(model_list==null) {
						Toast.makeText(DailyPaperActivity.this, "远程连接出现异常", 3000).show();
					}
					else {
						if(page==0) {
							data_list.clear();
						}
						data_list.addAll(model_list);
						if(model_list.size()==showCount&&actualListView.getFooterViewsCount()==1) {
							actualListView.addFooterView(footerView);
						}
						else if(model_list.size()<showCount&&actualListView.getFooterViewsCount()>1) {
							actualListView.removeFooterView(footerView);
						}
						footerView.findViewById(R.id.loadMore_bar).setVisibility(View.INVISIBLE);
						page++;
						adapter.notifyDataSetChanged();
					}
				}
				else {
					Toast.makeText(DailyPaperActivity.this, "网络异常", 3000).show();
				}
				// Call onRefreshComplete when the list has been refreshed.
				daily_listview.onRefreshComplete();
				isLoad=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", "1");
				map.put("page", ""+page);
				map.put("pageSize", ""+showCount);
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/waiqin/dailyManageMobile_getDailyPaperList.do");
				if(Util.convertNull(result).equals("")) {
					m.what=-1;
				}
				else {
					m.what=1;
				}
				m.obj=result;
				handler.sendMessage(m);
			}}).start();
	}

	public View loadFooterView() {
		return LayoutInflater.from(DailyPaperActivity.this).inflate(R.layout.footer_view, null);
	}
}
