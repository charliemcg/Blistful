package com.violenthoboenterprises.blistful.model;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.violenthoboenterprises.blistful.MainActivity;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
    public void addTask(String taskName, long timeCreated) {
        Task task = new Task(taskName, timeCreated);
        taskViewModel.insert(task);
    }

    @Override
    public void update(Task task) {
        taskViewModel.update(task);
    }

    @Override
    public void setTask(Task taskBeingEdited, String editedTaskString) {
        taskBeingEdited.setTask(editedTaskString);
        update(taskBeingEdited);
    }

    @Override
    public void reinstateTask(Task taskToReinstate) {
        taskViewModel.insert(taskToReinstate);
    }

    @Override
    public boolean showReviewPrompt(int intShowReviewPrompt, long lngTimeInstalled) {
        Calendar calendar = new GregorianCalendar().getInstance();
        //show review prompt after three days
        if(intShowReviewPrompt == 0 && (lngTimeInstalled
                <= (calendar.getTimeInMillis() - 259200000))){
            return true;
            //show review prompt after one week
        }else if (intShowReviewPrompt == 1 && (lngTimeInstalled
                <= (calendar.getTimeInMillis() - 604800000))){
            return true;
            //show review prompt after one month
            //numbers getting too long. converting from milliseconds to seconds
        }else if (intShowReviewPrompt == 2 && ((lngTimeInstalled / 1000)
                <= ((calendar.getTimeInMillis() / 1000) - 2635200))){
            return true;
            //show review prompt after two months
        }else if (intShowReviewPrompt == 3 && ((lngTimeInstalled / 1000)
                <= ((calendar.getTimeInMillis() / 1000) - 5270400))){
            return true;
        }
        return false;
    }

}
