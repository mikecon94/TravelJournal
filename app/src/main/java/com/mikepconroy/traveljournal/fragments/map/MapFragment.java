package com.mikepconroy.traveljournal.fragments.map;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.photos.PhotoDetailsFragment;
import com.mikepconroy.traveljournal.fragments.places.PlaceDetailsFragment;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Photo;
import com.mikepconroy.traveljournal.model.db.Place;

import java.util.List;

public class MapFragment extends Fragment implements ClusterManager.OnClusterItemInfoWindowClickListener<ClusterWrapper> {

    protected OnFragmentUpdateListener mListener;

    private GoogleMap gMap;
    private ClusterWrapper clickedClusterItem;
    private ClusterManager<ClusterWrapper> clusterManager;

    public MapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        final MapView mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(Bundle.EMPTY);

        final Activity activity = getActivity();

        mapView.getMapAsync(googleMap -> {
            MapsInitializer.initialize(activity);
            MapFragment.this.gMap = googleMap;

            gMap.getUiSettings().setZoomControlsEnabled(true);

            clusterManager = new ClusterManager<>(activity, gMap);
            gMap.setOnCameraIdleListener(clusterManager);
            gMap.setOnMarkerClickListener(clusterManager);
            gMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

            gMap.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterItemInfoWindowClickListener(MapFragment.this);
            clusterManager.getMarkerCollection().setInfoWindowAdapter(new CustomInfoWindow());

            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterWrapper>() {
                @Override
                public boolean onClusterItemClick(ClusterWrapper item) {
                    clickedClusterItem = item;
                    return false;
                }
            });

            mapView.onResume();
        });

        new LoadPlaces().execute();
        new LoadPhotos().execute();

        return view;
    }

    private void addPlaceMarkers(List<Place> places){
        for(Place place : places){
            if(place.getLongitude() != 0 && place.getLatitude() != 0) {
                LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
                ClusterWrapper item = new ClusterWrapper(location, place.getId(), place.getTitle(), place.getNotes());
                clusterManager.addItem(item);
            }
        }
    }

    private void addPhotoMarkers(List<Photo> photos){
        for(Photo photo : photos){
            if(photo.getLongitude() != 0 && photo.getLatitude() != 0){
                LatLng location = new LatLng(photo.getLatitude(), photo.getLongitude());
                ClusterWrapper item = new ClusterWrapper(location, photo.getId(), photo.getImagePath());
                clusterManager.addItem(item);
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

    @Override
    public void onClusterItemInfoWindowClick(ClusterWrapper clusterWrapper) {
        Log.i(Configuration.TAG, "MapFragment: Info Window clicked, opening details");
        Fragment fragment;
        if(clusterWrapper.getImageLocation() == null){
            fragment = PlaceDetailsFragment.newInstance(clusterWrapper.getId());
        } else {
            fragment = PhotoDetailsFragment.newInstance(clusterWrapper.getId());
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
    public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

        private final View view;

        CustomInfoWindow() {
            view = getLayoutInflater().inflate(
                    R.layout.map_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            TextView title= view.findViewById(R.id.text_title);
            TextView snippet = view.findViewById(R.id.text_snippet);
            ImageView imageView = view.findViewById(R.id.image);

            if(clickedClusterItem.getImageLocation() == null){
                title.setText(clickedClusterItem.getTitle());
                snippet.setText(clickedClusterItem.getSnippet());
                snippet.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            } else {
                title.setText("Photo");
                imageView.setImageURI(Uri.parse(clickedClusterItem.getImageLocation()));
                snippet.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
        }

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
