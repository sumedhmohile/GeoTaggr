package com.sumedh.geotaggr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.sumedh.geotaggr.Constants;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.callbacks.FacebookLoginCallback;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private CallbackManager callbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        callbackManager = CallbackManager.Factory.create();

        FacebookLoginCallback facebookCallback = new FacebookLoginCallback(getContext(), getFragmentManager());

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setPermissions(Constants.FACEBOOK_FRIENDS_PERMISSION, Constants.FACEBOOK_PROFILE_PERMISSION);
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, facebookCallback);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}