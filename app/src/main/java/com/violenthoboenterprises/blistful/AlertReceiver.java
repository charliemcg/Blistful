package com.violenthoboenterprises.blistful;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;

import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlertReceiver extends BroadcastReceiver {

    String TAG = this.getClass().getSimpleName();

    //Initialising variables for holding values from database
//    Database theDB;
//    String highlight;
//    String highlightDec;
//    boolean remindersAvailable;
//    int theTaskListSize;
//    private int intTaskId;
    private boolean boolSnoozeStatus;
    private Task task;
    private List<Integer> timestamps;

//    Intent theAlertIntent;
//    AlarmManager theAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolSnoozeStatus = intent.getBooleanExtra("snoozeStatus", false);
        task = (Task) intent.getSerializableExtra("task");
        timestamps = (List<Integer>) intent.getSerializableExtra("timestamps");

        //retrieving task properties necessary for setting notification
        createNotification(context, "", task.getId(), boolSnoozeStatus);

    }

    public void createNotification(Context context,
                                   String msgAlert, int broadId, boolean snoozeStatus) {

        //defining intent and action to perform
        PendingIntent notificIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        RemoteViews remoteViews;

//        if (MainActivity.db == null) {
//            theDB = new Database(context);
//            highlight = "#00FF00";
//            highlightDec = "-298516736";
//            //getting universal data
//            Cursor uniResult = theDB.getUniversalData();
//            while (uniResult.moveToNext()) {
//                remindersAvailable = uniResult.getInt(6) > 0;
//            }
//            uniResult.close();
//            theTaskListSize = theDB.getTotalRows();
//            theAlertIntent = new Intent(context, AlertReceiver.class);
//            theAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        } else {
//            theDB = MainActivity.db;
//            highlight = MainActivity.highlight;
//            highlightDec = MainActivity.highlightDec;
//            remindersAvailable = MainActivity.remindersAvailable;
//            theTaskListSize = MainActivity.taskList.size();
//            theAlertIntent = MainActivity.alertIntent;
//            theAlarmManager = MainActivity.alarmManager;
//        }

        //getting task data
//        String dbTimestamp = "";
//        String dbTask = "";
//        Boolean dbRepeat = false;
//        String dbRepeatInterval = "";
//        Boolean dbManualKill = false;
//        Boolean dbKilledEarly = false;
//        Cursor dbResult;
//        dbResult = theDB.getData(broadId);
//        while (dbResult.moveToNext()) {
//            dbTimestamp = dbResult.getString(3);
//            dbTask = dbResult.getString(4);
//            dbRepeat = dbResult.getInt(8) > 0;
//            dbRepeatInterval = dbResult.getString(13);
//            dbManualKill = dbResult.getInt(18) > 0;
//            dbKilledEarly = dbResult.getInt(19) > 0;
//        }
//        dbResult.close();

        //getting alarm data
//        Cursor alarmResult = theDB.getAlarmData(broadId);
//        String alarmDay = "";
//        String alarmMonth = "";
//        String alarmYear = "";
//        while (alarmResult.moveToNext()) {
//            alarmDay = alarmResult.getString(4);
//            alarmMonth = alarmResult.getString(5);
//            alarmYear = alarmResult.getString(6);
//        }
//        alarmResult.close();

        //allows for notifications
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting values to custom notification view
//        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_light);//TODO consider reinstating remote views
//        remoteViews.setTextViewText(R.id.notif_title, task.getTask());
        //TODO reinstate randomiser
        //randomly generating motivational toast
//        int j = MainActivity.random.nextInt(7);
//        while (MainActivity.motivation[j].equals(MainActivity.lastToast)) {
//            j = MainActivity.random.nextInt(7);
//        }

        //Setting up notification channel for Oreo
        final String notificChannelId = "notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    notificChannelId, "notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Notifications about due being due");
            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Integer.parseInt(MainActivity.highlightDec));//TODO find LED colour
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Building the notification
        builder = new NotificationCompat.Builder(context, notificChannelId)
                .setSmallIcon(R.drawable.small_notific_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_og))
