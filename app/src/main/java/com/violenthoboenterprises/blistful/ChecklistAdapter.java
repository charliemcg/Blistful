//package com.violenthoboenterprises.blistful;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//class ChecklistAdapter extends ArrayAdapter<String> {
//
//    String TAG = this.getClass().getSimpleName();
//
//    public ChecklistAdapter(Context context, ArrayList<String> values) {
//
//        super(context, R.layout.checklist_item, values);
//
//    }
//
//    @Override
//    public View getView(final int position, final View convertView, final ViewGroup parent) {
//
//        final String item = getItem(position);
//        final LayoutInflater theInflater = LayoutInflater.from(getContext());
//        final View checklistItemView = theInflater.inflate
//                (R.layout.checklist_item, parent, false);
////        TextView checklistTextView = checklistItemView.findViewById(R.id.checklistTextView);
////        final ImageView tickWhite = checklistItemView.findViewById(R.id.subtaskCompleteWhite);
////        final ImageView tickedWhite = checklistItemView.findViewById(R.id.subtaskCompletedWhite);
////        final ImageView tickWhiteFaded = checklistItemView
////                .findViewById(R.id.subtaskCompleteWhiteFaded);
////        final ImageView tickedWhiteFaded = checklistItemView
////                .findViewById(R.id.subtaskCompletedWhiteFaded);
//
//        String dbTaskId = "";
//
//        //getting app-wide data
//        Cursor dbResult = MainActivity.db.getUniversalData();
//        while (dbResult.moveToNext()) {
//            dbTaskId = dbResult.getString(4);
//        }
//
//        final String finalDbTaskId = dbTaskId;
//
//        //setting up UI based on light or dark mode
////        checklistTextView.setTextColor(Color.parseColor("#000000"));
////        if (SubtasksActivity.fadeSubTasks) {
////            checklistTextView.setTextColor(Color.parseColor("#AAAAAA"));
////            tickWhite.setVisibility(View.GONE);
////            tickWhiteFaded.setVisibility(View.VISIBLE);
////        } else {
////            tickWhiteFaded.setVisibility(View.GONE);
////            tickWhite.setVisibility(View.VISIBLE);
////        }
//
//        //registering that subtask should be marked as done
////        tickWhite.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
//
////                markAsDone(position, finalDbTaskId);
//
////            }
////
////        });
//
////        checklistTextView.setText(item);
//
//        Boolean isKilled = false;
//
//        //finding out if the subtask is completed
////        Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(dbTaskId),
////                SubtasksActivity.sortedSubtaskIds.get(position));
////        while (dbIdResult.moveToNext()) {
////            isKilled = dbIdResult.getInt(3) > 0;
////        }
////        dbIdResult.close();
//
//        //sub task is crossed out if it is marked as done
//        if (isKilled) {
//
////            checklistTextView.setPaintFlags(checklistTextView.getPaintFlags() |
////                    Paint.STRIKE_THRU_TEXT_FLAG);
//
////            if (SubtasksActivity.fadeSubTasks) {
////                tickWhiteFaded.setVisibility(View.GONE);
////                tickedWhiteFaded.setVisibility(View.VISIBLE);
////            } else {
////                tickWhite.setVisibility(View.GONE);
////                tickedWhite.setVisibility(View.VISIBLE);
////            }
//
//        }
//
//        //remove task name from view if it is currently being edited
////        if (position == SubtasksActivity.renameMe && SubtasksActivity.subTaskBeingEdited) {
////            checklistTextView.setText("");
////        }
//
//        //setting sub task name
////        if (SubtasksActivity.subTaskBeingEdited && (/*Integer.parseInt(dbTaskId)*/SubtasksActivity.renameMe ==
////                SubtasksActivity.sortedSubtaskIds.get(position)) &&
////                !SubtasksActivity.goToChecklistAdapter) {
//
////            String oldSubTaskString = SubtasksActivity.checklist.get(SubtasksActivity.renameMe);
//
////            SubtasksActivity.checklistEditText.setText(oldSubTaskString);
//
//            //Focus on edit text so that keyboard does not cover it up
////            SubtasksActivity.checklistEditText.requestFocus();
//
////            SubtasksActivity.checklistEditText.setSelection(SubtasksActivity.checklistEditText
////                    .getText().length());
//
////        }
//
//        return checklistItemView;
//
//    }
//
//    private void markAsDone(int position, String finalDbTaskId) {
//
//        MainActivity.vibrate.vibrate(50);
//
//        if (!MainActivity.mute) {
//            MainActivity.trash.start();
//        }
//
//        Rect screen = new Rect();
//
////        SubtasksActivity.checklistRootView.getWindowVisibleDisplayFrame(screen);
//
////        if (SubtasksActivity.subTasksClickable) {
//
////            boolean isKilled = false;
//
//            //finding out if subtask has been killed
////            Cursor dbIdResult = MainActivity.db.getSubtaskData(Integer.parseInt(finalDbTaskId),
////                    SubtasksActivity.sortedSubtaskIds.get(position));
////            while (dbIdResult.moveToNext()) {
////                isKilled = dbIdResult.getInt(3) > 0;
////            }
////            dbIdResult.close();
//
//            //Marks sub task as complete
////            if (!isKilled) {
////
////                MainActivity.db.updateSubtaskKilled(finalDbTaskId, String.valueOf(SubtasksActivity
////                        .sortedSubtaskIds.get(position)), true);
////                SubtasksActivity.subTasksKilled.set(position, true);
////
////                notifyDataSetChanged();
////
////            }
//
//        }
//
////    }
//
//}
