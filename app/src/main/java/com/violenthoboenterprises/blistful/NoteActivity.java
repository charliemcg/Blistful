package com.violenthoboenterprises.blistful;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.violenthoboenterprises.blistful.model.NotePresenterImpl;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.NotePresenter;
import com.violenthoboenterprises.blistful.view.NoteView;

public class NoteActivity extends MainActivity implements NoteView {

    private final String TAG = this.getClass().getSimpleName();
    private TextView tvNote;
    private EditText etNote;
    private InputMethodManager keyboard;
    private ImageView btnSubmitNote, btnSubmitNoteOne, btnSubmitNoteTwo, btnSubmitNoteThree, btnSubmitNoteFour;
    private Toolbar noteToolbar;
    private MenuItem trashNote, trashNoteOpen;
    private TaskViewModel taskViewModel;
    private NotePresenter notePresenter;
    //The parent task to which the note belongs
    private Task task;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteToolbar = findViewById(R.id.tbNote);
        setSupportActionBar(noteToolbar);

        //getting the task to which this note is related
        task = (Task) getIntent().getSerializableExtra("task");
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        notePresenter = new NotePresenterImpl
                (NoteActivity.this, taskViewModel, task, getApplicationContext());

        tvNote = findViewById(R.id.tvNote);
        etNote = findViewById(R.id.etNote);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        btnSubmitNote = findViewById(R.id.btnSubmitNote);
        btnSubmitNoteOne = findViewById(R.id.btnSubmitNoteOne);
        btnSubmitNoteFour = findViewById(R.id.btnSubmitNoteTwo);
        btnSubmitNoteTwo = findViewById(R.id.btnSubmitNoteThree);
        btnSubmitNoteThree = findViewById(R.id.btnSubmitNoteFour);

        getSupportActionBar().setTitle(R.string.note);
        noteToolbar.setSubtitle(notePresenter.getTaskName());

        tvNote.setMovementMethod(new ScrollingMovementMethod());

        String theNote = notePresenter.getNote();

        //Display note if there is one
        if (theNote != null) {

            tvNote.setText(theNote);
            this.getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            etNote.setVisibility(View.GONE);
            btnSubmitNote.setVisibility(View.GONE);

        }

        onCreateOptionsMenu(noteToolbar.getMenu());

        //keyboard will display the default edit text instead of the custom one without this line
        etNote.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Actions to occur when user clicks submit
        btnSubmitNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Keyboard is inactive without this line
                etNote.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                animateSubmitButton();

                //new note being added
                String newNote = etNote.getText().toString();
                notePresenter.update(newNote);

                //display new note in the view
                tvNote.setText(newNote);

                //clear the edit text
                etNote.setText("");

            }

        });

        //Long click allows editing of text
        tvNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                MainActivity.vibrate.vibrate(50);

                trashNote.setVisible(false);

                //show edit text
                etNote.setVisibility(View.VISIBLE);

                //show submit button
                btnSubmitNote.setVisibility(View.VISIBLE);

                //Focus on edit text so that keyboard does not cover it up
                etNote.requestFocus();

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //set text to existing note
                etNote.setText(notePresenter.getNote());

                //put cursor at end of text
                etNote.setSelection(etNote.getText().length());

                tvNote.setText("");

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

                btnSubmitNote.setVisibility(View.GONE);
                btnSubmitNoteOne.setVisibility(View.VISIBLE);

                final Handler handler2 = new Handler();

                final Runnable runnable2 = new Runnable() {
                    public void run() {

                        btnSubmitNoteOne.setVisibility(View.GONE);
                        btnSubmitNoteTwo.setVisibility(View.VISIBLE);

                        final Handler handler3 = new Handler();

                        final Runnable runnable3 = new Runnable() {
                            public void run() {

                                btnSubmitNoteTwo.setVisibility(View.GONE);
                                btnSubmitNoteFour.setVisibility(View.VISIBLE);

                                final Handler handler4 = new Handler();

                                final Runnable runnable4 = new Runnable() {
                                    @Override
                                    public void run() {

                                        btnSubmitNoteFour.setVisibility(View.GONE);
                                        btnSubmitNoteThree.setVisibility(View.VISIBLE);

                                        final Handler handler5 = new Handler();

                                        final Runnable runnable5 = new Runnable() {
                                            @Override
                                            public void run() {

                                                vibrate.vibrate(50);

                                                if (!boolMute) {
                                                    mpBlip.start();
                                                }

                                                btnSubmitNoteThree.setVisibility(View.GONE);

                                                //Hide text box
                                                etNote.setVisibility(View.GONE);

                                                btnSubmitNote.setVisibility(View.GONE);

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
            trashNote = this.noteToolbar.getMenu().findItem(R.id.itemTrashNote);
            trashNoteOpen = this.noteToolbar.getMenu().findItem(R.id.itemTrashNoteOpen);
            if (tvNote.getText().toString().equals("")) {
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

        //Removing the note
        if (id == R.id.itemTrashNote) {

            //animating the trash can
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    trashNote.setVisible(false);
                    trashNoteOpen.setVisible(true);

                    vibrate.vibrate(50);

                    if (!boolMute) {
                        mpTrash.start();
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

                                    notePresenter.update(null);

                                    tvNote.setText("");

                                    //show edit text
                                    etNote.setVisibility(View.VISIBLE);
                                    btnSubmitNote.setVisibility(View.VISIBLE);
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

}
