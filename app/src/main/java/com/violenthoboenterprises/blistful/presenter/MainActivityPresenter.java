package com.violenthoboenterprises.blistful.presenter;

import android.os.Parcelable;

import com.violenthoboenterprises.blistful.model.Task;

public interface MainActivityPresenter {

    void addTask(long timestamp, String task, long timeCreated);

    void update(Task task);

    boolean showReviewPrompt(int intShowReviewPrompt, long lngTimeInstalled);

    void setTask(Task taskBeingEdited, String editedTaskString);

    void reinstateTask(Task taskToReinstate);

    void showPurchases();
}
