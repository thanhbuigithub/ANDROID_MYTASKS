package com.example.mytasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static android.app.PendingIntent.FLAG_ONE_SHOT;

public class AlarmService extends IntentService {

    Task currentTask;
    DbHelper db;
    int taskID;
    int code;

    public AlarmService() {
        super("Alarm");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent targetIntent = new Intent(this, TaskActivity.class);
        Bundle bundle = intent.getExtras();
        taskID = bundle.getInt("taskID",1);
        code = bundle.getInt("code", 11);
        db = new DbHelper(AlarmService.this, MainActivity.mDatabaseUser);
        currentTask = db.getTask(taskID);
        Bundle myBundle = new Bundle();
        myBundle.putInt("taskID", taskID);
        targetIntent.putExtras(myBundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                code,
                targetIntent,
                FLAG_ONE_SHOT);

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nhắc nhở")
                .setContentIntent(pendingIntent)
                .setContentText(currentTask.getmName())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(code, builder.build());
    }

}
