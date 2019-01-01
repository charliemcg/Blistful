package com.violenthoboenterprises.blistful.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.violenthoboenterprises.blistful.activities.MainActivity;
import com.violenthoboenterprises.blistful.model.ReminderPresenterImpl;
import com.violenthoboenterprises.blistful.model.Task;
import com.violenthoboenterprises.blistful.model.TaskViewModel;
import com.violenthoboenterprises.blistful.presenter.ReminderPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        //Reminder data is lost when device powered down. Need to know when device is booted up again
        //in order to set all alarms again.
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferences preferences = context.getSharedPreferences("com.violenthoboenterprises.blistful",
                    Context.MODE_PRIVATE);

            preferences.edit().putBoolean(StringConstants.REINSTATE_REMINDERS_AFTER_REBOOT, true).apply();

//
//            int taskListSize = db.getTotalRows();
//
//            ArrayList<Integer> IDList = db.getIDs();
//
//            for( int i = 0 ; i < taskListSize ; i++ ) {
//
//                //Getting values from database
//                String dbTimestamp = "";
//                Boolean dbDue = false;
//                int dbBroadcast = 0;
//                Boolean dbOverdue = false;
//                Cursor dbResult = db.getData(IDList.get(i));
//                while (dbResult.moveToNext()) {
//                    dbTimestamp = dbResult.getString(3);
//                    dbDue = dbResult.getInt(5) > 0;
//                    dbBroadcast = dbResult.getInt(7);
//                    dbOverdue = dbResult.getInt(9) > 0;
//                }
//                dbResult.close();
//
//                //Setting alarm if required
//                if(dbDue && !dbOverdue) {
//                    alertIntent = new Intent(context, AlertReceiver.class);
//                    alertIntent.putExtra("broadId", dbBroadcast);
//                    pendIntent = PendingIntent.getBroadcast(context, dbBroadcast,
//                            alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    long dueTimestamp = Long.parseLong(dbTimestamp + "000");
//                    alarmManager.set(AlarmManager.RTC, dueTimestamp, pendIntent);
//                }
//            }
        }
    }

    public void reinstateReminders(MainActivity context) {
        List<Task> tasks = MainActivity.taskViewModel.getAllTasksAsTasks();
        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i).getTimestamp() != 0){
                MainActivity.alertIntent = new Intent(context, AlertReceiver.class);
                MainActivity.alertIntent.putExtra("snoozeStatus", false);
                MainActivity.alertIntent.putExtra("task", tasks.get(i));

                //Setting alarm
                MainActivity.pendingIntent = PendingIntent.getBroadcast(
                        context, tasks.get(i).getId(), MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.cancel(MainActivity.pendingIntent);

                MainActivity.alarmManager.set(AlarmManager.RTC,
                        tasks.get(i).getTimestamp(),
                        MainActivity.pendingIntent);
            }
        }
    }
}
