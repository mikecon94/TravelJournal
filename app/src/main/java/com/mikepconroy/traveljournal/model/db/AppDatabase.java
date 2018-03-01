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
@Database(entities = {Holiday.class, Photo.class, Place.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){
        if(INSTANCE == null){
            Log.i(Configuration.TAG, "AppDatabase: Creating new instance.");

            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    Configuration.DB_NAME).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract HolidayDao holidayDao();
    public abstract PhotoDao photoDao();
    public abstract PlaceDao placeDao();

}
