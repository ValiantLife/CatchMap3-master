package com.example.gareth.catchmap;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CatchDao {

    @Insert
    void insert(Catch iCatch);

    @Update
    void update(Catch iCatch);

    @Delete
    void delete(Catch iCatch);

    @Query("SELECT * FROM catch_table")
    LiveData<List<Catch>> getAllCatches();

    /*Example of custom query
    @Query("DELETE FROM catch_table")
    void deleteAllCatches();
    */
}
