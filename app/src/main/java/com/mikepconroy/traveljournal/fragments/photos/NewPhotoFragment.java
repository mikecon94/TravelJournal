package com.mikepconroy.traveljournal.fragments.photos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;

public class NewPhotoFragment extends EditableBaseFragment {

    public NewPhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewPhotoFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_photo_edit_base, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("New Photo", false);
    }



    @Override
    protected void saveItem() {
        //TODO: Implement this method.
    }
}
