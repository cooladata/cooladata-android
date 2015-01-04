package com.cooladata.cooladatasample.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cooladata.android.CoolaDataTracker;
import com.cooladata.android.CoolaDataTrackerOptions;
import com.cooladata.android.EventPublisher;
import com.cooladata.cooladatasample.CoolaDataSample;
import com.cooladata.cooladatasample.MainActivity;

public class ConnectionTests extends ActivityInstrumentationTestCase2<MainActivity> {
	private Context	mContext;
	
	private static String TAG = "CoolaDataSampleTest";
	private static String COOLADATA_APP_KEY = "aknek0zuo8i4ztcmgopn5wotsceg80m9"; //valid
	private boolean bContinue = true;
	private long eventCount = 0;
	private long delCount = 0;
	private long loadCount = 0;

	public ConnectionTests(Class<MainActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}
	private void ResetTestData(){
		String ProcName = "ResetTestData";
		bContinue = true;
		Log.d(TAG, ProcName + "ResetTestData Starts");
		Handler handler1 = new Handler();
		do {
			CoolaDataTracker.flush();
			EventPublisher.resetDelCount();
			EventPublisher.resetDelMaxCount();
			EventPublisher.resetLoadCount();

			eventCount = EventPublisher.getEventCount();
			delCount = EventPublisher.getDelCount();
			loadCount = EventPublisher.getLoadCount();

			if ((eventCount + delCount + loadCount) == 0){
				bContinue = false;
			} else {
				handler1.postDelayed(new Runnable() {

					@Override
					public void run() {
					}
				}, 1000);
			}
		} while (bContinue);
		Log.d(TAG, ProcName + "ResetTestData Done");
	}

