package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PhotoDao {
    @Insert
    public void insertPhoto(Photo photo);

    @Update
    public void updatePhoto(Photo photo);

    @Delete
    public void deletePhoto(Photo photo);

    @Query("SELECT * FROM Photo")
    public List<Photo> getAllPhotos();

    @Query("SELECT * FROM Photo WHERE id IS :id")
    public Photo findPhotoById(int id);
}
