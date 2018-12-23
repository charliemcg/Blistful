package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.SubtasksView;

import java.util.List;

public class SubtasksPresenterImpl implements SubtasksPresenter {

    private SubtaskViewModel subtaskViewModel;
    private SubtasksView subtasksView;
    private Task task;
    private Context context;

    public SubtasksPresenterImpl(SubtasksView subtasksView, SubtaskViewModel subtaskViewModel,
                                 Task task, Context context) {
        this.subtaskViewModel = subtaskViewModel;
        this.subtasksView = subtasksView;
        this.task = task;
        this.context = context;
    }

    @Override
    public void addSubtask(int parentId, String subtaskName, long timeCreated) {
        Subtask subtask = new Subtask(parentId, subtaskName, timeCreated);
        subtaskViewModel.insert(subtask);
    }

//    @Override
//    public Subtask getSubtask(int parentId, int subtaskId) {
//        return subtaskViewModel.getSubtask(parentId, subtaskId);
//    }

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
