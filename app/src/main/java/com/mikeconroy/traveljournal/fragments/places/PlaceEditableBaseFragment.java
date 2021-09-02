package com.mikeconroy.traveljournal.fragments.places;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.mikeconroy.traveljournal.Configuration;
import com.mikeconroy.traveljournal.HolidayChooserActivity;
import com.mikeconroy.traveljournal.MapViewWrapper;
import com.mikeconroy.traveljournal.PhotoChooserActivity;
import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.fragments.DatePickerFragment;
import com.mikeconroy.traveljournal.fragments.EditableBaseFragment;
import com.mikeconroy.traveljournal.db.Place;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class PlaceEditableBaseFragment extends EditableBaseFragment {

    protected static final int REQUEST_IMAGE_PATH = 0;
    protected static final int REQUEST_HOLIDAY_TRIP = 1;
    protected static final int REQUEST_DATE = 2;

    private SimpleDateFormat formatter = new SimpleDateFormat(Configuration.DATE_PATTERN);
    private Button dateButton;

    protected String imagePath;
    protected MapViewWrapper mapViewWrapper;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 565;
    private boolean locationPermissionGranted;

    protected int holidayId = -1;

    //If placeId is -1 then we are inserting.
    protected int placeId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Configuration.TAG, "PlaceBaseFragment#onCreate: Creating.");
        if (getArguments() != null) {
            placeId = getArguments().getInt("placeId");
            Log.i(Configuration.TAG, "PlaceBaseFragment#onCreate: Place ID: " + placeId);
        }
    }

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

        Button contactListButton = view.findViewById(R.id.associate_contacts_button);
        contactListButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Not yet implemented.", Toast.LENGTH_SHORT).show();
            }
        });

        dateButton = view.findViewById(R.id.place_date_button);
        String today = formatter.format(new Date());
        dateButton.setText(today);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                try {
                    Date startDate = formatter.parse(dateButton.getText().toString());
                    DatePickerFragment dialog = DatePickerFragment.newInstance(startDate);
                    dialog.setTargetFragment(PlaceEditableBaseFragment.this, REQUEST_DATE);
                    dialog.show(fm, Configuration.DIALOG_DATE);
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });

        getLocationPermission();
        MapView mapView = view.findViewById(R.id.map_view);
        mapViewWrapper = new MapViewWrapper(mapView, this, locationPermissionGranted);
        mapViewWrapper.createMap();

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

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    mapViewWrapper.locationPermissionGranted();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DATE) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Setting date: " + date.toString());
                dateButton.setText(formatter.format(date));
            } else if (requestCode == REQUEST_IMAGE_PATH) {
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Image Path Received.");
                imagePath = (String) data.getExtras().get("imagePath");
                ImageView imageView = getActivity().findViewById(R.id.place_image);
                imageView.setImageURI(Uri.parse(imagePath));
            } else if (requestCode == REQUEST_HOLIDAY_TRIP){
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Holiday received.");
                Toast.makeText(getContext(), "Holiday Chosen.", Toast.LENGTH_SHORT).show();
                int id = (int) data.getExtras().get(Configuration.ITEM_ID);
                String title = (String) data.getExtras().get(Configuration.ITEM_TITLE);
                Button associateButton = getActivity().findViewById(R.id.associate_holiday_button);
                holidayId = id;
                title = "Holiday: " + title;
                associateButton.setText(title);
            } else if (requestCode == MapViewWrapper.REQUEST_PLACE) {
                com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(Configuration.TAG, "PhotoEditableBaseFragment#onActivityResult: Place received. Name: "
                        + place.getName()
                        + ", ID: " + place.getId()
                        + ", LatLng: " + place.getLatLng());
                mapViewWrapper.placeMarkerAndZoom(place.getLatLng());
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == REQUEST_IMAGE_PATH){
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Image Path Removed.");
                imagePath = null;
                ImageView imageView = getActivity().findViewById(R.id.place_image);
                imageView.setImageResource(R.drawable.photo_not_found);
            }
        }
    }

    @Override
    protected void saveItem() {
        View view = getView();

        String title = ((EditText) view.findViewById(R.id.place_title)).getText().toString();
        String notes = ((EditText) view.findViewById(R.id.place_notes)).getText().toString();
        String date = ((Button) view.findViewById(R.id.place_date_button)).getText().toString();


        if(title == null || title.trim().length() == 0) {
            Toast.makeText(getContext(), "Please enter a place name.", Toast.LENGTH_SHORT).show();
        } else {
            Place place = new Place();

            if(placeId != -1){
                place.setId(placeId);
            }

            place.setTitle(title);
            place.setNotes(notes);
            place.setDate(date);

            if(imagePath != null && !imagePath.equals("")){
                place.setPhotoPath(imagePath);
            }

            if(holidayId != -1){
                place.setHolidayId(holidayId);
            }

            try {
                LatLng location = mapViewWrapper.getGoogleMap().getCameraPosition().target;
                place.setLatitude(location.latitude);
                place.setLongitude(location.longitude);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            savePlaceToDatabase(place);
            Toast.makeText(getContext(), "Place Saved!", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
    }

    protected abstract void savePlaceToDatabase(Place place);

}
