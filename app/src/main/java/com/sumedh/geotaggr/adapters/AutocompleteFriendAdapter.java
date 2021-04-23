package com.sumedh.geotaggr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.sumedh.geotaggr.FriendFilter;
import com.sumedh.geotaggr.R;
import com.sumedh.geotaggr.domain.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class AutocompleteFriendAdapter extends ArrayAdapter<User> {

    private final List<User> friends;
    private List<User> filteredFriends = new ArrayList<>();

    public AutocompleteFriendAdapter(Context context, List<User> friends) {
        super(context, 0, friends);
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return filteredFriends.size();
    }

    @Override
    @NonNull
    public Filter getFilter() {
        return new FriendFilter(this, friends);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User friend = filteredFriends.get(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView nameTextView = convertView.findViewById(R.id.friend_name_text);
        ProfilePictureView profilePictureView = convertView.findViewById(R.id.profile_picture_view);

        nameTextView.setText(friend.getName());
        profilePictureView.setProfileId(friend.getFacebookId());

        return convertView;
    }

    public List<User> getFilteredFriends() {
        return filteredFriends;
    }

    @Override
    public long getItemId(int position) {
        String facebookId = filteredFriends.get(position).getFacebookId();
        for (User friend : friends) {
            if(friend.getFacebookId().equals(facebookId))
                return friends.indexOf(friend);
        }
        return super.getItemId(position);
    }
}
