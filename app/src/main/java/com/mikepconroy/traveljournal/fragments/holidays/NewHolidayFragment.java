package com.mikepconroy.traveljournal.fragments.holidays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.MainActivity;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.DatePickerFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewHolidayFragment extends Fragment implements OnBackPressListener {

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_END_DATE = 1;

    private SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
    private Button startDateButton;
    private Button endDateButton;

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private NewHolidayFragmentInteractionListener mListener;

    public NewHolidayFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Configuration.TAG, "NewHolidayFragment#onCreate: Creating.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewHolidayFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_new_holiday, container, false);

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
                    dialog.setTargetFragment(NewHolidayFragment.this, REQUEST_START_DATE);
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
                    dialog.setTargetFragment(NewHolidayFragment.this, REQUEST_END_DATE);
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
                saveHoliday();
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
                Log.i(Configuration.TAG, "NewHolidayFragment#onActivityResult: Setting start date: " + date.toString());
                String startDate = formatter.format(date);
                startDateButton.setText(startDate);
            } else if (requestCode == REQUEST_END_DATE){
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                Log.i(Configuration.TAG, "NewHolidayFragment#onActivityResult: Setting end date: " + date.toString());
                String endDate = formatter.format(date);
                endDateButton.setText(endDate);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(Configuration.TAG, "NewHolidayFragment#onAttach: Attaching.");
        if (context instanceof NewHolidayFragmentInteractionListener) {
            mListener = (NewHolidayFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HolidayDetailsInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "NewHolidayFragment#onResume: Resuming.");
        String title = getActivity().getResources().getString(R.string.add_holiday_title);
        getActivity().setTitle(title);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Configuration.TAG, "NewHolidayFragment#onDetach: Detaching.");

        if (mListener != null) {
            mListener.onFragmentClose();
        }

        mListener = null;
    }

    public void onBackPressed(){
        Log.i(Configuration.TAG, "NewHolidayFragment#onBackPressed");

        new AlertDialog.Builder(getContext()).setTitle("Back Pressed").setMessage("Closing.")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveHoliday();
                    }
                }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).setCancelable(true).show();
    }

    private void saveHoliday(){
        View view = getView();
        String name = ((EditText) view.findViewById(R.id.holiday_title)).getText().toString();
        String notes = ((EditText) view.findViewById(R.id.holiday_notes)).getText().toString();
        String startDate = ((Button) view.findViewById(R.id.holiday_start_date)).getText().toString();
        String endDate = ((Button) view.findViewById(R.id.holiday_end_date)).getText().toString();

        if(name.trim().length() == 0){
            Log.i(Configuration.TAG, "NewHolidayFragment#saveHoliday: User didn't enter Holiday Name.");
            Toast.makeText(getContext(), "Please enter a holiday name.", Toast.LENGTH_SHORT).show();
        } else {
            final Holiday holiday = new Holiday();
            holiday.setTitle(name);
            holiday.setNotes(notes);
            holiday.setStartDate(startDate);
            holiday.setEndDate(endDate);

            new InsertHolidayTask().execute(holiday);

            Toast.makeText(getContext(), "Holiday Saved!", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
    }

    public interface NewHolidayFragmentInteractionListener {
        void onFragmentClose();
    }

    private class InsertHolidayTask extends AsyncTask<Holiday, Void, Void>{
        @Override
        protected Void doInBackground(Holiday... holidays) {
            AppDatabase.getInstance(getContext()).holidayDao().insertHoliday(holidays[0]);
            return null;
        }
    }

}


