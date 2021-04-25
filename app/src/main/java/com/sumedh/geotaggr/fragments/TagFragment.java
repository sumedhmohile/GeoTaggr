package com.sumedh.geotaggr.fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.sumedh.geotaggr.domain.Constants;
import com.sumedh.geotaggr.domain.ProgressBarManager;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.domain.TagResourceManager;

public class TagFragment extends DialogFragment {

    private static final String TAG = "TagFragment";
    private String tagText;
    private Double latitude;
    private Double longitude;
    private Integer tagId;


    public TagFragment() {
        // Required empty public constructor
    }

    public static TagFragment newInstance(String tagText, Double latitude, Double longitude, Integer tagId) {
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SERVER_FIELD_TAG_TEXT, tagText);
        args.putDouble(Constants.SERVER_FIELD_LATITUDE, latitude);
        args.putDouble(Constants.SERVER_FIELD_LONGITUDE, longitude);
        args.putInt(Constants.SERVER_FIELD_TAG_ID, tagId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tagText = getArguments().getString(Constants.SERVER_FIELD_TAG_TEXT);
            latitude = getArguments().getDouble(Constants.SERVER_FIELD_LATITUDE);
            longitude = getArguments().getDouble(Constants.SERVER_FIELD_LONGITUDE);
            tagId = getArguments().getInt(Constants.SERVER_FIELD_TAG_ID);
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag, container, false);

        TextView tagTextView = view.findViewById(R.id.tag_view_text);
        TextView tagSetByTextView = view.findViewById(R.id.tag_view_set_by_text);
        ImageView tagImageView = view.findViewById(R.id.tag_view_image);
        Button deleteButton = view.findViewById(R.id.tag_view_delete_button);

        tagTextView.setText(tagText);

        Picasso.get().load(resolveStaticMapsUrl(new LatLng(latitude, longitude))).into(tagImageView);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TagResourceManager.deleteTagById(tagId, getContext(), getDialog());
                ProgressBarManager.showProgressBar("Deleting tag", getChildFragmentManager());
            }
        });

        return view;
    }

    private String resolveStaticMapsUrl(LatLng latLng) {
        String result = latLng.longitude + "," + latLng.latitude;
        result = Constants.STATIC_MAPS_URL + Constants.STATIC_MAPS_MARKER + result + Constants.STATIC_MAPS_PARENTHESES + result + Constants.STATIC_MAPS_SCALE + getResources().getString(R.string.static_maps_api_key);

        Log.i(TAG, "Static maps url: " + result);
        return result;
    }
}
