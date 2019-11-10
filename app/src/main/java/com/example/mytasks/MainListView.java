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

import java.util.ArrayList;
import java.util.List;

public class MainListView extends BaseAdapter {

    Context context;
    Integer layout;
    List<TaskList> mainList;

    public MainListView(Context context, Integer layout, List<TaskList> mainList) {
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
        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            convertView = inflater.inflate(layout, parent, false);

            viewholder.tvMainList = (TextView) convertView.findViewById(R.id.tvMainList);
            viewholder.tvMainList.setText(mainList.get(position).getmName());

            viewholder.imgMainList = (ImageView) convertView.findViewById(R.id.imgMainList);
            viewholder.imgMainList.setImageResource(mainList.get(position).getmIcon());

        }
        else{
            viewholder = (Viewholder) convertView.getTag();
        }
        return convertView;
    }
}
