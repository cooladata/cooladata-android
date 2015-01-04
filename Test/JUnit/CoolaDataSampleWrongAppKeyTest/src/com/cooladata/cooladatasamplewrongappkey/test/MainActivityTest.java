package com.cooladata.cooladatasamplewrongappkey.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.cooladata.android.CoolaDataTracker;
import com.cooladata.android.EventPublisher;
import com.cooladata.cooladatasamplewrongappkey.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	
	private static String TAG = "CoolaDataSampleTestWongAppKey";
	
	private long eventCount = 0;
	private long delCount = 0;
	private long loadCount = 0;
	private boolean bContinue;

	public MainActivityTest() {
		super(MainActivity.class);
	}

	public void testWrongAPPKey(){
		String ProcName = "testWrongAPPKey";
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
