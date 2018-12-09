package com.violenthoboenterprises.blistful.model;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;

import com.violenthoboenterprises.blistful.MainActivity;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private TaskViewModel taskViewModel;
    private MainActivityView mainActivityView;
    private Context context;

    public MainActivityPresenterImpl(MainActivityView mainActivityView, TaskViewModel taskViewModel, Context context){
        this.mainActivityView = mainActivityView;
        this.context = context;
        this.taskViewModel = taskViewModel;
    }

    @Override
    public void addTask(String taskName) {
        Task task = new Task(taskName);
        taskViewModel.insert(task);
    }

}
