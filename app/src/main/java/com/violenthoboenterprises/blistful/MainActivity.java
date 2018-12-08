package com.violenthoboenterprises.blistful;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.MobileAds;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Random;

import static android.database.DatabaseUtils.queryNumEntries;

public class MainActivity extends AppCompatActivity implements
        BillingProcessor.IBillingHandler, AbsListView.OnScrollListener {

    //Indicates if a tasks properties are showing
    static boolean taskPropertiesShowing;
    //Indicates if a task's options are showing
    static boolean taskOptionsShowing;
    //Indicates if tasks can be clicked on
    static boolean tasksAreClickable;
    //Indicates if a task is being edited
    static boolean taskBeingEdited;
    //Indicates there are no menus or unusual stuff showing
    private boolean restoreNormalListView;
    //Allows the list to be updated
    static boolean goToMyAdapter;
    //Indicates that a checklist is showing
    static boolean checklistShowing;
    //Indicates that tasks should be faded out due to keyboard being up
    static boolean fadeTasks;
    //Used when making task centered in list view
    static boolean centerTask;
    //Used to indicate that user is in the note screen
    static boolean inNote;
    //Used to indicate that user is in the sub-tasks screen
    static boolean inChecklist;
    //Used to indicate that a repeating alarm is being set
    static boolean repeating;
    //Used to indicate that list needs to be reordered
    static boolean reorderList;
    //Reinstate alarm after reinstating task
    static boolean reinstateAlarm;
    //Used to determine that task is being set to complete
    static boolean completeTask;
    //Used to determine if sound effects should play or not
    static boolean mute;
    //used to indicate if color picker is showing
    static boolean colorPickerShowing;
    //used to indicate if purchase options are showing
    static boolean purchasesShowing;
    //used to indicate that user purchased ad removal
    static boolean adsRemoved;
    //used to indicate that user purchased reminders
    static boolean remindersAvailable;
    //used to indicate that the snooze options are showing
    static boolean snoozeRowShowing;
    //used to indicate that a task was long clicked
    static boolean longClicked;
    //used to block excessive sounds and animations if user kills tasks in close succession
    static boolean blockSoundAndAnimate;
    //indicates that task was just reinstated
    static boolean justReinstated;
    //attempting to upgrade to pro from MyAdapter causes an error so need to try again
    static boolean tryAgain;
    static boolean blah;

    //task properties require exit animation
    static boolean exitTaskProperties;
    //used to determine whether or not to show motivational toasts
    static boolean showMotivation;
    //don't show due dates until after ids have been reordered
    static boolean showDueDates;
    //indicates that user has been prompted to review the app once
    boolean reviewOne;
    //indicates that user has been prompted to review the app twice
    boolean reviewTwo;
    //indicates that user has been prompted to review the app three times
    boolean reviewThree;
    //indicates that user has been prompted to review the app four times
    boolean reviewFour;

    //Indicates which task has it's properties showing
    static int activeTask;
    //Height of the 'add' button
    static int addHeight;
    //Height of list view as viewable on screen
    static int listViewHeight;
    static int thePosition;
    //portrait width of device
    static int deviceWidth;
    //portrait height of device
    static int deviceheight;
    //indicates how many due dates are set because free users have a limitation
    static int duesSet;
    //indicates if the repeat hint should be shown
    static int repeatHint;
    //indicates if the rename hint should be shown
    static int renameHint;
    //indicates if the reinstate hint should be shown
    static int reinstateHint;
    //timestamp that keeps record of when user downloaded the app.
    // Used for determining when to prompt for a review
    int launchTime;

    //Interval between repeating alarms
    static long repeatInterval;

    //List of task names
    public static ArrayList<String> taskList;
    //Keeps track of task IDs sorted by due date
    public static ArrayList<String> sortedIDs;
    //Keeps track of task IDs sorted by due date to be used by note class
    public static ArrayList<String> sortedIdsForNote;

    //Toasts which show up when adding new task
    static String[] motivation;
    //Keep track of last phrase used so as to not have the same thing twice in a row
    static String lastToast;
    //Toasts which show up when completing task
    static String[] killedAffirmation;
    //Keep track of last phrase used so as to not have the same thing twice in a row
    static String lastKilledToast;
    //Decimal version of the highlight color
    static String highlightDec;

    //Required for setting notification alarms
    static Intent alertIntent;

    //Managers notification alarms
    static AlarmManager alarmManager;

    //Message that shows up when there are no tasks
    private ImageView noTasksToShow;
    private ImageView noTasksToShowWhite;

    //Custom toast
    static TextView toast;
    static RelativeLayout toastView;

    static PendingIntent pendIntent;

    //The editable text box that allows for creating and editing task names
    static EditText taskNameEditText;

    //The button that facilitates the adding of tasks
    static Button add;

    //Used for debugging purposes. Button should not be visible in release.
//    Button showDb;
//    Button showAlarmDb;
//    Button showSnoozeDb;
//    Button showUniversalDb;
//    Button showSubtasksDb;

    //Scrollable list
    static ListView theListView;

    //The master view
    static View activityRootView;

    //The keyboard
    static InputMethodManager keyboard;

    //Parameters of 'add' button
    static RelativeLayout.LayoutParams params;
    RelativeLayout.LayoutParams toastParams;

    //Allow phone to vibrate
    static Vibrator vibrate;

    //Allow for updating the list
    public static ListAdapter[] theAdapter;

    //Inflater for checklists
    static LayoutInflater inflater;

    //String used for debugging
    String TAG = this.getClass().getSimpleName();

    //Database for keeping track of notes
    static Database db;

    //for generating random number to select toast phrases
    static Random random = new Random();

    //The user selectable highlight color
    static String highlight;

    //Sound played when task marked as complete
    static MediaPlayer punch;
    //Sound played when toast displays
    static MediaPlayer sweep;
    //Default sound played throughout the app
    static MediaPlayer blip;
    //Sound played when user selects an in-app purchase
    static MediaPlayer chime;
    //Sound played when user selects a remove button
    static MediaPlayer trash;
    //Sound played when user is presented with a hint
    static MediaPlayer hint;

    //The action bar
    private Toolbar toolbarLight;
    static RelativeLayout.LayoutParams toolbarParams;

    //Action bar options
    MenuItem muteBtn;
    MenuItem proBtn;
    MenuItem motivationBtn;

    //The color picker view and it's corresponding buttons
    LinearLayout colorPicker;

    static BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.turquoise));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.enter_from_right, R.anim.enter_from_right);

        String ADKEY = "ca-app-pub-2378583121223638~3855319141";
        //App ID for AdMob
        MobileAds.initialize(this, ADKEY);

        toolbarLight = findViewById(R.id.toolbar_light);
        toolbarLight.setTitle("");
        setSupportActionBar(toolbarLight);

        //Initialising variables
        taskPropertiesShowing = false;
        tasksAreClickable = true;
        taskList = new ArrayList<>();
        noTasksToShowWhite = findViewById(R.id.noTasksWhite);
        taskNameEditText = findViewById(R.id.taskNameEditText);
        add = findViewById(R.id.add);
        theListView = findViewById(R.id.theListView);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        params = (RelativeLayout.LayoutParams) add.getLayoutParams();
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        inflater = LayoutInflater.from(getApplicationContext());
        addHeight = params.height;
        theAdapter = new ListAdapter[]{new MyAdapter(this, taskList)};
        activityRootView = findViewById(R.id.activityRoot);
        fadeTasks = false;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        db = new Database(this);
        lastToast = "";
        lastKilledToast = "";
        inNote = false;
        inChecklist = false;
        taskOptionsShowing = false;
        repeating = false;
        repeatInterval = 0;
        reorderList = false;
        sortedIDs = new ArrayList<>();
        reinstateAlarm = false;
        completeTask = false;
        punch = MediaPlayer.create(this, R.raw.punch);
        sweep = MediaPlayer.create(this, R.raw.sweep);
        blip = MediaPlayer.create(this, R.raw.blip);
        chime = MediaPlayer.create(this, R.raw.chime);
        trash = MediaPlayer.create(this, R.raw.trash);
        hint = MediaPlayer.create(this, R.raw.hint);
        mute = false;
        String BILLINGKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tgG+jdhW6GSpvOiNrY58CConsEH9S6iYxyaRxp7a3+9CPzhXohy0LIJxZFZPAkLC0PSJnlA3N2JUHfGSdE5hY/7nXwHas+a8XUaQLHdYaA9usOBUEWs24MZFVNrpg4LnshBuFdM6eJ737ReMvCZAz9/lfoACrRx8ABgYEPs74Y+Ms1697DrQ/OPJFT4BRVuSDBIWmEc8GY4dAlh3/C3DK6FsofsKhkC1+bztIUa2n0XNm5UTJZO4sj4d6K/5A4Qo5qUMvWUFQ08L+1DbNif40y/j4ps8yDn3EW/LNPKbZ9m5avE4j6lLdXMRZ22a8OYhv//MVPhoCJ0/yeXcuOCwQIDAQAB";
        bp = new BillingProcessor(this, BILLINGKEY, this);
        adsRemoved = false;
        remindersAvailable = false;
        motivation = new String[]{getString(R.string.getItDone),
                getString(R.string.smashThatTask), getString(R.string.beAWinner),
                getString(R.string.onlyWimpsGiveUp), getString(R.string.dontBeAFailure),
                getString(R.string.beVictorious), getString(R.string.killThisTask)};
        killedAffirmation = new String[]{getString(R.string.youKilledThisTask),
                getString(R.string.neverGiveUp), getString(R.string.youreAnInspiration),
                getString(R.string.accomplishmentsMakeYouStronger),
                getString(R.string.yourePositivityPaysOff)};
        exitTaskProperties = false;
        snoozeRowShowing = false;
        toast = findViewById(R.id.toast);
        toastView = findViewById(R.id.toastView);
        longClicked = false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidth = displayMetrics.widthPixels;
        deviceheight = displayMetrics.heightPixels;
        duesSet = 0;
        showMotivation = false;
        showDueDates = true;
        repeatHint = 0;
        renameHint = 0;
        launchTime = 0;
        reviewOne = false;
        reviewTwo = false;
        reviewThree = false;
        reviewFour = false;
        toastParams = (RelativeLayout.LayoutParams) toastView.getLayoutParams();
        toolbarParams = (RelativeLayout.LayoutParams) toolbarLight.getLayoutParams();
        theListView.setOnScrollListener(this);
        blockSoundAndAnimate = false;
        justReinstated = false;
        tryAgain = false;
        blah = false;

        db.insertUniversalData(mute);

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            highlight = dbResult.getString(2);
            adsRemoved = dbResult.getInt(5) > 0;
            remindersAvailable = dbResult.getInt(6) > 0;
            highlightDec = dbResult.getString(29);
        }
        dbResult.close();

        //Put data in list
        theListView.setAdapter(theAdapter[0]);

