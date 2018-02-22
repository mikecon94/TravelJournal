package com.mikepconroy.traveljournal.fragments.photos;

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
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.holidays.EditHolidayFragment;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayDetailsFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Photo;

public class PhotoDetailsFragment extends Fragment {

    private static final String PHOTO_ID = "photoId";
    private int photoId;

    private OnFragmentUpdateListener mListener;

    public PhotoDetailsFragment() {}

    public static PhotoDetailsFragment newInstance(int photoId) {
        Log.i(Configuration.TAG, "PhotoDetailsFragment#newInstance: Creating new instance.");
        PhotoDetailsFragment fragment = new PhotoDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(PHOTO_ID, photoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoId = getArguments().getInt(PHOTO_ID);
            Log.i(Configuration.TAG, "PhotoDetailsFragment#onCreate: Photo ID: " + photoId);
        } else {
            Log.e(Configuration.TAG, "Photo Details opened without Photo ID");
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "PhotoDetailsFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_photo_details, container, false);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_white_24dp);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: FAB Clicked.");
                //Start the Edit Holiday Fragment.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.replace(R.id.fragment_container, EditPhotoFragment.newInstance(photoId));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        new PhotoDetailsFragment.LoadPhoto().execute(photoId);

        return view;
    }

    private void updatePhotoDetailsDisplay(Photo photo){
        if(photo == null){
            Log.e(Configuration.TAG, "PhotoDetailsFragment: Photo not found.");
            Toast.makeText(getContext(), "Photo not found :(.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Log.i(Configuration.TAG, "PhotoDetailsFragment: Displaying photo with path: " + photo.getImagePath());
            mListener.onFragmentOpened("Photo Details", false);

            ImageView imageView = getActivity().findViewById(R.id.photo_image);
            imageView.setImageURI(Uri.parse(photo.getImagePath()));

            TextView tags = getActivity().findViewById(R.id.image_tags);
            if(!photo.getTags().equals("")) {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Tags on photo.");
                tags.setText(photo.getTags());
            } else {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: No tags on photo.");
                tags.setTextColor(getResources().getColor(R.color.noTagsMessage));
                tags.setHint("No tags set.");
            }

            Button viewAssociatedTrip = getView().findViewById(R.id.view_associated_trip);
            if(photo.getHolidayId() != 0){
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Photo is associated with a holiday: " + photo.getHolidayId());
                viewAssociatedTrip.setText("Loading Associated Holiday");
                new LoadHoliday().execute(photo.getHolidayId());
            } else if (photo.getPlaceId() != 0){
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Photo is associated with a place." + photo.getPlaceId());
                viewAssociatedTrip.setText("Loading Associated Place");
                //TODO Load Place.
            } else {
                //TODO Maybe remove the button if there is no associated trip.
                viewAssociatedTrip.setText("No associated trip");
                viewAssociatedTrip.setClickable(false);
            }

            if(photo.getLatitude() != 0 && photo.getLongitude() != 0) {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Displaying MapView.");
                getActivity().findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                LatLng latLng = new LatLng(photo.getLatitude(), photo.getLongitude());
                createMap(getView(), latLng);
            } else {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Latitude or Longitude set to 0");
            }
        }

    }

    private void updateAssociatedTripButton(final Holiday holiday){
        Button viewAssociatedTrip = getView().findViewById(R.id.view_associated_trip);
        viewAssociatedTrip.setText("View Holiday: " + holiday.getTitle());
        viewAssociatedTrip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "PhotoDetailsFragment: Opening Holiday Details Fragment with ID: " + holiday.getId());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, HolidayDetailsFragment.newInstance(holiday.getId()));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
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

    private class LoadPhoto extends AsyncTask<Integer, Void, Photo> {
        @Override
        protected Photo doInBackground(Integer... photoId) {
            Log.i(Configuration.TAG, "PhotoDetailsFragment#doInBackground: Finding photo with ID: " + photoId[0]);
            return AppDatabase.getInstance(getContext()).photoDao().findPhotoById(photoId[0]);
        }

        @Override
        protected void onPostExecute(Photo photo) {
            updatePhotoDetailsDisplay(photo);
        }
    }

    private class LoadHoliday extends AsyncTask<Integer, Void, Holiday> {
        @Override
        protected Holiday doInBackground(Integer... holidayId) {
            Log.i(Configuration.TAG, "PhotoDetailsFragment#doInBackground: Finding holiday with ID: " + holidayId[0]);
            return AppDatabase.getInstance(getContext()).holidayDao().findHolidayById(holidayId[0]);
        }

        @Override
        protected void onPostExecute(Holiday holiday) {
            updateAssociatedTripButton(holiday);
        }
    }
}
