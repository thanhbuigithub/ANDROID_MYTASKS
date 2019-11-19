package com.example.mytasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    ImageView imView;
    TextView tvName,tvEmail;
    ListView lvMainSpec, lvMainList;
    public static ArrayList<Integer> srcIcons;


    // kiem tra luong login
    boolean sCheckLogin;

    ArrayList<TaskList> mainList, mainSpec;
    FloatingActionButton btnAdd;
    MainListView mainListAdapter;
    MainListView specListAdapter;
    DbHelper db;

    public static String mDatabaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addIcon();
        imView = findViewById(R.id.imageView);
        tvName = findViewById(R.id.Name);
        tvEmail = findViewById(R.id.Email);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            mDatabaseUser = personEmail;
            //String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Glide.with(this).load(personPhoto).into(imView);
            tvName.setText(personName);
            tvEmail.setText(personEmail);

        }
        else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle!=null) {
                boolean mCheck = bundle.getBoolean("LoginWithGG", false);
                mDatabaseUser = bundle.getString("username", "");
                if (!mCheck) {
                    tvName.setText(bundle.getString("username", ""));
                    tvEmail.setText(bundle.getString("password", ""));
                }
            }
        }


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
//        TaskList todayList = new TaskList();
//        todayList.setmName("Công việc hôm nay");
//        todayList.setmID(1);
//        todayList.setmIcon(R.drawable.checked_important);

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

}
