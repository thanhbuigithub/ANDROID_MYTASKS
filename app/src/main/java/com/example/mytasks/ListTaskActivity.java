package com.example.mytasks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ListTaskActivity extends AppCompatActivity {
    ListView lvListTask;
    TaskList list;
    List<Task> tasksArrayList;
    ListTaskAdapter listTaskAdapter;
    DbHelper db;
    FloatingActionButton fabListTask;
    int listID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);
        db = new DbHelper(this);
        lvListTask = (ListView) findViewById(R.id.lvListTask);
        fabListTask = (FloatingActionButton) findViewById(R.id.fabListTask);
        list = new TaskList();
        list.setmName("Danh sách chưa có tiêu đề");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

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

        listTaskAdapter = new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks());

        lvListTask.setAdapter(listTaskAdapter);

        Toast toast = Toast.makeText(ListTaskActivity.this, String.valueOf(list.getmListTasks().size()),Toast.LENGTH_SHORT );
        toast.show();

        initActionBar(list.getmName());

        addEvent();
    }

    private void addEvent() {
//        lvListTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent;
//                intent = new Intent(ListTaskActivity.this,TaskActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void initActionBar(String listName){
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_list_tasks);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView customTitle = (TextView) findViewById(R.id.toolbar_lt_txt);
        customTitle.setText(listName);
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
            case R.id.menu_list_task_Add:
                DialogAddTask();
                break;
            case R.id.menu_list_task_Delete:
                DialogDeleteList();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogRenameList(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename_list);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_rename);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_rename);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_rename);

        txtName.setText(list.getmName());

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

        dialog.show();
    }

    private void DialogAddTask(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_addTask);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addTask);
        final Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addTask);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setEnabled(false);

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnSave.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtName.getText().toString().trim().length() == 0){
                    btnSave.setEnabled(false);
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Task task = new Task();
                            task.setmName(txtName.getText().toString().trim());
                            task.setmIsImportant(0);
                            task.setmIsDone(0);
                            task.setmIDList(listID);
                            //Set On Database
                            db.insertNewTask(task);
                            list = db.getList(listID);
                            Toast toast = Toast.makeText(ListTaskActivity.this, String.valueOf(list.getmListTasks().size()),Toast.LENGTH_SHORT );
                            toast.show();
                            lvListTask.setAdapter(new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks()));
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addList);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addList);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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

        dialog.show();
    }
}
