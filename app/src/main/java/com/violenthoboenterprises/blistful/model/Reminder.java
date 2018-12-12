package com.violenthoboenterprises.blistful.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/*
 * This is where the subtask table is defined
 */
@Entity(tableName = "reminder_table")
public class Reminder implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //id of the parent task
    @NonNull
    private int parentId;

    private int hour;

    private int minute;

    private int year;

    private int month;

    private int day;

    public Reminder(int id, @NonNull int parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    protected Reminder(Parcel in) {
        id = in.readInt();
        parentId = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public int getParentId() {
        return parentId;
    }

    public void setParentId(@NonNull int parentId) {
        this.parentId = parentId;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(parentId);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
    }
}
