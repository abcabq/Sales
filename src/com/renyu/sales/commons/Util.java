package com.renyu.sales.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.renyu.sales.R;
import com.renyu.sales.model.City;
import com.renyu.sales.model.Province;

public class Util {
	
	/**
	 * 保存当前系统版本
	 * @param context
	 */
	public static void setCurrentVersion(Context context) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		PackageManager manager=context.getPackageManager();		
		try {
			PackageInfo pi = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			editor.putInt("versionCode", pi.versionCode);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editor.commit();
	}
	
	/**
	 * 判断当前版本是新安装还是更新之后的版本
	 * @param context
	 * @return
	 */
	public static boolean isNewInstallOrUpdate(Context context) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		PackageManager manager=context.getPackageManager();
		try {
			PackageInfo pi = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			if(sp.getInt("versionCode", 0)<pi.versionCode) {
				return true;
			}
			else {
				return false;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * GPS日志
	 * @param addrStr
	 * @param longitude
	 * @param latitude
	 */
	public static void writeLog(String addrStr, String longitude, String latitude) {
		File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale");
		if(!file.exists()) {
			file.mkdirs();
		}
		File file_=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale/log.txt");
		if(!file_.exists()) {
			try {
				file_.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			RandomAccessFile raf=new RandomAccessFile(file_, "rw");
			long file_length=file_.length();
			raf.seek(file_length);
			SimpleDateFormat formet=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
			String str=addrStr+" "+longitude+" "+latitude+" "+formet.format(new Date(System.currentTimeMillis()))+"\n";
			raf.write(str.getBytes());
			raf.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 判断服务是否存在
	 * @param context
	 * @return
	 */
	public static boolean isServiceWorked(Context context, String serviceName) {  
		ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);  
		for(int i = 0 ; i<runningService.size();i++) {  
			if(runningService.get(i).service.getClassName().toString().equals(serviceName)) {  
				return true;  
			}  
		}  
		return false;  
	}
	
	public static String getData(HashMap<String, String> map, String url) {
		ArrayList<NameValuePair> params=new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it=map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, String> entry=it.next();
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		HttpPost post=new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));//设置post参数 并设置编码格式
			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			DefaultHttpClient client=new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			HttpResponse resp=client.execute(post);
			if(resp.getStatusLine().getStatusCode()==200) {
				return EntityUtils.toString(resp.getEntity());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
		return null;
	}
	
	/**
     * 获取唯一机器码
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
    	try {
    		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        	return tm.getDeviceId();
    	} catch(Exception e) {
    		return "";
    	}
   	
    }
    
    /**
     * 获取wifi mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
    	try {
    		String macAddress = "";
    	    WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	    WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
    	    if (null != info) {
    	        macAddress = info.getMacAddress();
    	    }
    	    if(!Util.convertNull(macAddress).equals("")) {
    	    	if(macAddress.indexOf(":")!=-1) {
    	    		macAddress=macAddress.replace(":", "");
    	    		return macAddress;
    	    	}
    	    }
    	    return "";
    	} catch(Exception e) {
    		return "";
    	}   	
	}
    
    public static String convertNull(String returnValue) {
        try {
            returnValue = (returnValue==null||(returnValue!=null&&returnValue.equals("null")))?"":returnValue;
        } catch (Exception e) {
            returnValue = "";
        }
        return returnValue;
    }
    
    /**
     * 将本地数据库文件保存到数据库中
     * @param context
     */
    public static void saveToSdCard(Context context) {       
    	final String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale";    
    	String DATABASE_FILENAME = "sales.db";
    	try {
			String databaseFilename=DATABASE_PATH+"/"+DATABASE_FILENAME;            
			File dir = new File(DATABASE_PATH);            
			if(!dir.exists()) {
				dir.mkdir(); 
			}               				           
			if(!(new File(databaseFilename)).exists()) {               
				InputStream is = context.getResources().openRawResource(R.raw.sales);                
				FileOutputStream fos = new FileOutputStream(databaseFilename);                
				byte[] buffer = new byte[8192];                
				int count = 0;                
				while ((count = is.read(buffer)) > 0) {                    
					fos.write(buffer, 0, count);                
				}                
				fos.close();                
				is.close();            
			}            			      
		} catch (Exception e) {           
			e.printStackTrace();        
		}        
	}
    
    /**
	 * 获取省信息
	 * @param provinceId
	 * @return
	 */
	public static ArrayList<Province> getProvinceListInfo() {
		String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale";    
		String DATABASE_FILENAME = "sales.db";
		ArrayList<Province> pro_list=new ArrayList<Province>();
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH+"/"+DATABASE_FILENAME, null); 
		Cursor cs=db.query("province", null, null, null, null, null, null);
		cs.moveToFirst();
		int count=cs.getCount();
		for(int i=0;i<count;i++) {
			cs.moveToPosition(i);
			Province prov=new Province(cs.getInt(0), cs.getString(1), cs.getInt(6));
			pro_list.add(prov);
		}
		cs.close();
		db.close();
		return pro_list;
	}
	
	/**
	 * 获取城市信息
	 * @param provinceId
	 * @return
	 */
	public static ArrayList<City> getCityListInfo(int provinceId) {
		ArrayList<City> list=new ArrayList<City>();
		String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale";    
		String DATABASE_FILENAME = "sales.db";
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH+"/"+DATABASE_FILENAME, null); 
		Cursor cs=db.query("city", null, "provinceId=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		cs.moveToFirst();
		for(int i=0;i<cs.getCount();i++) {
			cs.moveToPosition(i);
			City city=new City(cs.getInt(0), cs.getString(1), cs.getInt(2));
			list.add(city);
		}
		cs.close();
		db.close();
		return list;
	}
	
	/**
	 * 获取城市ID
	 * @param cityName
	 * @return
	 */
	public static int getCityId(String cityName) {
		City city=null;
		String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale";    
		String DATABASE_FILENAME = "sales.db";
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH+"/"+DATABASE_FILENAME, null); 
		Cursor cs=db.query("city", null, "name=?", new String[]{cityName}, null, null, null);
		cs.moveToFirst();
		if(cs.getCount()>0) {
			cs.moveToPosition(0);
			city=new City(cs.getInt(0), cs.getString(1), cs.getInt(2));
		}		
		cs.close();
		db.close();
		return city.getId();
	}
	
	/**
	 * 保存用户登录信息
	 * @param context
	 * @param userId
	 * @param head_img
	 * @param loginname
	 * @param loginpwd
	 */
	public static void saveUserInfo(Context context, int userId, String head_img, String loginname, String loginpwd, boolean remember_pass, boolean auto_login) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putInt("userId", userId);
		editor.putString("head_img", head_img);
		editor.putString("loginname", loginname);
		editor.putString("loginpwd", loginpwd);
		editor.putBoolean("remember_pass", remember_pass);
		editor.putBoolean("auto_login", auto_login);
		editor.commit();
	}
	
