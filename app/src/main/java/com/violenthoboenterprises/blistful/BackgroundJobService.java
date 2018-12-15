package com.violenthoboenterprises.blistful;

import android.app.job.JobParameters;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.app.job.JobService;
import android.util.Log;

import com.violenthoboenterprises.blistful.model.TaskAdapter;
import com.violenthoboenterprises.blistful.model.TaskViewModel;

public class BackgroundJobService extends JobService {

    private static String TAG = "BackgroundJobService";
    private boolean jobCancelled = false;
//    private TaskViewModel taskViewModel;
//    private TaskAdapter adapter;
//    private RecyclerView.ViewHolder viewHolder;

//    public BackgroundJobService(TaskViewModel taskViewModel, TaskAdapter adapter, RecyclerView.ViewHolder viewHolder) {
//        this.taskViewModel = taskViewModel;
//        this.adapter = adapter;
//        this.viewHolder = viewHolder;
//    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");
        doBackgroundWork(jobParameters);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        PersistableBundle bundle = params.getExtras();
        String testString = bundle.getString("test_string");
        Log.d(TAG, testString);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(jobCancelled){
                    return;
                }

                try {
                    Thread.sleep(3000);
//                    taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                    Log.d(TAG, "Remove task permanently");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}
