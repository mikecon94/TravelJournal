package com.mikepconroy.traveljournal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;

import com.mikepconroy.traveljournal.fragments.holidays.HolidayDetailsFragment;
import com.mikepconroy.traveljournal.fragments.holidays.HolidayListFragment;
import com.mikepconroy.traveljournal.fragments.holidays.NewHolidayFragment;
import com.mikepconroy.traveljournal.fragments.holidays.OnBackPressListener;
import com.mikepconroy.traveljournal.model.db.Holiday;

//TODO: Bundle the below interfaces into a single interface.
public  class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HolidayListFragment.HolidayListInteractionListener,
        HolidayDetailsFragment.HolidayDetailsInteractionListener,
        NewHolidayFragment.NewHolidayFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Configuration.TAG, "MainActivity: On Create Called.");

        // Create a new Fragment to be placed in the activity layout
        HolidayListFragment firstFragment = new HolidayListFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getIntent().getExtras());


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

        prepareActionBar("Holidays");

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Configuration.TAG, "MainActivity: FAB Clicked.");

                updateFragment(new NewHolidayFragment(), "Add Holiday");

                //TODO: The following code can be used for undoing deletions etc.
                //Snackbar.make(view, "Adding new Holiday.", Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void prepareActionBar(String title) {
        Log.i(Configuration.TAG, "MainActivity: Preparing Action Bar.");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //TODO: Disable drawer usability on add/edit fragments.

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Camera clicked.");
        } else if (id == R.id.nav_map) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Map clicked.");
        } else if (id == R.id.nav_travel_gallery) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Travel Gallery clicked.");
        } else if (id == R.id.nav_trips) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Trips clicked.");
        } else if (id == R.id.nav_settings) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Settings clicked.");
        } else if (id == R.id.nav_about) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: About clicked.");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(Holiday item) {

        Log.i(Configuration.TAG, "MainActivity#OnListFragmentInteractions:" +
            "Opening HolidayDetailsFragment with item: " + item.getId());
        //Toast.makeText(this, "You clicked " + item.toString(), Toast.LENGTH_SHORT).show();
        HolidayDetailsFragment holidayFragment = HolidayDetailsFragment.newInstance(item.getId());
        updateFragment(holidayFragment, item.getTitle());

    }

    private void updateFragment(Fragment fragment, String title){
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(title);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onFragmentClose(){
        Log.i(Configuration.TAG, "MainActivity: Fragment closed. Setting title to Holidays");
        prepareActionBar("Holidays");

        //TODO this will also need to set the FAB to the plus symbol and use the new holiday.
        findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }
}
