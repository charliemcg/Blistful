package com.violenthoboenterprises.blistful;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    //TODO see if LiveData is needed
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getIntance(application);
        taskDao = taskDatabase.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public void insert(Task task){new InsertTaskAsyncTask(taskDao).execute(task);}

    public void update(Task task){new UpdateTaskAsyncTask(taskDao).execute(task);}

    public void delete(Task task){new DeleteTaskAsyncTask(taskDao).execute(task);}

    //TODO see if LiveData is needed
    public LiveData<List<Task>> getAllTasks(){return allTasks;}

    private class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        public InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.insert(tasks[0]);
            return null;
        }
    }

    private class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        public UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        public DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }
        @Override
        protected Void doInBackground(Task... tasks){
            taskDao.delete(tasks[0]);
            return null;
        }
    }

}
