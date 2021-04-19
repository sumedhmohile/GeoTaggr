package com.sumedh.geotaggr.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumedh.geotaggr.Constants;
import com.sumedh.geotaggr.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ProgressBarFragment extends DialogFragment {

    private String message;

    public ProgressBarFragment() {
        // Required empty public constructor
    }

    public static ProgressBarFragment newInstance(String message) {
        ProgressBarFragment fragment = new ProgressBarFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PROGRESSBAR_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(Constants.PROGRESSBAR_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress_bar, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.fragment_progress_bar, null);
        alertDialogBuilder.setView(view);

        TextView progressbarText = view.findViewById(R.id.progress_bar_text);


        progressbarText.setText(message);

        return alertDialogBuilder.create();
    }
}