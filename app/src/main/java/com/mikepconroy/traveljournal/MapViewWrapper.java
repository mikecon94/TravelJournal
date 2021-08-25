package com.mikepconroy.traveljournal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MapViewWrapper {

    public static final int REQUEST_PLACE = 837;

    private GoogleMap googleMap;
    private MapView mapView;
    private Fragment fragment;
    private Activity activity;

    private boolean mapCreated = false;
    private NetworkChecker netChecker;

    public MapViewWrapper(MapView mapView, Fragment fragment){
        this.mapView = mapView;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        netChecker = new NetworkChecker(activity);
    }

    public void createMap(){
        Log.i(Configuration.TAG, "MapViewWrapper: Creating Map");
        mapView.onCreate(Bundle.EMPTY);
        mapView.getMapAsync(googleMap -> {
            MapsInitializer.initialize(activity);

            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.i(Configuration.TAG, "MapViewWrapper: Map clicked. Launching PlacePicker.");
                    if (netChecker.isNetworkAvailable()) {
                        Log.i(Configuration.TAG, "MapViewWrapper: Network Available.");
                        //showPlacePicker();
                        showPlacesAutocomplete();
                    } else {
                        Toast.makeText(activity, "No internet connection.", Toast.LENGTH_SHORT).show();
                    }}
            });

            MapViewWrapper.this.googleMap = googleMap;
            Log.i(Configuration.TAG, "MapViewWrapper: Map Created.");

            //TODO: Update this to show current location. (Update target sdk back to 26).
            //Set location to Aston University.
            LatLng location = new LatLng(52.486864, -1.888372);
            placeMarkerAndZoom(location);
            mapView.onResume();
            mapCreated = true;
        });
    }

    private void showPlacesAutocomplete(){
        String geoApiKey = (String) fragment.getContext().getText(R.string.GEO_API_KEY);
        if (!Places.isInitialized()) {
            Places.initialize(fragment.getContext().getApplicationContext(), geoApiKey);
        }
        Log.i(Configuration.TAG, "MapViewWrapper#showPlacesAutocomplete: Show Autocomplete."
                + geoApiKey);

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(activity);
        fragment.startActivityForResult(intent, REQUEST_PLACE);
    }

    private void showPlacePicker(){
//        VisibleRegion mapBounds = googleMap.getProjection().getVisibleRegion();
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//        builder.setLatLngBounds(mapBounds.latLngBounds);
//
//        try {
//            fragment.startActivityForResult(builder.build(activity), REQUEST_PLACE);
//        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//            Toast.makeText(activity, "Location chooser unavailable.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void placeMarkerAndZoom(LatLng location){
        Log.i(Configuration.TAG, "MapViewWrapper: Placing Marker.");
        if(googleMap == null) {
            Log.e(Configuration.TAG, "MapViewWrapper:  GoogleMap has not been set. Call the createMap method first.");
            throw new NullPointerException();
        }
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(location));
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(location, 17.0f);
        googleMap.animateCamera(camUpdate);
    }

    public GoogleMap getGoogleMap(){
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public boolean isMapCreated(){
        return mapCreated;
    }

}
