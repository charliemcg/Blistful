package com.violenthoboenterprises.blistful.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class SubtaskViewModel extends AndroidViewModel {

    private SubtaskRepository subtaskRepository;

    public SubtaskViewModel(@NonNull Application application) {
        super(application);
        this.subtaskRepository = new SubtaskRepository(application);
    }

    public void insert(Subtask subtask){subtaskRepository.insert(subtask);}
    public void update(Subtask subtask){subtaskRepository.update(subtask);}
    public void delete(Subtask subtask){subtaskRepository.delete(subtask);}

    //TODO see if LiveData is needed
    public LiveData<List<Subtask>> getAllSubtasks(int parentId){
        return subtaskRepository.getAllSubtasks(parentId);
    }

    public Subtask getSubtask(int parentId, int subtaskId) {
        return subtaskRepository.getSubtask(parentId, subtaskId);
    }
}
