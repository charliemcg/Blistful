package com.violenthoboenterprises.blistful.presenter;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.violenthoboenterprises.blistful.model.Reminder;
import com.violenthoboenterprises.blistful.model.Subtask;

import java.util.List;

/*
 * These are the database calls on the subtask table
 */
@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminder_table WHERE parentId = :parentId")
    Reminder getReminderByParent(int parentId);

}
