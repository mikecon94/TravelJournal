package com.mikepconroy.traveljournal.fragments.places;


public class NewPlaceFragment extends PlaceEditableBaseFragment {

    public NewPlaceFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        mListener.onFragmentOpened("New Place", false);
    }

    @Override
    protected void saveItem() {
        //TODO
    }
}
