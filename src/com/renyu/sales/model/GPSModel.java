package com.renyu.sales.model;

public class GPSModel {
	
	int id=-1;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	String longitude="";
	String latitude="";
	String info="";
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	String time="";

}
