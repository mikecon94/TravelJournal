package com.mikeconroy.traveljournal.fragments.photos;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.db.AppDatabase;
import com.mikeconroy.traveljournal.db.Photo;

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
