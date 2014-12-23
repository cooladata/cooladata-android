package com.cooladata.android;

import android.os.Handler;
import android.os.HandlerThread;

public class WorkerThread extends HandlerThread {

    private Handler handler;

    public WorkerThread(String name) {
        super(name);
    }

    public Handler getHandler() {
        return handler;
    }

    public void post(Runnable r) {
        waitForInitialization();
        handler.post(r);
    }

    public void postDelayed(Runnable r, long delayMillis) {
        waitForInitialization();
        handler.postDelayed(r, delayMillis);
    }

    public void removeCallbacks(Runnable r) {
        waitForInitialization();
        handler.removeCallbacks(r);
    }

    private synchronized void waitForInitialization() {
        if (handler == null) {
            handler = new Handler(getLooper());
        }
    }
}
