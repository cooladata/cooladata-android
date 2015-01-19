package com.cooladata.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.cooladata.android.json.JSONObject;

/**
 *
 */
public class CoolaDataTracker {

    static final String TAG = "CoolaDataTracker";

    static Context context;
    static CoolaDataTrackerOptions setupOptions;

    private static boolean initialized = false;
    private static DeviceInfo deviceInfo;
    private static UtmData mUtmData = null;
    private static String mRandomUUID;
    private static String mAdvertisingId;

    private CoolaDataTracker() {
    }


    /**
     * @param context
     * @param options
     */
    public synchronized static void setup(Context context, CoolaDataTrackerOptions options) {
        if (context == null) {
            Log.e(TAG, "Argument context cannot be null in setup");
            return;
        }
        if (options == null) {
            Log.e(TAG, "Argument options cannot be null setup");
            return;
        }
        if (TextUtils.isEmpty(options.getAppKey())) {
            Log.e(TAG, "Argument appKey cannot be null setup");
            return;
        }
        if (!initialized) {
            CoolaDataTracker.context = context.getApplicationContext();
            EventPublisher.context = context.getApplicationContext();

            CoolaDataTracker.setupOptions = options;

            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    getAdvertisingId();
                }
            });
            
            initializeDeviceInfo();
            mUtmData = new UtmData(context);
            SharedPreferences mPref = context.getSharedPreferences(Constants.PREF_KEY, Context.MODE_PRIVATE);
            mRandomUUID	= mPref.getString(Constants.USER_ID_FIELD_NAME, "");
            if (mRandomUUID.equals("")){
            	mRandomUUID = UUID.randomUUID().toString();
            	Editor editor = mPref.edit();
            	editor.putString(Constants.USER_ID_FIELD_NAME, mRandomUUID);
            	editor.commit();
            }
            initialized = true;
        }
    }
    public static void getAdvertisingId() {
    	// This should not be called on the main thread.
    	try {
    		Class AdvertisingIdClient = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
    		Method getAdvertisingInfo = AdvertisingIdClient.getMethod("getAdvertisingIdInfo", Context.class);
    		Object advertisingInfo = getAdvertisingInfo.invoke(null, context);
    		Method isLimitAdTrackingEnabled = advertisingInfo.getClass().getMethod("isLimitAdTrackingEnabled");
    		Boolean limitAdTrackingEnabled = (Boolean) isLimitAdTrackingEnabled.invoke(advertisingInfo);

    		if (limitAdTrackingEnabled) {
    			return;
    		}
    		Method getId = advertisingInfo.getClass().getMethod("getId");
    		mAdvertisingId = (String) getId.invoke(advertisingInfo);
    	} catch (ClassNotFoundException e) {
    		Log.w(TAG, "Google Play Services SDK not found!");
    	} catch (InvocationTargetException e) {
    		Log.w(TAG, "Google Play Services Invocation Target Exception!");
    	} catch (Exception e) {
    		Log.e(TAG, "Encountered an error connecting to Google Play Services", e);
    	}
    }

    private static void initializeDeviceInfo() {
        deviceInfo = new DeviceInfo(context);
    }
    
    /**
     * @param eventName
     */
    public static void trackEvent(String eventName) {
        trackEvent(eventName, null);
    }

    /**
     * @param eventName
     * @param userId
     * @param sessionId
     * @param eventProperties
     */
    public static void trackEvent(String eventName, String userId, String sessionId, Map<String, Object> eventProperties){
        checkedLogEvent(eventName,eventProperties,sessionId,null,userId);
    }

    /**
     * @param eventName
     * @param userId
     * @param properties
     */

    public static void trackEvent(String eventName, String userId, Map<String, Object> properties) {
        checkedLogEvent(eventName, properties, null, null, userId);
    }

    private static boolean checkReadPhoneStatePermission()
    {

        String permission = "android.permission.READ_PHONE_STATE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);            
    }

    private static boolean checkReadNetworkStatePermission()
    {

        String permission = "android.permission.ACCESS_NETWORK_STATE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);            
    }

    private static String getDeviceId(){
    	final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    	return tm.getDeviceId();
    	/*
        final String tmDevice, tmSerial, androidId;
        
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        */    	
    }
    
    /**
     * @param eventName
     * @param eventProperties
     */
    public static void trackEvent(String eventName, Map<String, Object> eventProperties) {
        if (CoolaDataTracker.setupOptions ==null){
            Log.e(TAG, "trying to publish event before setup");
            return;
        }
        String userId = null;
        if (eventProperties.containsKey(Constants.USER_ID_FIELD_NAME)){
        	if (!eventProperties.get(Constants.USER_ID_FIELD_NAME).equals("")){
        		userId = eventProperties.get(Constants.USER_ID_FIELD_NAME).toString();
        	}
        }

        if (userId == null) {
        	userId = CoolaDataTracker.setupOptions.getUserId();
        	if (userId == null){ // User Id was not set
        		if (mAdvertisingId != null) { // AdvertisingId
        			userId = mAdvertisingId;
        		} else if (checkReadPhoneStatePermission()){ // If has permission
        			userId = getDeviceId();
        		} else { // Random GUID
        			userId = mRandomUUID;
        		}
        		CoolaDataTracker.setupOptions.setUserId(userId);
        	}
        }

        //checkedLogEvent(eventName, eventProperties, null, null, CoolaDataTracker.setupOptions.getUserId());
        checkedLogEvent(eventName, eventProperties, null, null, userId);
    }

    private static void checkedLogEvent(final String eventName, final Map<String, Object> eventProperties, final String sessionId, final String eventId, final String userId) {
        if (TextUtils.isEmpty(eventName)) {
            Log.e(TAG, "Argument eventName cannot be null or blank in logEvent()");
            return;
        }
        if (TextUtils.isEmpty(userId)) {
            Log.e(TAG, "Argument userId cannot be null or blank in logEvent()");
            return;
        }

        if (!contextAndApiKeySet("logEvent()")) {
            return;
        }

        EventPublisher.runOnLogThread(new Runnable() {
            @Override
            public void run() {
                trackEvent(eventName, eventProperties, sessionId, eventId, userId);
            }
        });
    }

	private static long trackEvent(String eventName, Map<String, Object> eventProperties, String sessionId, String eventId, String userId) {

        JSONObject event = new JSONObject();
        try {

            // event mandatories
            event.put(Constants.EVENT_NAME_FIELD_NAME, eventName);
            event.put(Constants.USER_ID_FIELD_NAME, userId);
            event.put(Constants.EVENT_TIMESTAMP_FIELD_NAME, System.currentTimeMillis());

            // event optional
            if (!TextUtils.isEmpty(sessionId))
                event.put(Constants.SESSION_ID_FIELD_NAME, sessionId);

            if (!TextUtils.isEmpty(eventId))
                event.put(Constants.SESSION_ID_FIELD_NAME, eventId);

            // device properties
            
            event.put(Constants.TRACKER_TYPE_FIELD_NAME, Constants.TRACKER_TYPE);
            event.put(Constants.TRACKER_VERSION_FIELD_NAME, Constants.TRACKER_VERSION);
            event.put(Constants.SESSION_DUA_FIELD_NAME, System.getProperty("http.agent"));
            event.put(Constants.SESSION_DEVICE_MANUFACTURER, Build.MANUFACTURER);
            event.put(Constants.SESSION_MODEL_FIELD_NAME, Build.MODEL);
            event.put(Constants.SESSION_SCREEN_SIZE_FIELD_NAME, deviceInfo.screenSize);
            event.put(Constants.SESSION_OS_VERSION_FIELD_NAME, Build.VERSION.RELEASE);
            event.put(Constants.SESSION_OS_FIELD_NAME, deviceInfo.deviceOS);
            event.put(Constants.EVENT_TIMEZONE_OFFSET, deviceInfo.timeZoneOffset);
            event.put(Constants.SESSION_DEVICE_ORIENTATION_FIELD_NAME, deviceInfo.screenOrientation);
            event.put(Constants.SESSION_CARRIER, deviceInfo.carrier);
            event.put(Constants.SESSION_APP_ID_FIELD_NAME, deviceInfo.appId);
            event.put(Constants.SESSION_APP_VERSION_FIELD_NAME, deviceInfo.appVersion);
            
			String networkState = getNetworkState();
			if(networkState.length() > 0)
			{
				event.put(Constants.EVENT_CONNECTIVITY_STATE,  networkState);
			}

            if (mUtmData.HasInstallReferrer()){
            	event.put(Constants.SESSION_INSTALL_REFERRER,	mUtmData.getReferrerValue());
            	event.put(Constants.SESSION_UTM_CAMPAIGN, 		mUtmData.getUtmCampaign());
            	event.put(Constants.SESSION_UTM_CONTENT, 		mUtmData.getUtmContent());
            	event.put(Constants.SESSION_UTM_MEDIUM, 		mUtmData.getUtmMedium());
            	event.put(Constants.SESSION_UTM_SOURCE, 		mUtmData.getUtmSource());
            	event.put(Constants.SESSION_UTM_TERM, 			mUtmData.getUtmTerm());
            }

            // user properties
            if (eventProperties!=null && eventProperties.entrySet().size()>0) {
                for (Entry<String, Object> entry : eventProperties.entrySet()) {
                    if (entry.getValue() instanceof String) // escaping
                        event.put(entry.getKey(), com.cooladata.android.json.JSONObject.escape((String) entry.getValue()));
                    else
                        event.put(entry.getKey(), entry.getValue());
                }
            }

            // location
            Location location = DeviceInfo.getMostRecentLocation(context);
            if (location != null) {
                event.put(Constants.SESSION_LAT, location.getLatitude());
                event.put(Constants.SESSION_LONG, location.getLongitude());
                event.put(Constants.SESSION_ALT, location.getAltitude());
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return EventPublisher.logEvent(event);
    }

    /**
     *
     */
    
    private static String getNetworkState(){
    	String networkState = "";
    	
    	if (checkReadNetworkStatePermission()){
    		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

    		//For 3G check
    		boolean isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
    		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

    		if (isWifi) {
    			networkState = "WiFi";
    		} else if (isMobile) {
    			networkState = "Mobile";
    		} else {
    			networkState = "Offline";
    		}
    		/*
    		ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo networkInfo=null;
    		if (connectivityManager != null) {
    			networkInfo =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    			if (networkInfo.isAvailable()) {
    				networkState = "WiFi";
    			} else {
    				networkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    				if (networkInfo.isAvailable()) {
    					networkState = "Mobile";
    				} else {
    					networkState = "Offline";
    				}
    			}
    		} else {
    			networkState = "Offline";
    		}*/
    	}
    	return networkState;
    }
    
    public static void flush() {
        EventPublisher.uploadEvents();
    }

    private synchronized static boolean contextAndApiKeySet(String methodName) {
        if (context == null) {
            Log.e(TAG, "context cannot be null, set context with setup() before calling " + methodName);
            return false;
        }
        if (TextUtils.isEmpty(CoolaDataTracker.setupOptions.getAppKey())) {
            Log.e(TAG, "appKey cannot be null, set appKey with setup() before calling " + methodName);
            return false;
        }
        return true;
    }
}
