package com.violenthoboenterprises.blistful;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.violenthoboenterprises.blistful.model.MainActivityPresenterImpl;
import com.violenthoboenterprises.blistful.model.Subtask;
import com.violenthoboenterprises.blistful.model.SubtaskViewModel;
import com.violenthoboenterprises.blistful.model.SubtasksAdapter;
import com.violenthoboenterprises.blistful.model.SubtasksPresenterImpl;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskAdapter;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.SubtasksView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class SubtasksActivity extends MainActivity implements SubtasksView {

    private String TAG = this.getClass().getSimpleName();
    private static InputMethodManager keyboard;
    private static EditText etSubtask;
    //flag used for determining if subtask is being edited or new task being created
    private static boolean subTaskBeingEdited;
    //The parent task to which the subtasks belong
    private Task task;
    //subtask that's being edited
    private Subtask subtaskEditing;
    private SubtaskViewModel subtaskViewModel;
    private SubtasksPresenter subtasksPresenter;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_layout);
        Toolbar subTasksToolbar = findViewById(R.id.subTasksToolbar);

        //Getting the parent task to which the subtasks are related
        task = (Task) getIntent().getSerializableExtra("task");
        subtaskViewModel = ViewModelProviders.of(this).get(SubtaskViewModel.class);
        subtasksPresenter = new SubtasksPresenterImpl(SubtasksActivity.this,
                subtaskViewModel, task, getApplicationContext());

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        etSubtask = findViewById(R.id.checklistEditText);

        //Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.subTasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //setting up the adapter
        final SubtasksAdapter subtasksAdapter = new SubtasksAdapter();
        recyclerView.setAdapter(subtasksAdapter);

        //observing the recycler view items for changes
        //TODO find out if observer is necessary
        subtaskViewModel = ViewModelProviders.of(this).get(SubtaskViewModel.class);
        //need to specifically get subtasks belonging to parent task
        subtaskViewModel.getAllSubtasks(subtasksPresenter.getId()).observe(this, new Observer<List<Subtask>>(){
            @Override
            public void onChanged(@Nullable List<Subtask> subtasks){
                subtasksAdapter.setSubtasks(subtasks);
            }
        });

        //detect swipes
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                subtaskViewModel.delete(subtasksAdapter.getSubtaskAt(viewHolder.getAdapterPosition()));
                String stringSnack = "Subtask deleted";
                showSnackbar(stringSnack);
            }
        }).attachToRecyclerView(recyclerView);

        //edit subtask on long click
        subtasksAdapter.setOnItemClickListener(new SubtasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subtask subtask) {

                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                //Indicates that a task is being edited
                subTaskBeingEdited = true;

                //setting subtask name in the edit text
                etSubtask.setText(subtask.getSubtask());

                //keeping a reference to the subtask which is being edited
                subtaskEditing = subtask;

            }
        });

        //setting text in toolbar
        subTasksToolbar.setTitle(R.string.subTasks);

        //Actions to occur when keyboard's 'Done' button is pressed
        etSubtask.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Keyboard is inactive without this line
                etSubtask.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                //Actions to occur when user submits new sub task
                if (actionId == EditorInfo.IME_ACTION_DONE && !subTaskBeingEdited) {

                    //Getting user data
                    String subtaskName = etSubtask.getText().toString();

                    //Clear text from text box
                    etSubtask.setText("");

                    //Don't allow blank tasks
                    if(!subtaskName.equals("")) {

                        vibrate.vibrate(50);

                        if(!boolMute) {
                            mpBlip.start();
                        }

                        subtasksPresenter.addSubtask(subtasksPresenter.getId(), subtaskName);

                    }

                    return true;

                //Actions to occur when editing sub tasks
                }else if(actionId == EditorInfo.IME_ACTION_DONE){

                    vibrate.vibrate(50);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String subtaskName = etSubtask.getText().toString();

                    //Clear text from text box
                    etSubtask.setText("");

                    //Don't allow blank tasks
                    if(!subtaskName.equals("")) {

                        //updating the subtask with the new name
                        subtaskEditing.setSubtask(subtaskName);
                        subtasksPresenter.update(subtaskEditing);

                        subTaskBeingEdited = false;

                    }

                    return true;

                }

                //Marking editing as complete
                subTaskBeingEdited = false;

                return true;

            }

        });

    }

    private void showSnackbar(String stringSnack) {
        View view = findViewById(R.id.subtasksRoot);
        Snackbar.make(view, stringSnack, Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(SubtasksActivity.this, "Reinstate subtask",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.purple))
                .show();
    }

}
