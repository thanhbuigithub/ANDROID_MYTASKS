package com.example.mytasks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ListTaskActivity extends AppCompatActivity implements Task_RecyclerViewAdapter.OnTaskListener{
    //    ListView lvListTask;
    RecyclerView recyclerViewTask;
    TaskList list;
    //    ListTaskAdapter listTaskAdapter;
    Task_RecyclerViewAdapter task_recyclerViewAdapter;
    DbHelper db;
    FloatingActionButton fabListTask;
    int listID;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    ArrayList<String> imageThemes;
    ArrayList<Integer> btnimageThemes;
    Integer Theme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        db = new DbHelper(this, MainActivity.mDatabaseUser);
//        lvListTask = (ListView) findViewById(R.id.lvListTask);
        recyclerViewTask = findViewById(R.id.recyclerView_Task);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(ListTaskActivity.this));
        fabListTask = (FloatingActionButton) findViewById(R.id.fabListTask);
        list = new TaskList();
        list.setmName("Danh sách chưa có tiêu đề");

        initActionBar(list.getmName());

        fabListTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddTask();
            }
        });

        if (bundle != null){
            String action = bundle.getString("ACTION");
            listID = bundle.getInt("LIST_ID",0);
            if (action != null)
            {
                DialogAddList();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listID != 0){
            list = db.getList(listID);
        }

//        listTaskAdapter = new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks());
        task_recyclerViewAdapter = new Task_RecyclerViewAdapter(this,R.layout.list_task, list.getmListTasks(), this);

//        lvListTask.setAdapter(listTaskAdapter);
        recyclerViewTask.setAdapter(task_recyclerViewAdapter);

        Toast toast = Toast.makeText(ListTaskActivity.this, String.valueOf(list.getmListTasks().size()),Toast.LENGTH_SHORT );
        toast.show();

        initActionBar(list.getmName());

        addEvent();
    }

    private void addEvent() {

    }

    private void initActionBar(String listName){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        if (listName .equals("Danh sách chưa có tiêu đề")){
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.Collapsing_NoTitleText);
        } else {
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.Collapsing_TitleText);
        }
        collapsingToolbarLayout.setTitle(listName);
        //  getSupportActionBar().setDisplayShowTitleEnabled(false);
        //  TextView customTitle = (TextView) findViewById(R.id.toolbar_lt_txt);
        // customTitle.setText(listName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_tasks, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_list_task_Rename:
                DialogRenameList();
                break;
            case R.id.menu_list_task_Delete:
                DialogDeleteList();
                break;
            case R.id.menu_list_task_ChangeTheme:
                DialogChangeTheme();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogChangeTheme() {
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_theme);

        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_changeTheme);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_changeTheme);

        imageThemes = new ArrayList<>();
        btnimageThemes = new ArrayList<>();
        addBackground();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerViewChangeThemeAdapter adapter = new RecyclerViewChangeThemeAdapter(imageThemes, btnimageThemes,this,Theme);
        RecyclerView recyclerView = dialog.findViewById(R.id.rcv_dialog_changeTheme);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator);
                layout.setBackgroundResource(R.drawable.tokyo);
                Toast.makeText(ListTaskActivity.this, "Lay background", LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void DialogRenameList(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename_list);
        dialog.setCancelable(false);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_rename);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_rename);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_rename);

        txtName.setText(list.getmName());

        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = txtName.getText().toString().trim();
                TaskList taskList = new TaskList();
                taskList.setmName(newName);
                taskList.setmID(listID);
                taskList.setmIcon(R.drawable.list);
                db.updateList(taskList);
                list.setmName(newName);
                initActionBar(list.getmName());
                dialog.dismiss();
            }
        });
        txtName.requestFocus();
        dialog.show();
    }

    private void DialogAddTask(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);
        dialog.setCancelable(false);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_addTask);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addTask);
        final Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addTask);

        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setEnabled(false);
        btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorGrey));

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnSave.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtName.getText().toString().trim().length() == 0){
                    btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorGrey));
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorBlue));
                    btnSave.setEnabled(true);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Task task = new Task();
                            task.setmName(txtName.getText().toString().trim());
                            task.setmIDList(listID);
                            task.setmCreatedTime(DateTimeHelper.FromDateToDb(new Date()));
                            //Set On Database
                            db.insertNewTask(task);
                            list = db.getList(listID);
                            Toast toast = Toast.makeText(ListTaskActivity.this, String.valueOf(list.getmListTasks().size()),Toast.LENGTH_SHORT );
                            toast.show();
                            //lvListTask.setAdapter(new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks()));
                            recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks(),ListTaskActivity.this));
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtName.requestFocus();
        dialog.show();
    }

    private void DialogDeleteList(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_list);

        final TextView txtName = dialog.findViewById(R.id.txtName_dialog_delete);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_delete);
        Button btnDelete = dialog.findViewById(R.id.btnDelete_dialog_delete);

        txtName.setText("\""+list.getmName()+"\""+ " will be permanently deleted.");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteList(list);
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    private void DialogAddList(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_list);
        dialog.setCancelable(false);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_addList);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addList);
        final Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addList);

        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListTaskActivity.this,MainActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnSave.setEnabled(false);
        btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorGrey));

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnSave.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtName.getText().toString().trim().length() == 0){
                    btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorGrey));
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorBlue));
                    btnSave.setEnabled(true);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String listNameAdd = txtName.getText().toString().trim();
                            TaskList taskList = new TaskList();
                            taskList.setmName(listNameAdd);
                            taskList.setmIcon(R.drawable.list);
                            db.insertNewList(taskList);
                            initActionBar(listNameAdd);
                            List<TaskList> mainList = new ArrayList<>();
                            mainList = db.getAllList();
                            listID = mainList.get(mainList.size()-1).getmID();
                            list = db.getList(listID);
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtName.requestFocus();
        dialog.show();
    }

    @Override
    public void OnTaskClick(int position) {
        Intent intent = new Intent(ListTaskActivity.this, TaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("taskID", list.getmListTasks().get(position).getmID());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void addBackground(){
        imageThemes.clear();
        btnimageThemes.clear();

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("Tokyo");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("Paris");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("London");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("Madrid");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("Barcelona");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("Singapore");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("HoChiMinh");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("DaNang");

        btnimageThemes.add(R.drawable.custom_checkbox_isimportant);
        imageThemes.add("LasVegas");
    }
}
