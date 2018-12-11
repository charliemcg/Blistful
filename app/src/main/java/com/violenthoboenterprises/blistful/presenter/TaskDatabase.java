package com.violenthoboenterprises.blistful.presenter;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.violenthoboenterprises.blistful.model.Subtask;
import com.violenthoboenterprises.blistful.model.Task;

/*
 * This is where the database is built
 */
@Database(entities = {Task.class, Subtask.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public abstract TaskDao taskDao();
    public abstract SubtaskDao subtaskDao();

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
        private SubtaskDao subtaskDao;
        private PopulateDbAsyncTask(TaskDatabase db){
            taskDao = db.taskDao();
            subtaskDao = db.subtaskDao();
        }
        @Override
        protected Void doInBackground(Void... voids){
            taskDao.insert(new Task("This is a task"));
            taskDao.insert(new Task("Swipe left or right to delete a task"));
            taskDao.insert(new Task("Press '+' to add a new task"));
            subtaskDao.insert(new Subtask(1, "id 1 subby 1"));
            subtaskDao.insert(new Subtask(1, "id 1 subby 2"));
            subtaskDao.insert(new Subtask(1, "id 1 subby 3"));
            subtaskDao.insert(new Subtask(2, "id 2 subby 1"));
            subtaskDao.insert(new Subtask(2, "id 2 subby 2"));
            subtaskDao.insert(new Subtask(2, "id 2 subby 3"));
            subtaskDao.insert(new Subtask(3, "id 3 subby 1"));
            return null;
        }
    }

}
