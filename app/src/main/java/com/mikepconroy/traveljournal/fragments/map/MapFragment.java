package com.mikepconroy.traveljournal.fragments.map;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.MapViewWrapper;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.places.NewPlaceFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Photo;
import com.mikepconroy.traveljournal.model.db.Place;

import java.util.List;

public class MapFragment extends Fragment {

    protected OnFragmentUpdateListener mListener;

    private GoogleMap gMap;


    public MapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        final MapView mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(Bundle.EMPTY);

        final Activity activity = getActivity();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                MapsInitializer.initialize(activity);
                MapFragment.this.gMap = googleMap;
                mapView.onResume();
            }
        });

        new LoadPlaces().execute();
        new LoadPhotos().execute();

        return view;
    }

    private void addPlaceMarkers(List<Place> places){
        for(Place place : places){
            if(place.getLongitude() != 0 && place.getLatitude() != 0) {
                LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
                gMap.addMarker(new MarkerOptions().position(location));
            }
        }
    }

    private void addPhotoMarkers(List<Photo> photos){
        for(Photo photo : photos){
            if(photo.getLongitude() != 0 && photo.getLatitude() != 0){
                LatLng location = new LatLng(photo.getLatitude(), photo.getLongitude());
                gMap.addMarker(new MarkerOptions().position(location));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(Configuration.TAG, "MapFragment#onAttach: Attaching.");
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
        Log.i(Configuration.TAG, "MapFragment#onDetach: Detaching.");
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("Map", true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        if(fab != null) {
            fab.setVisibility(View.GONE);
        }
    }

    private class LoadPlaces extends AsyncTask<Void, Void, List<Place>> {
        @Override
        protected List<Place> doInBackground(Void... params) {
            return AppDatabase.getInstance(getContext()).placeDao().getAllPlaces();
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            addPlaceMarkers(places);        }
    }

    private class LoadPhotos extends AsyncTask<Void, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Void... params) {
            return AppDatabase.getInstance(getContext()).photoDao().getAllPhotos();
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            addPhotoMarkers(photos);
        }
    }

}
