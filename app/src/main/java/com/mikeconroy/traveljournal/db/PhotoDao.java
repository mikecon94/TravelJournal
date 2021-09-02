package com.mikeconroy.traveljournal.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT * FROM Photo WHERE tags LIKE :search COLLATE NOCASE")
    public List<Photo> findPhotoByTags(String search);

}
