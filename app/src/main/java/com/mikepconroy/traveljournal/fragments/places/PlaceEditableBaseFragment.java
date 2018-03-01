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
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.HolidayChooserActivity;
import com.mikepconroy.traveljournal.PhotoChooserActivity;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.DatePickerFragment;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;

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
            if (requestCode == REQUEST_DATE){
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Setting date: " + date.toString());
                dateButton.setText(formatter.format(date));
            } else if (requestCode == REQUEST_IMAGE_PATH){
                Log.i(Configuration.TAG, "PlaceEditableBaseFragment#onActivityResult: Image Path Received.");
                imagePath = (String) data.getExtras().get("imagePath");
                ImageView imageView = getActivity().findViewById(R.id.place_image);
                imageView.setImageURI(Uri.parse(imagePath));
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

    protected abstract void saveItem();

}
