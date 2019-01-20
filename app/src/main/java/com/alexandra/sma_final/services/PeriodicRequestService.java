package com.alexandra.sma_final.services;

import android.app.IntentService;
import android.content.Intent;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.server.RequestGateway;

import androidx.annotation.Nullable;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.City;
import realm.Topic;

public class PeriodicRequestService extends IntentService {

    private MyApplication mApp;
    private RequestGateway reqGtw;

    public PeriodicRequestService() {
        super("PeriodicRequestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp= ((MyApplication) getApplicationContext());
        reqGtw = mApp.requestGateway;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doGetRequests();
    }

    public void doGetRequests() {
        reqGtw.getNearbyTopics(45.731527D, 21.240686D, null, mApp.refreshTime);
//        reqGtw.getNearbyTopics(((MyApplication)getApplication()).city, mApp.refreshTime);
//        reqGtw.getNearbyTopics("Timisoara", mApp.refreshTime);

        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<City> cities  = realm.where(City.class).findAll();
                if (cities.size() != 0){
                    for(City t : cities){
                        reqGtw.getNearbyTopics(t.getName());
                    }
                }
            });
        }

        reqGtw.getAllUsers();
//        req.getUserByUsername("system");
        reqGtw.getUserConversations(mApp.refreshTime);
    }
}
