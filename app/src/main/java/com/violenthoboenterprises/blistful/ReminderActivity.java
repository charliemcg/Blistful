package com.violenthoboenterprises.blistful;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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
    private Task task;
    private static Reminder reminder;
    private String REPEAT_DAY = "day";
    private String REPEAT_WEEK = "week";
    private String REPEAT_MONTH = "month";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_picker);

        dueToolbar = findViewById(R.id.dueToolbar);
        setSupportActionBar(dueToolbar);

        ReminderViewModel reminderViewModel =
                ViewModelProviders.of(this).get(ReminderViewModel.class);
        TaskViewModel taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        reminderPresenter = new ReminderPresenterImpl(ReminderActivity.this,
                taskViewModel, reminderViewModel, getApplicationContext());
        //getting the task to which this note is related
        task = (Task) getIntent().getSerializableExtra("task");
        //getting the instance of the reminder
        reminder = reminderViewModel.getReminderByParent(task.getId());

        //Create a reminder instance if there isn't one already
        if (reminder == null) {
            reminderPresenter.addReminder(task.getId());
            reminder = reminderViewModel.getReminderByParent(task.getId());
        }

        imgTime = findViewById(R.id.time);
        imgTimeFaded = findViewById(R.id.timeFadedLight);
        imgCalendar = findViewById(R.id.calendar);
        imgCalendarFaded = findViewById(R.id.calendarFadedLight);
        View btnDate = findViewById(R.id.dateBtn);
        View btnTime = findViewById(R.id.timeBtn);
        tvDate = findViewById(R.id.dateTextView);
        tvTime = findViewById(R.id.timeTextView);
        imgDailyFaded = findViewById(R.id.dailyLight);
        imgDaily = findViewById(R.id.daily);
        imgWeeklyFaded = findViewById(R.id.weeklyLight);
        imgWeekly = findViewById(R.id.weekly);
        imgMonthlyFaded = findViewById(R.id.monthlyLight);
        imgMonthly = findViewById(R.id.monthly);
        imgCancelRepeatFaded = findViewById(R.id.cancelRepeatLight);
        imgCancelRepeat = findViewById(R.id.cancelRepeat);

        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        onCreateOptionsMenu(dueToolbar.getMenu());

        //Inform user that they can set an alarm
        if (reminder.getYear() == 0) {
            tvDate.setText(R.string.addDate);
        }
        if (reminder.getHour() == 0) {
            tvTime.setText(R.string.addTime);
        }

        String formattedMonth = "";

        if (reminder.getYear() != 0) {
            imgCalendarFaded.setVisibility(View.INVISIBLE);
            imgCalendar.setVisibility(View.VISIBLE);

            int intMonth = reminder.getMonth() + 1;
            if (intMonth == 1) {
                formattedMonth = getString(R.string.jan);
            } else if (intMonth == 2) {
                formattedMonth = getString(R.string.feb);
            } else if (intMonth == 3) {
                formattedMonth = getString(R.string.mar);
            } else if (intMonth == 4) {
                formattedMonth = getString(R.string.apr);
            } else if (intMonth == 5) {
                formattedMonth = getString(R.string.may);
            } else if (intMonth == 6) {
                formattedMonth = getString(R.string.jun);
            } else if (intMonth == 7) {
                formattedMonth = getString(R.string.jul);
            } else if (intMonth == 8) {
                formattedMonth = getString(R.string.aug);
            } else if (intMonth == 9) {
                formattedMonth = getString(R.string.sep);
            } else if (intMonth == 10) {
                formattedMonth = getString(R.string.oct);
            } else if (intMonth == 11) {
                formattedMonth = getString(R.string.nov);
            } else if (intMonth == 12) {
                formattedMonth = getString(R.string.dec);
            }

            String lang = String.valueOf(Locale.getDefault());
            String formattedDate;
            if (lang.equals("en_AS") || lang.equals("en_BM")
                    || lang.equals("en_CA") || lang.equals("en_GU")
                    || lang.equals("en_PH")
                    || lang.equals("en_PR") || lang.equals("en_UM")
                    || lang.equals("en_US") || lang.equals("en_VI")) {
                formattedDate = formattedMonth + " " + reminder.getDay();
                tvDate.setText(formattedDate);
            } else {
                formattedDate = reminder.getDay() + " " + formattedMonth;
                tvDate.setText(formattedDate);
            }

            imgTimeFaded.setVisibility(View.INVISIBLE);
            imgTime.setVisibility(View.VISIBLE);

            String formattedTime = adjustTime();
            tvTime.setText(formattedTime);

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

        dueToolbar.setSubtitleTextColor(Color.parseColor("#666666"));
        tvDate.setTextColor(Color.parseColor("#000000"));
        tvTime.setTextColor(Color.parseColor("#000000"));

        //Highlight the repeat type or highlight "No Repeat" if none exists
        if (task.getRepeatInterval() == null) {
            imgCancelRepeat.setVisibility(View.INVISIBLE);
            imgCancelRepeat.setVisibility(View.VISIBLE);
        } else if (task.getRepeatInterval().equals(REPEAT_DAY)) {
            imgDailyFaded.setVisibility(View.INVISIBLE);
            imgDaily.setVisibility(View.VISIBLE);
        } else if (task.getRepeatInterval().equals(REPEAT_WEEK)) {
            imgWeeklyFaded.setVisibility(View.INVISIBLE);
            imgWeekly.setVisibility(View.VISIBLE);
        } else if (task.getRepeatInterval().equals(REPEAT_MONTH)) {
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

                if (!boolMute && task.getRepeatInterval() == null) {
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

                if (reminder.getYear() == 0 && reminder.getHour() == 0) {

                    killReminder.setVisible(false);

                }

                task.setRepeatInterval(null);
                reminderPresenter.update(task);

            }

        });

        //Actions to occur if user selects to set a daily recurring alarm
        imgDailyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && task.getRepeatInterval() != null) {
                    if (!task.getRepeatInterval().equals(REPEAT_DAY)) {
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

                task.setRepeatInterval(REPEAT_DAY);
                reminderPresenter.update(task);

            }

        });

        //Actions to occur if user selects to set a weekly recurring alarm
        imgWeeklyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && task.getRepeatInterval() != null) {
                    if (!task.getRepeatInterval().equals(REPEAT_WEEK)) {
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

                task.setRepeatInterval(REPEAT_WEEK);
                reminderPresenter.update(task);

            }

        });

        //Actions to occur if user selects to set a monthly  recurring alarm
        imgMonthlyFaded.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!boolMute && task.getRepeatInterval() != null) {
                    if (!task.getRepeatInterval().equals(REPEAT_MONTH)) {
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

                task.setRepeatInterval(REPEAT_MONTH);
                reminderPresenter.update(task);

            }

        });

    }

    //Formatting the time into an easy to read string
    private static String adjustTime() {
        String adjustedAmPm;
        int adjustedHour = reminder.getHour();
        int adjustedMinute = reminder.getMinute();
        String adjustedMinuteString;

        if (adjustedHour < 12) {
            adjustedAmPm = "am";
        } else {
            adjustedAmPm = "pm";
        }

        if (adjustedHour == 0) {
            adjustedHour = 12;
        } else if (adjustedHour > 12) {
            adjustedHour -= 12;
        }

        if (adjustedMinute < 10) {
            adjustedMinuteString = "0" + adjustedMinute;
        } else {
            adjustedMinuteString = String.valueOf(adjustedMinute);
        }

        return adjustedHour + ":" + adjustedMinuteString + adjustedAmPm;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_alarm, dueToolbar.getMenu());
            killReminder = this.dueToolbar.getMenu().findItem(R.id.killAlarmItem);
            killReminderOpen = this.dueToolbar.getMenu().findItem(R.id.trashAlarmOpen);
            this.dueToolbar.setTitle(R.string.setDateTime);
            this.dueToolbar.setSubtitle(task.getTask());
            //if reminder already exists then delete icon should be present
            if (reminder.getYear() == 0) {
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
        if (id == R.id.killAlarmItem) {

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

                                    task.setRepeatInterval(null);
                                    task.setTimestamp(0);
                                    reminderPresenter.update(task);

                                    reminder.setYear(0);
                                    reminder.setMonth(0);
                                    reminder.setDay(0);
                                    reminder.setHour(0);
                                    reminder.setMinute(0);
                                    reminderPresenter.updateReminder(reminder);

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

            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

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

            TextView tvDate = getActivity().findViewById(R.id.dateTextView);

            //Format and display chosen date
            String formattedMonth = "";

            int intMonth = month + 1;
            if (intMonth == 1) {
                formattedMonth = getString(R.string.jan);
            } else if (intMonth == 2) {
                formattedMonth = getString(R.string.feb);
            } else if (intMonth == 3) {
                formattedMonth = getString(R.string.mar);
            } else if (intMonth == 4) {
                formattedMonth = getString(R.string.apr);
            } else if (intMonth == 5) {
                formattedMonth = getString(R.string.may);
            } else if (intMonth == 6) {
                formattedMonth = getString(R.string.jun);
            } else if (intMonth == 7) {
                formattedMonth = getString(R.string.jul);
            } else if (intMonth == 8) {
                formattedMonth = getString(R.string.aug);
            } else if (intMonth == 9) {
                formattedMonth = getString(R.string.sep);
            } else if (intMonth == 10) {
                formattedMonth = getString(R.string.oct);
            } else if (intMonth == 11) {
                formattedMonth = getString(R.string.nov);
            } else if (intMonth == 12) {
                formattedMonth = getString(R.string.dec);
            }

            String lang = String.valueOf(Locale.getDefault());
            String formattedDate;
            if (lang.equals("en_AS") || lang.equals("en_BM")
                    || lang.equals("en_CA") || lang.equals("en_GU")
                    || lang.equals("en_PH")
                    || lang.equals("en_PR") || lang.equals("en_UM")
                    || lang.equals("en_US") || lang.equals("en_VI")) {
                formattedDate = formattedMonth + " " + day;
                tvDate.setText(formattedDate);
            } else {
                formattedDate = day + " " + formattedMonth;
                tvDate.setText(formattedDate);
            }

            vibrate.vibrate(50);

            ImageView calendarFadedLight = getActivity().findViewById(R.id.calendarFadedLight);
            calendarFadedLight.setVisibility(View.INVISIBLE);

            ImageView calendar = getActivity().findViewById(R.id.calendar);
            calendar.setVisibility(View.VISIBLE);

            if (screenSize == 3) {
                tvDate.setTextSize(65);
            } else if (screenSize == 4) {
                tvDate.setTextSize(85);
            } else {
                tvDate.setTextSize(25);
            }

            killReminder.setVisible(true);

            reminder.setYear(year);
            reminder.setMonth(month);
            reminder.setDay(day);
            reminderPresenter.updateReminder(reminder);

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

            minute = calendar.get(Calendar.MINUTE);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            defaultTimePickerHour = hour;

            TimePickerDialog timePickerDialog;

            timePickerDialog = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                    this, defaultTimePickerHour, minute, false);


            return timePickerDialog;

        }

        public void onTimeSet(TimePicker view, int hour, int minute) {

            TextView timeTextView = getActivity().findViewById(R.id.timeTextView);

            if (!boolMute) {
                mpBlip.start();
            }

            MainActivity.vibrate.vibrate(50);

            ImageView timeFadedLight = getActivity().findViewById(R.id.timeFadedLight);
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

            reminder.setHour(hour);
            reminder.setMinute(minute);
            reminderPresenter.updateReminder(reminder);

            String formattedTime = adjustTime();
            timeTextView.setText(formattedTime);

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
        if (reminder.getYear() != 0 || reminder.getHour() != 0 ||
                task.getRepeatInterval() != null) {

            Calendar calendar = new GregorianCalendar();
            //set current date if date wasn't picked
            if (reminder.getYear() == 0) {
                reminder.setYear(calendar.get(Calendar.YEAR));
                reminder.setMonth(calendar.get(Calendar.MONTH));
                reminder.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            }
            //set current time if time wasn't picked
            if (reminder.getHour() == 0) {
                reminder.setHour(calendar.get(Calendar.HOUR));
                reminder.setMinute(calendar.get(Calendar.MINUTE));
            }
            //updating the reminder
            reminderPresenter.updateReminder(reminder);
            //Setting timestamp of the reminder
            calendar.set(Calendar.YEAR, reminder.getYear());
            calendar.set(Calendar.MONTH, reminder.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, reminder.getDay());
            calendar.set(Calendar.HOUR, reminder.getHour());
            calendar.set(Calendar.MINUTE, reminder.getMinute());
            task.setTimestamp(calendar.getTimeInMillis());
            //Updating the task
            reminderPresenter.update(task);

        }

        //return to mainActivity
        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
