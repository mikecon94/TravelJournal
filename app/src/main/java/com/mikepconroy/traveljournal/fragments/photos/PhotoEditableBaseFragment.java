package com.mikepconroy.traveljournal.fragments.photos;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
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
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.EditableBaseFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public abstract class PhotoEditableBaseFragment extends EditableBaseFragment {

    protected static final int REQUEST_PLACE = 0;
    protected static final int REQUEST_IMAGE = 1;
    protected static final int REQUEST_HOLIDAY_TRIP = 2;

    //We need to track these when they are set as the views do not store them.
    protected String imagePath;
    protected int holidayId = -1;
    protected int tripId = -1;

    protected boolean mapCreated = false;
    protected GoogleMap googleMap;

    //If photoId is -1 then we are inserting.
    protected int photoId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoId = getArguments().getInt("photoId");
            Log.i(Configuration.TAG, "PhotoEditableBase#onCreate: Photo ID: " + photoId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "PhotoEditableBaseFragment#onCreateView: Creating View.");
        final View view = inflater.inflate(R.layout.fragment_photo_edit_base, container, false);

        ImageView image = view.findViewById(R.id.photo_image);
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

        Button saveItemButton = view.findViewById(R.id.save_image_button);
        saveItemButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

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
                        Log.i(Configuration.TAG, "PhotoEditableBaseFragment: Map clicked. Launching PlacePicker.");
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

                PhotoEditableBaseFragment.this.googleMap = googleMap;

                //TODO: Update this to show current location. (Update target sdk back to 26).
                //Set location to Aston University.
                LatLng location = new LatLng(52.486864, -1.888372);
                placeMarkerAndZoom(location);
                mMapView.onResume();
                mapCreated = true;
            }
        });
    }

    protected void placeMarkerAndZoom(LatLng location){
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(location));
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(location, 17.0f);
        googleMap.animateCamera(camUpdate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PLACE) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                placeMarkerAndZoom(place.getLatLng());
            } else if (requestCode == REQUEST_IMAGE){
                Log.i(Configuration.TAG, "PhotoEditableBaseFragment#onActivityResult: Image Received.");

                //TODO: The image resets on rotate.
                //TODO: Update this to store the image in a Photo Entity (?).
                Uri uri = data.getData();
                displayImage(uri);
            } else if (requestCode == REQUEST_HOLIDAY_TRIP) {
                Log.i(Configuration.TAG, "PhotoEditableBaseFragment#onActivityResult: Holiday or Trip received.");
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
        } else if(resultCode == Activity.RESULT_CANCELED){
            if(requestCode == REQUEST_IMAGE){
                Log.i(Configuration.TAG, "HolidayEditableBaseFragment#onActivityResult: Image Removed.");
                imagePath = null;
                ImageView imageView = getActivity().findViewById(R.id.photo_image);
                imageView.setImageResource(R.drawable.photo_not_found);
            }
        }
    }

    private void displayImage(Uri uri){
        Log.i(Configuration.TAG, "Image URI: " + uri.toString());
        ImageView imageView = getActivity().findViewById(R.id.photo_image);
        imageView.setImageURI(uri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            Log.i(Configuration.TAG, "displayImage: imagePath = " + imagePath);
            if(imagePath == null){
                imagePath = saveImage(bitmap, UUID.randomUUID().toString());
            } else {
                //The imagePath is already set so we overwrite the current image instead of creating a new one.
                File image = new File(imagePath);
                imagePath = saveImage(bitmap, image.getName());
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    //Takes a filename so when we edit photos users can overwrite current ones.
    private String saveImage(Bitmap image, String fileName){
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + "/" + fileName;
    }

    @Override
    protected void saveItem() {
        View view = getView();
        if(imagePath == null) {
            Toast.makeText(getContext(), "Please choose a photo.", Toast.LENGTH_SHORT).show();
        } else {

            String tags = ((EditText) view.findViewById(R.id.image_tags)).getText().toString();
            CheckBox addLocation = view.findViewById(R.id.location_enabled);

            Photo photo = new Photo();

            if(photoId != -1){
                photo.setId(photoId);
            }

            photo.setImagePath(imagePath);
            photo.setTags(tags);
            if (holidayId != -1) {
                photo.setHolidayId(holidayId);
            } else if (tripId != -1) {
                photo.setPlaceId(tripId);
            }

            if (addLocation.isChecked()) {
                try {
                    LatLng location = googleMap.getCameraPosition().target;
                    photo.setLatitude(location.latitude);
                    photo.setLongitude(location.longitude);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            savePhotoToDatabase(photo);

            Toast.makeText(getContext(), "Photo Saved!", Toast.LENGTH_SHORT).show();
            getFragmentManager().popBackStack();
        }
    }

    protected abstract void savePhotoToDatabase(Photo photo);

}
