package com.violenthoboenterprises.blistful.model;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.violenthoboenterprises.blistful.Database;
import com.violenthoboenterprises.blistful.MainActivity;
import com.violenthoboenterprises.blistful.StringConstants;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private static String TAG = "MainActivityPresenter";
    private TaskViewModel taskViewModel;
    private MainActivityView mainActivityView;
    private Context context;
    private SubtasksPresenter subtasksPresenter;

    public MainActivityPresenterImpl(MainActivityView mainActivityView, TaskViewModel taskViewModel, Context context, SubtasksPresenter subtasksPresenter) {
        this.mainActivityView = mainActivityView;
        this.context = context;
        this.taskViewModel = taskViewModel;
        this.subtasksPresenter = subtasksPresenter;
    }

    @Override
    public void addTask(String note, long dueTimestamp, String taskName, String repeatInterval,
                        long timeCreated, boolean manualKill, boolean killedEarly, int originalDay) {
        Task task = new Task(note, dueTimestamp, taskName, repeatInterval,
                timeCreated, manualKill, killedEarly, originalDay);
        taskViewModel.insert(task);
    }

    @Override
    public void update(Task task) {
        taskViewModel.update(task);
    }

    @Override
    public void setTask(Task taskBeingEdited, String editedTaskString) {
        taskBeingEdited.setTask(editedTaskString);
        update(taskBeingEdited);
    }

    @Override
    public void reinstateTask(Task taskToReinstate) {
        taskViewModel.insert(taskToReinstate);
    }

    @Override
    public void showPurchases() {
        mainActivityView.showPurchases();
    }

    @Override
    public void toggleFab(boolean showFab) {
        mainActivityView.toggleFab(showFab);
    }

    @Override
    public int getTaskIdByName(String taskName) {
        return taskViewModel.getTaskIdByName(taskName);
    }

    @Override
    public void migrateDatabase() {

        //Step 1 is only needed for testing purposes. Not to be used in production code.
        ///////////////Step 1. Create mock data. Remove once created
//        Database db = new Database(context);
//        db.insertUniversalData(true);
//        //adding mock universal data
//        db.updateMotivation(false);
//        db.updateMute(false);
//        long timeCreated = Calendar.getInstance().getTimeInMillis() / 1000 / 60 / 60;
//        db.updateTimeCreated(String.valueOf(timeCreated));
//        db.updateAdsRemoved(true);
//        db.updateRemindersAvailable(true);
//        db.updateReviewOne(true);
//
//        //adding mock task data
//        db.insertData(3, "", "Task Name",
//                3, "123456789");
//        db.updateNote(String.valueOf(3), "This is a note");
//        long stamp = (Calendar.getInstance().getTimeInMillis() + (1000 * 60 * 60 * 24)) / 1000;
//        db.updateTimestamp(String.valueOf(3), String.valueOf(stamp));
//        db.updateRepeatInterval(String.valueOf(3), "none");
//
//        db.insertData(4, "", "Task Name 2",
//                4, "123456789");
//        db.updateNote(String.valueOf(4), null);
//        stamp = (Calendar.getInstance().getTimeInMillis() + (1000 * 60 * 60 * 48)) / 1000;
//        db.updateTimestamp(String.valueOf(4), String.valueOf(stamp));
//        db.updateChecklistSize(String.valueOf(4), 2);
//        db.updateRepeatInterval(String.valueOf(4), "none");
//
//        //adding mock subtask data
//        db.insertSubtaskData(4, 0, "Subtask 1",
//                String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));
//        db.insertSubtaskData(4, 1, "Subtask 2",
//                String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000));

        ///////////////Step 2. Merge the database. Remove while creating mock data
        //The old database
        Database db = new Database(context);
        db.insertUniversalData(true);
        //getting universal data and putting into shared preferences
        Cursor cursor = db.getUniversalData();
        while (cursor.moveToNext()) {
//            MainActivity.preferences.edit().putBoolean(StringConstants.MUTE_KEY, (cursor.getInt(1) > 0)).apply();
            MainActivity.preferences.edit().putBoolean(StringConstants.ADS_REMOVED_KEY, (cursor.getInt(5) > 0)).apply();
            MainActivity.preferences.edit().putBoolean(StringConstants.REMINDERS_AVAILABLE_KEY, (cursor.getInt(6) > 0)).apply();
//            MainActivity.preferences.edit().putInt(StringConstants.DUES_SET, (cursor.getInt(19))).apply();
            MainActivity.preferences.edit().putBoolean(StringConstants.MOTIVATION_KEY, (cursor.getInt(20) > 0)).apply();
            MainActivity.preferences.edit().putInt(StringConstants.REPEAT_HINT_KEY, (cursor.getInt(21))).apply();
            MainActivity.preferences.edit().putInt(StringConstants.RENAME_HINT_KEY, (cursor.getInt(22))).apply();
            long stampToAdjust = cursor.getInt(24);
            stampToAdjust = stampToAdjust * 1000 * 60 * 60;
            MainActivity.preferences.edit().putLong(StringConstants.TIME_INSTALLED_KEY, stampToAdjust).apply();
            if (cursor.getInt(25) > 0) {
                MainActivity.preferences.edit().putInt(StringConstants.SHOW_REVIEW_KEY, 5).apply();
            }
        }

        int taskListSize = db.getTotalRows();

        ArrayList<Integer> IDList = db.getIDs();

        for (int i = 0; i < taskListSize; i++) {
            int id = 0;
            String note = null;
            long dueTimestamp = 0;
            String taskName = null;
            String repeatInterval = null;
            long timeCreated = 0;
            int subtasksCount = 0;
            boolean manualKill = false;
            boolean killedEarly = false;
            int originalDay = 0;
            Cursor result = db.getData(IDList.get(i));
            while (result.moveToNext()) {
                id = result.getInt(0);
                note = result.getString(1);
                dueTimestamp = Long.parseLong(result.getString(3));
                taskName = result.getString(4);
                repeatInterval = result.getString(13);
                timeCreated = Long.parseLong(result.getString(15));
                subtasksCount = result.getInt(17);
                manualKill = result.getInt(18) > 0;
                killedEarly = result.getInt(19) > 0;
                originalDay = result.getInt(20);
                if (repeatInterval.equalsIgnoreCase("none")) {
                    repeatInterval = null;
                }
                //old db uses seconds. New db uses millis
                dueTimestamp *= 1000;
                timeCreated *= 1000;
            }

            addTask(note, dueTimestamp, taskName, repeatInterval,
                    timeCreated, manualKill, killedEarly, originalDay);

            //getting the subtask ids
            ArrayList<Integer> sortedSubtaskIds = new ArrayList<>();
            Cursor dbIdResult = db.getSubtask(id);
            while (dbIdResult.moveToNext()) {
                sortedSubtaskIds.add(dbIdResult.getInt(1));
            }
            dbIdResult.close();
            //getting the newly generated id of the current task
            int newId = getTaskIdByName(taskName);
            //adding subtasks to current task if there are any
            for (int j = 0; j < subtasksCount; j++) {
                result = db.getSubtaskData(id,
                        sortedSubtaskIds.get(j));
                String subtask = null;
                long subtaskTimeCreated = 0;
                while (result.moveToNext()) {
                    subtask = result.getString(2);
                    subtaskTimeCreated = Long.parseLong(result.getString(4));
                }
                subtasksPresenter.addSubtask(newId, subtask, subtaskTimeCreated);
            }
            result.close();
        }

        //Only need to perform migration once
        MainActivity.preferences.edit().putBoolean(StringConstants.DATABASE_MERGED_KEY, true).apply();

    }

    @Override
    public int getDuesSet() {
        return taskViewModel.getDuesSet();
    }

    @Override
    public long getInterval(String repeatInterval, long stamp, int originalDay) {
        if (repeatInterval.equals(StringConstants.DAY)) {
            return 1000 * 60 * 60 * 24;
        } else if (repeatInterval.equals(StringConstants.WEEK)) {
            return 1000 * 60 * 60 * 24 * 7;
        } else if (repeatInterval.equals(StringConstants.MONTH)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(stamp);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            //Determining if it's a leap year
            int leapYear = 0;
            if (year % 4 == 0) {
                leapYear = 1;
            }
            long twentyFourHours = 1000 * 60 * 60 * 24;
            int multiplier;
            //months that have 31 days
            if (month == 0 || month == 2 || month == 4 || month == 6
                    || month == 7 || month == 9 || month == 11) {
                //if due on 31st day and following month doesn't have 31 days set to last day of following month
                if (originalDay == 31 && month != 6 && month != 11 && month != 0) {
                    multiplier = 30;
                //if due on 31st and following month is February set to last day of February
                } else if (originalDay == 31 && month == 0) {
                    multiplier = 28 + leapYear;
                //if due on 30th and following month is February set to last day of February
                }else if(originalDay == 30 && month == 0) {
                    multiplier = 29 + leapYear;
                //if due on 29th and following month is February set to last day of February
                }else if(originalDay == 29 && month == 0){
                    multiplier = 30 + leapYear;
                } else {
                    multiplier = 31;
                }
            //February
            } else if (month == 1) {
                //if original due day is 31 then set to 31st of following month
                if (originalDay == 31) {
                    multiplier = 31;
                //if original due day is 30 then set to 30th of following month
                } else if(originalDay == 30){
                    multiplier = 30;
                //if original due day is 39 then set to 39th of following month
                } else if(originalDay == 29){
                    multiplier = 29;
                }else{
                    multiplier = 28 + leapYear;
                }
            //months that have 30 days
            } else {
                //if original due day is 31 then set to 31st of following month
                if (originalDay == 31) {
                    multiplier = 31;
                } else {
                    multiplier = 30;
                }
            }
            return twentyFourHours * multiplier;
        }
        return 0;
    }

    @Override
    public void detectIfKilledEarly(Task taskAt) {
//        Calendar stampCal = Calendar.getInstance();
//        stampCal.setTimeInMillis(taskAt.getTimestamp());
//        Calendar currentCal = Calendar.getInstance();
//        if((taskAt.getTimestamp() - Calendar.getInstance().getTimeInMillis()) > (1000 * 60 * 60 * 24)){//TODO adjust for weekly and monthly
//            taskAt.setKilledEarly(true);
//        }
    }

    @Override
    public void setManualKill(Task taskAt) {
        taskAt.setManualKill(true);
//        update(taskAt);
    }

    @Override
    public boolean showReviewPrompt(int intShowReviewPrompt, long lngTimeInstalled) {
        Calendar calendar = new GregorianCalendar().getInstance();
        //show review prompt after three days
        if (intShowReviewPrompt == 0 && (lngTimeInstalled
                <= (calendar.getTimeInMillis() - 259200000))) {
            return true;
            //show review prompt after one week
        } else if (intShowReviewPrompt == 1 && (lngTimeInstalled
                <= (calendar.getTimeInMillis() - 604800000))) {
            return true;
            //show review prompt after one month
            //numbers getting too long. converting from milliseconds to seconds
        } else if (intShowReviewPrompt == 2 && ((lngTimeInstalled / 1000)
                <= ((calendar.getTimeInMillis() / 1000) - 2635200))) {
            return true;
            //show review prompt after two months
        } else if (intShowReviewPrompt == 3 && ((lngTimeInstalled / 1000)
                <= ((calendar.getTimeInMillis() / 1000) - 5270400))) {
            return true;
        }
        return false;
    }

}
