package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    public void insert(Task task){repository.insert(task);}
    public void update(Task task){repository.update(task);}
    public void delete(Task task){repository.delete(task);}

    public LiveData<List<Task>> getAllTasks(){return allTasks;}

    public Task getTask(int id){return repository.getTaskById(id);}

    public List<Integer> getAllTimestamps() {
        return repository.getAllTimestamps();
    }

    public int getTaskIdByName(String taskName) {return repository.getTaskIdByName(taskName);}

    public int getDuesSet(){return repository.getDuesSet();}

    public List<Task> getAllTasksAsTasks() {return repository.getAllTasksAsTasks();}
}
