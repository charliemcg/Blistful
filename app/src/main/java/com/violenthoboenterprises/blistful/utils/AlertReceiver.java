package com.violenthoboenterprises.blistful.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;

import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.activities.MainActivity;
import com.violenthoboenterprises.blistful.model.Task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class AlertReceiver extends BroadcastReceiver {

    String TAG = this.getClass().getSimpleName();
    private Task task;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Initialising variables for holding values from database
        boolean boolSnoozeStatus = intent.getBooleanExtra("snoozeStatus", false);
        task = (Task) intent.getSerializableExtra("task");
        //retrieving task properties necessary for setting notification
        createNotification(context, "", boolSnoozeStatus);

    }

    public void createNotification(Context context,
                                   String msgAlert, boolean snoozeStatus) {

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        RemoteViews remoteViews;

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting values to custom notification view
//        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);//TODO consider reinstating remote views
//        remoteViews.setTextViewText(R.id.tvTaskText, task.getTask());
        //randomly generating motivational toast
        Random random = new Random();
        String[] strMotivation = new String[]{context.getResources().getString(R.string.getItDone),
                context.getResources().getString(R.string.smashThatTask), context.getResources().getString(R.string.beAWinner),
                context.getResources().getString(R.string.onlyWimpsGiveUp), context.getResources().getString(R.string.dontBeAFailure),
                context.getResources().getString(R.string.beVictorious), context.getResources().getString(R.string.killThisTask)};
        int j = random.nextInt(7);

        //Setting up notification channel for Oreo
        final String notificChannelId = "notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notificChannelId, "notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Notifications about due being due");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Building the notification
        builder = new NotificationCompat.Builder(context, notificChannelId)
                .setSmallIcon(R.drawable.small_notific_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_og))
                .setContentTitle(strMotivation[j])
                .setTicker(msgAlert)
                .setWhen(0)
                .setContentText(task.getTask())
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColorized(true)
                .setColor(context.getResources().getColor(R.color.purple))