//                .setContentTitle(MainActivity.motivation[j])//TODO get randomised string
                .setTicker(msgAlert)
                .setWhen(0)
                .setContentText(task.getTask())
                .setStyle(new NotificationCompat.BigTextStyle())
                .setColorized(true)
//                .setColor(Color.parseColor(highlight))TODO get colour
//                .setCustomContentView(remoteViews)//TODO reinstate remote views
//                .setLights(66666666, 500, 500).setDefaults
//                        (NotificationCompat.DEFAULT_SOUND)//TODO get highlight colour
                .setContentIntent(notificIntent)
                .setAutoCancel(true);

        //Can only show notification if user has feature enabled. Non repeating tasks need
        //no further processing than notify()
        if (task.getRepeatInterval() == null) {

            notificationManager.notify(1, builder.build());

        //need to set up next notification for repeating task
        } else {

            //don't inform user that task is due if they marked it as done
            if (/*!dbKilledEarly && */true) {//TODO check if killed early

                notificationManager.notify(1, builder.build());

            } else {

//                theDB.updateKilledEarly(String.valueOf(broadId), false);

            }

            //cancelling any snoozed alarm data
//            theDB.updateSnoozeData(String.valueOf(broadId), "", "", "",
//                    "", "", "");
//            theDB.updateSnoozedTimestamp(String.valueOf(broadId), "0");
//            theDB.updateSnooze(String.valueOf(broadId), false);

//            theDB.updateIgnored(String.valueOf(broadId), false);

            //snoozed notifications cannot corrupt regular repeating notifications
            if (task.getRepeatInterval().equals("day") && !snoozeStatus) {

                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the millisecond level
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

//
//                //updating timestamp
////                theDB.updateTimestamp(String.valueOf(broadId),
////                        String.valueOf(futureStamp));
//
//                //setting the name of the task for which the
//                // notification is being set
//                theAlertIntent.putExtra("broadId", broadId);
//
//                //Setting alarm
//                MainActivity.pendIntent = PendingIntent.getBroadcast(
//                        context, broadId, theAlertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
                Calendar alarmCalendar = Calendar.getInstance();
                Long diff;
//
//                //Setting a repeat alarm
                if (/*!dbKilledEarly*/true) {//TODO check if killed early

                    Calendar currentCal = Calendar.getInstance();
                    Calendar futureCal = Calendar.getInstance();
                    futureCal.setTimeInMillis(futureStamp);
//                    Log.d(TAG,
//                            "\nYear: " + futureCal.get(Calendar.YEAR) +
//                                    "\nMonth: " + futureCal.get(Calendar.MONTH) +
//                                    "\nDay: " + futureCal.get(Calendar.DAY_OF_MONTH) +
//                                    "\nHour: " + futureCal.get(Calendar.HOUR_OF_DAY) +
//                                    "\nMinute: " + futureCal.get(Calendar.MINUTE));
                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
                    //checking if timestamp has been updated or not
                    if (diff < 86400000) {
                        MainActivity.alarmManager.set(AlarmManager.RTC, futureStamp, pendingIntent);
                    } else {
                        long daysOut = diff / 86400000;
                        MainActivity.alarmManager.set(AlarmManager.RTC, (futureStamp - (86400000 * daysOut)),
                                pendingIntent);
                    }
                } else {
//                    theAlarmManager.set(AlarmManager.RTC, Long.parseLong
//                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
                }

                Calendar currentCal = Calendar.getInstance();

                alarmCalendar.setTimeInMillis(futureStamp - AlarmManager.INTERVAL_DAY);

                Calendar dayCal = Calendar.getInstance();
                dayCal.setTimeInMillis(task.getTimestamp());
                int alarmDay = dayCal.get(Calendar.DAY_OF_MONTH);

                //alarm data is already updated if user marked task as done
                if (/*!dbManualKill
                        && */(alarmDay != currentCal.get(Calendar.DAY_OF_MONTH))) {//TODO detect manual kill
                    diff = futureStamp - AlarmManager.INTERVAL_DAY - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / 86400000;
                        futureStamp = futureStamp - (86400000 * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - AlarmManager.INTERVAL_DAY);
                    }

                    task.setTimestamp(/*alarmCalendar.getTimeInMillis()*/futureStamp);
                    MainActivity.taskViewModel.update(task);

//                    //updating due date in database
////                    theDB.updateAlarmData(String.valueOf(broadId),
////                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
////                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
////                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
////                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));
//
                }else{
                    diff = futureStamp - AlarmManager.INTERVAL_DAY - currentCal.getTimeInMillis();

                    if (diff > 0) {
                        long daysOut = diff / 86400000;
                        futureStamp = futureStamp - (86400000 * (daysOut + 1));
                        alarmCalendar.setTimeInMillis(futureStamp
                                - AlarmManager.INTERVAL_DAY);
                    }

                    task.setTimestamp(alarmCalendar.getTimeInMillis());
                    MainActivity.taskViewModel.update(task);
                }
//
////                theDB.updateManualKill(String.valueOf(broadId), false);
//
            } else if (task.getRepeatInterval().equals("week") && !snoozeStatus) {
//
//                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
//                // detect duplicates and then adjusting the timestamp on the millisecond level
//                long futureStamp = Long.parseLong(dbTimestamp) +
//                        ((AlarmManager.INTERVAL_DAY * 7) / 1000);
//                futureStamp = getFutureStamp(futureStamp);
//
//                //updating timestamp
////                theDB.updateTimestamp(String.valueOf(broadId), String.valueOf(futureStamp));
//
//                //setting the name of the task for which the
//                // notification is being set
//                theAlertIntent.putExtra("broadId", broadId);
//
//                //Setting alarm
//                MainActivity.pendIntent = PendingIntent.getBroadcast(
//                        context, broadId, theAlertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//                Calendar alarmCalendar = Calendar.getInstance();
//                Long diff;
//
//                //setting a repeat alarm
//                if (!dbKilledEarly) {
//                    Calendar currentCal = Calendar.getInstance();
//                    Calendar futureCal = Calendar.getInstance();
//                    futureCal.setTimeInMillis(futureStamp * 1000);
//                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
//                    diff = diff / 1000;
//                    //checking if timestamp has been updated or not
//                    if (diff < (86400 * 7)) {
//                        theAlarmManager.set(AlarmManager.RTC, Long.parseLong
//                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);
//                    } else {
//                        int daysOut = (int) (diff / (86400 * 7));
//                        theAlarmManager.set(AlarmManager.RTC, (Long.parseLong
//                                        (String.valueOf(futureStamp) + "000") -
//                                        ((86400000 * 7) * daysOut)),
//                                MainActivity.pendIntent);
//                    }
//                } else {
//                    theAlarmManager.set(AlarmManager.RTC, Long.parseLong
//                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
//                }
//
//                Calendar currentCal = Calendar.getInstance();
//
//                alarmCalendar.setTimeInMillis(Long.parseLong(String.valueOf(futureStamp)
//                        + "000") - (AlarmManager.INTERVAL_DAY * 7));
//
//                //alarm data is already updated if user marked task as done
//                if (!dbManualKill
//                        && (Integer.parseInt(alarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))) {
//                    currentCal = Calendar.getInstance();
//                    Calendar futureCal = Calendar.getInstance();
//                    futureCal.setTimeInMillis((futureStamp * 1000) - (AlarmManager.INTERVAL_DAY * 7));
//                    diff = (Long.parseLong(String.valueOf(futureStamp) + "000")
//                            - (AlarmManager.INTERVAL_DAY * 7)) - currentCal.getTimeInMillis();
//                    diff = diff / 1000;
//                    if (diff > 0) {
//                        int daysOut = (int) (diff / (86400 * 7));
//                        futureStamp = futureStamp - ((86400 * 7) * (daysOut + 1));
//                        alarmCalendar.setTimeInMillis(Long.parseLong
//                                (String.valueOf(futureStamp) + "000")
//                                - (AlarmManager.INTERVAL_DAY * 7));
//                    }
//
//                    //updating due date in database
////                    theDB.updateAlarmData(String.valueOf(broadId),
////                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
////                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
////                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
////                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));
//
//                }
//
////                theDB.updateManualKill(String.valueOf(broadId), false);
//
            } else if (task.getRepeatInterval().equals("month") && !snoozeStatus) {
//
//                //Getting interval in seconds based on specific day and month
//                int interval = 0;
//                int theYear = Integer.parseInt(alarmYear);
//                int theMonth = Calendar.getInstance().get(Calendar.MONTH);
//
//                int theDay = Integer.parseInt(alarmDay);
//                //Month January and day is 29 non leap year 2592000
//                if ((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)) {
//                    interval = 2592000;
//                    //Month January and day is 30 non leap year 2505600
//                } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)) {
//                    interval = 2505600;
//                    //Month January and day is 31 non leap year 2419200
//                } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)) {
//                    interval = 2419200;
//                    //Month January and day is 30 leap year 2592000
//                } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 == 0)) {
//                    interval = 2592000;
//                    //Month January and day is 31 leap year 2505600
//                } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)) {
//                    interval = 2505600;
//                    //Month March||May||August||October and day is 31 2592000
//                } else if (((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
//                        || (theMonth == 9)) && (theDay == 31)) {
//                    interval = 2592000;
//                    //Month January||March||May||July||August||October||December 2678400
//                } else if ((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
//                        || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
//                        || (theMonth == 11)) {
//                    interval = 2678400;
//                    //Month April||June||September||November 2592000
//                } else if ((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
//                        || (theMonth == 10)) {
//                    interval = 2592000;
//                    //Month February non leap year 2419200
//                } else if ((theMonth == 1) && (theYear % 4 != 0)) {
//                    interval = 2419200;
//                    //Month February leap year 2505600
//                } else if ((theMonth == 1) && (theYear % 4 == 0)) {
//                    interval = 2505600;
//                }
//
//                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
//                // detect duplicates and then adjusting the timestamp on the millisecond level
//                long futureStamp = (Long.parseLong(dbTimestamp) + interval);
//                futureStamp = getFutureStamp(futureStamp);
//
//                futureStamp = Long.parseLong(String.valueOf(futureStamp) + "000");
////                Cursor origResult = theDB.getData(broadId);
//                String originalDay = "";
//                while (origResult.moveToNext()) {
//                    originalDay = origResult.getString(20);
//                }
//                origResult.close();
//                int daysOut = 0;
//
//                //detecting and adjusting timestamp if it is wrong
//                Calendar cal = Calendar.getInstance();
//                cal.setTimeInMillis(futureStamp);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//                int month = cal.get(Calendar.MONTH);
//                if (day != Integer.parseInt(originalDay)) {
//                    if (month == 0 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 2 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 3 && (day == 28 || day == 29)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 4 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 5 && (day == 28 || day == 29)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 6 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 7 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 8 && (day == 28 || day == 29)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 9 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 10 && (day == 28 || day == 29)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    } else if (month == 11 && (day == 28 || day == 29 || day == 30)) {
//                        daysOut = Integer.parseInt(originalDay) - day;
//                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
//                    }
//                }
//
//                futureStamp = futureStamp / 1000;
//
//                //updating timestamp
////                theDB.updateTimestamp(String.valueOf(broadId),
////                        String.valueOf(futureStamp));
//
//                //setting the name of the task for which the
//                // notification is being set
//                theAlertIntent.putExtra("broadId", broadId);
//
//                //Setting alarm
//                MainActivity.pendIntent = PendingIntent.getBroadcast(
//                        context, broadId, theAlertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//                Calendar alarmCalendar = Calendar.getInstance();
//                Long diff;
//
//                if (!dbKilledEarly) {
//                    Calendar currentCal = Calendar.getInstance();
//                    Calendar futureCal = Calendar.getInstance();
//                    futureCal.setTimeInMillis(futureStamp * 1000);
//                    diff = futureCal.getTimeInMillis() - currentCal.getTimeInMillis();
//                    diff = diff / 1000;
//                    if (diff < (interval + ((AlarmManager.INTERVAL_DAY * daysOut) / 1000))) {
//                        futureCal.setTimeInMillis(Long.parseLong
//                                (String.valueOf(futureStamp) + "000"));
//                        theAlarmManager.set(AlarmManager.RTC, Long.parseLong
//                                (String.valueOf(futureStamp) + "000"), MainActivity.pendIntent);
//                    } else {
//                        int daysWrong = (int) (diff / (interval +
//                                ((AlarmManager.INTERVAL_DAY * daysOut) / 1000)));
//                        theAlarmManager.set(AlarmManager.RTC, (Long.parseLong
//                                        (String.valueOf(futureStamp) + "000") -
//                                        (((interval + (AlarmManager.INTERVAL_DAY * daysOut)) * daysWrong))),
//                                MainActivity.pendIntent);
//                    }
//                } else {
//                    theAlarmManager.set(AlarmManager.RTC, Long.parseLong
//                            (String.valueOf(dbTimestamp) + "000"), MainActivity.pendIntent);
//                }
//
//                Calendar currentCal = Calendar.getInstance();
//
//                Long futureStampForCalculation = Long.parseLong(String.valueOf(futureStamp) + "000");
//                Long intervalForCalculation = Long.parseLong(String.valueOf(interval)) * 1000;
//                Long dayDriftForCalculation = AlarmManager.INTERVAL_DAY * daysOut;
//
//                alarmCalendar.setTimeInMillis(futureStampForCalculation
//                        - (intervalForCalculation + dayDriftForCalculation));
//
//                //alarm data is already updated if user marked task as done
//                if (!dbManualKill && (Integer.parseInt(alarmMonth) != currentCal.get(Calendar.MONTH))) {
//
//                    Long currentMillisForCalculation = currentCal.getTimeInMillis();
//
//                    diff = futureStampForCalculation - (intervalForCalculation
//                            + dayDriftForCalculation) - currentMillisForCalculation;
//                    diff = diff / 1000;
//
//                    if (diff > 0) {
//                        int daysWrong = (int) (diff / (interval +
//                                ((AlarmManager.INTERVAL_DAY / 1000) * daysOut)));
//                        futureStamp = futureStamp - ((interval +
//                                ((AlarmManager.INTERVAL_DAY / 1000) * daysOut) * (daysWrong + 1)));
//                        alarmCalendar.setTimeInMillis(Long.parseLong
//                                (String.valueOf(futureStamp) + "000")
//                                - (interval + (AlarmManager.INTERVAL_DAY * daysOut)));
//                    }
//
//                    //updating due date in database
////                    theDB.updateAlarmData(String.valueOf(broadId),
////                            String.valueOf(alarmCalendar.get(Calendar.HOUR)),
////                            String.valueOf(alarmCalendar.get(Calendar.MINUTE)),
////                            String.valueOf(alarmCalendar.get(Calendar.AM_PM)),
////                            String.valueOf(alarmCalendar.get(Calendar.DAY_OF_MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.MONTH)),
////                            String.valueOf(alarmCalendar.get(Calendar.YEAR)));
//
//                }
//
////                theDB.updateManualKill(String.valueOf(broadId), false);
//
            }
        }
    }

    private long getFutureStamp(long futureStamp) {
//        String tempTimestamp = "";

        //ensuring that saved timestamp is unique
        for(int i = 0; i < timestamps.size(); i++){
            if(timestamps.get(i) == futureStamp){
                futureStamp++;
                i = 0;
            }
        }

//        for (int i = 0; i < theTaskListSize; i++) {
////            Cursor tempResult = theDB.getData(i);
////            while (tempResult.moveToNext()) {
////                tempTimestamp = tempResult.getString(3);
////            }
////            tempResult.close();
//            if (futureStamp == Long.parseLong(tempTimestamp)) {
//                futureStamp++;
//                i = 0;
//            }
//
//        }
        return futureStamp;
    }
}
