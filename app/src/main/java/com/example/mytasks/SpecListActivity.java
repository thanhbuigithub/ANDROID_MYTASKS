package com.example.mytasks;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class SpecListActivity extends AppCompatActivity implements Task_RecyclerViewAdapter.OnTaskListener{
    RecyclerView recyclerViewTask;
    public static TaskList list;
    Task_RecyclerViewAdapter task_recyclerViewAdapter;
    DbHelper db;
    int listID;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    public static CoordinatorLayout layout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasks);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        layout = findViewById(R.id.coordinator);

        db = new DbHelper(this, MainActivity.mDatabaseUser);

        layout = (CoordinatorLayout) findViewById(R.id.coordinator);

        recyclerViewTask = findViewById(R.id.recyclerView_Task);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(SpecListActivity.this));
        FloatingActionButton fabListTask = (FloatingActionButton) findViewById(R.id.fabListTask);
        fabListTask.setVisibility(View.GONE);

        list = new TaskList();

        if (bundle != null){
            listID = bundle.getInt("LIST_ID",0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (listID != 0){
            list = db.getSpecList(listID);
        }

        task_recyclerViewAdapter = new Task_RecyclerViewAdapter(this,R.layout.list_task, list.getmListTasks(), this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewTask);
        task_recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewTask.setAdapter(task_recyclerViewAdapter);

        initActionBar(list.getmName());
        layout.setBackgroundResource(list.getmTheme());
        addEvent();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEvent() {

    }

    private void initActionBar(String listName){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(listName);
    }

    @Override
    public void OnTaskClick(int position) {
        Intent intent = new Intent(SpecListActivity.this, TaskActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("taskID", list.getmListTasks().get(position).getmID());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteItem(viewHolder);
        }
    };

    private void deleteItem(RecyclerView.ViewHolder viewHolder) {
        Task mRecentlyDeletedItem = new Task();
        mRecentlyDeletedItem = list.getmListTasks().get(viewHolder.getAdapterPosition());
        db.deleteTask(list.getmListTasks().get(viewHolder.getAdapterPosition()));
        list.getmListTasks().remove(viewHolder.getAdapterPosition());
        recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(SpecListActivity.this,R.layout.list_task, list.getmListTasks(), SpecListActivity.this));
        showUndoSnackbar(viewHolder, mRecentlyDeletedItem);
    }

    private void showUndoSnackbar(final RecyclerView.ViewHolder viewHolder, final Task mRecentlyDeletedItem) {
        View view = layout;
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete(viewHolder,mRecentlyDeletedItem);
            }
        });
        snackbar.show();
    }

    private void undoDelete(RecyclerView.ViewHolder viewHolder, Task mRecentlyDeletedItem) {
        db.insertNewTask(mRecentlyDeletedItem);
        list = db.getSpecList(list.getmID());
        recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(SpecListActivity.this,R.layout.list_task, list.getmListTasks(), SpecListActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listID != 0) {
            list = db.getSpecList(listID);
            recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(SpecListActivity.this, R.layout.list_task, list.getmListTasks(), SpecListActivity.this));
        }
    }
}
