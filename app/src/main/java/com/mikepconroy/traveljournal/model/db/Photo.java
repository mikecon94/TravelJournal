package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "image_uri")
    private String imagePath;
    private String tags;
    @ColumnInfo(name = "holiday_id")
    private int holidayId;
    @ColumnInfo(name = "place_id")
    private int placeId;
    private double latitude;
    private double longitude;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imageUri) {
        this.imagePath = imageUri;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString(){
        return "ID: " + id + "; Uri: " + imagePath + "; tags: " + tags + "; HolidayID: "
                + holidayId + "; PlaceID: " + placeId + "LatLng: " + latitude + ", " + longitude;
    }

}
