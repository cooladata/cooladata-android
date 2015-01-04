package com.cooladata.cooladatatestapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.cooladata.android.CoolaDataTracker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

	private class ThreadData{
		public int ThreadId;
		public String ThreadText;

		public ThreadData(int threadId){
			this.ThreadId = threadId;
			this.ThreadText = "Thread # " + this.ThreadId + " Event Count = 0";
		}

		@Override
		public String toString(){
			return ThreadText;
		}

	}


	private Button mAddEventButton;
	private Button mAddMultiEventButton;
	private Button mClearEventButton;
	private Button mStopEventButton;
	//private TextView mLogTextView;
	private EditText mTinyVersionEditText;
	private int mCounter = 0;
	private Time time;
	private String mLog = "";
	private int ThreadCount = 0;
	private int EventCount = 0;
	private boolean bStop = false;
	private boolean bCounting = false;
	private ArrayList<ThreadData> threads = new ArrayList<ThreadData>();
	private int mThreadCount = 200;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAddEventButton = (Button) findViewById(R.id.addEventButton);
		mAddEventButton.setOnClickListener(mAddEventListener);

		mAddMultiEventButton = (Button) findViewById(R.id.addMultiEventButton);
		mAddMultiEventButton.setOnClickListener(mAddMultiEventListener);
		
		mClearEventButton = (Button) findViewById(R.id.clearEventButton);
		mClearEventButton.setOnClickListener(mClearEventListener);

		mStopEventButton = (Button) findViewById(R.id.stopEventButton);
		mStopEventButton.setOnClickListener(mStopEventListener);

		mTinyVersionEditText = (EditText) findViewById(R.id.tinyVersionEditText);
		//mLogTextView = (TextView) findViewById(R.id.logTextView);
		//mLogTextView.setMovementMethod(new ScrollingMovementMethod());

		time = new Time();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		CoolaDataTracker.flush();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	View.OnClickListener mAddMultiEventListener = new OnClickListener() {
		public void onClick(View v) {
			bStop = false;
			ThreadCount ++;
			ThreadData threadData = new ThreadData(ThreadCount);
			threads.add(threadData);
			StartEvents(ThreadCount);

			mCounter++;

			//String sEventLog = mLogTextView.getText().toString() + ThreadCount + " (" + mThreadCount + ")  activity_started \n"; 
			//mLogTextView.setText(sEventLog);
		
		}

	};  

	View.OnClickListener mClearEventListener = new OnClickListener() {
		public void onClick(View v) {
			threads = new ArrayList<ThreadData>();
			ThreadCount = 0;
			mCounter = 0;
			CoolaDataTracker.flush();
			mTinyVersionEditText.setText("");
			//mLogTextView.setText("");
		}

	};     

	View.OnClickListener mStopEventListener = new OnClickListener() {
		public void onClick(View v) {
			bStop = true;
			bCounting = false;    		  
		}

	};
	
	View.OnClickListener mAddEventListener = new OnClickListener() {
		public void onClick(View v) {
			TrackEvent();
		}

	};  

	private void StartEvents(final int threadCount){
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < mThreadCount; i ++){
					if (bStop) {
						break;
					}

					final int EventCount = i;
					try{
						Thread.sleep(100);
					}
					catch (Exception e) {}
					handler.post(new Runnable() {
						@Override
						public void run() {
							for (int t = 0; t < threads.size(); t ++){
								if (threads.get(t).ThreadId ==  threadCount){
									String thraed = "Thread # " + threadCount + " Event Count = " + (EventCount + 1) + "\\" + mThreadCount;
									threads.get(t).ThreadText = thraed;
									break;
								}
							}
							TrackEvent(threadCount, EventCount);
						}
					});
				}
			}
		}).start();
	}

	private void TrackEvent(int threadCount, int eventCount){
		// event properties
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("param1", threadCount);
		properties.put("param2", eventCount);

		String tinyVersion = mTinyVersionEditText.getText().toString();
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

		// track event with properties
		//CoolaDataTracker.trackEvent("SDK test", properties);
		CoolaDataTracker.trackEvent("activity_started", properties);

		mCounter++;

		time.setToNow();
		time.format2445();
		//mLog += " "+time.format2445()+" "+"activity_started"+"\n";
		//mLogTextView.setText(mLog);
	}


	private void TrackEvent(){
		// event properties
		String tinyLog = ""; 
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("param1", "1");
		properties.put("param2", "2");

		String tinyVersion = mTinyVersionEditText.getText().toString();
		if(tinyVersion.length()>0)
		{
			try {
				int tinyVerInt = Integer.parseInt(tinyVersion);
				
				tinyLog = "(" + tinyVerInt + ")";
				properties.put("tiny_ver", tinyVerInt);
			} catch(NumberFormatException e){

			}
		}

		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(10000);
		properties.put("user_id", index);

		CoolaDataTracker.trackEvent("activity_started", properties);

		mCounter++;

		//String sEventLog = mLogTextView.getText().toString() + mCounter + " activity_started " + tinyLog + "\n"; 
		//mLogTextView.setText(sEventLog);

		time.setToNow();
		time.format2445();
	}
}
