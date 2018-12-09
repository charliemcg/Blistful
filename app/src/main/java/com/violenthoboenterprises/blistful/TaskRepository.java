package com.violenthoboenterprises.blistful;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    //TODO see if LiveData is needed
    private LiveData<List<Task>> allTasks;

    TaskRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getInstance(application);
        taskDao = taskDatabase.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    void insert(Task task){new InsertTaskAsyncTask(taskDao).execute(task);}

    void update(Task task){new UpdateTaskAsyncTask(taskDao).execute(task);}

    public void delete(Task task){new DeleteTaskAsyncTask(taskDao).execute(task);}

    //TODO see if LiveData is needed
    LiveData<List<Task>> getAllTasks(){return allTasks;}

    //Performing these tasks off of the UI thread
    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.insert(tasks[0]);
            return null;
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.delete(tasks[0]);
            return null;
        }
    }

}
