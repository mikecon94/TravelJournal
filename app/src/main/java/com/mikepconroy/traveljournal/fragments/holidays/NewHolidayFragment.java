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

public class NewHolidayFragment extends HolidayBaseFragment {

    public NewHolidayFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "NewHolidayFragment#onResume: Resuming.");
        String title = getActivity().getResources().getString(R.string.add_holiday_title);
        mListener.onFragmentOpened(title, false);
    }

    public void saveHolidayToDatabase(Holiday holiday){
        new NewHolidayFragment.InsertHolidayTask().execute(holiday);
    }

    private class InsertHolidayTask extends AsyncTask<Holiday, Void, Void>{
        @Override
        protected Void doInBackground(Holiday... holidays) {
            AppDatabase.getInstance(getContext()).holidayDao().insertHoliday(holidays[0]);
            return null;
        }
    }
}


