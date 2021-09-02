package com.mikeconroy.traveljournal.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
