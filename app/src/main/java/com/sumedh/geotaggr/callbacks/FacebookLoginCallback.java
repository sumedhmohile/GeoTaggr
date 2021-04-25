package com.sumedh.geotaggr.callbacks;

import android.content.Context;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.sumedh.geotaggr.domain.ProgressBarManager;

import androidx.fragment.app.FragmentManager;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {
    private static final String TAG = "FacebookLoginCallback";
    private Context context;
    private FragmentManager fragmentManager;

    public FacebookLoginCallback(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.i(TAG, "Facebook login result successfully fetched: " + loginResult);

        ProgressBarManager.showProgressBar("Logging in", fragmentManager);

        final String facebookId = loginResult.getAccessToken().getUserId();
        LoginGraphRequestCallback loginGraphRequestCallback = new LoginGraphRequestCallback(context, fragmentManager);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + facebookId + "/",
                null,
                HttpMethod.GET,
                loginGraphRequestCallback
        ).executeAsync();
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "Login cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "Login error due to: " + error.toString());
    }
}
