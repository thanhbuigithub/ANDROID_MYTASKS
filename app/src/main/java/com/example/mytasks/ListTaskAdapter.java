package com.example.mytasks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListTaskAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Task> tasksList;
    private DbHelper db;


    public ListTaskAdapter(Context context, int layout, List<Task> tasksList) {
        this.context = context;
        this.layout = layout;
        this.tasksList = tasksList;
        this.db = new DbHelper(context);
    }

    @Override
    public int getCount() {
        return tasksList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class Viewholder{
        TextView txtTasks;
        CheckBox cbDone;
        CheckBox cbImportant;
        LinearLayout layoutTask;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Viewholder viewholder;

        if(view == null)
        {
            viewholder = new Viewholder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            viewholder.txtTasks = (TextView) view.findViewById(R.id.tvtask);
            viewholder.cbDone = (CheckBox) view.findViewById(R.id.chbDone);
            viewholder.cbImportant = (CheckBox) view.findViewById(R.id.chbImportant);
            viewholder.layoutTask = (LinearLayout) view.findViewById(R.id.layoutTask);

            view.setTag(viewholder);
        }else{
            viewholder = (Viewholder) view.getTag();
        }

        final Task task = tasksList.get(i);
        viewholder.txtTasks.setText(task.getmName());
        viewholder.cbDone.setChecked(task.getmIsDone() == 1);
        viewholder.cbImportant.setChecked(task.getmIsImportant() == 1);
        Log.d("TASK: ", task.getmName() + " " + task.getmIsDone() + " " + task.getmIsImportant());
        if (task.getmIsDone() == 1) {
            viewholder.txtTasks.setTextColor(Color.GRAY);
            viewholder.txtTasks.setPaintFlags(viewholder.txtTasks.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            viewholder.txtTasks.setTextColor(Color.BLACK);
            viewholder.txtTasks.setPaintFlags(viewholder.txtTasks.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (task.getmIsImportant() == 1){
            viewholder.cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
            viewholder.layoutTask.setBackgroundResource(R.drawable.shape_task_isimportant);
        } else {
            viewholder.cbDone.setButtonDrawable(R.drawable.custom_checkbox);
            viewholder.layoutTask.setBackgroundResource(R.drawable.shape_task);
        }

        viewholder.cbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewholder.cbDone.isChecked()) {
                    task.setmIsDone(1);
                    viewholder.txtTasks.setTextColor(Color.GRAY);
                    viewholder.txtTasks.setPaintFlags(viewholder.txtTasks.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    task.setmIsDone(0);
                    viewholder.txtTasks.setTextColor(Color.BLACK);
                    viewholder.txtTasks.setPaintFlags(viewholder.txtTasks.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.updateTask(task);
            }
        });

        viewholder.cbImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewholder.cbImportant.isChecked()){
                    task.setmIsImportant(1);
                    viewholder.cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
                    viewholder.layoutTask.setBackgroundResource(R.drawable.shape_task_isimportant);
                } else {
                    task.setmIsImportant(0);
                    viewholder.cbDone.setButtonDrawable(R.drawable.custom_checkbox);
                    viewholder.layoutTask.setBackgroundResource(R.drawable.shape_task);
                }
                db.updateTask(task);
            }
        });

        return view;
    }
}
