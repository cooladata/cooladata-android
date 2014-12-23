package com.cooladata.android;

public class Constants {

    public static final String PACKAGE_NAME = Constants.class.getPackage().getName();
    public static final String SHARED_PREFERENCES_NAME_PREFIX = PACKAGE_NAME;
    public static final String PREFKEY_PREVIOUS_END_SESSION_ID = PACKAGE_NAME + ".previousEndSessionId";
    public static final int EVENT_UPLOAD_THRESHOLD = 20;
    public static final int EVENT_UPLOAD_MAX_BATCH_SIZE = 20;
    public static final int EVENT_MAX_COUNT = 1000;
    public static final int EVENT_REMOVE_BATCH_SIZE = 20;
    public static final long EVENT_UPLOAD_PERIOD_MILLIS = 60 * 1000; // 30s


    // from cooladata
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CooladataEvents";
    public static final String COOLADATA_SERVICE_MULTI_ENDPOINT = "%s/egw/3/%s/multi/json";
    public static final String TRACKER_TYPE_FIELD_NAME = "tracker_type";
    public static final String TRACKER_TYPE = "Android";
    public static final String TRACKER_VERSION_FIELD_NAME = "tracker_version";
    public static final String TRACKER_VERSION = "2.1.7";
    public static final String EVENT_NAME_FIELD_NAME = "event_name";
    public static final String USER_ID_FIELD_NAME = "user_id";
    public static final String SESSION_ID_FIELD_NAME = "session_id";
    public static final String EVENT_ID_FIELD_NAME = "event_id";
    public static final String EVENT_TIMESTAMP_FIELD_NAME = "event_timestamp_epoch";
    public static final String EVENT_TIMEZONE_OFFSET = "event_timezone_offset";
    public static final String SESSION_OS_FIELD_NAME = "session_os";
    public static final String SESSION_OS_VERSION_FIELD_NAME = "session_os_version";
    public static final String SESSION_SCREEN_SIZE_FIELD_NAME = "session_screen_size";
    public static final String SESSION_DUA_FIELD_NAME = "session_dua";
    public static final String SESSION_APP_ID_FIELD_NAME = "session_app_id";
    public static final String SESSION_APP_VERSION_FIELD_NAME = "app_version";
    public static final String SESSION_DEVICE_MANUFACTURER = "session_manufacturer";
    public static final String SESSION_MODEL_FIELD_NAME = "session_model";
    public static final String SESSION_DEVICE_ORIENTATION_FIELD_NAME = "session_device_orientation";
    public static final String SESSION_CARRIER = "carrier";
    public static final String SESSION_ALT = "location_altitude";
    public static final String SESSION_LONG = "location_longitude";
    public static final String SESSION_LAT = "location_latitude";
    public static final String HAS_NFC = "has_nfc";
    public static final String HAS_TELEPHONE = "has_telephone";
    public static final String EVENT_CONNECTIVITY_STATE = "connectivity_state";


   public static final String SERVICE_ENDPOINT = "https://ec01.cooladata.com";
    //public static final String SERVICE_ENDPOINT = "http://10.0.0.117:9090";
   
   public static final String PREF_KEY	= "CoolaData";
   public static final String INSTALL_REFERRER_KEY	= "referrer";
   public static final String UTM_CAMPAIGN_KEY = "utm_campaign";
   public static final String UTM_CONTENT_KEY = "utm_content";
   public static final String UTM_MEDIUM_KEY = "utm_medium";
   public static final String UTM_SOURCE_KEY = "utm_source";
   public static final String UTM_TERM_KEY = "utm_term";

   public static final String SESSION_INSTALL_REFERRER = "referrer";
   public static final String SESSION_UTM_CAMPAIGN = "utm_campaign";
   public static final String SESSION_UTM_CONTENT = "utm_content";
   public static final String SESSION_UTM_MEDIUM = "utm_medium";
   public static final String SESSION_UTM_SOURCE = "utm_source";
   public static final String SESSION_UTM_TERM = "utm_term";


   
}
