package com.example.mytasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imView;
    TextView tvName,tvEmail;
    ListView lvMainSpec, lvMainList;
    LinearLayout lExpandableView;
    Button bExpandMore;
    CardView cvCardView;
    Button btLogout,btAddAccount;
    public static ArrayList<Integer> srcIcons;
    Database_User dbName;
    DbHelper db;
    ArrayList<TaskList> mainList, mainSpec;
    FloatingActionButton btnAdd;
    MainListView mainListAdapter;
    MainListView specListAdapter;
    SharedPreferences spLogout;
    public static String mDatabaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbName = new Database_User(this);
        spLogout = getSharedPreferences("loginPrefs", MODE_PRIVATE);


        addIcon();
        getInfor();
        showDepart();
        addAccount();
        logOut();
        addControl();
        mainListAdapter = new MainListView(this, R.layout.list_view, mainList);
        specListAdapter = new MainListView(this, R.layout.list_view, mainSpec);
        lvMainList.setAdapter(mainListAdapter);
        lvMainSpec.setAdapter(specListAdapter);

        TaskList importantList = new TaskList();
        importantList.setmName("Quan trọng");
        importantList.setmID(2);
        importantList.setmIcon(26);
        TaskList planList = new TaskList();
        planList.setmName("Đã lên kế hoạch");
        planList.setmID(3);
        planList.setmIcon(27);
        TaskList todayList = new TaskList();
        todayList.setmName("Ngày hôm nay");
        todayList.setmID(1);
        todayList.setmIcon(9);

        mainSpec.add(todayList);
        mainSpec.add(importantList);
        mainSpec.add(planList);

        List<TaskList> list = db.getAllList();
        mainList.addAll(list);

        addEvent();
        getDataFromDbToMainList();
    }
    private void getDataFromDbToMainList()
    {
        mainList.clear();
        List<TaskList> list = db.getAllList();
        mainList.addAll(list);
        mainListAdapter.notifyDataSetChanged();
    }
    private void addEvent() {
        lvMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(MainActivity.this,ListTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("DATABASE_NAME", mDatabaseUser);
                bundle.putInt("LIST_ID", mainList.get(position).getmID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        lvMainSpec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(MainActivity.this, SpecListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("LIST_ID", mainSpec.get(position).getmID());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,ListTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("DATABASE_NAME", mDatabaseUser);
                bundle.putString("ACTION", "ADD");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void addControl() {
        db = new DbHelper(this, mDatabaseUser);
        lvMainList = (ListView) findViewById(R.id.lvMainList);
        lvMainSpec = (ListView) findViewById(R.id.lvMainSpec);
        btnAdd = (FloatingActionButton) findViewById(R.id.fabMainList);
        mainList = new ArrayList<>();
        mainSpec = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDbToMainList();
        mainListAdapter.notifyDataSetChanged();
    }

    private void addIcon() {
        srcIcons = new ArrayList<>();
        srcIcons.add(R.drawable.icon_default);
        srcIcons.add(R.drawable.icon_1);
        srcIcons.add(R.drawable.icon_2);
        srcIcons.add(R.drawable.icon_3);
        srcIcons.add(R.drawable.icon_4);
        srcIcons.add(R.drawable.icon_5);
        srcIcons.add(R.drawable.icon_6);
        srcIcons.add(R.drawable.icon_7);
        srcIcons.add(R.drawable.icon_8);
        srcIcons.add(R.drawable.icon_9);
        srcIcons.add(R.drawable.icon_10);
        srcIcons.add(R.drawable.icon_11);
        srcIcons.add(R.drawable.icon_12);
        srcIcons.add(R.drawable.icon_13);
        srcIcons.add(R.drawable.icon_14);
        srcIcons.add(R.drawable.icon_15);
        srcIcons.add(R.drawable.icon_16);
        srcIcons.add(R.drawable.icon_17);
        srcIcons.add(R.drawable.icon_18);
        srcIcons.add(R.drawable.icon_19);
        srcIcons.add(R.drawable.icon_20);
        srcIcons.add(R.drawable.icon_21);
        srcIcons.add(R.drawable.icon_22);
        srcIcons.add(R.drawable.icon_23);
        srcIcons.add(R.drawable.icon_24);
        srcIcons.add(R.drawable.icon_25);
        srcIcons.add(R.drawable.checked_important);
        srcIcons.add(R.drawable.ic_deadline_blue);
    }
    private void getInfor() {
        imView = findViewById(R.id.imageView);
        tvName = findViewById(R.id.Name);
        tvEmail = findViewById(R.id.Email);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String mName = dbName.getName(bundle.getString("username", ""));
            tvName.setText(mName);
            tvEmail.setText(bundle.getString("username", ""));
            mDatabaseUser = bundle.getString("username", "");
            }
    }

    private void addAccount(){
        btAddAccount = findViewById(R.id.btAddAccount);
        btAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,SignUp_Activity.class);
                startActivity(intent);
            }
        });
    }
    private void logOut(){
        btLogout = findViewById(R.id.btLogout);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,SignIn_Activity.class);
                SharedPreferences.Editor editor = spLogout.edit();
                editor.clear();
                editor.apply();
                startActivity(intent);
            }
        });
    }
    private void showDepart(){
        lExpandableView = findViewById(R.id.expandableView);
        bExpandMore = findViewById(R.id.buttonExpandMore);
        cvCardView = findViewById(R.id.cvExpand);
        bExpandMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lExpandableView.getVisibility()==View.GONE){
                    TransitionManager.beginDelayedTransition(cvCardView, new AutoTransition());
                    lExpandableView.setVisibility(View.VISIBLE);
                    bExpandMore.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
                }
                else {
                    TransitionManager.beginDelayedTransition(cvCardView, new AutoTransition());
                    lExpandableView.setVisibility(View.GONE);
                    bExpandMore.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
                }
            }
        });
    }
}
