package com.mikepconroy.traveljournal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.mikepconroy.traveljournal.fragments.OnBackPressListener;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayDetailsFragment;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayListFragment;
import com.mikepconroy.traveljournal.fragments.photos.PhotoDetailsFragment;
import com.mikepconroy.traveljournal.fragments.photos.PhotoListFragment;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Photo;

public  class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HolidayListFragment.HolidayListInteractionListener,
        PhotoListFragment.OnPhotoListInteractionListener,
        OnFragmentUpdateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Configuration.TAG, "MainActivity: On Create Called.");

        //TODO: Change the below to use the updateFragment method.
        // Create a new Fragment to be placed in the activity layout
        HolidayListFragment firstFragment = new HolidayListFragment();

        //This ensures the fragment hasn't already been placed into the activity.
        //onCreate is called on screen rotation.
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void setActionBarTitle(String title) {
        Log.i(Configuration.TAG, "MainActivity: Setting Title to: " + title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
    }

    private void enableNavDrawer(){
        Log.i(Configuration.TAG, "MainActivity: Enabling Nav Drawer");
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
        navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, navDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void disableNavDrawer(){
        Log.i(Configuration.TAG, "MainActivity: Disabling Nav Drawer");
        DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
        navDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.i(Configuration.TAG, "Back Pressed.");

        Fragment fm = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.i(Configuration.TAG, "Fragment is of type:" + fm.getClass().toString());
        if(fm != null && fm instanceof OnBackPressListener){
            ((OnBackPressListener) fm).onBackPressed();
        } else {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(Configuration.TAG, "MainActivity: Settings option clicked.");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_holidays) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Holidays clicked.");
            updateFragment(new HolidayListFragment(), false);
        } else if (id == R.id.nav_places) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Places clicked.");
        } else if (id == R.id.nav_photos) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Photos clicked.");
            updateFragment(new PhotoListFragment(), false);
        } else if (id == R.id.nav_travel_galleries) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Travel Gallery clicked.");
        } else if (id == R.id.nav_camera) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Camera clicked.");
        } else if (id == R.id.nav_map) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Map clicked.");
        } else if (id == R.id.nav_search) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Search clicked.");
        } else if (id == R.id.nav_settings) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Settings clicked.");
        } else if (id == R.id.nav_about) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: About clicked.");
             new AlertDialog.Builder(this).setTitle("About Travel Journal")
                     .setMessage("Mike Conroy \nDC3040\nAston University")
                     .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which) {}
             }).setCancelable(true).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment);
        if(addToBackStack){
            transaction.addToBackStack(null);
        }
        // Commit the transaction
        transaction.commit();
    }

    public void onFragmentOpened(String title, boolean navDrawerActive){
        setActionBarTitle(title);
        if(navDrawerActive){
            enableNavDrawer();
        } else {
            disableNavDrawer();
        }
    }

    @Override
    public void onHolidayListItemInteraction(Holiday item) {

        Log.i(Configuration.TAG, "MainActivity#OnListFragmentInteractions:" +
                "Opening HolidayDetailsFragment with item: " + item.getId());
        //Toast.makeText(this, "You clicked " + item.toString(), Toast.LENGTH_SHORT).show();
        HolidayDetailsFragment holidayFragment = HolidayDetailsFragment.newInstance(item.getId());
        updateFragment(holidayFragment, true);
    }

    @Override
    public void onPhotoListItemInteraction(Photo item) {
        //TODO: Open fragment for editing a photo.
        Log.i(Configuration.TAG, "MainActivity#OnPhotoListInteraction: Opening PhotoDetails with ID: " + item.getId());
        Log.d(Configuration.TAG, "Photo: " + item.toString());
        PhotoDetailsFragment photoDetailsFragment = PhotoDetailsFragment.newInstance(item.getId());
        updateFragment(photoDetailsFragment, true);
    }
}