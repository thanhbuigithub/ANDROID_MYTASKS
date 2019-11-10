package com.example.mytasks;

import android.app.Dialog;
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

public class ListTaskActivity extends AppCompatActivity {
    ListView lvListTask;
    ArrayList<Task> tasksArrayList;
    ListTaskAdapter listTaskAdapter;

    //Test case
    final String listName = "Some thing";
    int listID = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);

        lvListTask = (ListView) findViewById(R.id.lvListTask);

        tasksArrayList = new ArrayList<>();
        listTaskAdapter = new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,tasksArrayList);

        lvListTask.setAdapter(listTaskAdapter);

        tasksArrayList.add(new Tasks("Do something!"));
        listTaskAdapter.notifyDataSetChanged();

        initActionBar(listName);
        //GetDataListTask();
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
                String taskName = txtName.getText().toString().trim();
                //Set On Database

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
}
