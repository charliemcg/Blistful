package com.violenthoboenterprises.blistful;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.violenthoboenterprises.blistful.model.MainActivityPresenterImpl;
import com.violenthoboenterprises.blistful.model.SubtaskViewModel;
import com.violenthoboenterprises.blistful.model.SubtasksPresenterImpl;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskAdapter;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        BillingProcessor.IBillingHandler, MainActivityView {

    private final String TAG = this.getClass().getSimpleName();

    //Used to determine if sound effects should play or not
    public static boolean boolMute;
    //used to indicate that user purchased ad removal
    private boolean boolAdsRemoved;
    //used to indicate that user purchased reminders
    public boolean boolRemindersAvailable;
    //used to determine whether or not to show motivational toasts
    private boolean boolShowMotivation;

    //indicates how many due dates are set because free users have a limitation
    private int intDuesSet;
    //indicates if the repeat hint should be shown
    private int intRepeatHint;
    //indicates if the rename hint should be shown
    private int intRenameHint;
    //indicates if the reinstate hint should be shown
    private int intReinstateHint;
    //timestamp that keeps record of when user downloaded the app.
    // Used for determining when to prompt for a review
    private long lngTimeInstalled;
    //indicates if the review prompt should be shown
    private int intShowReviewPrompt;
    //height of device minus keyboard
    private int deviceheight;
    //keeps track of the selected task so recyclerview
    //knows which task to update in regards to icons etc
    public static int intPositionToUpdate;

    //Toasts which show up when adding new task
    private String[] strMotivation;
    //Keep track of last phrase used so as to not have the same thing twice in a row
    private String strLastToast;
    //Toasts which show up when completing task
    private String[] strKilledAffirmation;
    //Keep track of last phrase used so as to not have the same thing twice in a row
    private String strLastKilledToast;

    //Custom toast
    private TextView toast;
    private RelativeLayout toastView;

    //The editable text box that allows for creating and editing task names
    private EditText etTask;

    //Graphic to display if there are no tasks
    private ImageView imgNoTasks;

    //Placeholder banner for when ad cannot be loaded
    private ImageView imgBanner;

    //The banner ad
    private AdView adView;

    //The master view
    private View activityRootView;

    //The keyboard
    private InputMethodManager keyboard;

    //Allow phone to vibrate
    public static Vibrator vibrate;

    //for generating random number to select toast phrases
    private Random random = new Random();

    //Sound played when task marked as complete
    public static MediaPlayer mpPunch;
    //Sound played when toast displays
    public static MediaPlayer mpSweep;
    //Default sound played throughout the app
    public static MediaPlayer mpBlip;
    //Sound played when user selects an in-app purchase
    public static MediaPlayer mpChime;
    //Sound played when user selects a remove button
    public static MediaPlayer mpTrash;
    //Sound played when user is presented with a hint
    public static MediaPlayer mpHint;

    //The action bar
    private Toolbar toolbarLight;

    private MenuItem miPro;

    private static BillingProcessor billingProcessor;

    public static TaskViewModel taskViewModel;

    private FloatingActionButton fab;

    private MainActivityPresenter mainActivityPresenter;

    public TaskAdapter adapter;

    private Task taskBeingEdited;

    public static PendingIntent pendingIntent;
    public static Intent alertIntent;
    public static AlarmManager alarmManager;

    //preferences used for persisting app-wide data
    public static SharedPreferences preferences;
    //keys for shared preferences
//    public String MUTE_KEY = "mute_key";
//    public String ADS_REMOVED_KEY = "ads_removed_key";
//    public String REMINDERS_AVAILABLE_KEY = "reminders_available_key";
//    public String MOTIVATION_KEY = "motivation_key";
//    public String REPEAT_HINT_KEY = "repeat_hint_key";
//    public String RENAME_HINT_KEY = "rename_hint_key";
//    public String REINSTATE_HINT_KEY = "reinstate_hint_key";
//    public String SHOW_REVIEW_KEY = "show_review_key";
//    public String TIME_INSTALLED_KEY = "time_installed_key";
//    public String REFRESH_THIS_ITEM = "refresh_this_item";
//    public int DELETE_TASK_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.turquoise));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String ADKEY = "ca-app-pub-2378583121223638~3855319141";
        //App ID for AdMob
        MobileAds.initialize(this, ADKEY);

        toolbarLight = findViewById(R.id.toolbar_light);
        toolbarLight.setTitle("");
        setSupportActionBar(toolbarLight);

        preferences = this.getSharedPreferences("com.violenthoboenterprises.blistful",
                Context.MODE_PRIVATE);
        boolMute = preferences.getBoolean(StringConstants.MUTE_KEY, false);
        boolAdsRemoved = preferences.getBoolean(StringConstants.ADS_REMOVED_KEY, false);//TODO change to false
        boolRemindersAvailable = preferences.getBoolean(StringConstants.REMINDERS_AVAILABLE_KEY, false);//TODO change to false
        boolShowMotivation = preferences.getBoolean(StringConstants.MOTIVATION_KEY, true);
        intRepeatHint = preferences.getInt(StringConstants.REPEAT_HINT_KEY, 0);
        intRenameHint = preferences.getInt(StringConstants.RENAME_HINT_KEY, 0);
        intReinstateHint = preferences.getInt(StringConstants.REINSTATE_HINT_KEY, 0);
        intShowReviewPrompt = preferences.getInt(StringConstants.SHOW_REVIEW_KEY, 0);
        lngTimeInstalled = preferences.getLong(StringConstants.TIME_INSTALLED_KEY, 0);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (lngTimeInstalled == 0) {
            long defaultTime = new GregorianCalendar().getInstance().getTimeInMillis();
            preferences.edit().putLong(StringConstants.TIME_INSTALLED_KEY, defaultTime).apply();
            lngTimeInstalled = preferences.getLong(StringConstants.TIME_INSTALLED_KEY, 0);
        }

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        SubtaskViewModel subtaskViewModel =
                ViewModelProviders.of(this).get(SubtaskViewModel.class);
        mainActivityPresenter = new MainActivityPresenterImpl
                (MainActivity.this, taskViewModel, getApplicationContext());
        SubtasksPresenter subtasksPresenter = new SubtasksPresenterImpl
                (null, subtaskViewModel, null, getApplicationContext());

        //Initialising variables
        etTask = findViewById(R.id.taskNameEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        activityRootView = findViewById(R.id.activityRoot);
        strLastToast = "";
        strLastKilledToast = "";
        mpPunch = MediaPlayer.create(this, R.raw.punch);
        mpSweep = MediaPlayer.create(this, R.raw.sweep);
        mpBlip = MediaPlayer.create(this, R.raw.blip);
        mpChime = MediaPlayer.create(this, R.raw.chime);
        mpTrash = MediaPlayer.create(this, R.raw.trash);
        mpHint = MediaPlayer.create(this, R.raw.hint);
        String BILLINGKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0tgG+jdhW6GSpvOiNrY58CConsEH9S6iYxyaRxp7a3+9CPzhXohy0LIJxZFZPAkLC0PSJnlA3N2JUHfGSdE5hY/7nXwHas+a8XUaQLHdYaA9usOBUEWs24MZFVNrpg4LnshBuFdM6eJ737ReMvCZAz9/lfoACrRx8ABgYEPs74Y+Ms1697DrQ/OPJFT4BRVuSDBIWmEc8GY4dAlh3/C3DK6FsofsKhkC1+bztIUa2n0XNm5UTJZO4sj4d6K/5A4Qo5qUMvWUFQ08L+1DbNif40y/j4ps8yDn3EW/LNPKbZ9m5avE4j6lLdXMRZ22a8OYhv//MVPhoCJ0/yeXcuOCwQIDAQAB";
        billingProcessor = new BillingProcessor(this, BILLINGKEY, this);
        strMotivation = new String[]{getString(R.string.getItDone),
                getString(R.string.smashThatTask), getString(R.string.beAWinner),
                getString(R.string.onlyWimpsGiveUp), getString(R.string.dontBeAFailure),
                getString(R.string.beVictorious), getString(R.string.killThisTask)};
        strKilledAffirmation = new String[]{getString(R.string.youKilledThisTask),
                getString(R.string.neverGiveUp), getString(R.string.youreAnInspiration),
                getString(R.string.accomplishmentsMakeYouStronger),
                getString(R.string.yourePositivityPaysOff)};
        toast = findViewById(R.id.toast);
        toastView = findViewById(R.id.toastView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceheight = displayMetrics.heightPixels;
        intDuesSet = 0;
        imgNoTasks = findViewById(R.id.imgNoTasks);
        imgBanner = findViewById(R.id.banner);
        adView = findViewById(R.id.adView);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask(null);
            }
        });

        //Setting up the recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //setting up the adapter
        adapter = new TaskAdapter(this, mainActivityPresenter,
                subtasksPresenter, activityRootView, this, taskViewModel);
        recyclerView.setAdapter(adapter);

        //observing the recycler view items for changes
        //TODO find out if observer is necessary
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                adapter.setTasks(tasks);
                if (adapter.getItemCount() > 0) {
                    imgNoTasks.setVisibility(View.GONE);
                    if (adapter.getItemCount() > 4 && !boolAdsRemoved) {
                        showBannerAd();
                    } else {
                        imgBanner.setVisibility(View.GONE);
                    }
                } else {
                    imgNoTasks.setVisibility(View.VISIBLE);
                }
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

                if (adapter.getTaskAt(viewHolder.getAdapterPosition()).getRepeatInterval() == null) {
                    //Saving a temporary instance of the deleted task should it need to be reinstated
                    Task taskToReinstate = adapter.getTaskAt(viewHolder.getAdapterPosition());
                    taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));

                    String stringSnack = "Task deleted";
                    showSnackbar(stringSnack, taskToReinstate);
                } else {
                 //////////////////////////////////////////////////////////////
                    if (preferences.getInt(StringConstants.REPEAT_HINT_KEY, 0) <= 10) {
                        if ((preferences.getInt(StringConstants.REPEAT_HINT_KEY, 0) == 1)
                                || (preferences.getInt(StringConstants.REPEAT_HINT_KEY, 0) == 10)) {
                            toast.setText(R.string.youCanCancelRepeat);
                            final Handler handler = new Handler();

                            final Runnable runnable = new Runnable() {
                                public void run() {
                                    mpHint.start();
                                    toastView.startAnimation(AnimationUtils.loadAnimation
                                            (MainActivity.this, R.anim.enter_from_right_fast));
                                    toastView.setVisibility(View.VISIBLE);
                                    final Handler handler2 = new Handler();
                                    final Runnable runnable2 = new Runnable() {
                                        public void run() {
                                            toastView.startAnimation
                                                    (AnimationUtils.loadAnimation
                                                            (MainActivity.this, android.R.anim.fade_out));
                                            toastView.setVisibility(View.GONE);
                                        }
                                    };
                                    handler2.postDelayed(runnable2, 2500);
                                }
                            };

                            handler.postDelayed(runnable, 500);
                        }
                    }
//                    } else if (MainActivity.showMotivation) {
//                        //showing motivational toast
//                        int i = MainActivity.random.nextInt(5);
//                        while (MainActivity.killedAffirmation[i].equals(MainActivity.lastKilledToast)) {
//                            i = MainActivity.random.nextInt(5);
//                        }
//                        MainActivity.lastKilledToast = MainActivity.killedAffirmation[i];
//                        MainActivity.toast.setText(MainActivity.killedAffirmation[i]);
//                        final Handler handler = new Handler();
//
//                        final Runnable runnable = new Runnable() {
//                            public void run() {
//                                if (!MainActivity.mute) {
//                                    MainActivity.sweep.start();
//                                }
//                                MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
//                                        (getContext(), R.anim.enter_from_right_fast));
//                                MainActivity.toastView.setVisibility(View.VISIBLE);
//                                final Handler handler2 = new Handler();
//                                final Runnable runnable2 = new Runnable() {
//                                    public void run() {
//                                        MainActivity.toastView.startAnimation(
//                                                AnimationUtils.loadAnimation
//                                                        (getContext(),
//                                                                android.R.anim.fade_out));
//                                        MainActivity.toastView.setVisibility(View.GONE);
//                                    }
//                                };
//                                handler2.postDelayed(runnable2, 1500);
//                            }
//                        };
//
//                        handler.postDelayed(runnable, 500);
//                    }
                    int remindersSoFar = preferences.getInt(StringConstants.REPEAT_HINT_KEY, 0);
                    preferences.edit().putInt(StringConstants.REPEAT_HINT_KEY, ++remindersSoFar).apply();
//                } else if (MainActivity.showMotivation) {
//                    //showing motivational toast
//                    int i = MainActivity.random.nextInt(5);
//                    while (MainActivity.killedAffirmation[i].equals(MainActivity.lastKilledToast)) {
//                        i = MainActivity.random.nextInt(5);
//                    }
//                    MainActivity.lastKilledToast = MainActivity.killedAffirmation[i];
//                    MainActivity.toast.setText(MainActivity.killedAffirmation[i]);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            if (!MainActivity.mute) {
//                                MainActivity.sweep.start();
//                            }
//                            MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toastView.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable() {
//                                public void run() {
//                                    MainActivity.toastView.startAnimation(
//                                            AnimationUtils.loadAnimation
//                                                    (getContext(),
//                                                            android.R.anim.fade_out));
//                                    MainActivity.toastView.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                }
                    ////////////////////////////////////////////////////////////////
                }

            }
        }).attachToRecyclerView(recyclerView);

        //Actions to occur when user submits new task
        etTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Actions to take when creating new task
                if (actionId == EditorInfo.IME_ACTION_DONE && taskBeingEdited == null) {

                    if (!boolMute) {
                        mpBlip.start();
                    }

                    vibrate.vibrate(50);

                    //Text box and keyboard disappear
                    etTask.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //Getting user data
                    String taskName = etTask.getText().toString();

                    //Clear text from text box
                    etTask.setText("");

                    if (!taskName.equals("")) {

                        Calendar calendar = new GregorianCalendar().getInstance();
                        mainActivityPresenter.addTask(0, taskName, calendar.getTimeInMillis());

                        if (intRenameHint <= 2) {
                            if (intRenameHint == 2) {
                                toast.setText(R.string.longClickToRename);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        mpHint.start();
                                        toastView.startAnimation(AnimationUtils
                                                .loadAnimation(MainActivity.this,
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
                                        handler2.postDelayed(runnable2, 2500);
                                    }
                                };

                                handler.postDelayed(runnable, 500);
                            } else {
                                if (boolShowMotivation) {
                                    //showing motivational toast
                                    int i = random.nextInt(7);
                                    while (strMotivation[i].equals(strLastToast)) {
                                        i = random.nextInt(7);
                                    }
                                    strLastToast = strMotivation[i];
                                    toast.setText(strMotivation[i]);
                                    final Handler handler = new Handler();

                                    final Runnable runnable = new Runnable() {
                                        public void run() {
                                            if (!boolMute) {
                                                mpSweep.start();
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
                            intRenameHint++;
                            preferences.edit().putInt(StringConstants.RENAME_HINT_KEY, intRenameHint).apply();
                        } else {

                            if (boolShowMotivation) {
                                //showing motivational toast
                                int i = random.nextInt(7);
                                while (strMotivation[i].equals(strLastToast)) {
                                    i = random.nextInt(7);
                                }
                                strLastToast = strMotivation[i];
                                toast.setText(strMotivation[i]);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        if (!boolMute) {
                                            mpSweep.start();
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

                    return true;

                    //Actions to take when editing existing task
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {

                    Toast.makeText(MainActivity.this, "Editing task", Toast.LENGTH_SHORT).show();

                    if (!boolMute) {
                        mpBlip.start();
                    }

                    vibrate.vibrate(50);

                    etTask.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //Getting user data
                    String editedTaskString = etTask.getText().toString();

                    etTask.setText("");

                    if (!editedTaskString.equals("")) {

                        mainActivityPresenter.setTask(taskBeingEdited, editedTaskString);

                    }

                    taskBeingEdited = null;

                    return true;

                }

                return false;

            }

        });

    }

    private void showBannerAd() {
        boolean networkAvailable = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            networkAvailable = true;
        }

        if (networkAvailable) {
            adView.setVisibility(View.VISIBLE);
            final AdRequest banRequest = new AdRequest.Builder().build();
            adView.loadAd(banRequest);
        } else {
            imgBanner.setVisibility(View.VISIBLE);
        }

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                imgBanner.setVisibility(View.VISIBLE);
            }

        });
    }

    //Actions to occur when fab clicked
    public void addTask(Task task) {

        //track the task which is being edited. This is null when creating a new task
        taskBeingEdited = task;

        vibrate.vibrate(50);

        //Show keyboard
        keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        //Set return button to 'Done'
        etTask.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //Check if editing existing task or adding new one
        if (taskBeingEdited != null) {
            //put task name in the edit text
            etTask.setText(taskBeingEdited.getTask());
        } else {
            //Ensure that there is no previous text in the text box
            etTask.setText("");
        }

        //Actions to occur when keyboard is showing
        checkKeyboardShowing();

    }

    @Override
    public void showPurchases() {
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
                if (!boolRemindersAvailable && !boolAdsRemoved) {

                    vibrate.vibrate(50);

                    if (!boolMute) {
                        mpChime.start();
                    }

                    billingProcessor.purchase(MainActivity.this, StringConstants.TEST_PURCHASE);

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
    public void toggleFab(boolean showFab) {
        if(showFab){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }
    }

    //Give user option to undo deletion of task
    private void showSnackbar(String stringSnack, final Task taskToReinstate) {
        View view = findViewById(R.id.activityRoot);
        Snackbar.make(view, stringSnack, Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                        scheduler.cancel(StringConstants.DELETE_TASK_ID);
                        mainActivityPresenter.reinstateTask(taskToReinstate);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.purple))
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            miPro = this.toolbarLight.getMenu().findItem(R.id.buy);
            MenuItem miMotivation = this.toolbarLight.getMenu().findItem(R.id.motivation);
            //Action bar options
            MenuItem miMute = this.toolbarLight.getMenu().findItem(R.id.mute);
            if (boolShowMotivation) {
                miMotivation.setChecked(true);
            }
            if (!boolMute) {
                miMute.setChecked(true);
            }
            if (boolAdsRemoved && boolRemindersAvailable) {
                miPro.setVisible(false);
            }
            return true;
        } else {
            miPro.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        vibrate.vibrate(50);

        //Actions to occur if user selects 'mute'
        if (id == R.id.mute) {

            if (boolMute) {
                boolMute = false;
                item.setChecked(true);
                preferences.edit().putBoolean(StringConstants.MUTE_KEY, false).apply();
            } else {
                boolMute = true;
                item.setChecked(false);
                preferences.edit().putBoolean(StringConstants.MUTE_KEY, true).apply();
            }

            return true;

            //Actions to occur if user selects the pro icon
        } else if (id == R.id.buy) {

            showPurchases();

            return true;

        }

        //Actions to occur if user selects 'motivation'
        else if (id == R.id.motivation) {

            if (boolShowMotivation) {
                boolShowMotivation = false;
                item.setChecked(false);
                preferences.edit().putBoolean(StringConstants.MOTIVATION_KEY, false).apply();
            } else {
                boolShowMotivation = true;
                item.setChecked(true);
                preferences.edit().putBoolean(StringConstants.MOTIVATION_KEY, false).apply();
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //Create prompt that enables user to upgrade to pro
//    private void purchasePrompt() {
//
//        final Dialog dialog = new Dialog(MainActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//
//        dialog.setContentView(R.layout.purchases);
//
//        Button positive = dialog.findViewById(R.id.positive);
//        Button negative = dialog.findViewById(R.id.negative);
//
//        //Buy button actions
//        positive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//                if (!boolRemindersAvailable && !boolAdsRemoved) {
//
//                    vibrate.vibrate(50);
//
//                    if (!boolMute) {
//                        mpChime.start();
//                    }
//
//                    billingProcessor.purchase(MainActivity.this, StringConstants.TEST_PURCHASE);
//
//                }
//
//            }
//        });
//
//        //Cancel button options
//        negative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//
//            }
//        });
//
//        dialog.show();
//
//    }

    //reinstates completed task
//    public void reinstate(int i) {
//
//        if (boolShowMotivation) {
//            toast.setText(R.string.taskReinstated);
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//
//                    if (!boolMute) {
//                        mpSweep.start();
//                    }
//
//                    toastView.startAnimation(AnimationUtils.loadAnimation
//                            (MainActivity.this, R.anim.enter_from_right_fast));
//                    toastView.setVisibility(View.VISIBLE);
//                    final Handler handler2 = new Handler();
//                    final Runnable runnable2 = new Runnable() {
//                        public void run() {
//                            toastView.startAnimation(AnimationUtils.loadAnimation
//                                    (MainActivity.this, android.R.anim.fade_out));
//                            toastView.setVisibility(View.GONE);
//                        }
//                    };
//                    handler2.postDelayed(runnable2, 1500);
//                }
//            };
//
//            handler.postDelayed(runnable, 500);
//        }
//
//    }

    //Tells user to add tasks when task list is empty
//    private void noTasksLeft() {

    //Checks if there are any existing tasks
//        if (taskList.size() == 0) {
//
//            //Inform user to add some tasks
//            imgNoTasks.setVisibility(View.VISIBLE);
//        } else {
//            imgNoTasks.setVisibility(View.GONE);
//        }

//    }

    //Actions to occur when keyboard is showing
    void checkKeyboardShowing() {

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener
                (new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect screen = new Rect();

                        activityRootView.getWindowVisibleDisplayFrame(screen);

                        if (screen.bottom != deviceheight) {

                            etTask.setFocusable(true);

                            etTask.requestFocus();

                            //Keyboard is inactive without this line
                            etTask.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                            //Textbox is visible and 'add' button is gone
                            // whenever keyboard is showing
                            etTask.setVisibility(View.VISIBLE);

                            //remove fab when keybaord is up
                            fab.setVisibility(View.GONE);

                        } else /*if (restoreNormalListView)*/ {

                            //Textbox is gone and 'add' button is visible whenever
                            //keyboard is not showing
                            etTask.setVisibility(View.GONE);

                            //fab must be visible when keyboard is down
                            fab.setVisibility(View.VISIBLE);

                        }

                    }

                });

    }

    public void showPro(View view) {

        showPurchases();

    }

    private void showPrompt() {
        if (mainActivityPresenter.showReviewPrompt(intShowReviewPrompt, lngTimeInstalled)) {
            preferences.edit().putInt(StringConstants.SHOW_REVIEW_KEY, ++intShowReviewPrompt).apply();
            prompt();
        }
    }

    private void prompt() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.review_dialog_light);

        Button positive = dialog.findViewById(R.id.positive);
        Button negative = dialog.findViewById(R.id.negative);

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //show review prompt no more than four times. Setting times
                //shown to five means it'll no long be shown
                intShowReviewPrompt = 5;
                preferences.edit().putInt(StringConstants.SHOW_REVIEW_KEY, intShowReviewPrompt).apply();
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

            }
        });

        dialog.show();

    }

    @Override
    public void onProductPurchased(@NonNull String productId,
                                   @Nullable TransactionDetails details) {

        //Inform user of successful purchase
        if (productId.equals(StringConstants.TEST_PURCHASE)) {

            toast.setText(R.string.thank_you_for_purchase);
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    if (!boolMute) {
                        mpSweep.start();
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

            boolAdsRemoved = true;
            preferences.edit().putBoolean(StringConstants.ADS_REMOVED_KEY, true).apply();
            boolRemindersAvailable = true;
            preferences.edit().putBoolean(StringConstants.REMINDERS_AVAILABLE_KEY, true).apply();
            miPro.setVisible(false);
            imgBanner.setVisibility(View.GONE);

        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {


        Log.d(TAG, "error in purchasing...");


        //try purchasing again in case error was a once off
//        if (tryAgain) {
//            tryAgain = false;
//        billingProcessor.purchase(MainActivity.this, "unlock_all");
        //inform user of error
//        } else {
//            Toast.makeText(MainActivity.this, R.string.somethingWentWrong,
//                    Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    public void onBillingInitialized() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPrompt();

        adapter.notifyItemChanged(preferences.getInt("refresh_this_item", 0));

    }

}