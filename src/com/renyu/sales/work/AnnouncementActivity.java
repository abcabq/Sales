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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;
import com.renyu.sales.SalesApplication;
import com.renyu.sales.commons.Util;
import com.renyu.sales.model.JsonParse;
import com.renyu.sales.model.NoticeListModel;

public class AnnouncementActivity extends BaseActivity {
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	PullToRefreshListView announcement_listview=null;
	ListView actualListView=null;
	View footerView=null;
	SimpleAdapter adapter=null;
	
	ArrayList<HashMap<String, String>> data_list=null;
	int page=0;
	//每页显示个数
	int showCount=20;
	boolean isLoad=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_announcement);
		
		data_list=new ArrayList<HashMap<String, String>>();
		
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
					announcement_listview.manualRefresh();
					loadData();
				}
			}});
		
		adapter=new SimpleAdapter(AnnouncementActivity.this, data_list, R.layout.adapter_announcement, new String[]{"announcement_type", 
				"announcement_name", "announcement_date", "announcement_title"}, new int[]{R.id.announcement_type, R.id.announcement_name,
				R.id.announcement_date, R.id.announcement_title});
		announcement_listview=(PullToRefreshListView) findViewById(R.id.announcement_listview);
		announcement_listview.setOnRefreshListener(new OnRefreshListener<ListView>() {

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
		announcement_listview.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				footerView.findViewById(R.id.loadMore_bar).setVisibility(View.VISIBLE);
				loadData();
			}
		});
		
		actualListView=announcement_listview.getRefreshableView();
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
					ArrayList<NoticeListModel> model_list=JsonParse.getNoticeListModelList(msg.obj.toString());
					if(model_list==null) {
						Toast.makeText(AnnouncementActivity.this, "远程连接出现异常", 3000).show();
					}
					else {
						if(page==0) {
							data_list.clear();
						}
						for(int i=0;i<model_list.size();i++) {
							HashMap<String, String> map=new HashMap<String, String>();
							map.put("announcement_type", model_list.get(i).getType_name());
							map.put("announcement_name", model_list.get(i).getContent());
							map.put("announcement_date", model_list.get(i).getCreatetime());
							map.put("announcement_title", model_list.get(i).getTitle());
							data_list.add(map);
						}
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
					Toast.makeText(AnnouncementActivity.this, "网络异常", 3000).show();
				}				
				// Call onRefreshComplete when the list has been refreshed.
				announcement_listview.onRefreshComplete();
				isLoad=true;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("userid", Util.getUserInfo(AnnouncementActivity.this).get(4));
				map.put("page", ""+page);
				map.put("pageSize", ""+showCount);
				String result=Util.getData(map, ((SalesApplication) getApplication()).requestIp+"/waiqin/dailyManageMobile_getNoticeList.do");
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
		return LayoutInflater.from(AnnouncementActivity.this).inflate(R.layout.footer_view, null);
	}

}
