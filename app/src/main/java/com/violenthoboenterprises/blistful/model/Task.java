package com.violenthoboenterprises.blistful.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/*
 * This is where the task table is defined
 */
@Entity(tableName = "task_table")
public class Task{

    private static final String TAG = "Task";

    @PrimaryKey(autoGenerate = true)
    private int id;

    //Note if there is one
    private String note;

    //Is there a list of subtasks
    private boolean subtasks;

    //when the task is due if there is a due date
    private long timestamp;

    //task name
    @NonNull
    private String task;

    //does task have a due date
    private boolean due;

    //is task killed
    private boolean killed;

    //is there a repeating task
    private boolean repeat;

    //is task overdue
    private boolean overdue;

    //is task snoozed
    private boolean snoozed;

    //do overdue options need to be shown
    private boolean showonce;

    //how long task is snoozed for
    private int interval;

    //type of repeat
    private String repeatInterval;

    //were overdue options ignored
    private boolean ignored;

    //timestamp of when the task was created
    private String timeCreated;

    //
    private int sortedIndex;

    //how many subtasks there are
    private int subtasksSize;

    //did user kill the task
    private boolean manualKill;

    //repeating task killed while not overdue
    private boolean killedEarly;

    //used to recalibrate days that fall at the end of the month during monthly repeats
    private int originalDay;

    //when the snooze alarm is due
    private long snoozedTimestamp;

    public Task(/*int id, String note, boolean subtasks, long timestamp, */String task/*, boolean due,
                boolean killed, boolean repeat, boolean overdue, boolean snoozed, boolean showonce,
                int interval, String repeatInterval, boolean ignored, String timeCreated,
                int sortedIndex, int subtasksSize, boolean manualKill, boolean killedEarly,
                int originalDay, long snoozedTimestamp*/) {
//        this.id = id;
//        this.note = note;
//        this.subtasks = subtasks;
//        this.timestamp = timestamp;
        this.task = task;
//        this.due = due;
//        this.killed = killed;
//        this.repeat = repeat;
//        this.overdue = overdue;
//        this.snoozed = snoozed;
//        this.showonce = showonce;
//        this.interval = interval;
//        this.repeatInterval = repeatInterval;
//        this.ignored = ignored;
//        this.timeCreated = timeCreated;
//        this.sortedIndex = sortedIndex;
//        this.subtasksSize = subtasksSize;
//        this.manualKill = manualKill;
//        this.killedEarly = killedEarly;
//        this.originalDay = originalDay;
//        this.snoozedTimestamp = snoozedTimestamp;
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

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isDue() {
        return due;
    }

    public void setDue(boolean due) {
        this.due = due;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public boolean isSnoozed() {
        return snoozed;
    }

    public void setSnoozed(boolean snoozed) {
        this.snoozed = snoozed;
    }

    public boolean isShowonce() {
        return showonce;
    }

    public void setShowonce(boolean showonce) {
        this.showonce = showonce;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getSortedIndex() {
        return sortedIndex;
    }

    public void setSortedIndex(int sortedIndex) {
        this.sortedIndex = sortedIndex;
    }

    public int getSubtasksSize() {
        return subtasksSize;
    }

    public void setSubtasksSize(int subtasksSize) {
        this.subtasksSize = subtasksSize;
    }

    public boolean isManualKill() {
        return manualKill;
    }

    public void setManualKill(boolean manualKill) {
        this.manualKill = manualKill;
    }

    public boolean isKilledEarly() {
        return killedEarly;
    }

    public void setKilledEarly(boolean killedEarly) {
        this.killedEarly = killedEarly;
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