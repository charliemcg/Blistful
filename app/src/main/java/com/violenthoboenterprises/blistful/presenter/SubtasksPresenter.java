package com.violenthoboenterprises.blistful.presenter;

import com.violenthoboenterprises.blistful.model.Subtask;

public interface SubtasksPresenter {

    void addSubtask(int parentId, String subtask);

    Subtask getSubtask(int parentId, int subtaskId);

    void update(Subtask subtask);

}
