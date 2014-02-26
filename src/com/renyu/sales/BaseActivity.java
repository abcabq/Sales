package com.renyu.sales;

import android.app.Activity;
import android.app.ProgressDialog;

public class BaseActivity extends Activity {
	
	private ProgressDialog _dialog = null;
	
	protected void showProgressDialog(String text) {
		if(_dialog == null) {
			_dialog = new ProgressDialog(this);
    	}
		if(!_dialog.isShowing()) {
			_dialog = ProgressDialog.show(this, "", text , true);
			_dialog.setCancelable(false);
			_dialog.setCanceledOnTouchOutside(false);
		}
	}
	
	protected void dismissProgressDialog() {
		if(_dialog != null &&_dialog.isShowing()) {
			_dialog.dismiss();
		}
	}
}
