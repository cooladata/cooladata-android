package com.cooladata.android;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

public class Thresholds {
	private Context	mContext;
	private int		DeviceMemoryLimit;
	
	public Thresholds(Context context){
		mContext = context;
		initMemoryThresholds();
		if (DeviceMemoryLimit <= 24) {
			/* Low memory devices should store less events in the DB */
			Constants.EVENT_MAX_COUNT				= 500;
			Constants.EVENT_UPLOAD_PERIOD_MILLIS 	= 30 * 1000;
		} else {
			Constants.EVENT_MAX_COUNT				= 1000;
			Constants.EVENT_UPLOAD_PERIOD_MILLIS 	= 30 * 1000;
		}
	}
	
	private void initMemoryThresholds(){
		ActivityManager am 	= (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT < 5){
			DeviceMemoryLimit = 0; // Older devices will be considered as low memory devices.
		} else {
			DeviceMemoryLimit	= am.getMemoryClass(); // Available from API level 5 (Android 2.0)
		}
	}
	
}
