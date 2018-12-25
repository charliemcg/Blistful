package com.violenthoboenterprises.blistful.model;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.view.SubtasksView;

import java.util.ArrayList;
import java.util.List;

/*
 * Adding subtasks to the recycler view
 */
public class SubtasksAdapter extends RecyclerView.Adapter<SubtasksAdapter.SubtaskHolder>{

    private final static String TAG = "SubtasksAdapter";
    private List<Subtask> subtasks = new ArrayList<>();
    private SubtasksAdapter.OnItemClickListener listener;
    private SubtasksView subtaskView;

    public SubtasksAdapter(SubtasksView subtaskView) {
        this.subtaskView = subtaskView;
    }

    @Override
    public SubtasksAdapter.SubtaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subtask, parent, false);
        return new SubtasksAdapter.SubtaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubtasksAdapter.SubtaskHolder holder, int position) {
        //setting the subtask name in the item
        final Subtask currentSubtask = subtasks.get(position);
        holder.tvSubtask.setText(currentSubtask.getSubtask());
        //rename subtask on long click
        holder.subtaskLayout.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                subtaskView.editSubtask(currentSubtask);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    public void setSubtasks(List<Subtask> subtasks){
        this.subtasks = subtasks;
        notifyDataSetChanged();
    }

    public Subtask getSubtaskAt(int position){return subtasks.get(position);}

    //Building the item view
    class SubtaskHolder extends RecyclerView.ViewHolder {
        private TextView tvSubtask;
        private ConstraintLayout subtaskLayout;
        public SubtaskHolder(final View itemView) {
            super(itemView);
            tvSubtask = itemView.findViewById(R.id.tvSubtask);
            subtaskLayout = itemView.findViewById(R.id.subtask_layout);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(subtasks.get(position));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(subtasks.get(position));
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Subtask subtask);
    }

    public void setOnItemClickListener(SubtasksAdapter.OnItemClickListener listener){this.listener = listener;}
}
