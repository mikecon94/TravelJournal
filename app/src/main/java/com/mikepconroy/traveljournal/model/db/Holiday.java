package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by mikecon on 09/02/2018.
 */

//TODO: Come back and check what should be indexed.
@Entity
public class Holiday {

    @PrimaryKey (autoGenerate = true)
    private int id;
    private String title;
    @ColumnInfo(name = "start_date")
    private String startDate;
    @ColumnInfo(name = "end_date")
    private String endDate;
    private String notes;

    //TODO: This is fragile and relies on the photo not being deleted. - Stores the photos path.
    //If a user edits the photo object then the path remains the same (thankfully).
    //Can't store the photo id due to issues loading the image path in room.
    //Could potentially look at using the id as the filename...
    @ColumnInfo(name = "photo_path")
    private String profilePhotoPath;

    //TODO: Add a list of Place IDs as a foreign key (Places associated with this holiday).

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getStartDate(){
        return this.startDate;
    }

    public String getEndDate(){
        return this.endDate;
    }

    public String getNotes(){
        return this.notes;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String name){
        this.title = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setNotes(String notes){
        this.notes = notes;
    }

    public String getProfilePhotoPath() { return this.profilePhotoPath; }

    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }
}
