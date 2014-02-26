package com.renyu.sales.model;

public class DailyAttendanceModel {

	boolean success=false;
	int signstate=-1;
	String message="";
	String timeHHMM="";
	String requestSiginTime="";
	public String getTimeHHMM() {
		return timeHHMM;
	}
	public void setTimeHHMM(String timeHHMM) {
		this.timeHHMM = timeHHMM;
	}
	public String getRequestSiginTime() {
		return requestSiginTime;
	}
	public void setRequestSiginTime(String requestSiginTime) {
		this.requestSiginTime = requestSiginTime;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getSignstate() {
		return signstate;
	}
	public void setSignstate(int signstate) {
		this.signstate = signstate;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
