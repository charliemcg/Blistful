package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SubtaskViewModel extends AndroidViewModel {

    SubtaskRepository subtaskRepository;
    //TODO see if LiveData is needed
//    private LiveData<List<Subtask>> allSubtasks;

    public SubtaskViewModel(@NonNull Application application) {
        super(application);
        this.subtaskRepository = new SubtaskRepository(application);
    }

    public void insert(Subtask subtask){subtaskRepository.insert(subtask);}
    public void update(Subtask subtask){subtaskRepository.update(subtask);}
    public void delete(Subtask subtask){subtaskRepository.delete(subtask);}

    //TODO see if LiveData is needed
//    public LiveData<List<Subtask>> getAllSubtasks(){return allTasks;}
    public LiveData<List<Subtask>> getAllSubtasks(int parentId){return subtaskRepository.getAllSubtasks(parentId);}

}
