package com.mikepconroy.traveljournal.fragments.holidays;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;

public class EditHolidayFragment extends HolidayEditableBaseFragment {

    public EditHolidayFragment() {}

    public static EditHolidayFragment newInstance(int holidayId) {
        Log.i(Configuration.TAG, "HolidayDetailsFragment#newInstance: Creating new instance.");
        EditHolidayFragment fragment = new EditHolidayFragment();
        Bundle args = new Bundle();
        args.putInt("holidayId", holidayId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "EditHolidayFragment#onResume: Resuming.");
        mListener.onFragmentOpened("Edit Holiday", false);
        //getActivity().setTitle(title);
        //mListener.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Log.i(Configuration.TAG, "EditHolidayFragment#onCreateView: Loading Holiday from DB.");

        new LoadHolidayTask().execute(holidayId);
        return view;
    }

    private void updateHolidayDetailsDisplay(Holiday holiday){
        if(holiday == null){
            Log.i(Configuration.TAG, "EditHolidayFragment#updateHolidayDetails: Holiday not found.");
            Toast.makeText(getContext(), "Holiday not found :(.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Log.i(Configuration.TAG, "EditHolidayFragment#updateHolidayDetails: Displaying holiday with name: " + holiday.getTitle());

            //TODO: Include Updating of image here.

            holidayId = holiday.getId();
            EditText nameField = getActivity().findViewById(R.id.holiday_title);
            nameField.setText(holiday.getTitle());
            EditText notesField = getActivity().findViewById(R.id.holiday_notes);
            notesField.setText(holiday.getNotes());
            Button startDateField = getActivity().findViewById(R.id.holiday_start_date);
            startDateField.setText(holiday.getStartDate());
            Button endDateField = getActivity().findViewById(R.id.holiday_end_date);
            endDateField.setText(holiday.getEndDate());
            ImageView profilePhoto = getActivity().findViewById(R.id.holiday_image);
            if(holiday.getProfilePhotoPath() != null && !holiday.getProfilePhotoPath().equals("")) {
                profilePhoto.setImageURI(Uri.parse(holiday.getProfilePhotoPath()));
            }
        }
    }

    public void saveHolidayToDatabase(Holiday holiday){
        new EditHolidayFragment.EditHolidayTask().execute(holiday);
    }

    private class LoadHolidayTask extends AsyncTask<Integer, Void, Holiday>{
        @Override
        protected Holiday doInBackground(Integer... holidayId) {
            return AppDatabase.getInstance(getContext()).holidayDao().findHolidayById(holidayId[0]);
        }

        @Override
        protected void onPostExecute(Holiday holiday) {
            updateHolidayDetailsDisplay(holiday);
        }
    }

    private class EditHolidayTask extends AsyncTask<Holiday, Void, Void>{
        @Override
        protected Void doInBackground(Holiday... holidays) {
            AppDatabase.getInstance(getContext()).holidayDao().updateHoliday(holidays[0]);
            return null;
        }
    }
}


