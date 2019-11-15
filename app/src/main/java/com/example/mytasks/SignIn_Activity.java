package com.example.mytasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasks.Database_User;
import com.example.mytasks.R;
import com.example.mytasks.SignUp_Activity;

public class SignIn_Activity extends AppCompatActivity {
    private TextView tvIntro_01, tvIntro_02, tvIntro_03, tvRegister;
    private CheckBox cbSaveLogin;
    private Boolean bSaveLogin;
    private SharedPreferences spLogin;
    private SharedPreferences.Editor spEditorLogin;
    private EditText edName, edPass;
    private Button btnLogin;
    private Database_User db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        db = new Database_User(this);

        tvIntro_01 = (TextView) findViewById(R.id.text_welcome1);
        tvIntro_02 = (TextView) findViewById(R.id.text_welcome2);
        tvIntro_03 = (TextView) findViewById(R.id.text_welcome3);
        tvRegister = (TextView) findViewById(R.id.text_register);
        edName = (EditText) findViewById(R.id.edit_username);
        edPass = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);
        cbSaveLogin = (CheckBox) findViewById(R.id.checkBox);

        spLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        spEditorLogin = spLogin.edit();
        bSaveLogin = spLogin.getBoolean("saveLogin", false);

        if (bSaveLogin == true) {
            edName.setText(spLogin.getString("username", ""));
            edPass.setText(spLogin.getString("password", ""));
            cbSaveLogin.setChecked(true);
            Intent GoHomePage = new Intent(SignIn_Activity.this, MainActivity.class);
            startActivity(GoHomePage);

        }

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(SignIn_Activity.this, SignUp_Activity.class);
                startActivity(registerIntent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = edName.getText().toString();
                String mPass = edPass.getText().toString();
                Boolean mres = db.checkUser(mName, mPass);

                InputMethodManager mInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInput.hideSoftInputFromWindow(edName.getWindowToken(), 0);

                if (cbSaveLogin.isChecked()) {
                    spEditorLogin.putBoolean("saveLogin", true);
                    spEditorLogin.putString("username", mName);
                    spEditorLogin.putString("password", mPass);
                    spEditorLogin.commit();
                } else {
                    spEditorLogin.clear();
                    spEditorLogin.commit();
                }
                if (mres == true) {
                    Toast.makeText(SignIn_Activity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent GoHomePage = new Intent(SignIn_Activity.this, MainActivity.class);
                    startActivity(GoHomePage);
                } else if (mName.isEmpty() || mPass.isEmpty()) {
                    if (mName.isEmpty()) {
                        edName.setError("Tên đăng nhập không được bỏ trống");
                    } else edPass.setError("Mật khẩu không được bỏ trống");
                } else {
                    Toast.makeText(SignIn_Activity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

