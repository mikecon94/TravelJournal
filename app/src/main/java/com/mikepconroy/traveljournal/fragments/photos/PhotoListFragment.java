package com.mikepconroy.traveljournal.fragments.photos;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Photo;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPhotoListInteractionListener}
 * interface.
 */
public class PhotoListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnPhotoListInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotoListFragment() {
    }

    @SuppressWarnings("unused")
    public static PhotoListFragment newInstance(int columnCount) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.photo_list);
        TextView emptyView = view.findViewById(R.id.empty_view);
        TextView loadingView = view.findViewById(R.id.loading_photos_view);

        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }

        new LoadPhotos().execute();

        return view;
    }

    private void updatePhotoListView(List<Photo> photos){
        Log.i(Configuration.TAG, "PhotoListFragment#updatePhotoListView: Updated Photos Received.");

        //TODO: Fix permissions so the list view can load the images. Or store the image in the DB as a blob or base64 etc.
        RecyclerView recyclerView = getActivity().findViewById(R.id.photo_list);
        TextView emptyView = getActivity().findViewById(R.id.empty_view);
        TextView loadingView = getActivity().findViewById(R.id.loading_photos_view);
        loadingView.setVisibility(View.GONE);

        if(photos.isEmpty()){
            Log.i(Configuration.TAG, "PhotoListFragment#onCreateView: No photos created" +
                    " yet. Displaying message.");
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.i(Configuration.TAG, "PhotoListFragment#onCreateView: Photos Exist." +
                    "Displaying RecyclerView.");
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(new PhotoRecyclerViewAdapter(photos, mListener));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoListInteractionListener) {
            mListener = (OnPhotoListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("Photos", true);

        //TODO: Investigate whether this can be common code to remove the duplication across onResumes.
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if(fab != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(Configuration.TAG, "PhotoListFragment: FAB Clicked.");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new NewPhotoFragment());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPhotoListInteractionListener extends OnFragmentUpdateListener{
        void onPhotoListItemInteraction(Photo item);
    }

    private class LoadPhotos extends AsyncTask<Void, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Void... params) {
            Log.i(Configuration.TAG, "PhotoListFragment#doInBackground: Finding photos.");
            return AppDatabase.getInstance(getContext()).photoDao().getAllPhotos();
        }

        @Override
        protected void onPostExecute(List<Photo> photos) {
            Log.i(Configuration.TAG, "PhotoListFragment: AsyncTask complete. No. of Photos: " + photos.size());
            updatePhotoListView(photos);
        }
    }
}
