package com.mikepconroy.traveljournal.fragments.places;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayDetailsFragment;
import com.mikepconroy.traveljournal.fragments.photos.PhotoDetailsFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Place;

public class PlaceDetailsFragment extends Fragment {

    private static final String PLACE_ID = "placeId";
    private int placeId;

    private OnFragmentUpdateListener mListener;

    public PlaceDetailsFragment() {}

    public static PlaceDetailsFragment newInstance(int placeId) {
        Log.i(Configuration.TAG, "PlaceDetailsFragment#newInstance: Creating new instance.");
        PlaceDetailsFragment fragment = new PlaceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeId = getArguments().getInt(PLACE_ID);
            Log.i(Configuration.TAG, "PlaceDetailsFragment#onCreate: Place ID: " + placeId);
        } else {
            Log.e(Configuration.TAG, "Place Details opened without Place ID");
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentUpdateListener) {
            mListener = (OnFragmentUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "PlaceDetailsFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_white_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "PlaceDetailsFragment: FAB Clicked.");
                //Start the Edit Holiday Fragment.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, EditPlaceFragment.newInstance(placeId));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        new LoadPlaceTask().execute(placeId);

        return view;
    }

    private void updatePlaceDetailsDisplay(Place place){
        if(place == null){
            Log.e(Configuration.TAG, "PlaceDetailsFragment: Place not found.");
            Toast.makeText(getContext(), "Place not found :(.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            mListener.onFragmentOpened(place.getTitle(), false);

            View view = getView();

            if(place.getPhotoPath() != null && !place.getPhotoPath().equals("")){
                ImageView imageView = view.findViewById(R.id.place_image);
                imageView.setImageURI(Uri.parse(place.getPhotoPath()));
            }

            if(place.getNotes() != null && !place.getNotes().equals("")){
                TextView notes = view.findViewById(R.id.place_notes);
                notes.setText(place.getNotes());
            }

            if(place.getDate() != null && !place.getDate().equals("")){
                TextView placeDate = view.findViewById(R.id.place_date_text);
                placeDate.setText(place.getDate());
            }

            Button associatedHolBut = getView().findViewById(R.id.associate_holiday_button);
            if(place.getHolidayId() != 0){
                Log.i(Configuration.TAG, "PlaceDetailsFragment: Place is associated with a holiday: " + place.getHolidayId());
                associatedHolBut.setText("Loading Associated Holiday");
                new LoadHolidayTask().execute(place.getHolidayId());
            } else {
                associatedHolBut.setText("No Holiday Associated");
                associatedHolBut.setClickable(false);
            }

            if(place.getLatitude() != 0 && place.getLongitude() != 0) {
                Log.i(Configuration.TAG, "PlaceDetailsFragment: Displaying MapView.");
                getActivity().findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                createMap(getView(), latLng);
            }

        }
    }

    private void createMap(View view, final LatLng location){
        final MapView mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(Bundle.EMPTY);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                MapsInitializer.initialize(getContext());
                googleMap.getUiSettings().setAllGesturesEnabled(false);

                googleMap.addMarker(new MarkerOptions().position(location));
                CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(location, 17.0f);
                googleMap.animateCamera(camUpdate);

                mMapView.onResume();
            }
        });
    }


    //TODO Move this and have the holiday details load as a fragment in a view pager.
    private void updateAssociatedHolidayButton(final Holiday holiday){
        Button associateHolidayBut = getView().findViewById(R.id.associate_holiday_button);
        associateHolidayBut.setText("View Holiday: " + holiday.getTitle());
        associateHolidayBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "PlaceDetailsFragment: Opening Holiday Details Fragment with ID: " + holiday.getId());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, HolidayDetailsFragment.newInstance(holiday.getId()));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
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

    private class LoadHolidayTask extends AsyncTask<Integer, Void, Holiday> {
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
