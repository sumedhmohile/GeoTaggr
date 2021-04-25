package com.sumedh.geotaggr;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sumedh.geotaggr.domain.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class TagResourceManager {
    private static final String TAG = "TagResourceManager";

    public static void addTag(String tagText, String setForId, LatLng location, final Context context) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_url) + Constants.ENDPOINT_TAG;

        JSONObject object = new JSONObject();
        object.put(Constants.SERVER_FIELD_TAG_TEXT, tagText);
        object.put(Constants.SERVER_FIELD_SET_FOR, setForId);
        object.put(Constants.SERVER_FIELD_LATITUDE, location.latitude);
        object.put(Constants.SERVER_FIELD_LONGITUDE, location.longitude);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                Toast.makeText(context, "Added tag", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", DataProvider.getAuthToken(context));
                Log.i(TAG, "CHECK: " + DataProvider.getAuthToken(context));
                return params;
            }};

        requestQueue.add(jsonObjectRequest);
    }

    public static void saveTag(Tag tag, Context context) {
        Log.i(TAG, "Saving to db: " + tag.toString());
        if(context != null) {
            TagDatabase db = TagDatabase.getInstance(context);
            db.tagDao().Insert(tag);
            addTagToGeofence(tag, context);
        }
    }

    public static void clearTags(Context context) {
        if(context != null) {
            TagDatabase db = TagDatabase.getInstance(context);
            db.tagDao().DeleteAllTags();
        }
    }

    public static void loadTagsToMap(final GoogleMap map, final Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_url) + Constants.ENDPOINT_TAG;

        clearTags(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "ADD TO MAP: " + response.toString());
                try {
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject object = response.getJSONObject(i);
                        //TODO: add set by name here
                        Integer tagId = object.getInt("tagId");
                        String tagBody = object.getString("tagBody");
                        String setById = object.getString("setById");
                        Double latitude = object.getDouble("latitude");
                        Double longitude = object.getDouble("longitude");

                        Tag tag = new Tag(tagId, tagBody, latitude, longitude, "", setById);
                        saveTag(tag, context);
                    }

                } catch (JSONException je) {
                    Log.e(TAG, "Error when parsing response " + je);
                    je.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", DataProvider.getAuthToken(context));
                return params;
            }};

        requestQueue.add(jsonObjectRequest);
    }

    private static void addTagToGeofence(Tag tag, Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, context.getResources().getString(R.string.location_permission_request), Toast.LENGTH_LONG).show();
            return;
        }

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);

        final Geofence geofence = new Geofence.Builder()
                .setRequestId(tag.getTagId().toString())
                .setCircularRegion(
                        tag.getLatitude(),
                        tag.getLongitude(),
                        Constants.GEOFENCE_RADIUS_METRES
                )
                .setExpirationDuration(Constants.GEOFENCE_RADIUS_EXPIRY_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        final GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence).build();

        GeofencingRequest request = builder.build();

        Intent intent = new Intent(context, GeofenceBroadcastListener.class);
        PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        geofencingClient.addGeofences(request, geofencePendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Added geofence");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Could not add geofence: " + e);
                e.printStackTrace();
            }
        });
    }

    public static void deleteTagById(final Integer tagId, final Context context, final Dialog dialog) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.base_url) + Constants.ENDPOINT_TAG + "/" + tagId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                TagDatabase db = TagDatabase.getInstance(context);
                db.tagDao().deleteTagById(tagId);
                ProgressBarManager.dismissProgressBar();
                dialog.dismiss();
                Toast.makeText(context, "Deleted tag", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", DataProvider.getAuthToken(context));
                return params;
            }};

        requestQueue.add(jsonObjectRequest);
    }
}
