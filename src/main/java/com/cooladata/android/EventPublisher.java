package com.cooladata.android;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.cooladata.android.json.JSONArray;
import com.cooladata.android.json.JSONObject;
import com.cooladata.android.json.parser.JSONParser;

public class EventPublisher {

    final static JSONParser jsonParser = new JSONParser();


    static Context context;
    static WorkerThread httpThread = new WorkerThread("httpThread");
    static WorkerThread logThread = new WorkerThread("logThread");
    private static long mDelCount = 0; 
    private static long mDelMaxCount = 0;
    private static long mLoadCount = 0;

    static {
        httpThread.setDaemon(false);
        httpThread.start();
        logThread.setDaemon(false);
        logThread.start();
    }

    private static AtomicBoolean updateScheduled = new AtomicBoolean(false);
    private static AtomicBoolean uploadingCurrently = new AtomicBoolean(false);
    private static CustomEventHandler customEventHandler = null;

    /**
     * @param event
     * @return
     */
    public static long logEvent(JSONObject event) {
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getDatabaseHelper(context);
            long eventId = dbHelper.addEvent(event.toString());

            // custom event handler logic
            CustomEventHandler customHandler = getCustomEventHandler();
            if(customHandler != null) {
                customHandler.recordEvents(context, Arrays.asList(event));
            }

            if (dbHelper.getEventCount() >= Constants.EVENT_MAX_COUNT) {
            	mDelMaxCount += dbHelper.removeEvents(dbHelper.getNthEventId(Constants.EVENT_REMOVE_BATCH_SIZE));
            }

            if (dbHelper.getEventCount() >= Constants.EVENT_UPLOAD_THRESHOLD) {
                updateServer();
            } else {
                updateServerLater(Constants.EVENT_UPLOAD_PERIOD_MILLIS);
            }
            return eventId;
        }
        catch (Throwable e){
            Log.e(CoolaDataTracker.TAG,"error in logEvent",e);
            return -1;
        }
    }

    public static void runOnLogThread(Runnable r) {
        if (Thread.currentThread() != logThread) {
            logThread.post(r);
        } else {
            r.run();
        }
    }

    public static void updateServer() {
        updateServer(true);
    }

    public static void uploadEvents() {

        logThread.post(new Runnable() {

            @Override
            public void run() {
                updateServer();
            }
        });
    }

    public static long getDelCount(){
    	return mDelCount;
    }

    public static long getDelMaxCount(){
    	return mDelMaxCount;
    }
    
    public static long getLoadCount(){
    	return mLoadCount;
    }

    public static void resetDelCount(){
    	mDelCount = 0;
    }

    public static void resetDelMaxCount(){
    	mDelMaxCount = 0;
    }
    
    public static void resetLoadCount(){
    	mLoadCount = 0;
    }
    
    public static long getEventCount(){
    	DatabaseHelper dbHelper = DatabaseHelper.getDatabaseHelper(context);
    	return dbHelper.getEventCount();
    }
    private static void updateServerLater(long delayMillis) {
        if (!updateScheduled.getAndSet(true)) {

            logThread.postDelayed(new Runnable() {

                @Override
                public void run() {
                    updateScheduled.set(false);
                    updateServer();
                }
            }, delayMillis);
        }
    }

    // Always call this from logThread
    private static void updateServer(boolean limit) {
        if (!isOnline(context)){
            return;
        }
        if (!uploadingCurrently.getAndSet(true)) {
            DatabaseHelper dbHelper = DatabaseHelper.getDatabaseHelper(context);
            try {
                Pair<Long, List<String>> pair = dbHelper.getEvents(limit ? Constants.EVENT_UPLOAD_MAX_BATCH_SIZE : -1);
                final long maxId = pair.first;
                // no events
                if (pair.second.size()==0){
                    uploadingCurrently.set(false);
                    return;
                }
                final JSONObject eventsData = new JSONObject();
                final JSONArray arr = new JSONArray();

                for (String eventWrapper : pair.second) {
                    JSONObject event = (JSONObject) jsonParser.parse(eventWrapper);
                    arr.add(event);
                }
                eventsData.put("events",arr);

                // custom event handler logic
                CustomEventHandler customHandler = getCustomEventHandler();
                if(customHandler != null) {
                    customHandler.publishEvents(context, arr);
                } else {
                    httpThread.post(new Runnable() {
                        @Override
                        public void run() {
                            String endPoint = String.format(Constants.COOLADATA_SERVICE_MULTI_ENDPOINT,
                                    CoolaDataTracker.setupOptions.getServiceEndPoint(), CoolaDataTracker.setupOptions.getAppKey());
                            if (makeEventUploadPostRequest(endPoint, eventsData, maxId)) {
                                uploadEvents();
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                uploadingCurrently.set(false);
                Log.e(CoolaDataTracker.TAG, e.toString());
            }
        }
    }





    private static boolean makeEventUploadPostRequest(String url, JSONObject events, final long maxId) {

        // plan to use the response to check out individual events in batch
        byte[] response = null;
        boolean uploadSuccess = false;
        int retries = 0;

        while (retries++ < 3 && !uploadSuccess) {

            InputStream in = null;
            OutputStream out = null;
            BufferedOutputStream bout = null;
            HttpURLConnection connection = null;

            try {

                //Create connection
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(10000);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                BasicNameValuePair data = new BasicNameValuePair("data",events.toJSONString());
                params.add(data);
                final UrlEncodedFormEntity form = new UrlEncodedFormEntity(params, "UTF-8");

                connection.setRequestMethod("POST");
                connection.setFixedLengthStreamingMode((int)form.getContentLength());

                // addressing java.io.EOFException
                if (Build.VERSION.SDK_INT > 13) {
                    connection.setRequestProperty("Connection", "close");
                }

                out = connection.getOutputStream();
                bout = new BufferedOutputStream(out);
                form.writeTo(bout);
                bout.close();
                bout = null;
                out.close();
                out = null;



                in = connection.getInputStream();
                response  = slurp(in);
                in.close();
                in = null;
                uploadSuccess = true;

                org.json.JSONObject jObject = new org.json.JSONObject(data.getValue());
                org.json.JSONArray jArray = jObject.getJSONArray("events");
                
                mLoadCount += jArray.length();

                // server response
                if (uploadSuccess) {
                    removeEventsAndReturn(maxId);
                }
                else
                {
                    Log.w(CoolaDataTracker.TAG, "Upload failed, will attempt to re-upload later");
                }
            }
            catch(final EOFException e)
            {
                // do not remove, retry now
                continue;
            }
            catch(final IOException e){
                // do not remove. Try again later
                break;
            }

            catch (Throwable e)
            {
                Log.e(CoolaDataTracker.TAG, "Exception:", e);
                removeEventsAndReturn(maxId);
                break;
            }
            finally
            {
                if (null != bout) {
                    try {
                        bout.close();
                    } catch (final IOException e) {
                        ;
                    }
                }
                if (null != out) {
                    try {
                        out.close();
                    } catch (final IOException e) {
                        ;
                    }
                }
                if (null != in) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        ;
                    }
                }
                if (null != connection) {
                    connection.disconnect();
                }
            }
        }

        if (!uploadSuccess) {
            uploadingCurrently.set(false);
        }
        return uploadSuccess;
    }


    private static void removeEventsAndReturn(final long maxId){
        logThread.post(new Runnable() {
            @Override
            public void run() {
                try{
                    DatabaseHelper dbHelper = DatabaseHelper.getDatabaseHelper(context);
                    mDelCount += dbHelper.removeEvents(maxId);
                }
                catch (Exception ignore){
                    //
                }
                uploadingCurrently.set(false);
            }
        });
    }

    private static boolean isOnline(Context context) {
        boolean isOnline;
        try {
            final ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo netInfo = cm.getActiveNetworkInfo();
            isOnline = netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (final SecurityException e) {
            isOnline = true;
        }
        return isOnline;
    }

    // Does not close input streamq
    private static byte[] slurp(final InputStream inputStream)
            throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * Returns an instance of a custom event handler. If the handler class
     * was not defined in the tracker options, this method will return null.
     * Otherwise, it will return an instance of the handler and, if it has not
     * been previously instantiated, it will create it first.
     *
     * @return
     * @throws Exception
     */
    private static CustomEventHandler getCustomEventHandler() throws Exception {
        if(customEventHandler != null) {
            return customEventHandler;
        }

        String className = CoolaDataTracker.setupOptions.getCustomEventHandlerClassName();
        if(className == null) {
            return null;
        }

        try {
            Class<?> clazz = Class.forName(className);
            customEventHandler = (CustomEventHandler)clazz.newInstance();
        } catch(ClassNotFoundException cnfe) {
            throw new IllegalStateException("Invalid custom event handler class name: " + className, cnfe);
        } catch(Throwable t) {
            throw new IllegalStateException("Error instantiating custom event handler " + className, t);
        }

        return customEventHandler;
    }

}
