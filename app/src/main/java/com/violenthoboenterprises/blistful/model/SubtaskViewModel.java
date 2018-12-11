package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SubtaskViewModel extends AndroidViewModel {

    SubtaskRepository subtaskRepository;
    //TODO see if LiveData is needed
    private LiveData<List<Subtask>> allTasks;

    public SubtaskViewModel(@NonNull Application application) {
        super(application);
        subtaskRepository = new SubtaskRepository(application);
        allTasks = subtaskRepository.getAllSubtasks();
    }

    public void insert(Subtask subtask){subtaskRepository.insert(subtask);}
    public void update(Subtask subtask){subtaskRepository.update(subtask);}
    public void delete(Subtask subtask){subtaskRepository.delete(subtask);}

    //TODO see if LiveData is needed
    public LiveData<List<Subtask>> getAllSubtasks(){return allTasks;}

}
