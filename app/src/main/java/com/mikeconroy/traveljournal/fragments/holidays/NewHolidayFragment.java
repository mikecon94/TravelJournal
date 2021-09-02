package com.mikeconroy.traveljournal.fragments.holidays;

import android.os.AsyncTask;
import android.util.Log;

import com.mikeconroy.traveljournal.Configuration;
import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.db.AppDatabase;
import com.mikeconroy.traveljournal.db.Holiday;

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