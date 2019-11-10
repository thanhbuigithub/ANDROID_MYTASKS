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
    private List<Tasks> tasksList;

    public ListTaskAdapter(Context context, int layout, List<Tasks> tasksList) {
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
        TextView txtTaks;
        CheckBox cbDone;
        CheckBox cbImportant;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Viewholder viewholder;

        if(view != null)
        {
            viewholder = new Viewholder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            viewholder.txtTaks = (TextView) view.findViewById(R.id.tvtask);
            viewholder.cbDone = (CheckBox) view.findViewById(R.id.chbDone);
            viewholder.cbImportant = (CheckBox) view.findViewById(R.id.chbImportant);

        }else{
            viewholder = (Viewholder) view.getTag();
        }
        return view;
    }
}