//        taskNameEditText.setBackgroundColor(Color.parseColor(highlight));
//        toast.setBackgroundColor(Color.parseColor(highlight));

        //Make task clickable
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Tasks are not clickable if keyboard is up
                if (tasksAreClickable && !completeTask) {

                    vibrate.vibrate(50);

                    //checking if task has been killed
                    Boolean killed = false;
                    Cursor result = db.getData(Integer
                            .parseInt(sortedIDs.get(position)));
                    while (result.moveToNext()) {
                        killed = result.getInt(6) > 0;
                    }
                    result.close();

                    //Selecting a task to view options
                    if (!taskPropertiesShowing && !killed) {

                        viewProperties(position);

                        theListView.post(new Runnable() {
                            @Override
                            public void run() {
                                theListView.setSelection(activeTask);
                            }
                        });

                        //Removes completed task
                    } else if (!taskPropertiesShowing && killed) {

                        removeTask(position);

                        //Removes task options from view
                    } else {

                        removeTaskProperties();

                    }

                } else {

                    completeTask = false;

                }

            }

        });

        //Long click allows for editing/reinstating task
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                //checking if task has been killed
                Boolean killed = false;
                Cursor result = db.getData(Integer
                        .parseInt(sortedIDs.get(position)));
                while (result.moveToNext()) {
                    killed = result.getInt(6) > 0;
                }
                result.close();

                //Determine if it's possible to edit task
                if (tasksAreClickable && !killed && !taskPropertiesShowing) {

                    longClicked = true;

                    rename(position);

                    //long click reinstates task that is crossed out
                } else if (tasksAreClickable && killed && !taskPropertiesShowing) {

                    reinstate(position);

                }

                return true;

            }

        });

        //Actions to occur when 'add' selected
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskBeingEdited = false;

                goToMyAdapter = true;

                vibrate.vibrate(50);

                //Show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                //Set return button to 'Done'
                taskNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

                //Ensure that there is no previous text in the text box
                taskNameEditText.setText("");

                //Actions to occur when keyboard is showing
                checkKeyboardShowing();

            }

        });

