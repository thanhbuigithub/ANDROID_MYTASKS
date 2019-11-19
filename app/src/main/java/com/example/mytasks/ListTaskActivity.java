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

public class ListTaskActivity extends AppCompatActivity implements Task_RecyclerViewAdapter.OnTaskListener{
    //    ListView lvListTask;
    RecyclerView recyclerViewTask;
    public static TaskList list;
    //    ListTaskAdapter listTaskAdapter;
    Task_RecyclerViewAdapter task_recyclerViewAdapter;
    DbHelper db;
    FloatingActionButton fabListTask;
    int listID;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;


    ArrayList<String> nameThemes;
    ArrayList<Integer> srcThemes;
    public static Integer themePosition = -1;
    public static Integer iconPosition = -1;
    public static Integer iconPosition_addList = -1;
    public static CoordinatorLayout layout;
    public static CircleImageView circleImageView;
    public static ImageView iconImageview;

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
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(ListTaskActivity.this));
        fabListTask = (FloatingActionButton) findViewById(R.id.fabListTask);

        nameThemes = new ArrayList<>();
        srcThemes = new ArrayList<>();

        addBackground();
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
            iconPosition = (list.getmIcon()==null)? (-1): list.getmIcon();
        }

//        listTaskAdapter = new ListTaskAdapter(ListTaskActivity.this,R.layout.list_task,list.getmListTasks());
        task_recyclerViewAdapter = new Task_RecyclerViewAdapter(this,R.layout.list_task, list.getmListTasks(), this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewTask);
        task_recyclerViewAdapter.notifyDataSetChanged();
//        lvListTask.setAdapter(listTaskAdapter);
        recyclerViewTask.setAdapter(task_recyclerViewAdapter);

        Toast toast = Toast.makeText(ListTaskActivity.this, String.valueOf(list.getmListTasks().size()),Toast.LENGTH_SHORT );
        toast.show();
        initActionBar(list.getmName());
        initLayoutTheme();
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

    private void initLayoutTheme(){
        themePosition = (list.getmTheme() == null) ? (-1) : list.getmTheme();
        if(themePosition != -1)
        {
            layout.setBackgroundResource(srcThemes.get(themePosition));
        }
        else {
            layout.setBackgroundResource(R.drawable.bg_default);
        }
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_changeTheme);
        Button btnSave = dialog.findViewById(R.id.btnSave_dialog_changeTheme);
        circleImageView = dialog.findViewById(R.id.profile_image);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerViewChangeThemeAdapter adapter = new RecyclerViewChangeThemeAdapter(nameThemes, srcThemes,this);
        RecyclerView recyclerView = dialog.findViewById(R.id.rcv_dialog_changeTheme);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if(themePosition != -1){
            circleImageView.setImageResource(srcThemes.get(themePosition));
            if(themePosition >= 2){
                recyclerView.scrollToPosition(themePosition -2);
            }
            else{
                recyclerView.scrollToPosition(themePosition);
            }

        }
        else {
            circleImageView.setImageResource(R.drawable.bg_default);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.setmTheme(themePosition);
                db.updateList(list);
                initLayoutTheme();
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_rename);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_rename);
        final Button btnSave = dialog.findViewById(R.id.btnSave_dialog_rename);
        iconImageview = dialog.findViewById(R.id.icon_rename);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerViewRenameListAdapter adapter = new RecyclerViewRenameListAdapter(MainActivity.srcIcons,this);
        RecyclerView recyclerView = dialog.findViewById(R.id.rcv_dialog_renameList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if(iconPosition != -1)
        {
            iconImageview.setImageResource(MainActivity.srcIcons.get(iconPosition));
        }else{
            iconImageview.setImageResource(R.drawable.icon_default);
        }

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

        btnSave.setTextColor(getApplication().getResources().getColor(R.color.colorGrey));

        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
                            String newName = txtName.getText().toString().trim();
                            TaskList taskList = new TaskList();
                            taskList.setmName(newName);
                            taskList.setmID(listID);
                            taskList.setmIcon(iconPosition);
                            db.updateList(taskList);
                            list.setmName(newName);
                            initActionBar(list.getmName());
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtName.setText(list.getmName());
        txtName.requestFocus();
        dialog.show();
    }

    private void DialogAddTask(){
        final Dialog dialog = new Dialog(ListTaskActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_task);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        iconPosition_addList = -1;
        final EditText txtName = dialog.findViewById(R.id.txtName_dialog_addList);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel_dialog_addList);
        final Button btnSave = dialog.findViewById(R.id.btnSave_dialog_addList);
        iconImageview = dialog.findViewById(R.id.icon_add_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        final RecyclerViewAddListAdapter adapter = new RecyclerViewAddListAdapter(MainActivity.srcIcons,this);
        RecyclerView recyclerView = dialog.findViewById(R.id.rcv_dialog_addList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if(iconPosition_addList != -1)
        {
            iconImageview.setImageResource(MainActivity.srcIcons.get(iconPosition_addList));
        }else{
            iconImageview.setImageResource(R.drawable.icon_default);
        }

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
                            taskList.setmIcon(iconPosition_addList);
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
        nameThemes.clear();
        srcThemes.clear();

        srcThemes.add(R.drawable.bg_default);
        nameThemes.add("Default");

        srcThemes.add(R.drawable.bg_tokyo);
        nameThemes.add("Tokyo");

        srcThemes.add(R.drawable.bg_paris);
        nameThemes.add("Paris");

        srcThemes.add(R.drawable.bg_london);
        nameThemes.add("London");

        srcThemes.add(R.drawable.bg_couple);
        nameThemes.add("Couple");

        srcThemes.add(R.drawable.bg_frozen);
        nameThemes.add("Frozen");

        srcThemes.add(R.drawable.bg_lol);
        nameThemes.add("LoL");

        srcThemes.add(R.drawable.bg_nature);
        nameThemes.add("Nature");

        srcThemes.add(R.drawable.bg_sun);
        nameThemes.add("Sun");

        srcThemes.add(R.drawable.bg_avengers);
        nameThemes.add("Avengers");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"onResume",Toast.LENGTH_SHORT);
        if (listID != 0) {
            list = db.getList(listID);
            recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(ListTaskActivity.this, R.layout.list_task, list.getmListTasks(), ListTaskActivity.this));
        }
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
        recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(ListTaskActivity.this,R.layout.list_task, list.getmListTasks(), ListTaskActivity.this));
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
        ListTaskActivity.list = db.getList(ListTaskActivity.list.getmID());
        recyclerViewTask.setAdapter(new Task_RecyclerViewAdapter(ListTaskActivity.this,R.layout.list_task, list.getmListTasks(), ListTaskActivity.this));
    }

}
