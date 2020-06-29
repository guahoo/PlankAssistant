package com.PlankAssistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrainingNotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, TrainingNotificationService.class);
        context.startService(intent1);



    }
}
