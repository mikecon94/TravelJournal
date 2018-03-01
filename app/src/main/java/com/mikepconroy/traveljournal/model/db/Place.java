package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Place {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String date;
    private String photoPath;
    private int holidayId;
    private double latitude;
    private double longitude;

    //TODO Include Contacts.
    

}
