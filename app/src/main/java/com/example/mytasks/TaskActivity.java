package com.example.mytasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskActivity extends AppCompatActivity {
    DbHelper db;
    Task currentTask;
    TaskList currentTaskList;

    Toolbar toolbar;
    EditText edTaskName;
    CheckBox cbDone;
    CheckBox cbImportant;
    RelativeLayout btnRemind;
    RelativeLayout btnAddDeadline;
    RelativeLayout btnRepeat;
    ImageButton btnDelete;
    EditText edNote;
    Button btnCancelRemind, btnCancelRepeat, btnCancelDeadline;


    TextView txtRemind, txtDeadline, txtRepeat, txtCreatedTime;

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
        //btnAddStep = (LinearLayout) findViewById(R.id.btnAddStep);
        btnRemind = (RelativeLayout) findViewById(R.id.btnRemind);
        btnAddDeadline = (RelativeLayout) findViewById(R.id.btnAddDeadline);
        btnRepeat = (RelativeLayout) findViewById(R.id.btnRepeat);
        //btnAttach = (RelativeLayout) findViewById(R.id.btnAttach);
        btnDelete = (ImageButton) findViewById(R.id.btnDeleteTask);
        edNote = (EditText) findViewById(R.id.edNote);
        txtRemind = (TextView) findViewById(R.id.txtRemind);
        txtDeadline = (TextView) findViewById(R.id.txtDeadline);
        txtRepeat = (TextView) findViewById(R.id.txtRepeat);
        //txtFile = (TextView) findViewById(R.id.txtFile);
        txtCreatedTime = (TextView) findViewById(R.id.txtCreatedTime);
        btnCancelRemind = (Button) findViewById(R.id.btnCancelRemind);
        btnCancelDeadline = (Button) findViewById(R.id.btnCancelDeadline);
        btnCancelRepeat = (Button) findViewById(R.id.btnCancelRepeat);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            taskID = bundle.getInt("taskID");
        }
        db = new DbHelper(this);
        currentTask = db.getTask(taskID);
        currentTaskList = db.getList(currentTask.getmIDList());


    }

    @Override
    protected void onStart() {
        super.onStart();
        setEverythingUp();
        addEventListener();
    }

    public void setEverythingUp() {
        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(currentTaskList.getmName());

        //Task name
        edTaskName.setText(currentTask.getmName());

        //Checkbox Done
        if (currentTask.getmIsDone() == 1) {
            cbDone.setChecked(true);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            cbDone.setChecked(false);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        //checkbox Important
        cbImportant.setChecked(currentTask.getmIsImportant() == 1);

        //Remind
        if (currentTask.getmRemind() != null && !currentTask.getmRemind().equals("")) {
            txtRemind.setText("Nhắc tôi lúc " + Datetime_FromDbToDisplay(currentTask.getmRemind()));
            onDateSetted(txtRemind, btnCancelRemind);
        }

        //Deadline
        if (currentTask.getmDeadline() != null && !currentTask.getmDeadline().equals("")) {
            txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(currentTask.getmDeadline()));
            onDateSetted(txtDeadline, btnCancelDeadline);
        }

        //Note
        if (currentTask.getmNote() != null && !currentTask.getmNote().equals("")) {
            edNote.setText(currentTask.getmNote());
        }


        if (currentTask.getmRepeat() != NO_REPEAT && !currentTask.getmDeadline().equals("")) {
            String currentDeadline_String = currentTask.getmDeadline();
            Date currentDeadline_Date = Datetime_FromDbToNewDate(currentDeadline_String);
            String newDeadline_String;
            Date newDeadline_Date;
            switch (currentTask.getmRepeat()) {
                case DAILY:
                    txtRepeat.setText("Lặp lại hàng ngày");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (Datetime_DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = Datetime_Add(currentDeadline_Date, Calendar.DATE, 1);
                        newDeadline_String = Datetime_FromNewDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case THIS_WEEK:
                    txtRepeat.setText("Lặp lại trong tuần");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (Datetime_DatePassed(currentDeadline_Date)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(currentDeadline_Date);
                    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && Datetime_DatePassed(c.getTime())) {
                        c.add(Calendar.DATE, 1);
                    }
                    if (!Datetime_DatePassed(c.getTime())) {
                        newDeadline_Date = c.getTime();
                        newDeadline_String = Datetime_FromNewDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(newDeadline_String));
                    }
                }

                    break;
                case WEEKLY:
                    txtRepeat.setText("Lặp lại hàng tuần");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (Datetime_DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = Datetime_Add(currentDeadline_Date, Calendar.DATE, 7);
                        newDeadline_String = Datetime_FromNewDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case MONTHLY:
                    txtRepeat.setText("Lặp lại hàng tháng");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (Datetime_DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = Datetime_Add(currentDeadline_Date, Calendar.MONTH, 1);
                        newDeadline_String = Datetime_FromNewDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case YEARLY:
                    txtRepeat.setText("Lặp lại hàng năm");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (Datetime_DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = Datetime_Add(currentDeadline_Date, Calendar.YEAR, 1);
                        newDeadline_String = Datetime_FromNewDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(newDeadline_String));
                    }

                    break;
            }
        }
    }

    public void addEventListener() {
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isDone) {
                currentTask.setmIsDone((isDone) ? 1 : 0);
                if (isDone) {
                    edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
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
                        switch (currentTask.getmRepeat()) {
                            case DAILY:
                                txtRepeat.setText("Lặp lại hàng ngày");
                                onDateSetted(txtRepeat, btnCancelRepeat);
                                break;
                            case THIS_WEEK:
                                txtRepeat.setText("Lặp lại trong tuần");
                                onDateSetted(txtRepeat, btnCancelRepeat);
                                break;
                            case WEEKLY:
                                txtRepeat.setText("Lặp lại hàng tuần");
                                onDateSetted(txtRepeat, btnCancelRepeat);
                                break;
                            case MONTHLY:
                                txtRepeat.setText("Lặp lại hàng tháng");
                                onDateSetted(txtRepeat, btnCancelRepeat);
                                break;
                            case YEARLY:
                                txtRepeat.setText("Lặp lại hàng năm");
                                onDateSetted(txtRepeat, btnCancelRepeat);
                                break;
                        }
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

        btnCancelRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRemind.setText("Nhắc tôi");
                onCancelDateSetted(txtRemind, btnCancelRemind);
                currentTask.setmRemind("");
            }
        });

        btnCancelDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDeadline.setText("Thêm ngày đến hạn");
                onCancelDateSetted(txtDeadline, btnCancelDeadline);
                currentTask.setmDeadline("");
                txtRepeat.setText("Lặp lại");
                onCancelDateSetted(txtRepeat, btnCancelRepeat);
                currentTask.setmRepeat(0);
            }
        });

        btnCancelRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRepeat.setText("Lặp lại");
                onCancelDateSetted(txtRepeat, btnCancelRepeat);
                currentTask.setmRepeat(NO_REPEAT);
            }
        });

    }


    private void showDateTimePicker(final TextView textView) {
        new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int day, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                int month = monthNumber + 1;
                String datetime = "" + year + "-" + month + "-" + day + " " + hour24 + ":" + min + ":" + sec;
                if (textView.getId() == txtRemind.getId()) {
                    currentTask.setmRemind(datetime);
                    txtRemind.setText("Nhắc tôi lúc " + Datetime_FromDbToDisplay(datetime));
                    onDateSetted(txtRemind, btnCancelRemind);
                } else {
                    currentTask.setmDeadline(datetime);
                    txtDeadline.setText("Đến hạn lúc " + Datetime_FromDbToDisplay(datetime));
                    onDateSetted(txtDeadline, btnCancelDeadline);
                }
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
        currentTask.setmNote(edNote.getText().toString());
        db.updateTask(currentTask);
    }

    public String Datetime_FromDbToDisplay(String datetime) {
        if (!datetime.equals("")) {
            String[] temp = datetime.split(" ");
            String[] date = temp[0].split("-");
            String year = date[0];
            String month = date[1];
            if (month.length() == 1) month = "0" + month;
            String day = date[2];
            if (day.length() == 1) day = "0" + day;
            String[] time = temp[1].split(":");
            String hour = time[0];
            if (hour.length() == 1) hour = "0" + hour;
            String minute = time[1];
            if (minute.length() == 1) minute = "0" + minute;
            return String.format("%s:%s ngày %s/%s/%s", hour, minute, day, month, year);
        }
        return "";
    }


    public String Datetime_FromNewDateToDb(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public Date Datetime_FromDbToNewDate(String dateFromDb) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateFromDb);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date Datetime_Add(Date date, int type, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        while ((new Date().getTime() - c.getTime().getTime()) > 0) {
            c.add(type, offset);
        }
        return c.getTime();
    }

    public Boolean Datetime_DatePassed(Date date) {
        return (new Date().getTime() - date.getTime()) > 0;
    }


    public void onDateSetted(TextView textView, Button btnCancel) {
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(14);
        btnCancel.setVisibility(View.VISIBLE);
    }

    public void onCancelDateSetted(TextView textView, Button btnCancel) {
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        btnCancel.setVisibility(View.INVISIBLE);
    }
}
