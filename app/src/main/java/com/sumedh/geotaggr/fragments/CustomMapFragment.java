package com.sumedh.geotaggr.fragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sumedh.geotaggr.domain.Constants;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.database.TagDatabase;
import com.sumedh.geotaggr.domain.TagResourceManager;
import com.sumedh.geotaggr.domain.Tag;

import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;


public class CustomMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "CustomMapFragment";

    private GoogleMap map;

    public CustomMapFragment() {
        // Required empty public constructor
    }

    public static CustomMapFragment newInstance() {
        return new CustomMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (getActivity() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        createNotificationChannel();
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                AddTagFragment addTagFragment = AddTagFragment.newInstance(latLng);
                addTagFragment.show(getChildFragmentManager(), "addTagFragment");
            }
        });

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));
                    }
                }
            });
        }


        final TagDatabase tagDatabase = TagDatabase.getInstance(getContext());

        tagDatabase.tagDao().getAllTags().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                Log.i(TAG, "tag changed");
                map.clear();
                for(Tag tag : tags) {
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(tag.getLatitude(), tag.getLongitude())).title(tag.getTagText()));
                    marker.setTag(tag.getTagId());
                    marker.showInfoWindow();
                }
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Tag tag = tagDatabase.tagDao().getTagById((Integer) marker.getTag());
                TagFragment tagFragment = TagFragment.newInstance(tag.getTagText(), tag.getLatitude(), tag.getLongitude(), tag.getTagId());
                tagFragment.show(getChildFragmentManager(), "tagFragment");

                return true;
            }
        });

        TagResourceManager.loadTagsToMap(map, getContext());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.NOTIFICATION_CHANNEL;
            String description = Constants.NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}