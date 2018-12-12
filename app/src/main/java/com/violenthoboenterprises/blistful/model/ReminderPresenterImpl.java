package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.ReminderPresenter;
import com.violenthoboenterprises.blistful.view.NoteView;
import com.violenthoboenterprises.blistful.view.ReminderView;

public class ReminderPresenterImpl implements ReminderPresenter {

    private ReminderViewModel reminderViewModel;
    private ReminderView reminderView;
    private Context context;

    public ReminderPresenterImpl(ReminderView ReminderView,
                                 ReminderViewModel reminderViewModel, Context context) {
        this.reminderViewModel = reminderViewModel;
        this.reminderView = reminderView;
        this.context = context;
    }

    @Override
    public void addReminder(int parentId) {
        Reminder reminder = new Reminder(parentId);
        reminderViewModel.insert(reminder);
    }

}
