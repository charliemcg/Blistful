package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.NoteView;
import com.violenthoboenterprises.blistful.view.SubtasksView;

public class SubtasksPresenterImpl implements SubtasksPresenter {

    private SubtaskViewModel subtaskViewModel;
    private SubtasksView subtasksView;
    private Context context;

    public SubtasksPresenterImpl(SubtasksView subtasksView, SubtaskViewModel subtaskViewModel, Context context) {
        this.subtaskViewModel = subtaskViewModel;
        this.subtasksView = subtasksView;
        this.context = context;
    }

    @Override
    public void addSubtask(int parentId, String subtaskName) {
        Subtask subtask = new Subtask(parentId, subtaskName);
        subtaskViewModel.insert(subtask);
    }

}
