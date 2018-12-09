package com.violenthoboenterprises.blistful.presenter;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.violenthoboenterprises.blistful.model.Task;

/*
 * This is where the database is built
 */
@Database(entities = {Task.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public abstract TaskDao taskDao();

    //Instance
    public static synchronized TaskDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TaskDatabase.class, "task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    //Callback
    public static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    //Populating the task table with tutorial tasks
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private TaskDao taskDao;
        private PopulateDbAsyncTask(TaskDatabase db){taskDao = db.taskDao();}
        @Override
        protected Void doInBackground(Void... voids){
            taskDao.insert(new Task("This is a task"));
            taskDao.insert(new Task("Swipe left or right to delete a task"));
            taskDao.insert(new Task("Press '+' to add a new task"));
            return null;
        }
    }

}
