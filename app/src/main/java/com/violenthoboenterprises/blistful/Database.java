package com.violenthoboenterprises.blistful;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class Database extends SQLiteOpenHelper {

    private static final String DBNAME = "Notes.db";
    //Main Table
    private static final String TABLE = "notes_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "NOTE";
    private static final String COL3 = "CHECKLIST";
    private static final String COL4 = "TIMESTAMP";
    private static final String COL5 = "TASK";
    private static final String COL6 = "DUE";
    private static final String COL7 = "KILLED";
    private static final String COL8 = "BROADCAST";
    private static final String COL9 = "REPEAT";
    private static final String COL10 = "OVERDUE";
    private static final String COL11 = "SNOOZED";
    private static final String COL12 = "SHOWONCE";
    private static final String COL13 = "INTERVAL";
    private static final String COL14 = "REPEATINTERVAL";
    private static final String COL15 = "IGNORED";
    private static final String COL16 = "TIMECREATED";
    private static final String COL17 = "SORTEDINDEX";
    private static final String COL18 = "CHECKLISTSIZE";
    private static final String COL19 = "MANUALKILL";
    private static final String COL20 = "KILLEDEARLY";
    private static final String COL21 = "ORIGINALDAY";
    private static final String COL22 = "SNOOZEDTIMESTAMP";

    //Reminder Table
    private static final String ATABLE = "alarms_table";
    private static final String ACOL1 = "ID";
    private static final String ACOL2 = "HOUR";
    private static final String ACOL3 = "MINUTE";
    private static final String ACOL4 = "AMPM";
    private static final String ACOL5 = "DAY";
    private static final String ACOL6 = "MONTH";
    private static final String ACOL7 = "YEAR";

    //Snooze Table
    private static final String STABLE = "snooze_table";
    private static final String SCOL1 = "ID";
    private static final String SCOL2 = "HOUR";
    private static final String SCOL3 = "MINUTE";
    private static final String SCOL4 = "AMPM";
    private static final String SCOL5 = "DAY";
    private static final String SCOL6 = "MONTH";
    private static final String SCOL7 = "YEAR";

    //Universal data
    private static final String UTABLE = "universal_table";
    private static final String UCOL1 = "ID";
    private static final String UCOL2 = "MUTE";
    private static final String UCOL3 = "HIGHLIGHT";
    private static final String UCOL4 = "DARKLIGHT";
    private static final String UCOL5 = "ACTIVETASKNAME";
    private static final String UCOL6 = "ADSREMOVED";
    private static final String UCOL7 = "REMINDERSAVAILABLE";
    private static final String UCOL8 = "CYCLECOLORS";
    private static final String UCOL9 = "TASKLISTSIZE";
    private static final String UCOL10 = "CHECKLISTLISTSIZE";
    private static final String UCOL11 = "SETALARM";
    private static final String UCOL12 = "YEAR";
    private static final String UCOL13 = "MONTH";
    private static final String UCOL14 = "DAY";
    private static final String UCOL15 = "HOUR";
    private static final String UCOL16 = "MINUTE";
    private static final String UCOL17 = "COLORLASTCHANGED";
    private static final String UCOL18 = "AMPM";
    private static final String UCOL19 = "CYCLEENABLED";
    private static final String UCOL20 = "DUESSET";
    private static final String UCOL21 = "MOTIVATION";
    private static final String UCOL22 = "REPEATHINT";
    private static final String UCOL23 = "RENAMEHINT";
    private static final String UCOL24 = "REINSTATEHINT";
    private static final String UCOL25 = "TIMESTARTED";
    private static final String UCOL26 = "REVIEWONE";
    private static final String UCOL27 = "REVIEWTWO";
    private static final String UCOL28 = "REVIEWTHREE";
    private static final String UCOL29 = "REVIEWFOUR";
    private static final String UCOL30 = "HIGHLIGHTDEC";
    private static final String UCOL31 = "REPEATINTERVALTEMP";
    private static final String UCOL32 = "ORIGINALDAYTEMP";
    private static final String UCOL33 = "REPEATTEMP";

    //SubtasksActivity
    private static final String CTABLE = "subtasks_table";
    private static final String CCOL1 = "ID";
    private static final String CCOL2 = "SUBTASKID";
    private static final String CCOL3 = "SUBTASK";
    private static final String CCOL4 = "KILLED";
    private static final String CCOL5 = "TIMECREATED";
    private static final String CCOL6 = "SORTEDINDEX";

    String TAG = this.getClass().getSimpleName();

    Database(Context context) {
        super(context, DBNAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE + " (ID INTEGER PRIMARY KEY, " +
                "NOTE TEXT, CHECKLIST BOOLEAN, TIMESTAMP TEXT, TASK TEXT, DUE BOOLEAN," +
                " KILLED BOOLEAN, BROADCAST INTEGER, REPEAT BOOLEAN, OVERDUE BOOLEAN, " +
                "SNOOZED BOOLEAN, SHOWONCE BOOLEAN, INTERVAL INTEGER, REPEATINTERVAL TEXT," +
                " IGNORED BOOLEAN, TIMECREATED TEXT, SORTEDINDEX INTEGER, CHECKLISTSIZE INTEGER, " +
                "MANUALKILL BOOLEAN, KILLEDEARLY BOOLEAN, ORIGINALDAY TEXT, SNOOZEDTIMESTAMP TEXT)");
        db.execSQL("create table " + ATABLE + " (ID INTEGER PRIMARY KEY, " +
                "HOUR TEXT, MINUTE TEXT, AMPM TEXT, DAY TEXT, MONTH TEXT, YEAR TEXT)");
        db.execSQL("create table " + STABLE + " (ID INTEGER PRIMARY KEY, " +
                "HOUR TEXT, MINUTE TEXT, AMPM TEXT, DAY TEXT, MONTH TEXT, YEAR TEXT)");
        db.execSQL("create table " + UTABLE + " (ID INTEGER PRIMARY KEY, MUTE BOOLEAN," +
                " HIGHLIGHT TEXT, DARKLIGHT BOOLEAN, ACTIVETASKNAME TEXT, ADSREMOVED BOOLEAN," +
                " REMINDERSAVAILABLE BOOLEAN, CYCLECOLORS BOOLEAN, TASKLISTSIZE INTEGER, " +
                "CHECKLISTLISTSIZE INTEGER, SETALARM BOOLEAN, YEAR INTEGER, MONTH INTEGER," +
                " DAY INTEGER, HOUR INTEGER, MINUTE INTEGER, COLORLASTCHANGED INTEGER, " +
                "AMPM INTEGER, CYCLEENABLED BOOLEAN, DUESSET INTEGER, MOTIVATION BOOLEAN, " +
                "REPEATHINT INTEGER, RENAMEHINT INTEGER, REINSTATEHINT INTEGER, " +
                "TIMESTARTED INTEGER, REVIEWONE BOOLEAN, REVIEWTWO BOOLEAN, REVIEWTHREE BOOLEAN, " +
                "REVIEWFOUR BOOLEAN, HIGHLIGHTDEC TEXT, REPEATINTERVALTEMP TEXT, " +
                "ORIGINALDAYTEMP TEXT, REPEATTEMP BOOLEAN)");
        db.execSQL("create table " + CTABLE + " (ID INTEGER, SUBTASKID INTEGER," +
                " SUBTASK TEXT, KILLED BOOLEAN, TIMECREATED TEXT, SORTEDINDEX INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ATABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CTABLE);
        onCreate(db);
    }

    void insertData(int id, String note, String task, int broadcast, String timeCreated){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL1, id);
        content.put(COL2, note);
        content.put(COL3, false);
        content.put(COL4, "0");
        content.put(COL5, task);
        content.put(COL6, false);
        content.put(COL7, false);
        content.put(COL8, broadcast);
        content.put(COL9, false);
        content.put(COL10, false);
        content.put(COL11, false);
        content.put(COL12, false);
        content.put(COL13, 0);
        content.put(COL14, "");
        content.put(COL15, false);
        content.put(COL16, timeCreated);
        content.put(COL17, 0);
        content.put(COL18, 0);
        content.put(COL19, 0);
        content.put(COL20, 0);
        content.put(COL21, "");
        content.put(COL22, 0);
    }

    void insertAlarmData(int id, String hour, String minute, String ampm, String day,
                                   String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(ACOL1, id);
        content.put(ACOL2, hour);
        content.put(ACOL3, minute);
        content.put(ACOL4, ampm);
        content.put(ACOL5, day);
        content.put(ACOL6, month);
        content.put(ACOL7, year);
    }

    void insertSnoozeData(int id, String hour, String minute, String ampm,
                                    String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(SCOL1, id);
        content.put(SCOL2, hour);
        content.put(SCOL3, minute);
        content.put(SCOL4, ampm);
        content.put(SCOL5, day);
        content.put(SCOL6, month);
        content.put(SCOL7, year);
    }

    void insertUniversalData(boolean mute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        Calendar calendar = Calendar.getInstance();
        content.put(UCOL1, 0);
        content.put(UCOL2, mute);
        content.put(UCOL3, "#FFF152C9");
        content.put(UCOL4, false);
        content.put(UCOL5, "");
        content.put(UCOL6, false);//TODO change back to false!!!
        content.put(UCOL7, false);//TODO change back to false!!!
        content.put(UCOL8, false);//TODO change back to false!!!
        content.put(UCOL9, 0);
        content.put(UCOL10, 0);
        content.put(UCOL11, false);
        content.put(UCOL12, 0);
        content.put(UCOL13, 0);
        content.put(UCOL14, 0);
        content.put(UCOL15, 0);
        content.put(UCOL16, 0);
        content.put(UCOL17, (calendar.getTimeInMillis() /1000 /60 /60));
        content.put(UCOL18, 0);
        content.put(UCOL19, 0);
        content.put(UCOL20, 0);
        content.put(UCOL21, true);
        content.put(UCOL22, 0);
        content.put(UCOL23, 0);
        content.put(UCOL24, 0);
        content.put(UCOL25, (calendar.getTimeInMillis() /1000 /60 /60));
        content.put(UCOL26, false);
        content.put(UCOL27, false);
        content.put(UCOL28, false);
        content.put(UCOL29, false);
        content.put(UCOL30, "-285270841");
        content.put(UCOL31, "0");
        content.put(UCOL32, "");
        content.put(UCOL33, false);
    }

    void insertSubtaskData(int id, int subtaskID, String subtask, String stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(CCOL1, id);
        content.put(CCOL2, subtaskID);
        content.put(CCOL3, subtask);
        content.put(CCOL4, false);
        content.put(CCOL5, stamp);
        content.put(CCOL6, 0);
    }

    Cursor getData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL1
                + " == " + id, null);
        return result;
    }

    Cursor getDataByTimestamp(String stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL16
                + " == " + stamp, null);
        return result;
    }

    Cursor getSubtaskDataByTimestamp(String stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + CTABLE + " where " + CCOL5
                + " == " + stamp, null);
        return result;
    }

    Cursor getDataByDueTime(String stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL4
                + " == " + stamp, null);
        return result;
    }

    Cursor getDataBySnoozeTime(String stamp){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE + " where " + COL22
                + " == " + stamp, null);
        return result;
    }

    Cursor getAlarmData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + ATABLE + " where " + ACOL1
                + " == " + id, null);
        return result;
    }

    Cursor getSnoozeData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + STABLE + " where " + SCOL1
                + " == " + id, null);
        return result;
    }

    Cursor getUniversalData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + UTABLE + " where " + UCOL1
                + " == " + 0, null);
        return result;
    }

    Cursor getSubtaskData(int id, int subId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + CTABLE + " where " + CCOL1
                + " == " + id + " AND " + CCOL2 + " == " + subId, null);
        return result;
    }

    Cursor getSubtask(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + CTABLE + " where " + CCOL1
                + " == " + id, null);
        return result;
    }

    void updateData(String id, String note, Boolean checklist){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL1, id);
        if(MainActivity.inNote) {
            content.put(COL2, note);
        }else if(MainActivity.inChecklist){
            content.put(COL3, checklist);
        }else{
            content.put(COL2, note);
            content.put(COL3, checklist);
        }
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateDue(String id, Boolean due){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL6, due);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateSnooze(String id, Boolean snooze){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL11, snooze);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateKilled(String id, Boolean killed){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL7, killed);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateName(String id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL5, name);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateRepeat(String id, Boolean repeat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL9, repeat);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateTimestamp(String id, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(COL4, timestamp);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateOverdue(String id, Boolean overdue){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL10, overdue);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateShowOnce(String id, Boolean showOnce){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL12, showOnce);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateInterval(String id, String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL13, interval);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateRepeatInterval(String id, String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL14, interval);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateIgnored(String id, boolean ignored){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL15, ignored);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateSortedIndex(String id, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL17, index);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateManualKill(String id, boolean kill){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL19, kill);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateKilledEarly(String id, boolean kill){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL20, kill);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateSubtaskSortedIndex(String id, String subtask, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(CCOL6, index);
        db.update(CTABLE, content, "ID = ? AND SUBTASKID = ?",
                new String[] {id, subtask});
    }

    void updateChecklistSize(String id, int index){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL18, index);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    //Used to recalibrate monthly repeating tasks
    void updateOriginalDay(String id, String day){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL21, day);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateSnoozedTimestamp(String id, String snoozed){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL22, snoozed);
        db.update(TABLE, content, "ID = ?", new String[] {id});
    }

    void updateAlarmData(String id, String hour, String minute, String ampm,
                                   String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(ACOL1, id);
        content.put(ACOL2, hour);
        content.put(ACOL3, minute);
        content.put(ACOL4, ampm);
        content.put(ACOL5, day);
        content.put(ACOL6, month);
        content.put(ACOL7, year);
        db.update(ATABLE, content, "ID = ?", new String[] {id});
    }

    void updateSnoozeData(String id, String hour, String minute, String ampm,
                                    String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content= new ContentValues();
        content.put(SCOL1, id);
        content.put(SCOL2, hour);
        content.put(SCOL3, minute);
        content.put(SCOL4, ampm);
        content.put(SCOL5, day);
        content.put(SCOL6, month);
        content.put(SCOL7, year);
        db.update(STABLE, content, "ID = ?", new String[] {id});
    }

    void updateMute(Boolean mute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL2, mute);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateHighlight(String highlight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL3, highlight);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateActiveTaskTemp(String tempTask){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL5, tempTask);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateAdsRemoved(boolean removal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL6, removal);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateRemindersAvailable(boolean reminder){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL7, reminder);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateTaskListSize(int size){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL9, size);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateChecklistListSize(int size){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL10, size);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateSetAlarm(boolean set){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL11, set);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateYear(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL12, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateMonth(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL13, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateDay(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL14, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateHour(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL15, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateMinute(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL16, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateAmPm(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL18, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateColorLastChanged(int value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL17, value);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateDuesSet(int dues){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL20, dues);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateMotivation(boolean motivate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL21, motivate);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateRepeatHint(int hint){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL22, hint);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateRenameHint(int hint){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL23, hint);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateReinstateHint(int hint){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL24, hint);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateReviewOne(boolean review){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL26, review);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateReviewTwo(boolean review){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL27, review);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateReviewThree(boolean review){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL28, review);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateReviewFour(boolean review){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL29, review);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateHighlightDec(String highlight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL30, highlight);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateRepeatIntervalTemp(String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL31, interval);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateOriginalDayTemp(String day){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL32, day);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateRepeatTemp(boolean repeat){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(UCOL33, repeat);
        db.update(UTABLE, content, "ID = ?", new String[] {"0"});
    }

    void updateSubtask(String id, String subtaskId, String subtask){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(CCOL3, subtask);
        db.update(CTABLE, content, "ID = ? AND SUBTASKID = ?",
                new String[] {id, subtaskId});
    }

    void updateSubtaskKilled(String id, String subtask, Boolean killed){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(CCOL4, killed);
        db.update(CTABLE, content, "ID = ? AND SUBTASKID = ?",
                new String[] {id, subtask});
    }

    Integer deleteData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE, "ID = ?", new String[] {id});
    }

    Integer deleteAlarmData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ATABLE, "ID = ?", new String[] {id});
    }

    Integer deleteSnoozeData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(STABLE, "ID = ?", new String[] {id});
    }

    Integer deleteSubtaskData (String id, String subtask){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CTABLE, "ID = ? AND SUBTASKID = ?", new String[] {id, subtask});
    }

    Integer deleteAllSubtaskData (String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CTABLE, "ID = ?", new String[] {id});
    }

    int getTotalRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor countCursor = db.rawQuery("SELECT COUNT (*) FROM notes_table", null);
        int count = 0;
        if(countCursor != null){
            if(countCursor.getCount() > 0){
                countCursor.moveToFirst();
                count = countCursor.getInt(0);
            }
            countCursor.close();
        }
        return count;
    }

    ArrayList<Integer> getIDs(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Integer> theList = new ArrayList<>();
        Cursor IDCursor = db.rawQuery("SELECT ID FROM notes_table", null);
        while (IDCursor.moveToNext()) {
            theList.add(IDCursor.getInt(0));
        }
        IDCursor.close();
        return theList;
    }

}