	/**
	 * 获取用户登录信息
	 * @param context
	 * @param loginname
	 * @param loginpwd
	 * @return
	 */
	public static ArrayList<String> getUserInfo(Context context) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		ArrayList<String> user_list=null;
		if(!sp.getString("loginname", "").equals("")) {
			user_list=new ArrayList<String>();
			user_list.add(sp.getString("loginname", ""));
			user_list.add(sp.getString("loginpwd", ""));
			user_list.add(sp.getBoolean("remember_pass", false)?"true":"false");
			user_list.add(sp.getBoolean("auto_login", false)?"true":"false");
			user_list.add(""+sp.getInt("userId", -1));
		}		
		return user_list;
	}
	
	/**
	 * 正则判断手机号
	 * @param value
	 * @return
	 */
	public static boolean checkPhoneNum(String value) {
		String regExp="^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";  
		Pattern p=Pattern.compile(regExp);  
		Matcher m=p.matcher(value);
		return m.find();
	}
	
	/**
	 * 判断是否为邮箱
	 * @param name
	 * @return
	 */
	public static boolean checkEmail(String name) {
		String str="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";  
		if(name.matches(str)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 插入未上传的gps坐标
	 * @param context
	 * @param id
	 */
	public static void insertMissUploadGPS(Context context, String id) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		String gps_db_id_temp=sp.getString("gps_db_id", "");
		editor.putString("gps_db_id", gps_db_id_temp+id+"&");
		editor.commit();
	}
	
	/**
	 * 获取全部未上传的gps坐标id
	 * @param context
	 * @return
	 */
	public ArrayList<String> getMissUploadGPS(Context context) {
		ArrayList<String> list=new ArrayList<String>();
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		String gps_db_id_temp=sp.getString("gps_db_id", "");
		if(gps_db_id_temp.indexOf("&")!=-1) {
			String[] temp=gps_db_id_temp.split("&");
			for(int i=0;i<temp.length;i++) {
				if(!temp[i].equals("")) {
					list.add(temp[i]);
				}
			}
		}
		return list;
	}
	
	/**
	 * 得到与北京时间相比的时间差值
	 * @param context
	 */
	public static void getTimeOffset(final Context context) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor=sp.edit();
				editor.putLong("ld", msg.getData().getLong("ld"));
				editor.commit();
				System.out.println("时间差值为："+msg.getData().getLong("ld"));
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try {
					URL url = new URL("http://www.bjtime.cn");//取得资源对象
					URLConnection uc=url.openConnection();//生成连接对象
					uc.connect(); //发出连接
					long ld=uc.getDate(); //取得网站日期时间
					if(ld!=0) {
						long result=System.currentTimeMillis()-ld;
						Message m=new Message();
						Bundle bundle=new Bundle();
						bundle.putLong("ld", result);
						m.setData(bundle);
						handler.sendMessage(m);	
					}									
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}}).start();
	}
	
	/**
	 * 获取时间差值
	 * @param context
	 * @return
	 */
	public static long isTimeOffsetExists(final Context context) {
		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
		long temp=sp.getLong("ld", -1l);
		return temp;
	}
	
	/**
	 * 从view 得到图片
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
	}
	
	/**
     * 根据图片的文件信息,返回图片的角度;
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {

        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
 
            }
        }
        return degree;
    }
    
    /**
     * 压缩图片
     * @param bmp
     * @param path
     * @return
     */
  	public static Bitmap compressBmp(String path) {
  		Bitmap bmp=BitmapFactory.decodeFile(path);
  		int width=bmp.getWidth();
  		int height=bmp.getHeight();
  		int size=0;
  		Matrix m=new Matrix(); 
  		if(!path.equals("")) {
  			m.setRotate(getExifOrientation(path), width/2, height/2);
  		}
  		if(width>200||height>200) {
  			if(width>height) {
  				size=width/200+1;
  			}
  			else {
  				size=height/200+1;
  			}			
  			m.postScale(1f/size, 1f/size);
  		}
  		else {
  			m.postScale(1.01f, 1.01f);			
  		}
  		return Bitmap.createBitmap(bmp, 0, 0, width, height, m, true);
  	}
  	
  	/**
  	 * 保存屏幕信息
  	 * @param context
  	 * @param height
  	 * @param width
  	 * @param density
  	 */
  	public static void setScreenInfo(Context context, int height, int width, float density) {
  		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
  		SharedPreferences.Editor editor=sp.edit();
  		editor.putInt("height", height);
  		editor.putInt("width", width);
  		editor.putFloat("density", density);
  		editor.commit();
  	}
  	
  	/**
  	 * 获取屏幕信息
  	 * @param context
  	 * @return
  	 */
  	public static LinkedList<String> getScreenInfo(Context context) {
  		LinkedList<String> list=new LinkedList<String>();
  		SharedPreferences sp=context.getSharedPreferences("sales", Activity.MODE_PRIVATE);
  		list.add(""+sp.getInt("height", 0));
  		list.add(""+sp.getInt("width", 0));
  		list.add(""+sp.getFloat("density", 0f));
  		return list;
  	}

}
