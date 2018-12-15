package com.violenthoboenterprises.blistful;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.app.job.JobService;
import android.util.Log;

import com.violenthoboenterprises.blistful.model.TaskAdapter;
import com.violenthoboenterprises.blistful.model.TaskViewModel;

public class BackgroundJobService extends JobService {

    private static String TAG = "BackgroundJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }

                Intent intent = new Intent(BackgroundJobService.this,
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity
                        (getApplicationContext(), 0, intent, 0);

                //TODO get new channel id
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                        "123").setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("My notification")
                        .setContentText("Much longer text that cannot fit one line...")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Much longer text that cannot fit one line..."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManagerCompat =
                        NotificationManagerCompat.from(getApplicationContext());

                notificationManagerCompat.notify(0, builder.build());

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        };
        handler.postDelayed(runnable, 100000);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(jobCancelled){
//                    return;
//                }
//
//                Intent intent = new Intent(BackgroundJobService.this,
//                        MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PendingIntent pendingIntent = PendingIntent.getActivity
//                        (getApplicationContext(), 0, intent, 0);
//
//                //TODO get new channel id
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
//                        "123").setSmallIcon(R.mipmap.ic_launcher_round)
//                        .setContentTitle("My notification")
//                        .setContentText("Much longer text that cannot fit one line...")
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText("Much longer text that cannot fit one line..."))
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        // Set the intent that will fire when the user taps the notification
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//
//                NotificationManagerCompat notificationManagerCompat =
//                        NotificationManagerCompat.from(getApplicationContext());
//
//                notificationManagerCompat.notify(0, builder.build());
//
//                Log.d(TAG, "Job finished");
//                jobFinished(params, false);
//            }
//        }).start();

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}
