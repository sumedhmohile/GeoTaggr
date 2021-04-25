package com.sumedh.geotaggr.callbacks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.sumedh.geotaggr.domain.Constants;
import com.sumedh.geotaggr.services.MessagingService;
import com.sumedh.geotaggr.domain.ProgressBarManager;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.fragments.CustomMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.FragmentManager;

public class LoginGraphRequestCallback implements GraphRequest.Callback {
    private final String TAG = "GraphRequestCallback";
    private Context context;
    private FragmentManager fragmentManager;

    public LoginGraphRequestCallback(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;

    }

    @Override
    public void onCompleted(GraphResponse response) {
        Log.i(TAG, "Login graph request completed with response: " + response);
        try {
            final String name = response.getJSONObject().getString(Constants.FACEBOOK_NAME);
            final String facebookId = response.getJSONObject().getString(Constants.FACEBOOK_ID);

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String url = context.getString(R.string.base_url) + Constants.ENDPOINT_USER_LOGIN;

            JSONObject object = new JSONObject();
            object.put(Constants.SERVER_FIELD_NAME, name);
            object.put(Constants.SERVER_FIELD_FACEBOOK_ID, facebookId);

            object.put(Constants.SERVER_FIELD_FCM_TOKEN, "dummy");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, "Login request to server completed with response: " + response);
                    try {
                        String responseToken = response.getString(Constants.SERVER_FIELD_RESPONSE_TOKEN);
                        saveUserDetails(responseToken, facebookId, name);

                        Toast.makeText(context, String.format(context.getString(R.string.login_success), name), Toast.LENGTH_LONG).show();

                        MessagingService messagingService = new MessagingService();
                        messagingService.refreshToken(context);

                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, CustomMapFragment.newInstance())
                                .commit();

                    } catch (JSONException je) {
                        Log.e(TAG, je.toString());
                    } finally {
                        ProgressBarManager.dismissProgressBar();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                    LoginManager.getInstance().logOut();
                    ProgressBarManager.dismissProgressBar();
                    Toast.makeText(context, "Error when logging in", Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException je) {
            Log.e(TAG, "Error when fetching name");
            Toast.makeText(context, context.getString(R.string.login_error), Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            ProgressBarManager.dismissProgressBar();
        }
    }

    private void saveUserDetails(String token, String facebookId, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constants.SERVER_FIELD_RESPONSE_TOKEN, token);
        editor.putString(Constants.SERVER_FIELD_FACEBOOK_ID, facebookId);
        editor.putString(Constants.SERVER_FIELD_NAME, name);
        editor.apply();
    }
}