	private void ResetTestData(CountDownLatch _latch){
		String ProcName = "ResetTestData";
		bContinue = true;
		Log.d(TAG, ProcName + "ResetTestData Starts");
		Handler handler1 = new Handler();
		do {
			CoolaDataTracker.flush();
			EventPublisher.resetDelCount();
			EventPublisher.resetDelMaxCount();
			EventPublisher.resetLoadCount();

			eventCount = EventPublisher.getEventCount();
			delCount = EventPublisher.getDelCount();
			loadCount = EventPublisher.getLoadCount();

			if ((eventCount + delCount + loadCount) == 0){
				bContinue = false;
			} else {
				handler1.postDelayed(new Runnable() {

					@Override
					public void run() {
					}
				}, 1000);
			}
		} while (bContinue);
		Log.d(TAG, ProcName + "ResetTestData Done");
		_latch.countDown();
	}

    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	mContext = getActivity();
    	ResetTestData();
    }

    private void DeclareCoolaData(){
        CoolaDataSample mCoolaDataSample = (CoolaDataSample)getActivity().getApplicationContext();
		TelephonyManager tm = (TelephonyManager)mContext.getSystemService(mCoolaDataSample.TELEPHONY_SERVICE);
        CoolaDataTracker.setup(mContext, new CoolaDataTrackerOptions(COOLADATA_APP_KEY, tm.getDeviceId(), null, true));
    }
	
    public void testConnectionProblems() {
		String ProcName = "testConnectionProblems";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		CountDownLatch _latchMobileData;

		int N = 25;
		Log.d(TAG, ProcName + "Started");
		
		DeclareCoolaData();
    	try {
    		boolean bMobileState = getMobileDataEnabled();

    		_latchMobileData = new CountDownLatch(1);
	    	setMobileDataEnabled(false, _latchMobileData);
	    	_latchMobileData.await();

	    	_latchEvents = new CountDownLatch(N);

	    	for (int i = 0; i < N; i ++){
	    		new Thread(new AddEventThread(_latchEvents)).start();
	    	}
	    	_latchEvents.await();
			Log.d(TAG, ProcName + N + " Events Ended");

			long NewSum = 0;
			for (int j = 0; j < 3; j ++){
				Log.d(TAG, ProcName + "Check Counters #" + j);
				
				_latchCounters = new CountDownLatch(1);
				
				new Thread(new getCounters(_latchCounters)).start();
				_latchCounters.await();
				NewSum = eventCount + delCount;
		    	eventCount = EventPublisher.getEventCount();
				
				if (NewSum > 0) { break; }
				
			}
			Log.d(TAG, ProcName + "eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);
	    	
    		assertEquals(N, eventCount);
    		assertEquals(0, delCount);
    		assertEquals(0, loadCount);
	    	
    		_latchMobileData = new CountDownLatch(1);
	    	setMobileDataEnabled(true, _latchMobileData);
	    	_latchMobileData.await();
	    	
	    	_latchEvents = new CountDownLatch(1);
    		new Thread(new AddEventThread(_latchEvents)).start();
	    	_latchEvents.await();

			for (int j = 0; j < 3; j ++){
				Log.d(TAG, ProcName + "Check delete #" + j + 1);
				if (delCount == 0){
					_latchCounters = new CountDownLatch(1);
					new Thread(new getCounters(_latchCounters)).start();
					_latchCounters.await();
				} else {
					break;
				}
			}
			Log.d(TAG, ProcName + "eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);
    		
    		assertEquals(N + 1, eventCount);
    		assertEquals(0, delCount);
    		assertEquals(0, loadCount);
    		
	    	if (!bMobileState){
	    		setMobileDataEnabled(bMobileState, _latchMobileData);
	    	}
    	} catch (Exception e) {
			
			e.printStackTrace();
		}
    	

    	Log.d(TAG, ProcName + "Done");
    }

	public void testNoWifi(){
		String ProcName = "testNoWifi";
    	//DeclareCoolaData();
		CountDownLatch _latchReset;
		CountDownLatch _latchWifi;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		Log.d(TAG, ProcName + "Test Started");
		
		int N = 25;
		
		_latchReset = new CountDownLatch(1);
		_latchWifi = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);

		try {
			ResetTestData(_latchReset);
			_latchReset.await();

			int WifiState = SetWiFiSate(false, _latchWifi);
			_latchWifi.await();
		
			Log.d(TAG, ProcName + "eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);
			long eventOrigCount = EventPublisher.getEventCount();
			long delOrigCount = EventPublisher.getDelCount();

			Log.d(TAG, ProcName + "Loading " + N + "Events");
			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			
			Log.d(TAG, ProcName + "Check counts");
			for (int j = 0; j < 3; j ++){
				if (delCount == 0){
					_latchCounters = new CountDownLatch(1);
					new Thread(new getCounters(_latchCounters)).start();
					_latchCounters.await();
				} else {
					break;
				}
			}
			Log.d(TAG, ProcName + "eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);
		
			assertEquals("Check Totals", N, (eventCount + delCount) - (eventOrigCount + delOrigCount));
			
			_latchWifi = new CountDownLatch(1);
			SetWiFiSate((WifiState == 1), _latchWifi);
			_latchWifi.await();
		
			Log.d(TAG, ProcName + "Test Ended");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int SetWiFiSate(boolean state, CountDownLatch _latch){
		String ProcName = "SetWiFiSate";
		Log.d(TAG, ProcName + "SetWiFiSate");
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE); 
		int OrigWifiState = wifiManager.getWifiState();
		wifiManager.setWifiEnabled(state);
		
		_latch.countDown();
		return OrigWifiState;
	}

	private boolean getMobileDataEnabled() throws Exception {
		String ProcName = "getMobileDataEnabled";
		Log.d(TAG, ProcName + "getMobileDataEnabled");
	    ConnectivityManager mcm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class ownerClass = mcm.getClass();
	    Method method = ownerClass.getMethod("getMobileDataEnabled");
	    boolean result = (Boolean)method.invoke(mcm);
	    
	    return result;
	}

	private void setMobileDataEnabled(boolean enabled, CountDownLatch _latchMobileData) throws Exception {
		String ProcName = "setMobileDataEnabled";
		Log.d(TAG, ProcName + "setMobileDataEnabled");
	    ConnectivityManager mcm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    Class ownerClass = mcm.getClass();
	    ownerClass.getMethod("setMobileDataEnabled",boolean.class).invoke(mcm, enabled);
	    _latchMobileData.countDown();
	}

	private class AddEventThread implements Runnable {
		String ProcName = "AddEventThread";
		private CountDownLatch _latch = null;
		private String mEventName = "activity_started"; 
		
		public AddEventThread(CountDownLatch _latch) {
			this._latch = _latch;
		}

		public AddEventThread(CountDownLatch _latch, String eventName) {
			this._latch = _latch;
			this.mEventName = eventName;
		}
		
		
		@Override
		public void run() {
			try {
				AddSingleValidEvent(mEventName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "New Event Added");
			_latch.countDown();
		}
	}
	
	private void AddSingleValidEvent(String eventName){
		String ProcName = "AddSingleValidEvent";
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("param1", "1");
		properties.put("param2", "2");
		String tinyVersion = "999";

		Log.d(TAG, ProcName + "AddSingleValidEvent tinyVersion = " + tinyVersion);
		if(tinyVersion.length()>0)
		{
			try {
				int tinyVerInt = Integer.parseInt(tinyVersion);
				properties.put("tiny_ver", tinyVerInt);
			} catch(NumberFormatException e){

			}
		}

		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(10000);
		properties.put("user_id", index);

		CoolaDataTracker.trackEvent(eventName, properties);
		Log.d(TAG, ProcName + "Adding event finished");
	}

	private class getCounters implements Runnable {
		String ProcName = "getCounters";
		private CountDownLatch _latch = null;
		
		public getCounters(CountDownLatch _latch) {
			Log.d(TAG, ProcName + "Counters Start");
			this._latch = _latch;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
		
				eventCount = EventPublisher.getEventCount();
				delCount = EventPublisher.getDelCount();
				loadCount = EventPublisher.getLoadCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "Counters Done");
			_latch.countDown();
		}
	}
}
