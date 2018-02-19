package com.mikepconroy.traveljournal.fragments.holidays;

import android.os.AsyncTask;
import android.util.Log;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;

public class NewHolidayFragment extends HolidayEditableBaseFragment{

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


