package com.mikepconroy.traveljournal.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.mikepconroy.traveljournal.Configuration;
import com.mikepconroy.traveljournal.OnFragmentUpdateListener;

/**
 * Created by MICONROY on 15/02/2018.
 */

public abstract class EditableBaseFragment extends Fragment {
    protected OnFragmentUpdateListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(Configuration.TAG, "HolidayBaseFragment#onAttach: Attaching.");
        if (context instanceof OnFragmentUpdateListener) {
            mListener = (OnFragmentUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HolidayBaseFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Configuration.TAG, "HolidayBaseFragment#onDetach: Detaching.");

        mListener = null;
    }


    public void onBackPressed(){
        Log.i(Configuration.TAG, "HolidayBaseFragment#onBackPressed");

        new AlertDialog.Builder(getContext()).setTitle("Save changes?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveItem();
                    }
                }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().popBackStack();
            }
        }).setCancelable(true).show();
    }

    protected abstract void saveItem();
}
