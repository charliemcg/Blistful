package com.violenthoboenterprises.blistful.presenter;

import com.violenthoboenterprises.blistful.model.Reminder;
import com.violenthoboenterprises.blistful.model.Task;

public interface ReminderPresenter {

    void addReminder(int id);

    void update(Task task);

    void updateReminder(Reminder reminder);

}