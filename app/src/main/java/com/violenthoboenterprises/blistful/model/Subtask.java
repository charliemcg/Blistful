package com.violenthoboenterprises.blistful.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*
 * This is where the subtask table is defined
 */
@Entity(tableName = "subtask_table")
public class Subtask {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //id of the parent task
    private int taskId;

    //name of the subtask
    @NonNull
    private String subtask;

    //is the subtask killed
    private boolean killed;

    //when the subtask was created
    private int timeCreated;

    public Subtask(int id, @NonNull String subtask) {
        this.id = id;
        this.subtask = subtask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @NonNull
    public String getSubtask() {
        return subtask;
    }

    public void setSubtask(@NonNull String subtask) {
        this.subtask = subtask;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public int getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(int timeCreated) {
        this.timeCreated = timeCreated;
    }
}
