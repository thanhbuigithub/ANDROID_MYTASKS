package com.example.mytasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignIn_Activity extends AppCompatActivity {
    TextView tvIntro_01,tvIntro_02,tvIntro_03,tvRegister;
    ImageView imgView;
    EditText edName,edPass;
    Button btnLogin;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        tvIntro_01=(TextView)findViewById(R.id.text_welcome1);
        tvIntro_02=(TextView)findViewById(R.id.text_welcome2);
        tvIntro_03=(TextView)findViewById(R.id.text_welcome3);
        tvRegister=(TextView)findViewById(R.id.text_register);
        imgView=(ImageView)findViewById(R.id.image_logo);
        edName = (EditText)findViewById(R.id.edit_username);
        edPass = (EditText)findViewById(R.id.edit_password);
        btnLogin = (Button)findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName=edName.getText().toString();
                String mPass=edPass.getText().toString();
                Intent intent=new Intent(SignIn_Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn_Activity.this,SignUp_Activity.class);
                startActivity(intent);
            }
        });
    }
}
