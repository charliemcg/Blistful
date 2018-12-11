package com.violenthoboenterprises.blistful;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.violenthoboenterprises.blistful.model.NotePresenterImpl;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.NotePresenter;
import com.violenthoboenterprises.blistful.view.NoteView;

import java.io.Serializable;

//TODO find out if necessary to extend MainActivity
public class NoteActivity extends MainActivity implements NoteView {

    String TAG = this.getClass().getSimpleName();
    TextView noteTextView;
    EditText noteEditText;
    InputMethodManager keyboard;
    ImageView submitNoteBtnDark, submitNoteOne, submitNoteOneHalf, submitNoteTwoHalf, submitNoteTwo;
    String dbTask;
    //Indicates that the active task has subtasks
    Boolean checklistExists;
    View noteRoot;
    private Toolbar noteToolbar;
    MenuItem trashNote, trashNoteOpen;
    private TaskViewModel taskViewModel;
    private NotePresenter notePresenter;
    private Task task;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);
        noteToolbar = findViewById(R.id.noteToolbar);
        setSupportActionBar(noteToolbar);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        notePresenter = new NotePresenterImpl
                (NoteActivity.this, taskViewModel, getApplicationContext());
        //getting the task to which this note is related
        task = getIntent().getParcelableExtra("task");

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        submitNoteBtnDark = findViewById(R.id.submitNoteBtnDark);
        submitNoteOne = findViewById(R.id.submitNoteOne);
        submitNoteTwo = findViewById(R.id.submitNoteTwo);
        submitNoteOneHalf = findViewById(R.id.submitNoteOneHalf);
        submitNoteTwoHalf = findViewById(R.id.submitNoteTwoHalf);
        checklistExists = false;
        inNote = true;
        noteRoot = findViewById(R.id.noteRoot);

        getSupportActionBar().setTitle(R.string.note);
        noteToolbar.setSubtitle(dbTask);

        noteTextView.setMovementMethod(new ScrollingMovementMethod());

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
        }
        dbResult.close();

        //keyboard will display the default edit text instead of the custom one without this line
        noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Actions to occur when user clicks submit
        submitNoteBtnDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                animateSubmitButton();

                //new note being added
                String newNote = noteEditText.getText().toString();
                task.setNote(newNote);
                taskViewModel.update(task);

                //display new note in the view
                noteTextView.setText(newNote);

                //clear the edit text
                noteEditText.setText("");

            }

        });

        //Long click allows editing of text
        noteTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                MainActivity.vibrate.vibrate(50);

                trashNote.setVisible(false);

                //show edit text
                noteEditText.setVisibility(View.VISIBLE);

                //show submit button
                submitNoteBtnDark.setVisibility(View.VISIBLE);

                //Focus on edit text so that keyboard does not cover it up
                noteEditText.requestFocus();

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //set text to existing note
                noteEditText.setText(task.getNote());

                //put cursor at end of text
                noteEditText.setSelection(noteEditText.getText().length());

                noteTextView.setText("");

                return true;
            }
        });

    }

    //Submit button collapses on itself on click
    private void animateSubmitButton() {
        //Animating the submit icon
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {

                submitNoteBtnDark.setVisibility(View.GONE);
                submitNoteOne.setVisibility(View.VISIBLE);

                final Handler handler2 = new Handler();

                final Runnable runnable2 = new Runnable() {
                    public void run() {

                        submitNoteOne.setVisibility(View.GONE);
                        submitNoteOneHalf.setVisibility(View.VISIBLE);

                        final Handler handler3 = new Handler();

                        final Runnable runnable3 = new Runnable() {
                            public void run() {

                                submitNoteOneHalf.setVisibility(View.GONE);
                                submitNoteTwo.setVisibility(View.VISIBLE);

                                final Handler handler4 = new Handler();

                                final Runnable runnable4 = new Runnable() {
                                    @Override
                                    public void run() {

                                        submitNoteTwo.setVisibility(View.GONE);
                                        submitNoteTwoHalf.setVisibility(View.VISIBLE);

                                        final Handler handler5 = new Handler();

                                        final Runnable runnable5 = new Runnable() {
                                            @Override
                                            public void run() {

                                                vibrate.vibrate(50);

                                                if (!mute) {
                                                    blip.start();
                                                }

                                                submitNoteTwoHalf.setVisibility(View.GONE);

                                                //Hide text box
                                                noteEditText.setVisibility(View.GONE);

                                                submitNoteBtnDark.setVisibility(View.GONE);

                                                trashNote.setVisible(true);

                                                //Hide keyboard
                                                keyboard.toggleSoftInput(InputMethodManager
                                                        .HIDE_IMPLICIT_ONLY, 0);

                                            }
                                        };
                                        handler5.postDelayed(runnable5, 50);
                                    }
                                };
                                handler4.postDelayed(runnable4, 50);
                            }
                        };
                        handler3.postDelayed(runnable3, 50);
                    }
                };
                handler2.postDelayed(runnable2, 50);
            }
        };
        handler.postDelayed(runnable, 50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_note, noteToolbar.getMenu());
            trashNote = this.noteToolbar.getMenu().findItem(R.id.killNoteItem);
            trashNoteOpen = this.noteToolbar.getMenu().findItem(R.id.trashOpen);
            if (noteTextView.getText().toString().equals("")) {
                trashNote.setVisible(false);
            } else {
                trashNote.setVisible(true);
            }
            return true;
        } else {
            trashNote.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Resetting alarm to off
        if (id == R.id.killNoteItem) {

            //animating the trash can
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    trashNote.setVisible(false);
                    trashNoteOpen.setVisible(true);

                    vibrate.vibrate(50);

                    if (!mute) {
                        trash.start();
                    }

                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                        public void run() {

                            trashNote.setVisible(true);
                            trashNoteOpen.setVisible(false);
                            final Handler handler3 = new Handler();
                            final Runnable runnable3 = new Runnable() {
                                @Override
                                public void run() {

                                    trashNote.setVisible(false);

                                    task.setNote(null);
                                    taskViewModel.update(task);

                                    noteTextView.setText("");

                                    //show add button
                                    noteEditText.setVisibility(View.VISIBLE);
                                    submitNoteBtnDark.setVisibility(View.VISIBLE);
                                }
                            };
                            handler3.postDelayed(runnable3, 100);
                        }
                    };
                    handler2.postDelayed(runnable2, 100);
                }
            };
            handler.postDelayed(runnable, 100);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {

        super.onPause();

        inNote = false;

    }

    @Override
    protected void onResume() {

        super.onResume();

        inNote = true;

        getSavedData();

    }

    //Existing notes are recalled when app opened
    private void getSavedData() {

        String theNote = task.getNote();

        //Display note if there is one
        if (theNote != null) {

            noteTextView.setText(theNote);
            this.getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            noteEditText.setVisibility(View.GONE);
            submitNoteBtnDark.setVisibility(View.GONE);

        }

        onCreateOptionsMenu(noteToolbar.getMenu());

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
