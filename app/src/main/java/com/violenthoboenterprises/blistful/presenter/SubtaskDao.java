package com.violenthoboenterprises.blistful.presenter;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.violenthoboenterprises.blistful.model.Subtask;
import com.violenthoboenterprises.blistful.model.Task;

import java.util.List;

/*
 * These are the database calls on the subtask table
 */
@Dao
public interface SubtaskDao {

    @Insert
    void insert(Subtask subtask);

    @Update
    void update(Subtask subtask);

    @Delete
    void delete(Subtask subtask);

    @Query("SELECT * FROM subtask_table WHERE parentId = :parentId ORDER BY timeCreated DESC")
        //TODO see if LiveData is needed
    LiveData<List<Subtask>> getAllTasks(int parentId);

    @Query("SELECT * FROM subtask_table WHERE parentId = :parentId AND id = :subtaskId")
    Subtask getSubtask(Integer parentId, Integer subtaskId);
}
