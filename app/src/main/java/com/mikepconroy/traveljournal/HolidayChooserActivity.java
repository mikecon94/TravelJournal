package com.mikepconroy.traveljournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepconroy.traveljournal.fragments.holidays.HolidayListFragment;
import com.mikepconroy.traveljournal.model.db.Holiday;

/**
 * Created by mikecon on 01/03/2018.
 */

public class HolidayChooserActivity extends AppCompatActivity implements HolidayListFragment.HolidayListInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_chooser);

        HolidayListFragment holidayListFragment = new HolidayListFragment();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, holidayListFragment).commit();
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
    public void onHolidayListItemInteraction(Holiday item) {
        Intent i = new Intent();
        i.putExtra("holidayId", item.getId());
        i.putExtra("holidayTitle", item.getTitle());
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onFragmentOpened(String title, boolean navDrawerActive) {
        //Nothing to do.
    }
}
