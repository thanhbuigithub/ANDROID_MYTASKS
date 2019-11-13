package com.example.mytasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvMainSpec, lvMainList;
    ArrayList<TaskList> mainList, mainSpec;
    FloatingActionButton btnAdd;
    MainListView mainListAdapter;
    MainListView specListAdapter;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        mainListAdapter = new MainListView(this, R.layout.list_view, mainList);
        specListAdapter = new MainListView(this, R.layout.list_view, mainSpec);
        lvMainList.setAdapter(mainListAdapter);
        lvMainSpec.setAdapter(specListAdapter);

        List<TaskList> list = db.getAllList();
        mainList.addAll(list);

        addEvent();
        getDataFromDbToMainList();
    }
    private void getDataFromDbToMainList()
    {
        mainList.clear();
        List<TaskList> list = db.getAllList();
        mainList.addAll(list);
        mainListAdapter.notifyDataSetChanged();
    }
    private void addEvent() {
        lvMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(MainActivity.this,ListTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("LIST_ID", mainList.get(position).getmID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,ListTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ACTION", "ADD");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void addControl() {
        db = new DbHelper(this);
        lvMainList = (ListView) findViewById(R.id.lvMainList);
        lvMainSpec = (ListView) findViewById(R.id.lvMainSpec);
        btnAdd = (FloatingActionButton) findViewById(R.id.fabMainList);
        mainList = new ArrayList<>();
        mainSpec = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDbToMainList();
        mainListAdapter.notifyDataSetChanged();
    }
}
