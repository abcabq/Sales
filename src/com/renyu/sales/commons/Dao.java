package com.renyu.sales.commons;

import java.util.ArrayList;

import com.renyu.sales.model.GPSModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dao extends SQLiteOpenHelper {
	
	static String DATABASE_NAME="gps_db";
	static int DATABASE_VERSION=1;
	
	static String _ID="_id";
	static String TABLE_NAME="gpstable";
	static String LAT="lat";
	static String LON="lon";
	static String INFO="info";
	static String TIME="time";
	static String UPLOAD="upload";
	
	static Dao dao=null;
	
	public static Dao getInstance(Context context) {
		if(dao==null) {
			dao=new Dao(context);
		}
		return dao;
	}

	public Dao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql="create table if not exists "+TABLE_NAME+"(_ID integer primary key autoincrement not null, "+LAT+" double, "+LON+" double, "+INFO+" text, "+TIME+" text, "+UPLOAD+" integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 插入GPS坐标
	 * @param lat
	 * @param lon
	 * @param info
	 * @param time
	 */
	public void insertGPS(double lat, double lon, String info, String time) {
		synchronized (this) {
			SQLiteDatabase db=this.getWritableDatabase();
			String sql="insert into "+TABLE_NAME+"("+LAT+", "+LON+", "+INFO+", "+TIME+", "+UPLOAD+") values("+lat+","+lon+",'"+info+"','"+time+"',0)";
			db.execSQL(sql);
			db.close();
		}
	}
	
	/**
	 * 读取新一条GPS记录
	 * @return
	 */
	public GPSModel readLastGPS() {
		synchronized (this) {
			SQLiteDatabase db=this.getReadableDatabase();
			Cursor cs=db.query(TABLE_NAME, null, null, null, null, null, _ID+" desc");
			cs.moveToFirst();
			if(cs.getCount()>0) {
				cs.moveToPosition(0);
				GPSModel model=new GPSModel();
				model.setLatitude(""+cs.getDouble(1));
				model.setLongitude(""+cs.getDouble(2));
				model.setInfo(cs.getString(3));
				model.setTime(cs.getString(4));
				return model;
			}
			return null;
		}
	}
	
	/**
	 * 获取GPS待上传集合
	 * @return
	 */
	public ArrayList<GPSModel> getAllUploadGPSModelList() {
		synchronized (this) {
			ArrayList<GPSModel> list=new ArrayList<GPSModel>();
			SQLiteDatabase db=this.getReadableDatabase();
			Cursor cs=db.query(TABLE_NAME, null, UPLOAD+"=0", null, null, null, null);
			cs.moveToFirst();
			if(cs.getCount()>0) {
				for(int i=0;i<cs.getCount();i++) {
					cs.moveToPosition(i);
					GPSModel model=new GPSModel();
					model.setId(cs.getInt(0));
					model.setLatitude(""+cs.getDouble(1));
					model.setLongitude(""+cs.getDouble(2));
					model.setInfo(cs.getString(3));
					model.setTime(cs.getString(4));
					list.add(model);
				}
			}
			return list;
		}
	}

}
