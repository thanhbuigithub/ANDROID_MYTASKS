package com.example.mytasks;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;

public class SignIn_Activity extends AppCompatActivity {
    TextView tvIntro_02, tvIntro_03, tvRegister;
    CheckBox cbSaveLogin;
    boolean bSaveLogin;
    SharedPreferences spLogin;
    SharedPreferences.Editor spEditorLogin;
    EditText edUserName, edPass;
    Button btnLogin;
    Button btnLoginGG;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Database_User db;
    private final static int RC_SIGN_IN = 2;
    private static final String TAG = "GoogleActivity";
    ProgressDialog pDialog;
    ConnectivityManager connMng;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        Log.d("MainActivity", "SignIn_Activity OnCreate");
        connMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        File signupFile = new File("data/data/com.example.mytasks/databases/signup.db");
        if (isNetworkConnected()){
//            try {
//                FireBaseHelper.getInstance(this).DownloadLoginDatabase("signup.db");
//            }catch (Exception e){
//                Log.d("SignIn_Activity", "Download Login Fail "+e.getMessage());
//            }
        } else if (!signupFile.exists()){
            AlertDialog internetDialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK).create();
            internetDialog.setMessage("Vui lòng kết nối internet cho lần sử dụng đầu tiên");
            internetDialog.setInverseBackgroundForced(true);
            internetDialog.show();
        }
        db = new Database_User(this);
        tvIntro_02 = (TextView) findViewById(R.id.text_welcome2);
        tvIntro_03 = (TextView) findViewById(R.id.text_welcome3);
        tvRegister = (TextView) findViewById(R.id.text_register);
        edUserName = (EditText) findViewById(R.id.edit_username);
        edPass = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);
        btnLoginGG = (Button) findViewById(R.id.sign_in_button);
        cbSaveLogin = (CheckBox) findViewById(R.id.checkBox);

        spLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        spEditorLogin = spLogin.edit();
        bSaveLogin = spLogin.getBoolean("saveLogin", false);



        // Sign in google
        googleSignIn();

        //Nhận intent
        getIntentSignIn();

        //Remember me
        rememberMe();

        //Sign in database
        signInDB();

        // Click sign in database
        addEventSignIn();

    }

    private void rememberMe(){

        if (bSaveLogin) {
            edUserName.setText(spLogin.getString("username", ""));
            edPass.setText(spLogin.getString("password", ""));
            cbSaveLogin.setChecked(true);
            Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("LoginWithGG", false);
            bundle.putString("username",edUserName.getText().toString());
            SignInIntent.putExtras(bundle);
            startActivity(SignInIntent);
        }
    }
    private void getIntentSignIn(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null) {
            spEditorLogin.clear();
            spEditorLogin.apply();
            edUserName.setText(bundle.getString("username", ""));
            edPass.setText(bundle.getString("password", ""));
        }
    }

    private void signInDB(){
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent registerIntent = new Intent(SignIn_Activity.this, SignUp_Activity.class);
                    edUserName.setText(spLogin.getString("username", ""));
                    edPass.setText(spLogin.getString("password", ""));
                    cbSaveLogin.setChecked(false);
                    spEditorLogin.putBoolean("saveLogin", false);
                    spEditorLogin.putString("username", edUserName.getText().toString());
                    spEditorLogin.putString("password", edPass.getText().toString());
                    spEditorLogin.apply();
                    startActivity(registerIntent);
                } else {
                    Toast.makeText(SignIn_Activity.this,"Please connect to internet to sign up!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addEventSignIn(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager mInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert mInput != null;
                mInput.hideSoftInputFromWindow(edUserName.getWindowToken(), 0);
                String mUserName = edUserName.getText().toString();
                String mPass = edPass.getText().toString();
                boolean mres = db.checkUser(mUserName,mPass);

                if (cbSaveLogin.isChecked()) {
                    spEditorLogin.putBoolean("saveLogin", true);
                    spEditorLogin.putString("username", mUserName);
                    spEditorLogin.putString("password", mPass);
                    spEditorLogin.commit();
                } else {
                    spEditorLogin.clear();
                    spEditorLogin.commit();
                }
                if (mres) {
                    Toast.makeText(SignIn_Activity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putString("username",mUserName);
                    bundle.putBoolean("LoginWithGG", false);
                    SignInIntent.putExtras(bundle);
                    startActivity(SignInIntent);
                } else if(mUserName.isEmpty() || mPass.isEmpty())
                {
                    if (mUserName.isEmpty()) {
                        edUserName.setError("Tên đăng nhập không được bỏ trống");
                    } else edPass.setError("Mật khẩu không được bỏ trống");
                }
                else {
                    Toast.makeText(SignIn_Activity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void googleSignIn(){
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        pDialog = new ProgressDialog(SignIn_Activity.this);
        btnLoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleSignIn();
            }
        });
    }

    private void startGoogleSignIn() {
        showProgressDialog();
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
                Log.d("GGLogin",e.getMessage());
                if(pDialog.isShowing()){
                    hideProgressDialog();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (pDialog.isShowing()){
                    hideProgressDialog();
                }
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (pDialog.isShowing()){
                    hideProgressDialog();
                }
                Toast.makeText(SignIn_Activity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
            Bundle bundle =new Bundle();
            String personName = user.getDisplayName();
            String personEmail = user.getEmail();
            Uri personPhoto = user.getPhotoUrl();
            bundle.putString("personName",personName);
            bundle.putString("personEmail",personEmail);
            bundle.putParcelable("personPhoto",personPhoto);
            bundle.putBoolean("LoginWithGG", true);
            SignInIntent.putExtras(bundle);
            startActivity(SignInIntent);
        }
    }

    private void showProgressDialog() {
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void hideProgressDialog() {
        pDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetStateReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(internetStateReceiver);
    }

    private BroadcastReceiver internetStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected())
            {
                Toast.makeText(SignIn_Activity.this,"Internet Connected",Toast.LENGTH_SHORT).show();
                try {
                    FireBaseHelper.getInstance(SignIn_Activity.this).DownloadLoginDatabase("signup.db");
                    Log.d("SignIn_Activity", " BroadcastReceiver Download Login Success ");
                }catch (Exception e){
                    Log.d("SignIn_Activity", " BroadcastReceiver Download Login Fail "+e.getMessage());
                }
            }
            else{
                Toast.makeText(SignIn_Activity.this,"Internet Disconnected",Toast.LENGTH_SHORT).show();
            }
        }
    };
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

}

