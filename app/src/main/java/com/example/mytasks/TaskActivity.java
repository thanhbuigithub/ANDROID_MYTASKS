package com.example.mytasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.intellij.lang.annotations.RegExp;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kotlin.text.Regex;

public class TaskActivity extends AppCompatActivity {
    DbHelper db;
    Task currentTask;
    TaskList currentTaskList;

    Toolbar toolbar;
    EditText edTaskName;
    CheckBox cbDone;
    CheckBox cbImportant;
    LinearLayout btnAddStep;
    RelativeLayout btnRemind;
    RelativeLayout btnAddDeadline;
    RelativeLayout btnRepeat;
    RelativeLayout btnAttach;
    ImageButton btnDelete;
    EditText edNote;

    TextView txtRemind, txtDeadline, txtRepeat, txtFile, txtCreatedTime;

    Integer taskID = null;

    private static final int NO_REPEAT = 0;
    private static final int DAILY = 1;
    private static final int THIS_WEEK = 2;
    private static final int WEEKLY = 3;
    private static final int MONTHLY = 4;
    private static final int YEARLY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edTaskName = (EditText) findViewById(R.id.edTaskName);
        cbDone = (CheckBox) findViewById(R.id.cbDone);
        cbImportant = (CheckBox) findViewById(R.id.cbImportant);
        btnAddStep = (LinearLayout) findViewById(R.id.btnAddStep);
        btnRemind = (RelativeLayout) findViewById(R.id.btnRemind);
        btnAddDeadline = (RelativeLayout) findViewById(R.id.btnAddDeadline);
        btnRepeat = (RelativeLayout) findViewById(R.id.btnRepeat);
        btnAttach = (RelativeLayout) findViewById(R.id.btnAttach);
        btnDelete = (ImageButton) findViewById(R.id.btnDeleteTask);
        edNote =(EditText) findViewById(R.id.edNote);
        txtRemind = (TextView) findViewById(R.id.txtRemind);
        txtDeadline = (TextView) findViewById(R.id.txtDeadline);
        txtRepeat = (TextView) findViewById(R.id.txtRepeat);
        txtFile = (TextView) findViewById(R.id.txtFile);
        txtCreatedTime = (TextView) findViewById(R.id.txtCreatedTime);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            taskID = bundle.getInt("taskID");
        }
        db = new DbHelper(this);
        currentTask = db.getTask(taskID);
        currentTaskList = db.getList(currentTask.getmIDList());

        setEverythingUp();
        addEventListener();


    }

    public void setEverythingUp() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(currentTaskList.getmName());

        edTaskName.setText(currentTask.getmName());
        if (currentTask.getmIsDone()==1) {
            cbDone.setChecked(true);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            cbDone.setChecked(false);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        cbImportant.setChecked(currentTask.getmIsImportant()==1);
        if (currentTask.getmRemind() != null) txtRemind.setText(formatDateTime(currentTask.getmRemind()));
        if (currentTask.getmDeadline() != null) txtDeadline.setText(formatDateTime(currentTask.getmDeadline()));

//        if (currentTask.getmRepeat() != NO_REPEAT) {
//            switch (currentTask.getmRepeat()) {
//                case DAILY:
//                    txtRepeat.setText("Hàng ngày");
//                    if (!txtDeadline.getText().equals("")) {
//
//                    }
//                    else {
//
//                    }
//
//            }
//        }
    }

    public void addEventListener() {
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isDone) {
                currentTask.setmIsDone((isDone) ? 1 : 0);
                if (isDone) {
                    edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    edTaskName.setPaintFlags(edTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
        cbImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isImportant) {
                currentTask.setmIsImportant((isImportant) ? 1 : 0);
            }
        });
        btnRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(txtRemind);
            }
        });
        btnAddDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(txtDeadline);
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(TaskActivity.this, btnRepeat);
                popup.getMenuInflater().inflate(R.menu.popup_repeat_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        currentTask.setmRepeat(item.getOrder());
                        Toast.makeText(TaskActivity.this, String.valueOf(currentTask.getmRepeat()), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlertDialog();
            }
        });
    }



    private void showDateTimePicker(final TextView textView) {
        new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int day, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                int month = monthNumber + 1;
                String datetime = "" + year + "-" + month + "-" + day + " " + hour24 + ":" + min + ":" + sec;
                textView.setText(formatDateTime(datetime));
                if (textView.getId() == txtRemind.getId())
                currentTask.setmRemind(datetime);
                else currentTask.setmDeadline(datetime);
            }

            @Override
            public void onCancel() {

            }
        })
                .set24HourFormat(true)
                .setDate(Calendar.getInstance())
                .showDialog();
    }

    private void showDeleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Bạn có chắc không ?");
        builder.setMessage(String.format("\"%s\" sẽ bị xóa vĩnh viễn", currentTask.getmName()));
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteTask(currentTask);
                finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currentTask.setmName(edTaskName.getText().toString());
        db.updateTask(currentTask);
    }

    public String formatDateTime(String datetime) {
        if (!datetime.equals("")) {
            String[] temp = datetime.split(" ");
            String[] date = temp[0].split("-");
            String year = date[0];
            String month = date[1];
            if (month.length()==1) month = "0"+month;
            String day = date[2];
            if (day.length()==1) day = "0"+day;
            String [] time = temp[1].split(":");
            String hour = time[0];
            if (hour.length()==1) hour="0"+hour;
            String minute = time[1];
            if (minute.length()==1) minute="0"+minute;
            return String.format("%s:%s %s/%s/%s",hour,minute,day,month,year);
        }
        return "";
    }
}
