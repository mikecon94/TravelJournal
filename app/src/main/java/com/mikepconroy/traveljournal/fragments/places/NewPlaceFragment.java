package com.mikepconroy.traveljournal.fragments.places;

import android.os.AsyncTask;

import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Place;

public class NewPlaceFragment extends PlaceEditableBaseFragment {

    public NewPlaceFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("New Place", false);
    }

    @Override
    protected void savePlaceToDatabase(Place place) {
        new InsertPlaceTask().execute(place);
    }

    private class InsertPlaceTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... places) {
            AppDatabase.getInstance(getContext()).placeDao().insertPlace(places[0]);
            return null;
        }
    }

}
