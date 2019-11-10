package com.example.mytasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ListTaskAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Task> tasksList;

    public ListTaskAdapter(Context context, int layout, List<Task> tasksList) {
        this.context = context;
        this.layout = layout;
        this.tasksList = tasksList;
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
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Viewholder viewholder;

        if(view == null)
        {
            viewholder = new Viewholder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            viewholder.txtTasks = (TextView) view.findViewById(R.id.tvtask);
            viewholder.cbDone = (CheckBox) view.findViewById(R.id.chbDone);
            viewholder.cbImportant = (CheckBox) view.findViewById(R.id.chbImportant);

            view.setTag(viewholder);
        }else{
            viewholder = (Viewholder) view.getTag();
        }

        final Task tasks = tasksList.get(i);

        viewholder.txtTasks.setText(tasks.getmName());

        return view;
    }
}
