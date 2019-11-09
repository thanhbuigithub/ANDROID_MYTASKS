package com.example.mytasks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpendableListView_List extends BaseExpandableListAdapter {
    MainActivity context;
    List<String> listList;
    HashMap<String,List<Tasks>> listTask;

    public ExpendableListView_List(MainActivity context, List<String> listList, HashMap<String, List<Tasks>> listTask) {
        this.context = context;
        this.listList = listList;
        this.listTask = listTask;
    }

    @Override
    public int getGroupCount() {
        return listList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listTask.get(listList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listTask.get(listList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listName = (String) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_view,null);
        TextView tvList = (TextView) convertView.findViewById(R.id.tvlist2);
        ImageView imgList = (ImageView) convertView.findViewById(R.id.imgListView);
        tvList.setText(listName);

        tvList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context,ListTaskActivity.class);

                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Tasks taskName = (Tasks) getChild(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_task,null);
        TextView tvTask = (TextView) convertView.findViewById(R.id.tvtask);
        tvTask.setText(taskName.mName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