//                .setCustomContentView(remoteViews)//TODO reinstate remote views
                .setLights(66666666, 500, 2000)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentIntent(notificIntent)
                .setAutoCancel(true);

        //Can only show notification if user has feature enabled. Non repeating tasks need
        //no further processing than notify()
        if (task.getRepeatInterval() == null) {

            notificationManager.notify(1, builder.build());

        //need to set up next notification for repeating task
        } else {

            //updating the task in case any of the values have changed
            task = MainActivity.taskViewModel.getTask(task.getId());

            notificationManager.notify(1, builder.build());

            //snoozed notifications cannot corrupt regular repeating notifications
            if (task.getRepeatInterval().equals("day") && !snoozeStatus) {

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level//TODO reinstate duplicate detection. Use the list of timestamps
                long futureStamp = task.getTimestamp() + AlarmManager.INTERVAL_DAY;
                futureStamp = getFutureStamp(futureStamp);
                task.setTimestamp(futureStamp);
                MainActivity.taskViewModel.update(task);

                Intent alertIntent = new Intent(context, AlertReceiver.class);
                alertIntent.putExtra
                        ("snoozeStatus", false);
                alertIntent.putExtra("task", task);
                List<Integer> timestamps = MainActivity.taskViewModel.getAllTimestamps();//TODO check that it gets data at due time and no sooner
                alertIntent.putExtra("timestamps", (Serializable) timestamps);

                //Setting alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, task.getId(), alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar alarmCalendar = Calendar.getInstance();
                Long diff;

                //Setting a repeat alarm
                Calendar currentCal = Calendar.getInstance();
                Calendar futureCal = Calendar.getInstance();
                futureCal.setTimeInMillis(futureStamp);
                diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                //checking if timestamp has been updated or not
                if (diff < 86400000) {
                    MainActivity.alarmManager.set(AlarmManager.RTC, futureStamp, pendingIntent);
                } else {
                    long daysOut = diff / 86400000;
                    MainActivity.alarmManager.set(AlarmManager.RTC, (futureStamp - (86400000 * daysOut)),
                            pendingIntent);
                }

                alarmCalendar.setTimeInMillis(futureStamp - AlarmManager.INTERVAL_DAY);

                Calendar dayCal = Calendar.getInstance();
                dayCal.setTimeInMillis(task.getTimestamp());
                int alarmDay = dayCal.get(Calendar.DAY_OF_MONTH);

                //alarm data is already updated if user marked task as done
                if (/*!task.isManualKill() && */(alarmDay != currentCal.get(Calendar.DAY_OF_MONTH))) {//TODO found out if this conditional is still needed
                    diff = futureStamp - AlarmManager.INTERVAL_DAY - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / 86400000;
                        futureStamp = futureStamp - (86400000 * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - AlarmManager.INTERVAL_DAY);
                    }

                    MainActivity.taskViewModel.update(task);

                } else {
                    diff = futureStamp - AlarmManager.INTERVAL_DAY - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / 86400000;
                        futureStamp = futureStamp - (86400000 * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - AlarmManager.INTERVAL_DAY);
                    }

                    MainActivity.taskViewModel.update(task);
                }

            } else if (task.getRepeatInterval().equals("week") && !snoozeStatus) {

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = task.getTimestamp() + (AlarmManager.INTERVAL_DAY * 7);
                futureStamp = getFutureStamp(futureStamp);
                task.setTimestamp(futureStamp);
                MainActivity.taskViewModel.update(task);

                Intent alertIntent = new Intent(context, AlertReceiver.class);
                alertIntent.putExtra
                        ("snoozeStatus", false);
                alertIntent.putExtra("task", task);

                //Setting alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, task.getId(), alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar alarmCalendar = Calendar.getInstance();
                Long diff;

                Calendar currentCal = Calendar.getInstance();
                Calendar futureCal = Calendar.getInstance();
                futureCal.setTimeInMillis(futureStamp);
                diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                //checking if timestamp has been updated or not
                if (diff < (86400000 * 7)) {
                    MainActivity.alarmManager.set(AlarmManager.RTC, futureStamp, pendingIntent);
                } else {
                    long daysOut = diff / (86400000 * 7);
                    MainActivity.alarmManager.set(AlarmManager.RTC, (futureStamp - ((86400000 * 7) * daysOut)),
                            pendingIntent);
                }

                alarmCalendar.setTimeInMillis(futureStamp - (AlarmManager.INTERVAL_DAY * 7));

                Calendar dayCal = Calendar.getInstance();
                dayCal.setTimeInMillis(task.getTimestamp());
                int alarmDay = dayCal.get(Calendar.DAY_OF_MONTH);

                //alarm data is already updated if user marked task as done
                if ((alarmDay != currentCal.get(Calendar.DAY_OF_MONTH))) {
                    diff = futureStamp - (AlarmManager.INTERVAL_DAY * 7) - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / (86400000 * 7);
                        futureStamp = futureStamp - ((86400000 * 7) * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - (AlarmManager.INTERVAL_DAY * 7));
                    }

                    MainActivity.taskViewModel.update(task);

                } else {
                    diff = futureStamp - (AlarmManager.INTERVAL_DAY * 7) - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / (86400000 * 7);
                        futureStamp = futureStamp - ((86400000 * 7) * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - (AlarmManager.INTERVAL_DAY * 7));
                    }

                    MainActivity.taskViewModel.update(task);
                }

            } else if (task.getRepeatInterval().equals("month") && !snoozeStatus) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(task.getTimestamp());
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int originalDay = task.getOriginalDay();
                //Determining if it's a leap year
                int leapYear = 0;
                if (year % 4 == 0) {
                    leapYear = 1;
                }
                int multiplier;
                //months that have 31 days
                if (month == 0 || month == 2 || month == 4 || month == 6
                        || month == 7 || month == 9 || month == 11) {
                    //if due on 31st day and following month doesn't have 31 days set to last day of following month
                    if (originalDay == 31 && month != 6 && month != 11 && month != 0) {
                        multiplier = 30;
                        //if due on 31st and following month is February set to last day of February
                    } else if (originalDay == 31 && month == 0) {
                        multiplier = 28 + leapYear;
                        //if due on 30th and following month is February set to last day of February
                    } else if (originalDay == 30 && month == 0) {
                        multiplier = 29 + leapYear;
                        //if due on 29th and following month is February set to last day of February
                    } else if (originalDay == 29 && month == 0) {
                        multiplier = 30 + leapYear;
                    } else {
                        multiplier = 31;
                    }
                    //February
                } else if (month == 1) {
                    //if original due day is 31 then set to 31st of following month
                    if (originalDay == 31) {
                        multiplier = 31;
                        //if original due day is 30 then set to 30th of following month
                    } else if (originalDay == 30) {
                        multiplier = 30;
                        //if original due day is 39 then set to 39th of following month
                    } else if (originalDay == 29) {
                        multiplier = 29;
                    } else {
                        multiplier = 28 + leapYear;
                    }
                    //months that have 30 days
                } else {
                    //if original due day is 31 then set to 31st of following month
                    if (originalDay == 31) {
                        multiplier = 31;
                    } else {
                        multiplier = 30;
                    }
                }

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
                long futureStamp = task.getTimestamp() + (AlarmManager.INTERVAL_DAY * multiplier);
                futureStamp = getFutureStamp(futureStamp);
                task.setTimestamp(futureStamp);
                Calendar blah = Calendar.getInstance();
                blah.setTimeInMillis(futureStamp);
                Log.d(TAG, "Due: hour." + blah.get(Calendar.HOUR_OF_DAY) + " day." + blah.get(Calendar.DAY_OF_MONTH) + " month." + blah.get(Calendar.MONTH));
                MainActivity.taskViewModel.update(task);

                Intent alertIntent = new Intent(context, AlertReceiver.class);
                alertIntent.putExtra
                        ("snoozeStatus", false);
                alertIntent.putExtra("task", task);

                //Setting alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context, task.getId(), alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Calendar alarmCalendar = Calendar.getInstance();
                Long diff;

                Calendar currentCal = Calendar.getInstance();
                Calendar futureCal = Calendar.getInstance();
                futureCal.setTimeInMillis(futureStamp);

                MainActivity.alarmManager.set(AlarmManager.RTC, futureStamp, pendingIntent);

                alarmCalendar.setTimeInMillis(futureStamp - (AlarmManager.INTERVAL_DAY * multiplier));

                Calendar dayCal = Calendar.getInstance();
                dayCal.setTimeInMillis(task.getTimestamp());
                int alarmDay = dayCal.get(Calendar.DAY_OF_MONTH);

                //alarm data is already updated if user marked task as done
                if ((alarmDay != currentCal.get(Calendar.DAY_OF_MONTH))) {//TODO found out if these conditions do anything
                    diff = futureStamp - (AlarmManager.INTERVAL_DAY * multiplier) - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / (86400000 * multiplier);
                        futureStamp = futureStamp - ((86400000 * multiplier) * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - (AlarmManager.INTERVAL_DAY * multiplier));
                    }

                    MainActivity.taskViewModel.update(task);

                } else {
                    diff = futureStamp - (AlarmManager.INTERVAL_DAY * multiplier) - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / (86400000 * multiplier);
                        futureStamp = futureStamp - ((86400000 * multiplier) * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - (AlarmManager.INTERVAL_DAY * multiplier));
                    }

                    MainActivity.taskViewModel.update(task);
                }

            }
        }
    }

    private long getFutureStamp(long futureStamp) {

        List<Integer> timestamps = MainActivity.taskViewModel.getAllTimestamps();

        //ensuring that saved timestamp is unique
        for (int i = 0; i < timestamps.size(); i++) {
            if (timestamps.get(i) == futureStamp) {
                futureStamp++;
                i = 0;
            }
        }

        return futureStamp;
    }
}
