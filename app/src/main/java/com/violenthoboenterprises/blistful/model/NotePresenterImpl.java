package com.violenthoboenterprises.blistful.model;

import android.content.Context;

import com.violenthoboenterprises.blistful.presenter.NotePresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;
import com.violenthoboenterprises.blistful.view.NoteView;

public class NotePresenterImpl implements NotePresenter {

    private TaskViewModel taskViewModel;
    private NoteView noteView;
    private Context context;

    public NotePresenterImpl(NoteView noteView, TaskViewModel taskViewModel, Context context){
        this.noteView = noteView;
        this.context = context;
        this.taskViewModel = taskViewModel;
    }

}
