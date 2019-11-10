package com.example.mytasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvMainSpec, lvMainList;
    List<TaskList> mainList, mainSpec;
    Button btn;
    MainListView mainListAdapter;
    MainListView specListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        mainListAdapter = new MainListView(MainActivity.this, R.layout.list_view, mainList);
        specListAdapter = new MainListView(MainActivity.this, R.layout.list_view, mainSpec);
        lvMainList.setAdapter(mainListAdapter);
        lvMainSpec.setAdapter(specListAdapter);
        addEvent();
    }

    private void addEvent() {
        lvMainSpec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(MainActivity.this,ListTaskActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper db = new DbHelper(MainActivity.this);
                TaskList taskList = new TaskList();
                taskList.setmName("ABCDE");
                taskList.setmIcon(R.drawable.list);
                db.insertNewList(taskList);
                mainList.clear();
                List<TaskList> list = db.getAllList();
                mainList.addAll(list);
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addControl() {
        DbHelper db = new DbHelper(this);
        lvMainList = (ListView) findViewById(R.id.lvMainList);
        lvMainSpec = (ListView) findViewById(R.id.lvMainSpec);
        btn = (Button) findViewById(R.id.btnAdd);
        mainList = new ArrayList<>();
        mainSpec = new ArrayList<>();
        List<TaskList> list = db.getAllList();
        mainList.addAll(list);
    }

}
