package com.example.mytasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytasks.DatabaseHelper;
import com.example.mytasks.R;
import com.example.mytasks.SignUp_Activity;

public class SignIn_Activity extends AppCompatActivity {
    TextView tvIntro_01,tvIntro_02,tvIntro_03,tvRegister;
    ImageView imgView;
    EditText edName,edPass;
    Button btnLogin;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        db = new DatabaseHelper(this);

        tvIntro_01=(TextView)findViewById(R.id.text_welcome1);
        tvIntro_02=(TextView)findViewById(R.id.text_welcome2);
        tvIntro_03=(TextView)findViewById(R.id.text_welcome3);
        tvRegister=(TextView)findViewById(R.id.text_register);
        imgView=(ImageView)findViewById(R.id.image_logo);
        edName = (EditText)findViewById(R.id.edit_username);
        edPass = (EditText)findViewById(R.id.edit_password);
        btnLogin = (Button)findViewById(R.id.btn_signin);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent =new Intent(SignIn_Activity.this, SignUp_Activity.class);
                startActivity(registerIntent);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = edName.getText().toString();
                String mPass = edPass.getText().toString();
                Boolean mres = db.checkUser(mName, mPass);


                if(mres == true)
                {
                    Toast.makeText(SignIn_Activity.this,"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                    Intent GoHomePage = new Intent(SignIn_Activity.this,MainActivity.class);
                    startActivity(GoHomePage);
                }
                else if(mName.isEmpty()||mPass.isEmpty()){
                    Toast.makeText(SignIn_Activity.this,"Tên đăng nhập hoặc mật khẩu không được bỏ trống",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignIn_Activity.this,"Tên đăng nhập hoặc mật khẩu không đúng",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
