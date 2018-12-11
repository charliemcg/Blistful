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
    private int parentId;

    //name of the subtask
    @NonNull
    private String subtask;

    //is the subtask killed
    private boolean killed;

    //when the subtask was created
    private int timeCreated;

    public Subtask(int parentId, @NonNull String subtask) {
        //need to know the parent task to which the subtask belongs
        this.parentId = parentId;
        //name of the subtask
        this.subtask = subtask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setTaskId(int parentId) {
        this.parentId = parentId;
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
