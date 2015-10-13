package com.cooladata.android;

import android.content.Context;
import com.cooladata.android.json.JSONObject;
import java.util.List;

/**
 *
 */
public interface CustomEventHandler {

    /**
     * Notification method indicating new events to be recorded
     *
     * @param context context
     * @param events events
     * @throws Exception if an error occurs
     */
    public void recordEvents(Context context, List<JSONObject> events) throws Exception;

    /**
     * Notification method indicating events to be published
     *
     * @param context context
     * @param events events
     * @throws Exception if an error occurs
     */
    public void publishEvents(Context context, List<JSONObject> events) throws Exception;

}
