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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn_Activity extends AppCompatActivity {
    TextView tvIntro_02, tvIntro_03, tvRegister;
    CheckBox cbSaveLogin;
    boolean bSaveLogin;
    SharedPreferences spLogin;
    SharedPreferences.Editor spEditorLogin;
    EditText edName, edPass;
    Button btnLogin;
    Database_User db;

    private static final String TAG = "SignIn_Activity";
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        db = new Database_User(this);

        tvIntro_02 = (TextView) findViewById(R.id.text_welcome2);
        tvIntro_03 = (TextView) findViewById(R.id.text_welcome3);
        tvRegister = (TextView) findViewById(R.id.text_register);
        edName = (EditText) findViewById(R.id.edit_username);
        edPass = (EditText) findViewById(R.id.edit_password);
        btnLogin = (Button) findViewById(R.id.btn_signin);
        cbSaveLogin = (CheckBox) findViewById(R.id.checkBox);
        signInButton = findViewById(R.id.sign_in_button);
        pDialog = new ProgressDialog(SignIn_Activity.this);

        //Remember me
        spLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        spEditorLogin = spLogin.edit();
        bSaveLogin = spLogin.getBoolean("saveLogin", false);

        if (bSaveLogin) {

            edName.setText(spLogin.getString("username", ""));
            edPass.setText(spLogin.getString("password", ""));
            cbSaveLogin.setChecked(true);
            Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("LoginWithGG", false);
            bundle.putString("username",edName.getText().toString());
            bundle.putString("password",edPass.getText().toString());
            SignInIntent.putExtras(bundle);
            startActivity(SignInIntent);
        }

        //Nhận intent từ login
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle!=null) {
            if (bSaveLogin) {
                cbSaveLogin.setChecked(false);
            }
            edName.setText(bundle.getString("username", ""));
            edPass.setText(bundle.getString("password", ""));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

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

                InputMethodManager mInput = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInput.hideSoftInputFromWindow(edName.getWindowToken(), 0);

                String mName = edName.getText().toString();
                String mPass = edPass.getText().toString();
                boolean mres = db.checkUser(mName,mPass);

                if (cbSaveLogin.isChecked()) {
                    spEditorLogin.putBoolean("saveLogin", true);
                    spEditorLogin.putString("username", mName);
                    spEditorLogin.putString("password", mPass);
                    spEditorLogin.commit();
                } else {
                    spEditorLogin.clear();
                    spEditorLogin.commit();
                }
                if (mres) {
                    Toast.makeText(SignIn_Activity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent SignInIntent = new Intent(SignIn_Activity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("LoginWithGG", false);
                    bundle.putString("username",mName);
                    bundle.putString("password",mPass);
                    SignInIntent.putExtras(bundle);
                    startActivity(SignInIntent);
                } else if(mName.isEmpty() || mPass.isEmpty())
                    {
                    if (mName.isEmpty()) {
                        edName.setError("Tên đăng nhập không được bỏ trống");
                    } else if (mPass.isEmpty()) edPass.setError("Mật khẩu không được bỏ trống");
                }
                else {
                    Toast.makeText(SignIn_Activity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        displayProgressDialog();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
           // handleSignInResult(task);
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        displayProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent SignInIntent = new Intent(SignIn_Activity.this,MainActivity.class);
                            startActivity(SignInIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login Failed: ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }

                });
    }

//    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
//
//        TextView displayName = findViewById(R.id.displayName);
//        ImageView profileImage = findViewById(R.id.profilePic);
//        if (user != null) {
//            displayName.setText(user.getDisplayName());
//            displayName.setVisibility(View.VISIBLE);
//            // Loading profile image
//            Uri profilePicUrl = user.getPhotoUrl();
//            if (profilePicUrl != null) {
//                Glide.with(this).load(profilePicUrl)
//                        .into(profileImage);
//            }
//            profileImage.setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
//        } else {
//            displayName.setVisibility(View.GONE);
//            profileImage.setVisibility(View.GONE);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            //findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//        }
//    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            startActivity(new Intent(SignIn_Activity.this, MainActivity.class));
//        } catch (ApiException e) {
//
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            Toast.makeText(SignIn_Activity.this, "Failed", Toast.LENGTH_LONG).show();
//        }
//        hideProgressDialog();
//    }
    @Override
    protected void onStart() {
        //super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if(account != null) {
//            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(SignIn_Activity.this, MainActivity.class));
//        }
//        else {
//            Log.d(TAG, "Đăng nhập không thành công ! Thử lại");}
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    private void displayProgressDialog() {
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    private void hideProgressDialog() {
        pDialog.dismiss();
    }
}
