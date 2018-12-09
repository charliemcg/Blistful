package com.violenthoboenterprises.blistful.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.violenthoboenterprises.blistful.MainActivity;
import com.violenthoboenterprises.blistful.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Adding tasks to the recycler view
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private final static String TAG = "TaskAdapter";
    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new TaskHolder(itemView);
    }

    @Override //Binding data to the view
    public void onBindViewHolder(final TaskHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.tvTask.setText(currentTask.getTask());
        holder.taskLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(holder.taskProperties.getVisibility() == View.VISIBLE) {
                    holder.taskProperties.setVisibility(View.GONE);
                }else{
                    holder.taskProperties.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position){return tasks.get(position);}

    //Building the item view
    class TaskHolder extends RecyclerView.ViewHolder {
        private TextView tvTask;
        private LinearLayout taskLayout;
        private TableRow taskProperties;
        public TaskHolder(final View itemView) {
            super(itemView);
            tvTask = itemView.findViewById(R.id.tvTask);
            taskLayout = itemView.findViewById(R.id.taskLayout);
            taskProperties = itemView.findViewById(R.id.properties);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(tasks.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener){this.listener = listener;}

}
