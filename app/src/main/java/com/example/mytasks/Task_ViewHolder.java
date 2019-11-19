package com.example.mytasks;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Task_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView tvTask, tvDeadline;
    public CheckBox cbDone, cbImportant;
    public LinearLayout layoutTask;
    Task_RecyclerViewAdapter.OnTaskListener onTaskListener;
    public Task_ViewHolder(@NonNull View itemView, Task_RecyclerViewAdapter.OnTaskListener onTaskListener) {
        super(itemView);
        this.tvTask = (TextView) itemView.findViewById(R.id.tvtask);
        this.tvDeadline = (TextView) itemView.findViewById(R.id.tvdeadline);
        this.cbDone = (CheckBox) itemView.findViewById(R.id.chbDone);
        this.cbImportant = (CheckBox) itemView.findViewById(R.id.chbImportant);
        this.layoutTask = (LinearLayout) itemView.findViewById(R.id.layoutTask);
        this.onTaskListener = onTaskListener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onTaskListener.OnTaskClick(getAdapterPosition());
    }
}
