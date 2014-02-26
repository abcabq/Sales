package com.renyu.sales.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParse {
	
	public static LoginModel getLoginModel(String str) {
		LoginModel model=new LoginModel();
		try {
			JSONObject obj=new JSONObject(str);
			model.setSuccess(obj.getBoolean("success"));
			model.setErrorMsg(obj.getString("errorMsg"));
			model.setUserId(obj.getInt("userId"));
			model.setHead_img(obj.getString("head_img"));
			return model;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static RegistModel getRegistModel(String str) {
		RegistModel model=new RegistModel();
		try {
			JSONObject obj=new JSONObject(str);
			model.setSuccess(obj.getBoolean("success"));
			model.setMessage(obj.getString("message"));
			return model;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<NoticeListModel> getNoticeListModelList(String str) {
		ArrayList<NoticeListModel> list=new ArrayList<NoticeListModel>();
		try {
			JSONObject obj=new JSONObject(str);
			if(!obj.getBoolean("success")) {
				return null;
			}
			JSONArray array=new JSONArray(obj.getString("data"));
			for(int i=0;i<array.length();i++) {
				NoticeListModel model=new NoticeListModel();
				JSONObject temp=array.getJSONObject(i);
				model.setContent(temp.getString("content"));
				model.setCreatetime(temp.getString("createtime"));
				model.setId(temp.getInt("id"));
				model.setTitle(temp.getString("title"));
				model.setType_name(temp.getString("type_name"));
				list.add(model);
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<DailyPaperModel> getDailyPaperModelList(String str) {
		ArrayList<DailyPaperModel> list=new ArrayList<DailyPaperModel>();
		try {
			JSONObject obj=new JSONObject(str);
			if(!obj.getBoolean("success")) {
				return null;
			}
			JSONArray array=new JSONArray(obj.getString("data"));
			for(int i=0;i<array.length();i++) {
				DailyPaperModel model=new DailyPaperModel();
				JSONObject temp=array.getJSONObject(i);
				model.setContent(temp.getString("content"));
				model.setCreatetime(temp.getString("createtime"));
				model.setId(temp.getInt("id"));
				model.setPic1(temp.getString("pic1"));
				model.setPic2(temp.getString("pic2"));
				model.setPic3(temp.getString("pic3"));
				list.add(model);
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static DailyAttendanceModel getDailyAttendanceModel(String str) {
		try {
			DailyAttendanceModel model=new DailyAttendanceModel();
			JSONObject obj=new JSONObject(str);
			model.setSuccess(obj.getBoolean("success"));
			model.setSignstate(obj.getInt("signState"));
			model.setMessage(obj.getString("message"));
			model.setTimeHHMM(obj.getString("timeHHMM"));
			model.setRequestSiginTime(obj.getString("requestSiginTime"));
			return model;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
