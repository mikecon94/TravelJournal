package com.mikepconroy.traveljournal.fragments.holidays;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link HolidayListInteractionListener}
 * interface.
 */
public class HolidayListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private HolidayListInteractionListener mListener;

    //TODO: Currently all Holidays are controlled via access to the databse.
    //This may cause issues with performance. Should look into loading the list into memory
    //and managing it entirely in there.

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HolidayListFragment() {}

    public static HolidayListFragment newInstance(int columnCount) {
        Log.i(Configuration.TAG, "HolidayListFragment#newInstance: ??");
        HolidayListFragment fragment = new HolidayListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Configuration.TAG, "HolidayListFragment#onCreate: Creating.");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(Configuration.TAG, "HolidayListFragment#onCreateView: Starting AsyncTask to grab Holidays.");

        //TODO: Display loading icon whilst the holiday list is loaded from database.
        View view = inflater.inflate(R.layout.fragment_holiday_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.holiday_list);
        TextView emptyView = view.findViewById(R.id.empty_view);
        TextView loadingView = view.findViewById(R.id.loading_holidays_view);

        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);

        Context context = getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        new LoadHolidays().execute();

        return view;
    }

    private void updateHolidayListView(List<Holiday> holidays) {
        Log.i(Configuration.TAG, "HolidayListFragment#updateHolidayListView: Updated Holidays Received.");

        RecyclerView recyclerView = getActivity().findViewById(R.id.holiday_list);
        TextView emptyView = getActivity().findViewById(R.id.empty_view);
        TextView loadingView = getActivity().findViewById(R.id.loading_holidays_view);
        loadingView.setVisibility(View.GONE);
        if(holidays.isEmpty()){
            Log.i(Configuration.TAG, "HolidayListFragment#onCreateView: No holidays created" +
                    " yet. Displaying message.");
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            Log.i(Configuration.TAG, "HolidayListFragment#onCreateView: Holidays Exist." +
                    "Displaying RecyclerView.");
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setAdapter(new HolidayRecyclerAdapter(holidays, mListener));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(Configuration.TAG, "HolidayListFragment#onAttach: Attaching.");
        if (context instanceof HolidayListInteractionListener) {
            mListener = (HolidayListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HolidayListInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "HolidayListFragment#onResume: Resuming.");
        mListener.onFragmentOpened("Holidays", true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        //TODO See whether the FAB should be enabled on the chooser activity and allow the user to use it.
        //The fab does not exist on the chooser activity.
        if(fab != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(Configuration.TAG, "HolidayListFragment: FAB Clicked.");
                    //Start the Edit Holiday Fragment.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new NewHolidayFragment());
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Configuration.TAG, "HolidayListFragment#onDetach: Detaching.");
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface HolidayListInteractionListener extends OnFragmentUpdateListener{
        void onHolidayListItemInteraction(Holiday item);
    }

    private class LoadHolidays extends AsyncTask<Void, Void, List<Holiday>> {
        @Override
        protected List<Holiday> doInBackground(Void... params) {
            Log.i(Configuration.TAG, "HolidayListFragment#doInBackground: Finding holidays.");
            return AppDatabase.getInstance(getContext()).holidayDao().getAllHolidays();
        }

        @Override
        protected void onPostExecute(List<Holiday> holidays) {
            Log.i(Configuration.TAG, "HolidayListFragment: AsyncTask complete. No. of Holidays: " + holidays.size());
            updateHolidayListView(holidays);        }
    }
}
