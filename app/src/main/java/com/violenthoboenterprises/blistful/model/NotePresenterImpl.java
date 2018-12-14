package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.NotePresenter;
import com.violenthoboenterprises.blistful.view.NoteView;

public class NotePresenterImpl implements NotePresenter {

    private TaskViewModel taskViewModel;
    private NoteView noteView;
    private Task task;
    private Context context;

    public NotePresenterImpl(NoteView noteView, TaskViewModel taskViewModel, Task task, Context context){
        this.noteView = noteView;
        this.context = context;
        this.task = task;
        this.taskViewModel = taskViewModel;
    }

    @Override
    public void update(String strNote) {
        task.setNote(strNote);
        taskViewModel.update(task);
    }

    @Override
    public String getNote() {
        return task.getNote();
    }

    @Override
    public String getTaskName() {
        return task.getTask();
    }
}
