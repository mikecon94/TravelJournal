package com.mikepconroy.traveljournal.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.mikepconroy.traveljournal.Configuration;

/**
 * Created by mikecon on 09/02/2018.
 */

//TODO: Enable schema exporting.
@Database(entities = {Holiday.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            Log.i(Configuration.TAG, "AppDatabase: Creating new instance.");

            //TODO: Remove allow Main Thread queries.
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    Configuration.DB_NAME).allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public abstract HolidayDao holidayDao();

    //TODO: Add Dao abstract methods for the other entities.
}
