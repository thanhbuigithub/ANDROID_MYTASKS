package com.example.mytasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainListView extends BaseAdapter {

    Context context;
    Integer layout;
    ArrayList<TaskList> mainList;

    public MainListView(Context context, Integer layout, ArrayList<TaskList> mainList) {
        this.context = context;
        this.layout = layout;
        this.mainList = mainList;
    }

    @Override
    public int getCount() {
        return mainList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Viewholder{
        TextView tvMainList;
        ImageView imgMainList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder;

        if(convertView == null)
        {
            viewholder = new Viewholder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            viewholder.tvMainList = (TextView) convertView.findViewById(R.id.tvMainList);
            viewholder.imgMainList = (ImageView) convertView.findViewById(R.id.imgMainList);

            convertView.setTag(viewholder);
        }
        else{
            viewholder = (Viewholder) convertView.getTag();
        }

        final TaskList taskList = mainList.get(position);
        viewholder.tvMainList.setText(taskList.getmName());
        viewholder.imgMainList.setImageResource(taskList.getmIcon());

        return convertView;
    }
}
