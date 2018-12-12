package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class ReminderViewModel extends AndroidViewModel {

    private ReminderRepository reminderRepository;

    public ReminderViewModel(@NonNull Application application) {
        super(application);
        reminderRepository = new ReminderRepository(application);
    }

    public void insert(Reminder reminder){reminderRepository.insert(reminder);}
    public void update(Reminder reminder){reminderRepository.update(reminder);}
    public void delete(Reminder reminder){reminderRepository.delete(reminder);}

    public Reminder getReminderByParent(int parentId) {
        return reminderRepository.getReminderByParent(parentId);
    }
}
