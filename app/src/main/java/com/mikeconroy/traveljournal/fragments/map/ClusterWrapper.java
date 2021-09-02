package com.mikeconroy.traveljournal.fragments.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * This class is a wrapper for Place and Photo to allow the ClusterManager to use them.
 */

public class ClusterWrapper implements ClusterItem {

    private LatLng location;
    private String title;
    private String notes;
    private String imageLocation;
    private int id;

    public ClusterWrapper(LatLng location, int id, String title, String notes){
        this.location = location;
        this.title = title;
        this.notes = notes;
        this.id = id;
    }

    public ClusterWrapper(LatLng location, int id, String imageLocation){
        this.location = location;
        this.imageLocation =  imageLocation;
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return notes;
    }

    public String getImageLocation(){
        return imageLocation;
    }

    public int getId(){
        return id;
    }

}
