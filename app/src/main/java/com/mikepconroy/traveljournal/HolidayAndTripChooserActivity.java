package com.mikepconroy.traveljournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayListFragment;
import com.mikepconroy.traveljournal.fragments.places.PlaceListFragment;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Place;

import java.util.ArrayList;
import java.util.List;

///TODO: Extract most of this to a base class. Subclasses would only edit the viewpager.
public class HolidayAndTripChooserActivity extends AppCompatActivity implements HolidayListFragment.HolidayListInteractionListener, PlaceListFragment.PlaceListInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_and_trip_chooser);
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

        final ViewPager viewPager = findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new HolidayListFragment(), "Holidays");
        adapter.addFragment(new PlaceListFragment(), "Place");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentOpened(String title, boolean navDrawerActive) {
        //Nothing to do.
    }

    @Override
    public void onPlaceListItemInteraction(Place item) {
        int id = item.getId();
        String title = item.getTitle();
        String itemType = Configuration.PLACE_ITEM;
        returnResult(id, title, itemType);
    }

    @Override
    public void onHolidayListItemInteraction(Holiday item) {
        int id = item.getId();
        String title = item.getTitle();
        String itemType = Configuration.HOLIDAY_ITEM;
        returnResult(id, title, itemType);
    }

    private void returnResult(int id, String title, String itemType){
        Intent i = new Intent();
        i.putExtra(Configuration.ITEM_ID, id);
        i.putExtra(Configuration.ITEM_TITLE, title);
        i.putExtra(Configuration.ITEM_TYPE, itemType);
        setResult(RESULT_OK, i);
        finish();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
