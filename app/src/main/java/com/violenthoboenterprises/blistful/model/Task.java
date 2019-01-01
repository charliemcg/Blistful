package com.violenthoboenterprises.blistful.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/*
 * This is where the task table is defined. It's serializable to facility passing between activities
 */
@Entity(tableName = "task_table")
public class Task implements Serializable {

    private static final String TAG = "Task";

    @PrimaryKey(autoGenerate = true)
    private int id;

    //NoteActivity if there is one
    private String note;

    //Is there a list of subtasks
    private boolean subtasks;

    //when the task is due if there is a due date
    private long timestamp;

    //due to the need to display overdues etc the displayed due date is separate from reminder due date
    private long displayedTimestamp;

    //task name
    @NonNull
    private String task;

    //is task snoozed
    private boolean snoozed;

    //type of repeat
    private String repeatInterval;

    //timestamp of when the task was created
    @NonNull
    private long timeCreated;

    //used to recalibrate days that fall at the end of the month during monthly repeats
    private int originalDay;

    //when the snooze alarm is due
    private long snoozedTimestamp;

    public Task(String note, long timestamp, String task, String repeatInterval, long timeCreated,
                int originalDay) {
        this.note = note;
        this.timestamp = timestamp;
        this.task = task;
        this.repeatInterval = repeatInterval;
        this.timeCreated = timeCreated;
        this.originalDay = originalDay;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        note = in.readString();
        subtasks = in.readByte() != 0;
        timestamp = in.readInt();
        displayedTimestamp = in.readLong();
        task = in.readString();
        snoozed = in.readByte() != 0;
        repeatInterval = in.readString();
        timeCreated = in.readLong();
        originalDay = in.readInt();
        snoozedTimestamp = in.readLong();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSubtasks() {
        return subtasks;
    }

    public void setSubtasks(boolean subtasks) {
        this.subtasks = subtasks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDisplayedTimestamp() {
        return displayedTimestamp;
    }

    public void setDisplayedTimestamp(long displayedTimestamp) {this.displayedTimestamp = displayedTimestamp;}

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isSnoozed() {
        return snoozed;
    }

    public void setSnoozed(boolean snoozed) {
        this.snoozed = snoozed;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getOriginalDay() {
        return originalDay;
    }

    public void setOriginalDay(int originalDay) {
        this.originalDay = originalDay;
    }

    public long getSnoozedTimestamp() {
        return snoozedTimestamp;
    }

    public void setSnoozedTimestamp(long snoozedTimestamp) {
        this.snoozedTimestamp = snoozedTimestamp;
    }

}
