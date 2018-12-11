package com.violenthoboenterprises.blistful.presenter;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.violenthoboenterprises.blistful.model.Task;

import java.util.List;

/*
 * These are the database calls on the task table
 */
@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM task_table ORDER BY timeCreated DESC")
    //TODO see if LiveData is needed
    LiveData<List<Task>> getAllTasks();

}
