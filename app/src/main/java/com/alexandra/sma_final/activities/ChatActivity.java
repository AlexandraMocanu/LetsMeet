package com.alexandra.sma_final.activities;

import android.os.Bundle;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Conversation;

public class ChatActivity extends BaseActivity {

    protected String activityName = "Messenger";

    @Override
    public String getActivityName() {
        return activityName;
    }

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        conversations = new ArrayList<Conversation>();

        //get messages from Realm
        ((MyApplication)getApplication()).requestGateway.getUserConversations();
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final RealmResults<Conversation> convs  = realm.where(Conversation.class).findAll();
                if(convs != null){
                    for(Conversation c : convs){
                        addConv(c);
                    }
                }
            });
        }

//        mMessageAdapter = new MessageListAdapter(this, conversations);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addConv(Conversation c){
        conversations.add(c);
    }

}
