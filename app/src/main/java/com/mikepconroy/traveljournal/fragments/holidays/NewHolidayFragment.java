package com.mikepconroy.traveljournal.fragments.holidays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.MainActivity;
import com.mikepconroy.traveljournal.R;
import com.mikepconroy.traveljournal.model.db.AppDatabase;
import com.mikepconroy.traveljournal.model.db.Holiday;

public class NewHolidayFragment extends Fragment implements OnBackPressListener {

    private Holiday holiday;

    private NewHolidayFragmentInteractionListener mListener;

    private boolean saved = false;

    public NewHolidayFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Configuration.TAG, "NewHolidayFragment#onCreate: Creating.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Configuration.TAG, "NewHolidayFragment#onCreateView: Creating View.");
        View view = inflater.inflate(R.layout.fragment_new_holiday, container, false);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(Configuration.TAG, "NewHolidayFragment#onAttach: Attaching.");
        if (context instanceof NewHolidayFragmentInteractionListener) {
            mListener = (NewHolidayFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HolidayDetailsInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Configuration.TAG, "NewHolidayFragment#onResume: Resuming.");
        String title = getActivity().getResources().getString(R.string.add_holiday_title);
        getActivity().setTitle(title);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Configuration.TAG, "NewHolidayFragment#onDetach: Detaching.");

        if (mListener != null) {
            mListener.onFragmentClose();
        }

        mListener = null;
    }

    public void onBackPressed(){
        Log.i(Configuration.TAG, "NewHolidayFragment#onBackPressed");



        new AlertDialog.Builder(getContext()).setTitle("Back Pressed").setMessage("Closing.")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveHoliday();
                    }
                }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStack();
                    }
                }).setCancelable(true).show();
    }

    private void saveHoliday(){
        Toast.makeText(getContext(), "Holiday Saved.", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    public interface NewHolidayFragmentInteractionListener {
        void onFragmentClose();
    }

}


