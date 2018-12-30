package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.violenthoboenterprises.blistful.presenter.TaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

public class TaskRepository {

    private static String TAG = "TaskRepository";

    private TaskDao taskDao;
    //TODO see if LiveData is needed
    private LiveData<List<Task>> allTasks;

    TaskRepository(Application application) {
        TaskDatabase taskDatabase = TaskDatabase.getInstance(application);
        taskDao = taskDatabase.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    void insert(Task task) {
        new InsertTaskAsyncTask(taskDao).execute(task);
    }

    void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }

    //TODO see if LiveData is needed
    LiveData<List<Task>> getAllTasks(){
        return allTasks;
    }

    public List<Integer> getAllTimestamps() {
        AsyncTask<Void, Void, List<Integer>> result = new GetAllTimestampsAsyncTask(taskDao).execute();
        List<Integer> allTimestamps;
        try {
            allTimestamps = result.get();
            return allTimestamps;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTaskIdByName(String taskName) {
        AsyncTask<String, Void, Integer> result = new GetTaskIdByNameAsyncTask(taskDao).execute(taskName);
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDuesSet() {
        AsyncTask<Void, Void, Integer> result = new GetDuesSetAsyncTask(taskDao).execute();
        try{
            return result.get();
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        return 0;
    }

    public Task getTaskById(int id) {
        AsyncTask<Integer, Void, Task> result = new GetTaskByIdAsyncTask(taskDao).execute(id);
        try{
            return result.get();
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        return null;
    }

    //Performing these tasks off of the UI thread
    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        InsertTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
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
        protected Void doInBackground(Task... tasks) {
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
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

    private class GetAllTimestampsAsyncTask extends AsyncTask<Void, Void, List<Integer>> {
        private TaskDao taskDao;

        public GetAllTimestampsAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            return taskDao.getAllTimestamps();
        }
    }

    private class GetTaskIdByNameAsyncTask extends AsyncTask<String, Void, Integer> {
        private TaskDao taskDao;
        public GetTaskIdByNameAsyncTask(TaskDao taskDao) {this.taskDao = taskDao;}

        @Override
        protected Integer doInBackground(String... strings) {
            return taskDao.getTaskIdByName(strings[0]);
        }
    }

    private class GetDuesSetAsyncTask extends AsyncTask<Void, Void, Integer> {
        private TaskDao taskDao;
        GetDuesSetAsyncTask(TaskDao taskDao) {this.taskDao = taskDao;}

        @Override
        protected Integer doInBackground(Void... voids) {
            return taskDao.getDuesSet();
        }
    }

    private class GetTaskByIdAsyncTask extends AsyncTask<Integer, Void, Task> {
        private TaskDao taskDao;
        GetTaskByIdAsyncTask(TaskDao taskDao) {this.taskDao = taskDao;}

        @Override
        protected Task doInBackground(Integer... integers) {
            return taskDao.getTaskById(integers[0]);
        }
    }
}
