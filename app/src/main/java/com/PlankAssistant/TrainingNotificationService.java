package com.PlankAssistant;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rxtimertest.R;

public class TrainingNotificationService extends IntentService {
    private static final int NOTIFY_ID = 101;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TrainingNotificationService(String name) {
        super(name);
    }

    public TrainingNotificationService() {
        super("MyNewIntentService");
    }
    //private Context context;



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        createNotification();
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        String CHANNEL_ID = "1";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_icon_add)
                        .setContentTitle(this.getString(R.string.treningtimenotification))
                        .setContentText(this.getString(R.string.have_training))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentIntent)

                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.ic_icon_add)) // большая картинка

                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }


        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }



}
