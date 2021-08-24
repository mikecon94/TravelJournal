package com.mikepconroy.traveljournal.fragments.search;

import androidx.annotation.NonNull;

public class SearchResultItem implements Comparable<SearchResultItem>{

    private String type;
    private String title;
    private String imageLocation;
    private int id;

    public SearchResultItem(){}

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NonNull SearchResultItem item2) {
        return title.toLowerCase().compareTo(item2.getTitle().toLowerCase());
    }
}
