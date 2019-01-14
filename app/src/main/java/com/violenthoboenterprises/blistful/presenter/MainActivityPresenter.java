package com.violenthoboenterprises.blistful.presenter;

import com.violenthoboenterprises.blistful.model.Task;

public interface MainActivityPresenter {

    void addTask(String note, long dueTimestamp, String task, String repeatInterval,
                 long timeCreated, boolean manualKill, boolean killedEarly, int originalDay);

    void update(Task task);

    boolean showReviewPrompt(int intShowReviewPrompt, long lngTimeInstalled);

    void setTask(Task taskBeingEdited, String editedTaskString);

    void reinstateTask(Task taskToReinstate);

    void showPurchases();

    void toggleFab(boolean showFab);

    int getTaskIdByName(String taskName);

    void migrateDatabase();

    int getDuesSet();

    long getInterval(String repeatInterval, long stamp, int originalDay);

    String getFormattedDate(long timestamp);

    String getFormattedTime(long timestamp);
}