////////////////////////////Used for debugging purposes////////////////////////////
//        showDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllData();
//
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("NOTE: " + res.getString(1) + "\n");
//                    buffer.append("CHECKLIST: " + res.getString(2) + "\n");
//                    buffer.append("TIMESTAMP: " + res.getString(3) + "\n");
//                    buffer.append("TASK: " + res.getString(4) + "\n");
//                    buffer.append("DUE: " + res.getString(5) + "\n");
//                    buffer.append("KILLED: " + res.getString(6) + "\n");
//                    buffer.append("BROADCAST: " + res.getString(7) + "\n");
//                    buffer.append("REPEAT: " + res.getString(8) + "\n");
//                    buffer.append("OVERDUE: " + res.getString(9) + "\n");
//                    buffer.append("SNOOZED: " + res.getString(10) + "\n");
//                    buffer.append("SHOWONCE: " + res.getString(11) + "\n");
//                    buffer.append("INTERVAL: " + res.getString(12) + "\n");
//                    buffer.append("REPEATINTERVAL: " + res.getString(13) + "\n");
//                    buffer.append("IGNORED: " + res.getString(14) + "\n");
//                    buffer.append("CREATETIMESTAMP: " + res.getString(15) + "\n");
//                    buffer.append("SORTEDINDEX: " + res.getString(16) + "\n");
//                    buffer.append("CHECKLISTSIZE: " + res.getString(17) + "\n");
//                    buffer.append("SNOOZESTAMP: " + res.getString(21) + "\n\n");
//                }
//                res.close();
//
//                showMessage("Data", buffer.toString());
//
//            }
//        });

//        showAlarmDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllAlarmData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("HOUR: " + res.getString(1) + "\n");
//                    buffer.append("MINUTE: " + res.getString(2) + "\n");
//                    buffer.append("AMPM: " + res.getString(3) + "\n");
//                    buffer.append("DAY: " + res.getString(4) + "\n");
//                    buffer.append("MONTH: " + res.getString(5) + "\n");
//                    buffer.append("YEAR: " + res.getString(6) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

//        showSnoozeDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = noteDb.getAllSnoozeData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("HOUR: " + res.getString(1) + "\n");
//                    buffer.append("MINUTE: " + res.getString(2) + "\n");
//                    buffer.append("AMPM: " + res.getString(3) + "\n");
//                    buffer.append("DAY: " + res.getString(4) + "\n");
//                    buffer.append("MONTH: " + res.getString(5) + "\n");
//                    buffer.append("YEAR: " + res.getString(6) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

