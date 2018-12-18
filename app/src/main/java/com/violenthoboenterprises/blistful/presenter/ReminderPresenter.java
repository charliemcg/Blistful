package com.violenthoboenterprises.blistful.presenter;

import com.violenthoboenterprises.blistful.model.Reminder;
import com.violenthoboenterprises.blistful.model.Task;

public interface ReminderPresenter {

    void addReminder(int id);

    void updateTask(Task task);

    void updateReminder(Reminder reminder);

    int getId();

    String getRepeatInterval();

    void setRepeatInterval(String interval);

    String getTask();

    void setTimestamp(long stamp);

    int getYear();

    int getHour();

    int getMonth();

    int getDay();

    void setYear(int year);

    void setMonth(int month);

    void setDay(int day);

    void setHour(int hour);

    void setMinute(int minute);

    int getMinute();

    String getFormattedDate();

    String getFormattedTime();

    long getTimestamp();

    long getTimeCreated();

    void setDue(boolean b);
}
