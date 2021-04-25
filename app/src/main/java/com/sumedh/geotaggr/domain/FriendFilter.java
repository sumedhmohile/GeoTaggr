package com.sumedh.geotaggr.domain;

import android.util.Log;
import android.widget.Filter;

import com.sumedh.geotaggr.adapters.AutocompleteFriendAdapter;
import com.sumedh.geotaggr.domain.User;

import java.util.ArrayList;
import java.util.List;

public class FriendFilter extends Filter {

    private AutocompleteFriendAdapter adapter;
    private List<User> originalFriendList;
    private List<User> filteredFriendList;
    private String TAG = "FriendFilter";

    public FriendFilter(AutocompleteFriendAdapter adapter, List<User> friendList) {
        super();
        this.adapter = adapter;
        this.originalFriendList = friendList;
        this.filteredFriendList = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Log.i(TAG, "Performing filtering for constraint: " + ((null == constraint) ? "null" : constraint.toString()));
        filteredFriendList.clear();
        final FilterResults results = new FilterResults();

        if(null == constraint || constraint.length() == 0) {
            filteredFriendList.addAll(originalFriendList);
        }
        else {
            final String filterPattern = constraint.toString().toLowerCase().trim();

            for(final User friend : originalFriendList) {
                if(friend.getName().toLowerCase().contains(filterPattern)) {
                    filteredFriendList.add(friend);
                }
            }
        }
        Log.i(TAG, "Results after filtering: " + filteredFriendList.toString());
        results.values = filteredFriendList;
        results.count = filteredFriendList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.getFilteredFriends().clear();
        if(null != results.values) {
            Log.i(TAG, "Publishing results: " + results.values);
            adapter.getFilteredFriends().addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
