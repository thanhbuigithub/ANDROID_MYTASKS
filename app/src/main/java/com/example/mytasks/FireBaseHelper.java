package com.example.mytasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.function.Function;

import javax.security.auth.callback.Callback;

import kotlin.jvm.functions.Function1;

public class FireBaseHelper {
    private static Context context;
    public static FireBaseHelper getInstance(Context c) {
        context = c;
        if(instance==null)
            instance = new FireBaseHelper();
        return instance;
    }

    private static void setInstance(FireBaseHelper instance) {
        FireBaseHelper.instance = instance;
    }

    private static FireBaseHelper instance;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();

    ProgressDialog progressDialog;

    public void UploadFile(String filename){
        Uri file = Uri.fromFile(new File("data/data/com.example.mytasks/databases/"+filename));

        StorageReference dbRef = storageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = dbRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("FireBase"," Upload Fail - " + exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("FireBase"," Upload Success ");
            }
        });
    }

    public void DownloadUserDatabase(String filename){
        StorageReference dbRef = storageRef.child(filename);

        progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle("Đang đồng bộ dữ liệu!");
//        progressDialog.setMessage("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_layout);
        final File localFile = new File("data/data/com.example.mytasks/databases/"+filename);

        dbRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                progressDialog.dismiss();
                ((MainActivity) context).onResume();
                Log.d("FireBase"," Download Success "+ localFile.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Log.d("FireBase"," Download Fail - " + exception.toString());
            }
        });
    }

    public void DownloadLoginDatabase(String filename){
        final File localFile = new File("data/data/com.example.mytasks/databases/"+filename);
        StorageReference dbRef = storageRef.child(filename);

            progressDialog = new ProgressDialog(context);
//        progressDialog.setTitle("Đang đồng bộ dữ liệu!");
//        progressDialog.setMessage("Please wait!");
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog_layout);

        dbRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                progressDialog.dismiss();
                Log.d("FireBase"," Download Success "+ localFile.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressDialog.dismiss();
                Log.d("FireBase"," Download Fail - " + exception.toString());
            }
        });
    }

    public void UploadUserFile(final String filename){
        Uri file = Uri.fromFile(new File("data/data/com.example.mytasks/databases/"+filename));

        StorageReference dbRef = storageRef.child(filename);
        UploadTask uploadTask = dbRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("FireBase"," Upload Fail - " + exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("MainActivity", " BroadcastReceiver Upload UserData Success ");
                ref.child("users").child(filename).setValue(Calendar.getInstance().getTimeInMillis());
                Log.d("FireBase"," Upload Success ");
            }
        });
    }

}
