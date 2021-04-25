package com.sumedh.geotaggr.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.sumedh.geotaggr.domain.Constants;

public class DataProvider {
    public static String getUserFacebookId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(Constants.SERVER_FIELD_FACEBOOK_ID, "");
    }

    public static String getAuthToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        return "Bearer " + sp.getString(Constants.SERVER_FIELD_RESPONSE_TOKEN, "");
    }

    public static String getFCMToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        return sp.getString(Constants.SERVER_FIELD_FCM_TOKEN, "");
    }
}
