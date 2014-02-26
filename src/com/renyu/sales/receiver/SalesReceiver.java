package com.renyu.sales.receiver;

import com.renyu.sales.commons.Util;
import com.renyu.sales.gps.GuardService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SalesReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//全广播服务均可打开守护服务
		System.out.println("由 "+intent.getAction()+" 打开守护服务");
		//重复打开
		if(!Util.isServiceWorked(context, "com.renyu.sales.gps.GuardService")) {
			System.out.println("守护服务不存在，重启守护服务");
			Intent intent_guard = new Intent(context, GuardService.class);
			intent_guard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent_guard);
		}
		if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")||
				intent.getAction().equals("android.intent.action.TIME_SET")||
				intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")||
				intent.getAction().equals("android.intent.action.DATE_CHANGED")||
				Util.isTimeOffsetExists(context)==-1l) {
			Util.getTimeOffset(context);
		}
	}
	  

}
