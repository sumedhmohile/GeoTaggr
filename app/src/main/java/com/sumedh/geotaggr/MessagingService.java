package com.sumedh.geotaggr;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sumedh.geotaggr.domain.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendToServer(token, getApplicationContext());
    }

    public void refreshToken(final Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        sendToServer(token, context);
                    }
                });
    }

    public void sendToServer(String token, final Context context) {
        String url = context.getString(R.string.base_url) + Constants.ENDPOINT_USER_FCM;

        SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SERVER_FIELD_FCM_TOKEN, token);
        editor.apply();

        JSONObject object = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            object.put(Constants.SERVER_FIELD_FCM_TOKEN, token);

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
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", DataProvider.getAuthToken(context));
                    return params;
                }
            };

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();

            if(Constants.NEW_TAG_COMMAND.equals(data.get("type"))) {
                Integer tagId = Integer.parseInt(data.get(Constants.SERVER_FIELD_TAG_ID));
                String tagText = data.get(Constants.SERVER_FIELD_TAG_TEXT);
                String setByName = data.get(Constants.SERVER_FIELD_SET_BY_NAME);
                String setById = data.get(Constants.SERVER_FIELD_SET_BY_ID);
                Double latitude = Double.parseDouble(data.get(Constants.SERVER_FIELD_LATITUDE));
                Double longitude = Double.parseDouble(data.get(Constants.SERVER_FIELD_LONGITUDE));

                Tag tag = new Tag(tagId, tagText, latitude, longitude, setByName, setById);

                TagResourceManager.saveTag(tag, getApplicationContext());
            }
        }
    }

}
