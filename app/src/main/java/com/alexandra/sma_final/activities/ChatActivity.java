package com.alexandra.sma_final.activities;

import android.os.Bundle;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.adapters.MessageListAdapter;
import com.alexandra.sma_final.rest.UserDTO;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import realm.Conversation;
import realm.Message;
import realm.User;

public class ChatActivity extends BaseActivity {

    protected String activityName = "Messenger";

    @Override
    public String getActivityName() {
        return activityName;
    }

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messages;
    private UserDTO mCurrentUser;
    private User mRespondingUser;
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messages = new ArrayList<Message>();

        //set responding user = author of topic
        String respondingUser = getIntent().getExtras().getString("usernameTopic");
        ((MyApplication)getBaseContext()).requestGateway.getUserByUsername(respondingUser);
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final User user  = realm.where(User.class).findFirst();
                if (user != null){
                    setUser(user);
                }
            });
        }

        //set current user
        Long currentUserId = getIntent().getExtras().getLong("usernameRespond_id");
        if(currentUserId == ((MyApplication)getBaseContext()).getCurrentUser().getId()){
            mCurrentUser = ((MyApplication)getBaseContext()).getCurrentUser();
        }

        //set conversation
        Long topicId = getIntent().getExtras().getLong("idTopic");
        //get conversation from Realm
        try(Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(inRealm -> {
                final Conversation convs  = realm.where(Conversation.class).equalTo("topicId", topicId)
                        .and().equalTo("respondingUserId", mRespondingUser.getId()).findFirst(); //one conv per topic
                if(convs != null){
                    setConversation(convs);
                }
            });
        }

//        mMessageAdapter = new MessageListAdapter(this, conversations);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUser(User u){
        mRespondingUser = u;
    }

    private void setConversation(Conversation c){
        conversation = c;
    }

}
