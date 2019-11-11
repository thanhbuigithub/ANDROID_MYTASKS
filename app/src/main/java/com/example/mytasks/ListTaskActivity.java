package com.example.mytasks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ListTaskActivity extends AppCompatActivity {
    ListView lvListTask;
    ArrayList<Task> tasksArrayList;
    ListTaskAdapter listTaskAdapter;
    DbHelper db;
    ArrayList<TaskList> mainList = new ArrayList<>();
    //Test case
    static String listName;
    int listID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);
        db = new DbHelper(this);
        getDataFromDbToMainList();
        lvListTask = (ListView) findViewById(R.id.lvListTask);
        listName = "Danh sách chưa có tiêu đề";
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        initActionBar(listName);

        if (bundle != null){
            String action = bundle.getString("ACTION");
            String name = bundle.getString("LIST_NAME");
            listID = bundle.getInt("LIST_ID");
            if (name != null)
            {
                listName = name;
                initActionBar(listName);
            }
            if (action != null)
            {
                DialogAddList();
            }
        }

        tasksArrayList = new ArrayList<>();
        listTaskAdapter = new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,tasksArrayList);

        lvListTask.setAdapter(listTaskAdapter);

        for (int i = 0; i < mainList.size();i++)
        {
            if (mainList.get(i).getmID() == listID)
            {
                Toast toast = Toast.makeText(this, String.valueOf(listID), Toast.LENGTH_SHORT);
                toast.show();
                tasksArrayList.clear();
                tasksArrayList.addAll(mainList.get(i).getmListTasks());
            }
        }
        listTaskAdapter.notifyDataSetChanged();

        addEvent();
        //GetDataListTask();
    }

    private void addEvent() {
    }

//    private void GetDataListTask()
//    {
//        tasksArrayList.clear();
//        Cursor dataCongViec = database.GetData("SELECT * FROM ListTask");
//        while(dataCongViec.moveToNext())
//        {
//            String ten = dataCongViec.getString(1);//TenCV nam o cot thu 2
//            int id = dataCongViec.getInt(0); //ID nam o cot dau tien
//            tasksArrayList.add(new Tasks(ten));
//        }
//
//        listTaskAdapter.notifyDataSetChanged();
//    }

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
                DialogRename(listName,listID);
                break;
            case R.id.menu_list_task_Add:
                DialogAdd();
                break;
            case R.id.menu_list_task_Delete:
                DialogDelete(listName,listID);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogRename(final String listName, final int listID){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename_list);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_rename);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_rename);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_rename);

        txtName.setText(listName);

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
                //Set On Database
                dialog.dismiss();

                initActionBar(newName);
                //GetDataListTask();
            }
        });

        dialog.show();
    }

    private void DialogAdd(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_addTask);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addTask);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addTask);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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
                getDataFromDbToMainList();
                for (int i = 0; i < mainList.size();i++)
                {
                    if (mainList.get(i).getmID() == listID)
                    {
                        tasksArrayList.clear();
                        tasksArrayList.addAll(mainList.get(i).getmListTasks());
                    }
                }
                listTaskAdapter.notifyDataSetChanged();
                dialog.dismiss();
                //GetDataListTask();
            }
        });

        dialog.show();

    }

    private void DialogDelete(String listName,final int listID){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_list);

        final TextView txtName = dialog.findViewById(R.id.txtName_dialog_delete);
        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_delete);
        Button btnDelete = dialog.findViewById(R.id.btnDelete_dialog_delete);

        txtName.setText("\""+listName+"\""+ " will be permanently deleted.");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Set On Database
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
                listName = listNameAdd;
                initActionBar(listName);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getDataFromDbToMainList()
    {
        mainList.clear();
        List<TaskList> list = db.getAllList();
        mainList.addAll(list);
    }
}
