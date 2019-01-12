package com.alexandra.sma_final;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import realm.Message;

public class ChatActivity extends BaseActivity {

    protected String activityName = "Messenger";

    @Override
    public String getActivityName() {
        return activityName;
    }

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);

        List<Message> messageList = new ArrayList<Message>();

        //get messages from Realm

        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

}
