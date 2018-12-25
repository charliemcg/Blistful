package com.violenthoboenterprises.blistful;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.violenthoboenterprises.blistful.model.Reminder;
import com.violenthoboenterprises.blistful.model.ReminderPresenterImpl;
import com.violenthoboenterprises.blistful.model.ReminderViewModel;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.ReminderPresenter;
import com.violenthoboenterprises.blistful.view.ReminderView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class ReminderActivity extends MainActivity implements ReminderView {

    private final String TAG = this.getClass().getSimpleName();
    private Toolbar dueToolbar;
    private ImageView imgTime, imgTimeFaded, imgCalendar, imgCalendarFaded;
    private ImageView imgDailyFaded, imgWeeklyFaded, imgMonthlyFaded, imgCancelRepeatFaded,
            imgDaily, imgWeekly, imgMonthly, imgCancelRepeat;
    private TextView tvDate, tvTime;
    private static MenuItem killReminder, killReminderOpen;
    private static int screenSize;
    private static ReminderPresenter reminderPresenter;
    private static Task task;
    private static Reminder reminder;
    private String REPEAT_DAY = "day";
    private String REPEAT_WEEK = "week";
    private String REPEAT_MONTH = "month";
    private SharedPreferences preferences;
    private static int tempMinute;
    private static int tempHour;
    private static int tempDay;
    private static int tempMonth;
    private static int tempYear;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        dueToolbar = findViewById(R.id.tbDue);
        setSupportActionBar(dueToolbar);

        //getting the task to which this note is related
        task = (Task) getIntent().getSerializableExtra("task");
        ReminderViewModel reminderViewModel =
                ViewModelProviders.of(this).get(ReminderViewModel.class);
        //getting the instance of the reminder
        reminder = reminderViewModel.getReminderByParent(task.getId());
        TaskViewModel taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        reminderPresenter = new ReminderPresenterImpl(ReminderActivity.this,
                taskViewModel, reminderViewModel, task, reminder, getApplicationContext());

        imgTime = findViewById(R.id.imgTime);
        imgTimeFaded = findViewById(R.id.imgTimeFaded);
        imgCalendar = findViewById(R.id.imgCalendar);
        imgCalendarFaded = findViewById(R.id.imgCalendarFaded);
        View btnDate = findViewById(R.id.btnDate);
        View btnTime = findViewById(R.id.btnTime);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        imgDailyFaded = findViewById(R.id.imgDailyRepeatFaded);
        imgDaily = findViewById(R.id.imgDailyRepeat);
        imgWeeklyFaded = findViewById(R.id.imgWeeklyRepeatFaded);
        imgWeekly = findViewById(R.id.imgWeeklyRepeat);
        imgMonthlyFaded = findViewById(R.id.imgMonthlyRepeatFaded);
        imgMonthly = findViewById(R.id.imgMonthlyRepeat);
        imgCancelRepeatFaded = findViewById(R.id.imgCancelRepeatFaded);
        imgCancelRepeat = findViewById(R.id.imgCancelRepeat);
        preferences = this.getSharedPreferences("com.violenthoboenterprises.blistful",
                Context.MODE_PRIVATE);

        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        onCreateOptionsMenu(dueToolbar.getMenu());
        tempMinute = -1;
        tempHour = -1;
        tempDay = -1;
        tempMonth = -1;
        tempYear = -1;

        //Inform user that they can set an alarm
        if (reminderPresenter.getYear() == 0) {
            tvDate.setText(R.string.addDate);
        }
        if (reminderPresenter.getHour() == 0) {
            tvTime.setText(R.string.addTime);
        }

        if (reminderPresenter.getYear() != 0) {
            imgCalendarFaded.setVisibility(View.INVISIBLE);
            imgCalendar.setVisibility(View.VISIBLE);

            tvDate.setText(reminderPresenter.getFormattedDate());

            imgTimeFaded.setVisibility(View.INVISIBLE);
            imgTime.setVisibility(View.VISIBLE);

            tvTime.setText(reminderPresenter.getFormattedTime());

            if (screenSize == 3) {
                tvDate.setTextSize(65);
                tvTime.setTextSize(65);
            } else if (screenSize == 4) {
                tvDate.setTextSize(85);
                tvTime.setTextSize(85);
            } else {
                tvDate.setTextSize(25);
                tvTime.setTextSize(25);
            }

        }

        //Highlight the repeat type or highlight "No Repeat" if none exists
        if (reminderPresenter.getRepeatInterval() == null) {
            imgCancelRepeat.setVisibility(View.INVISIBLE);
            imgCancelRepeat.setVisibility(View.VISIBLE);
        } else if (reminderPresenter.getRepeatInterval().equals(REPEAT_DAY)) {
            imgDailyFaded.setVisibility(View.INVISIBLE);
            imgDaily.setVisibility(View.VISIBLE);
        } else if (reminderPresenter.getRepeatInterval().equals(REPEAT_WEEK)) {
            imgWeeklyFaded.setVisibility(View.INVISIBLE);
            imgWeekly.setVisibility(View.VISIBLE);
        } else if (reminderPresenter.getRepeatInterval().equals(REPEAT_MONTH)) {
            imgMonthlyFaded.setVisibility(View.INVISIBLE);
            imgMonthly.setVisibility(View.VISIBLE);
        }

        //Actions to occur when user selects to set/change date
        btnDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new DatePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Date");

            }

        });

        //Actions to occur when user selects to set/change time
        btnTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new TimePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Time");

            }

        });

        //Actions to occur when user chooses to cancel the repeat
        imgCancelRepeatFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && reminderPresenter.getRepeatInterval() == null) {
                    mpBlip.start();
                }

                vibrate.vibrate(50);

                imgCancelRepeatFaded.setVisibility(View.INVISIBLE);
                imgCancelRepeat.setVisibility(View.VISIBLE);
                imgDailyFaded.setVisibility(View.VISIBLE);
                imgDaily.setVisibility(View.INVISIBLE);
                imgWeeklyFaded.setVisibility(View.VISIBLE);
                imgWeekly.setVisibility(View.INVISIBLE);
                imgMonthlyFaded.setVisibility(View.VISIBLE);
                imgMonthly.setVisibility(View.INVISIBLE);

                if (reminderPresenter.getYear() == 0 && reminderPresenter.getHour() == 0) {

                    killReminder.setVisible(false);

                }

                reminderPresenter.setRepeatInterval(null);

            }

        });

        //Actions to occur if user selects to set a daily recurring alarm
        imgDailyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && reminderPresenter.getRepeatInterval() != null) {
                    if (!reminderPresenter.getRepeatInterval().equals(REPEAT_DAY)) {
                        mpBlip.start();
                    }
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                imgCancelRepeatFaded.setVisibility(View.VISIBLE);
                imgCancelRepeat.setVisibility(View.INVISIBLE);
                imgDailyFaded.setVisibility(View.INVISIBLE);
                imgDaily.setVisibility(View.VISIBLE);
                imgWeeklyFaded.setVisibility(View.VISIBLE);
                imgWeekly.setVisibility(View.INVISIBLE);
                imgMonthlyFaded.setVisibility(View.VISIBLE);
                imgMonthly.setVisibility(View.INVISIBLE);

                killReminder.setVisible(true);

                reminderPresenter.setRepeatInterval(REPEAT_DAY);

            }

        });

        //Actions to occur if user selects to set a weekly recurring alarm
        imgWeeklyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && reminderPresenter.getRepeatInterval() != null) {
                    if (!reminderPresenter.getRepeatInterval().equals(REPEAT_WEEK)) {
                        mpBlip.start();
                    }
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                imgCancelRepeatFaded.setVisibility(View.VISIBLE);
                imgCancelRepeat.setVisibility(View.INVISIBLE);
                imgDailyFaded.setVisibility(View.VISIBLE);
                imgDaily.setVisibility(View.INVISIBLE);
                imgWeeklyFaded.setVisibility(View.INVISIBLE);
                imgWeekly.setVisibility(View.VISIBLE);
                imgMonthlyFaded.setVisibility(View.VISIBLE);
                imgMonthly.setVisibility(View.INVISIBLE);

                killReminder.setVisible(true);

                reminderPresenter.setRepeatInterval(REPEAT_WEEK);

            }

        });

        //Actions to occur if user selects to set a monthly  recurring alarm
        imgMonthlyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && reminderPresenter.getRepeatInterval() != null) {
                    if (!reminderPresenter.getRepeatInterval().equals(REPEAT_MONTH)) {
                        mpBlip.start();
                    }
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                imgCancelRepeatFaded.setVisibility(View.VISIBLE);
                imgCancelRepeat.setVisibility(View.INVISIBLE);
                imgDailyFaded.setVisibility(View.VISIBLE);
                imgDaily.setVisibility(View.INVISIBLE);
                imgWeeklyFaded.setVisibility(View.VISIBLE);
                imgWeekly.setVisibility(View.INVISIBLE);
                imgMonthlyFaded.setVisibility(View.INVISIBLE);
                imgMonthly.setVisibility(View.VISIBLE);

                killReminder.setVisible(true);

                reminderPresenter.setRepeatInterval(REPEAT_MONTH);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_reminder, dueToolbar.getMenu());
            killReminder = this.dueToolbar.getMenu().findItem(R.id.itemTrashReminder);
            killReminderOpen = this.dueToolbar.getMenu().findItem(R.id.itemTrashReminderOpen);
            this.dueToolbar.setTitle(R.string.setDateTime);
            this.dueToolbar.setSubtitle(reminderPresenter.getTask());
            //if reminder already exists then delete icon should be present
            if (reminderPresenter.getYear() == 0) {
                killReminder.setVisible(false);
            } else {
                killReminder.setVisible(true);
            }
            return true;
        } else {
            killReminder.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Resetting alarm to off
        if (id == R.id.itemTrashReminder) {

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    killReminder.setVisible(false);
                    killReminderOpen.setVisible(true);

                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                        public void run() {
                            killReminder.setVisible(true);
                            killReminderOpen.setVisible(false);
                            final Handler handler3 = new Handler();
                            final Runnable runnable3 = new Runnable() {
                                @Override
                                public void run() {

                                    killReminder.setVisible(false);

                                    vibrate.vibrate(50);

                                    if (!boolMute) {
                                        mpTrash.start();
                                    }

                                    reminderPresenter.setRepeatInterval(null);
                                    reminderPresenter.setTimestamp(reminderPresenter.getTimeCreated());
//                                    reminderPresenter.setDue(false);

                                    reminderPresenter.setYear(0);
                                    reminderPresenter.setMonth(0);
                                    reminderPresenter.setDay(0);
                                    reminderPresenter.setHour(0);
                                    reminderPresenter.setMinute(0);

                                    imgCalendarFaded.setVisibility(View.VISIBLE);
                                    imgCalendar.setVisibility(View.INVISIBLE);
                                    imgTimeFaded.setVisibility(View.VISIBLE);
                                    imgTime.setVisibility(View.INVISIBLE);

                                    tvDate.setText(R.string.addDate);
                                    tvTime.setText(R.string.addTime);
                                    if (screenSize == 3) {
                                        tvDate.setTextSize(25);
                                        tvTime.setTextSize(25);
                                    } else if (screenSize == 4) {
                                        tvDate.setTextSize(35);
                                        tvTime.setTextSize(35);
                                    } else {
                                        tvDate.setTextSize(15);
                                        tvTime.setTextSize(15);
                                    }

                                    imgCancelRepeat.setVisibility(View.INVISIBLE);
                                    imgCancelRepeat.setVisibility(View.VISIBLE);
                                    imgDailyFaded.setVisibility(View.VISIBLE);
                                    imgDaily.setVisibility(View.INVISIBLE);
                                    imgWeeklyFaded.setVisibility(View.VISIBLE);
                                    imgWeekly.setVisibility(View.INVISIBLE);
                                    imgMonthlyFaded.setVisibility(View.VISIBLE);
                                    imgMonthly.setVisibility(View.INVISIBLE);

                                    tvDate.setText(R.string.addDate);
                                    tvTime.setText(R.string.addTime);

                                    int duesset = preferences.getInt(StringConstants.DUES_SET, 0);
                                    preferences.edit().putInt(StringConstants.DUES_SET, --duesset).apply();

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

    public static class DatePickerDialogFrag extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            int year;
            int month;
            int day;

            DatePickerDialog datePickerDialog;

//            year = calendar.get(Calendar.YEAR);
//            month = calendar.get(Calendar.MONTH);
//            day = calendar.get(Calendar.DAY_OF_MONTH);

            if(task.getTimestamp() != 0){
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(task.getTimestamp());
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }else if(tempDay != -1) {
                year = tempYear;
                month = tempMonth;
                day = tempDay;
            }else{
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }

            //Initialise date picker
            datePickerDialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);

            //Make so all previous dates are inactive.
            //User shouldn't be able to set due date to in the past
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            if (!boolMute) {
                mpBlip.start();
            }

            TextView tvDate = getActivity().findViewById(R.id.tvDate);

            vibrate.vibrate(50);

            ImageView calendarFadedLight = getActivity().findViewById(R.id.imgCalendarFaded);
            calendarFadedLight.setVisibility(View.INVISIBLE);

            ImageView calendar = getActivity().findViewById(R.id.imgCalendar);
            calendar.setVisibility(View.VISIBLE);

            if (screenSize == 3) {
                tvDate.setTextSize(65);
            } else if (screenSize == 4) {
                tvDate.setTextSize(85);
            } else {
                tvDate.setTextSize(25);
            }

            killReminder.setVisible(true);

            reminderPresenter.setYear(year);
            reminderPresenter.setMonth(month);
            reminderPresenter.setDay(day);

            tempDay = day;
            tempMonth = month;
            tempYear = year;

            tvDate.setText(reminderPresenter.getFormattedDate());

        }

    }

    public static class TimePickerDialogFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            int hour;
            int minute;

            int defaultTimePickerHour;

            if(task.getTimestamp() != 0){
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(task.getTimestamp());
                minute = cal.get(Calendar.MINUTE);
                defaultTimePickerHour = cal.get(Calendar.HOUR_OF_DAY);
            }else if(tempMinute != -1) {
                minute = tempMinute;
                defaultTimePickerHour = tempHour;
            }else{
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                defaultTimePickerHour = hour;
            }

            TimePickerDialog timePickerDialog;

            timePickerDialog = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    this, defaultTimePickerHour, minute, false);


            return timePickerDialog;

        }

        public void onTimeSet(TimePicker view, int hour, int minute) {

            TextView timeTextView = getActivity().findViewById(R.id.tvTime);

            if (!boolMute) {
                mpBlip.start();
            }

            MainActivity.vibrate.vibrate(50);

            ImageView timeFadedLight = getActivity().findViewById(R.id.imgTimeFaded);
            timeFadedLight.setVisibility(View.INVISIBLE);

            ImageView time = getActivity().findViewById(R.id.time);
            time.setVisibility(View.VISIBLE);

            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            if (screenSize == 3) {
                timeTextView.setTextSize(65);
            } else if (screenSize == 4) {
                timeTextView.setTextSize(85);
            } else {
                timeTextView.setTextSize(25);
            }

            killReminder.setVisible(true);

            reminderPresenter.setHour(hour);
            reminderPresenter.setMinute(minute);

            tempMinute = minute;
            tempHour = hour;

            timeTextView.setText(reminderPresenter.getFormattedTime());

        }

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        //Timestamp needs to be saved if user has set a reminder
        if (reminderPresenter.getYear() != 0 || reminderPresenter.getHour() != 0 ||
                reminderPresenter.getRepeatInterval() != null) {

            Calendar calendar = Calendar.getInstance();
            //set current date if date wasn't picked
            if (reminderPresenter.getYear() == 0) {
                reminderPresenter.setYear(calendar.get(Calendar.YEAR));
                reminderPresenter.setMonth(calendar.get(Calendar.MONTH));
                reminderPresenter.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            }
            //set current time if time wasn't picked
            if (reminderPresenter.getHour() == 0) {
                reminderPresenter.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                reminderPresenter.setMinute(calendar.get(Calendar.MINUTE));
            }
            //Setting timestamp of the reminder
            calendar.set(Calendar.YEAR, reminderPresenter.getYear());
            calendar.set(Calendar.MONTH, reminderPresenter.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, reminderPresenter.getDay());
            calendar.set(Calendar.HOUR_OF_DAY, reminderPresenter.getHour());
            calendar.set(Calendar.MINUTE, reminderPresenter.getMinute());
            //Updating the task
            reminderPresenter.setTimestamp(calendar.getTimeInMillis());

            if(boolRemindersAvailable) {
                scheduleNotification();
            }

//            reminderPresenter.setDue(true);

            int duesset = preferences.getInt(StringConstants.DUES_SET, 0);
            preferences.edit().putInt(StringConstants.DUES_SET, ++duesset).apply();

        }

        super.onBackPressed();

    }

    private void scheduleNotification() {

        //TODO see if all these variables really need to be initialised in MainActivity
        MainActivity.alertIntent = new Intent(getApplicationContext(), AlertReceiver.class);
        MainActivity.alertIntent.putExtra
                ("snoozeStatus", false);
        MainActivity.alertIntent.putExtra("task", task);
        List<Integer> timestamps = taskViewModel.getAllTimestamps();//TODO make sure to get data a due time and no sooner
        MainActivity.alertIntent.putExtra("timestamps", (Serializable) timestamps);

        //Setting alarm
        MainActivity.pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), task.getId(), MainActivity.alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        MainActivity.alarmManager.cancel(MainActivity.pendingIntent);

        MainActivity.alarmManager.set(AlarmManager.RTC,
                reminderPresenter.getTimestamp(),
                MainActivity.pendingIntent);

    }

}
