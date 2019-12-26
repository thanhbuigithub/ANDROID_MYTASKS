package com.example.mytasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.ToolbarWidgetWrapper;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.Callback;

public class MainActivity extends AppCompatActivity {
    ImageView imView;
    TextView tvName,tvEmail;
    ListView lvMainSpec, lvMainList;
    LinearLayout lExpandableView;
    Button bExpandMore;
    CardView cvCardView;
    Button btnLogout,btnLogoutGG;
    boolean cLogin;
    FirebaseAuth mAuth;
    public static ArrayList<Integer> srcIcons;
    Database_User dbName;
    DbHelper db;
    public static ArrayList<TaskList> mainList, mainSpec;
    FloatingActionButton btnAdd;
    public static MainListView mainListAdapter;
    MainListView specListAdapter;
    SharedPreferences spLogout;
    private static ConnectivityManager connMng;
    public static String mDatabaseUser;
    private boolean dbExist;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    public static boolean thisActivity;
    public static boolean firstCreate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "OnCreate");
        setContentView(R.layout.activity_main);
        thisActivity = true;

        dbName = new Database_User(this);
        spLogout = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        addIcon();
        getInfor();
        if(cLogin){
            logOutGG();
        }else {
            logOut();
        }
        showDepart();
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

    private void addControl(){

        if (firstCreate)
        {
            final File localFile = new File("data/data/com.example.mytasks/databases/" + mDatabaseUser);
            dbExist = localFile.exists();

            dbRef.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (mDatabaseUser != null && thisActivity) {
                        RealTimeDownloadDb(mDatabaseUser);
                        Toast.makeText(MainActivity.this, "Realtime Download", Toast.LENGTH_SHORT).show();
                    }
                    getDataFromDbToMainList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(internetStateReceiver, intentFilter);

            firstCreate = false;
        }


        db = new DbHelper(this, mDatabaseUser);
        Log.d("MainActivity","Created database!");
        //FireBaseHelper.getInstance().UploadFile(mDatabaseUser);
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
//        Toast.makeText(this,"On Resume",Toast.LENGTH_LONG).show();
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
            cLogin = bundle.getBoolean("LoginWithGG", false);
            if (cLogin) {
                tvName.setText(bundle.getString("personName"));
                tvEmail.setText(bundle.getString("personEmail"));
                Glide.with(this).load(bundle.getParcelable("personPhoto")).into(imView);
                mDatabaseUser = bundle.getString("personEmail");
            } else {
                String mName = dbName.getName(bundle.getString("username", ""));
                tvName.setText(mName);
                tvEmail.setText(bundle.getString("username", ""));
                mDatabaseUser = bundle.getString("username", "");
            }
        }
    }

    private void logOutGG(){
        btnLogoutGG = findViewById(R.id.btnLogoutGG);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        btnLogoutGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutCurrentUser();
            }
        });
    }
    private void logoutCurrentUser(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(MainActivity.this, signInOptions);
        signInClient.revokeAccess().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignIn_Activity.class);
                SharedPreferences.Editor editor = spLogout.edit();
                editor.clear();
                editor.apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unable to logout!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
    private void logOut(){
        btnLogout = findViewById(R.id.btnLogout);
        btnLogoutGG = findViewById(R.id.btnLogoutGG);
        btnLogoutGG.setVisibility(View.GONE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this,SignIn_Activity.class);
                Toast.makeText(MainActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
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

    private boolean isNetworkConnected(){
        boolean have_WIFI = false;
        boolean have_MobileData = false;
        connMng = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connMng.getAllNetworkInfo();
        for (NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI") && info.isConnected())
                have_WIFI = true;
            if(info.getTypeName().equalsIgnoreCase("MOBILE") && info.isConnected())
                have_WIFI = true;
        }
        return have_WIFI || have_MobileData;
    }

    private BroadcastReceiver internetStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Start BroadcastReceiver");
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,ProgressDialog.THEME_HOLO_DARK);
            progressDialog.setTitle("Vui lòng kết nối internet!");
            progressDialog.setCancelable(false);

            if (isNetworkConnected() && mDatabaseUser != null)
            {
                //Internet connected
                if (dbExist){
                    //Upload database to firebase
                    try {
                        FireBaseHelper.getInstance(MainActivity.this).UploadUserFile(mDatabaseUser);
                    }catch (Exception e){
                        Log.d("MainActivity", " BroadcastReceiver Upload UserData Fail "+e.getMessage());
                    }

                }
                else
                {
                    //Download database from firebase
                    progressDialog.dismiss();
                    dbExist = true;
                    try {
                        FireBaseHelper.getInstance(MainActivity.this).DownloadUserDatabase(mDatabaseUser);
                        Log.d("MainActivity", " BroadcastReceiver Download UserData Success ");
                    }catch (Exception e){
                        Log.d("MainActivity", " BroadcastReceiver Download UserData Fail "+e.getMessage());
                    }
                }
                Toast.makeText(MainActivity.this,"Online Mode",Toast.LENGTH_SHORT).show();
            }
            else{
                //Internet disconnected
                if (!dbExist){
                    progressDialog.show();
                }
                Toast.makeText(MainActivity.this,"Offline Mode",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        thisActivity = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        thisActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetStateReceiver);
    }

    public void RealTimeDownloadDb(String filename){
        StorageReference dbRef = storageRef.child(filename);

        final File localFile = new File("data/data/com.example.mytasks/databases/"+filename);

        dbRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                getDataFromDbToMainList();
                Log.d("Realtime"," Download Success "+ localFile.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Realtime"," Download Fail - " + exception.toString());
            }
        });
    }

}
