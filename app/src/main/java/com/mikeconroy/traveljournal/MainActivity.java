package com.mikeconroy.traveljournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mikeconroy.traveljournal.fragments.OnBackPressListener;
import com.mikeconroy.traveljournal.fragments.holidays.HolidayDetailsFragment;
import com.mikeconroy.traveljournal.fragments.holidays.HolidayListFragment;
import com.mikeconroy.traveljournal.fragments.map.MapFragment;
import com.mikeconroy.traveljournal.fragments.photos.NewPhotoFragment;
import com.mikeconroy.traveljournal.fragments.photos.PhotoDetailsFragment;
import com.mikeconroy.traveljournal.fragments.photos.PhotoListFragment;
import com.mikeconroy.traveljournal.fragments.places.PlaceDetailsFragment;
import com.mikeconroy.traveljournal.fragments.places.PlaceListFragment;
import com.mikeconroy.traveljournal.fragments.search.SearchFragment;
import com.mikeconroy.traveljournal.fragments.search.SearchResultItem;
import com.mikeconroy.traveljournal.db.Holiday;
import com.mikeconroy.traveljournal.db.Photo;
import com.mikeconroy.traveljournal.db.Place;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public  class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HolidayListFragment.HolidayListInteractionListener,
        PhotoListFragment.OnPhotoListInteractionListener,
        PlaceListFragment.PlaceListInteractionListener,
        SearchFragment.SearchListInteractionListener,
        OnFragmentUpdateListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri imagePath;

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
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Place clicked.");
            updateFragment(new PlaceListFragment(), false);
        } else if (id == R.id.nav_photos) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Photos clicked.");
            updateFragment(new PhotoListFragment(), false);
        } else if (id == R.id.nav_camera) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Camera clicked.");
            dispatchTakePictureIntent();
        } else if (id == R.id.nav_map) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Map clicked.");
            updateFragment(new MapFragment(), false);
        } else if (id == R.id.nav_search) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Search clicked.");
            updateFragment(new SearchFragment(), false);
        } else if (id == R.id.nav_settings) {
            Log.i(Configuration.TAG, "MainActivity#NavDrawer: Settings clicked.");
            Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.i(Configuration.TAG, "MainActivity#dispatchTakePictureIntent: takePictureIntent != null. Creating File location.");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i(Configuration.TAG, photoFile.getAbsolutePath());
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mikeconroy.traveljournal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".png", storageDir);
        Log.i(Configuration.TAG, "MainActivity: Image Store File Created: "+ image.toString());
        imagePath = Uri.fromFile(image);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(Configuration.TAG, "MainActivity: Result received.");
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == Activity.RESULT_OK){
                NewPhotoFragment newPhoto = new NewPhotoFragment();

                Bundle b = new Bundle();
                b.putString("imagePath", imagePath.toString());
                newPhoto.setArguments(b);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, newPhoto)
                        .addToBackStack(null).commit();

            }
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
        Log.i(Configuration.TAG, "MainActivity#OnPhotoListInteraction: Opening PhotoDetails with ID: " + item.getId());
        Log.d(Configuration.TAG, "Photo: " + item.toString());
        PhotoDetailsFragment photoDetailsFragment = PhotoDetailsFragment.newInstance(item.getId());
        updateFragment(photoDetailsFragment, true);
    }

    @Override
    public void onPlaceListItemInteraction(Place item) {
        Log.i(Configuration.TAG, "MainActivity#OnPlaceListInteraction: Opening PlaceDetails with ID: " + item.getId());
        PlaceDetailsFragment placeDetailsFragment= PlaceDetailsFragment.newInstance(item.getId());
        updateFragment(placeDetailsFragment, true);
    }

    @Override
    public void onSearchListInteraction(SearchResultItem item) {
        Log.i(Configuration.TAG, "MainActivity#OnSearchListInteraction: Opening item.");

        EditText searchBox = findViewById(R.id.search_box);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

        if(item.getType().equals("Holiday")){
            HolidayDetailsFragment holidayFragment = HolidayDetailsFragment.newInstance(item.getId());
            updateFragment(holidayFragment, true);
        } else if (item.getType().equals("Place")){
            PlaceDetailsFragment placeDetailsFragment= PlaceDetailsFragment.newInstance(item.getId());
            updateFragment(placeDetailsFragment, true);
        } else if (item.getType().equals("Photo")){
            PhotoDetailsFragment photoDetailsFragment = PhotoDetailsFragment.newInstance(item.getId());
            updateFragment(photoDetailsFragment, true);
        }
    }
}