package com.cooladata.android;

import android.text.TextUtils;

public class CoolaDataTrackerOptions {

    private String appKey;
    private String serviceEndPoint = Constants.SERVICE_ENDPOINT;
    private String userId = null;
    private String sessionId = null;
    private boolean loggingEnabled = false;
    private String customEventHandlerClassName = null;

    /**
     * The options object to use when setting up CoolaDataTracker
     *
     * @param appKey          Mandatory, the app key to use.
     */
    public CoolaDataTrackerOptions(String appKey) {
        this.appKey = appKey;
    }
    
    /**
     * The options object to use when setting up CoolaDataTracker
     *
     * @param appKey          Mandatory, the app key to use.
     * @param userId          Optional, the user id for this user, you can set this value here once, and this value will be used with each event.
     */
    public CoolaDataTrackerOptions(String appKey, String userId) {
        this.appKey = appKey;
        this.userId = userId;
    }

    /**
     * The options object to use when setting up CoolaDataTracker
     *
     * @param appKey          Mandatory, the app key to use.
     * @param userId          Optional, the user id for this user, you can set this value here once, and this value will be used with each event.
     * @param loggingEnabled  Optional, default is false, whether to log info, warning and error messages to the device logs, used for development time mainly.
     */
    public CoolaDataTrackerOptions(String appKey, String userId, boolean loggingEnabled) {
        this(appKey, userId, null, loggingEnabled);
    }

    /**
     * The options object to use when setting up CoolaDataTracker
     *
     * @param appKey          Mandatory, the app key to use.
     * @param userId          Optional, the user id for this user, you can set this value here once, and this value will be used with each event.
     * @param sessionId       Optional, the session id for this user, you can set this value here once, and this value will be used with each event.
     * @param loggingEnabled  Optional, default is false, whether to log info, warning and error messages to the device logs, used for development time mainly.
     */
    public CoolaDataTrackerOptions(String appKey, String sessionId, String userId, boolean loggingEnabled) {
        this(appKey, userId, sessionId, null, loggingEnabled);
    }

    /**
     * The options object to use when setting up CoolaDataTracker
     *
     * @param appKey          Mandatory, the app key to use.
     * @param userId          Optional, the user id for this user, you can set this value here once, and this value will be used with each event.
     * @param sessionId       Optional, the session id for this user, you can set this value here once, and this value will be used with each event.
     * @param serviceEndPoint Optional, used for testing, the endpoint to use for publishing events.
     * @param loggingEnabled  Optional, default is false, whether to log info, warning and error messages to the device logs, used for development time mainly.
     */
    public CoolaDataTrackerOptions(String appKey, String sessionId, String userId, String serviceEndPoint, boolean loggingEnabled) {
        this.appKey = appKey;
        this.userId = userId;
        this.sessionId = sessionId;
        this.serviceEndPoint = serviceEndPoint;
        this.loggingEnabled = loggingEnabled;
        if (serviceEndPoint==null || serviceEndPoint.length()==0){
            this.serviceEndPoint = Constants.SERVICE_ENDPOINT;
        }
    }

    /**
     * @return
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @return
     */
    public String getServiceEndPoint() {
        return serviceEndPoint;
    }

    /**
     * @param serviceEndPoint
     */
    public void setServiceEndPoint(String serviceEndPoint) {
        this.serviceEndPoint = serviceEndPoint;
    }

    /**
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return
     */
    public Boolean getLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * @return
     */
    public String getCustomEventHandlerClassName() {
        return customEventHandlerClassName;
    }

    /**
     * @param customEventHandlerClassName
     */
    public void setCustomEventHandlerClassName(String customEventHandlerClassName) {
        this.customEventHandlerClassName = customEventHandlerClassName;
    }

    /**
     * @param userId
     * @return
     */
    public String resolveUserId(String userId) {
        if (TextUtils.isEmpty(userId)) {
            if (!TextUtils.isEmpty(getUserId())) {
                return getUserId();
            } else {
                throw new IllegalStateException("User Id must be provided, either in the setup(...) method or in the trackEvent(...) method.");
            }
        } else {
            setUserId(userId);
            return userId;
        }
    }

    /**
     * @param sessionId
     * @return
     */
    public String resolveSessionId(String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            if (!TextUtils.isEmpty(getSessionId())) {
                return getSessionId();
            } else {
                return null;
            }
        } else {
            setSessionId(sessionId);
            return sessionId;
        }
    }

    @Override
    public String toString() {
        return "CoolaDataTrackerOptions{" + "appKey='" + appKey + '\'' + ", serviceEndPoint='" + serviceEndPoint + '\'' + ", userId='"
                + userId + '\'' + ", sessionId='" + sessionId + '\'' + ", loggingEnabled=" + loggingEnabled + '\'' + ", customEventHandlerClassName=" +
                customEventHandlerClassName + '}';
    }
}