//        showUniversalDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllUniversalData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("MUTE: " + res.getString(1) + "\n");
//                    buffer.append("HIGHLIGHT: " + res.getString(2) + "\n");
//                    buffer.append("DARKLIGHT: " + res.getString(3) + "\n");
//                    buffer.append("ACTIVETASKNAME: " + res.getString(4) + "\n");
//                    buffer.append("ADSREMOVED: " + res.getString(5) + "\n");
//                    buffer.append("REMINDERSAVAILABLE: " + res.getString(6) + "\n");
//                    buffer.append("CYCLECOLORS: " + res.getString(7) + "\n");
//                    buffer.append("TASKLISTSIZE: " + res.getString(8) + "\n");
//                    buffer.append("CHECKLISTLISTSIZE: " + res.getString(9) + "\n");
//                    buffer.append("SETALARM: " + res.getString(10) + "\n");
//                    buffer.append("YEAR: " + res.getString(11) + "\n");
//                    buffer.append("MONTH: " + res.getString(12) + "\n");
//                    buffer.append("DAY: " + res.getString(13) + "\n");
//                    buffer.append("HOUR: " + res.getString(14) + "\n");
//                    buffer.append("MINUTE: " + res.getString(15) + "\n");
//                    buffer.append("AMPM: " + res.getString(17) + "\n");
//                    buffer.append("DUESSET: " + res.getString(19) + "\n");
//                    buffer.append("HIGHLIGHTDEC: " + res.getString(29) + "\n\n");
//                }
//                res.close();
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

