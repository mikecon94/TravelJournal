package com.mikeconroy.traveljournal.fragments.photos;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.mikeconroy.traveljournal.Configuration;
import com.mikeconroy.traveljournal.HolidayAndTripChooserActivity;
import com.mikeconroy.traveljournal.MapViewWrapper;
import com.mikeconroy.traveljournal.NetworkChecker;
import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.fragments.EditableBaseFragment;
import com.mikeconroy.traveljournal.db.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public abstract class PhotoEditableBaseFragment extends EditableBaseFragment {

    protected static final int REQUEST_IMAGE = 1;
    protected static final int REQUEST_HOLIDAY_TRIP = 2;

    //We need to track these when they are set as the views do not store them.
    protected String imagePath;
    protected int holidayId = -1;
    protected int tripId = -1;

    protected MapViewWrapper mapViewWrapper;
    private NetworkChecker netChecker;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 566;
    private boolean locationPermissionGranted;

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

        MapView mapView = view.findViewById(R.id.map_view);
        netChecker = new NetworkChecker(getContext());

        if(netChecker.isNetworkAvailable()){
            checkBox.setChecked(true);
            getLocationPermission();
            mapViewWrapper = new MapViewWrapper(mapView, this, locationPermissionGranted);
            mapViewWrapper.createMap();
        } else {
            checkBox.setChecked(false);
            mapView.setVisibility(View.GONE);
        }

        LinearLayout locationEnableLayout = view.findViewById(R.id.location_enabled_layout);
        locationEnableLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    checkBox.setChecked(false);
                    view.findViewById(R.id.map_view).setVisibility(View.GONE);
                } else {
                    if(netChecker.isNetworkAvailable()){
                        checkBox.setChecked(true);
                        view.findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                        if(!mapViewWrapper.isMapCreated()){
                            mapViewWrapper.createMap();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MapViewWrapper.REQUEST_PLACE) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(Configuration.TAG, "PhotoEditableBaseFragment#onActivityResult: Place received. Name: "
                        + place.getName()
                        + ", ID: " + place.getId()
                        + ", ID: " + place.getLatLng());
                mapViewWrapper.placeMarkerAndZoom(place.getLatLng());

//                Place place = PlacePicker.getPlace(getActivity(), data);
//                mapViewWrapper.placeMarkerAndZoom(place.getLatLng());
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
                } else if (type.equals(Configuration.PLACE_ITEM)){
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


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    mapViewWrapper.locationPermissionGranted();
                }
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
                imagePath = saveImage(bitmap, UUID.randomUUID().toString() + ".png");
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
        File directory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
                    LatLng location = mapViewWrapper.getGoogleMap().getCameraPosition().target;
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
