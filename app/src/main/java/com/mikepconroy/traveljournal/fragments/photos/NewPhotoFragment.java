package com.mikepconroy.traveljournal.fragments.photos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;

public class NewPhotoFragment extends Fragment {

    public NewPhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewPhotoFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_photo_edit_base, container, false);
        return view;
    }

}
