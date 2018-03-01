package com.mikepconroy.traveljournal.fragments.photos;

import android.graphics.Bitmap;
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
import com.mikepconroy.traveljournal.model.db.Photo;

public class EditPhotoFragment extends PhotoEditableBaseFragment {

    public static EditPhotoFragment newInstance(int photoId){
        EditPhotoFragment fragment = new EditPhotoFragment();
        Bundle args = new Bundle();
        args.putInt("photoId", photoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.findViewById(R.id.map_view).setVisibility(View.GONE);
        ((CheckBox) view.findViewById(R.id.location_enabled)).setChecked(false);
        new EditPhotoFragment.LoadPhotoTask().execute(photoId);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("Edit Photo", false);
    }

    private void updatePhotoDetailsDisplay(Photo photo){
        if(photo == null) {
            Log.i(Configuration.TAG, "EditPhoto#updatePhotoDetailsDisplay: Photo not found.");
            Toast.makeText(getContext(), "Photo not found :(.", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            imagePath = photo.getImagePath();
            photoId = photo.getId();

            ImageView imageView = getActivity().findViewById(R.id.photo_image);
            imageView.setImageURI(Uri.parse(photo.getImagePath()));

            if(!photo.getTags().equals("")){
                EditText tags = getActivity().findViewById(R.id.image_tags);
                tags.setText(photo.getTags());
            }

            Button viewAssociatedTrip = getView().findViewById(R.id.associate_image_button);
            if(photo.getHolidayId() != 0){
                viewAssociatedTrip.setText("Loading Associated Photo");
                holidayId = photo.getHolidayId();
                new LoadHoliday().execute(photo.getHolidayId());
            } else if (photo.getPlaceId() != 0){
                viewAssociatedTrip.setText("Loading Associated Place");
                tripId = photo.getPlaceId();
                //TODO Load Place.
            }

            if(photo.getLatitude() != 0 && photo.getLongitude() != 0){
                Log.i(Configuration.TAG, "EditPhotoFragment#updatePhotoDetailsDisplay: LatLng are set.");
                getView().findViewById(R.id.map_view).setVisibility(View.VISIBLE);
                ((CheckBox) getView().findViewById(R.id.location_enabled)).setChecked(true);
                mapViewWrapper.placeMarkerAndZoom(new LatLng(photo.getLatitude(), photo.getLongitude()));
            }
        }
    }

    private void updateAssociatedTripButton(final Holiday holiday){
        Button viewAssociatedTrip = getView().findViewById(R.id.associate_image_button);
        viewAssociatedTrip.setText("Holiday: " + holiday.getTitle());
    }

    @Override
    protected void savePhotoToDatabase(Photo photo) {
        new EditPhotoTask().execute(photo);
    }

    private class LoadPhotoTask extends AsyncTask<Integer, Void, Photo> {
        @Override
        protected Photo doInBackground(Integer... photoId) {
            Log.i(Configuration.TAG, "Loading photo: "+ photoId[0]);
            return AppDatabase.getInstance(getContext()).photoDao().findPhotoById(photoId[0]);
        }

        @Override
        protected void onPostExecute(Photo photo) {
            updatePhotoDetailsDisplay(photo);
        }
    }

    private class EditPhotoTask extends AsyncTask<Photo, Void, Void>{
        @Override
        protected Void doInBackground(Photo... photos) {
            AppDatabase.getInstance(getContext()).photoDao().updatePhoto(photos[0]);
            return null;
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
