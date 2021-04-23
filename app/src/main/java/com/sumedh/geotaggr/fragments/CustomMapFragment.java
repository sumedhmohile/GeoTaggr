package com.sumedh.geotaggr.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.TagDatabase;
import com.sumedh.geotaggr.TagResourceManager;
import com.sumedh.geotaggr.domain.Tag;

import java.util.List;

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

        if(getActivity() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
            if(mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                AddTagFragment addTagFragment = AddTagFragment.newInstance(latLng);
                addTagFragment.show(getChildFragmentManager(), "addTagFragment");
            }
        });


        TagDatabase tagDatabase = TagDatabase.getInstance(getContext());

        tagDatabase.tagDao().getAllTags().observe(this, new Observer<List<Tag>>() {
            @Override
            public void onChanged(List<Tag> tags) {
                Log.i(TAG, "tag changed");

                for(Tag tag : tags) {
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(tag.getLatitude(), tag.getLongitude())).title(tag.getTagText()));
                    marker.setTag(tag.getTagId());
                    marker.showInfoWindow();
                }

            }
        });

        TagResourceManager.loadTagsToMap(map, getContext());
    }

}