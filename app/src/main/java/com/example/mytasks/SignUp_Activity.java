package com.example.mytasks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SignUp_Activity extends AppCompatActivity {
    TextView mTextView_Head,mTextView_Name,mTextView_UserName,mTextView_Pass,mTextView_PassAgain;
    EditText mTextName,mTextUsername,mTextPassword;
    EditText mTextPassAgain;
    Button mButtonSignUp;
    View mView;
    TextView mTextViewLogin,mGoBack,mTextView_Foot;
    Database_User db;
    SharedPreferences spAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        db = new Database_User(this);
        spAdd = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        mTextView_Head=(TextView)findViewById(R.id.text_title);
        mTextView_Name=(TextView)findViewById(R.id.name);
        mTextName = (EditText)findViewById(R.id.input_name);
        mTextView_UserName=(TextView)findViewById(R.id.username);
        mTextUsername = (EditText)findViewById(R.id.input_username);
        mTextView_Pass=(TextView)findViewById(R.id.password);
        mTextPassword = (EditText)findViewById(R.id.input_password);
        mTextView_PassAgain=(TextView)findViewById(R.id.password_again);
        mTextPassAgain = (EditText)findViewById(R.id.input_password_again);
        mButtonSignUp = (Button)findViewById(R.id.btn_signup);
        mView=(View)findViewById(R.id.rule);
        mGoBack=(TextView)findViewById(R.id.text_back);
        mTextView_Foot=(TextView)findViewById(R.id.text_foot);
        mTextViewLogin = (TextView)findViewById(R.id.text_login);


        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoBackIntent = new Intent(SignUp_Activity.this,SignIn_Activity.class);
                Bundle bundle = new Bundle();
                GoBackIntent.putExtras(bundle);
                startActivity(GoBackIntent);
            }
        });

        mTextView_Foot.setVisibility(View.GONE);
        mTextViewLogin.setVisibility(View.GONE);

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mname = mTextName.getText().toString();
                String muser = mTextUsername.getText().toString();
                String mpass = mTextPassword.getText().toString();
                String mpassagain = mTextPassAgain.getText().toString();

                if(mname.isEmpty() || muser.isEmpty() || mpass.isEmpty() || mpassagain.isEmpty()){

                    if (mname.isEmpty()){
                        mTextName.setError("Tên người dùng không được bỏ trống");
                    }
                    else if(muser.isEmpty()){
                        mTextUsername.setError("Tên đăng nhập không được bỏ trống");
                    }
                    else if(mpass.isEmpty()){
                        mTextPassword.setError("Mật khẩu không được bỏ trống");
                    }
                    else {
                        mTextPassAgain.setError("Xác nhận lại mật khẩu");
                    }
                }
                else if(db.checkAccount(muser) || db.checkUser(mname,mpass))
                {
                    Toast.makeText(SignUp_Activity.this,"Tài khoản đã tồn tại ! Thử lại",Toast.LENGTH_SHORT).show();
                }
                else if(mpass.equals(mpassagain)){
                    long val = db.addUser(mname, muser, mpass);
                    if (val > 0) {
                        mView.setVisibility(View.VISIBLE);
                        mTextView_Foot.setVisibility(View.VISIBLE);
                        mTextViewLogin.setVisibility(View.VISIBLE);
                        mGoBack.setVisibility(View.GONE);

                        SharedPreferences.Editor editor = spAdd.edit();
                        editor.clear();
                        editor.apply();
                        Intent SignInIntent = new Intent(SignUp_Activity.this,SignIn_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("LoginWithGG", false);
                        bundle.putString("username",muser);
                        bundle.putString("password",mpass);
                        SignInIntent.putExtras(bundle);
                        startActivity(SignInIntent);
                        Toast.makeText(SignUp_Activity.this, "Đăng kí thành công !", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(SignUp_Activity.this,"Mật khẩu không trùng khớp ! Thử lại",Toast.LENGTH_SHORT).show();
                }

                mTextViewLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent SignInIntent = new Intent(SignUp_Activity.this,SignIn_Activity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("password", mTextPassword.getText().toString());
                        bundle.putString("username",mTextUsername.getText().toString());
                        SignInIntent.putExtras(bundle);
                        startActivity(SignInIntent);
                    }
                });
            }
        });


        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignInIntent = new Intent(SignUp_Activity.this,SignIn_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("LoginWithGG", false);
                bundle.putString("username",mTextUsername.getText().toString());
                bundle.putString("password", mTextPassword.getText().toString());
                SignInIntent.putExtras(bundle);
                startActivity(SignInIntent);
            }
        });
    }
}
