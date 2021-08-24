package com.mikepconroy.traveljournal.fragments.photos;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
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

public class NewPhotoFragment extends PhotoEditableBaseFragment {


    public NewPhotoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if(getArguments() != null && getArguments().getString("imagePath") != null) {
            imagePath = getArguments().getString("imagePath");
            ImageView imageView = view.findViewById(R.id.photo_image);
            imageView.setImageURI(Uri.parse(imagePath));
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("New Photo", false);
    }

    @Override
    protected void savePhotoToDatabase(Photo photo) {
        new InsertPhotoTask().execute(photo);
    }

    private class InsertPhotoTask extends AsyncTask<Photo, Void, Void> {
        @Override
        protected Void doInBackground(Photo... photos) {
            AppDatabase.getInstance(getContext()).photoDao().insertPhoto(photos[0]);
            return null;
        }
    }
}
