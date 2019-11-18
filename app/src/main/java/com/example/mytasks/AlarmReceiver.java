package com.example.mytasks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {

    Task currentTask;
    DbHelper db;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmService.class);
        Bundle bundle = intent.getExtras();
        serviceIntent.putExtras(bundle);
        context.startService(serviceIntent);
    }
}