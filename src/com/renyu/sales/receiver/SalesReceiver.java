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
		//ȫ�㲥������ɴ��ػ�����
		System.out.println("�� "+intent.getAction()+" ���ػ�����");
		//�ظ���
		if(!Util.isServiceWorked(context, "com.renyu.sales.gps.GuardService")) {
			System.out.println("�ػ����񲻴��ڣ������ػ�����");
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
