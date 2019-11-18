package com.example.mytasks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Task_RecyclerViewAdapter extends RecyclerView.Adapter<Task_ViewHolder> {
    private Context context;
    private int layout;
    private List<Task> tasksList;
    private DbHelper db;
    private OnTaskListener onTaskListener;
    private Task mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public Task_RecyclerViewAdapter(Context context, int layout, List<Task> tasksList, OnTaskListener onTaskListener) {
        this.context = context;
        this.layout = layout;
        this.tasksList = tasksList;
        this.onTaskListener = onTaskListener;
        db = new DbHelper(context, MainActivity.mDatabaseUser);
    }

    @NonNull
    @Override
    public Task_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                layout, parent, false);
        Task_ViewHolder mainHolder = new Task_ViewHolder(mainGroup,onTaskListener) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        return mainHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Task_ViewHolder holder, int position) {
        final Task_ViewHolder mainHolder = holder;
        final Task task = tasksList.get(position);
        mainHolder.tvTask.setText(task.getmName());
        mainHolder.cbDone.setChecked(task.getmIsDone() == 1);
        mainHolder.cbImportant.setChecked(task.getmIsImportant() == 1);
        Log.d("TASK: ", task.getmName() + " " + task.getmIsDone() + " " + task.getmIsImportant());
        if (task.getmIsDone() == 1) {
            mainHolder.tvTask.setTextColor(Color.GRAY);
            mainHolder.tvTask.setPaintFlags(mainHolder.tvTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            mainHolder.tvTask.setTextColor(Color.WHITE);
            mainHolder.tvTask.setPaintFlags(mainHolder.tvTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (task.getmIsImportant() == 1){
            mainHolder.cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
            mainHolder.layoutTask.setBackgroundResource(R.drawable.shape_task_isimportant);
        } else {
            mainHolder.cbDone.setButtonDrawable(R.drawable.custom_checkbox);
            mainHolder.layoutTask.setBackgroundResource(R.drawable.shape_task);
        }

        mainHolder.cbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainHolder.cbDone.isChecked()) {
                    task.setmIsDone(1);
                    mainHolder.tvTask.setTextColor(Color.GRAY);
                    mainHolder.tvTask.setPaintFlags(mainHolder.tvTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    task.setmIsDone(0);
                    mainHolder.tvTask.setTextColor(Color.WHITE);
                    mainHolder.tvTask.setPaintFlags(mainHolder.tvTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.updateTask(task);
            }
        });

        mainHolder.cbImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainHolder.cbImportant.isChecked()){
                    task.setmIsImportant(1);
                    mainHolder.cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
                    mainHolder.layoutTask.setBackgroundResource(R.drawable.shape_task_isimportant);
                } else {
                    task.setmIsImportant(0);
                    mainHolder.cbDone.setButtonDrawable(R.drawable.custom_checkbox);
                    mainHolder.layoutTask.setBackgroundResource(R.drawable.shape_task);
                }
                db.updateTask(task);
            }
        });

//        mainHolder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, TaskActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("taskID", task.getmID());
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (null != tasksList ? tasksList.size() : 0);
    }

    public Context getContext() {
        return context;
    }

    public interface OnTaskListener{
        void OnTaskClick(int position);
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = tasksList.get(position);
        mRecentlyDeletedItemPosition = position;
        tasksList.remove(position);
        db.deleteTask(mRecentlyDeletedItem);
        ListTaskActivity.list = db.getList(ListTaskActivity.list.getmID());
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = ListTaskActivity.layout;
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.show();
    }

    private void undoDelete() {
        tasksList.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        db.insertNewTask(mRecentlyDeletedItem);
        ListTaskActivity.list = db.getList(ListTaskActivity.list.getmID());
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
}
