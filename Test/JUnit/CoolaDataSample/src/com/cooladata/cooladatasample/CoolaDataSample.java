package com.cooladata.cooladatasample;

import com.cooladata.android.CoolaDataTracker;
import com.cooladata.android.CoolaDataTrackerOptions;

import android.app.Application;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;


public class CoolaDataSample extends Application {
	
	private static String COOLADATA_APP_KEY = "aknek0zuo8i4ztcmgopn5wotsceg80m9"; //valid
	//private static String COOLADATA_APP_KEY = "1234aknek0zuo8i4ztcmgopn5wotscegXXXX"; //invalid
	private CoolaDataTrackerOptions trackerOptions;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
		//TelephonyManager tm = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);

        //CoolaDataTracker.setup(this, new CoolaDataTrackerOptions(COOLADATA_APP_KEY, tm.getDeviceId(), null, true));
		
		trackerOptions = new CoolaDataTrackerOptions(COOLADATA_APP_KEY);
        CoolaDataTracker.setup(this, trackerOptions);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	public void UpdateCoolaDataUserId(String userId){
		trackerOptions.setUserId(userId);
	}

	public void DeleteCoolaDataUserId(){
		trackerOptions.setUserId(null);
	}
	
}

