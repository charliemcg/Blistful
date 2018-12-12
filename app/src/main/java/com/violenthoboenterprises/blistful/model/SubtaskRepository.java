package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.violenthoboenterprises.blistful.presenter.SubtaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDao;
import com.violenthoboenterprises.blistful.presenter.TaskDatabase;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubtaskRepository {

    private SubtaskDao subtaskDao;

    SubtaskRepository(Application application){
        TaskDatabase taskDatabase = TaskDatabase.getInstance(application);
        subtaskDao = taskDatabase.subtaskDao();
    }

    void insert(Subtask subtask){new SubtaskRepository.InsertSubtaskAsyncTask(subtaskDao).execute(subtask);}

    void update(Subtask subtask){new SubtaskRepository.UpdateSubtaskAsyncTask(subtaskDao).execute(subtask);}

    public void delete(Subtask subtask){new SubtaskRepository.DeleteSubtaskAsyncTask(subtaskDao).execute(subtask);}

    //getting all subtasks which belong to the parent task
    //TODO see if LiveData is needed
    LiveData<List<Subtask>> getAllSubtasks(int id){
        AsyncTask<Integer, Void, LiveData<List<Subtask>>> result = new GetAllSubtasksAsyncTask(subtaskDao).execute(id);
        LiveData<List<Subtask>> allSubtasks;
        try {
            allSubtasks = result.get();
            return allSubtasks;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Subtask getSubtask(int parentId, int subtaskId) {
        AsyncTask<Integer, Void, Subtask> result = new GetSubtaskAsyncTask(subtaskDao).execute(parentId, subtaskId);
        Subtask subtask;
        try {
            subtask = result.get();
            return subtask;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Subtask> getSubtasksByParent(int parentId) {
        AsyncTask<Integer, Void, List<Subtask>> result = new GetSubtasksByParentAsynTask(subtaskDao).execute(parentId);
        List<Subtask> subtasks;
        try {
            subtasks = result.get();
            return subtasks;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    private static class GetAllSubtasksAsyncTask extends AsyncTask<Integer, Void, LiveData<List<Subtask>>> {
        private SubtaskDao subtaskDao;
        GetAllSubtasksAsyncTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }
        @Override
        protected LiveData<List<Subtask>> doInBackground(Integer... integers){
            return subtaskDao.getAllTasks(integers[0]);
        }
    }

    private class GetSubtaskAsyncTask extends AsyncTask<Integer, Void, Subtask>{
        private SubtaskDao subtaskDao;
        public GetSubtaskAsyncTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }

        @Override
        protected Subtask doInBackground(Integer... integers) {
            return subtaskDao.getSubtask(integers[0], integers[1]);
        }
    }

    private class GetSubtasksByParentAsynTask extends AsyncTask<Integer, Void, List<Subtask>>{
        private SubtaskDao subtaskDao;
        public GetSubtasksByParentAsynTask(SubtaskDao subtaskDao) {
            this.subtaskDao = subtaskDao;
        }

        @Override
        protected List<Subtask> doInBackground(Integer... integers) {
            return subtaskDao.getSubtasksByParent(integers[0]);
        }
    }
}
