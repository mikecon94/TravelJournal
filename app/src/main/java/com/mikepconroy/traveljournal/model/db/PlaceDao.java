package com.mikepconroy.traveljournal.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert
    public void insertPlace(Place place);

    @Update
    public void updatePlace(Place place);

    @Delete
    public void deletePlace(Place place);

    @Query("SELECT * FROM Place")
    public List<Place> getAllPlaces();

    @Query("SELECT * FROM Place WHERE id IS :id")
    public Place findPlaceById(int id);


    @Query("SELECT * FROM Place WHERE title LIKE :search COLLATE NOCASE")
    public List<Place> findPlaceByTitle(String search);
}
