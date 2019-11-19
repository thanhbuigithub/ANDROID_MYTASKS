package com.example.mytasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.Task;

public class SignIn_Activity extends AppCompatActivity {
    TextView tvIntro_02, tvIntro_03, tvRegister;
    CheckBox cbSaveLogin;
    boolean bSaveLogin;
    SharedPreferences spLogin;
    SharedPreferences.Editor spEditorLogin;
    EditText edUserName, edPass;
    Button btnLogin;
    Database_User db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        db = new Database_User(this);

        tvIntro_02 = (TextView) findViewById(R.id.text_welcome2);
        tvIntro_03 = (TextView) findViewById(R.id.text_welcome3);
        tvRegister = (TextView) findViewById(R.id.text_register);
        edUserName = (EditText) findViewById(R.id.edit_username);
        edPass = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);
        cbSaveLogin = (CheckBox) findViewById(R.id.checkBox);

        spLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        spEditorLogin = spLogin.edit();
        bSaveLogin = spLogin.getBoolean("saveLogin", false);

        //Remember me
        rememberMe();

        //Nhận intent
        getIntentSignIn();

        //Sign in database
        signIn();

        // Click sign in database
        addEventSignIn();

    }

    private void rememberMe(){

        if (bSaveLogin) {

            edUserName.setText(spLogin.getString("username", ""));
            edPass.setText(spLogin.getString("password", ""));
            cbSaveLogin.setChecked(true);
            Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putBoolean("LoginWithGG", false);
            bundle1.putString("username",edUserName.getText().toString());
            SignInIntent.putExtras(bundle1);
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

    private void signIn(){
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(SignIn_Activity.this, SignUp_Activity.class);
                edUserName.setText(spLogin.getString("username", ""));
                edPass.setText(spLogin.getString("password", ""));
                cbSaveLogin.setChecked(false);
                spEditorLogin.putBoolean("saveLogin", false);
                spEditorLogin.putString("username", edUserName.getText().toString());
                spEditorLogin.putString("password", edPass.getText().toString());
                spEditorLogin.apply();
                startActivity(registerIntent);
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
}
