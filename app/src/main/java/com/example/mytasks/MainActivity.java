package com.example.mytasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListView eplvImportant, eplv;
    List<String> listList;
    HashMap<String,List<Tasks>> listTask;

    CustomExpendableListView customExpendableListView;
    ExpendableListView_List expendableListView_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        customExpendableListView = new CustomExpendableListView(MainActivity.this, listList,listTask);
        expendableListView_list = new ExpendableListView_List(MainActivity.this, listList,listTask);
        eplvImportant.setAdapter(customExpendableListView);
        eplv.setAdapter(expendableListView_list);
        addEvent();
    }

    private void addEvent() {
        for(int i=0;i<listList.size();i++)
        {
            eplvImportant.expandGroup(i);
        }

        eplvImportant.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                eplvImportant.expandGroup(groupPosition);
                return true;
            }
        });

    }

    private void addControl() {
        eplvImportant = (ExpandableListView) findViewById(R.id.eplvImportant);
        eplv = (ExpandableListView) findViewById(R.id.eplv);
        listList = new ArrayList<>();
        listTask = new HashMap<String,List<Tasks>>();

        listList.add("Danh sách 1");
        listList.add("Danh sách 2");

        List<Tasks> danhsach1 = new ArrayList<Tasks>();
        danhsach1.add(new Tasks("Công việc 1"));
        danhsach1.add(new Tasks("Công việc 2"));
        danhsach1.add(new Tasks("Công việc 3"));

        List<Tasks> danhsach2 = new ArrayList<Tasks>();
        danhsach2.add(new Tasks("Công việc 1"));
        danhsach2.add(new Tasks("Công việc 2"));
        danhsach2.add(new Tasks("Công việc 3"));

        listTask.put(listList.get(0),danhsach1);
        listTask.put(listList.get(1),danhsach2);
    }

}
