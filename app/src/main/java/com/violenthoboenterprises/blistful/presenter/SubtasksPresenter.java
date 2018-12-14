package com.violenthoboenterprises.blistful.presenter;

import com.violenthoboenterprises.blistful.model.Subtask;

import java.util.List;

public interface SubtasksPresenter {

    void addSubtask(int parentId, String subtask);

//    Subtask getSubtask(int parentId, int subtaskId);

    void update(Subtask subtask);

    List<Subtask> getSubtasksByParent(int parentId);

    int getId();
}
