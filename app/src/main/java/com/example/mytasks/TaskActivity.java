package com.example.mytasks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
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

import com.thefuntasty.hauler.DragDirection;
import com.thefuntasty.hauler.HaulerView;

import java.util.Calendar;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class TaskActivity extends AppCompatActivity{
    DbHelper db;
    Task currentTask;
    TaskList currentTaskList;
    String mDbUser;

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

    HaulerView haulerView;

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

        haulerView = (HaulerView) findViewById(R.id.haulerView);
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

        haulerView.setOnDragDismissedListener(new Function1<DragDirection, Unit>() {
            @Override
            public Unit invoke(DragDirection dragDirection) {
                finish();
                return null;
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            taskID = bundle.getInt("taskID");
            mDbUser = bundle.getString("mUser", MainActivity.mDatabaseUser);
        }

        db = new DbHelper(this, mDbUser);
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

        //Done
        if (currentTask.getmIsDone() == 1) {
            cbDone.setChecked(true);
            edTaskName.setTextColor(Color.GRAY);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            cbDone.setChecked(false);
            edTaskName.setTextColor(Color.WHITE);
            edTaskName.setPaintFlags(edTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        //Important
        cbImportant.setChecked(currentTask.getmIsImportant() == 1);
        if (currentTask.getmIsImportant() == 1){
            cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
        } else {
            cbDone.setButtonDrawable(R.drawable.custom_checkbox);
        }

        //Remind
        if (currentTask.getmRemind() != null && !currentTask.getmRemind().equals("")) {
            txtRemind.setText("Nhắc tôi lúc " + DateTimeHelper.FromDbToDisplay(currentTask.getmRemind()));
            onDateSetted(txtRemind, btnCancelRemind);
        }

        //Deadline
        if (currentTask.getmDeadline() != null && !currentTask.getmDeadline().equals("")) {
            txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(currentTask.getmDeadline()));
            onDateSetted(txtDeadline, btnCancelDeadline);
        }

        //Note
        if (currentTask.getmNote() != null && !currentTask.getmNote().equals("")) {
            edNote.setText(currentTask.getmNote());
        }

        //Repeat
        if (currentTask.getmRepeat() != NO_REPEAT && !currentTask.getmDeadline().equals("")) {
            String currentDeadline_String = currentTask.getmDeadline();
            Date currentDeadline_Date = DateTimeHelper.FromDbToDate(currentDeadline_String);
            String newDeadline_String;
            Date newDeadline_Date;
            switch (currentTask.getmRepeat()) {
                case DAILY:
                    txtRepeat.setText("Lặp lại hàng ngày");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (DateTimeHelper.DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = DateTimeHelper.AddDate(currentDeadline_Date, Calendar.DATE, 1);
                        newDeadline_String = DateTimeHelper.FromDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case THIS_WEEK:
                    txtRepeat.setText("Lặp lại trong tuần");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (DateTimeHelper.DatePassed(currentDeadline_Date)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(currentDeadline_Date);
                    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && DateTimeHelper.DatePassed(c.getTime())) {
                        c.add(Calendar.DATE, 1);
                    }
                    if (!DateTimeHelper.DatePassed(c.getTime())) {
                        newDeadline_Date = c.getTime();
                        newDeadline_String = DateTimeHelper.FromDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(newDeadline_String));
                    }
                }

                    break;
                case WEEKLY:
                    txtRepeat.setText("Lặp lại hàng tuần");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (DateTimeHelper.DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = DateTimeHelper.AddDate(currentDeadline_Date, Calendar.DATE, 7);
                        newDeadline_String = DateTimeHelper.FromDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case MONTHLY:
                    txtRepeat.setText("Lặp lại hàng tháng");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (DateTimeHelper.DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = DateTimeHelper.AddDate(currentDeadline_Date, Calendar.MONTH, 1);
                        newDeadline_String = DateTimeHelper.FromDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(newDeadline_String));
                    }

                    break;
                case YEARLY:
                    txtRepeat.setText("Lặp lại hàng năm");
                    onDateSetted(txtRepeat, btnCancelRepeat);

                    if (DateTimeHelper.DatePassed(currentDeadline_Date)) {
                        newDeadline_Date = DateTimeHelper.AddDate(currentDeadline_Date, Calendar.YEAR, 1);
                        newDeadline_String = DateTimeHelper.FromDateToDb(newDeadline_Date);
                        currentTask.setmDeadline(newDeadline_String);
                        txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(newDeadline_String));
                    }

                    break;
            }
        }

        onDeadlineReset();

        //Created Time
        if (!currentTask.getmCreatedTime().equals(""))
            txtCreatedTime.setText("Đã tạo lúc " + DateTimeHelper.FromDbToDisplay(currentTask.getmCreatedTime()));
    }

    public void addEventListener() {
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isDone) {
                currentTask.setmIsDone((isDone) ? 1 : 0);
                if (isDone) {
                    edTaskName.setTextColor(Color.GRAY);
                    edTaskName.setPaintFlags(edTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    edTaskName.setTextColor(Color.WHITE);
                    edTaskName.setPaintFlags(edTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.updateTask(currentTask);
            }
        });
        cbImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isImportant) {
                currentTask.setmIsImportant((isImportant) ? 1 : 0);
                if (isImportant) {
                    cbDone.setButtonDrawable(R.drawable.custom_checkbox_isimportant);
                } else {
                    cbDone.setButtonDrawable(R.drawable.custom_checkbox);
                }
                db.updateTask(currentTask);
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
                    txtRemind.setText("Nhắc tôi lúc " + DateTimeHelper.FromDbToDisplay(datetime));
                    onDateSetted(txtRemind, btnCancelRemind);
                    startAlarm(dateSelected);
                } else {
                    currentTask.setmDeadline(datetime);
                    txtDeadline.setText("Đến hạn lúc " + DateTimeHelper.FromDbToDisplay(datetime));
                    onDateSetted(txtDeadline, btnCancelDeadline);
                    onDeadlineReset();
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


    public void onDateSetted(TextView textView, Button btnCancel) {
        textView.setTextColor(getResources().getColor(R.color.colorBlueLight));
        textView.setTextSize(14);
        btnCancel.setVisibility(View.VISIBLE);
    }

    public void onCancelDateSetted(TextView textView, Button btnCancel) {
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        btnCancel.setVisibility(View.INVISIBLE);
    }

    public void onDeadlineReset() {
        if (!currentTask.getmDeadline().equals("") && DateTimeHelper.DatePassed(DateTimeHelper.FromDbToDate(currentTask.getmDeadline()))) {
            txtDeadline.setTextColor(Color.RED);
        }
    }

    private void startAlarm(Date target) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        // SET TIME HERE
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(target);
        calendar.set(Calendar.SECOND,0);

        int code = Integer.parseInt(currentTask.getmIDList().toString() + currentTask.getmID().toString());


        myIntent = new Intent(TaskActivity.this, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        bundle.putInt("taskID",currentTask.getmID());
        bundle.putString("mUser", MainActivity.mDatabaseUser);
        myIntent.putExtras(bundle);
        pendingIntent = PendingIntent.getBroadcast(this, code, myIntent,PendingIntent.FLAG_ONE_SHOT);

        manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
