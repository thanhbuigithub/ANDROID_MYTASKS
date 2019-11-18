package com.example.mytasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class AlarmService extends IntentService {

    Task currentTask;
    DbHelper db;
    int taskID;
    int code;
    String mDbUser;
    public AlarmService() {
        super("Alarm");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast.makeText(this, "handle", Toast.LENGTH_SHORT).show();

        Intent targetIntent = new Intent(this, TaskActivity.class);
        Bundle bundle = intent.getExtras();
        taskID = bundle.getInt("taskID",1);
        code = bundle.getInt("code", 11);
        mDbUser = bundle.getString("mUser", "");
        if (!mDbUser.equals(""))
            db = new DbHelper(AlarmService.this, mDbUser);
        currentTask = db.getTask(taskID);
        Bundle myBundle = new Bundle();
        myBundle.putInt("taskID", taskID);
        myBundle.putString("mUser", mDbUser);
        targetIntent.putExtras(myBundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                code,
                targetIntent,
                FLAG_ONE_SHOT);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("channel1", "Alarm", importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel1");
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nhắc nhở")
                .setContentIntent(pendingIntent)
                .setContentText(currentTask.getmName())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentInfo("Info");
        notificationManager.notify(code, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "destroyed", Toast.LENGTH_SHORT).show();
    }
}
