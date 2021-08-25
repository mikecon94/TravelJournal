package com.mikepconroy.traveljournal.fragments.places;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Place;

public class EditPlaceFragment extends PlaceEditableBaseFragment {

    public static EditPlaceFragment newInstance(int placeId){
        EditPlaceFragment fragment = new EditPlaceFragment();
        Bundle args = new Bundle();
        args.putInt("placeId", placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.map_view).setVisibility(View.GONE);
        new LoadPlaceTask().execute(placeId);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("Edit Place", false);
    }

    private void updatePlaceDetailsDisplay(Place place){
        if(place == null) {
            Log.i(Configuration.TAG, "EditPlace#updatePlaceDetailsDisplay: Place not found.");
            Toast.makeText(getContext(), "Place not found :(.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {

            View view = getView();

            placeId = place.getId();

            EditText placeName = view.findViewById(R.id.place_title);
            placeName.setText(place.getTitle());

            if(place.getPhotoPath() != null && !place.getPhotoPath().equals("")) {
                ImageView image = view.findViewById(R.id.place_image);
                imagePath = place.getPhotoPath();
                image.setImageURI(Uri.parse(imagePath));
            }

            if(!place.getNotes().equals("")) {
                EditText placeNotes = view.findViewById(R.id.place_notes);
                placeNotes.setText(place.getNotes());
            }

            Button placeDate = view.findViewById(R.id.place_date_button);
            placeDate.setText(place.getDate());

            Button associateHolidayBut = getView().findViewById(R.id.associate_holiday_button);
            if(place.getHolidayId() != 0){
                associateHolidayBut.setText("Loading Associated Holiday");
                holidayId = place.getHolidayId();
                new LoadHoliday().execute(place.getHolidayId());
            }

            if(place.getLatitude() != 0 && place.getLongitude() != 0){
                Log.i(Configuration.TAG, "EditPlaceFragment#updatePlaceDetailsDisplay: LatLng are set.");
                getView().findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                Log.i(Configuration.TAG, "EditPlaceFragment#UpdatePlaceDetails: Placing Marker.");
                mapViewWrapper.placeMarkerAndZoom(new LatLng(place.getLatitude(), place.getLongitude()));
            }
        }
    }

    private void updateAssociatedHolidayButton(Holiday holiday){
        Button associateHolidayBut = getView().findViewById(R.id.associate_holiday_button);
        associateHolidayBut.setText("Holiday: " + holiday.getTitle());
    }

    private class LoadPlaceTask extends AsyncTask<Integer, Void, Place> {
        @Override
        protected Place doInBackground(Integer... placeId) {
            Log.i(Configuration.TAG, "Loading place: "+ placeId[0]);
            return AppDatabase.getInstance(getContext()).placeDao().findPlaceById(placeId[0]);
        }

        @Override
        protected void onPostExecute(Place place) {
            updatePlaceDetailsDisplay(place);
        }
    }

    @Override
    protected void savePlaceToDatabase(Place place) {
        new UpdatePlaceTask().execute(place);
    }

    private class UpdatePlaceTask extends AsyncTask<Place, Void, Void> {
        @Override
        protected Void doInBackground(Place... places) {
            AppDatabase.getInstance(getContext()).placeDao().updatePlace(places[0]);
            return null;
        }
    }

    private class LoadHoliday extends AsyncTask<Integer, Void, Holiday> {
        @Override
        protected Holiday doInBackground(Integer... holidayId) {
            Log.i(Configuration.TAG, "PlaceDetailsFragment#doInBackground: Finding holiday with ID: " + holidayId[0]);
            return AppDatabase.getInstance(getContext()).holidayDao().findHolidayById(holidayId[0]);
        }

        @Override
        protected void onPostExecute(Holiday holiday) {
            updateAssociatedHolidayButton(holiday);
        }
    }

}
