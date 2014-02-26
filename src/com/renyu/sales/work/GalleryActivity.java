package com.renyu.sales.work;

import java.lang.ref.SoftReference;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import ru.truba.touchgallery.Util.CommonUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;

import com.renyu.sales.BaseActivity;
import com.renyu.sales.R;

public class GalleryActivity extends BaseActivity {

    private GalleryViewPager mViewPager;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);
        
        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, getIntent().getExtras().getStringArrayList("pic"));
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
			public void onItemChange(int currentPosition) {
				
			}
		});
        
        mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);   
        mViewPager.setCurrentItem(getIntent().getExtras().getInt("pos"));
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(CommonUtil.list_image.size()>20) {
    		Object[] obj=CommonUtil.list_image.keySet().toArray();
    		for(int i=0;i<20;i++) {
    			SoftReference<Bitmap> sr=CommonUtil.list_image.remove(obj[i]);
    			if(sr!=null) {
					Bitmap bmp=sr.get();
					if(bmp!=null&&!bmp.isRecycled()) {
						bmp.recycle();
						bmp=null;
					}
				}
    		}
    	}
    }
}
