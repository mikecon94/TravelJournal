package com.mikeconroy.traveljournal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.mikeconroy.traveljournal.R;
import com.mikeconroy.traveljournal.fragments.photos.PhotoListFragment;
import com.mikeconroy.traveljournal.db.Photo;

public class PhotoChooserActivity extends AppCompatActivity implements PhotoListFragment.OnPhotoListInteractionListener {

    //TODO Take in a Holiday or Trip ID so the fragment only displays related trips.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_chooser);

        PhotoListFragment photoListFragment = new PhotoListFragment();

        //This ensures the fragment hasn't already been placed into the activity.
        //onCreate is called on screen rotation.
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, photoListFragment).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPhotoListItemInteraction(Photo item) {
        Intent i = new Intent();
        i.putExtra("imagePath", item.getImagePath());
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onFragmentOpened(String title, boolean navDrawerActive) {
        //Nothing to do.
    }
}
