package com.violenthoboenterprises.blistful.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.violenthoboenterprises.blistful.MainActivity;
import com.violenthoboenterprises.blistful.ReminderActivity;
import com.violenthoboenterprises.blistful.SubtasksActivity;
import com.violenthoboenterprises.blistful.NoteActivity;
import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;
import com.violenthoboenterprises.blistful.view.MainActivityView;

import java.io.Serializable;
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
    private TaskViewModel taskViewModel;
//    public String REFRESH_THIS_ITEM = "refresh_this_item";
    public SharedPreferences preferences;

    public TaskAdapter(Context context, MainActivityPresenter mainActivityPresenter,
                       SubtasksPresenter subtasksPresenter, View activityRootView,
                       MainActivityView mainActivityView, TaskViewModel taskViewModel) {
        this.context = context;
        this.mainActivityPresenter = mainActivityPresenter;
        this.subtasksPresenter = subtasksPresenter;
        this.activityRootView = activityRootView;
        this.mainActivityView = mainActivityView;
        this.taskViewModel = taskViewModel;
        preferences = context.getSharedPreferences("com.violenthoboenterprises.blistful",
                Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
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
        holder.overdueIcon.setVisibility(View.GONE);
        holder.tvDue.setVisibility(View.GONE);
        holder.taskProperties.setVisibility(View.GONE);
        if(currentTask.getTimestamp() != 0){
            if((currentTask.getTimestamp() - Calendar.getInstance().getTimeInMillis()) > 0) {
                holder.dueIcon.setVisibility(View.VISIBLE);
            }else{
                holder.overdueIcon.setVisibility(View.VISIBLE);
            }
            holder.tvDue.setVisibility(View.VISIBLE);
            String formattedDate = getFormattedDate(currentTask.getTimestamp());
            holder.tvDue.setText(formattedDate);
        }
        if (currentTask.getNote() != null) {
            holder.noteIcon.setVisibility(View.VISIBLE);
        }
        //checking for existence of subtasks
        List<Subtask> subtasks = subtasksPresenter.getSubtasksByParent(currentTask.getId());
        int subtasksSize = subtasks.size();
        if (subtasksSize > 0) {
            holder.subtasksIcon.setVisibility(View.VISIBLE);
        }
        if (currentTask.getRepeatInterval() != null) {
            holder.repeatIcon.setVisibility(View.VISIBLE);
        }
//        if (currentTask.getTimestamp() > 0) {
//            holder.dueIcon.setVisibility(View.VISIBLE);
//        }
        //show properties on click
        holder.taskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //removing any other visible properties
                if(preferences.getInt("refresh_this_item", 0) != position) {
                    notifyItemChanged(preferences.getInt("refresh_this_item", 0));
                }
                //tracking this item as requiring updating upon return from a child activity
                preferences.edit().putInt("refresh_this_item", position).apply();
                if (holder.taskProperties.getVisibility() == View.VISIBLE) {
                    holder.taskProperties.setVisibility(View.GONE);
                    //redrawing the UI to remove properties from view
                    activityRootView.postInvalidate();
                } else {
                    holder.taskProperties.setVisibility(View.VISIBLE);
                }
            }
        });
        //rename task on long click
        holder.taskLayout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                mainActivityView.addTask(currentTask);
                return true;
            }
        });
        //buttons should open respective activities
        holder.btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReminderActivity.class);
                intent.putExtra("task", currentTask);
                context.startActivity(intent);
            }
        });
        holder.btnSubtasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubtasksActivity.class);
                intent.putExtra("task", currentTask);
                context.startActivity(intent);
            }
        });
        holder.btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("task", currentTask);
                context.startActivity(intent);
            }
        });
    }

    private String getFormattedDate(long timestamp) {
        String formattedMonth = "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int intMonth = cal.get(Calendar.MONTH) + 1;
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
                || lang.equals("en_CA") || lang.equals("en_GU")
                || lang.equals("en_PH")
                || lang.equals("en_PR") || lang.equals("en_UM")
                || lang.equals("en_US") || lang.equals("en_VI")) {
            return formattedMonth + " " + cal.get(Calendar.DAY_OF_MONTH);
        } else {
            return cal.get(Calendar.DAY_OF_MONTH) + " " + formattedMonth;
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
        private ImageView overdueIcon;

        public TaskHolder(final View itemView) {
            super(itemView);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvDue = itemView.findViewById(R.id.tvDue);
            taskLayout = itemView.findViewById(R.id.taskLayout);
            taskProperties = itemView.findViewById(R.id.properties);
            btnAlarm = itemView.findViewById(R.id.alarm);
            btnSubtasks = itemView.findViewById(R.id.subTasks);
            btnNote = itemView.findViewById(R.id.note);
            noteIcon = itemView.findViewById(R.id.noteClearWhite);
            subtasksIcon = itemView.findViewById(R.id.checklistClearWhite);
            repeatIcon = itemView.findViewById(R.id.repeatClearWhite);
            dueIcon = itemView.findViewById(R.id.dueClearWhite);
            overdueIcon = itemView.findViewById(R.id.overdueClearWhite);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(tasks.get(position));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(tasks.get(position));
                    }
                    return true;
                }
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
