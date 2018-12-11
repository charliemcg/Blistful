package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.violenthoboenterprises.blistful.presenter.SubtaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDatabase;

import java.util.List;

public class SubtaskRepository {

    private SubtaskDao subtaskDao;
    //TODO see if LiveData is needed
    private LiveData<List<Subtask>> allTasks;

    SubtaskRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getInstance(application);
        subtaskDao = taskDatabase.subtaskDao();
        allTasks = subtaskDao.getAllTasks();
    }

    void insert(Subtask subtask){new SubtaskRepository.InsertSubtaskAsyncTask(subtaskDao).execute(subtask);}

    void update(Subtask subtask){new SubtaskRepository.UpdateSubtaskAsyncTask(subtaskDao).execute(subtask);}

    public void delete(Subtask subtask){new SubtaskRepository.DeleteSubtaskAsyncTask(subtaskDao).execute(subtask);}

    //TODO see if LiveData is needed
    LiveData<List<Subtask>> getAllSubtasks(){return allTasks;}

    //Performing these tasks off of the UI thread
    private static class InsertSubtaskAsyncTask extends AsyncTask<Subtask, Void, Void> {
        private SubtaskDao subtaskDao;
        InsertSubtaskAsyncTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }
        @Override
        protected Void doInBackground(Subtask... subtasks){
            subtaskDao.insert(subtasks[0]);
            return null;
        }
    }

    private static class UpdateSubtaskAsyncTask extends AsyncTask<Subtask, Void, Void> {
        private SubtaskDao subtaskDao;
        UpdateSubtaskAsyncTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }
        @Override
        protected Void doInBackground(Subtask... subtasks){
            subtaskDao.update(subtasks[0]);
            return null;
        }
    }

    private static class DeleteSubtaskAsyncTask extends AsyncTask<Subtask, Void, Void> {
        private SubtaskDao subtaskDao;
        DeleteSubtaskAsyncTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }
        @Override
        protected Void doInBackground(Subtask... subtasks){
            subtaskDao.delete(subtasks[0]);
            return null;
        }
    }

}
