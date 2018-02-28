package com.mikepconroy.traveljournal.fragments.holidays;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.photos.PhotoListFragment;
import com.mikepconroy.traveljournal.model.db.Photo;

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
