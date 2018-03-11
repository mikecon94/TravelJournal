package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by mikecon on 09/02/2018.
 */

@Dao
public interface HolidayDao {

    @Insert
    public void insertHoliday(Holiday holiday);

    @Update
    public void updateHoliday(Holiday holiday);

    @Delete
    public void deleteHoliday(Holiday holiday);

    @Query("SELECT * FROM Holiday")
    public List<Holiday> getAllHolidays();

    @Query("SELECT * FROM Holiday WHERE id IS :id")
    public Holiday findHolidayById(int id);

    @Query("SELECT * FROM Holiday WHERE title LIKE :search COLLATE NOCASE")
    public List<Holiday> findHolidaysByTitle(String search);
}
