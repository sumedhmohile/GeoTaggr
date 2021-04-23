package com.sumedh.geotaggr;

import android.content.Context;

import com.sumedh.geotaggr.domain.Tag;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Tag.class,version = 1,exportSchema = false)
public abstract class TagDatabase extends RoomDatabase {
    private static TagDatabase instance;

    public abstract TagDao tagDao();

    public static synchronized TagDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), TagDatabase.class , "tag_db")
                    //TODO: replace with background
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
