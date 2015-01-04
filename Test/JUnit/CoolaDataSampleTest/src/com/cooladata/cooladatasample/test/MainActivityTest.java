package com.cooladata.cooladatasample.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cooladata.android.CoolaDataTracker;
import com.cooladata.android.EventPublisher;
import com.cooladata.cooladatasample.CoolaDataSample;
import com.cooladata.cooladatasample.MainActivity;

import android.os.Handler;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private static String TAG = "CoolaDataSampleTest";
	//private Context	mContext;
	//private static String COOLADATA_APP_KEY = "aknek0zuo8i4ztcmgopn5wotsceg80m9"; //valid
	//private static String COOLADATA_INVALID_APP_KEY = "aknek0zuo8i4ztcmgopn5wotscegXXXX"; //invalid
	private boolean bContinue = true;
	
	private long eventCount = 0;
	private long delCount = 0;
	private long loadCount = 0;
	//private boolean bCounting;
	private CoolaDataSample mCoolaDataSample;

	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	  
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	//mContext = getActivity();
    	mCoolaDataSample = (CoolaDataSample)this.getInstrumentation().getTargetContext().getApplicationContext();
    	ResetTestData();
    }
/*
    private void DeclareCoolaData(){
        CoolaDataSample mCoolaDataSample = (CoolaDataSample)getActivity().getApplicationContext();
		TelephonyManager tm = (TelephonyManager)mContext.getSystemService(mCoolaDataSample.TELEPHONY_SERVICE);
        CoolaDataTracker.setup(mContext, new CoolaDataTrackerOptions(COOLADATA_APP_KEY, tm.getDeviceId(), null, true));
    }
*/    
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
/*
	private void FlashTestData(){
		bContinue = true;
		Handler handler1 = new Handler();
		CoolaDataTracker.flush();

		handler1.postDelayed(new Runnable() {

			@Override
			public void run() {
			}
		}, 5000);
	}
*/
    /*
	public void testWrongAPPKey(){
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchChangeAppKey;
		CountDownLatch _latchCounters;
		
		int N = 25;

		_latchChangeAppKey = new CountDownLatch(1);
		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();

			new Thread(new ChangeAPPKey(_latchChangeAppKey, COOLADATA_INVALID_APP_KEY)).start();
			_latchChangeAppKey.await();

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
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
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);

			assertEquals("eventCount", N, eventCount);
			assertTrue(delCount == 0);
			assertTrue(delCount == loadCount);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testRightAPPKey(){
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchChangeAppKey;
		CountDownLatch _latchCounters;
		
		int N = 25;

		_latchChangeAppKey = new CountDownLatch(1);
		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();

			new Thread(new ChangeAPPKey(_latchChangeAppKey, COOLADATA_APP_KEY)).start();
			_latchChangeAppKey.await();

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
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
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);

			assertEquals("eventCount", N, eventCount);
			assertTrue(delCount > 0);
			assertTrue(delCount == loadCount);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class ChangeAPPKey implements Runnable {
		private CountDownLatch _latch = null;
		private String mAppKey;
		
		public ChangeAPPKey(CountDownLatch _latch, String AppKey) {
			Log.d(TAG, ProcName + "Counters Start");
			this._latch = _latch;
			this.mAppKey = AppKey;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
		        CoolaDataSample mCoolaDataSample = (CoolaDataSample)getActivity().getApplicationContext();
				TelephonyManager tm = (TelephonyManager)mContext.getSystemService(mCoolaDataSample.TELEPHONY_SERVICE);
		        CoolaDataTracker.setup(mContext, new CoolaDataTrackerOptions(mAppKey, tm.getDeviceId(), null, true));
				Thread.sleep(2000);

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "Counters Done");
			_latch.countDown();
		}
	}
*/		
/*	
	private void AddSingleValidEvent(){
		AddSingleValidEvent("activity_started");
	}
*/
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

	private void AddSingleValidEventWithUserId(String eventName){
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

		CoolaDataTracker.trackEvent(eventName, "eventId", "CoolaDataSampleTest", properties);
		Log.d(TAG, ProcName + "Adding event finished");
	}
	
	
	private void AddSingleInValidNameEvent(){
		String ProcName = "AddSingleInValidNameEvent";
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

		CoolaDataTracker.trackEvent("XXXXX", properties);
		Log.d(TAG, ProcName + "Adding event finished");
	}

	private void AddSingleNoNameEvent(){
		String ProcName = "AddSingleNoNameEvent";
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

		CoolaDataTracker.trackEvent(null, properties);
		Log.d(TAG, ProcName + "Adding event finished");
	}

	private void AddSingleNoPropertiesEvent(){
		String ProcName = "AddSingleNoPropertiesEvent";
		Log.d(TAG, ProcName + "Adding event Started");
		CoolaDataTracker.trackEvent("activity_started", null);
		Log.d(TAG, ProcName + "Adding event finished");
	}

	public void testAddEventNoPropertiesEvent(){
		String ProcName = "testAddEventNoPropertiesEvent";
		Log.d(TAG, ProcName + "testAddEventNoPropertiesEvent");
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 25;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + loadCount;

			
			Log.d(TAG, ProcName + "Start Test " + N + " events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventNoPropertiesThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			long NewSum = 0;
			for (int j = 0; j < 3; j ++){
				Log.d(TAG, ProcName + "Check Counters #" + j);
				
				_latchCounters = new CountDownLatch(1);
				
				new Thread(new getCounters(_latchCounters)).start();
				_latchCounters.await();
				NewSum = eventCount + loadCount;
				
				if (NewSum > 0) { break; }
				
			}
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);

			assertEquals("Check Totals", N, eventCount + loadCount);
			if (delCount > 0){
				if (delCount > 0){
					assertTrue(delCount == loadCount);
				}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAddEventNoNameEvent(){
		String ProcName = "testAddEventNoNameEvent";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 25;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount;
			long OrigDelete = delCount;
			long OrigLoaded = loadCount;

			Log.d(TAG, ProcName + "Start Test " + N + " events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventNoNameThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			long NewSum = 0;
			for (int j = 0; j < 3; j ++){
				Log.d(TAG, ProcName + "Check Counters #" + j);
				
				_latchCounters = new CountDownLatch(1);
				
				new Thread(new getCounters(_latchCounters)).start();
				_latchCounters.await();
				NewSum = eventCount + delCount;
				
				if (NewSum > 0) { break; }
				
			}
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);

			assertEquals("Check Totals", 0, NewSum - OrigSum);
			assertEquals("Check Delete is 0", 0, OrigDelete - delCount);
			assertEquals("Check Loaded is 0", 0, OrigLoaded - loadCount);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAddEventInvalidEventName(){
		String ProcName = "testAddEventInvalidEventName";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 25;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount;

			Log.d(TAG, ProcName + "Start Test " + N + " events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventInvalidNameThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			
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

			long NewSum = eventCount + delCount;
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);

			assertEquals("Check Totals", N, NewSum - OrigSum);
			if (delCount > 0){
				assertTrue(delCount == loadCount);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAddEvent21EventsCheckDelete(){
		String ProcName = "testAddEvent21EventsCheckDelete";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);

		try {
			Log.d(TAG, ProcName + "Start Test " + N + " events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount;

			Log.d(TAG, ProcName + "Start Test " + N + " events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
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
			
			
			long NewSum = eventCount + delCount;
			Log.d(TAG, ProcName + "Process Ended eventCount = " + eventCount + "; delCount = " + delCount + "; loadCount = " + loadCount);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
			assertTrue(delCount > 0);
			assertTrue(delCount == loadCount);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAddEvetsWithoutUserId_UserIdNotSet(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.DeleteCoolaDataUserId();
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testAddEvetsWithUserId_UserIdNotSet(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.DeleteCoolaDataUserId();
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventWithUserThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testAddEvetsWithoutUserId_UserIdSet(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.UpdateCoolaDataUserId("CoolaData_UpdatedUserId");
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testAddEvetsWithUserId_UserIdSet(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.UpdateCoolaDataUserId("CoolaData_UpdatedUserId");
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventWithUserThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testAddEvetsWithoutUserId(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.UpdateCoolaDataUserId("CoolaData_UpdatedUserId");
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventWithUserThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void testAddEvetsWithUserId(){
		String ProcName = "testAddEvetsWithoutUserId";
		mCoolaDataSample.UpdateCoolaDataUserId("CoolaData_UpdatedUserId");
		
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 21;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test EvetsWithoutUserId events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventWithUserThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
	public void testAddEvent19EventsCheckDelete(){
		String ProcName = "testAddEvent19EventsCheckDelete";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		
		int N = 19;

		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start Test 19 events");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test 19 events. OrigSum = " + OrigSum);

			for (int i = 0; i < N; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}
			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			new Thread(new getCounters(_latchCounters)).start();
			_latchCounters.await();
			long NewSum = eventCount + delCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", 19, NewSum - OrigSum);
			
			_latchEvents = new CountDownLatch(2);
			for (int i = 0; i < 2; i++) {
				new Thread(new AddEventThread(_latchEvents)).start();
			}

		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void testAddEventAllTypes(){
		String ProcName = "testAddEventAllTypes";
		CountDownLatch _latchReset;
		CountDownLatch _latchEvents;
		CountDownLatch _latchCounters;
		int N = 27;
		_latchReset = new CountDownLatch(1);
		_latchEvents = new CountDownLatch(N);
		_latchCounters = new CountDownLatch(1);

		try {
			Log.d(TAG, ProcName + "Start different events Names");
			
			ResetTestData(_latchReset);
			_latchReset.await();
			long OrigSum = eventCount + delCount + loadCount;

			Log.d(TAG, ProcName + "Start Test 27 events. OrigSum = " + OrigSum);

			new Thread(new AddEventThread(_latchEvents, "Add Item")).start(); 			// 1
			new Thread(new AddEventThread(_latchEvents, "activit_started")).start();	// 2
			new Thread(new AddEventThread(_latchEvents, "activit_startedd")).start();	// 3
			new Thread(new AddEventThread(_latchEvents, "add_item")).start();			// 4
			new Thread(new AddEventThread(_latchEvents, "error")).start();				// 5
			new Thread(new AddEventThread(_latchEvents, "retry_clicekd")).start();		// 6
			new Thread(new AddEventThread(_latchEvents, "right_clicekd")).start();		// 7
			new Thread(new AddEventThread(_latchEvents, "second_activity")).start();	// 8
			new Thread(new AddEventThread(_latchEvents, "wrong_clicked")).start();		// 9

			new Thread(new AddEventThread(_latchEvents, "Add Item")).start(); 			// 1
			new Thread(new AddEventThread(_latchEvents, "activit_started")).start();	// 2
			new Thread(new AddEventThread(_latchEvents, "activit_startedd")).start();	// 3
			new Thread(new AddEventThread(_latchEvents, "add_item")).start();			// 4
			new Thread(new AddEventThread(_latchEvents, "error")).start();				// 5
			new Thread(new AddEventThread(_latchEvents, "retry_clicekd")).start();		// 6
			new Thread(new AddEventThread(_latchEvents, "right_clicekd")).start();		// 7
			new Thread(new AddEventThread(_latchEvents, "second_activity")).start();	// 8
			new Thread(new AddEventThread(_latchEvents, "wrong_clicked")).start();		// 9

			new Thread(new AddEventThread(_latchEvents, "Add Item")).start(); 			// 1
			new Thread(new AddEventThread(_latchEvents, "activit_started")).start();	// 2
			new Thread(new AddEventThread(_latchEvents, "activit_startedd")).start();	// 3
			new Thread(new AddEventThread(_latchEvents, "add_item")).start();			// 4
			new Thread(new AddEventThread(_latchEvents, "error")).start();				// 5
			new Thread(new AddEventThread(_latchEvents, "retry_clicekd")).start();		// 6
			new Thread(new AddEventThread(_latchEvents, "right_clicekd")).start();		// 7
			new Thread(new AddEventThread(_latchEvents, "second_activity")).start();	// 8
			new Thread(new AddEventThread(_latchEvents, "wrong_clicked")).start();		// 9

			_latchEvents.await();
			Log.d(TAG, ProcName + "Events Added");
			
			
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
			long NewSum = eventCount + loadCount;
			Log.d(TAG, ProcName + "Process Ended NewSum = " + NewSum);
			
			assertEquals("Check Totals", N, NewSum - OrigSum);
			assertTrue(loadCount == delCount);
			assertTrue(delCount > 0);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
	
	private class AddEventWithUserThread implements Runnable {
		String ProcName = "AddEventThread";
		private CountDownLatch _latch = null;
		private String mEventName = "activity_started"; 
		
		public AddEventWithUserThread(CountDownLatch _latch) {
			this._latch = _latch;
		}
		
		
		@Override
		public void run() {
			try {
				AddSingleValidEventWithUserId(mEventName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "New Event Added");
			_latch.countDown();
		}
	}

	private class AddEventInvalidNameThread implements Runnable {
		String ProcName = "AddEventInvalidNameThread";
		private CountDownLatch _latch = null;

		public AddEventInvalidNameThread(CountDownLatch _latch) {
			this._latch = _latch;
		}

		@Override
		public void run() {
			try {
				AddSingleInValidNameEvent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "New Event Added");
			_latch.countDown();
		}
	}

	private class AddEventNoNameThread implements Runnable {
		String ProcName = "AddEventNoNameThread";
		private CountDownLatch _latch = null;

		public AddEventNoNameThread(CountDownLatch _latch) {
			this._latch = _latch;
		}

		@Override
		public void run() {
			try {
				AddSingleNoNameEvent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "New Event Added");
			_latch.countDown();
		}
	}

	private class AddEventNoPropertiesThread implements Runnable {
		String ProcName = "AddEventNoPropertiesThread";
		private CountDownLatch _latch = null;

		public AddEventNoPropertiesThread(CountDownLatch _latch) {
			this._latch = _latch;
		}

		@Override
		public void run() {
			try {
				AddSingleNoPropertiesEvent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(TAG, ProcName + "New Event Added");
			_latch.countDown();
		}
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
