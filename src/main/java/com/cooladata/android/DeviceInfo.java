package com.cooladata.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DeviceInfo {

    public String versionOS;
    public String deviceManufacturer;
    public String deviceModel;
    public String carrier;
    public String screenSize;
    public String deviceOS = "Android";
    public String dua;
    public float timeZoneOffset;
    public String screenOrientation;
    public String appVersion;
    public String appId;

    private Context context;

    public DeviceInfo(Context context) {
        this.context = context;

        setScreenSize();
        setTimeZoneOffset();
        setDUA();
        setCarrier();
        setOrientation();
        setAppVersion();
        setAppId();
    }

    public static Location getMostRecentLocation(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            List<Location> locations = new ArrayList<Location>();
            for (String provider : providers) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    locations.add(location);
                }
            }

            long maximumTimestamp = -1;
            Location bestLocation = null;
            for (Location location : locations) {
                if (location.getTime() > maximumTimestamp) {
                    maximumTimestamp = location.getTime();
                    bestLocation = location;
                }
            }

            return bestLocation;
        }
        catch (Exception e){
            return null;
        }
    }

    private void setDUA() {
        dua = System.getProperty("http.agent");
    }

    private void setScreenSize() {
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            screenSize = String.format("%sx%s", metrics.widthPixels, metrics.heightPixels);

        } catch (Exception e) {
            // TODO
        }
    }

    private void setAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (Exception e) {
            // TODO
        }
    }

    private void setAppId() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appId = packageInfo.packageName;
        } catch (Exception e) {
            // TODO
        }
    }

    public void setCarrier() {

        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            carrier = manager.getNetworkOperatorName();
        } catch (Exception e) {
            // TODO
        }
    }

    private void setOrientation() {
        int orientation = context.getResources().getConfiguration().orientation;
        String orientationStr = "Undefined";
        switch (orientation) {
            case 2:
                orientationStr = "Landscape";
                break;
            case 1:
                orientationStr = "Portrait";
                break;
            default:
                break;
        }
        screenOrientation = orientationStr;

    }

    private void setTimeZoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        float offsetFromUtc = tz.getOffset(now.getTime()) / 3600000;
        timeZoneOffset = offsetFromUtc;
    }
}
