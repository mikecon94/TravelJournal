package com.mikeconroy.traveljournal.fragments;

import android.content.Context;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mikeconroy.traveljournal.Configuration;
import com.mikeconroy.traveljournal.OnFragmentUpdateListener;

/**
 * Created by MICONROY on 15/02/2018.
 */

public abstract class EditableBaseFragment extends Fragment implements OnBackPressListener{
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
        Log.i(Configuration.TAG, "EditableBaseFragment#onBackPressed");
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.i(Configuration.TAG, "EditableBaseFragment#onBackPressed: Checking for current focus.");
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null){
            String focusedViewName = getResources().getResourceName(focusedView.getId());
            Log.i(Configuration.TAG, "EditableBaseFragment#onBackPressed: Current Focus - " + focusedViewName);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

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
