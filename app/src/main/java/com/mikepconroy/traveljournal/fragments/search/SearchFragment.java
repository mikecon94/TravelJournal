package com.mikepconroy.traveljournal.fragments.search;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;
import com.mikepconroy.traveljournal.model.db.Photo;
import com.mikepconroy.traveljournal.model.db.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class SearchFragment extends Fragment {

    private int mColumnCount = 1;

    private SearchListInteractionListener mListener;
    private List<SearchResultItem> searchResults = new ArrayList<>();

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "SearchFragment#onCreateView: Creating View.");

        View view = inflater.inflate(R.layout.fragment_search_result_list, container, false);

        final TextView emptyResults = view.findViewById(R.id.empty_view);

        Context context = view.getContext();
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new SearchResultRecyclerViewAdapter(searchResults, mListener));

        EditText searchBox = view.findViewById(R.id.search_box);
        searchBox.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 500; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable search) {
                        if(search.length() == 0){
                            Log.i(Configuration.TAG, "SearchFragment#afterTextChanged: Search is empty.");
                            showMessage("Enter search terms such as Holiday & Place titles or Photo tags.");
                        } else {
                            Log.i(Configuration.TAG, "SearchFragment#onCreateView: Searching for: " + search.toString());
                            searchResults.clear();
                            new FindHolidays().execute(search.toString());
                            new FindPhotos().execute(search.toString());
                            new FindPlaces().execute(search.toString());
                        }
                    }
                }
        );

        return view;
    }

    private void appendSearchResults(List<SearchResultItem> results){
        searchResults.addAll(results);
        if(searchResults.isEmpty()){
            showMessage("No Results.");
        } else {
            Collections.sort(searchResults);
            showList();
        }
    }

    private void updateHolidayResults(List<Holiday> holidays){
        List<SearchResultItem> results = new ArrayList<>();
        for (Holiday holiday : holidays){
            SearchResultItem item = new SearchResultItem();
            item.setId(holiday.getId());
            item.setImageLocation(holiday.getProfilePhotoPath());
            item.setTitle(holiday.getTitle());
            item.setType("Holiday");
            Log.i(Configuration.TAG, "SearchFragment: Adding Holiday with title: " + item.getTitle());
            results.add(item);
        }
        appendSearchResults(results);
    }

    private void updatePlaceResults(List<Place> places){
        List<SearchResultItem> results = new ArrayList<>();
        for(Place place : places){
            SearchResultItem item = new SearchResultItem();
            item.setId(place.getId());
            item.setImageLocation(place.getPhotoPath());
            item.setTitle(place.getTitle());
            item.setType("Place");
            Log.i(Configuration.TAG, "SearchFragment: Adding Place with title: " + item.getTitle());
            results.add(item);
        }
        appendSearchResults(results);
    }

    private void updatePhotoResults(List<Photo> photos){
        List<SearchResultItem> results = new ArrayList<>();
        for(Photo photo : photos){
            SearchResultItem item = new SearchResultItem();
            item.setId(photo.getId());
            item.setImageLocation(photo.getImagePath());
            item.setTitle(photo.getTags());
            item.setType("Photo");
            Log.i(Configuration.TAG, "SearchFragment: Adding Photo with ID: " + item.getId());
            results.add(item);
        }
        appendSearchResults(results);
    }

    public void showList(){
        RecyclerView recyclerView = getView().findViewById(R.id.list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.getAdapter().notifyDataSetChanged();
        TextView emptyResults = getView().findViewById(R.id.empty_view);
        emptyResults.setVisibility(View.GONE);
    }

    public void showMessage(String message){
        RecyclerView recyclerView = getView().findViewById(R.id.list);
        recyclerView.setVisibility(View.GONE);

        TextView emptyResults = getView().findViewById(R.id.empty_view);
        emptyResults.setVisibility(View.VISIBLE);
        emptyResults.setText(message);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "SearchFragment#onResume: Resuming.");
        mListener.onFragmentOpened("Search", true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchListInteractionListener) {
            mListener = (SearchListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SearchListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SearchListInteractionListener extends OnFragmentUpdateListener{
        void onSearchListInteraction(SearchResultItem item);
    }

    private class FindHolidays extends AsyncTask<String, Void, List<Holiday>> {
        @Override
        protected List<Holiday> doInBackground(String... searchTerm) {
            return AppDatabase.getInstance(getContext()).holidayDao().findHolidaysByTitle("%" + searchTerm[0] + "%");
        }

        @Override
        protected void onPostExecute(List<Holiday> holidays) {
            updateHolidayResults(holidays);
        }
    }

    private class FindPhotos extends AsyncTask<String, Void, List<Photo>>{
        @Override
        protected List<Photo> doInBackground(String... searchTerm) {
            return AppDatabase.getInstance(getContext()).photoDao().findPhotoByTags("%" + searchTerm[0] + "%");
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            updatePhotoResults(photos);
        }
    }

    private class FindPlaces extends AsyncTask<String, Void, List<Place>>{
        @Override
        protected List<Place> doInBackground(String... searchTerm) {
            return AppDatabase.getInstance(getContext()).placeDao().findPlaceByTitle("%" + searchTerm[0] + "%");
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            updatePlaceResults(places);
        }
    }
}
