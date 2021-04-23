package com.sumedh.geotaggr;

import com.sumedh.geotaggr.domain.Tag;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(Tag tag);

    @Delete
    void Delete(Tag tag);

    @Query("DELETE FROM tag")
    void DeleteAllTags();

    @Query("SELECT * FROM tag")
    LiveData<List<Tag>> getAllTags();
}