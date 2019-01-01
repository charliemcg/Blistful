package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.SubtasksView;

import java.util.List;

public class SubtasksPresenterImpl implements SubtasksPresenter {

    private SubtaskViewModel subtaskViewModel;
    private Task task;

    public SubtasksPresenterImpl(SubtaskViewModel subtaskViewModel, Task task) {
        this.subtaskViewModel = subtaskViewModel;
        this.task = task;
    }

    @Override
    public void addSubtask(int parentId, String subtaskName, long timeCreated) {
        Subtask subtask = new Subtask(parentId, subtaskName, timeCreated);
        subtaskViewModel.insert(subtask);
    }

    @Override
    public void update(Subtask subtask) {
        subtaskViewModel.update(subtask);
    }

    @Override
    public List<Subtask> getSubtasksByParent(int parentId) {
        return subtaskViewModel.getSubtasksByParent(parentId);
    }

    @Override
    public int getId() {
        return task.getId();
    }

    @Override
    public void rename(Subtask subtaskBeingEdited, String subtaskName) {
        subtaskBeingEdited.setSubtask(subtaskName);
        update(subtaskBeingEdited);
    }

    @Override
    public void reinstateSubTask(Subtask subtaskToReinstate) {
        subtaskViewModel.insert(subtaskToReinstate);
    }

}
