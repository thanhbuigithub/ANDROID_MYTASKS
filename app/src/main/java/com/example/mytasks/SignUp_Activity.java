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

public class SignUp_Activity extends AppCompatActivity {
    TextView mTextView_Head,mTextView_UserName,mTextView_Pass,mTextView_PassAgain,mTextView_Foot;
    ImageView mImageView;
    EditText mTextUsername,mTextPassword;
    EditText mTextPassAgain;
    Button mButtonSignUp;
    View mView;
    TextView mTextViewLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        db = new DatabaseHelper(this);

        mTextView_Head=(TextView)findViewById(R.id.text_title);
        mImageView=(ImageView)findViewById(R.id.image_logo);
        mTextView_UserName=(TextView)findViewById(R.id.username);
        mTextUsername = (EditText)findViewById(R.id.input_username);
        mTextView_Pass=(TextView)findViewById(R.id.password);
        mTextPassword = (EditText)findViewById(R.id.input_password);
        mTextView_PassAgain=(TextView)findViewById(R.id.password_again);
        mTextPassAgain = (EditText)findViewById(R.id.input_password_again);
        mButtonSignUp = (Button)findViewById(R.id.btn_signup);
        mView=(View)findViewById(R.id.rule);
        mTextView_Foot=(TextView)findViewById(R.id.text_foot);
        mTextViewLogin = (TextView)findViewById(R.id.text_login);

        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignInIntent = new Intent(SignUp_Activity.this,SignIn_Activity.class);
                startActivity(SignInIntent);
            }
        });

        mView.setVisibility(View.GONE);
        mTextView_Foot.setVisibility(View.GONE);
        mTextViewLogin.setVisibility(View.GONE);



        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String muser = mTextUsername.getText().toString();
                String mpass = mTextPassword.getText().toString();
                String mpassagain = mTextPassAgain.getText().toString();

                if(mpass.equals(mpassagain) && !mpass.isEmpty()){
                    long val = db.addUser(muser,mpass);
                    if(val > 0){
                        mView.setVisibility(View.VISIBLE);
                        mTextView_Foot.setVisibility(View.VISIBLE);
                        mTextViewLogin.setVisibility(View.VISIBLE);

                        Toast.makeText(SignUp_Activity.this,"Đăng kí thành công !",Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(SignUp_Activity.this,"Đăng kí thất bại ! Thử lại",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                if(muser.isEmpty() || mpass.isEmpty()){
                    Toast.makeText(SignUp_Activity.this,"Tên đăng nhập hoặc mật khẩu không được để trống !",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignUp_Activity.this,"Mật khẩu không giống nhau ! Thử lại",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
