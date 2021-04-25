package com.sumedh.geotaggr.database;

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

    @Query("SELECT * FROM tag WHERE tagId = (:id)")
    Tag getTagById(Integer id);

    @Query("DELETE FROM tag WHERE tagId = (:id)")
    void deleteTagById(Integer id);
}
