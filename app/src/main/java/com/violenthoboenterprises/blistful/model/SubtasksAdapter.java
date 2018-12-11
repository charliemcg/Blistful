package com.violenthoboenterprises.blistful.model;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.violenthoboenterprises.blistful.R;
import com.violenthoboenterprises.blistful.SubtasksActivity;
import com.violenthoboenterprises.blistful.presenter.MainActivityPresenter;
import com.violenthoboenterprises.blistful.presenter.SubtasksPresenter;

import java.util.ArrayList;
import java.util.List;

/*
 * Adding subtasks to the recycler view
 */
public class SubtasksAdapter extends RecyclerView.Adapter<SubtasksAdapter.SubtaskHolder>{

    private final static String TAG = "SubtasksAdapter";
    private List<Subtask> subtasks = new ArrayList<>();
    private SubtasksAdapter.OnItemClickListener listener;

    @Override
    public SubtasksAdapter.SubtaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checklist_item, parent, false);
        return new SubtasksAdapter.SubtaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubtasksAdapter.SubtaskHolder holder, int position) {
        //setting the subtask name in the item
        Subtask currentSubtask = subtasks.get(position);
        holder.tvSubtask.setText(currentSubtask.getSubtask());
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
        public SubtaskHolder(final View itemView) {
            super(itemView);
            tvSubtask = itemView.findViewById(R.id.tvSubtask);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(subtasks.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Subtask subtask);
    }

    public void setOnItemClickListener(SubtasksAdapter.OnItemClickListener listener){this.listener = listener;}
}
