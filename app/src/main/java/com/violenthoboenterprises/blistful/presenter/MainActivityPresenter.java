package com.violenthoboenterprises.blistful.presenter;

import android.os.Parcelable;

import com.violenthoboenterprises.blistful.model.Task;

public interface MainActivityPresenter {

    void addTask(String task);

    void update(Task task);

}
