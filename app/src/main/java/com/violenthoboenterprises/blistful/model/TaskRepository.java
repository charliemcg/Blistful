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
//        List<Task> tempListNoDues = taskDao.getAllTasks();
//        List<Task> tempListWithDues = taskDao.getAllTasksByStamp();
//        tempListNoDues.addAll(tempListWithDues);
//        allTasks = (LiveData<List<Task>>) tempListNoDues;
//        int blah = taskDao.getTaskCount();
//        Log.d(TAG, "count " + blah);

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
    LiveData<List<Task>> getAllTasks(){return allTasks;}
//    LiveData<List<Task>> getAllTasks() {
//        AsyncTask<Void, Void, LiveData<List<Task>>> resultOne = new GetAllTasksNotDueAsyncTask(taskDao).execute();
//        AsyncTask<Void, Void, LiveData<List<Task>>> resultTwo = new GetAllTasksWithDueAsyncTask(taskDao).execute();
//        MediatorLiveData liveDataMerger = new MediatorLiveData<>();
//        try {
//            liveDataMerger.addSource(resultOne.get(), liveDataMerger::setValue);
//            liveDataMerger.addSource(resultTwo.get(), liveDataMerger::setValue);
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return liveDataMerger;
        ///////////////////////////////////
//        List<Task> blah = new ArrayList<>();
//        AsyncTask<Void, Void, List<Task>> resultOne = new GetAllTasksNotDueAsyncTask(taskDao).execute();
//        AsyncTask<Void, Void, List<Task>> resultTwo = new GetAllTasksWithDueAsyncTask(taskDao).execute();
//        try {
//            ArrayList<Task> blah = (ArrayList<Task>) resultOne.get();
//            for (int i = 0; i < blah.size(); i++) {
//                Log.d(TAG, "Task: " + blah.get(i));
//            }
//            MutableLiveData<List<Task>> fruitList;
//
//            fruitList = new MutableLiveData<>();
//            fruitList.setValue(blah);
//            return fruitList;
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

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

//    private class GetAllTasksNotDueAsyncTask extends AsyncTask<Void, Void, /*LiveData<*/List<Task>> {
//        private TaskDao taskDao;
//
//        public GetAllTasksNotDueAsyncTask(TaskDao taskDao) {
//            this.taskDao = taskDao;
//        }
//
//        @Override
//        protected /*LiveData<*/List<Task> doInBackground(Void... voids) {
//            return taskDao.getAllTasks();
//        }
//    }
//
//    private class GetAllTasksWithDueAsyncTask extends AsyncTask<Void, Void, /*LiveData<*/List<Task>> {
//        private TaskDao taskDao;
//
//        public GetAllTasksWithDueAsyncTask(TaskDao taskDao) {
//            this.taskDao = taskDao;
//        }
//
//        @Override
//        protected /*LiveData<*/List<Task> doInBackground(Void... voids) {
//            return taskDao.getAllTasksByStamp();
//        }
//    }
}
