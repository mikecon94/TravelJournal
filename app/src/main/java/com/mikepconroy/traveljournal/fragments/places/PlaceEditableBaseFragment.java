package com.mikepconroy.traveljournal.fragments.places;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.HolidayChooserActivity;
import com.mikepconroy.traveljournal.MapViewWrapper;
import com.mikepconroy.traveljournal.PhotoChooserActivity;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.DatePickerFragment;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;
import com.mikepconroy.traveljournal.model.db.Place;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class PlaceEditableBaseFragment extends EditableBaseFragment {

    protected static final int REQUEST_IMAGE_PATH = 0;
    protected static final int REQUEST_HOLIDAY_TRIP = 1;
    protected static final int REQUEST_DATE = 2;

    private SimpleDateFormat formatter = new SimpleDateFormat(Configuration.DATE_PATTERN);
    private Button dateButton;

    private String imagePath;
    private MapViewWrapper mapViewWrapper;

    protected int holidayId = -1;

    //If placeId is -1 then we are inserting.
    protected int placeId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Configuration.TAG, "PlaceBaseFragment#onCreate: Creating.");
        if (getArguments() != null) {
            placeId = getArguments().getInt("holidayId");
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

        MapView mapView = view.findViewById(R.id.map_view);
        mapViewWrapper = new MapViewWrapper(mapView, this);
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
                com.google.android.gms.location.places.Place place = PlacePicker.getPlace(getActivity(), data);
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
