package com.violenthoboenterprises.blistful.view;

import com.violenthoboenterprises.blistful.model.Task;

public interface MainActivityView {

    void addTask(Task task);

    void showPurchases();

    void toggleFab(boolean showFab);
}
