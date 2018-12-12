package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.violenthoboenterprises.blistful.presenter.ReminderDao;
import com.violenthoboenterprises.blistful.presenter.SubtaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReminderRepository {

    private ReminderDao reminderDao;

    ReminderRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getInstance(application);
        reminderDao = taskDatabase.reminderDao();
    }

    void insert(Reminder reminder){new ReminderRepository.InsertReminderAsyncTask(reminderDao).execute(reminder);}

    void update(Reminder reminder){new ReminderRepository.UpdateReminderAsyncTask(reminderDao).execute(reminder);}

    public void delete(Reminder reminder){new ReminderRepository.DeleteReminderAsyncTask(reminderDao).execute(reminder);}

    //getting all reminder which belongs to the parent task
    public Reminder getReminderByParent(int parentId) {
        AsyncTask<Integer, Void, Reminder> result = new ReminderRepository.GetReminderByParentAsyncTask(reminderDao).execute(parentId);
        Reminder reminder;
        try {
            reminder = result.get();
            return reminder;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Performing these tasks off of the UI thread
    private static class InsertReminderAsyncTask extends AsyncTask<Reminder, Void, Void> {
        private ReminderDao reminderDao;
        InsertReminderAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }
        @Override
        protected Void doInBackground(Reminder... reminders){
            reminderDao.insert(reminders[0]);
            return null;
        }
    }

    private static class UpdateReminderAsyncTask extends AsyncTask<Reminder, Void, Void> {
        private ReminderDao reminderDao;
        UpdateReminderAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }
        @Override
        protected Void doInBackground(Reminder... reminders){
            reminderDao.update(reminders[0]);
            return null;
        }
    }

    private static class DeleteReminderAsyncTask extends AsyncTask<Reminder, Void, Void> {
        private ReminderDao reminderDao;
        DeleteReminderAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }
        @Override
        protected Void doInBackground(Reminder... reminders){
            reminderDao.delete(reminders[0]);
            return null;
        }
    }

    private class GetReminderByParentAsyncTask extends AsyncTask<Integer, Void, Reminder>{
        private ReminderDao reminderDao;
        public GetReminderByParentAsyncTask(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Reminder doInBackground(Integer... integers) {
            return reminderDao.getReminderByParent(integers[0]);
        }
    }

}
