package com.mikepconroy.traveljournal.fragments.photos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;

import static android.app.Activity.RESULT_OK;

public class NewPhotoFragment extends EditableBaseFragment {

    protected static final int REQUEST_PLACE = 0;

    public NewPhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewPhotoFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_photo_edit_base, container, false);

        Button chooseLocationButton = view.findViewById(R.id.image_location_button);
        chooseLocationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "NewPhotoFragment: Location Button clicked. Launching PlacePicker.");
                //TODO: Restrict the Place API Key to this app only.
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), REQUEST_PLACE);
                } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Location chooser unavailable.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
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
