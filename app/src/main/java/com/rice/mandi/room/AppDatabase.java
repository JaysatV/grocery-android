package com.rice.mandi.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CartTableClass.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();

    private static AppDatabase INSTANCE ;

    public static AppDatabase getDbInstance(Context context) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "RiceDB")
                    .allowMainThreadQueries()
                    .build();

        return INSTANCE;
    }
}
