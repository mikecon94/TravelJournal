package com.mikepconroy.traveljournal;

/**
 * Created by MICONROY on 14/02/2018.
 */

public interface OnFragmentUpdateListener  {
    void onFragmentClose();
    void onFragmentOpened(String title, boolean navDrawerActive);
}
