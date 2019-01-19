package com.alexandra.sma_final.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alexandra.sma_final.activities.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int count = intent.getIntExtra("count", 0);

        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent operation = PendingIntent.getActivity(context, -1,
                newIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("New requests!")
                .setContentText("You've got " + count + " new requests")
                .setSmallIcon(android.R.drawable.sym_action_email)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .getNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}