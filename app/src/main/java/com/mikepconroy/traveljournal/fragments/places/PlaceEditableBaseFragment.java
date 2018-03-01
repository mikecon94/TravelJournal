package com.mikepconroy.traveljournal.fragments.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.HolidayChooserActivity;
import com.mikepconroy.traveljournal.PhotoChooserActivity;
import com.mikepconroy.traveljournal.R;

public abstract class PlaceEditableBaseFragment extends Fragment {

    protected static final int REQUEST_IMAGE_PATH = 0;
    protected static final int REQUEST_HOLIDAY_TRIP = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "PlaceEditableBase#onCreateView: Creating View.");
        final View view = inflater.inflate(R.layout.fragment_place_edit_base, container, false);

        ImageView image = view.findViewById(R.id.place_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PhotoChooserActivity.class);
                startActivityForResult(i, REQUEST_IMAGE_PATH);
            }
        });


        Button associateWithHoliday = view.findViewById(R.id.associate_holiday_button);
        associateWithHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HolidayChooserActivity.class);
                startActivityForResult(i, REQUEST_HOLIDAY_TRIP);
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Button saveItemButton = view.findViewById(R.id.save_place_button);
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

        return view;
    }

    protected abstract void saveItem();

}
