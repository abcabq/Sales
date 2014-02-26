package com.renyu.sales.attendance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;

public class CameraActivity extends BaseActivity {
	
	ImageView nav_back=null;
	ImageView nav_title=null;
	
	private SurfaceView camera_surfaceview = null;
	private SurfaceHolder mSurfaceHolder = null;
	private TextView camera_ok=null;
	private TextView camera_cancel=null;
	private Camera mCamera = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		
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
		nav_title.setImageResource(R.drawable.camera_title);
		
		camera_cancel=(TextView) findViewById(R.id.camera_cancel);
		camera_cancel.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				finish();
			}
		});
		camera_ok=(TextView) findViewById(R.id.camera_ok);
        camera_ok.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				mCamera.takePicture(null, null, pictureCallback);
			}
		});	
		camera_surfaceview = (SurfaceView) findViewById(R.id.camera_surfaceview);
        mSurfaceHolder = camera_surfaceview.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolderCallback());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public class SurfaceHolderCallback implements SurfaceHolder.Callback {

    	public void surfaceChanged(SurfaceHolder holder, int format, int width,
    			int height) {
    		Bitmap getpage;
    		getpage = Bitmap.createBitmap(800, 380,Bitmap.Config.ARGB_8888);
    		Canvas canvas = new Canvas(getpage);
    		canvas.drawColor(Color.LTGRAY);//这里可以进行任何绘图步骤 
    		canvas.save(Canvas.ALL_SAVE_FLAG);
    		canvas.restore();
    	}

    	public void surfaceCreated(SurfaceHolder holder) {
    		
    		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
    			for(int i=0; i<Camera.getNumberOfCameras();i++){
    				CameraInfo info = new CameraInfo();
    				Camera.getCameraInfo(i, info);
    				if(info.facing == CameraInfo.CAMERA_FACING_FRONT){
    					mCamera = Camera.open(i);
    					break;
    				}
    			}
    		}
    		if(mCamera == null){
    			mCamera = Camera.open();
    		}
    		try {
    			mCamera.setPreviewDisplay(mSurfaceHolder);
    		}catch (IOException e){
    			e.printStackTrace();
    		}
    		
    		mCamera.setDisplayOrientation(90);
    		try {
				mCamera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		try {
				mCamera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		mCamera.startPreview();    		
    	}

    	public void surfaceDestroyed(SurfaceHolder holder) {    		
    		if(mCamera != null){
    			mCamera.stopPreview();
    			mCamera.unlock();
    			mCamera.release();
    			mCamera = null;
    		}
    	}
    }
	
	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {		
		public void onPictureTaken(byte[] data, Camera camera) {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				mCamera.stopPreview();
				mCamera.unlock();
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sale";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				Matrix matrix = new Matrix();
				int j=0;
    			for(int i=0; i<Camera.getNumberOfCameras();i++){
    				CameraInfo info = new CameraInfo();
    				Camera.getCameraInfo(i, info);
    				if(info.facing == CameraInfo.CAMERA_FACING_FRONT){
    					j=1;
    					break;
    				}
    			}
    			if(j==0){
    				matrix.setRotate(90);
    			}
    			else{
    				matrix.setRotate(-90);
    			}
				Bitmap mBitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
				Bitmap mBitmap = Bitmap.createBitmap(mBitmap2, 0, 0, mBitmap2.getWidth(), mBitmap2.getHeight(), matrix, true);
				File pictureFile = new File(path+"/1.jpg");
				try{
					FileOutputStream mFileOutputStream = new FileOutputStream(pictureFile);
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, mFileOutputStream);
					mFileOutputStream.close();
				}catch(FileNotFoundException e){
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				mCamera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.startPreview();
			
			Intent intent=getIntent();
			setResult(RESULT_OK, intent);
			finish();
		}
	};
	
}
