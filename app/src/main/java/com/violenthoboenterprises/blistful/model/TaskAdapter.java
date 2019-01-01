package com.violenthoboenterprises.blistful.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.violenthoboenterprises.blistful.activities.MainActivity;
import com.violenthoboenterprises.blistful.activities.ReminderActivity;
import com.violenthoboenterprises.blistful.utils.StringConstants;
import com.violenthoboenterprises.blistful.activities.SubtasksActivity;
import com.violenthoboenterprises.blistful.activities.NoteActivity;
import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
 * Adding tasks to the recycler view
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private final static String TAG = "TaskAdapter";
    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private MainActivityPresenter mainActivityPresenter;
    private SubtasksPresenter subtasksPresenter;
    private View activityRootView;
    private MainActivityView mainActivityView;
    public SharedPreferences preferences;

    public TaskAdapter(Context context, MainActivityPresenter mainActivityPresenter,
                       SubtasksPresenter subtasksPresenter, View activityRootView,
                       MainActivityView mainActivityView) {
        this.context = context;
        this.mainActivityPresenter = mainActivityPresenter;
        this.subtasksPresenter = subtasksPresenter;
        this.activityRootView = activityRootView;
        this.mainActivityView = mainActivityView;
        preferences = context.getSharedPreferences("com.violenthoboenterprises.blistful",
                Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskHolder(itemView);
    }

    @Override //Binding data to the view
    public void onBindViewHolder(final TaskHolder holder, final int position) {
        final Task currentTask = tasks.get(position);
        holder.tvTask.setText(currentTask.getTask());
        //checking if any status icons need to be displayed
        holder.noteIcon.setVisibility(View.GONE);
        holder.subtasksIcon.setVisibility(View.GONE);
        holder.repeatIcon.setVisibility(View.GONE);
        holder.dueIcon.setVisibility(View.GONE);
        holder.tvDue.setVisibility(View.GONE);
        holder.taskProperties.setVisibility(View.GONE);
//        MainActivity.boolPropertiesShowing = false;
        holder.tvDue.setTextColor(Color.BLACK);

        //checking if needed to display due icon
        if (currentTask.getTimestamp() != 0) {
            holder.dueIcon.setVisibility(View.VISIBLE);
            long repeatsAdjustedTimestamp;
            if(currentTask.getRepeatInterval() != null) {
                repeatsAdjustedTimestamp = getRepeatsAdjustedTimestamp(currentTask);
            }else{
                repeatsAdjustedTimestamp = currentTask.getDisplayedTimestamp();
            }
            //Switch to overdue icon when appropriate
            if ((repeatsAdjustedTimestamp < Calendar.getInstance().getTimeInMillis())) {
                holder.dueIcon.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.overdue_icon_light));
                holder.tvDue.setTextColor(Color.RED);
            } else {
                holder.dueIcon.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.due_icon_light));
            }
            holder.tvDue.setVisibility(View.VISIBLE);
            String formattedDate = getFormattedDate(currentTask);
            holder.tvDue.setText(formattedDate);
        }

        //checking if needed to display note icon
        if (currentTask.getNote() != null) {
            holder.noteIcon.setVisibility(View.VISIBLE);
        }

        //checking if needed to display subtasks icon
        List<Subtask> subtasks = subtasksPresenter.getSubtasksByParent(currentTask.getId());
        int subtasksSize = subtasks.size();
        if (subtasksSize > 0) {
            holder.subtasksIcon.setVisibility(View.VISIBLE);
        }

        //checking if needed to display repeat icon
        if (currentTask.getRepeatInterval() != null) {
            holder.repeatIcon.setVisibility(View.VISIBLE);
        }

        //show properties on click
        holder.taskLayout.setOnClickListener(view -> {
            //removing any other visible properties
            if (preferences.getInt(StringConstants.REFRESH_THIS_ITEM, 0) != position) {
                notifyItemChanged(preferences.getInt(StringConstants.REFRESH_THIS_ITEM, 0));
            }
            //tracking this item as requiring updating upon return from a child activity
            preferences.edit().putInt(StringConstants.REFRESH_THIS_ITEM, position).apply();
            if (holder.taskProperties.getVisibility() == View.VISIBLE) {
                MainActivity.boolPropertiesShowing = false;
                holder.taskProperties.setVisibility(View.GONE);
                mainActivityPresenter.toggleFab(true);
                //redrawing the UI to remove properties from view
                activityRootView.postInvalidate();
            } else {
                MainActivity.boolPropertiesShowing = true;
                holder.taskProperties.setVisibility(View.VISIBLE);
                mainActivityPresenter.toggleFab(false);
            }
        });

        //rename task on long click
        holder.taskLayout.setOnLongClickListener(view -> {
            mainActivityView.addTask(currentTask);
            return true;
        });

        //show reminder activity
        holder.btnAlarm.setOnClickListener(view -> {
            holder.taskProperties.setVisibility(View.GONE);
            MainActivity.boolPropertiesShowing = false;
            if (mainActivityPresenter.getDuesSet() < 5 || currentTask.getTimestamp() != 0
                            || preferences.getBoolean(StringConstants.REMINDERS_AVAILABLE_KEY, false)) {
                Intent intent = new Intent(context, ReminderActivity.class);
                intent.putExtra("task", currentTask);
                context.startActivity(intent);
            } else {
                mainActivityPresenter.showPurchases();
            }
        });

        //show subtasks activity
        holder.btnSubtasks.setOnClickListener(view -> {
            holder.taskProperties.setVisibility(View.GONE);
            MainActivity.boolPropertiesShowing = false;
            Intent intent = new Intent(context, SubtasksActivity.class);
            intent.putExtra("task", currentTask);
            context.startActivity(intent);
        });

        //show note activity
        holder.btnNote.setOnClickListener(view -> {
            holder.taskProperties.setVisibility(View.GONE);
            MainActivity.boolPropertiesShowing = false;
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("task", currentTask);
            context.startActivity(intent);
        });
    }

    private long getRepeatsAdjustedTimestamp(Task task) {
        //timestamps have been converted from millis to seconds to prevent memory errors
        Calendar currentCal = Calendar.getInstance();
        Calendar displayedCal = Calendar.getInstance();
        displayedCal.setTimeInMillis(task.getDisplayedTimestamp());
        long diff = (currentCal.getTimeInMillis() / 1000) - (displayedCal.getTimeInMillis() / 1000);
        if(task.getRepeatInterval().equals("day")) {
            //Can't have daily repeating task overdue for more than 24 hours
            if (diff > 86400) {
                task.setDisplayedTimestamp(task.getTimestamp() - (1000 * 60 * 60 * 24));
                mainActivityPresenter.update(task);
            }
        }else if(task.getRepeatInterval().equals("week")){
            //Can't have weekly repeating task overdue for more than 7 days
            long threshold = 86400 * 7;
            if (diff > threshold) {
                long amountToSubtract = 1000 * 60 * 60 * 24 * 7;
                task.setDisplayedTimestamp(task.getTimestamp() - amountToSubtract);
                mainActivityPresenter.update(task);
            }
        }else if(task.getRepeatInterval().equals("month")){
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
            if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 0) {
                //if due on 31st day and following month doesn't have 31 days set to last day of following month
                if (originalDay == 31 && month != 7 && month != 0 && month != 1) {
                    multiplier = 30;
                //if due on 31st and following month is February set to last day of February
                } else if (originalDay == 31 && month == 1) {
                    multiplier = 28 + leapYear;
                //if due on 30th and following month is February set to last day of February
                }else if(originalDay == 30 && month == 1) {
                    multiplier = 29 + leapYear;
                //if due on 29th and following month is February set to last day of February
                }else if(originalDay == 29 && month == 1){
                    multiplier = 30 + leapYear;
                } else {
                    multiplier = 31;
                }
            //February
            } else if (month == 2) {
                //if original due day is 31 then set to 31st of following month
                if (originalDay == 31) {
                    multiplier = 31;
                //if original due day is 30 then set to 30th of following month
                } else if(originalDay == 30){
                    multiplier = 30;
                //if original due day is 39 then set to 39th of following month
                } else if(originalDay == 29){
                    multiplier = 29;
                }else{
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
            //getting the amount of time between now and one month ago accounting for
            // end-of-month days and leap years. Needed for updating displayed overdue time
            int daysToMultiply;
            if(month == 0 || month == 5 || month == 7 || month == 10){
                daysToMultiply = 30;
            }else if(month == 2 || month == 4 || month == 6 || month == 8 || month == 9 || month == 11){
                if(originalDay == 31){
                    if(month != 2) {
                        daysToMultiply = 30;
                    }else{
                        daysToMultiply = 28 + leapYear;
                    }
                }else if(originalDay == 30){
                    if(month != 2) {
                        daysToMultiply = 31;
                    }else{
                        daysToMultiply = 29 + leapYear;
                    }
                }else if(originalDay == 29) {
                    if(month != 2){
                        daysToMultiply = 32;
                    }else{
                        daysToMultiply = 30 + leapYear;
                    }
                }else {
                    daysToMultiply = 31;
                }
            }else{
                daysToMultiply = 28;
            }
            //Can't have monthly repeating task overdue for more than a month
            long threshold = 86400 * daysToMultiply;
            if(diff > threshold){
                long amountToSubtract = 1000 * 60;
                amountToSubtract *= 60;
                amountToSubtract *= 24;
                amountToSubtract *= multiplier;
                task.setDisplayedTimestamp(task.getTimestamp() - amountToSubtract);
                mainActivityPresenter.update(task);
            }
        }
        return task.getDisplayedTimestamp();
    }

    private String getFormattedDate(Task task) {
        String formattedMonth = "";
        Calendar cal = Calendar.getInstance();
        long timestamp;
        if(task.getRepeatInterval() != null) {
            timestamp = getRepeatsAdjustedTimestamp(task);
        }else{
            timestamp = task.getDisplayedTimestamp();
        }
        cal.setTimeInMillis(timestamp);
        int intMonth = cal.get(Calendar.MONTH) + 1;

        Calendar currentCal = Calendar.getInstance();

        //Show time if due on same day, show date if due in future
        if (cal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH)
                && cal.get(Calendar.DAY_OF_MONTH) == currentCal.get(Calendar.DAY_OF_MONTH)) {

            String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
            String minute = String.valueOf(cal.get(Calendar.MINUTE));
            String ampm;

            //converting into 12 hour time
            if (Integer.parseInt(hour) == 0) {
                hour = String.valueOf(12);
                ampm = context.getString(R.string.am);
            } else if (Integer.parseInt(hour) == 12) {
                ampm = context.getString(R.string.pm);
            } else if (Integer.parseInt(hour) > 12) {
                hour = String.valueOf(Integer.parseInt(hour) - 12);
                ampm = context.getString(R.string.pm);
            } else {
                ampm = context.getString(R.string.am);
            }

            if (Integer.parseInt(minute) < 10) {
                minute = String.valueOf("0" + minute);
            }

            return hour + ":" + minute + ampm;

        } else {

            //Checking if date due tomorrow
            if (currentCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                if (((currentCal.get(Calendar.MONTH) == 0) || (currentCal.get(Calendar.MONTH) == 2)
                        || (currentCal.get(Calendar.MONTH) == 4) || (currentCal.get(Calendar.MONTH) == 6)
                        || (currentCal.get(Calendar.MONTH) == 7) || (currentCal.get(Calendar.MONTH) == 9))
                        && (currentCal.get(Calendar.DAY_OF_MONTH) == 31) && (cal.get(Calendar.DAY_OF_MONTH) == 1)
                        && (currentCal.get(Calendar.MONTH) == (cal.get(Calendar.MONTH) - 1))) {
                    return context.getString(R.string.tomorrow);
                } else if (((currentCal.get(Calendar.MONTH) == 1) || (currentCal.get(Calendar.MONTH) == 3)
                        || (currentCal.get(Calendar.MONTH) == 5) || (currentCal.get(Calendar.MONTH) == 8)
                        || (currentCal.get(Calendar.MONTH) == 10)) && (currentCal.get(Calendar.DAY_OF_MONTH) == 30)
                        && (cal.get(Calendar.DAY_OF_MONTH) == 1)
                        && (currentCal.get(Calendar.MONTH) == (cal.get(Calendar.MONTH) - 1))) {
                    return context.getString(R.string.tomorrow);
                } else if ((currentCal.get(Calendar.MONTH) == 11) && (currentCal.get(Calendar.DAY_OF_MONTH) == 31)
                        && (cal.get(Calendar.DAY_OF_MONTH) == 1)
                        && (currentCal.get(Calendar.MONTH) == (cal.get(Calendar.MONTH) - 1))) {
                    return context.getString(R.string.tomorrow);
                } else if ((currentCal.get(Calendar.MONTH) == 1) && (currentCal.get(Calendar.DAY_OF_MONTH) == 28)
                        && (currentCal.get(Calendar.YEAR) % 4 != 0) && (cal.get(Calendar.DAY_OF_MONTH) == 1)
                        && (currentCal.get(Calendar.MONTH) == (cal.get(Calendar.MONTH) - 1))) {
                    return context.getString(R.string.tomorrow);
                } else if ((currentCal.get(Calendar.MONTH) == 1) && (currentCal.get(Calendar.DAY_OF_MONTH) == 29)
                        && (currentCal.get(Calendar.YEAR) % 4 == 0) && (cal.get(Calendar.DAY_OF_MONTH) == 1)
                        && (currentCal.get(Calendar.MONTH) == (cal.get(Calendar.MONTH) - 1))) {
                    return context.getString(R.string.tomorrow);
                } else if (currentCal.get(Calendar.DAY_OF_MONTH) == (cal.get(Calendar.DAY_OF_MONTH) - 1) &&
                        currentCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                    return context.getString(R.string.tomorrow);
                }
            }

            //getting string representation for month
            if (intMonth == 1) {
                formattedMonth = context.getString(R.string.jan);
            } else if (intMonth == 2) {
                formattedMonth = context.getString(R.string.feb);
            } else if (intMonth == 3) {
                formattedMonth = context.getString(R.string.mar);
            } else if (intMonth == 4) {
                formattedMonth = context.getString(R.string.apr);
            } else if (intMonth == 5) {
                formattedMonth = context.getString(R.string.may);
            } else if (intMonth == 6) {
                formattedMonth = context.getString(R.string.jun);
            } else if (intMonth == 7) {
                formattedMonth = context.getString(R.string.jul);
            } else if (intMonth == 8) {
                formattedMonth = context.getString(R.string.aug);
            } else if (intMonth == 9) {
                formattedMonth = context.getString(R.string.sep);
            } else if (intMonth == 10) {
                formattedMonth = context.getString(R.string.oct);
            } else if (intMonth == 11) {
                formattedMonth = context.getString(R.string.nov);
            } else if (intMonth == 12) {
                formattedMonth = context.getString(R.string.dec);
            }

            //setting date format based of locale
            String lang = String.valueOf(Locale.getDefault());
            if (lang.equals("en_AS") || lang.equals("en_BM")
                    || lang.equals("en_GU") || lang.equals("en_PH")
                    || lang.equals("en_PR") || lang.equals("en_UM")
                    || lang.equals("en_US") || lang.equals("en_VI")) {
                return formattedMonth + " " + cal.get(Calendar.DAY_OF_MONTH);
            } else {
                return cal.get(Calendar.DAY_OF_MONTH) + " " + formattedMonth;
            }
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    //Building the item view
    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView tvTask;
        private TextView tvDue;
        private ConstraintLayout taskLayout;
        private ConstraintLayout taskProperties;
        private ConstraintLayout btnAlarm;
        private ConstraintLayout btnSubtasks;
        private ConstraintLayout btnNote;
        private ImageView noteIcon;
        private ImageView subtasksIcon;
        private ImageView repeatIcon;
        private ImageView dueIcon;

        TaskHolder(final View itemView) {
            super(itemView);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvDue = itemView.findViewById(R.id.tvDue);
            taskLayout = itemView.findViewById(R.id.taskLayout);
            taskProperties = itemView.findViewById(R.id.viewProperties);
            btnAlarm = itemView.findViewById(R.id.btnAlarm);
            btnSubtasks = itemView.findViewById(R.id.btnSubtasks);
            btnNote = itemView.findViewById(R.id.btnNote);
            noteIcon = itemView.findViewById(R.id.imgNote);
            subtasksIcon = itemView.findViewById(R.id.imgSubtasks);
            repeatIcon = itemView.findViewById(R.id.imgRepeat);
            dueIcon = itemView.findViewById(R.id.imgDue);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(tasks.get(position));
                }
            });
            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(tasks.get(position));
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
