package com.mikepconroy.traveljournal.fragments.holidays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.DatePickerFragment;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;
import com.mikepconroy.traveljournal.fragments.photos.HolidayAndTripChooserActivity;
import com.mikepconroy.traveljournal.model.db.Holiday;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mikecon on 11/02/2018.
 */

public abstract class HolidayEditableBaseFragment extends EditableBaseFragment {

    protected static final String DIALOG_DATE = "date";
    protected static final int REQUEST_START_DATE = 0;
    protected static final int REQUEST_END_DATE = 1;
    protected static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_IMAGE_PATH = 3;

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);

    private Button startDateButton;
    private Button endDateButton;

    private String imagePath;

    //If holidayId is -1 then we are inserting.
    protected int holidayId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Configuration.TAG, "HolidayBaseFragment#onCreate: Creating.");
        if (getArguments() != null) {
            holidayId = getArguments().getInt("holidayId");
            Log.i(Configuration.TAG, "HolidayBaseFragment#onCreate: Holiday ID: " + holidayId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewHolidayFragment#onCreateView: Creating View.");

        View view = inflater.inflate(R.layout.fragment_holiday_edit_base, container, false);

        ImageView holidayPhoto = view.findViewById(R.id.holiday_image);
        holidayPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
                Intent i = new Intent(getActivity(), PhotoChooserActivity.class);
                startActivityForResult(i, REQUEST_IMAGE_PATH);
            }
        });

        startDateButton = view.findViewById(R.id.holiday_start_date);
        String today = formatter.format(new Date());
        startDateButton.setText(today);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                try {
                    Date startDate = formatter.parse(startDateButton.getText().toString());
                    DatePickerFragment dialog = DatePickerFragment.newInstance(startDate);
                    dialog.setTargetFragment(HolidayEditableBaseFragment.this, REQUEST_START_DATE);
                    dialog.show(fm, DIALOG_DATE);
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });

        endDateButton = view.findViewById(R.id.holiday_end_date);
        endDateButton.setText(today);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                try {
                    Date endDate = formatter.parse(endDateButton.getText().toString());
                    DatePickerFragment dialog = DatePickerFragment.newInstance(endDate);
                    dialog.setTargetFragment(HolidayEditableBaseFragment.this, REQUEST_END_DATE);
                    dialog.show(fm, DIALOG_DATE);
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        });

        Button saveButton = view.findViewById(R.id.save_holiday_button);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i(Configuration.TAG, "HolidayBaseFragment#onCreateView: Save Clicked.");
                saveItem();
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_START_DATE) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Setting start date: " + date.toString());
                String startDate = formatter.format(date);
                startDateButton.setText(startDate);
            } else if (requestCode == REQUEST_END_DATE){
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Setting end date: " + date.toString());
                String endDate = formatter.format(date);
                endDateButton.setText(endDate);
            } else if (requestCode == REQUEST_IMAGE){
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Image Received.");

                //TODO: Update this to store the image in a Photo Entity (?).
                //TODO: Change the photo chooser to choose from image list (with images unassociated or appropriate holiday/trip).
                Uri uri = data.getData();
                Log.i(Configuration.TAG, "Image URI: " + uri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    ImageView imageView = getActivity().findViewById(R.id.holiday_image);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_PATH){
                //TODO!!!
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Image Path Received.");
                Toast.makeText(getContext(), "Photo Chosen.", Toast.LENGTH_SHORT).show();
                imagePath = (String) data.getExtras().get("imagePath");
                ImageView imageView = getActivity().findViewById(R.id.holiday_image);
                imageView.setImageURI(Uri.parse(imagePath));
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == REQUEST_IMAGE_PATH){
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Image Path Removed.");
                imagePath = null;
                ImageView imageView = getActivity().findViewById(R.id.holiday_image);
                imageView.setImageResource(R.drawable.photo_not_found);
            }
        }
    }

    protected void saveItem(){
        View view = getView();
        String name = ((EditText) view.findViewById(R.id.holiday_title)).getText().toString();
        String notes = ((EditText) view.findViewById(R.id.holiday_notes)).getText().toString();
        String startDate = ((Button) view.findViewById(R.id.holiday_start_date)).getText().toString();
        String endDate = ((Button) view.findViewById(R.id.holiday_end_date)).getText().toString();

        if(name.trim().length() == 0){
            Log.i(Configuration.TAG, "HolidayBaseFragment#saveHoliday: User didn't enter Holiday Name.");
            Toast.makeText(getContext(), "Please enter a holiday name.", Toast.LENGTH_SHORT).show();
        } else {
            Holiday holiday = new Holiday();

            //HolidayId is -1 then we are creating a new holiday otherwise updating one.
            if(holidayId != -1) {
                holiday.setId(holidayId);
            }
            holiday.setTitle(name);
            holiday.setNotes(notes);
            holiday.setStartDate(startDate);
            holiday.setEndDate(endDate);
            holiday.setProfilePhotoPath(imagePath);
            Log.i(Configuration.TAG, "saveHoliday: Saving Holiday with name: " + holiday.getTitle());
            saveHolidayToDatabase(holiday);

            Toast.makeText(getContext(), "Holiday Saved!", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
    }

   protected abstract void saveHolidayToDatabase(Holiday holiday);

}
