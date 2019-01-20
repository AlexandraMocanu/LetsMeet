package com.alexandra.sma_final.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alexandra.sma_final.services.PeriodicRequestService;

//see https://guides.codepath.com/android/Starting-Background-Services#using-with-alarmmanager-for-periodic-tasks
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 9876;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PeriodicRequestService.class);
        context.startService(i);
    }

}
