package com.sumedh.geotaggr.domain;

public class Constants {
    public static final String FACEBOOK_FRIENDS_PERMISSION = "user_friends";
    public static final String FACEBOOK_PROFILE_PERMISSION = "public_profile";
    public static final String FACEBOOK_NAME = "name";
    public static final String FACEBOOK_ID = "id";

    public static final String ENDPOINT_USER_LOGIN = "users/login";
    public static final String ENDPOINT_USER_FCM = "users/fcm";
    public static final String ENDPOINT_TAG = "tags";

    public static final String SERVER_FIELD_NAME = "name";
    public static final String SERVER_FIELD_FACEBOOK_ID = "facebookId";
    public static final String SERVER_FIELD_FCM_TOKEN = "fcmToken";
    public static final String SERVER_FIELD_RESPONSE_TOKEN = "token";
    public static final String SERVER_FIELD_TAG_TEXT = "tagText";
    public static final String SERVER_FIELD_SET_FOR = "setForId";
    public static final String SERVER_FIELD_LATITUDE = "latitude";
    public static final String SERVER_FIELD_LONGITUDE = "longitude";
    public static final String SERVER_FIELD_TAG_ID = "tagId";
    public static final String SERVER_FIELD_SET_BY_NAME = "setByName";
    public static final String SERVER_FIELD_SET_BY_ID = "setById";

    public static final String NEW_TAG_COMMAND = "NEW_TAG";

    public static final String PREFERENCES = "GT_PREFERENCES";
    public static final String PROGRESSBAR_MESSAGE = "message";

    public static final String STATIC_MAPS_URL = "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/";
    public static final String STATIC_MAPS_MARKER = "pin-l+f90101(";
//    public static final String STATIC_MAPS_MARKER = "pin-l+f90101(-122.3486,37.8169)";
    public static final String STATIC_MAPS_PARENTHESES = ")/";
    public static final String STATIC_MAPS_SCALE = ",17/900x600?access_token=";

    public static final int GEOFENCE_RADIUS_METRES = 100;
    public static final int GEOFENCE_RADIUS_EXPIRY_MILLISECONDS = 7 * 24 * 60 * 60 * 1000;

    public static final String NOTIFICATION_CHANNEL = "GEOTAGGR_APP_CHANEL";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Geotaggr notification channel";


}
