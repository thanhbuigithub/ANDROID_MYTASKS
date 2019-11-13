package com.example.mytasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class TaskActivity extends AppCompatActivity {
    DbHelper db;
    Task currentTask;
    TaskList currentTaskList;


    Toolbar toolbar;
    TextView tvTaskName;
    CheckBox cbDone;
    CheckBox cbImportant;
    LinearLayout btnAddStep;
    LinearLayout btnRemind;
    LinearLayout btnAddDeadline;
    LinearLayout btnRepeat;
    LinearLayout btnAttach;
    LinearLayout btnDelete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTaskName = (TextView) findViewById(R.id.tvTaskName);
        cbDone = (CheckBox) findViewById(R.id.cbDone);
        cbImportant = (CheckBox) findViewById(R.id.cbImportant);
        btnAddStep = (LinearLayout) findViewById(R.id.btnAddStep);
        btnRemind = (LinearLayout) findViewById(R.id.btnRemind);
        btnAddDeadline = (LinearLayout) findViewById(R.id.btnAddDeadline);
        btnRepeat = (LinearLayout) findViewById(R.id.btnRepeat);
        btnAttach = (LinearLayout) findViewById(R.id.btnAttach);
        btnDelete = (LinearLayout) findViewById(R.id.btnDeleteTask);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Integer taskID = null;
        if (bundle != null) {
            taskID = bundle.getInt("taskID");
        }
        db = new DbHelper(this);
        currentTask = db.getTask(taskID);
        currentTaskList = db.getList(currentTask.getmIDList());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(currentTaskList.getmName());


        tvTaskName.setText(currentTask.getmName());
        if (currentTask.getmIsDone()==1) {
            cbDone.setChecked(true);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            cbDone.setChecked(false);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        cbImportant.setChecked(currentTask.getmIsImportant()==1);

        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isDone) {
                currentTask.setmIsDone((isDone) ? 1 : 0);
                //Toast.makeText(TaskActivity.this, String.valueOf(currentTask.getmIsDone()), Toast.LENGTH_SHORT).show();
                if (isDone) {
                    tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.updateTask(currentTask);
            }
        });

        cbImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isImportant) {
                currentTask.setmIsImportant((isImportant) ? 1 : 0);
                db.updateTask(currentTask);
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlertDialog();
            }
        });
    }

    private void showDeleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Bạn có chắc không ?");
        builder.setMessage(String.format("\"%s\" sẽ bị xóa vĩnh viễn", currentTask.getmName()));
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(TaskActivity.this, "abc", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog deleteAlertDialog = builder.create();
        deleteAlertDialog.show();
        deleteAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
