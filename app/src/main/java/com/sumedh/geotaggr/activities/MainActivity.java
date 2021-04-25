package com.sumedh.geotaggr.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.sumedh.geotaggr.domain.Constants;
import com.sumedh.geotaggr.fragments.CustomMapFragment;
import com.sumedh.geotaggr.fragments.LoginFragment;
import com.sumedh.geotaggr.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        Fragment nextFragment;

        if("".equals(sharedPreferences.getString(Constants.SERVER_FIELD_FACEBOOK_ID, ""))) {
            nextFragment = LoginFragment.newInstance();
        }
        else {
            nextFragment = CustomMapFragment.newInstance();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, nextFragment)
                .commit();
    }
}