//        showSubtasksDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor res = db.getAllSubtaskData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("SUBTASKID: " + res.getString(1) + "\n");
//                    buffer.append("SUBTASK: " + res.getString(2) + "\n");
//                    buffer.append("SUBTASKKILLED: " + res.getString(3) + "\n");
//                    buffer.append("TIMECREATED: " + res.getString(4) + "\n\n");
//                }
//                res.close();
//
//                showMessage("Data", buffer.toString());
//
//            }
//
//        });

        //Actions to occur when user submits new task
        taskNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Actions to take when creating new task
                if ((actionId == EditorInfo.IME_ACTION_DONE) && !taskBeingEdited) {

                    if (!mute) {
                        blip.start();
                    }

                    vibrate.vibrate(50);

                    //Text box and keyboard disappear
                    taskNameEditText.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //Getting user data
                    String taskName = taskNameEditText.getText().toString();

                    //Clear text from text box
                    taskNameEditText.setText("");

                    //Add new task in task list
                    createTask(taskName, taskList, taskBeingEdited);

                    //Checks to see if there are still tasks available
                    noTasksLeft();

                    if (!taskName.equals("")) {

                        Calendar timeNow = new GregorianCalendar();

                        //create records in database
                        db.insertData(Integer.parseInt(sortedIDs
                                        .get(taskList.size() - 1)), "", taskName,
                                Integer.parseInt(sortedIDs.get(taskList.size() - 1)),
                                String.valueOf(timeNow.getTimeInMillis() / 1000));
                        db.insertAlarmData(Integer.parseInt(sortedIDs
                                        .get(taskList.size() - 1)), "", "",
                                "", "", "", "");
                        db.insertSnoozeData(Integer.parseInt(sortedIDs
                                        .get(taskList.size() - 1)), "", "",
                                "", "", "", "");

                        showDueDates = false;

                        reorderList = true;

                        if (MainActivity.renameHint <= 2) {
                            if (MainActivity.renameHint == 2) {
                                MainActivity.toast.setText(R.string.longClickToRename);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        hint.start();
                                        MainActivity.toastView.startAnimation(AnimationUtils
                                                .loadAnimation(MainActivity.this,
                                                        R.anim.enter_from_right_fast));
                                        MainActivity.toastView.setVisibility(View.VISIBLE);
                                        final Handler handler2 = new Handler();
                                        final Runnable runnable2 = new Runnable() {
                                            public void run() {
                                                MainActivity.toastView.startAnimation
                                                        (AnimationUtils.loadAnimation
                                                                (MainActivity.this,
                                                                        android.R.anim.fade_out));
                                                MainActivity.toastView.setVisibility(View.GONE);
                                            }
                                        };
                                        handler2.postDelayed(runnable2, 2500);
                                    }
                                };

                                handler.postDelayed(runnable, 500);
                            } else {
                                if (showMotivation) {
                                    //showing motivational toast
                                    int i = random.nextInt(7);
                                    while (motivation[i].equals(lastToast)) {
                                        i = random.nextInt(7);
                                    }
                                    lastToast = motivation[i];
                                    toast.setText(motivation[i]);
                                    final Handler handler = new Handler();

                                    final Runnable runnable = new Runnable() {
                                        public void run() {
                                            if (!mute) {
                                                sweep.start();
                                            }
                                            toastView.startAnimation(AnimationUtils.loadAnimation
                                                    (MainActivity.this,
                                                            R.anim.enter_from_right_fast));
                                            toastView.setVisibility(View.VISIBLE);
                                            final Handler handler2 = new Handler();
                                            final Runnable runnable2 = new Runnable() {
                                                public void run() {
                                                    toastView.startAnimation(
                                                            AnimationUtils.loadAnimation
                                                                    (MainActivity.this,
                                                                            android.R.anim.fade_out));
                                                    toastView.setVisibility(View.GONE);
                                                }
                                            };
                                            handler2.postDelayed(runnable2, 1500);
                                        }
                                    };

                                    handler.postDelayed(runnable, 500);
                                }
                            }
                            MainActivity.renameHint++;
                            MainActivity.db.updateRenameHint(MainActivity.renameHint);
                        } else {
                            if (showMotivation) {
                                //showing motivational toast
                                int i = random.nextInt(7);
                                while (motivation[i].equals(lastToast)) {
                                    i = random.nextInt(7);
                                }
                                lastToast = motivation[i];
                                toast.setText(motivation[i]);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        if (!mute) {
                                            sweep.start();
                                        }
                                        toastView.startAnimation(AnimationUtils.loadAnimation
                                                (MainActivity.this,
                                                        R.anim.enter_from_right_fast));
                                        toastView.setVisibility(View.VISIBLE);
                                        final Handler handler2 = new Handler();
                                        final Runnable runnable2 = new Runnable() {
                                            public void run() {
                                                toastView.startAnimation
                                                        (AnimationUtils.loadAnimation
                                                                (MainActivity.this,
                                                                        android.R.anim.fade_out));
                                                toastView.setVisibility(View.GONE);
                                            }
                                        };
                                        handler2.postDelayed(runnable2, 1500);
                                    }
                                };

                                handler.postDelayed(runnable, 500);
                            }
                        }
                    }

                    theListView.setAdapter(theAdapter[0]);

                    return true;

                    //Actions to take when editing existing task
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (!mute) {
                        blip.start();
                    }

                    vibrate.vibrate(50);

                    taskNameEditText.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //Getting user data
                    String editedTaskString = taskNameEditText.getText().toString();

                    createTask(editedTaskString, taskList, taskBeingEdited);

                    theListView.setAdapter(theAdapter[0]);

                    tasksAreClickable = true;

                    //Marking editing as complete
                    taskBeingEdited = false;

                    //Bringing back the 'add' button
                    params.height = addHeight;

                    add.setLayoutParams(params);

                    return true;

                }

                return false;

            }

        });

    }

    private void checkLightDark() {
        setSupportActionBar(toolbarLight);
        theListView.setAdapter(theAdapter[0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            proBtn = this.toolbarLight.getMenu().findItem(R.id.buy);
            motivationBtn = this.toolbarLight.getMenu().findItem(R.id.motivation);
            muteBtn = this.toolbarLight.getMenu().findItem(R.id.mute);
            if (showMotivation) {
                motivationBtn.setChecked(true);
            }
            if (!mute) {
                muteBtn.setChecked(true);
            }
            if (adsRemoved && remindersAvailable) {
                proBtn.setVisible(false);
            }
            return true;
        } else if (colorPickerShowing || purchasesShowing) {
            proBtn.setEnabled(false);
            return false;
        } else {
            proBtn.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        vibrate.vibrate(50);

        //Actions to occur if user selects 'mute'
        if (id == R.id.mute) {

            if (mute) {
                mute = false;
                item.setChecked(true);
                db.updateMute(false);
            } else {
                mute = true;
                item.setChecked(false);
                db.updateMute(true);
            }

            return true;

            //Actions to occur if user selects the pro icon
        } else if (id == R.id.buy) {

            purchasePrompt();

            return true;

        }

        //Actions to occur if user selects 'motivation'
        else if (id == R.id.motivation) {

            if (showMotivation) {
                showMotivation = false;
                item.setChecked(false);
                db.updateMotivation(false);
            } else {
                showMotivation = true;
                item.setChecked(true);
                db.updateMotivation(true);
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //Create prompt that enables user to upgrade to pro
    private void purchasePrompt() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.purchases);

        Button positive = dialog.findViewById(R.id.positive);
        Button negative = dialog.findViewById(R.id.negative);

        //Buy button actions
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if (!remindersAvailable && !adsRemoved) {

                    vibrate.vibrate(50);

                    if (!mute) {
                        chime.start();
                    }

                    bp.purchase(MainActivity.this, "unlock_all");

                }

            }
        });

        //Cancel button options
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, final int first,
                         final int visible, final int total) {

        //Detect if last list item is showing. 'Add' button can block it and therefore needs to
        //be removed in certain situations
        if (absListView.getId() == R.id.theListView) {

            if ((first + visible) == total && visible != total) {

                //Removes add button so as to not cover the last item
                params.height = 0;

                add.setLayoutParams(params);

            } else if (!taskPropertiesShowing) {

                //Returns the 'add' button
                params.height = addHeight;

                add.setLayoutParams(params);

            }
        }
    }

    ////////Shows table results for debugging purposes////////
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void removeTask(int position) {

        if (!mute) {
            blip.start();
        }

        taskList.remove(position);

        MainActivity.checklistShowing = true;

        db.updateTaskListSize(taskList.size());
        db.updateChecklistListSize(taskList.size());

        //deleting data related to deleted task
        db.deleteData(String.valueOf(sortedIDs.get(position)));
        db.deleteAlarmData(String.valueOf(sortedIDs.get(position)));
        db.deleteSnoozeData(String.valueOf(sortedIDs.get(position)));
        db.deleteAllSubtaskData(String.valueOf(sortedIDs.get(position)));

        //Cancel notification alarms if one is set
        alarmManager.cancel(pendIntent.getService(this,
                Integer.parseInt(sortedIDs.get(position)), alertIntent, 0));

        sortedIDs.remove(position);

        Cursor result;
        String id;
        String note;
        Boolean checklist;

        //Getting existing data before going to Database class to change ids.
        for (int i = (activeTask + 1); i < taskList.size(); i++) {
            result = db.getData(Integer.parseInt(sortedIDs.get(i)));
            id = "";
            note = "";
            checklist = false;
            while (result.moveToNext()) {
                id = result.getString(0);
                note = result.getString(1);
                checklist = result.getInt(2) == 1;
            }
            db.updateData(id, note, checklist);
            result.close();
        }

        //Updates the view
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                this, MainActivity.taskList)};
        theListView.setAdapter(theAdapter[0]);

        //Checks to see if there are still tasks left
        noTasksLeft();
    }

    //Show selected task's properties
    private void viewProperties(int position) {

        onPause();

        //Marks task as having it's properties showing
        taskPropertiesShowing = true;

        centerTask = true;

        //Gets position of selected task
        activeTask = position;

        //Updates the view
        theListView.setAdapter(theAdapter[0]);

        //Can't change visibility of 'add' button. Have to set height to zero instead.
        params.height = 0;

        add.setLayoutParams(params);

    }

    //remove selected task's properties
    private void removeTaskProperties() {

        //Updates the view
        theListView.setAdapter(theAdapter[0]);

        //Marks properties as not showing
        taskPropertiesShowing = false;

        //Returns the 'add' button
        params.height = addHeight;

        add.setLayoutParams(params);
    }

    //renames task
    public void rename(int i) {

        //Cannot update the list until after the task has been updated.
        goToMyAdapter = false;

        //Actions to occur when keyboard is showing
        checkKeyboardShowing();

        //Indicates that a task is being edited
        taskBeingEdited = true;

        activeTask = i;

        tasksAreClickable = false;

        fadeTasks = true;

        centerTask = true;

        theListView.setAdapter(theAdapter[0]);

        //Can't change visibility of 'add' button. Have to set height to zero instead.
        params.height = 0;

        add.setLayoutParams(params);
    }

    //reinstates completed task
    public void reinstate(int i) {

        if (showMotivation) {
            toast.setText(R.string.taskReinstated);
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    if (!mute) {
                        sweep.start();
                    }

                    toastView.startAnimation(AnimationUtils.loadAnimation
                            (MainActivity.this, R.anim.enter_from_right_fast));
                    toastView.setVisibility(View.VISIBLE);
                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                        public void run() {
                            toastView.startAnimation(AnimationUtils.loadAnimation
                                    (MainActivity.this, android.R.anim.fade_out));
                            toastView.setVisibility(View.GONE);
                        }
                    };
                    handler2.postDelayed(runnable2, 1500);
                }
            };

            handler.postDelayed(runnable, 500);
        }

        //checking if task has a due date
        Boolean due = false;
        Cursor result = db.getData(Integer
                .parseInt(sortedIDs.get(i)));
        while (result.moveToNext()) {
            due = result.getInt(5) > 0;
        }
        result.close();

        //marks task as not killed in database
        db.updateKilled(String.valueOf(sortedIDs.get(i)), false);
        if (!remindersAvailable && duesSet >= 5) {
            db.updateAlarmData(String.valueOf(
                    sortedIDs.get(i)),
                    "", "", "", "", "", "");
            db.updateTimestamp(String.valueOf(sortedIDs.get(i)), "0");
            db.updateDue(String.valueOf(sortedIDs.get(i)), false);
            db.updateRepeat(String.valueOf(sortedIDs.get(i)), false);
            db.updateSnooze(String.valueOf(sortedIDs.get(i)), false);
            db.updateInterval(String.valueOf(sortedIDs.get(i)), "0");
            db.updateRepeatInterval(String.valueOf(sortedIDs.get(i)), "");
            db.updateIgnored(String.valueOf(sortedIDs.get(i)), false);
            db.updateOverdue(String.valueOf(sortedIDs.get(i)), false);
        } else if (!remindersAvailable && due) {
            duesSet++;
            db.updateDuesSet(duesSet);
        }

        reinstateAlarm = true;

        new Reorder();
        //Updating the view with the new order
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                this, MainActivity.taskList)};
        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

    }

    //Create a new task
    private void createTask(final String taskName, ArrayList taskList, boolean taskBeingEdited) {

        //Don't allow blank tasks
        if (!taskName.equals("")) {

            if (!taskBeingEdited) {

                taskList.add(taskName);

                db.updateTaskListSize(taskList.size());
                db.updateChecklistListSize(taskList.size());

                //finding unique ID for task
                int i = 0;
                boolean idIsSet = false;
                while (!idIsSet) {
                    if (sortedIDs.contains(String.valueOf(i))) {
                        i++;
                    } else {
                        sortedIDs.add(taskList.size() - 1, String.valueOf(i));
                        db.updateSortedIndex(String.valueOf(taskList.size() - 1), i);
                        idIsSet = true;
                    }
                }

                alertIntent = new Intent(this, AlertReceiver.class);

                pendIntent = PendingIntent.getBroadcast(this, i, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

            } else {

                taskList.set(activeTask, taskName);

                db.updateName(sortedIDs.get(activeTask), taskName);

            }

        }

    }

    //Tells user to add tasks when task list is empty
    private void noTasksLeft() {

        //Checks if there are any existing tasks
        if (taskList.size() == 0) {

            //Inform user to add some tasks

            noTasksToShowWhite.setVisibility(View.VISIBLE);

        } else {
            noTasksToShowWhite.setVisibility(View.GONE);
        }

    }

    //Actions to occur when keyboard is showing
    void checkKeyboardShowing() {

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect screen = new Rect();

                activityRootView.getWindowVisibleDisplayFrame(screen);

                if (screen.bottom != deviceheight) {

                    fadeTasks = true;

                    if (goToMyAdapter) {

                        theListView.setAdapter(theAdapter[0]);

                        goToMyAdapter = false;

                    }

                    taskNameEditText.setFocusable(true);

                    taskNameEditText.requestFocus();

                    //Keyboard is inactive without this line
                    taskNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                    //Textbox is visible and 'add' button is gone
                    // whenever keyboard is showing
                    taskNameEditText.setVisibility(View.VISIBLE);

                    params.height = 0;

                    add.setLayoutParams(params);

                    tasksAreClickable = false;

                    restoreNormalListView = true;

                } else if (restoreNormalListView) {

                    fadeTasks = false;

                    //Textbox is gone and 'add' button is visible whenever
                    //keyboard is not showing
                    taskNameEditText.setVisibility(View.GONE);

                    params.height = addHeight;

                    add.setLayoutParams(params);

                    tasksAreClickable = true;

                    theListView.setAdapter(theAdapter[0]);

                    restoreNormalListView = false;

                    //Once editing is complete the adapter can update the list
                    goToMyAdapter = true;

                }

            }

        });

    }

    private void colorPickerShowing() {
        if (colorPickerShowing) {
            colorPicker.startAnimation(AnimationUtils.loadAnimation
                    (this, R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    colorPicker.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 200);
            colorPickerShowing = false;
        } else {

            purchasePrompt();

        }

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Tasks are not clickable if keyboard is up
                if (tasksAreClickable && !completeTask) {

                    vibrate.vibrate(50);

                    //checking if task has been killed
                    Boolean killed = false;
                    Cursor result = db.getData(Integer
                            .parseInt(sortedIDs.get(position)));
                    while (result.moveToNext()) {
                        killed = result.getInt(6) > 0;
                    }
                    result.close();

                    //Selecting a task to view options
                    if (!taskPropertiesShowing && !killed) {

                        viewProperties(position);

                        //Removes completed task
                    } else if (!taskPropertiesShowing) {

                        removeTask(position);

                        //Removes task options from view
                    } else {

                        removeTaskProperties();

                    }

                } else {

                    completeTask = false;

                }

            }

        });

        add.setClickable(true);

        onCreateOptionsMenu(toolbarLight.getMenu());


        theListView.setAdapter(theAdapter[0]);

    }

    //actions to occur if user clicks the complete check box
    public void complete(View view) {

        if (tasksAreClickable && !MainActivity.blockSoundAndAnimate) {

            if (!mute) {
                punch.start();
            }

            LinearLayout parentLayout = (LinearLayout) view.getParent();

            thePosition = theListView.getPositionForView(parentLayout);

            completeTask = true;

            theListView.performItemClick(theListView.getAdapter().getView(
                    thePosition, null, null), thePosition,
                    theListView.getAdapter().getItemId(thePosition));

            theListView.setAdapter(theAdapter[0]);

            //getting task data
            boolean dbRepeat = false;
            boolean dbOverdue = false;
            boolean dbSnooze = false;
            Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(thePosition)));
            while (dbResult.moveToNext()) {
                dbRepeat = dbResult.getInt(8) > 0;
                dbOverdue = dbResult.getInt(9) > 0;
                dbSnooze = dbResult.getInt(10) > 0;
            }
            dbResult.close();

            if (dbRepeat && !dbSnooze && !dbOverdue) {

                db.updateKilledEarly(MainActivity.sortedIDs.get(thePosition), true);

            }

            new Reorder();

            MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                    this, MainActivity.taskList)};
            theListView.setAdapter(theAdapter[0]);

        }

    }

    public void showPro(View view) {

        purchasePrompt();

    }

    private void showPrompt(final int launchTime) {

        final Calendar calendar = Calendar.getInstance();

        if (!reviewOne && ((launchTime <= (calendar.getTimeInMillis()
                / 1000 / 60 / 60) - 72))) {

            int reviewNumber = 1;
            prompt(reviewNumber);

        } else if (!reviewTwo && ((launchTime <= (calendar.getTimeInMillis()
                / 1000 / 60 / 60) - 168))) {

            int reviewNumber = 2;
            prompt(reviewNumber);

        } else if (!reviewThree && ((launchTime <= (calendar.getTimeInMillis()
                / 1000 / 60 / 60) - 732))) {

            int reviewNumber = 3;
            prompt(reviewNumber);

        } else if (!reviewFour && ((launchTime <= (calendar.getTimeInMillis()
                / 1000 / 60 / 60) - 1464))) {

            int reviewNumber = 4;
            prompt(reviewNumber);

        }
    }

    private void prompt(final int reviewNumber) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.review_dialog_light);

        Button positive = dialog.findViewById(R.id.positive);
        Button negative = dialog.findViewById(R.id.negative);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewOne = true;
                reviewTwo = true;
                reviewThree = true;
                reviewFour = true;
                db.updateReviewOne(true);
                db.updateReviewTwo(true);
                db.updateReviewThree(true);
                db.updateReviewFour(true);
                String URL = "https://play.google.com/store/apps/details?id=com.violenthoboenterprises.blistful";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(URL));
                startActivity(i);
                dialog.dismiss();

            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if (reviewNumber == 1) {
                    reviewOne = true;
                    db.updateReviewOne(true);
                } else if (reviewNumber == 2) {
                    reviewTwo = true;
                    db.updateReviewTwo(true);
                } else if (reviewNumber == 3) {
                    reviewThree = true;
                    db.updateReviewThree(true);
                } else if (reviewNumber == 4) {
                    reviewFour = true;
                    db.updateReviewFour(true);
                }

            }
        });

        dialog.show();

    }

    @Override
    public void onProductPurchased(@NonNull String productId,
                                   @Nullable TransactionDetails details) {

        //Inform user of successful purchase
        if (productId.equals("unlock_all")) {

            toast.setText(R.string.thank_you_for_purchase);
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    if (!mute) {
                        sweep.start();
                    }

                    toastView.startAnimation(AnimationUtils.loadAnimation
                            (MainActivity.this, R.anim.enter_from_right_fast));
                    toastView.setVisibility(View.VISIBLE);
                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                        public void run() {
                            toastView.startAnimation(AnimationUtils.loadAnimation
                                    (MainActivity.this, android.R.anim.fade_out));
                            toastView.setVisibility(View.GONE);
                        }
                    };
                    handler2.postDelayed(runnable2, 2000);
                }
            };

            handler.postDelayed(runnable, 500);

            //Update values so that app appears to be in 'pro mode'
            db.updateAdsRemoved(true);
            db.updateRemindersAvailable(true);
            adsRemoved = true;
            remindersAvailable = true;
            proBtn.setVisible(false);

        }

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

        //try purchasing again in case error was a once off
        if (tryAgain) {
            tryAgain = false;
            bp.purchase(MainActivity.this, "unlock_all");
        //inform user of error
        } else {
            Toast.makeText(MainActivity.this, R.string.somethingWentWrong, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();

        sortedIdsForNote = sortedIDs;

    }

    @Override
    protected void onResume() {

        super.onResume();

        taskBeingEdited = false;
        removeTaskProperties();

        getSavedData();

    }

    private void getSavedData() {

        //clearing the lists before adding data back into them so as to avoid duplication
        taskList.clear();
        sortedIDs.clear();

        int taskListSize = db.getTotalRows();

        //getting app-wide data
        Cursor dbResult = db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            highlight = dbResult.getString(2);
            adsRemoved = dbResult.getInt(5) > 0;
            remindersAvailable = dbResult.getInt(6) > 0;
            duesSet = dbResult.getInt(19);
            showMotivation = dbResult.getInt(20) > 0;
            repeatHint = dbResult.getInt(21);
            renameHint = dbResult.getInt(22);
            reinstateHint = dbResult.getInt(23);
            launchTime = dbResult.getInt(24);
            reviewOne = dbResult.getInt(25) > 0;
            reviewTwo = dbResult.getInt(26) > 0;
            reviewThree = dbResult.getInt(27) > 0;
            reviewFour = dbResult.getInt(28) > 0;
        }
        dbResult.close();

        checkLightDark();

        //restoring task list
        ArrayList<Integer> tempSortedIDs = new ArrayList<>();

        ArrayList<Integer> IDList = db.getIDs();

        for (int i = 0; i < taskListSize; i++) {

            Cursor sortedIdsResult = db.getData(IDList.get(i));
            while (sortedIdsResult.moveToNext()) {
                tempSortedIDs.add(sortedIdsResult.getInt(16));
            }
            sortedIdsResult.close();

        }

        Collections.sort(tempSortedIDs);

        for (int i = 0; i < taskListSize; i++) {

            sortedIDs.add(String.valueOf(tempSortedIDs.get(i)));

        }

        for (int i = 0; i < taskListSize; i++) {
            Cursor sharedPreferencesResult = db.getData(IDList.get(i));
            while (sharedPreferencesResult.moveToNext()) {
                taskList.add(sharedPreferencesResult.getString(4));
            }
            sharedPreferencesResult.close();
        }

        new Reorder();

        theListView.post(new Runnable() {
            @Override
            public void run() {
                theListView.setSelection(activeTask);
            }
        });

        //Updating the view with the new order
        theAdapter = new ListAdapter[]{new MyAdapter(
                this, taskList)};
        theListView.setAdapter(theAdapter[0]);

        alertIntent = new Intent(this, AlertReceiver.class);

        //Checks to see if there are still tasks left
        noTasksLeft();

        //Prompt user to leave review if required
        showPrompt(launchTime);

        //Setting the position for the toast
        toastParams.setMargins(15, (int) (deviceheight / 1.35), 0, 0);

    }

    @Override
    //Return to previous selection when back is pressed
    public void onBackPressed() {

        //options to properties
        if (colorPickerShowing) {
            colorPickerShowing();
        } else if (purchasesShowing) {
            colorPickerShowing();
        } else if (taskOptionsShowing) {
            theListView.setAdapter(theAdapter[0]);
            taskOptionsShowing = false;
            //Properties to home
        } else if (taskPropertiesShowing) {
            exitTaskProperties = true;
            removeTaskProperties();
            //Exit app
        } else {
            super.onBackPressed();
        }

    }

}