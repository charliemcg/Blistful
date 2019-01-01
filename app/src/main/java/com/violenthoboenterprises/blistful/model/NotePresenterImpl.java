package com.violenthoboenterprises.blistful.model;

import com.violenthoboenterprises.blistful.presenter.NotePresenter;

public class NotePresenterImpl implements NotePresenter {

    private TaskViewModel taskViewModel;
    private Task task;

    public NotePresenterImpl(TaskViewModel taskViewModel, Task task){
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
