package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository repository;
    //TODO see if LiveData is needed
    private LiveData<List<Task>> allTasks;
    private Task task;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    public void insert(Task task){repository.insert(task);}
    public void update(Task task){repository.update(task);}
    public void delete(Task task){repository.delete(task);}

    //TODO see if LiveData is needed
    public LiveData<List<Task>> getAllTasks(){return allTasks;}

    public Task getTask(int id){return task;}

    public List<Integer> getAllTimestamps() {
        return repository.getAllTimestamps();
    }
}
