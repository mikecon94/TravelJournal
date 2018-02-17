package com.mikepconroy.traveljournal.fragments.photos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class NewPhotoFragment extends EditableBaseFragment {

    protected static final int REQUEST_PLACE = 0;
    protected static final int REQUEST_IMAGE = 1;
    protected static final int REQUEST_HOLIDAY_TRIP = 2;

    private boolean mapCreated = false;

    private GoogleMap googleMap;
    private Uri imageUri;
    private LatLng photoLocation;
    private int holidayId = -1;
    private int tripId = -1;

    public NewPhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewPhotoFragment#onCreateView: Creating View.");
        final View view = inflater.inflate(R.layout.fragment_photo_edit_base, container, false);

        ImageView image = view.findViewById(R.id.holiday_image);
        image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
            }
        });

        Button chooseTripHolidayButton = view.findViewById(R.id.associate_image_button);
        chooseTripHolidayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), HolidayAndTripChooserActivity.class);
                startActivityForResult(i, REQUEST_HOLIDAY_TRIP);
            }
        });

        final CheckBox checkBox = view.findViewById(R.id.location_enabled);
        if(isNetworkAvailable()){
            checkBox.setChecked(true);
            createMap(view);
        } else {
            checkBox.setChecked(false);
            view.findViewById(R.id.map_view).setVisibility(View.GONE);
        }

        LinearLayout locationEnableLayout = view.findViewById(R.id.location_enabled_layout);
        locationEnableLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    view.findViewById(R.id.map_view).setVisibility(View.GONE);
                } else {
                    if(isNetworkAvailable()){
                        checkBox.setChecked(true);
                        view.findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                        if(!mapCreated){
                            createMap(view);
                        }
                    } else {
                        Toast.makeText(getContext(), "No internet available to display map.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void createMap(View view){

        final MapView mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(Bundle.EMPTY);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                MapsInitializer.initialize(getContext());

                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.i(Configuration.TAG, "NewPhotoFragment: Map clicked. Launching PlacePicker.");
                        //TODO: Restrict the Place API Key to this app only.
                        if (isNetworkAvailable()) {

                            VisibleRegion mapBounds = googleMap.getProjection().getVisibleRegion();

                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            builder.setLatLngBounds(mapBounds.latLngBounds);

                            try {
                                startActivityForResult(builder.build(getActivity()), REQUEST_PLACE);
                            } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Location chooser unavailable.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }}
                });

                NewPhotoFragment.this.googleMap = googleMap;

                //TODO: Update this to show current location. (Update target sdk back to 26).
                //Set location to Aston University.
                LatLng location = new LatLng(52.486864, -1.888372);
                placeMarkerAndZoom(location);
                mMapView.onResume();
                mapCreated = true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PLACE) {
                //TODO: Store place in database.
                Place place = PlacePicker.getPlace(getActivity(), data);
                placeMarkerAndZoom(place.getLatLng());
            } else if (requestCode == REQUEST_IMAGE){
                Log.i(Configuration.TAG, "NewPhotoFragment#onActivityResult: Image Received.");

                //TODO: The image resets on rotate.
                //TODO: Update this to store the image in a Photo Entity (?).
                Uri uri = data.getData();
                this.imageUri = uri;
                displayImage(uri);
            } else if (requestCode == REQUEST_HOLIDAY_TRIP) {
                Log.i(Configuration.TAG, "NewPhotoFragment#onActivityResult: Holiday or Trip received.");
                Toast.makeText(getContext(), "Holiday / Trip Chosen.", Toast.LENGTH_SHORT).show();
                int id = (int) data.getExtras().get(Configuration.ITEM_ID);
                String title = (String) data.getExtras().get(Configuration.ITEM_TITLE);
                String type = (String) data.getExtras().get(Configuration.ITEM_TYPE);

                Button associateButton = getActivity().findViewById(R.id.associate_image_button);

                if(type.equals(Configuration.HOLIDAY_ITEM)){
                    holidayId = id;
                    tripId = -1;
                    title = "Holiday: " + title;
                } else if (type.equals(Configuration.TRIP_ITEM)){
                    tripId = id;
                    holidayId = -1;
                    title = "Place: " + title;
                }
                associateButton.setText(title);
            }
        }
    }

    private void displayImage(Uri uri){
        Log.i(Configuration.TAG, "Image URI: " + uri.toString());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            ImageView imageView = getActivity().findViewById(R.id.holiday_image);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void placeMarkerAndZoom(LatLng location){
        //TODO: store this location as on rotate resets it.
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(location));
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(location, 17.0f);
        googleMap.animateCamera(camUpdate);
        photoLocation = location;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("New Photo", false);
    }

    @Override
    protected void saveItem() {
        //TODO: Implement this method.
    }
}
