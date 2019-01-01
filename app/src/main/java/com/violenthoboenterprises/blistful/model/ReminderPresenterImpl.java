package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.presenter.ReminderPresenter;

import java.util.Calendar;
import java.util.Locale;

public class ReminderPresenterImpl implements ReminderPresenter {

    private final static String TAG = "ReminderPresenterImpl";
    private ReminderViewModel reminderViewModel;
    private TaskViewModel taskViewModel;
    private Task task;
    private Reminder reminder;
    private Context context;

    public ReminderPresenterImpl(TaskViewModel taskViewModel, ReminderViewModel reminderViewModel,
                                 Task task, Reminder reminder, Context context) {
        this.reminderViewModel = reminderViewModel;
        this.taskViewModel = taskViewModel;
        this.task = task;
        this.reminder = reminder;
        this.context = context;

        if (reminder == null) {
            addReminder(getId());
            this.reminder = reminderViewModel.getReminderByParent(getId());
        }

    }

    @Override
    public void addReminder(int parentId) {
        Reminder reminder = new Reminder(parentId);
        reminderViewModel.insert(reminder);
    }

    @Override
    public void updateTask(Task task) {
        taskViewModel.update(task);
    }

    @Override
    public void updateReminder(Reminder reminder) {
        reminderViewModel.update(reminder);
    }

    @Override
    public int getId() {
        return task.getId();
    }

    @Override
    public String getRepeatInterval() {
        return task.getRepeatInterval();
    }

    @Override
    public void setRepeatInterval(String interval) {
        task.setRepeatInterval(interval);
        updateTask(task);
    }

    @Override
    public String getTask() {
        return task.getTask();
    }

    @Override
    public void setTimestamp(long stamp) {
        task.setTimestamp(stamp);
        updateTask(task);
    }

    @Override
    public void setDisplayedTimestamp(long stamp) {
        task.setDisplayedTimestamp(stamp);
        updateTask(task);
    }

    @Override
    public int getYear() {
        return reminder.getYear();
    }

    @Override
    public int getHour() {
        return reminder.getHour();
    }

    @Override
    public int getMonth() {
        return reminder.getMonth();
    }

    @Override
    public int getDay() {
        return reminder.getDay();
    }

    @Override
    public void setYear(int year) {
        reminder.setYear(year);
        updateReminder(reminder);
    }

    @Override
    public void setMonth(int month) {
        reminder.setMonth(month);
        updateReminder(reminder);
    }

    @Override
    public void setDay(int day) {
        reminder.setDay(day);
        updateReminder(reminder);
    }

    @Override
    public void setHour(int hour) {
        reminder.setHour(hour);
        updateReminder(reminder);
    }

    @Override
    public void setMinute(int minute) {
        reminder.setMinute(minute);
        updateReminder(reminder);
    }

    @Override
    public int getMinute() {
        return reminder.getMinute();
    }

    @Override
    public String getFormattedDate() {
        String formattedMonth = "";
        int intMonth = getMonth() + 1;
        //getting string representation for month
        if (intMonth == 1) {
            formattedMonth = context.getString(R.string.jan);
        } else if (intMonth == 2) {
            formattedMonth = context.getString(R.string.feb);
        } else if (intMonth == 3) {
            formattedMonth = context.getString(R.string.mar);
        } else if (intMonth == 4) {
            formattedMonth = context.getString(R.string.apr);
        } else if (intMonth == 5) {
            formattedMonth = context.getString(R.string.may);
        } else if (intMonth == 6) {
            formattedMonth = context.getString(R.string.jun);
        } else if (intMonth == 7) {
            formattedMonth = context.getString(R.string.jul);
        } else if (intMonth == 8) {
            formattedMonth = context.getString(R.string.aug);
        } else if (intMonth == 9) {
            formattedMonth = context.getString(R.string.sep);
        } else if (intMonth == 10) {
            formattedMonth = context.getString(R.string.oct);
        } else if (intMonth == 11) {
            formattedMonth = context.getString(R.string.nov);
        } else if (intMonth == 12) {
            formattedMonth = context.getString(R.string.dec);
        }

        //setting date format based of locale
        String lang = String.valueOf(Locale.getDefault());
        if (lang.equals("en_AS") || lang.equals("en_BM")
                || lang.equals("en_GU") || lang.equals("en_PH")
                || lang.equals("en_PR") || lang.equals("en_UM")
                || lang.equals("en_US") || lang.equals("en_VI")) {
            return formattedMonth + " " + getDay();
        } else {
            return getDay() + " " + formattedMonth;
        }
    }

    @Override
    public String getFormattedTime() {
        String adjustedAmPm;
        int adjustedHour = getHour();
        int adjustedMinute = getMinute();
        String adjustedMinuteString;

        //find if am or pm
        if (adjustedHour < 12) {
            adjustedAmPm = "am";
        } else {
            adjustedAmPm = "pm";
        }

        //convert 24 hour time to 12 hour time
        if (adjustedHour == 0) {
            adjustedHour = 12;
        } else if (adjustedHour > 12) {
            adjustedHour -= 12;
        }

        //add '0' to front of single digit minutes
        if (adjustedMinute < 10) {
            adjustedMinuteString = "0" + adjustedMinute;
        } else {
            adjustedMinuteString = String.valueOf(adjustedMinute);
        }

        return adjustedHour + ":" + adjustedMinuteString + adjustedAmPm;
    }

    @Override
    public long getTimestamp() {
        return task.getTimestamp();
    }

    @Override
    public long getTimeCreated() {
        return task.getTimeCreated();
    }

    @Override
    public long getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    @Override
    public int getCurrentYear(long stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp);
        return cal.get(Calendar.YEAR);
    }

    @Override
    public int getCurrentMonth(long stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp);
        return cal.get(Calendar.MONTH);
    }

    @Override
    public int getCurrentDay(long stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getCurrentHour(long stamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(stamp);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public void setOriginalDay(int originalDay) {
        task.setOriginalDay(originalDay);
        taskViewModel.update(task);
    }

}
