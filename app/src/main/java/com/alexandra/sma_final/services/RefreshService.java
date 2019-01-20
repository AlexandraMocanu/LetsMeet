package com.alexandra.sma_final.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RefreshService extends IntentService {

    private static final String TAG = RefreshService.class.getSimpleName();

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        sendBroadcast(new Intent(
//                "com.marakana.android.yamba.action.NEW_STATUSES").putExtra(
//                "count", count));


        return;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyed");
    }

}

