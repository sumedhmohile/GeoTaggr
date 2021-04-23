package com.sumedh.geotaggr.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.sumedh.geotaggr.Constants;
import com.sumedh.geotaggr.DataProvider;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.TagResourceManager;
import com.sumedh.geotaggr.adapters.AutocompleteFriendAdapter;
import com.sumedh.geotaggr.domain.User;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;

public class AddTagFragment extends DialogFragment {

    private LatLng location;
    private static final String TAG = "AddTagFragment";

    public AddTagFragment() {
        // Required empty public constructor
    }

    public static AddTagFragment newInstance(LatLng latLng) {
        AddTagFragment fragment = new AddTagFragment();
        Bundle args = new Bundle();
        args.putDouble(Constants.LATITUDE, latLng.latitude);
        args.putDouble(Constants.LONGITUDE, latLng.longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = new LatLng(getArguments().getDouble(Constants.LATITUDE), getArguments().getDouble(Constants.LONGITUDE));
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_tag, container, false);

        final ImageView mapImageView = view.findViewById(R.id.add_tag_map_image);
        final AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.add_tag_autocomplete);
        final List<User> friends = new ArrayList<>();
        final Button addTagButton = view.findViewById(R.id.add_tag_button);
        final TextInputEditText tagTextView = view.findViewById(R.id.tag_edit_text);

        populateFriendsList(friends);

        AutocompleteFriendAdapter adapter = new AutocompleteFriendAdapter(getContext(), friends);
        autoCompleteTextView.setAdapter(adapter);

        final String[] setForId = {DataProvider.getUserFacebookId(getContext())};

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User friend = friends.get((int) l);
                setForId[0] = friend.getFacebookId();
                autoCompleteTextView.setText(friend.getName());
            }
        });

        Picasso.get().load(resolveStaticMapsUrl(location)).into(mapImageView);

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtils.isNotBlank(tagTextView.getText())) {
                    String tagText = tagTextView.getText().toString();
                    try {
                        TagResourceManager.addTag(tagText, setForId[0], location, getContext());

                        if(getDialog() != null) getDialog().dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
    }

    private String resolveStaticMapsUrl(LatLng latLng) {
        String result = latLng.longitude + "," + latLng.latitude;
        result = Constants.STATIC_MAPS_URL +
                Constants.STATIC_MAPS_MARKER + latLng.longitude + Constants.STATIC_MAPS_COMMA + latLng.latitude + Constants.STATIC_MAPS_PARENTHESES + result + Constants.STATIC_MAPS_SCALE + getResources().getString(R.string.static_maps_api_key);

        Log.i(TAG, "Static maps url: " + result);
        return result;
    }

    private void populateFriendsList(final List<User> friends) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray friendsArray = response.getJSONObject().getJSONArray("data");

                            for(int i = 0 ; i < friendsArray.length() ; i++) {
                                String id = friendsArray.getJSONObject(i).getString(Constants.FACEBOOK_ID);
                                String name = friendsArray.getJSONObject(i).getString(Constants.FACEBOOK_NAME);
                                friends.add(new User(id, name));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

}